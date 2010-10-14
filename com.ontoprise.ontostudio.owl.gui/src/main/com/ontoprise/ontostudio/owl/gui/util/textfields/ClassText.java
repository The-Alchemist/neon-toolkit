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

import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.ClazzProposalProvider;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author mer
 * 
 *         This class represents a StyledText field for OWL classes.
 * 
 *         It uses syntax highlighting. It offers auto-completion.
 *         It is restricted to a single line. New-Lines are not
 *         permitted. It can be edited. It supports CTRL-click to jump to the property entity.
 * 
 *         For display, it uses the namespace-setting as chosen by the user, also for editing.
 */
public class ClassText extends AbstractOwlTextField {

    public static final int WIDTH = 200;
    
    /**
	 * 
	 */
    public ClassText(Composite parent, OWLModel owlModel, String entityName) {
        super(parent, owlModel, entityName);

        GridData data = new GridData();
        data.widthHint = WIDTH;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;

        createTextWidget(parent, data, new ClazzProposalProvider(owlModel), false, true);
    }
}
