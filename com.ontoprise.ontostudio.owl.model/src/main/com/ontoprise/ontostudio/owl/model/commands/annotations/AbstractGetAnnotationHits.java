/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public abstract class AbstractGetAnnotationHits extends OWLOntologyRequestCommand {

    private List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public AbstractGetAnnotationHits(String project, String module, Object ... args) {
        super(project, module, args);
    }

    @Override
    protected void perform() throws CommandException {

        try {
            OWLAnnotationSubject annotationSubject = getAnnotationSubject();
            _results = new ArrayList<String[]>();
            Set<LocatedItem<OWLAnnotationAssertionAxiom>> annots = getOwlModel().getAnnotationHits(annotationSubject);
            OWLOntology ontology = getOwlModel().getOntology();
            for (LocatedItem<OWLAnnotationAssertionAxiom> annotation: annots) {
                String axiom = OWLUtilities.toString(annotation.getItem(), ontology);
                String ontologyUri = annotation.getOntologyURI();
                _results.add(new String[] {axiom, ontologyUri});
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }
    
    protected abstract OWLAnnotationSubject getAnnotationSubject() throws NeOnCoreException;

    public String[][] getResults() throws CommandException {
        if (_results == null) {
            perform();
        }
        return _results.toArray(new String[_results.size()][2]);
    }

}
