/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.namespaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class GetRegisteredPrefixes extends OWLOntologyRequestCommand {

    List<String> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetRegisteredPrefixes(String project, String module) {
        super(project, module);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String>();
        Iterator<String> iter;
        try {
            iter = getOwlModel().getNamespaces().prefixes();
            for (Iterator<String> iterator = iter; iterator.hasNext();) {
                _results.add(iterator.next());
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
