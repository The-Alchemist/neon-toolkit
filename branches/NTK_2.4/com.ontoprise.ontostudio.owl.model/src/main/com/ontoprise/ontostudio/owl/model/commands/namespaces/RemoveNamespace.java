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
public class RemoveNamespace extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public RemoveNamespace(String project, String module, String prefix) throws NeOnCoreException {
        super(project, module, prefix);
    }

    @Override
    protected void doPerform() throws CommandException {
        String prefix = (String) getArgument(2);

        if (prefix != null) {
            try {
                getOwlModel().setNamespacePrefix(prefix, null);
            } catch (NeOnCoreException e) {
                throw new CommandException(e);
            }
        }
    }

}
