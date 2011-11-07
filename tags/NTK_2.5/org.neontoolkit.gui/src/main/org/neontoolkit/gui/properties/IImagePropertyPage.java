/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

import org.eclipse.swt.graphics.Image;

/**
 * @author werner
 * 
 * Should be implemented by sub property pages, that are used as main property pages. 
 *
 */
public interface IImagePropertyPage {

    Image getImage();
}
