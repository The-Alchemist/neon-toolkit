/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.ontovisualize;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;

/*
 * Created by Werner Hihn
 */

public class VisualizerSharedImages {

    public static final String ONTOLOGY = "ontology"; //$NON-NLS-1$

    static void register(ImageRegistry registry) {
        registry.put(ONTOLOGY, create("onto", "ontology.gif")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static ImageDescriptor create(String path, String name) {
        Bundle bundle = OntovisualizePlugin.getDefault().getBundle();
        return ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/" + path + "/" + name), null)); //$NON-NLS-1$ //$NON-NLS-2$
    }
}