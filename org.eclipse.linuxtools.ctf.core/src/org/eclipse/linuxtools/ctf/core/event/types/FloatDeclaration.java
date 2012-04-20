/*******************************************************************************
 * Copyright (c) 2011-2012 Ericsson, Ecole Polytechnique de Montreal and others
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Matthew Khouzam - Initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.ctf.core.event.types;

import java.nio.ByteOrder;


public class FloatDeclaration implements IDeclaration {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    private final int mant;
    private final int exp;
    private final ByteOrder byteOrder;
    private final Encoding encoding;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public FloatDeclaration(int exponent, int mantissa, ByteOrder byteOrder,
            Encoding encoding) {
        mant = mantissa;
        exp = exponent;
        this.byteOrder = byteOrder;
        this.encoding = encoding;

    }

    // ------------------------------------------------------------------------
    // Gettters/Setters/Predicates
    // ------------------------------------------------------------------------



    /**
     * @return the mant
     */
    public int getMantissa() {
        return mant;
    }

    /**
     * @return the exp
     */
    public int getExponent() {
        return exp;
    }

    /**
     * @return the byteOrder
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    /**
     * @return the encoding
     */
    public Encoding getEncoding() {
        return encoding;
    }

    // ------------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------------

    @Override
    public Definition createDefinition(IDefinitionScope definitionScope,
            String fieldName) {
        return new FloatDefinition(this, definitionScope, fieldName);
    }

    @Override
    public String toString() {
        /* Only used for debugging */
        return "[declaration] float[" + Integer.toHexString(hashCode()) + ']'; //$NON-NLS-1$
    }
}