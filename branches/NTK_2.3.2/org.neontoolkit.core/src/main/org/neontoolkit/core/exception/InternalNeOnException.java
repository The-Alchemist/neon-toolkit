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

/**
 * @author diwe
 *
 */
public class InternalNeOnException extends NeOnCoreException {
    private static final long serialVersionUID = 5545253761821777515L;

    public InternalNeOnException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param s
     */
    public InternalNeOnException(String s) {
        super(s);
    }

    /**
     * @param nestedException
     */
    public InternalNeOnException(Throwable nestedException) {
        super(nestedException);
    }

    @Override
    public String getErrorCode() {
        return String.valueOf(NeOnErrorCodes.UNKNOWN_EXCEPTION);
    }

}
