/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 20.01.2009
 * Created by: werner
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
public class AddRestriction extends OWLModuleChangeCommand {

    /**
     * Handles the modification of "restrictions on properties". The <code>newValues</code> array has a size of four elements containing information about:
     * quantifier, property, range, cardinality. 
     * NOTICE: complex class descriptions have to be passed in KAON2Syntax. This means ISyntaxManager has to be used in UI before calling these commands.  
     * 
     * @param oldValues
     * @param newValues
     * @param ontologyId
     * @param projectName
     * @return
     * @throws ControlException
     * @throws NeOnCoreException
     */
    public AddRestriction(String project, String module, String desc, String[] newValues, String clazzType) throws NeOnCoreException {
        super(project, module, desc, newValues, clazzType);
    }

    @Override
    protected void doPerform() throws CommandException {
        String desc = (String) getArgument(2);
        String[] newValues = (String[]) getArgument(3);
        String clazzType = (String) getArgument(4);

        String quantifier = newValues[0];
        String propertyId = newValues[1];
        String range = newValues[2];
        String quantity = newValues[3];

        if (emptyString(clazzType) || emptyString(quantifier) || (emptyString(propertyId))) {
            // lack of information, not possible to store
            throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
        }
        try {
            String axiomText = OWLCommandUtils.createAxiomText(desc, clazzType, quantifier, propertyId, range, quantity, getOntology(), getProjectName());
            new ApplyChanges(getProjectName(), getOntology(), new String[] {axiomText}, new String[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
