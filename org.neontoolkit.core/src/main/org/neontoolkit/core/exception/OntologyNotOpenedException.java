/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.core.exception;



/* 
 * Created on: 24.11.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Exception
 *
 */
/**
 * This Exception is thrown if an action is performed on an ontology which is not opened within datamodel
 */

public class OntologyNotOpenedException extends NeOnCoreException {


    /**
     * 
     */
    private static final long serialVersionUID = 9206889036034217148L;

    /**
	 * @param s
	 */
	public OntologyNotOpenedException(String ontologyUri) {
		super(ontologyUri);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OntologyNotOpenedException(String message, Throwable cause) {
		super(message, cause);
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.exception.OntopriseException#getErrorCode()
	 */
	@Override
	public String getErrorCode() {
		return null;
	}

}
