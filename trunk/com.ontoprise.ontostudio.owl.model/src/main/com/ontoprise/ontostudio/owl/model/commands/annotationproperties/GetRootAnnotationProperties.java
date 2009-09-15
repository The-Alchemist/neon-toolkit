/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.annotationproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

public class GetRootAnnotationProperties extends OWLOntologyRequestCommand {

    List<String> _results;
    
    public GetRootAnnotationProperties(String project, String module) {
        super(project, module);
    }

    @Override
    public void perform() throws CommandException {
        Set<OWLAnnotationProperty> rootAnnotationProperties;
        try {
            rootAnnotationProperties = getOwlModel().getRootAnnotationProperties();
            _results = new ArrayList<String>();
            for (OWLAnnotationProperty op: rootAnnotationProperties) {
                _results.add(op.getURI().toString());
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }
    
    public String[] getResults() throws CommandException {
        if (_results == null) {
            perform();
        }
        return _results.toArray(new String[_results.size()]);
    }
    
    public int getResultCount() throws CommandException {
        if (_results == null) {
            perform();
        }
        return _results.size();
    }

}
