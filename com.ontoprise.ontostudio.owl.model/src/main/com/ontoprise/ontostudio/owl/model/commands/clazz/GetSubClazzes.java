/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 15.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class GetSubClazzes extends OWLOntologyRequestCommand {

    private List<String> _result;

    /**
     * @param project
     * @param ontology
     */
    public GetSubClazzes(String project, String ontology, String superClazzId) {
        super(project, ontology, superClazzId);
    }

    @Override
    protected void perform() throws CommandException {
        try {
            Set<OWLClass> subClazzes = getOwlModel().getSubClasses((String) getArgument(2));
            _result = new ArrayList<String>();
            for (OWLClass clazz: subClazzes) {
                _result.add(clazz.getURI().toString());
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