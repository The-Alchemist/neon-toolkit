/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.objectproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class GetSuperObjectProperties extends OWLOntologyRequestCommand {

    private List<String> _result;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetSuperObjectProperties(String project, String module, String propertyId) {
        super(project, module, propertyId);
    }

    @Override
    public void perform() throws CommandException {
        String propertyId = (String) getArgument(2);
        Set<OWLObjectProperty> superObjectProperties;
        try {
            superObjectProperties = getOwlModel().getSuperObjectProperties(propertyId);
            _result = new ArrayList<String>();
            for (OWLObjectProperty op: superObjectProperties) {
                _result.add(op.getIRI().toString());
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    public String[] getResults() throws CommandException {
        if (_result == null) {
            run();
        }
        return _result.toArray(new String[_result.size()]);
    }

    public int getResultCount() throws CommandException {
        if (_result == null) {
            run();
        }
        return _result.size();
    }

}
