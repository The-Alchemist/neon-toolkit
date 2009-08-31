package com.ontoprise.ontostudio.owl.visualize.action;

import org.neontoolkit.ontovisualize.actions.AbstractVisualizeOntologyAction;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;


public class VisualizeOWLOntologyAction extends AbstractVisualizeOntologyAction {

    @Override
    protected String getOntologyLanguage() {
        return OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE;
    }

}
