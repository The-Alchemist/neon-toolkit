package com.ontoprise.ontostudio.owl.model.commands;

import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

public abstract class OWLOntologyRequestCommand extends DatamodelCommand {

    private String _ontology;
    
    public OWLOntologyRequestCommand(String project, String ontology, Object... arguments) {
        super(project, argumentsAsList(ontology, arguments));
        _ontology = ontology;
    }
    
    protected OWLModel getOwlModel() throws NeOnCoreException {
        OWLModel model = OWLModelFactory.getOWLModel(getOntology(), getProjectName());
        return model;
    }
    
    protected String getOntology() {
        return _ontology;
    }
    
}
