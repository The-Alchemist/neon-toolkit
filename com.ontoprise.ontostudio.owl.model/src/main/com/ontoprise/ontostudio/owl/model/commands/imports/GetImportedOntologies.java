/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 29.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class GetImportedOntologies extends OWLOntologyRequestCommand {

    List<String> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetImportedOntologies(String project, String module) {
        super(project, module);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String>();
        try {
            Set<OWLModel> importedOntos = getOwlModel().getImportedOntologies();
            for (OWLModel o: importedOntos) {
                _results.add(o.getOntologyURI());
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
}
