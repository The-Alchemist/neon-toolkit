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
package com.ontoprise.ontostudio.owl.gui.commands.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neontoolkit.core.ParameterizedConfiguration;
import org.neontoolkit.core.command.project.CreateProject;
import org.neontoolkit.core.command.project.RemoveProject;
import org.neontoolkit.core.command.project.RenameProject;
import org.neontoolkit.core.project.TestOntologyProject;
import org.neontoolkit.gui.navigator.TestTreeViewerListener;
import org.neontoolkit.gui.navigator.TestTreeViewerListener.ChildAddedEvent;
import org.neontoolkit.gui.navigator.TestTreeViewerListener.ChildRemovedEvent;
import org.neontoolkit.gui.navigator.TestTreeViewerListener.TreeViewerEvent;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/**
 * @author krekeler
 *
 */
@RunWith(ParameterizedConfiguration.class)
public class RenameProjectUITest extends AbstractOWLPluginTest {

    public RenameProjectUITest(Properties properties) {
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
        
        TestTreeViewerListener listener = new TestTreeViewerListener();
        listener.attachToNavigator();
        
        new CreateProject(oldProjectId, TestOntologyProject.LANGUAGE_ID, getProperties()).run();
        try {
            currentProjectId = oldProjectId;
//            new CreateOntology(oldProjectId, ontologyURI, "").run(); //$NON-NLS-1$
            new CreateOntology(oldProjectId, ontologyURI, ontologyURI, "").run(); //$NON-NLS-1$
            OWLModel model = OWLModelFactory.getOWLModel(ontologyURI, oldProjectId);
            assertNotNull(model);
            
            new RenameProject(oldProjectId, newProjectId).run();
            currentProjectId = newProjectId;
            assertEquals(newProjectId, model.getProjectId());
            assertEquals(model, OWLModelFactory.getOWLModel(ontologyURI, newProjectId));
            
            List<TreeViewerEvent> events = listener.getEventList();
            
            Assert.assertTrue(events.size() == 4);            
            try {
                ChildAddedEvent cae = (ChildAddedEvent) events.get(0);
                OWLProjectTreeElement pte = (OWLProjectTreeElement) cae.child;
                Assert.assertEquals(pte.getProjectName(), oldProjectId);
                
                cae = (ChildAddedEvent) events.get(1);
                OntologyTreeElement ote = (OntologyTreeElement) cae.child;
                Assert.assertEquals(ote.getOntologyUri(), ontologyURI);                
                
                ChildRemovedEvent cre = (ChildRemovedEvent) events.get(2);
                pte = (OWLProjectTreeElement) cre.child;
                Assert.assertEquals(pte.getProjectName(), oldProjectId);
                
                cae = (ChildAddedEvent) events.get(3);
                pte = (OWLProjectTreeElement) cae.child;
                Assert.assertEquals(pte.getProjectName(), newProjectId);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } finally {
            if (currentProjectId != null) {
                new RemoveProject(currentProjectId, true).run();
            }
            listener.detachFromNavigator();
        }
    }
}
