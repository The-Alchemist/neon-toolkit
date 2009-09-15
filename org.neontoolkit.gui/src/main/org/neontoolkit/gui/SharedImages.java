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
public final class SharedImages {

    public static final String ONTOLOGY = "ontology"; //$NON-NLS-1$
    public static final String PIN_ACTION = "pin"; //$NON-NLS-1$
    public static final String NEW_ONTOLOGY_PROJECT_WIZ = "newonto_wiz"; //$NON-NLS-1$
    public static final String REMOVE = "remove"; //$NON-NLS-1$
    public static final String REMOVE_ALL = "remove_all"; //$NON-NLS-1$
    public static final String REMOVE_DISABLED = "remove_disabled"; //$NON-NLS-1$
    public static final String REMOVE_ALL_DISABLED = "remove_all_disabled"; //$NON-NLS-1$
    public static final String RESULTS = "results"; //$NON-NLS-1$
    public static final String RESULTS_DISABLED = "results_disabled"; //$NON-NLS-1$
    public static final String PROJECT = "ontoprj_obj"; //$NON-NLS-1$


    static void register(ImageRegistry registry) {
        registry.put(NEW_ONTOLOGY_PROJECT_WIZ, create("wizard", "newonto_wiz.gif")); //$NON-NLS-1$ //$NON-NLS-2$    	
        registry.put(ONTOLOGY, create("onto", "ontology.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(PIN_ACTION, create("actions", "pin.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(REMOVE, create("common", "remove.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(REMOVE_ALL, create("common", "remove_all.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(REMOVE_DISABLED, create("common", "remove_disabled.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(REMOVE_ALL_DISABLED, create("common", "remove_all_disabled.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(RESULTS, create("common", "results.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(RESULTS_DISABLED, create("common", "results_disabled.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(PROJECT, create("onto", "ontoprj_obj.gif")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private static ImageDescriptor create(String path, String name) {
    	Bundle bundle = NeOnUIPlugin.getDefault().getBundle();
    	return ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/"+path+"/"+name), null)); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
