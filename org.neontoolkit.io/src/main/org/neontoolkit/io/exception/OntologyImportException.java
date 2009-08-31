/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.exception.OntologyAlreadyExistsException;
import org.neontoolkit.io.Messages;

/* 
 * Created on 9.11.2005
 * Created by Juergen Baier
 *
 * Keywords: Ontology, Import, Exception
 */

/**
 * This exception is thrown when an error occured during the import of an ontology.
  */
public class OntologyImportException extends CommandException {

    private static final long serialVersionUID = 8191377987508675814L;
    private static final String ERROR_CODE = "OS9"; //$NON-NLS-1$
    private static final String MSG_1A = Messages.getString("OntologyImportException.1a"); //$NON-NLS-1$
    private static final String MSG_1B = Messages.getString("OntologyImportException.1b"); //$NON-NLS-1$
    private static final String MSG_2A = Messages.getString("OntologyImportException.2a"); //$NON-NLS-1$
    private static final String MSG_2B = Messages.getString("OntologyImportException.2b"); //$NON-NLS-1$
    private static final String MSG_3 = Messages.getString("OntologyImportException.3"); //$NON-NLS-1$
    private static final String MSG_4 = Messages.getString("OntologyImportException.4"); //$NON-NLS-1$
    private static final String MSG_5 = Messages.getString("OntologyImportException.5"); //$NON-NLS-1$

    protected OntologyImportException(String message) {
        super(message);
    }
    
    public OntologyImportException(Throwable cause) {
        super(MSG_4, cause);
    }


    public OntologyImportException(String fileName, FileNotFoundException cause) {
        super(MSG_1A + " " + fileName + ". " + MSG_1B, cause); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param fileName	The file name of the ontology which cause problems.
     * @param cause		The cause for this exception (<code>IOException</code>).
     */
    public OntologyImportException(String fileName, IOException cause) {
        super(MSG_2A + " " + fileName + ". " + MSG_2B, cause); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param fileName	The file name of the ontology which cause problems.
     * @param cause		The cause for this exception (<code>ModuleAlreadyExistsException</code>).
     */
    public OntologyImportException(String fileName, OntologyAlreadyExistsException cause) {
        super(MSG_3 + " " + fileName + ".", cause); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param fileName	The file name of the ontology which cause problems.
     * @param cause		The cause for this exception.
     */
    public OntologyImportException(String fileName, Throwable cause) {
        super(MSG_4 + " " + fileName + ".", cause); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param fileName	The file name of the ontology which cause problems.
     * @param cause		The cause for this exception (<code>NeOnCoreException</code>).
     */
    public OntologyImportException(String fileName, NeOnCoreException cause) {
        super(MSG_2A + " " + fileName + ". " + MSG_5, cause); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /* (non-Javadoc)
     * @see com.ontoprise.exception.OntopriseException#getErrorCode()
     */
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

}
  
