/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io;

import org.neontoolkit.gui.LoggingUIPlugin;
import org.osgi.framework.BundleContext;

/* 
 * Created on 7.04.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Ontology, Import, Export, Open, Save
 */

/**
 * Plugin to import and export ontologies
 */
public class IOPlugin extends LoggingUIPlugin {
	public static final String ID = "com.ontoprise.ontostudio.io"; //$NON-NLS-1$
    public static final String ACTIONS = "com.ontoprise.ontostudio.io.actions"; //$NON-NLS-1$
 
    //The shared instance.
    private static IOPlugin _plugin;
    public static final String KEY_WEBDAV_DEFAULT_PATH = "webdav.default_path"; //$NON-NLS-1$
    public static final String KEY_WEBDAV_ACTUAL_PATH = "webdav.actual_path"; //$NON-NLS-1$
    
    public IOPlugin() {
        _plugin = this;
    }

    /**
     * Returns the shared instance.
     */
    public static IOPlugin getDefault() {
    	if (_plugin == null) {
    		_plugin = new IOPlugin();
    	}
        return _plugin;
    }

	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	@Override
    public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
}
