/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.gui.properties.IImagePropertyPage;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;


public class DatatypePropertyPage extends AbstractOWLMainIDPropertyPage implements IImagePropertyPage{

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
    }

    @Override
    protected List<Section> getSections() {
        return new ArrayList<Section>();
    }

    @Override
    protected String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dispose() {
        // nothing to do
    }
}
