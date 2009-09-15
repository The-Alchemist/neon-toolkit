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
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class RemoveRestriction extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public RemoveRestriction(String project, String module, String clazzType, String subClazzDescription, String superClazzDescription) throws NeOnCoreException {
        super(project, module, clazzType, subClazzDescription, superClazzDescription);
    }

    @Override
    protected void doPerform() throws CommandException {
        String subClazzDescription = (String) getArgument(3);
        String superClazzDescription = (String) getArgument(4);
        String clazzType = (String) getArgument(2);

        try {
            OWLAxiom axiom = OWLCommandUtils.createAxiom(subClazzDescription, superClazzDescription, clazzType, getOntology(), getProjectName());
            if (axiom != null) {
                new ApplyChanges(getProjectName(), getOntology(), new String[0], new String[]{OWLUtilities.toString(axiom)}).perform();
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
