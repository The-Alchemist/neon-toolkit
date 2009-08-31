/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.webdav;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.io.IOPlugin;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.wizard.webdav.WebDAVConnectException;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: WebDAV, Connection
 */

/**
 * Wizard to create a WebDAV Connection
 */
public class NewWebDAVConnectionWizard extends Wizard {

    private WebDAVPageNewConnection _pageNewConnection;
    private WebDAVActionSelectionPage _pageActionSelection;

    private URL _serverURL;
    private URL _proxyURL;
    private String _user = ""; //$NON-NLS-1$
    private String _password = ""; //$NON-NLS-1$
    private String _realm = ""; //$NON-NLS-1$
    protected WebDAVConnection _selectedWebDAVConnection = null;

    public NewWebDAVConnectionWizard() {
        super();
        setWindowTitle(Messages.getString("NewWebDAVConnectionWizard.1")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyImportWizard#addPages()
     */
    @Override
    public void addPages() {
        _pageActionSelection = new WebDAVActionSelectionPage();
        _pageActionSelection.setWizard(this);
        addPage(_pageActionSelection);

        _pageNewConnection = new WebDAVPageNewConnection();
        _pageNewConnection.setWizard(this);
        addPage(_pageNewConnection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        if (this._pageActionSelection._removeSiteOption.getSelection()) {
            this.deleteSite(_selectedWebDAVConnection);
        } else {
            WebDAVConnection conn = WebDAVSiteManager.getManager().getSite(this._pageNewConnection.getServerURL());
            conn.setServerURL(this._pageNewConnection.getServerURL());
            conn.setRealm(this._pageNewConnection.getRealm());
            conn.setUser(this._pageNewConnection.getUser());
            conn.setProxyServer(this._pageNewConnection.getProxyURL());
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("user", this._pageNewConnection.getUser()); //$NON-NLS-1$
            info.put("password", this._pageNewConnection.getPassword()); //$NON-NLS-1$
            try {
                Platform.addAuthorizationInfo(conn.getServerURL(), conn.getRealm(), "Basic", info); //$NON-NLS-1$
            } catch (CoreException e) {
                IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
            }

            try {
                conn.connect();
            } catch (WebDAVConnectException ex) {
            	NeonToolkitExceptionHandler handler = new NeonToolkitExceptionHandler();
                if(ex.getCause() instanceof NullPointerException) {
                    handler.handleException(Messages.getString("NewWebDAVConnectionWizard.4") + " " + ex.getServerName(), ex, getShell()); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    handler.handleException(ex, ex.getCause(), getShell());
                }
                return false;
            }
            this.addSite(conn);
        }
        return true;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return _password;
    }

    /**
     * @return Returns the proxyURL.
     */
    public URL getProxyURL() {
        return _proxyURL;
    }

    /**
     * @return Returns the serverURL.
     */
    public URL getServerURL() {
        return _serverURL;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return _user;
    }

    public String getRealm() {
        return _realm;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword(String password) {
        this._password = password;
    }

    /**
     * @param proxyURL
     *            The proxyURL to set.
     */
    public void setProxyURL(URL proxyURL) {
        this._proxyURL = proxyURL;
    }

    /**
     * @param serverURL
     *            The serverURL to set.
     */
    public void setServerURL(URL serverURL) {
        this._serverURL = serverURL;
    }

    /**
     * @param user
     *            The user to set.
     */
    public void setUser(String user) {
        this._user = user;
    }

    public void setRealm(String realm) {
        this._realm = realm;
    }

    @SuppressWarnings("unchecked")
	public void setWebDAVConnection(WebDAVConnection conn) {
        this._selectedWebDAVConnection = conn;
        this._pageNewConnection._serverURLInput.setEditable(true);
        this._pageNewConnection.clearSettings();
        if (conn != null) {
            if (conn.getServerURL() != null) {
                this._pageNewConnection.setServerURL(conn.getServerURL().toExternalForm());
            }
            this._pageNewConnection.setUser(conn.getUser());
            this._pageNewConnection.setRealm(conn.getRealm());
            if (conn.getProxyServer() != null) {
                this._pageNewConnection.setProxyServer(conn.getProxyServer().toExternalForm());
            }
            this._pageNewConnection._serverURLInput.setEditable(false);
            String password = ""; //$NON-NLS-1$
            HashMap<String, String> info = (HashMap<String, String>) Platform.getAuthorizationInfo(conn.getServerURL(), conn.getRealm(), "Basic"); //$NON-NLS-1$
            if (info != null && info.containsKey("password")) { //$NON-NLS-1$
                password = info.get("password"); //$NON-NLS-1$
            } else {
                password = ""; //$NON-NLS-1$
            }
            this._pageNewConnection.setPassword(password);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.webdav.IWebDAVSelectionPage#addSite(com.ontoprise.ontostudio.io.webdav.WebDAVConnection)
     */
    public void addSite(WebDAVConnection site) {
        _pageActionSelection._webDAVSiteManager.addSite(site);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.webdav.IWebDAVSelectionPage#deleteSite(com.ontoprise.ontostudio.io.webdav.WebDAVConnection)
     */
    public void deleteSite(WebDAVConnection site) {
        _pageActionSelection._webDAVSiteManager.removeSite(site.getServerURL());
    }

}
