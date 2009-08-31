/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.datatypes;

import java.lang.reflect.InvocationTargetException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDatatype;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class RemoveDatatype extends OWLModuleChangeCommand {

    public RemoveDatatype(String project, String ontologyId, String datatypeId) throws NeOnCoreException {
        super(project, ontologyId, datatypeId);
    }

    @Override
    public void doPerform() throws CommandException {
        try {
            OWLDatatype datatype = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLDatatype(OWLUtilities.toURI(getArgument(2).toString()));
            getOwlModel().delEntity(datatype, null);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        } catch (InterruptedException e) {
            throw new CommandException(e);
        } catch (InvocationTargetException e) {
            throw new CommandException(e);
        }
    }

}
