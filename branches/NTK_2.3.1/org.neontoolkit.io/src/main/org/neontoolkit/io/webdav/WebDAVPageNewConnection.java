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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.io.Messages;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Settings, WebDAV, Connection
 */

/**
 * WizardPage to create a new webdav-connection
 */
public class WebDAVPageNewConnection extends WizardPage {

    protected Text _serverURLInput;
    protected Text _userInput;
    protected Text _passwordInput;
    protected Button _extendedSettings;

    protected String _serverURL = ""; //$NON-NLS-1$
    protected String _user = ""; //$NON-NLS-1$
    protected String _password = ""; //$NON-NLS-1$
    protected String _realm = ""; //$NON-NLS-1$
    protected String _proxyServer = ""; //$NON-NLS-1$

    protected IWizardPage _prevPage;
    protected IWizard _wizard;
    protected Composite _composite;

    class EventHandler implements SelectionListener, ModifyListener {

        public void widgetSelected(SelectionEvent se) {
            checkStatus();
        }

        public void widgetDefaultSelected(SelectionEvent se) {
            widgetSelected(se);
        }

        public void modifyText(ModifyEvent me) {
            checkStatus();
        }
    }

    public WebDAVPageNewConnection() {
        super("wizardPage"); //$NON-NLS-1$
        setTitle(Messages.getString("WebDAVPageNewConnection.6")); //$NON-NLS-1$
        setDescription(Messages.getString("WebDAVPageNewConnection.7")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        _composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, false);
        _composite.setLayout(gridLayout);
        GridData gridData;
        EventHandler listener = new EventHandler();

        Label serverURLLabel = new Label(_composite, SWT.NONE);
        serverURLLabel.setText(Messages.getString("WebDAVPageNewConnection.8")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        serverURLLabel.setLayoutData(gridData);

        _serverURLInput = new Text(_composite, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        _serverURLInput.addModifyListener(listener);
        _serverURLInput.setLayoutData(gridData);

        Label userLabel = new Label(_composite, SWT.NONE);
        userLabel.setText(Messages.getString("WebDAVPageNewConnection.9")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        userLabel.setLayoutData(gridData);

        _userInput = new Text(_composite, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        _userInput.setLayoutData(gridData);
        _userInput.addModifyListener(listener);

        Label passwordLabel = new Label(_composite, SWT.NONE);
        passwordLabel.setText(Messages.getString("WebDAVPageNewConnection.10")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        passwordLabel.setLayoutData(gridData);

        _passwordInput = new Text(_composite, SWT.BORDER | SWT.PASSWORD);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        _passwordInput.setLayoutData(gridData);
        _passwordInput.addModifyListener(listener);

        
        Label dummyLabel = new Label(_composite, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 2;        
        dummyLabel.setLayoutData(gridData);

        Label dummyLabel2 = new Label(_composite, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        dummyLabel2.setLayoutData(gridData);
        
        _extendedSettings = new Button(_composite, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        _extendedSettings.setLayoutData(gridData);
        _extendedSettings.setText(Messages.getString("WebDAVPageNewConnection.13")); //$NON-NLS-1$
        _extendedSettings.addSelectionListener(
            new SelectionListener() {/* (non-Javadoc)
	         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	         */
	        public void widgetDefaultSelected(SelectionEvent e) {
	        }
	        /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected(SelectionEvent e) {
                WebDAVAdvancedDialog ad = 
                    new WebDAVAdvancedDialog(
                            getShell(), 
                            "Advanced WebDAV Settings", //$NON-NLS-1$
                            Messages.getString("WebDAVPageNewConnection.11"), //$NON-NLS-1$
                            _realm,
                            Messages.getString("WebDAVPageNewConnection.12"), //$NON-NLS-1$
                            _proxyServer);
                if(ad.open() == Window.OK) {
                    _realm = ad.getRealm();
                    _proxyServer = ad.getProxy();
                }
            }
        });

        initControls();
        setControl(_composite);
    }

    protected void initControls() {
        if (!_serverURL.equals("")) { //$NON-NLS-1$
            _serverURLInput.setText(_serverURL);
            _serverURLInput.setEnabled(false);
            _userInput.setText(_user);
            _passwordInput.setText(_password);
        }
        checkStatus();
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
    }

    public void checkStatus() {
        String status = null;
        boolean pageComplete = true;
        //check for possible errors
        String serverURL = _serverURLInput.getText();
        if (getServerURL() == null || serverURL.length() == 0) {
            status = Messages.getString("WebDAVPageNewConnection.14"); //$NON-NLS-1$
            pageComplete = false;
        }
        updateStatus(status);
        setPageComplete(pageComplete);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#isCurrentPage()
     */
    @Override
    protected boolean isCurrentPage() {
        return super.isCurrentPage();
    }

    public void clearSettings() {
        _serverURLInput.setText(""); //$NON-NLS-1$
        _userInput.setText(""); //$NON-NLS-1$
        _passwordInput.setText(""); //$NON-NLS-1$
        _realm = ""; //$NON-NLS-1$
        _proxyServer = ""; //$NON-NLS-1$
    }

    /**
     * @return Returns the serverURL.
     */
    public URL getServerURL() {
        try {
            return new URL(_serverURLInput.getText());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return _userInput.getText();
    }

    /**
     * @return Returns the password.
     */
    protected String getPassword() {
        return _passwordInput.getText();
    }

    public String getRealm() {
        return _realm;
    }

    /**
     * @return Returns the proxyServer.
     */
    public URL getProxyURL() {
        try {
            return new URL(_proxyServer);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public String getProxyServer() {
        return _proxyServer;
    }

    public void setProxyServer(String proxyServer) {
        this._proxyServer = proxyServer;
    }

    public void setPassword(String password) {
        this._password = password;
        this._passwordInput.setText(password);
    }

    public void setRealm(String realm) {
        this._realm = realm;
    }

    public void setServerURL(String serverURL) {
        this._serverURL = serverURL;
        this._serverURLInput.setText(serverURL);
    }

    public void setUser(String user) {
        this._user = user;
        this._userInput.setText(user);
    }
}
