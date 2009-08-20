/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * Created on: 03.04.2009
 * Created by: werner
 *****************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.ontologies;

import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Test;
import org.neontoolkit.core.command.project.CreateProject;
import org.neontoolkit.core.project.TestOntologyProject;
import org.neontoolkit.gui.navigator.TestTreeViewerListener;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlFolderTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/**
 * @author werner
 *
 */
@SuppressWarnings("nls")
public class FolderSortingUITest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public FolderSortingUITest(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("null")
    @Test
    public void testSortingOfFolders() throws Exception {
        TestTreeViewerListener listener = new TestTreeViewerListener();
        listener.attachToNavigator();

        String projectId = "LocalTestProject" + System.currentTimeMillis();
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        new CreateProject(projectId, TestOntologyProject.LANGUAGE_ID, getProperties()).run();
        new CreateOntology(projectId, ONTOLOGY_URI, DEFAULT_NS, "").run(); //$NON-NLS-1$
//        new CreateOntology(projectId, ONTOLOGY_URI, DEFAULT_NS, "").run(); //$NON-NLS-1$
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        listener.getTreeViewer().refresh();
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        TreeItem[] elements = listener.getTreeViewer().getTree().getItems();
        TreeItem projectItem = null;
        for (TreeItem item: elements) {
            if (item.getData() instanceof OWLProjectTreeElement && ((OWLProjectTreeElement) item.getData()).getProjectName().equals(projectId)) {
                projectItem = item;
            }
        }
        Assert.assertNotNull(projectItem);
        TreeItem[] ontologyItems = (projectItem).getItems();
        TreeItem ontologyItem = null;
        for (TreeItem item: ontologyItems) {
            if (item.getData() instanceof OntologyTreeElement && ((OntologyTreeElement) item.getData()).getOntologyUri().equals(ONTOLOGY_URI)) {
                ontologyItem = item;
            }
 
        }
        Assert.assertNotNull(ontologyItem);
        TreeItem[] folderItems = (ontologyItem).getItems();
        
        for (TreeItem item: folderItems) {
            if (!(item.getData() instanceof AbstractOwlFolderTreeElement)) {
                Assert.fail();
            }
        }
        Assert.assertEquals(5, folderItems.length);
        Assert.assertEquals("Classes", ((AbstractOwlFolderTreeElement)folderItems[0].getData()).getId());
        Assert.assertEquals("Object Properties", ((AbstractOwlFolderTreeElement)folderItems[1].getData()).getId());
        Assert.assertEquals("Data Properties", ((AbstractOwlFolderTreeElement)folderItems[2].getData()).getId());
        Assert.assertEquals("Annotation Properties", ((AbstractOwlFolderTreeElement)folderItems[3].getData()).getId());
        Assert.assertEquals("Datatypes", ((AbstractOwlFolderTreeElement)folderItems[4].getData()).getId());
    }
    
}
