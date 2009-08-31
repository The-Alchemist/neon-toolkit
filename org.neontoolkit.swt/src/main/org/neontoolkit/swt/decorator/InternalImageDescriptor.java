/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.swt.decorator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/* 
 * Created on 18.02.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public class InternalImageDescriptor extends ImageDescriptor {

	private Image _image;

	/**
	 * Constructor for InternalImageDescriptor.
	 */
	public InternalImageDescriptor(Image image) {
		super();
		_image= image;
	}

	/* (non-Javadoc)
	 * @see ImageDescriptor#getImageData()
	 */
	@Override
	public ImageData getImageData() {
		return _image.getImageData();
	}

	/* (non-Javadoc)
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && getClass().equals(obj.getClass()) && _image.equals(((InternalImageDescriptor)obj)._image);
	}

	/* (non-Javadoc)
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return _image.hashCode();
	}
}
