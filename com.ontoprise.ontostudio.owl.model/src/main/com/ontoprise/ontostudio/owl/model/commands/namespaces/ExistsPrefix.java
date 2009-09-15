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

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class ExistsPrefix extends OWLOntologyRequestCommand {

    private Boolean _exists;

    /**
     * @param project
     * @param ontology
     * @param arguments
     */
    public ExistsPrefix(String project, String ontology, String prefix) {
        super(project, ontology, prefix);
    }

    @Override
    protected void perform() throws CommandException {
        String prefix = (String) getArgument(2);

        try {
            OWLNamespaces namespaces = getOwlModel().getNamespaces();
            _exists = namespaces.getNamespaceForPrefix(prefix) != null;
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

    public boolean getExists() throws CommandException {
        if (_exists == null) {
            perform();
        }
        return _exists.booleanValue();
    }

}
