/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util.file;

import java.util.Map;

/**
 * @author krekeler
 *
 */
public class UnknownOWLOntologyFormatException extends Exception {
    private static final long serialVersionUID = 7299049438344982104L;
    private final Map<String,Exception> _triedOntologyFormatExceptions;
    public UnknownOWLOntologyFormatException(Map<String,Exception> triedOntologyFormatExceptions) {
        _triedOntologyFormatExceptions = triedOntologyFormatExceptions;
    }
    public Map<String,Exception> getTriedOntologyFormatExceptions() {
        return _triedOntologyFormatExceptions;
    }
    @Override
    public String toString() {
        StringBuilder target = new StringBuilder();
        for (Map.Entry<String,Exception> entry: _triedOntologyFormatExceptions.entrySet()) {
            target.append("== " + entry.getKey() + " ==\n");  //$NON-NLS-1$//$NON-NLS-2$
            target.append(entry.getValue().getMessage() + "\n"); //$NON-NLS-1$
        }
        return target.toString();
    }
}
