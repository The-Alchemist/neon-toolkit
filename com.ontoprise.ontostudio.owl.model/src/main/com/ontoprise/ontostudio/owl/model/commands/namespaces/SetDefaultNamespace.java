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

import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class SetDefaultNamespace extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public SetDefaultNamespace(String project, String module, String namespace) throws NeOnCoreException {
        super(project, module, namespace);
    }

    @Override
    protected void doPerform() throws CommandException {
        String namespace = getArgument(2) != null ? (String) getArgument(2) : null;

        try {
            getOwlModel().setDefaultNamespace(namespace);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
