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

import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author mer
 * 
 *         This class represents a StyledText field for an arbitrary string, e.g. dataproperty or annotation property value.
 * 
 *         It does not use syntax highlighting. It wraps the content into multiple lines if needed. It can be edited. New-Lines are permitted.
 */
public class AxiomText extends AbstractOwlTextField {

    public static final int WIDTH_2_COLS = 400;
    public static final int WIDTH_MORE_COLS = 200;

    /**
     * 
     */
    public AxiomText(Composite parent, OWLModel owlModel, int cols) {
        super(parent, owlModel);

        GridData data = new GridData();
        data.widthHint = cols < 4 ? WIDTH_2_COLS : WIDTH_MORE_COLS;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = false;

        createTextWidget(parent, data, true, true);
    }

    /**
     * 
     */
    public AxiomText(Composite parent, OWLModel owlModel) {
        super(parent, owlModel);

        GridData data = new GridData();
        data.verticalAlignment = SWT.FILL;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        
        createTextWidget(parent, data, true, true);
    }
}
