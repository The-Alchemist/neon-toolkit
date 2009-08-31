/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 23.03.2009
 * Created by: diwe
 ******************************************************************************/
package org.neontoolkit.core.exception;

/**
 * @author diwe
 *
 */
public class IllegalProjectException extends NeOnCoreException {

    private static final long serialVersionUID = 8581615341946998695L;


    /**
     * @param s
     */
    public IllegalProjectException(String s) {
        super(s);
    }

    public IllegalProjectException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getErrorCode() {
        return String.valueOf(NeOnErrorCodes.ILLEGAL_PROJECT);
    }

}
