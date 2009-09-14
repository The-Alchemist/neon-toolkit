/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/* 
 * Created on 18.02.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public class SwtPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.neontoolkit.swt"; //$NON-NLS-1$

	// The shared instance
	private static SwtPlugin plugin;
	
	public static void logError(Exception e) {
		logError("", e); //$NON-NLS-1$
	}

    /**
     * Default error logging
     * @param message error message
     * @param e exception
     */
    public static void logError(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, e));
    }

	public static void logWarning(Exception e) {
		logWarning("", e); //$NON-NLS-1$
	}

    /**
     * Default warning logging
     * @param message warning message
     * @param e exception
     */
    public static void logWarning(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.WARNING, PLUGIN_ID, 1, message, e));
    }

	public static void logInfo(Exception e) {
		logInfo("", e); //$NON-NLS-1$
	}

    /**
     * Default info logging
     * @param message info message
     * @param e exception
     */
    public static void logInfo(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.INFO, PLUGIN_ID, 1, message, e));
    }

	/**
	 * The constructor
	 */
	public SwtPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SwtPlugin getDefault() {
		return plugin;
	}


}
