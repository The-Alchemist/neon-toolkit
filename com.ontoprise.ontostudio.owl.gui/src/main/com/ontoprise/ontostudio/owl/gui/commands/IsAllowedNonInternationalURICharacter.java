/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands;

import org.neontoolkit.core.command.CommandException;

import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

public class IsAllowedNonInternationalURICharacter extends OWLOntologyRequestCommand {
    
    private boolean _isAllowed;

    public IsAllowedNonInternationalURICharacter(String project, String module, char input) {
        super(project, module, input);
    }

    @Override
    protected void perform() throws CommandException {
        char c = ((Character) getArgument(2)).charValue();
        // reserved = gen-delims / sub-delims
        //
        // gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"
        //
        // sub-delims = "!" / "$" / "&" / "'" / "(" / ")"
        // / "*" / "+" / "," / ";" / "="
        //
        // unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"

        // reserved
        if (":/?#[]@".indexOf(c) != -1) { //$NON-NLS-1$
            // gen-delims
            _isAllowed = true;
        }
        if ("!$&'()*+,;=".indexOf(c) != -1) { //$NON-NLS-1$
            // sub-delims
            _isAllowed = true;
        }
        // unreserved
        if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) {
            // ALPHA
            _isAllowed = true;
        }
        if ('0' <= c && c <= '9') {
            // DIGIT
            _isAllowed = true;
        }
        if ("-._~".indexOf(c) != -1) { //$NON-NLS-1$
            _isAllowed = true;
        }
        _isAllowed = false;
    }

    public boolean isAllowed() throws CommandException {
        run();
        return _isAllowed;
    }
}
