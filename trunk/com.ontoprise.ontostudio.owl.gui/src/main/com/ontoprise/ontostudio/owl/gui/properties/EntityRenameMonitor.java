/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties;

import org.eclipse.core.runtime.IProgressMonitor;

public class EntityRenameMonitor implements IProgressMonitor {
    @Override
    public void worked(int work) {
    }
    @Override
    public void subTask(String name) {
        // TODO Auto-generated method stub
    }
    @Override
    public void setTaskName(String name) {
        System.out.println("taskName: " + name); //$NON-NLS-1$
    }
    @Override
    public void setCanceled(boolean value) {
        // TODO Auto-generated method stub
    }
    @Override
    public boolean isCanceled() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void internalWorked(double work) {
        System.out.println("internalWorked: " + work); //$NON-NLS-1$
    }
    @Override
    public void done() {
        // TODO Auto-generated method stub
    }
    @Override
    public void beginTask(String name, int totalWork) {
        System.out.println("beginTask: " + name); //$NON-NLS-1$
    }

}
