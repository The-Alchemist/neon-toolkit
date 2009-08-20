/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
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

/**
 * @author mer
 * 
 *         This class represents a StyledText field for an arbitrary string, e.g. dataproperty or annotation property value.
 * 
 *         It does not use syntax highlighting. It wraps the content into multiple lines if needed. It can be edited. New-Lines are permitted.
 */
public class StringText extends AbstractOwlTextField {

    public static final int WIDTH = 200;

    /**
	 * 
	 */
    public StringText(Composite parent) {
        super(parent, null);

        GridData data = new GridData();
        data.widthHint = WIDTH;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;

        createTextWidget(parent, data, true, false);
    }
}
