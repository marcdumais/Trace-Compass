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
package org.eclipse.linuxtools.lttng.ui.views.control.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.linuxtools.lttng.ui.LTTngUiPlugin;
import org.eclipse.linuxtools.lttng.ui.views.control.ControlView;
import org.eclipse.linuxtools.lttng.ui.views.control.Messages;
import org.eclipse.linuxtools.lttng.ui.views.control.dialogs.CreateSessionDialog;
import org.eclipse.linuxtools.lttng.ui.views.control.dialogs.ICreateSessionDialog;
import org.eclipse.linuxtools.lttng.ui.views.control.model.impl.TraceSessionGroup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * <b><u>CreateSessionHandler</u></b>
 * <p>
 * Command handler implementation to create a trace session.
 * </p>
 */
public class CreateSessionHandler extends AbstractHandler {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------
    /**
     * The trace session group the command is to be executed on. 
     */
    private TraceSessionGroup fSessionGroup = null;
    
    // ------------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return false;
        }

        // Open dialog box for the node name and address
        ICreateSessionDialog dialog = new CreateSessionDialog(window.getShell(), fSessionGroup);

        if (dialog.open() == Window.OK) {
            final String sessionName = dialog.getSessionName();
            final String sessionPath = dialog.isDefaultSessionPath() ? null : dialog.getSessionPath();
            
            Job job = new Job(Messages.TraceControl_CreateSessionJob) {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        fSessionGroup.createSession(sessionName, sessionPath, monitor);
                    } catch (ExecutionException e) {
                        return new Status(Status.ERROR, LTTngUiPlugin.PLUGIN_ID, e.toString());
                    } 
                    return Status.OK_STATUS;
                }
            };
            job.setUser(true);
            job.schedule();
        }
        
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        fSessionGroup = null;

        // Check if we are closing down
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return false;
        }

        // Check if we are in the Project View
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return false;
        }

        IWorkbenchPart part = page.getActivePart();
        if (!(part instanceof ControlView)) {
            return false;
        }

        // Check if the session group project is selected
        ISelection selection = page.getSelection(ControlView.ID);
        if (selection instanceof StructuredSelection) {
            Object element = ((StructuredSelection) selection).getFirstElement();
            fSessionGroup = (element instanceof TraceSessionGroup) ? (TraceSessionGroup) element : null;
        }
        return fSessionGroup != null;
    }
}