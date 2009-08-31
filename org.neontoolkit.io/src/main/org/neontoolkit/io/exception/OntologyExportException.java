/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.exception;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.io.Messages;


/* 
 * Created on 7.04.2006
 * Created by Werner Hihn
 *
 * Keywords: Ontology, Export, Exception
 */

/**
 * This exception is thrown when an error occured during the export of an ontology.
 */
public class OntologyExportException extends CommandException {

    private static final long serialVersionUID = -11409511679569914L;
    private static final String ERROR_CODE = "OS13"; //$NON-NLS-1$
    private static final String MSG_0 = Messages.getString("OntologyExportException.0"); //$NON-NLS-1$
    private static final String MSG_1 = Messages.getString("OntologyExportException.1"); //$NON-NLS-1$


    public OntologyExportException(Throwable reason) {
        super(MSG_0, reason);
    }

    public OntologyExportException(String message) {
        super(message);
    }

    public OntologyExportException(String fileName, Throwable reason) {
        super(MSG_1 + " " + fileName + ".", reason); //$NON-NLS-1$ //$NON-NLS-2$
    }

    
    /* (non-Javadoc)
     * @see com.ontoprise.exception.OntopriseException#getErrorCode()
     */
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

}
