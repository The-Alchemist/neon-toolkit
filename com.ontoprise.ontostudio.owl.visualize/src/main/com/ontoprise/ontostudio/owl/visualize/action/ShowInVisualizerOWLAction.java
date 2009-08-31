package com.ontoprise.ontostudio.owl.visualize.action;

import org.neontoolkit.ontovisualize.actions.AbstractShowInVisualizerAction;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;


public class ShowInVisualizerOWLAction extends AbstractShowInVisualizerAction {

    @Override
    public String getOntologyLanguage() {
        return OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE;
    }

}
