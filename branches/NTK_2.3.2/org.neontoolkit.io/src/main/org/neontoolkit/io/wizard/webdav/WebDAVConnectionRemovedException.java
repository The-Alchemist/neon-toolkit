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
 * Keywords: Import, Connection, WebDAV, Exception
 */

/**
 * @author Juergen Baier
 */
public class WebDAVConnectionRemovedException extends CommandException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 8851813378571063737L;
    private static final String ERROR_CODE = "OS10"; //$NON-NLS-1$
    private static final String MSG_1 = Messages.getString("WebDAVConnectionRemovedException.1"); //$NON-NLS-1$
    private static final String MSG_2 = Messages.getString("WebDAVConnectionRemovedException.2"); //$NON-NLS-1$

    public WebDAVConnectionRemovedException(String serverName) {
        super(MSG_1 + " " + serverName + " " + MSG_2); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /* (non-Javadoc)
     * @see com.ontoprise.exception.OntopriseException#getErrorCode()
     */
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

}
