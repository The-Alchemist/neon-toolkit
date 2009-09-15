/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.namespaces;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class GetDefaultNamespace extends OWLOntologyRequestCommand {

    private String _result;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetDefaultNamespace(String project, String module) {
        super(project, module);
    }

    @Override
    protected void perform() throws CommandException {
        try {
            _result = getOwlModel().getDefaultNamespace();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    public String getResult() throws CommandException {
        if (_result == null) {
            perform();
        }
        return _result;
    }

}
