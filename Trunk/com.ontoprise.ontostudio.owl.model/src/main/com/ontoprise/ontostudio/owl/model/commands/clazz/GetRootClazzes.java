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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

public class GetRootClazzes extends OWLOntologyRequestCommand {
    
    protected static Logger _log = Logger.getLogger(GetRootClazzes.class);

    private List<String> _result;

    public GetRootClazzes(String project, String module) {
        super(project, module);
    }

    @Override
    public void perform() throws CommandException {
        Set<OWLClass> rootClazzes;
        try {
            rootClazzes = getOwlModel().getRootClasses();
            _result = new ArrayList<String>();
            for (OWLClass clazz: rootClazzes) {
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
