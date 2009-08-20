/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.wizard;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.ontoprise.ontostudio.owl.gui.Messages;

public class RemoveClazzWizardPage0 extends RemoveAxiomWizardPage0 {

    protected RemoveClazzWizardPage0(String pageName) {
        super(pageName);
        setTitle(pageName);
    }

    @Override
    protected String[] getDeleteModesForGui() {
        String[] deleteModes = new String[] {Messages.RemoveClazzWizardPage0_0, Messages.RemoveClazzWizardPage0_1, Messages.RemoveClazzWizardPage0_2}; 
        return deleteModes;
    }

    @Override
    protected void deleteModeWidgetSelected(SelectionEvent e) {
        String mode = (String) ((Button) e.getSource()).getText();
        if (mode != null) {
            _deleteMode = mode;
            ((RemoveAxiomWizard) getWizard()).setDeleteMode(mode);
            if (mode.equals(RemoveClazzWizard.DELETE_CLAZZ)) {
                _descriptionText.setText(Messages.RemoveClazzWizardPage0_3); 
            } else if (mode.equals(RemoveClazzWizard.DELETE_CLAZZ_WITH_INDIVIDUALS)) {
                _descriptionText.setText(Messages.RemoveClazzWizardPage0_4); 
            } else if (mode.equals(RemoveClazzWizard.DELETE_SUBTREE)) {
                _descriptionText.setText(Messages.RemoveClazzWizardPage0_5); 
            }
        }
    }
}
