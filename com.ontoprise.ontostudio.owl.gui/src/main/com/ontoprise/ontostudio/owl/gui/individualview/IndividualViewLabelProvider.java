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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;

public class IndividualViewLabelProvider extends LabelProvider implements IColorProvider {

    private Color _backgroundColor;

    public Color getBackground(Object element) {
        if (element instanceof IndividualViewItem) {
            IndividualViewItem item = (IndividualViewItem) element;
            if (item.isDirect()) {
                return null;
            }
            if (_backgroundColor == null) {
                _backgroundColor = new Color(Display.getCurrent(), new RGB(200, 200, 200));
            }
            return _backgroundColor;
        }
        return null;
    }

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
