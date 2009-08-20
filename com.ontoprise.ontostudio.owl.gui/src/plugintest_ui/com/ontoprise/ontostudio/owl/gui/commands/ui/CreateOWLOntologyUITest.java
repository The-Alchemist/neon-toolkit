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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;
import org.neontoolkit.core.command.project.CreateProject;
import org.neontoolkit.core.command.project.RemoveProject;
import org.neontoolkit.core.project.TestOntologyProject;
import org.neontoolkit.gui.navigator.TestTreeViewerListener;
import org.neontoolkit.gui.navigator.TestTreeViewerListener.ChildAddedEvent;
import org.neontoolkit.gui.navigator.TestTreeViewerListener.ChildRemovedEvent;
import org.neontoolkit.gui.navigator.TestTreeViewerListener.TreeViewerEvent;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;
import com.ontoprise.ontostudio.owl.model.commands.ontology.RemoveOntology;

/**
 * @author mika
 *
 */
public class CreateOWLOntologyUITest extends AbstractOWLPluginTest {

    public CreateOWLOntologyUITest(Properties properties) {
        super(properties);
    }
    
    /**
     * Related to issue 11537.
     */
    @Test
    public void test() throws Exception {
        String projectId = "OwlProject"; //$NON-NLS-1$
        String ontologyURIOne = "http://test.org/ontology#one"; //$NON-NLS-1$
        String ontologyURITwo = "http://test.org/ontology#two"; //$NON-NLS-1$

        assertFalse(ResourcesPlugin.getWorkspace().getRoot().getProject(projectId).exists());
        
        TestTreeViewerListener listener = new TestTreeViewerListener();
        listener.attachToNavigator();
        
        new CreateProject(projectId, TestOntologyProject.LANGUAGE_ID, getProperties()).run();
        try {
            new CreateOntology(projectId, ontologyURIOne, ontologyURIOne, "").run(); //$NON-NLS-1$
//            new CreateOntology(projectId, ontologyURIOne, ontologyURIOne, "").run(); //$NON-NLS-1$
            OWLModel model = OWLModelFactory.getOWLModel(ontologyURIOne, projectId);
            assertNotNull(model);
            
            listener.getTreeViewer().expandToLevel(2);
            
            new CreateOntology(projectId, ontologyURITwo, ontologyURITwo, "").run(); //$NON-NLS-1$
//            new CreateOntology(projectId, ontologyURITwo, ontologyURITwo, "").run(); //$NON-NLS-1$
            model = OWLModelFactory.getOWLModel(ontologyURITwo, projectId);
            assertNotNull(model);

            new RemoveOntology(projectId, ontologyURIOne, true).run();
//            new RemoveOntology(projectId, ontologyURIOne, true).run();
            Assert.assertTrue(OWLModelFactory.getOWLModels(projectId).size() == 1);

            
            try {
                
                
                List<TreeViewerEvent> events = listener.getEventList();
                
                Assert.assertTrue(events.size() == 4);            

                ChildAddedEvent cae = (ChildAddedEvent) events.get(1);
                OntologyTreeElement ote = (OntologyTreeElement) cae.child;
                Assert.assertEquals(ote.getProjectName(), projectId);
                Assert.assertEquals(ote.getOntologyUri(), ontologyURIOne);
                OWLProjectTreeElement pte = (OWLProjectTreeElement) cae.parent;
                Assert.assertEquals(pte.getProjectName(), projectId);                
                
                cae = (ChildAddedEvent) events.get(2);
                ote = (OntologyTreeElement) cae.child;
                Assert.assertEquals(ote.getProjectName(), projectId);
                Assert.assertEquals(ote.getOntologyUri(), ontologyURITwo);
                pte = (OWLProjectTreeElement) cae.parent;
                Assert.assertEquals(pte.getProjectName(), projectId);
                                               
                Widget[] ws = listener.getTreeViewer().findTreeItems(ote);
                Assert.assertTrue(ws.length == 1);

                ChildRemovedEvent cre = (ChildRemovedEvent) events.get(3);
                ote = (OntologyTreeElement) cre.child;
                Assert.assertEquals(ote.getProjectName(), projectId);
                Assert.assertEquals(ote.getOntologyUri(), ontologyURIOne);
                pte = (OWLProjectTreeElement) cae.parent;
                Assert.assertEquals(pte.getProjectName(), projectId);

                ws = listener.getTreeViewer().findTreeItems(ote);
                Assert.assertTrue(ws.length == 0);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } finally {
            listener.getEventList().clear();
            if (projectId != null) {
                new RemoveProject(projectId, true).run();
                
                try {
                    List<TreeViewerEvent> events = listener.getEventList();
                    Assert.assertTrue(events.size() == 2);
                    ChildRemovedEvent cre = (ChildRemovedEvent) events.get(0);
                    OntologyTreeElement ote = (OntologyTreeElement) cre.child;
                    Assert.assertEquals(ontologyURITwo, ote.getOntologyUri());

                    cre = (ChildRemovedEvent) events.get(1);
                    OWLProjectTreeElement pte = (OWLProjectTreeElement) cre.child;
                    Assert.assertEquals(projectId, pte.getProjectName());
                } catch (Exception e) {
                    Assert.assertTrue(false);
                }
            }
            listener.detachFromNavigator();
        }
    }
}
