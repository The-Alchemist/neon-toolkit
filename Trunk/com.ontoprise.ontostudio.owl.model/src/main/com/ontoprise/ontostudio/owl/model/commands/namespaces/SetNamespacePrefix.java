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

import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class SetNamespacePrefix extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public SetNamespacePrefix(String project, String module, String prefix, String namespace) throws NeOnCoreException {
        super(project, module, prefix, namespace);
    }

    @Override
    protected void doPerform() throws CommandException {
        String prefix = (String) getArgument(2);
        String namespace = (String) getArgument(3);

        try {
            getOwlModel().setNamespacePrefix(prefix, namespace);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
