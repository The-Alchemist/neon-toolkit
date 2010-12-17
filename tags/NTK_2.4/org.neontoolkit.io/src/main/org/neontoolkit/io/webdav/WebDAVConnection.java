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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;

import javax.wvcm.Callback;
import javax.wvcm.ControllableFolder;
import javax.wvcm.ControllableResource;
import javax.wvcm.Folder;
import javax.wvcm.Location;
import javax.wvcm.PropertyNameList;
import javax.wvcm.Provider;
import javax.wvcm.ProviderFactory;
import javax.wvcm.Resource;
import javax.wvcm.WvcmException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.io.IOPlugin;
import org.neontoolkit.io.wizard.webdav.WebDAVConnectException;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: WebDAV, Connection, Access
 */

/**
 * Class to connect to WebDAV
 */
public class WebDAVConnection {

    private URL _serverURL;
    private String _user;
    private String _password;
    private String _realm;
    private URL _proxyServer;
    private boolean _connected = false;
    private Provider _webDAVProvider = null;
    Resource _topResource = null;
    private boolean _connectFirst = true;
    private Exception _exception = null;

    private Callback _callback;
    

    protected WebDAVConnection() {

    }

    public void setServerURL(URL serverURL) {
        this._serverURL = serverURL;
    }

    public URL getServerURL() {
        return _serverURL;
    }

    public void setUser(String user) {
        this._user = user;
    }

    public String getUser() {
        return _user;
    }

    public void setPassword(String pwd) {
        this._password = pwd;
    }

    public String getPassword() {
        return this._password;
    }

    public void setRealm(String realm) {
        this._realm = realm;
    }

    public String getRealm() {
        return this._realm;
    }

    public void setProxyServer(URL proxyServer) {
        this._proxyServer = proxyServer;
    }

    public URL getProxyServer() {
        return this._proxyServer;
    }

    public Resource connect() throws WebDAVConnectException {
        return connectIntern();
    }

    private Resource connectIntern() throws WebDAVConnectException {
        _exception = null;
        _topResource = null;
        Hashtable<String, String> h = new Hashtable<String, String>();
        h.put("host", _serverURL.getHost()); //$NON-NLS-1$
        h.put("port", _serverURL.getPort() + ""); //$NON-NLS-1$ //$NON-NLS-2$
        h.put("user", getUser()); //$NON-NLS-1$
        String realm = getRealm();
        if (realm != null && !realm.equals("")) { //$NON-NLS-1$
            h.put("realm", realm); //$NON-NLS-1$
        }
        _callback = new CallbackImpl();
        try {
            _webDAVProvider = ProviderFactory.createProvider("org.apache.wvcm.ProviderImpl", _callback, h); //$NON-NLS-1$
            String location = _serverURL.toExternalForm();
            
            // Note: The following code of the crappy wvcm library leads to a NPE (!) if location is invalid.
            Location loca = _webDAVProvider.location(location);
            ControllableFolder folder = (ControllableFolder) loca.folder();
            PropertyNameList pnl = new PropertyNameList(new PropertyNameList.PropertyName[] {PropertyNameList.PropertyName.LAST_MODIFIED});
            folder.doReadMemberList(pnl, false);
            _topResource = loca.resource();
            _topResource = _topResource.doReadProperties(null);
            _connected = true;
            _connectFirst = false;
            return _topResource;
        } catch (InvocationTargetException e) {
            _webDAVProvider = null;
            _connected = false;
            _connectFirst = false;
            if(e.getTargetException() instanceof Exception) {
                _exception = (Exception) e.getTargetException();                
            }
            throw new WebDAVConnectException(_serverURL.toExternalForm(), e.getTargetException());
        } catch (Exception e) {
            _webDAVProvider = null;
            _connected = false;
            _connectFirst = false;
            _exception = e;
            throw new WebDAVConnectException(_serverURL.toExternalForm(), e);
        }
    }

    public Resource getResource() {
        return _topResource;
    }

    public Resource createFolder(String parentFolder, String newFolderName) throws IOException {
        try {
            Location newLocation = _webDAVProvider.location(parentFolder + "/" + newFolderName); //$NON-NLS-1$
            ControllableFolder newFolder = (ControllableFolder) newLocation.folder();
            newFolder.doCreateResource();
            return newFolder;
        } catch (WvcmException e) {
            throw new IOException("Cannot create Folder: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    public void deleteFolder(String folder) throws Exception {
        Location location = _webDAVProvider.location(folder);
        ControllableFolder f = (ControllableFolder) location.folder();
        f.doUnbind();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this._serverURL.toString();
    }

    public boolean isConnected() {
        return _connected;
    }

    public static synchronized InputStream getWebDAVInputStream(Resource file) throws IOException {
        try {
            try {
                if (file instanceof ControllableResource) {
                    ((ControllableResource) file).doControl();
                }
            } catch (WvcmException e1) {
                IOPlugin.getDefault().logError("", e1); //$NON-NLS-1$
            }
            Resource r = file.doReadProperties(null);
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            r = r.doReadContent(null, o);
            InputStream i = new ByteArrayInputStream(o.toByteArray());
            return i;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public void setDisplayName(Resource resource, String displayName) {
        resource.setDisplayName(displayName);
        try {
            resource.doWriteProperties();
        } catch (WvcmException e) {
            IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
        }
    }

    public Resource getFile(Resource resource, String fileName) throws IOException {
        try {
            if (resource instanceof Folder) {
                Location loca = _webDAVProvider.location(resource.location().toString() + fileName);
                ControllableResource newResource = loca.controllableResource();
                try {
                    //check if file exists
                    newResource.doReadProperties(null);
                } catch (WvcmException e) {
                    newResource.doCreateResource();
                }
                newResource.doControl();
                return newResource;
            } else {
                if (resource instanceof ControllableResource) {
                    ((ControllableResource) resource).doControl();
                }
                return resource;
            }
        } catch (WvcmException e) {
            throw new IOException(e.getMessage());
        }
    }

    public Resource getFileFromUrl(URL url) throws WebDAVConnectException {
        int retry = 0;
        do {
            if (!_connected) {
                connect();
            }
            if (_webDAVProvider != null) {
                
                String location = url.toExternalForm();
                try {
                    Location loca = _webDAVProvider.location(location);
                    Resource r = loca.resource();
                    try {
                        r.doReadProperties(null);
                    } catch (WvcmException e) {
                        r = loca.controllableResource();
                        ((ControllableResource) r).doCreateResource();
                        ((ControllableResource) r).doControl();
                    }
                    _connected = true;
                    _connectFirst = false;
                    return r;
                } catch (WvcmException e1) {
                    retry++;
                    if(e1.getMessage().endsWith("Unauthorized") && retry < 3) { //$NON-NLS-1$
                        if(!login()) {
                            throw new WebDAVConnectException(url.toExternalForm(), e1);
                        }
                    } else {
                        throw new WebDAVConnectException(url.toExternalForm(), e1);
                    }
                }
            }
        } while (retry < 4);
        return null;
    }

    public Resource getFileFromUrlForOutput(URL url) throws WebDAVConnectException {
        int retry = 0;
        do {
            if (!_connected) {
                connect();
            }
            if (_webDAVProvider != null) {
                
//                String location = url.toExternalForm();
                String location;
				try {
					location = url.toURI().toString();
				} catch (URISyntaxException e2) {
					location = url.toString();
				}
                try {
                    Location loca = _webDAVProvider.location(location);
                    Resource r = loca.resource();
                    Resource r1;
                    try {
                        r1 = r.doReadProperties(null);
                    } catch (WvcmException e) {
                        r1 = loca.controllableResource();
                        ((ControllableResource) r1).doCreateResource();
                        try {
                        	((ControllableResource) r1).doControl();
                        } catch (WvcmException e1) {
                        	// Ignore version Control not available
                        }
                    }
                    try {
                        r1.doWriteProperties();
                    } catch (WvcmException e) {
                        if(e.getMessage().endsWith("Unauthorized") && retry < 3) { //$NON-NLS-1$
                            retry++;
                            if(!login()) {
                                throw new WebDAVConnectException(url.toExternalForm(), e);
                            }
                        }
                        //else ignore, is some problem with webdavserver, see bug #2985
                    }
                    _connected = true;
                    _connectFirst = false;
                    return r1;
                } catch (WvcmException e1) {
                    if(e1.getMessage().endsWith("Unauthorized") && retry < 3) { //$NON-NLS-1$
                        retry++;
                        if(!login()) {
                        	throw new WebDAVConnectException(url.toExternalForm(), e1);
                        }
                    } else {
                        throw new WebDAVConnectException(url.toExternalForm(), e1);
                    }
                }
            }
        } while (retry < 4);
        return null;
    }

    public boolean isFileExisting(URL url) {
        if (!_connected) {
            try {
                connect();
            } catch (WebDAVConnectException e) {
            }
        }
        if (_webDAVProvider != null) {
            String location = url.toExternalForm();
            try {
                location = url.toURI().toString();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                Location loca = _webDAVProvider.location(location);
                ControllableResource r = loca.controllableResource();
                try {
                    r.doReadProperties(null);
                    return true;
                } catch (WvcmException e) {
                }
            } catch (WvcmException e1) {
            }
        }
        return false;
    }

	class UIOperation implements Runnable {
	    public boolean _canceled = false;
		@Override
        public void run() {
			String password = ""; //$NON-NLS-1$
			String user = ""; //$NON-NLS-1$
	        HashMap info = (HashMap) Platform.getAuthorizationInfo(getServerURL(), getRealm(), "Basic"); //$NON-NLS-1$
	        if (info != null && info.containsKey("password")) { //$NON-NLS-1$
	            password = (String) info.get("password"); //$NON-NLS-1$
	        } 
	        if (info != null && info.containsKey("user")) { //$NON-NLS-1$
	            user = (String) info.get("user"); //$NON-NLS-1$
	        } 
	        LoginDialog ld = new LoginDialog(null, "Login to " + getServerURL(), "User:", user, "Password:", password); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        if (ld.open() == Window.OK) {
	            password = ld.getPwd();
	            user = ld.getUser();
	            try {
	                Platform.flushAuthorizationInfo(getServerURL(), getRealm(), "Basic"); //$NON-NLS-1$
	                HashMap<String, String> info2 = new HashMap<String, String>();
	                info2.put("user", user); //$NON-NLS-1$
	                info2.put("password", password); //$NON-NLS-1$
	                Platform.addAuthorizationInfo(getServerURL(), getRealm(), "Basic", info2); //$NON-NLS-1$
	            } catch (CoreException e) {
	                IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
	            }	            
	            ld.close();
	        } else {
	            _canceled = true;
	            ld.close();
	        }
		}
	}
	
	public boolean login() {
        UIOperation uio = new UIOperation();
		if (Display.getCurrent() != null) {
			uio.run();
		} else {
			Display.getDefault().syncExec(uio);
		}
		return !uio._canceled;
    }

    /**
     * Callback implementation
     */
    private class CallbackImpl implements Callback {

        Authentication _authentication = new AuthenticationImpl();

        /**
         * Return authentication information for the current user.
         * 
         * @param realm
         *            The authentication realm for the provider.
         */
        @Override
        public Authentication getAuthentication(String realm, Integer retryCount) {
            return _authentication;
        }

        class AuthenticationImpl implements Authentication {

            @Override
            public String password() {
                String pwd = ""; //$NON-NLS-1$
                HashMap info = (HashMap) Platform.getAuthorizationInfo(getServerURL(), getRealm(), "Basic"); //$NON-NLS-1$
                if (info != null && info.containsKey("password")) { //$NON-NLS-1$
                    pwd = (String) info.get("password"); //$NON-NLS-1$
                }
                return pwd;
            }

            @Override
            public String loginName() {
                String user = ""; //$NON-NLS-1$
                HashMap info = (HashMap) Platform.getAuthorizationInfo(getServerURL(), getRealm(), "Basic"); //$NON-NLS-1$
                if (info != null && info.containsKey("user")) { //$NON-NLS-1$
                    user = (String) info.get("user"); //$NON-NLS-1$
                }
                return user;
            }
            
        }
    }


    public boolean isConnectFirst() {
        return _connectFirst;
    }

    public Exception getConnectionException() {
        return _exception;
    }
}
