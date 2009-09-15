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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This the core exception thrown by the NeOn Toolkit datamodel.
 * @author Dirk Wenke
 */
public abstract class NeOnCoreException extends Exception {

    private static final long serialVersionUID = -418703790784855995L;

    public NeOnCoreException(String s) {
        super(s);
    }

    public NeOnCoreException(Throwable nestedException) {
        super(nestedException);
    }
    
    public NeOnCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenience method for retrieving the stack trace as a <code>String</code>.
     * 
     * @return the stack trace as a string
     */
    public String getStackTraceAsString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.printStackTrace(pw);
        return sw.toString (); 
    }

    public abstract String getErrorCode();
}
