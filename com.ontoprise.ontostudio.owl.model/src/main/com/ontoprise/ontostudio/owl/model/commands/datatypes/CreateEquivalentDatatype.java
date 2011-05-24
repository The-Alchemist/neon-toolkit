/*****************************************************************************
 * written by the NeOn Technology Foundation Ltd.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.datatypes;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author Nico Stieler
 * 
 * the functionality is already implemented in CreateSubDatatype
 * 
 */
public class CreateEquivalentDatatype extends OWLModuleChangeCommand {
    CreateSubDatatype createSubDatatype;
    /**
     * @param project
     * @param ontologyId
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateEquivalentDatatype(String project, String ontologyId, String datatypeUri, String equivalentDatatypeUri) throws NeOnCoreException {
        super(project, ontologyId, datatypeUri, equivalentDatatypeUri);
        createSubDatatype = new CreateSubDatatype(project, ontologyId, equivalentDatatypeUri, datatypeUri);
    }

    @Override
    protected void doPerform() throws CommandException {
        createSubDatatype.run();
    }

}
