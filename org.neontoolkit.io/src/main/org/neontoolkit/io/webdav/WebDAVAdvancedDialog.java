/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
 * Keywords: Settings, WebDAV, Connection
 */

/**
 * Advanced Settings for a WebDAV Connection
 */
public class WebDAVAdvancedDialog extends Dialog {

    private String _realm;
    private Text _realmInput;
    private String _proxy;
    private Text _proxyInput;
    private String _dialogRealmMessage;
    private String _dialogProxyMessage;
    private String _title;

    public WebDAVAdvancedDialog(Shell parentShell, String dialogTitle, String dialogRealmMessage, String initialRealm, String dialogProxyMessage, String initialProxy) {
        super(parentShell);
        _title = dialogTitle;
        if (initialRealm == null) {
            _realm = ""; //$NON-NLS-1$
        } else {
            _realm = initialRealm;
        }
        if (initialProxy == null) {
            _proxy = ""; //$NON-NLS-1$
        } else {
            _proxy = initialProxy;
        }
        this._dialogRealmMessage = dialogRealmMessage;
        this._dialogProxyMessage = dialogProxyMessage;
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
        // create realm
        if (_dialogRealmMessage != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(_dialogRealmMessage);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        _realmInput = new Text(composite, SWT.SINGLE | SWT.BORDER);
        _realmInput.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        _realmInput.setText(_realm);
        // create proxy
        if (_dialogProxyMessage != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(_dialogProxyMessage);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        _proxyInput = new Text(composite, SWT.SINGLE | SWT.BORDER);
        _proxyInput.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        _proxyInput.setText(_proxy);

        applyDialogFont(composite);
        return composite;
    }

    public String getRealm() {
        return _realm;
    }

    public String getProxy() {
        return _proxy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        _realm = _realmInput.getText();
        _proxy = _proxyInput.getText();
        super.okPressed();
    }

}
