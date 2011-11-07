/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
/**
 * 
 * @author Nico Stieler
 */
public class IndividualViewLabelProvider extends LabelProvider implements IColorProvider {

    @Override
    @SuppressWarnings("rawtypes")
    public Color getBackground(Object element) {
        if (element instanceof IIndividualTreeElement) {
            IIndividualTreeElement item = (IIndividualTreeElement) element;
            if (item.isImported()) {
                return OWLGUIUtilities.COLOR_FOR_IMPORTED_AXIOMS;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public Color getForeground(Object element) {
        return null;
    }

    @Override
    public String getText(Object obj) {
        return obj.toString();
    }

    @Override
    public Image getImage(Object obj) {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
    }

}
