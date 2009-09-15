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

import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.io.Messages;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Settings, WebDAV, Connection
 */

/**
 * Interface for the WebDAV Selection Page
 */
public interface IWebDAVSelectionPage {

    String DEFAULT_WEBDAV_FOLDER_PATH = Messages.getString("IWebDAVSelectionPage.0"); //$NON-NLS-1$

    void addSite(WebDAVConnection site);

    void deleteSite(WebDAVConnection site);

    Shell getShell();

    void refresh();

}
