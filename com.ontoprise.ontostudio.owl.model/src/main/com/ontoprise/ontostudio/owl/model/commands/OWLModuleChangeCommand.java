/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * @author werner
 * 
 */
public abstract class OWLModuleChangeCommand extends DatamodelCommand {
    private OWLModel _owlModel;
    private String _ontology;

    /**
     * @param project
     * @param ontology
     * @param arguments
     * @throws NeOnCoreException
     */
    public OWLModuleChangeCommand(String project, String ontology, Object... arguments) throws NeOnCoreException {
        super(project, argumentsAsList(ontology, arguments));
        _ontology = ontology;
        try {
            _owlModel = OWLModelFactory.getOWLModel(getOntology(), getProjectName());
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
    }

    public String getOntology() {
        return _ontology;
    }

    protected OWLModel getOwlModel() {
        return _owlModel;
    }

    @Override
    public void perform() throws CommandException {
        doPerform();
        try {
            getOntologyProject().readAndDispatchWhileWaitingForEvents();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    protected abstract void doPerform() throws CommandException;
}
