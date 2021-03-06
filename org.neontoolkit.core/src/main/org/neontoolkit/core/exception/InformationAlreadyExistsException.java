/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.exception;

import org.neontoolkit.core.command.CommandException;


/* 
 * Created on: 24.11.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Exception
 *
 */
/**
 * This Exception is thrown if information is added to the datamodel which is already 
 * existing.
 */

public class InformationAlreadyExistsException extends CommandException {

	private static final long serialVersionUID = 3753662236219342406L;

	/**
	 * @param s
	 */
	public InformationAlreadyExistsException(String s) {
		super(s);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InformationAlreadyExistsException(String message, Throwable cause) {
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
