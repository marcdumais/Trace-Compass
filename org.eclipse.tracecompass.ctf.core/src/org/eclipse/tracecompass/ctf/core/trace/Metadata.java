/*******************************************************************************
 * Copyright (c) 2011, 2014 Ericsson, Ecole Polytechnique de Montreal and others
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Khouzam - Initial API and implementation
 *     Simon Marchi - Initial API and implementation
 *     Matthew Khouzam - Update for live trace reading support
 *******************************************************************************/

package org.eclipse.tracecompass.ctf.core.trace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.UUID;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.RewriteCardinalityException;
import org.eclipse.tracecompass.ctf.parser.CTFLexer;
import org.eclipse.tracecompass.ctf.parser.CTFParser;
import org.eclipse.tracecompass.ctf.parser.CTFParser.parse_return;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.IOStructGen;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.exceptions.CtfAntlrException;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.exceptions.ParseException;

/**
 * The CTF trace metadata TSDL file
 *
 * @version 1.0
 * @author Matthew Khouzam
 * @author Simon Marchi
 */
public class Metadata {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------

    /**
     * Name of the metadata file in the trace directory
     */
    private static final String METADATA_FILENAME = "metadata"; //$NON-NLS-1$

    /**
     * Size of the metadata packet header, in bytes, computed by hand.
     */
    private static final int METADATA_PACKET_HEADER_SIZE = 37;

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    /**
     * Byte order as detected when reading the TSDL magic number.
     */
    private ByteOrder detectedByteOrder = null;

    /**
     * The trace file to which belongs this metadata file.
     */
    private final CTFTrace trace;

    private IOStructGen fTreeParser;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a Metadata object.
     *
     * @param trace
     *            The trace to which belongs this metadata file.
     */
    public Metadata(CTFTrace trace) {
        this.trace = trace;
    }

    /**
     * For network streaming
     *
     * @since 3.0
     */
    public Metadata() {
        trace = new CTFTrace();
    }

    // ------------------------------------------------------------------------
    // Getters/Setters/Predicates
    // ------------------------------------------------------------------------

    /**
     * Returns the ByteOrder that was detected while parsing the metadata.
     *
     * @return The byte order.
     */
    public ByteOrder getDetectedByteOrder() {
        return detectedByteOrder;
    }

    /**
     * Gets the parent trace
     *
     * @return the parent trace
     * @since 3.0
     */
    public CTFTrace getTrace() {
        return trace;
    }

    // ------------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------------

    /**
     * Parse the metadata file.
     *
     * @throws CTFReaderException
     *             If there was a problem parsing the metadata
     * @since 3.0
     */
    public void parseFile() throws CTFReaderException {

        /*
         * Reader. It will contain a StringReader if we are using packet-based
         * metadata and it will contain a FileReader if we have text-based
         * metadata.
         */

        try (FileInputStream fis = new FileInputStream(getMetadataPath());
                FileChannel metadataFileChannel = fis.getChannel();
                /* Check if metadata is packet-based, if not it is text based */
                Reader metadataTextInput =
                        (isPacketBased(metadataFileChannel) ?
                                readBinaryMetaData(metadataFileChannel) :
                                new FileReader(getMetadataPath()));) {

            readMetaDataText(metadataTextInput);

        } catch (FileNotFoundException e) {
            throw new CTFReaderException("Cannot find metadata file!"); //$NON-NLS-1$
        } catch (IOException | ParseException e) {
            throw new CTFReaderException(e);
        } catch (RecognitionException | RewriteCardinalityException e) {
            throw new CtfAntlrException(e);
        }
    }

    private Reader readBinaryMetaData(FileChannel metadataFileChannel) throws CTFReaderException {
        /* Create StringBuffer to receive metadata text */
        StringBuffer metadataText = new StringBuffer();

        /*
         * Read metadata packet one by one, appending the text to the
         * StringBuffer
         */
        MetadataPacketHeader packetHeader = readMetadataPacket(
                metadataFileChannel, metadataText);
        while (packetHeader != null) {
            packetHeader = readMetadataPacket(metadataFileChannel,
                    metadataText);
        }

        /* Wrap the metadata string with a StringReader */
        return new StringReader(metadataText.toString());
    }

    /**
     * Read the metadata from a formatted TSDL string
     *
     * @param data
     *            the data to read
     * @throws CTFReaderException
     *             this exception wraps a ParseException, IOException or
     *             CtfAntlrException, three exceptions that can be obtained from
     *             parsing a TSDL file
     * @since 3.0
     */
    public void parseText(String data) throws CTFReaderException {
        Reader metadataTextInput = new StringReader(data);
        try {
            readMetaDataText(metadataTextInput);
        } catch (IOException | ParseException e) {
            throw new CTFReaderException(e);
        } catch (RecognitionException | RewriteCardinalityException e) {
            throw new CtfAntlrException(e);
        }

    }

    private void readMetaDataText(Reader metadataTextInput) throws IOException, RecognitionException, ParseException {
        CommonTree tree = createAST(metadataTextInput);

        /* Generate IO structures (declarations) */
        fTreeParser = new IOStructGen(tree, trace);
        fTreeParser.generate();
    }

    /**
     * Read a metadata fragment from a formatted TSDL string
     *
     * @param dataFragment
     *            the data to read
     * @throws CTFReaderException
     *             this exception wraps a ParseException, IOException or
     *             CtfAntlrException, three exceptions that can be obtained from
     *             parsing a TSDL file
     * @since 3.0
     */
    public void parseTextFragment(String dataFragment) throws CTFReaderException {
        Reader metadataTextInput = new StringReader(dataFragment);
        try {
            readMetaDataTextFragment(metadataTextInput);
        } catch (IOException | ParseException e) {
            throw new CTFReaderException(e);
        } catch (RecognitionException | RewriteCardinalityException e) {
            throw new CtfAntlrException(e);
        }
    }

    private void readMetaDataTextFragment(Reader metadataTextInput) throws IOException, RecognitionException, ParseException {
        CommonTree tree = createAST(metadataTextInput);
        fTreeParser.setTree(tree);
        fTreeParser.generateFragment();
    }

    private static CommonTree createAST(Reader metadataTextInput) throws IOException,
            RecognitionException {
        /* Create an ANTLR reader */
        ANTLRReaderStream antlrStream;
        antlrStream = new ANTLRReaderStream(metadataTextInput);

        /* Parse the metadata text and get the AST */
        CTFLexer ctfLexer = new CTFLexer(antlrStream);
        CommonTokenStream tokens = new CommonTokenStream(ctfLexer);
        CTFParser ctfParser = new CTFParser(tokens, false);

        parse_return pr = ctfParser.parse();
        return pr.getTree();
    }

    /**
     * Determines whether the metadata file is packet-based by looking at the
     * TSDL magic number. If it is packet-based, it also gives information about
     * the endianness of the trace using the detectedByteOrder attribute.
     *
     * @param metadataFileChannel
     *            FileChannel of the metadata file.
     * @return True if the metadata is packet-based.
     * @throws CTFReaderException
     */
    private boolean isPacketBased(FileChannel metadataFileChannel)
            throws CTFReaderException {
        /*
         * Create a ByteBuffer to read the TSDL magic number (default is
         * big-endian)
         */
        ByteBuffer magicByteBuffer = ByteBuffer.allocate(Utils.TSDL_MAGIC_LEN);

        /* Read without changing file position */
        try {
            metadataFileChannel.read(magicByteBuffer, 0);
        } catch (IOException e) {
            throw new CTFReaderException("Unable to read metadata file channel.", e); //$NON-NLS-1$
        }

        /* Get the first int from the file */
        int magic = magicByteBuffer.getInt(0);

        /* Check if it matches */
        if (Utils.TSDL_MAGIC == magic) {
            detectedByteOrder = ByteOrder.BIG_ENDIAN;
            return true;
        }

        /* Try the same thing, but with little-endian */
        magicByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        magic = magicByteBuffer.getInt(0);

        if (Utils.TSDL_MAGIC == magic) {
            detectedByteOrder = ByteOrder.LITTLE_ENDIAN;
            return true;
        }

        return false;
    }

    private String getMetadataPath() {
        /* Path of metadata file = trace directory path + metadata filename */
        if (trace.getTraceDirectory() == null) {
            return new String();
        }
        return trace.getTraceDirectory().getPath()
                + Utils.SEPARATOR + METADATA_FILENAME;
    }

    /**
     * Reads a metadata packet from the given metadata FileChannel, do some
     * basic validation and append the text to the StringBuffer.
     *
     * @param metadataFileChannel
     *            Metadata FileChannel
     * @param metadataText
     *            StringBuffer to which the metadata text will be appended.
     * @return A structure describing the header of the metadata packet, or null
     *         if the end of the file is reached.
     * @throws CTFReaderException
     */
    private MetadataPacketHeader readMetadataPacket(
            FileChannel metadataFileChannel, StringBuffer metadataText)
            throws CTFReaderException {
        /* Allocate a ByteBuffer for the header */
        ByteBuffer headerByteBuffer = ByteBuffer.allocate(METADATA_PACKET_HEADER_SIZE);

        /* Read the header */
        try {
            int nbBytesRead = metadataFileChannel.read(headerByteBuffer);

            /* Return null if EOF */
            if (nbBytesRead < 0) {
                return null;
            }

            if (nbBytesRead != METADATA_PACKET_HEADER_SIZE) {
                throw new CTFReaderException("Error reading the metadata header."); //$NON-NLS-1$
            }

        } catch (IOException e) {
            throw new CTFReaderException("Error reading the metadata header.", e); //$NON-NLS-1$
        }

        /* Set ByteBuffer's position to 0 */
        headerByteBuffer.position(0);

        /* Use byte order that was detected with the magic number */
        headerByteBuffer.order(detectedByteOrder);

        MetadataPacketHeader header = new MetadataPacketHeader(headerByteBuffer);

        /* Check TSDL magic number */
        if (!header.isMagicValid()) {
            throw new CTFReaderException("TSDL magic number does not match"); //$NON-NLS-1$
        }

        /* Check UUID */
        if (!trace.uuidIsSet()) {
            trace.setUUID(header.getUuid());
        } else if (!trace.getUUID().equals(header.getUuid())) {
            throw new CTFReaderException("UUID mismatch"); //$NON-NLS-1$
        }

        /* Extract the text from the packet */
        int payloadSize = ((header.getContentSize() / 8) - METADATA_PACKET_HEADER_SIZE);
        if (payloadSize < 0) {
            throw new CTFReaderException("Invalid metadata packet payload size."); //$NON-NLS-1$
        }
        int skipSize = (header.getPacketSize() - header.getContentSize()) / 8;

        /* Read the payload + the padding in a ByteBuffer */
        ByteBuffer payloadByteBuffer = ByteBuffer.allocateDirect(payloadSize
                + skipSize);
        try {
            metadataFileChannel.read(payloadByteBuffer);
        } catch (IOException e) {
            throw new CTFReaderException("Error reading metadata packet payload.", e); //$NON-NLS-1$
        }
        payloadByteBuffer.rewind();

        /* Read only the payload from the ByteBuffer into a byte array */
        byte payloadByteArray[] = new byte[payloadByteBuffer.remaining()];
        payloadByteBuffer.get(payloadByteArray, 0, payloadSize);

        /* Convert the byte array to a String */
        String str = new String(payloadByteArray, 0, payloadSize);

        /* Append it to the existing metadata */
        metadataText.append(str);

        return header;
    }

    private static class MetadataPacketHeader {

        private final int fMagic;
        private final UUID fUuid;
        private final int fChecksum;
        private final int fContentSize;
        private final int fPacketSize;
        private final byte fCompressionScheme;
        private final byte fEncryptionScheme;
        private final byte fChecksumScheme;
        private final byte fCtfMajorVersion;
        private final byte fCtfMinorVersion;

        public MetadataPacketHeader(ByteBuffer headerByteBuffer) {
            /* Read from the ByteBuffer */
            fMagic = headerByteBuffer.getInt();
            byte[] uuidBytes = new byte[16];
            headerByteBuffer.get(uuidBytes);
            fUuid = Utils.makeUUID(uuidBytes);
            fChecksum = headerByteBuffer.getInt();
            fContentSize = headerByteBuffer.getInt();
            fPacketSize = headerByteBuffer.getInt();
            fCompressionScheme = headerByteBuffer.get();
            fEncryptionScheme = headerByteBuffer.get();
            fChecksumScheme = headerByteBuffer.get();
            fCtfMajorVersion = headerByteBuffer.get();
            fCtfMinorVersion = headerByteBuffer.get();
        }

        public boolean isMagicValid() {
            return fMagic == Utils.TSDL_MAGIC;
        }

        public UUID getUuid() {
            return fUuid;
        }

        public int getContentSize() {
            return fContentSize;
        }

        public int getPacketSize() {
            return fPacketSize;
        }

        @Override
        public String toString() {
            /* Only for debugging, shouldn't be externalized */
            /* Therefore it cannot be covered by test cases */
            return "MetadataPacketHeader [magic=0x" //$NON-NLS-1$
                    + Integer.toHexString(fMagic) + ", uuid=" //$NON-NLS-1$
                    + fUuid.toString() + ", checksum=" + fChecksum //$NON-NLS-1$
                    + ", contentSize=" + fContentSize + ", packetSize=" //$NON-NLS-1$ //$NON-NLS-2$
                    + fPacketSize + ", compressionScheme=" + fCompressionScheme //$NON-NLS-1$
                    + ", encryptionScheme=" + fEncryptionScheme //$NON-NLS-1$
                    + ", checksumScheme=" + fChecksumScheme //$NON-NLS-1$
                    + ", ctfMajorVersion=" + fCtfMajorVersion //$NON-NLS-1$
                    + ", ctfMinorVersion=" + fCtfMinorVersion + ']'; //$NON-NLS-1$
        }

    }
}
