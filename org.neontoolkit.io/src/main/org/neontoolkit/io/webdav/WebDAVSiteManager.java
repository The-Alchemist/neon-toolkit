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
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.io.IOPlugin;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Management, WebDAV, Connection
 */

/**
 * Mangemenet of WebDAV Connections
 */
public class WebDAVSiteManager {

    private static WebDAVSiteManager _manager;

    private HashMap<URL, WebDAVConnection> _sites;

    private WebDAVSiteManager() {
        _sites = readSites();
    }

    public static WebDAVSiteManager getManager() {
        if (_manager == null) {
            _manager = new WebDAVSiteManager();
        }
        return _manager;
    }

    public WebDAVConnection getSite(URL url) {
        if (_sites.containsKey(url)) {
            return _sites.get(url);
        }
        return new WebDAVConnection();
    }

    public HashMap<URL, WebDAVConnection> getSites() {
        return _sites;
    }

    public void addSite(WebDAVConnection newSite) {
        URL url = newSite.getServerURL();
        if (!_sites.containsKey(url)) {
            _sites.put(url, newSite);
        }
        WebDAVConnection conn = newSite;
        IDialogSettings sitesSection = NeOnUIPlugin.getDefault().getDialogSettings().getSection("WebDAVSites"); //$NON-NLS-1$
        if (sitesSection == null) {
            sitesSection = NeOnUIPlugin.getDefault().getDialogSettings().addNewSection("WebDAVSites"); //$NON-NLS-1$
        }
        ArrayList<String> sitesArray = new ArrayList<String>();
        String[] sites = sitesSection.getArray("sites"); //$NON-NLS-1$
        if (sites == null) {
            sites = new String[] {};
        }
        IDialogSettings siteSection = null;
        for (int i = 0; i < sites.length; i++) {
            String site = sites[i];
            sitesArray.add(site);
            if (site.equals(conn.toString())) {
                siteSection = sitesSection.getSection(site);
            }
        }
        if (siteSection == null) {
            String site = conn.toString();
            sitesArray.add(site);
            sitesSection.put("sites", sitesArray.toArray(new String[] {})); //$NON-NLS-1$
            siteSection = sitesSection.addNewSection(site);
        }
        siteSection.put("type", "webdav"); //$NON-NLS-1$ //$NON-NLS-2$
        siteSection.put("serverURL", conn.getServerURL().toString()); //$NON-NLS-1$
        String user = conn.getUser();
        if (user == null) {
            user = ""; //$NON-NLS-1$
        }
        siteSection.put("user", user); //$NON-NLS-1$
        String realm = conn.getRealm();
        if (realm == null) {
            realm = ""; //$NON-NLS-1$
        }
        siteSection.put("realm", realm); //$NON-NLS-1$
        URL proxyServerURL = conn.getProxyServer();
        String proxyServer = ""; //$NON-NLS-1$
        if (proxyServerURL != null) {
            proxyServer = proxyServerURL.toString();
        }
        siteSection.put("proxyURL", proxyServer); //$NON-NLS-1$

    }

    public void removeSite(URL url) {
        if (_sites.containsKey(url)) {
            WebDAVConnection conn = _sites.get(url);
            IDialogSettings sitesSection = NeOnUIPlugin.getDefault().getDialogSettings().getSection("WebDAVSites"); //$NON-NLS-1$
            if (sitesSection == null) {
                sitesSection = NeOnUIPlugin.getDefault().getDialogSettings().addNewSection("WebDAVSites"); //$NON-NLS-1$
            }
            ArrayList<String> sitesArray = new ArrayList<String>();
            String[] sites = sitesSection.getArray("sites"); //$NON-NLS-1$
            if (sites == null) {
                sites = new String[] {};
            }
            IDialogSettings siteSection = null;
            for (int i = 0; i < sites.length; i++) {
                String site = sites[i];
                if (site.equals(conn.toString())) {
                    siteSection = sitesSection.getSection(site);
                } else {
                    sitesArray.add(site);
                }
            }
            if (siteSection != null) {
                sitesSection.put("sites", sitesArray.toArray(new String[] {})); //$NON-NLS-1$
                siteSection.put("type", "webdav"); //$NON-NLS-1$ //$NON-NLS-2$
                siteSection.put("serverURL", ""); //$NON-NLS-1$ //$NON-NLS-2$
                siteSection.put("user", ""); //$NON-NLS-1$ //$NON-NLS-2$
                siteSection.put("realm", ""); //$NON-NLS-1$ //$NON-NLS-2$
                siteSection.put("proxyURL", ""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            this._sites.remove(url);
        }
    }

    public WebDAVConnection getSiteFromUrl(URL url) {
        WebDAVConnection site = null;
        int connLength = 0;
        String urlString = url.toExternalForm();
        Object[] sites = this._sites.values().toArray();
        for (int i = 0; i < sites.length; i++) {
            WebDAVConnection conn = (WebDAVConnection) sites[i];
            URL siteURL = conn.getServerURL();
            String siteURLString = siteURL.toExternalForm();
            if (urlString.startsWith(siteURLString) && siteURLString.length() > connLength) {
                connLength = siteURLString.length();
                site = conn;
            }
        }
        return site;
    }

    private HashMap<URL, WebDAVConnection> readSites() {
        if (this._sites == null) {
            this._sites = new HashMap<URL, WebDAVConnection>();
            IDialogSettings sitesSection = NeOnUIPlugin.getDefault().getDialogSettings().getSection("WebDAVSites"); //$NON-NLS-1$
            if (sitesSection != null) {
                String[] sites = sitesSection.getArray("sites"); //$NON-NLS-1$
                for (int i = 0; i < sites.length; i++) {
                    String site = sites[i];
                    IDialogSettings siteSection = sitesSection.getSection(site);
                    String siteType = siteSection.get("type"); //$NON-NLS-1$
                    if (siteType.equals("webdav")) { //$NON-NLS-1$
                        WebDAVConnection conn = new WebDAVConnection();
                        try {
                            conn.setServerURL(new URL(siteSection.get("serverURL"))); //$NON-NLS-1$
                            conn.setUser(siteSection.get("user")); //$NON-NLS-1$
                            String realm = siteSection.get("realm"); //$NON-NLS-1$
                            if (realm == null) {
                                realm = ""; //$NON-NLS-1$
                            }
                            conn.setRealm(realm);
                            String proxyURL = siteSection.get("proxyURL"); //$NON-NLS-1$
                            if (proxyURL != null && !proxyURL.equals("")) { //$NON-NLS-1$
                                try {
                                    conn.setProxyServer(new URL(proxyURL));
                                } catch (MalformedURLException e) {
                                    IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
                                }
                            }

                            this._sites.put(conn.getServerURL(), conn);
                        } catch (MalformedURLException e) {
                            IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        return this._sites;
    }
}
