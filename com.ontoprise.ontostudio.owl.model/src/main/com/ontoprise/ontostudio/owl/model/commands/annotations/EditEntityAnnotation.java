/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 23.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.annotations;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class EditEntityAnnotation extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public EditEntityAnnotation(String project, String module, String entityUri, String oldAxiomText, String[] newValues) throws NeOnCoreException {
        super(project, module, entityUri, oldAxiomText, newValues);
    }

    @Override
    protected void doPerform() throws CommandException {
        String entityUri = (String) getArgument(2);
        String oldAxiomText = (String) getArgument(3);
        String[] newValues = (String[]) getArgument(4);

        try {
            // first remove old axiom
            new ApplyChanges(getProjectName(), getOntology(), new String[0], new String[]{oldAxiomText}).perform();
            // then add new
            new AddEntityAnnotation(getProjectName(), getOntology(), entityUri, newValues).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
