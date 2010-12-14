/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
