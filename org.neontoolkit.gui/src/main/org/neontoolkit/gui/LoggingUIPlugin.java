/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/* 
 * Created on 08.08.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class LoggingUIPlugin extends AbstractUIPlugin {
    public static final String ASK_FOR_PRESPECTIVE_SWITCH = "org.neontoolkit.gui.perspectiveSwitch"; //$NON-NLS-1$

    public void logError(String message, Exception e) {
        getLog().log(new Status(IStatus.ERROR, getBundle().getSymbolicName(), 1, message, e));
    }

    public void logWarning(String message, Exception e) {
    	getLog().log(new Status(IStatus.WARNING, getBundle().getSymbolicName(), 1, message, e));
    }

    public void logInfo(String message, Exception e) {
        getLog().log(new Status(IStatus.INFO, getBundle().getSymbolicName(), 1, message, e));
    }

    public void logInfo(String message) {
        getLog().log(new Status(IStatus.INFO, getBundle().getSymbolicName(), 1, message, null));
    }

}
