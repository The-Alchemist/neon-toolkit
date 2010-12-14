/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.dataProperty;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ontoprise.ontostudio.owl.gui.Messages;

/**
 * Wizard page that is displayed in the DeleteDataPropertyWizard.
 */

public class DeleteDataPropertyWizardPage extends UserInputWizardPage {
    private Button _removeSubProperties;

    private DeleteDataPropertyProcessor _processor;

    /**
     * @param name
     */
    public DeleteDataPropertyWizardPage(DeleteDataPropertyProcessor processor) {
        super("DeleteDataPropertyWizardPage"); //$NON-NLS-1$
        _processor = processor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        result.setLayout(new GridLayout());

        SelectionListener listener = new SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                selectionChanged();
            }
        };

        _removeSubProperties = new Button(result, SWT.CHECK);
        _removeSubProperties.setText(Messages.DeleteDataPropertyWizardPage_1); 
        _removeSubProperties.setSelection(_processor.getRemoveSubProperties());
        _removeSubProperties.addSelectionListener(listener);

    }

    private void selectionChanged() {
        _processor.setRemoveSubProperties(_removeSubProperties.getSelection());
    }
}
