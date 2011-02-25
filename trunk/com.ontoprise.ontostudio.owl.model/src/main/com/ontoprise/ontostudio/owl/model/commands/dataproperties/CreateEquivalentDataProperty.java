/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.dataproperties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class CreateEquivalentDataProperty extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateEquivalentDataProperty(String project, String module, String propertyUri1, String propertyUri2) throws NeOnCoreException {
        super(project, module, propertyUri1, propertyUri2);
    }

    @Override
    protected void doPerform() throws CommandException {
        String propertyUri1 = (String) getArgument(2);
        String propertyUri2 = (String) getArgument(3);
        try {
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLOntology ontology = getOwlModel().getOntology();
            
            OWLDataProperty dataproperty1 = OWLUtilities.dataProperty(propertyUri1, ontology);
            OWLDataProperty dataproperty2 = OWLUtilities.dataProperty(propertyUri2, ontology);
            OWLEquivalentDataPropertiesAxiom axiom = factory.getOWLEquivalentDataPropertiesAxiom(dataproperty1, dataproperty2);
            new ApplyChanges(getProjectName(), getOntology(), new String[] {OWLUtilities.toString(axiom, ontology)}, new String[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
