/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.textfields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.PropertyProposalProvider;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author mer
 * @author Nico Stieler
 * 
 *         This class represents a StyledText field for OWL properties (annotations, data, and object properties).
 * 
 *         It uses syntax highlighting. It offers auto-completion (for properties of the chosen type) It is restricted to a single line. New-Lines are not
 *         permitted. It can be edited. It supports CTRL-click to jump to the property entity.
 * 
 *         For display, it uses the namespace-setting as chosen by the user, also for editing.
 */
public class PropertyText extends AbstractOwlTextField {

    public static final int WIDTH = 200;
    public static final int DATA_PROPERTY = PropertyProposalProvider.DATA_PROPERTY_STYLE;
    public static final int OBJECT_PROPERTY = PropertyProposalProvider.OBJECT_PROPERTY_STYLE;
    public static final int ANNOTATION_PROPERTY = PropertyProposalProvider.ANNOTATION_PROPERTY_STYLE;

    /**
     * 
     */
    public PropertyText(Composite parent, OWLModel owlModel, int type) {
        this(parent,owlModel,owlModel, type);
    }
    /**
     * 
     */
    public PropertyText(Composite parent, OWLModel localOwlModel, OWLModel sourceOwlModel, int type) {
        super(parent, localOwlModel, sourceOwlModel);

        GridData data = new GridData();
        data.widthHint = WIDTH;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;

        createTextWidget(parent, data, new PropertyProposalProvider(localOwlModel, sourceOwlModel, type), false, true);
    }
}
