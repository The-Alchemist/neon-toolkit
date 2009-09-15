/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

/* 
 * Created on: 15.06.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Dialog
 */
/**
 * Confirm dialog with an optional line containing a checkbox.
 */

public class CheckableConfirmDialog extends SelectionDialog {

	public static final int OK_CHECKED = 2;
	private String _title = ""; //$NON-NLS-1$
	private String _message = ""; //$NON-NLS-1$
	private String _checkBoxText = ""; //$NON-NLS-1$
	private Button _checkButton;
    private boolean _isChecked = false;
    private boolean _showCheck = false;

    /**
	 * @param parentShell
	 */
	public CheckableConfirmDialog(Shell parentShell, String title, boolean showCheck) {
		super(parentShell);
		setHelpAvailable(false);
		_showCheck = showCheck;
		_title = title;
	}
	
	public void setDialogMessage(String message) {
		_message = message;
	}
	
	public void setCheckboxText(String checkBoxText) {
		_checkBoxText = checkBoxText;
	}

    @Override
	protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.getShell().setText(_title);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.getShell().setLayout(layout);
        Label textLabel = new Label(composite, SWT.NONE);
        textLabel.setText(_message); 
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.heightHint = 30;
        gridData.widthHint = 500;
        textLabel.setLayoutData(gridData);
        if (_showCheck) {
	        _checkButton = new Button(composite, SWT.CHECK);
	        _checkButton.setText(_checkBoxText);
        }
        return composite;
    }

    /*
     * returns 1 if cancel, 0 - if ok without delete of subelements, 2 - if
     * ok with deleting of subconcepts
     */
    @Override
	public int open() {
        int code = super.open();
        if (code == CANCEL) {
            return CANCEL;
        } else if (code == OK) {
            if (_isChecked) {
                return OK_CHECKED;
            } else {
                return OK;
            }
        }
        return code;
    }

    @Override
	protected void okPressed() {
    	if (_showCheck) {
    		_isChecked = _checkButton.getSelection();
    	}
        super.okPressed();
    }
}
