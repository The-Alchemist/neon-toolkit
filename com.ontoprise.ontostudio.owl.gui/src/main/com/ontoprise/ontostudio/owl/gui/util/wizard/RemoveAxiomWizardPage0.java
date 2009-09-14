/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.wizard;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.swt.widgets.RadioButtonComposite;

import com.ontoprise.ontostudio.owl.gui.Messages;

public class RemoveAxiomWizardPage0 extends UserInputWizardPage {

    protected Text _descriptionText;
    protected String _deleteMode = RemoveClazzWizard.DELETE_CLAZZ;

    private RadioButtonComposite _syntaxChooser;

    protected RemoveAxiomWizardPage0(String pageName) {
        super(pageName);
        setTitle(pageName);
    }

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout());
        GridData data = new GridData();
        data.widthHint = 400;
        comp.setLayoutData(data);

        data = new GridData();
        data.verticalIndent = 8;

        _descriptionText = null;

        Group buttonGroup = new Group(comp, SWT.NONE);
        buttonGroup.setText(Messages.RemoveAxiomWizardPage0_0); 
        String[] deleteModes = getDeleteModesForGui();
        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.marginWidth = 10;
        fillLayout.marginHeight = 5;
        buttonGroup.setLayout(fillLayout);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        buttonGroup.setLayoutData(data);
        _syntaxChooser = new RadioButtonComposite(buttonGroup, SWT.VERTICAL, deleteModes);
        _syntaxChooser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteModeWidgetSelected(e);
            }

        });

        _descriptionText = new Text(comp, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.FILL;
        data.widthHint = 400;
        data.heightHint = 50;
        _descriptionText.setLayoutData(data);

        setControl(comp);
    }

    protected String[] getDeleteModesForGui() {
        String[] deleteModes = new String[] {Messages.RemoveAxiomWizardPage0_1, Messages.RemoveAxiomWizardPage0_2};  
        return deleteModes;
    }

    protected void deleteModeWidgetSelected(SelectionEvent e) {
        String mode = (String) ((Button) e.getSource()).getText();
        if (mode != null) {
            _deleteMode = mode;
            ((RemoveAxiomWizard) getWizard()).setDeleteMode(mode);
            if (mode.equals(RemoveAxiomWizard.DELETE_ENTITY)) {
                _descriptionText.setText(Messages.RemoveAxiomWizardPage0_3); 
            } else if (mode.equals(RemoveAxiomWizard.DELETE_SUBTREE)) {
                _descriptionText.setText(Messages.RemoveAxiomWizardPage0_4); 
            }
        }
    }

    public String getDeleteMode() {
        return _deleteMode;
    }

}
