/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
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

    public void worked(int work) {
    }

    public void subTask(String name) {
        // TODO Auto-generated method stub

    }

    public void setTaskName(String name) {
        System.out.println("setTaskName: " + name); //$NON-NLS-1$

    }

    public void setCanceled(boolean value) {
        // TODO Auto-generated method stub

    }

    public boolean isCanceled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void internalWorked(double work) {
        System.out.println("internalWorked: " + work); //$NON-NLS-1$

    }

    public void done() {
        // TODO Auto-generated method stub

    }

    public void beginTask(String name, int totalWork) {
        System.out.println("beginTask: " + name); //$NON-NLS-1$
    }

}
