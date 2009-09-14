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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * @author Dirk Wenke
 */
public class SwtImages {

    private static final String PATH_SUFFIX = "icons/"; //$NON-NLS-1$
    private static URL _iconBaseURL = null;
    private static ImageRegistry _imageRegistry = null;
    private static Hashtable<ImageDescriptor, Image> _images = new Hashtable<ImageDescriptor, Image>();

    static {
        _iconBaseURL = SwtPlugin.getDefault().getBundle().getEntry(PATH_SUFFIX);
    }

    public static final ImageDescriptor CHECKBOX_CHECKED = create("table", "checked.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor CHECKBOX_UNCHECKED = create("table", "unchecked.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor REMOVE_ROW = create("table", "remove.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor EMPTY = create("table", "empty.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_ERROR = create("decorations", "error.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_WARNING = create("decorations", "warning.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_STORE_RAM = create("decorations", "store_ram.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_STORE_H2 = create("decorations", "store_h2.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_STORE_DB = create("decorations", "store_db.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_STORE_SERVER = create("decorations", "store_server.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_LANGUAGE_FLOGIC = create("decorations", "lang_flogic.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_LANGUAGE_RDF = create("decorations", "lang_rdf.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor DECO_LANGUAGE_OWL = create("decorations", "lang_owl.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    

    public static Image get(ImageDescriptor descr) {
        Image im = (Image) _images.get(descr);
        if (im == null) {
            im = descr.createImage();
            _images.put(descr, im);
        }
        return im;
    }

    static ImageRegistry getImageRegistry() {
        if (_imageRegistry == null) {
            _imageRegistry = new ImageRegistry();
        }
        return _imageRegistry;
    }

    private static ImageDescriptor create(String prefix, String name) {
        try {
            return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
        } catch (MalformedURLException e) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

    private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
        if (_iconBaseURL == null) {
            throw new MalformedURLException();
        } else {
            StringBuffer buffer = new StringBuffer(prefix);
            buffer.append('/');
            buffer.append(name);
            return new URL(_iconBaseURL, buffer.toString());
        }
    }
}
