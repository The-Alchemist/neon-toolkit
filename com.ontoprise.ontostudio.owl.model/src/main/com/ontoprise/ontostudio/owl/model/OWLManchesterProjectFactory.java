package com.ontoprise.ontostudio.owl.model;

import org.eclipse.core.resources.IProject;
import org.neontoolkit.core.IOntologyProjectFactory;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.project.IOntologyProject;

public class OWLManchesterProjectFactory implements IOntologyProjectFactory {
    public static final String FACTORY_ID = "com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory"; //$NON-NLS-1$
    public static final String ONTOLOGY_LANGUAGE = "OWL2"; //$NON-NLS-1$

    public OWLManchesterProjectFactory() {
    }

    @Override
    public IOntologyProject createOntologyProject(IProject project) {
        OWLManchesterProject ontologyProject = new OWLManchesterProject(project.getName());
        NeOnCorePlugin.getDefault().updateMarkers(ontologyProject);
        return ontologyProject;
    }

    @Override
    public String getIdentifier() {
        return FACTORY_ID;
    }

}
