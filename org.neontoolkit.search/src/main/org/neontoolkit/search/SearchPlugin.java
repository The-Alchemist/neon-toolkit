/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.search;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class SearchPlugin extends AbstractUIPlugin {

    public static final String ID = "org.neontoolkit.search"; //$NON-NLS-1$
    
    //The shared instance.
	private static SearchPlugin _plugin;
	
	/**
	 * The constructor.
	 */
	public SearchPlugin() {
		_plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static SearchPlugin getDefault() {
		return _plugin;
	}

    public static void logError(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.ERROR, SearchPlugin.ID, 1, message, e));
    }

    public static void logWarning(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.WARNING, SearchPlugin.ID, 1, message, e));
    }

    public static void logInfo(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.INFO, SearchPlugin.ID, 1, message, e));
    }

}
