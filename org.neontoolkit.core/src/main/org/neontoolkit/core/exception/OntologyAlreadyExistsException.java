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
 * This Exception is thrown if an ontology with the same uri exists within datamodel
 */

public class OntologyAlreadyExistsException extends NeOnCoreException {


	/**
     * 
     */
    private static final long serialVersionUID = 6263755913095095259L;

    /**
	 * @param s
	 */
	public OntologyAlreadyExistsException(String ontologyUri) {
		super(ontologyUri);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OntologyAlreadyExistsException(String message, Throwable cause) {
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
