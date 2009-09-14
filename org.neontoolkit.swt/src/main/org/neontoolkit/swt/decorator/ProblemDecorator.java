/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt.decorator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.neontoolkit.swt.SwtPlugin;
import org.neontoolkit.swt.images.ImageDescriptorRegistry;
import org.neontoolkit.swt.images.MarkerElementImageDescriptor;


/* 
 * Created on 19.02.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public abstract class ProblemDecorator implements ILabelDecorator {
	public static final int DECORATE_WARNING= MarkerElementImageDescriptor.WARNING;
	public static final int DECORATE_ERROR= MarkerElementImageDescriptor.ERROR;	

	private ImageDescriptorRegistry _registry;
	
	public Image decorateImage(Image image, Object element) {
		int decorationFlags= 0;
		try {
			decorationFlags = getDecorationFlags(element);
		} catch (CoreException e) {
			SwtPlugin.logError(e);
		}
		if (decorationFlags != 0) {
			ImageDescriptor baseImage= new InternalImageDescriptor(image);
			Rectangle bounds= image.getBounds();
			return getRegistry().get(new MarkerElementImageDescriptor(baseImage, decorationFlags, new Point(bounds.width, bounds.height)));
		}
		return image;
	}
    
    public Image decorateImage(Image image, Object element, String markerId) {
        int decorationFlags= 0;
        try {
            decorationFlags = getDecorationFlags(element, markerId);
        } catch (CoreException e) {
            SwtPlugin.logError(e);
        }
        if (decorationFlags != 0) {
            ImageDescriptor baseImage= new InternalImageDescriptor(image);
            Rectangle bounds= image.getBounds();
            return getRegistry().get(new MarkerElementImageDescriptor(baseImage, decorationFlags, new Point(bounds.width, bounds.height)));
        }
        return image;
    }

	public String decorateText(String text, Object element) {
		return text;
	}

	public void addListener(ILabelProviderListener listener) {
		// Do nothing
	}

	public void dispose() {
		// Do nothing
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// Do nothing
	}

	private ImageDescriptorRegistry getRegistry() {
		if (_registry == null) {
			_registry= new ImageDescriptorRegistry();
		}
		return _registry;
	}
	
	protected abstract int getDecorationFlags(Object element) throws CoreException;
    
    protected abstract int getDecorationFlags(Object element, String markerId) throws CoreException;
}
