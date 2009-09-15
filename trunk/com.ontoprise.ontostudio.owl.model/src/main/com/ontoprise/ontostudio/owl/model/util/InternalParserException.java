/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;

/**
 * @author janiko
 *
 */
public class InternalParserException extends Exception {
    private static final long serialVersionUID = 8987804942366736710L;
    /**
     * @param message
     */
    public InternalParserException(String message) {
        super(message);
    }
    public InternalParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
