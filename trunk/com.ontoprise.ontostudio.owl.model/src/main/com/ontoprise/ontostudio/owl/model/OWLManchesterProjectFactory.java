/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.neontoolkit.core.IOntologyProjectFactory;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.project.IOntologyProject;
import org.semanticweb.owlapi.model.OWLOntologyFactory;

public class OWLManchesterProjectFactory implements IOntologyProjectFactory {
    public static final String FACTORY_ID = "com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory"; //$NON-NLS-1$
    public static final String ONTOLOGY_LANGUAGE = "OWL2"; //$NON-NLS-1$
    
    private List<OWLOntologyFactory> thirdPartyFactories = new ArrayList<OWLOntologyFactory>();

    public OWLManchesterProjectFactory() {
    }

    @Override
    public IOntologyProject createOntologyProject(IProject project) {
        OWLManchesterProject ontologyProject = new OWLManchesterProject(project.getName());
        ontologyProject.setThirdPartyFactories(thirdPartyFactories);
        NeOnCorePlugin.getDefault().updateMarkers(ontologyProject);
        return ontologyProject;
    }



    @Override
    public String getIdentifier() {
        return FACTORY_ID;
    }
    
    public void addOntologyFactory(OWLOntologyFactory factory)
    {
        if (!thirdPartyFactories.contains(factory))
            thirdPartyFactories.add(factory);
        
    }


}
