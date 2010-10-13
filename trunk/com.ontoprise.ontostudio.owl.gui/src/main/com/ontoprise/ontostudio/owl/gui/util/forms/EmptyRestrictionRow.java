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
import org.neontoolkit.core.exception.NeOnCoreException;
/**
 * 
 * @author Nico Stieler
 */
public class EmptyRestrictionRow extends AbstractRestrictionRow {

    private Button _addButton;

    public EmptyRestrictionRow(FormToolkit toolkit, Composite parent, int cols) {
        super(toolkit, parent, cols);
    }

    @Override
    public Button getSubmitButton() {
        return getAddButton();
    }

    @Override
    public void init(AbstractRowHandler handler) throws NeOnCoreException {
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

    public Button getAddButton() {
        return _addButton;
    }

    @Override
    public void setCancelButton(Button button) {
        _cancelButton = button;
    }

    @Override
    public void setSubmitButton(Button b) {
        _addButton = b;
    }

    @Override
    protected void jump() {
        // nothing to do
    }

}
