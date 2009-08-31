/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.io;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;

/* 
 * Created on: 23.07.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Images
 */
/**
 * The keys for the shared images of this plugin
 */
public final class IOSharedImages {

    public static final String WEBDAV_SITE = "site_view"; //$NON-NLS-1$
    public static final String WEBDAV_SITE_ERROR = "site_view_error"; //$NON-NLS-1$

    static void register(ImageRegistry registry) {
        registry.put(WEBDAV_SITE, create("wizard", "site_view.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(WEBDAV_SITE_ERROR, create("wizard", "site_view_error.gif")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private static ImageDescriptor create(String path, String name) {
    	Bundle bundle = IOPlugin.getDefault().getBundle();
    	return ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/"+path+"/"+name), null)); //$NON-NLS-1$ //$NON-NLS-2$
    }
}