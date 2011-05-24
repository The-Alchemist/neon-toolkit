/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * Based on the class CreateDisjointDataProperty with Copyright by the ontoprise GmbH.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.dataproperties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author Nico Stieler
 * 
 */
public class CreateDisjointDataProperty extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateDisjointDataProperty(String project, String module, String propertyUri1, String propertyUri2) throws NeOnCoreException {
        super(project, module, propertyUri1, propertyUri2);
    }

    @Override
    protected void doPerform() throws CommandException {
        String propertyUri1 = (String) getArgument(2);
        String propertyUri2 = (String) getArgument(3);
        try {
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            
            OWLDataProperty dataproperty1 = OWLUtilities.dataProperty(propertyUri1);
            OWLDataProperty dataproperty2 = OWLUtilities.dataProperty(propertyUri2);
            OWLDisjointDataPropertiesAxiom axiom = factory.getOWLDisjointDataPropertiesAxiom(dataproperty1, dataproperty2);
            new ApplyChanges(getProjectName(), getOntology(), new String[] {OWLUtilities.toString(axiom)}, new String[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
