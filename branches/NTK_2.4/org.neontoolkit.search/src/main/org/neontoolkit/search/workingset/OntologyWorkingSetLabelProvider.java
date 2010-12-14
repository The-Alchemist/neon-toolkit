/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.workingset;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;

/* 
 * Created on 26.04.2005
 * Created by Dirk Wenke 
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2005 ontoprise GmbH.
 * 
 * @author Dirk Wenke
 * 
 */
public class OntologyWorkingSetLabelProvider extends LabelProvider {

	@Override
    public Image getImage(Object element) {
		return NeOnUIPlugin.getDefault().getImageRegistry().get(SharedImages.PROJECT);
	}

	@Override
    public String getText(Object element) {
		assert element instanceof IProject;
		return ((IProject) element).getName();
	}
}
