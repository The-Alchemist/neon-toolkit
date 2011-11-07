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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class GetOntologyAnnotations extends OWLOntologyRequestCommand {

    public static String DUMMY_ONTOLOGY_URI = "http://schema.ontoprise.com/reserved#ontology1"; //$NON-NLS-1$
    private List<String> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetOntologyAnnotations(String project, String module) {
        super(project, module);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String>();
        try {
            // it's an ontology (i.e. not a real entity)
            Set<OWLAnnotation> annots = getOwlModel().getOntologyAnnotations();

            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            for (OWLAnnotation annotation: annots) {
                OWLAnnotationAssertionAxiom ontologyAnnotation = factory.getOWLAnnotationAssertionAxiom(annotation.getProperty(), IRI.create(DUMMY_ONTOLOGY_URI), annotation.getValue());
                
                String axiom = OWLUtilities.toString(ontologyAnnotation);
                _results.add(axiom);
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
