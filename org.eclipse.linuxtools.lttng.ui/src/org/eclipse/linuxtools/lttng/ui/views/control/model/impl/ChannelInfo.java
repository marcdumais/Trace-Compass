/**********************************************************************
 * Copyright (c) 2012 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Bernd Hufmann - Initial API and implementation
 **********************************************************************/
package org.eclipse.linuxtools.lttng.ui.views.control.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo;
import org.eclipse.linuxtools.lttng.ui.views.control.model.IEventInfo;
import org.eclipse.linuxtools.lttng.ui.views.control.model.TraceEnablement;

/**
 * <b><u>ChannelInfo</u></b>
 * <p>
 * Implementation of the trace channel interface (IChannelInfo) to store channel
 * related data. 
 * </p>
 */
public class ChannelInfo extends TraceInfo implements IChannelInfo {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------
    /**
     * The overwrite mode of the channel.
     */
    private boolean fOverwriteMode;
    /**
     * The sub-buffer size of the channel.
     */
    private long fSubBufferSize;
    /**
     * The number of sub-buffers of the channel.
     */
    private int fNumberOfSubBuffers;
    /**
     * The switch timer interval of the channel.
     */
    private long fSwitchTimer;
    /**
     * The read timer interval of the channel.
     */
    private long fReadTimer;
    /**
     * The Output type of the channel.
     */
    private String fOutputType = ""; //$NON-NLS-1$
    /**
     * The channel enable state.
     */
    private TraceEnablement fState = TraceEnablement.DISABLED;
    /**
     * The events information of the channel.
     */
    private List<IEventInfo> fEvents = new ArrayList<IEventInfo>();

    
    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    /**
     * Constructor
     * @param name - name channel
     */
    public ChannelInfo(String name) {
        super(name);
    }

    /**
     * Copy constructor
     * @param other - the instance to copy
     */
    public ChannelInfo(ChannelInfo other) {
        super(other);
        fOverwriteMode = other.fOverwriteMode;
        fSubBufferSize = other.fSubBufferSize;
        fNumberOfSubBuffers = other.fNumberOfSubBuffers;
        fSwitchTimer = other.fSwitchTimer;
        fReadTimer = other.fReadTimer;
        fOutputType = (other.fOutputType == null ? null : String.valueOf(other.fOutputType));
        fState = other.fState;
        for (Iterator<IEventInfo> iterator = other.fEvents.iterator(); iterator.hasNext();) {
            IEventInfo event = iterator.next();
            if (event instanceof EventInfo) {
                fEvents.add(new EventInfo((EventInfo)event));
            } else {
                fEvents.add(event);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getOverwriteMode()
     */
    @Override
    public boolean isOverwriteMode() {
        return fOverwriteMode;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setOverwriteMode(boolean)
     */
    @Override
    public void setOverwriteMode(boolean mode) {
        fOverwriteMode = mode;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getSubBufferSize()
     */
    @Override
    public long getSubBufferSize() {
        return fSubBufferSize;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setSubBufferSize(long)
     */
    @Override
    public void setSubBufferSize(long bufferSize) {
        fSubBufferSize = bufferSize;

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getNumberOfSubBuffers()
     */
    @Override
    public int getNumberOfSubBuffers() {
        return fNumberOfSubBuffers;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setNumberOfSubBuffers(int)
     */
    @Override
    public void setNumberOfSubBuffers(int numberOfSubBuffers) {
        fNumberOfSubBuffers = numberOfSubBuffers;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getSwitchTimer()
     */
    @Override
    public long getSwitchTimer() {
        return fSwitchTimer;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setSwitchTimer(long)
     */
    @Override
    public void setSwitchTimer(long timer) {
        fSwitchTimer = timer;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getReadTimer()
     */
    @Override
    public long getReadTimer() {
        return fReadTimer;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setReadTimer(long)
     */
    @Override
    public void setReadTimer(long timer) {
        fReadTimer = timer;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getOutputType()
     */
    @Override
    public String getOutputType() {
        return fOutputType;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setOutputType(java.lang.String)
     */
    @Override
    public void setOutputType(String type) {
        fOutputType = type;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getState()
     */
    @Override
    public TraceEnablement getState() {
        return fState;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setState(org.eclipse.linuxtools.lttng.ui.views.control.model.TraceEnablement)
     */
    @Override
    public void setState(TraceEnablement state) {
        fState = state;
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setState(java.lang.String)
     */
    @Override
    public void setState(String stateName) {
        fState = TraceEnablement.ENABLED;
        if (TraceEnablement.DISABLED.getInName().equals(stateName)) {
            fState = TraceEnablement.DISABLED;
        } else if (TraceEnablement.ENABLED.getInName().equals(stateName)) {
            fState = TraceEnablement.ENABLED;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#getEvents()
     */
    @Override
    public IEventInfo[] getEvents() {
        return fEvents.toArray(new IEventInfo[fEvents.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#setEvents(java.util.List)
     */
    @Override
    public void setEvents(List<IEventInfo> events) {
        for (Iterator<IEventInfo> iterator = events.iterator(); iterator.hasNext();) {
            IEventInfo eventInfo = (IEventInfo) iterator.next();
            fEvents.add(eventInfo);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.IChannelInfo#addEvent(org.eclipse.linuxtools.lttng.ui.views.control.model.IEventInfo)
     */
    @Override
    public void addEvent(IEventInfo channel) {
        fEvents.add(channel);
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.impl.TraceInfo#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + super.hashCode();
        result = 37 * result + Boolean.valueOf(fOverwriteMode).hashCode();
        result = 37 * result + Long.valueOf(fSubBufferSize).hashCode();
        result = 37 * result + fNumberOfSubBuffers;
        result = 37 * result + Long.valueOf(fSwitchTimer).hashCode();
        result = 37 * result + Long.valueOf(fReadTimer).hashCode();
        result = 37 * result + (fOutputType != null ? fOutputType.hashCode() : 0);
        result = 37 * result + fState.ordinal();
        for (Iterator<IEventInfo> iterator = fEvents.iterator(); iterator.hasNext();) {
            IEventInfo event = (IEventInfo) iterator.next();
            result = 37 * result + event.hashCode();
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.impl.TraceInfo#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ChannelInfo)) {
            return false;
        }

        ChannelInfo otherInfo = (ChannelInfo) other;
        if (!super.equals(otherInfo)) {
            return false;
        }

        if (fOverwriteMode != otherInfo.fOverwriteMode) {
            return false;
        }
        if (fSubBufferSize != otherInfo.fSubBufferSize) {
            return false;
        }
        if (fNumberOfSubBuffers != otherInfo.fNumberOfSubBuffers) {
            return false;
        }
        if (fSwitchTimer != otherInfo.fSwitchTimer) {
            return false;
        }
        if (fReadTimer != otherInfo.fReadTimer) {
            return false;
        }
        if (fState.ordinal() != otherInfo.fState.ordinal()) {
            return false;
        }
        if (!fOutputType.equals(otherInfo.fOutputType)) {
            return false;
        }
        if (fEvents.size() != otherInfo.fEvents.size()) {
            return false;
        }
        for (int i = 0; i < fEvents.size(); i++) {
            if (!fEvents.get(i).equals(otherInfo.fEvents.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.lttng.ui.views.control.model.impl.TraceInfo#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        StringBuffer output = new StringBuffer();
            output.append("[ChannelInfo(");
            output.append(super.toString());
            output.append(",State=");
            output.append(fState);
            output.append(",OverwriteMode=");
            output.append(fOverwriteMode);
            output.append(",SubBuffersSize=");
            output.append(fSubBufferSize);
            output.append(",NumberOfSubBuffers=");
            output.append(fNumberOfSubBuffers);
            output.append(",SwitchTimer=");
            output.append(fSwitchTimer);
            output.append(",ReadTimer=");
            output.append(fReadTimer);
            output.append(",output=");
            output.append(fOutputType);
            output.append(",Events=");
            if (fEvents.isEmpty()) {
                output.append("None");
            } else {
                for (Iterator<IEventInfo> iterator = fEvents.iterator(); iterator.hasNext();) {
                    IEventInfo event = (IEventInfo) iterator.next();
                    output.append(event.toString());
                }
            }
            output.append(")]");
            return output.toString();
    }
}