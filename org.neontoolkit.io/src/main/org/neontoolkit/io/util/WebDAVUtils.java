/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 17.03.2009
 * Created by: mika
 ******************************************************************************/
package org.neontoolkit.io.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.Platform;
import org.neontoolkit.io.webdav.WebDAVSiteManager;


/**
 * @author mika
 *
 */
public class WebDAVUtils {
    /**
     * adds userinfo to the url in the format 
     * http://user:pwd@host/
     * user and pwd are from the Keyring of eclipse
     * @param fileUrl
     * @return
     */
    @SuppressWarnings("unchecked")
    public static URL getAuthImportUrl(String fileUrl) {
        String pwd = ""; //$NON-NLS-1$
        String user = ""; //$NON-NLS-1$
        String realm = ""; //$NON-NLS-1$
        String authString = ""; //$NON-NLS-1$
        URL authUrl = null;
        try {
            authUrl = new URL(fileUrl);
            WebDAVSiteManager man = WebDAVSiteManager.getManager();
            URL serverUrl = man.getSiteFromUrl(authUrl).getServerURL();
            HashMap info = (HashMap) Platform.getAuthorizationInfo(serverUrl, realm, "Basic"); //$NON-NLS-1$
            if (info != null && info.containsKey("user")) { //$NON-NLS-1$
                user = (String) info.get("user"); //$NON-NLS-1$
                authString = user;
            }
            if (!user.equals("") && info != null && info.containsKey("password")) { //$NON-NLS-1$ //$NON-NLS-2$
                pwd = (String) info.get("password"); //$NON-NLS-1$
                authString += ":" + pwd; //$NON-NLS-1$
            }
            if(!authString.equals("")) { //$NON-NLS-1$
                String protocol = authUrl.getProtocol() + "://"; //$NON-NLS-1$
                String authProtocol = protocol + authString + "@"; //$NON-NLS-1$
                String oldUrlString = fileUrl.toString();
                String newUrlString = authProtocol + oldUrlString.substring(protocol.length(), oldUrlString.length());
                try {
                    authUrl = new URL(newUrlString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }           
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        return authUrl;
    }

}
