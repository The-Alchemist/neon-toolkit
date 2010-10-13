/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
/**
 * 
 * @author Nico Stieler
 */
public class EmptyFormRow extends AbstractFormRow {

    private Button _addButton;

    public EmptyFormRow(FormToolkit toolkit, Composite parent, int cols) {
        super(toolkit, parent, cols);
    }

    @Override
    public void init(AbstractRowHandler handler) {
        _handler = handler;
        _addButton = createAddButton(getParent(), false);
        _cancelButton = createCancelButton(getParent(), true);
        _addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _handler.addPressed();
            }

        });
        _cancelButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _handler.cancelPressed();
            }

        });
    }

    @Override
    public Button getSubmitButton() {
        return getAddButton();
    }

    public Button getAddButton() {
        return _addButton;
    }

    public void setAddButton(Button button) {
        _addButton = button;
    }

    public void setCancelButton(Button button) {
        _cancelButton = button;
    }

    @Override
    protected void jump() {
        // nothing to do
    }
}
