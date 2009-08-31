/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.nodes;

import org.eclipse.swt.graphics.Image;

/*
 * Created by Werner Hihn
 */

/**
 * Default node with an image. 
 *  
 * @author Werner Hihn
 */
public class DefaultImageNode extends OntoPowerGraphDefaultNode {

	private Image _image;
	
	public DefaultImageNode() {
		super();
	}
	
	public Image getImage() {
		return _image;
	}
	
	public void setImage(Image image) {
	    _image = image;
	}
}
