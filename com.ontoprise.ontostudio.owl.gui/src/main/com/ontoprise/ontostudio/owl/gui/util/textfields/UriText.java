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
 *         This class represents a StyledText field for URIs.
 * 
 *         It is restricted to a single line. New-Lines are not permitted. It can be edited. It only allows valid URI strings.
 */
public class UriText extends AbstractOwlTextField {

    public static final int WIDTH = 200;

    /**
	 * 
	 */
    public UriText(Composite parent, OWLModel owlModel) {
        super(parent, owlModel, null);

        GridData data = new GridData();
        data.widthHint = WIDTH;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;

        createTextWidget(parent, data, false, false);
    }
}
