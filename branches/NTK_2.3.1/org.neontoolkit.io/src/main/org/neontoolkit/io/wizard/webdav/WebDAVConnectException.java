/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.wizard.webdav;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.io.Messages;



/* 
 * Created on 11.10.2006
 * Created by Juergen Baier
 *
 * Keywords: Import, Export, Connection, WebDAV, Exception
 */
public class WebDAVConnectException extends CommandException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -7120798615950028578L;
    private static final String ERROR_CODE = "OS10"; //$NON-NLS-1$
    private static final String MSG_1 = Messages.getString("WebDAVConnectException.1"); //$NON-NLS-1$
    private static final String MSG_2 = Messages.getString("WebDAVConnectException.2"); //$NON-NLS-1$
    private String _serverName;

    public WebDAVConnectException(String serverName, Throwable cause) {    	
        super(MSG_1 + " \"" + serverName + "\"" + MSG_2, cause); //$NON-NLS-1$ //$NON-NLS-2$
        _serverName = serverName;
    }

    public WebDAVConnectException(String serverName) {
        super(MSG_1 + " " + serverName + " " + MSG_2); //$NON-NLS-1$ //$NON-NLS-2$
        _serverName = serverName;
    }
    
    public String getServerName() {
    	return _serverName;
 
    }
    
    /* (non-Javadoc)
     * @see com.ontoprise.exception.OntopriseException#getErrorCode()
     */
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }


}
