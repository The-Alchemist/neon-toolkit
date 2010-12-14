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
 * @author Nico Stieler
 * 
 *         This class represents a StyledText field for an short string, e.g. namespace prefix, or language tag.
 * 
 *         It does not use syntax highlighting. It does not wrap the content into multiple lines. It can be edited. New-Lines are not permitted. The size is
 *         fixed to a small amount.
 */
public class ShortStringText extends AbstractOwlTextField {

    public static final int WIDTH = 50;

    /**
     * 
     */
    public ShortStringText(Composite parent, OWLModel owlModel) {
        this(parent,owlModel,owlModel);
    }
    /**
	 * 
	 */
    public ShortStringText(Composite parent, OWLModel localOwlModel, OWLModel sourceOwlModel) {
        super(parent, localOwlModel, sourceOwlModel);

        GridData data = new GridData();
        data.widthHint = WIDTH;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.CENTER;
        data.grabExcessHorizontalSpace = false;

        createTextWidget(parent, data, false, false);
    }
}
