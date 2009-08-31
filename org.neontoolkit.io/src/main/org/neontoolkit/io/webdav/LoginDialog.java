/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.webdav;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Login, Authorization, WebDAV, Connection
 */
public class LoginDialog extends Dialog {

    private String _user;
    private Text _userText;
    private String _pwd;
    private Text _pwdText;
    private String _dialogUserMessage;
    private String _dialogPwdMessage;
    private String _title;

    public LoginDialog(Shell parentShell, String dialogTitle, String dialogUserMessage, String initialUser, String dialogPwdMessage, String initialPwd) {
        super(parentShell);
        _title = dialogTitle;
        if (initialUser == null) {
            _user = ""; //$NON-NLS-1$
        } else {
            _user = initialUser;
        }
        if (initialPwd == null) {
            _pwd = ""; //$NON-NLS-1$
        } else {
            _pwd = initialPwd;
        }
        this._dialogUserMessage = dialogUserMessage;
        this._dialogPwdMessage = dialogPwdMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (_title != null) {
            newShell.setText(_title);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.InputDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create user
        if (_dialogUserMessage != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(_dialogUserMessage);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        _userText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        _userText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        _userText.setText(_user);
        // create password
        if (_dialogPwdMessage != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(_dialogPwdMessage);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        _pwdText = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        _pwdText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        _pwdText.setText(_pwd);

        applyDialogFont(composite);
        return composite;
    }

    public String getUser() {
        return _user;
    }

    public String getPwd() {
        return _pwd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        _user = _userText.getText();
        _pwd = _pwdText.getText();
        super.okPressed();
    }

}
