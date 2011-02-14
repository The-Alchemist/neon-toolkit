/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/*
 * Created by Werner Hihn
 */

public class JpowergraphPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static JpowergraphPlugin _plugin;
	
	/**
	 * The constructor.
	 */
	public JpowergraphPlugin() {
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
		_plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static JpowergraphPlugin getDefault() {
		return _plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.neontoolkit.jpowergraph", path); //$NON-NLS-1$
	}
}
