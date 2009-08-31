/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 25.03.2009
 * Created by: Dirk Wenke
 ******************************************************************************/
package org.neontoolkit.gui.progress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

/**
 * Progress monitor wrapper that dispatches events to the ui thread, everytime the
 * monitor is called.
 * @author Dirk Wenke
 */
public class EventDispatchProgressMonitor extends ProgressMonitorWrapper {
    private Display _display;
    
    protected EventDispatchProgressMonitor(IProgressMonitor monitor, Display display) {
        super(monitor);
        _display = display;
    }
    
    @Override
    public void beginTask(String name, int totalWork) {
        try {
            if (isAccessible()) {
                dispatchEvents();
                super.beginTask(name, totalWork);
            }
        } catch (SWTException e) {
        	// happens in collabserver cases, where the dialog might be disposed before 
        	// the label is set
        }
    }
    
    @Override
    public void done() {
        try {
            if (isAccessible()) {
                dispatchEvents();
                super.done();
            }
        } catch (SWTException e) {
            // happens in collabserver cases, where the dialog might be disposed before 
            // the label is set
        }
    }
    
    @Override
    public void internalWorked(double work) {
        try {
            if (isAccessible()) {
                dispatchEvents();
                super.internalWorked(work);
            }
        } catch (SWTException e) {
            // happens in collabserver cases, where the dialog might be disposed before 
            // the label is set
        }
    }
    
    @Override
    public void setCanceled(boolean b) {
        try {
            if (isAccessible()) {
                dispatchEvents();
                super.setCanceled(b);
            }
        } catch (SWTException e) {
            // happens in collabserver cases, where the dialog might be disposed before 
            // the label is set
        }
    }

    @Override
    public void setTaskName(String name) {
    	try {
    	    if (isAccessible()) {
    	        dispatchEvents();
    	        super.setTaskName(name);
    	    }
        } catch (SWTException e) {
            // happens in collabserver cases, where the dialog might be disposed before 
            // the label is set
        }
    }
    
    @Override
    public void subTask(String name) {
        try {
            if (isAccessible()) {
                dispatchEvents();
                super.subTask(name);
            }
        } catch (SWTException e) {
            // happens in collabserver cases, where the dialog might be disposed before 
            // the label is set
        }
    }
    
    @Override
    public void worked(int work) {
        try {
            if (isAccessible()) {
                dispatchEvents();
                super.worked(work);
            }
        } catch (SWTException e) {
            // happens in collabserver cases, where the dialog might be disposed before 
            // the label is set
        }
    }
    
    private void dispatchEvents() {
        if (_display != null && _display == Display.getCurrent()) {
            while (_display.readAndDispatch()){}
        }
    }
    
    private boolean isAccessible() {
    	if (getWrappedProgressMonitor() instanceof ProgressMonitorPart) {
    	    return  !((ProgressMonitorPart)getWrappedProgressMonitor()).isDisposed();
    	}
    	else {
    		return true;
    	}
    }
}
