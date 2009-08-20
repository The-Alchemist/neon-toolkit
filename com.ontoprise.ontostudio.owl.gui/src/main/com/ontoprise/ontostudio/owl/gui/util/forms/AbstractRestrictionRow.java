/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Holds text widgets of a form row, and the axioms behind them.
 * 
 * @author werner
 * 
 */
public abstract class AbstractRestrictionRow extends AbstractFormRow {

    private StyledText _rangeText;

    public AbstractRestrictionRow(FormToolkit toolkit, Composite parent, int cols) {
        super(toolkit, parent, cols);
    }

    public void setRangeText(StyledText rangeText) {
        _rangeText = rangeText;
    }

    public StyledText getRangeText() {
        return _rangeText;
    }

    public abstract void setSubmitButton(Button b);
    
    public abstract void setCancelButton(Button b);
}
