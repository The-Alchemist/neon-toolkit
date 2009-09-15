/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class EditRestriction extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public EditRestriction(String project, String module, String clazzType, String subClazzDesc, String[] newValues, String oldSuperClassDesc) throws NeOnCoreException {
        super(project, module, clazzType, subClazzDesc, newValues, oldSuperClassDesc);
    }

    @Override
    protected void doPerform() throws CommandException {
        String clazzType = (String) getArgument(2);
        String subClazzDesc = (String) getArgument(3);
        String[] newValues = (String[]) getArgument(4);
        String oldSuperClassDesc = (String) getArgument(5);

        String quantifier = newValues[0];
        String propertyId = newValues[1];
        String range = newValues[2];
        String quantity = newValues[3];

        try {
            String axiomToRemove = OWLCommandUtils.createAxiomText(subClazzDesc, oldSuperClassDesc, clazzType, getOntology(), getProjectName());
            String axiomToAdd = OWLCommandUtils.createAxiomText(subClazzDesc, clazzType, quantifier, propertyId, range, quantity, getOntology(), getProjectName());

            new ApplyChanges(getProjectName(), getOntology(), new String[] {axiomToAdd}, new String[] {axiomToRemove}).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
