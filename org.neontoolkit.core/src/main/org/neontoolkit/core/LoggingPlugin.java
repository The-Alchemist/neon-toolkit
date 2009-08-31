/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

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
