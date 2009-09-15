/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/* 
 * Created on 22.01.2007
 * Created by Dirk Wenke
 *
 * Keywords: Plugin, AbstractUIPlugin, Logging
 */

/**
 * Extension of the eclipses Plugin which provides simple logging methods which log
 * to the eclipse log. 
 */
public abstract class LoggingPlugin extends Plugin {
    public void logError(String message, Exception e) {
        getLog().log(new Status(IStatus.ERROR, getBundle().getSymbolicName(), 1, message, e));
    }
    public void logError(Exception e) {
        logError("", e); //$NON-NLS-1$
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
