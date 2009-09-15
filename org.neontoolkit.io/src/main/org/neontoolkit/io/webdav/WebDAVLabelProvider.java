/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.webdav;

import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Map;

import javax.wvcm.Folder;
import javax.wvcm.Location;
import javax.wvcm.Resource;
import javax.wvcm.WvcmException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.neontoolkit.io.IOPlugin;
import org.neontoolkit.io.IOSharedImages;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: WebDAV, Labelprovider
 */

/**
 * LabelProvider for webdav-resources; files are shown with last segment and
 * display name in brackets
 */
public class WebDAVLabelProvider implements ILabelProvider {

    /**
     * The cache of images that have been dispensed by this provider. Maps
     * ImageDescriptor->Image.
     */
    private Map<ImageDescriptor, Image> _imageTable;

    /**
     * Returns the implementation of IWorkbenchAdapter for the given object.
     * Returns <code>null</code> if the adapter is not defined or the object
     * is not adaptable.
     */
    protected final IWorkbenchAdapter getAdapter(Object o) {
        if (!(o instanceof IAdaptable)) {
            return null;
        }
        return (IWorkbenchAdapter) ((IAdaptable) o).getAdapter(IWorkbenchAdapter.class);
    }

    /**
     * Returns an image descriptor that is based on the given descriptor, but
     * decorated with additional information relating to the state of the
     * provided object.
     * 
     * Subclasses may reimplement this method to decorate an object's image.
     * 
     * @see org.eclipse.jface.resource.CompositeImageDescriptor
     */
    protected ImageDescriptor decorateImage(ImageDescriptor input, Object element) {
        return input;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element) {
        //obtain the base image by querying the element
        ImageDescriptor descriptor = null;
        if (element instanceof Resource) {
            Resource resource = (Resource) element;
            Location location = resource.location();
            String label = URLDecoder.decode(location.lastSegment());
            IResource tempResource = null;
            IWorkbenchAdapter adapter = null;
            try {
                IProject tempProject = ResourcesPlugin.getWorkspace().getRoot().getProject("WebDAVLabelProviderTempProject"); //$NON-NLS-1$
                if (resource instanceof Folder) {
                    tempResource = tempProject.getFolder(new Path(label));
                } else {
                    tempResource = tempProject.getFile(new Path(label));
                }
                adapter = getAdapter(tempResource);
            } catch (Exception e) {
                IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
            }
            if (adapter == null) {
                return null;
            }
            descriptor = adapter.getImageDescriptor(tempResource);
            if (descriptor == null) {
                return null;
            }

            //add any annotations to the image descriptor
            descriptor = decorateImage(descriptor, element);

            //obtain the cached image corresponding to the descriptor
            if (_imageTable == null) {
                _imageTable = new Hashtable<ImageDescriptor, Image>(40);
            }
            Image image = (Image) _imageTable.get(descriptor);
            if (image == null) {
                image = descriptor.createImage();
                _imageTable.put(descriptor, image);
            }
            return image;
        } else if (element instanceof WebDAVConnection) {
            if (((WebDAVConnection) element).isConnected() || ((WebDAVConnection) element).isConnectFirst()) {
                return IOPlugin.getDefault().getImageRegistry().get(IOSharedImages.WEBDAV_SITE);
            } else {
                return IOPlugin.getDefault().getImageRegistry().get(IOSharedImages.WEBDAV_SITE_ERROR);
            }
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element) {
        if (element instanceof Resource) {
            Resource resource = (Resource) element;
            Location location = resource.location();
            String label = URLDecoder.decode(location.lastSegment());
            try {
                //check if file exists
//                Resource r = resource.doReadProperties(new PropertyNameList(new PropertyNameList.PropertyName[] {PropertyNameList.PropertyName.DISPLAY_NAME}));
//                String displayName = r.getDisplayName();
                String displayName = resource.getDisplayName();
                if (!label.equals(displayName)) {
                    label += " (" + displayName + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                }
            } catch (WvcmException e) {
            }
            return label;
        }
        return element.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
    }
}
