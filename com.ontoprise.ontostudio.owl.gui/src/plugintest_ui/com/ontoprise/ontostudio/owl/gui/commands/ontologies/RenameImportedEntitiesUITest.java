/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 05.05.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.ontologies;

import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Test;
import org.neontoolkit.gui.navigator.TestTreeViewerListener;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlFolderTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.rename.RenameOWLClazz;

/**
 * @author werner
 *
 */
@SuppressWarnings("nls")
public class RenameImportedEntitiesUITest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public RenameImportedEntitiesUITest(Properties properties) {
        super(properties);
    }

    @Test
    public void testRenameClazzInImportingOntology() throws Exception {
        OWLModelPlugin.getDefault().getPreferenceStore().setValue(OWLModelPlugin.SHOW_IMPORTED, true);
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS);
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS);
        String c3 = createQualifiedIdentifier("c3", DEFAULT_NS);
        
        TestTreeViewerListener listener = new TestTreeViewerListener();
        listener.attachToNavigator();

        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        new CreateRootClazz(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c1).run();
        doTestC1(c1, listener);
        
        new RenameOWLClazz(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c1, c2).run();
        new RenameOWLClazz(PROJECT_ID, ONTOLOGY_URI, c1, c2).run();
        doTestC1(c2, listener);

        new RenameOWLClazz(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c2, c3).run();
        new RenameOWLClazz(PROJECT_ID, ONTOLOGY_URI, c2, c3).run();
        doTestC1(c3, listener); 
    }

    @SuppressWarnings("null")
    private void doTestC1(String targetConcept, TestTreeViewerListener listener) {
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        listener.getTreeViewer().refresh();
        listener.getTreeViewer().expandToLevel(TreeViewer.ALL_LEVELS);
        TreeItem[] elements = listener.getTreeViewer().getTree().getItems();
        TreeItem projectItem = null;
        for (TreeItem item: elements) {
            if (item.getData() instanceof OWLProjectTreeElement && ((OWLProjectTreeElement) item.getData()).getProjectName().equals(PROJECT_ID)) {
                projectItem = item;
            }
        }
        Assert.assertNotNull(projectItem);
        TreeItem[] ontologyItems = (projectItem).getItems();
        Assert.assertEquals(2, ontologyItems.length);
        TreeItem ontologyItem = null;
        TreeItem importedOntologyItem = null;
        for (TreeItem item: ontologyItems) {
            if (item.getData() instanceof OntologyTreeElement) { 
                if (((OntologyTreeElement) item.getData()).getOntologyUri().equals(ONTOLOGY_URI)) {
                    ontologyItem = item;
                } else if (((OntologyTreeElement) item.getData()).getOntologyUri().equals(IMPORTED_ONTOLOGY_URI)) {
                    importedOntologyItem = item;
                }
            } 
 
        }
        Assert.assertNotNull(ontologyItem);
        Assert.assertNotNull(importedOntologyItem);
        TreeItem[] folderItems = (ontologyItem).getItems();
        TreeItem[] importedFolderItems = (importedOntologyItem).getItems();
        
        for (TreeItem item: folderItems) {
            if (!(item.getData() instanceof AbstractOwlFolderTreeElement)) {
                Assert.fail();
            }
        }
        if (folderItems[0].getData() instanceof AbstractOwlFolderTreeElement) {
            TreeItem[] clazzItems = folderItems[0].getItems();
            Assert.assertEquals(1, clazzItems.length);
            Assert.assertTrue(clazzItems[0].getData() instanceof ClazzTreeElement);
            Assert.assertEquals(targetConcept, ((ClazzTreeElement)clazzItems[0].getData()).getId());
        }
        
        for (TreeItem item: importedFolderItems) {
            if (!(item.getData() instanceof AbstractOwlFolderTreeElement)) {
                Assert.fail();
            }
        }
        if (importedFolderItems[0].getData() instanceof AbstractOwlFolderTreeElement) {
            TreeItem[] clazzItems = importedFolderItems[0].getItems();
            Assert.assertEquals(1, clazzItems.length);
            Assert.assertTrue(clazzItems[0].getData() instanceof ClazzTreeElement);
            Assert.assertEquals(targetConcept, ((ClazzTreeElement)clazzItems[0].getData()).getId());
        }
    }

}
