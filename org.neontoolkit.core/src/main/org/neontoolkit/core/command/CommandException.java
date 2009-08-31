/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 04.06.2009
 * Created by: Dirk Wenke
 ******************************************************************************/
package org.neontoolkit.core.command;

import org.neontoolkit.core.exception.NeOnErrorCodes;

/**
 * Exception that is thrown if an implementation of ICommand cannot be run successfully. 
 * @author Dirk Wenke
 */
public class CommandException extends Exception {
	private static final long serialVersionUID = 4988957934432315647L;

	/**
	 * @param message
	 * @param cause
	 */
	public CommandException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CommandException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CommandException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Returns the error code of the exception.
	 * Should be overridden by subclasses.
	 * @return
	 */
    public String getErrorCode() {
        return String.valueOf(NeOnErrorCodes.COMMAND_EXCEPTION);
    }    

}
