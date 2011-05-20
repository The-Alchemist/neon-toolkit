/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * Based on the class CreateDisjointObjectProperty with Copyright by the ontoprise GmbH.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.objectproperties;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author Nico Stieler
 * 
 */
public class CreateDisjointObjectProperty extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateDisjointObjectProperty(String project, String module, String propertyUri, String disjointPropertyUri) throws NeOnCoreException {
        super(project, module, propertyUri, disjointPropertyUri);
    }

    @Override
    protected void doPerform() throws CommandException {
        try {
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            
            OWLObjectPropertyExpression propertyUri = 
                OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax((String) getArgument(2)));
            OWLObjectPropertyExpression disjointPropertyUri = 
                OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax((String) getArgument(3)));

            new ApplyChanges(getProjectName(), getOntology(), 
                    new OWLAxiom[] {factory.getOWLDisjointObjectPropertiesAxiom(new LinkedHashSet<OWLObjectPropertyExpression>(
                            Arrays.asList(propertyUri, disjointPropertyUri)))}, 
                    new OWLAxiom[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
