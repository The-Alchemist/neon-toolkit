/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 *
 */
public class GetIndividuals extends OWLOntologyRequestCommand {

    protected List<String> _results;
    
    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetIndividuals(String project, String module, String clazzId) {
        super(project, module, clazzId);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String>();
        Set<OWLIndividual> individuals = null;
        try {
            individuals = getOwlModel().getIndividuals((String) getArgument(2));
            if (individuals != null) {
                for (OWLIndividual i: individuals) {
                    _results.add(OWLUtilities.toString(i));
                }
            }
        } catch (NeOnCoreException e) {
            // nothing to do
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
