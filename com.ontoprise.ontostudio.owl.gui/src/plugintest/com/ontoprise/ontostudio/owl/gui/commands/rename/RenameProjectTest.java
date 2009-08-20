/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 13.03.2009
 * Created by: krekeler
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.rename;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.Test;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.project.CreateProject;
import org.neontoolkit.core.command.project.RemoveProject;
import org.neontoolkit.core.command.project.RenameProject;
import org.neontoolkit.core.project.IOntologyProject;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/**
 * @author krekeler
 *
 */
public class RenameProjectTest extends AbstractOWLPluginTest {

    public RenameProjectTest(Properties properties) {
        super(properties);
    }
    
    /**
     * Related to issue 11229.
     */
    @Test
    public void test() throws Exception {
        String oldProjectId = "ProjectToRename"; //$NON-NLS-1$
        String newProjectId = "RenamedProject"; //$NON-NLS-1$
        String ontologyURI = "http://test.org/ontology#"; //$NON-NLS-1$
        assertFalse(ResourcesPlugin.getWorkspace().getRoot().getProject(oldProjectId).exists());
        String currentProjectId = null;
        new CreateProject(oldProjectId, OWLManchesterProjectFactory.FACTORY_ID, getProperties()).run();
        try {
            currentProjectId = oldProjectId;
            
//            new CreateOntology(oldProjectId, ontologyURI, "").run(); //$NON-NLS-1$
            new CreateOntology(oldProjectId, ontologyURI, ontologyURI, "").run(); //$NON-NLS-1$
            OWLModel model = OWLModelFactory.getOWLModel(ontologyURI, oldProjectId);
            assertNotNull(model);
            
            IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(oldProjectId);
            new RenameProject(oldProjectId, newProjectId).run();
            // moving the project is done by a background thread as long operation... 
            // we need to wait until the background thread dispatches an event to the GUI thread 
            long timeout = System.currentTimeMillis() + 5 * 1000;
            while (!newProjectId.equals(model.getProjectId()) && System.currentTimeMillis() < timeout) {
                Thread.sleep(100);
                ontologyProject.readAndDispatchWhileWaitingForEvents();
            }
            
            currentProjectId = newProjectId;
            assertEquals(newProjectId, model.getProjectId());
            assertEquals(model, OWLModelFactory.getOWLModel(ontologyURI, newProjectId));
        } finally {
            if (currentProjectId != null) {
                new RemoveProject(currentProjectId, true).run();
            }
        }
    }
}
