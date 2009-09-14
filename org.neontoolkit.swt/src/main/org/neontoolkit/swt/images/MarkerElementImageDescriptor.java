/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt.images;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.neontoolkit.swt.SwtImages;
import org.neontoolkit.swt.SwtPlugin;


/* 
 * Created on 18.02.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public class MarkerElementImageDescriptor extends CompositeImageDescriptor {

	/** Flag for the warning decoration. */
	public final static int WARNING = 1 << 0;
	
	/** Flag for the error decoration. */
	public final static int ERROR =	1 << 1;

	/** Flags for the datastore decoration. */
	public final static int STORE_RAM =	1 << 2;
	public final static int STORE_PERSISTENT_DATAMODEL =	1 << 3;
	public final static int STORE_SERVER =	1 << 5;
	
	/** Flags for the datastore decoration. */
	public final static int LANGUAGE_FLOGIC =	1 << 6;
	public final static int LANGUAGE_RDF =	1 << 7;
	public final static int LANGUAGE_OWL =	1 << 8;

	private ImageDescriptor _baseImage;
	private int _decorateFlags;
	private Point _size;
	
	public MarkerElementImageDescriptor(ImageDescriptor baseImage, int decorateFlags, Point size) {
		_baseImage = baseImage;
		_decorateFlags = decorateFlags;
		_size = size;
	}
	
	private ImageData getImageData(ImageDescriptor descriptor) {
		ImageData data= descriptor.getImageData();
		if (data == null) {
			data= DEFAULT_IMAGE_DATA;
			SwtPlugin.logError("No Image data available for: " + descriptor.toString(), new IllegalArgumentException()); //$NON-NLS-1$
		}
		return data;
	}
	
	@Override
	protected void drawCompositeImage(int width, int height) {
		ImageData bg= getImageData(_baseImage);
		drawImage(bg, 0, 0);

		drawTopRight();
		drawBottomRight();
		drawBottomLeft();
	}

	@Override
	protected Point getSize() {
		return _size;
	}

	private void drawTopRight() {		
		Point size= getSize();
		int x= size.x;

		if ((_decorateFlags & LANGUAGE_FLOGIC) != 0) {
			ImageData data= getImageData(SwtImages.DECO_LANGUAGE_FLOGIC);
			x-= data.width;
			drawImage(data, x, 0);
		}
		else if ((_decorateFlags & LANGUAGE_RDF) != 0) {
			ImageData data= getImageData(SwtImages.DECO_LANGUAGE_RDF);
			x-= data.width;
			drawImage(data, x, 0);
		}
		else if ((_decorateFlags & LANGUAGE_OWL) != 0) {
			ImageData data= getImageData(SwtImages.DECO_LANGUAGE_OWL);
			x-= data.width;
			drawImage(data, x, 0);
		}
	}		
	
	private void drawBottomRight() {
		Point size= getSize();
		int x= size.x;

//		if ((_decorateFlags & STORE_RAM) != 0) {
//			ImageData data= getImageData(SwtImages.DECO_STORE_RAM);
//			x-= data.width;
//			drawImage(data, x, size.y - data.height);
//		}
//		else 
		if ((_decorateFlags & STORE_PERSISTENT_DATAMODEL) != 0) {
			ImageData data= getImageData(SwtImages.DECO_STORE_H2);
			x-= data.width;
			drawImage(data, x, size.y - data.height);
		}
		else if ((_decorateFlags & STORE_SERVER) != 0) {
			ImageData data= getImageData(SwtImages.DECO_STORE_SERVER);
			x-= data.width;
			drawImage(data, x, size.y - data.height);
		}

	}		
	
	private void drawBottomLeft() {
		Point size= getSize();
		int x= 0;
		if ((_decorateFlags & ERROR) != 0) {
			ImageData data= getImageData(SwtImages.DECO_ERROR);
			drawImage(data, x, size.y - data.height);
			x+= data.width;
		}
		if ((_decorateFlags & WARNING) != 0) {
			ImageData data= getImageData(SwtImages.DECO_WARNING);
			drawImage(data, x, size.y - data.height);
			x+= data.width;
		}

	}		

	/* (non-Javadoc)
	 * Method declared on Object.
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MarkerElementImageDescriptor)) {
			return false;
		}
		MarkerElementImageDescriptor that = (MarkerElementImageDescriptor)object;
		return (_baseImage.equals(that._baseImage) && _decorateFlags == that._decorateFlags && _size.equals(that._size));
	}
	
	/* (non-Javadoc)
	 * Method declared on Object.
	 */
	@Override
	public int hashCode() {
		return _baseImage.hashCode() | _decorateFlags | _size.hashCode();
	}

}
