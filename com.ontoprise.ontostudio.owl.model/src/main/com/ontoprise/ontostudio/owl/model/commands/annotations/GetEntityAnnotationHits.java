/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 22.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class GetEntityAnnotationHits extends OWLOntologyRequestCommand {

    private List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetEntityAnnotationHits(String project, String module, String entityUri) {
        super(project, module, entityUri);
    }

    @Override
    protected void perform() throws CommandException {
        String entityUri = (String) getArgument(2);
        _results = new ArrayList<String[]>();
        try {
            Set<LocatedItem<OWLAnnotationAssertionAxiom>> annots = getOwlModel().getAnnotationHits(entityUri);
            for (LocatedItem<OWLAnnotationAssertionAxiom> annotation: annots) {
                String axiom = OWLUtilities.toString(annotation.getItem());
                String ontologyUri = annotation.getOntologyURI();
                _results.add(new String[] {axiom, ontologyUri});
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    public String[][] getResults() throws CommandException {
        if (_results == null) {
            perform();
        }
        return _results.toArray(new String[_results.size()][2]);
    }

}
