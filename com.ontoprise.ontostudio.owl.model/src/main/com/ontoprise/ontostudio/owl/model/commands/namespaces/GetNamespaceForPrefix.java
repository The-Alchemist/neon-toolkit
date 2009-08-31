/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 22.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.namespaces;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class GetNamespaceForPrefix extends OWLOntologyRequestCommand {

    private String _result;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetNamespaceForPrefix(String project, String module, String prefix) {
        super(project, module, prefix);
    }

    @Override
    protected void perform() throws CommandException {
        String prefix = (String) getArgument(2);
        try {
            OWLNamespaces namespaces = getOwlModel().getNamespaces();
            _result = namespaces.getNamespaceForPrefix(prefix);
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
