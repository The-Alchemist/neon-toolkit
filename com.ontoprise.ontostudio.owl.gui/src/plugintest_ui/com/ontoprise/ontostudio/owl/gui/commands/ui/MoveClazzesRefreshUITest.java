/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * Created on: 03.04.2009
 * Created by: werner
 *****************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.ui;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.TreeViewer;
import org.junit.Assert;
import org.junit.Test;
import org.neontoolkit.core.command.project.CreateProject;
import org.neontoolkit.core.project.TestOntologyProject;
import org.neontoolkit.gui.navigator.TestTreeViewerListener;
import org.neontoolkit.gui.navigator.TestTreeViewerListener.TreeViewerEvent;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/**
 * @author werner
 *
 */
@SuppressWarnings("nls")
public class MoveClazzesRefreshUITest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public MoveClazzesRefreshUITest(Properties properties) {
        super(properties);
    }

    @Test
    public void testMoveSubClazzToRootClazzWithChildren() throws Exception {
        String clazz1 = createQualifiedIdentifier("c1", DEFAULT_NS);
        
        TestTreeViewerListener listener = new TestTreeViewerListener();
        listener.attachToNavigator();

        String projectId = "LocalTestProject";
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        new CreateProject(projectId, TestOntologyProject.LANGUAGE_ID, getProperties()).run();
        new CreateOntology(projectId, ONTOLOGY_URI, DEFAULT_NS, "").run();
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        new CreateRootClazz(projectId, ONTOLOGY_URI, clazz1).run();
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        List<TreeViewerEvent> eventlist = listener.getEventList();
        Assert.assertEquals(2, eventlist.size());

//        eventlist.clear();
//        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
//        new CreateSubClazz(projectId, ONTOLOGY_URI, CLAZZ2, CLAZZ1).run();
//        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
//        eventlist = listener.getEventList();
//        Assert.assertEquals(1, eventlist.size());
//        
//        eventlist.clear();
//        new CreateSubClazz(projectId, ONTOLOGY_URI, CLAZZ3, CLAZZ2).run();
//        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
//        eventlist = listener.getEventList();
//        Assert.assertEquals(1, eventlist.size());
//        
//        eventlist.clear();
//        new MoveClazz(projectId, ONTOLOGY_URI, CLAZZ2, CLAZZ1, null, false).run();
//        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
//        eventlist = listener.getEventList();
//        Assert.assertEquals(2, eventlist.size());
//        
//        TreeViewerEvent e0 = eventlist.get(0);
//        TreeViewerEvent e1 = eventlist.get(1);
//        Assert.assertTrue(e0 instanceof ChildRemovedEvent);
//        Assert.assertTrue(e1 instanceof ChildAddedEvent);
        
    }
    
}
