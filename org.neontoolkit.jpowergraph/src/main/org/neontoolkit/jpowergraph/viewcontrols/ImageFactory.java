/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.viewcontrols;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.jpowergraph.JpowergraphPlugin;


/*
 * Created by Werner Hihn
 */

public class ImageFactory {

    private static final String PATH_SUFFIX = "icons/"; //$NON-NLS-1$
    private static Hashtable<ImageDescriptor, Image> _imageRegistry = new Hashtable<ImageDescriptor, Image>();
    private static URL _fgIconBaseURL = null;

    static {
        _fgIconBaseURL = JpowergraphPlugin.getDefault().getBundle().getEntry(PATH_SUFFIX);
    }
    public static final ImageDescriptor BACK_NAV = create("eclipse", "backward_nav.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor FORWARD_NAV = create("eclipse", "forward_nav.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor BACK_NAV_DISABLED = create("eclipse", "backward_disabled.gif"); //$NON-NLS-1$ //$NON-NLS-2$
    public static final ImageDescriptor FORWARD_NAV_DISABLED = create("eclipse", "forward_disabled.gif"); //$NON-NLS-1$ //$NON-NLS-2$

    public static Image get(ImageDescriptor descr) {
        Image im = (Image) _imageRegistry.get(descr);
        if (im == null) {
            im = descr.createImage();
            _imageRegistry.put(descr, im);
        }
        return im;
    }

    private static ImageDescriptor create(String prefix, String name) {
        try {
            return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
        } catch (MalformedURLException e) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

    private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
        if (_fgIconBaseURL == null) {
            throw new MalformedURLException();
        } else {
            StringBuffer buffer = new StringBuffer(prefix);
            buffer.append('/');
            buffer.append(name);
            return new URL(_fgIconBaseURL, buffer.toString());
        }
    }
}