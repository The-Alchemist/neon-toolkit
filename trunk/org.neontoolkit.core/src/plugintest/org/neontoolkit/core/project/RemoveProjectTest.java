/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.project;

import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neontoolkit.core.NeOnCorePlugin;


/**
 * @author Dirk Wenke
 *
 */
public class RemoveProjectTest {
    private static final String PROJECT_ID = "TestProject"; //$NON-NLS-1$
    private static final String PROJECT_ID_RENAMED = "TestProject_Renamed"; //$NON-NLS-1$
    
    private static class TestOntologyProjectListener extends OntologyProjectAdapter {
        private boolean _waitForEvent = false;
        String _projectName = null;
        String _newProjectName;
        
        public void waitForEvent(boolean value) {
            _waitForEvent = value;
            if (value) {
                _projectName = null;
                _newProjectName = null;
            }
        }
        
        @Override
        public void projectRemoved(String projectName) {
            if (_waitForEvent) {
                _projectName = projectName;
            }
        }
        
        @Override
        public void projectRenamed(String oldProjectName, String newProjectName) {
            if (_waitForEvent) {
                _projectName = oldProjectName;
                _newProjectName = newProjectName;
            }
        }
    }
    
    private static TestOntologyProjectListener _listener = new TestOntologyProjectListener();
    
    @BeforeClass
    public static void startTests() {
        NeOnCorePlugin.getDefault().addOntologyProjectFactory(TestOntologyProject.LANGUAGE_ID, new TestOntologyProjectFactory());
        NeOnCorePlugin.getDefault().addOntologyProjectListener(_listener);
    }

    @AfterClass
    public static void endTests() {
        NeOnCorePlugin.getDefault().removeOntologyProjectFactory(TestOntologyProject.LANGUAGE_ID);
        NeOnCorePlugin.getDefault().removeOntologyProjectListener(_listener);
    }

    
    @Test
    public void testDisposeOntologyProject() throws Exception {
        OntologyProjectManager.getDefault().createOntologyProject(PROJECT_ID, TestOntologyProject.LANGUAGE_ID, new Properties());
        IOntologyProject project = OntologyProjectManager.getDefault().getOntologyProject(PROJECT_ID);
        _listener.waitForEvent(true);
        project.dispose(false);
        _listener.waitForEvent(false);
        Assert.assertEquals(PROJECT_ID, _listener._projectName);
        Assert.assertFalse(OntologyProjectManager.getDefault().existsOntologyProject(PROJECT_ID));
    }
    
    @Test
    public void testRemoveProjectResource() throws Exception {
        OntologyProjectManager.getDefault().createOntologyProject(PROJECT_ID, TestOntologyProject.LANGUAGE_ID, new Properties());
        IOntologyProject project = OntologyProjectManager.getDefault().getOntologyProject(PROJECT_ID);
        project.getResource().delete(true, true, new NullProgressMonitor());
        Assert.assertFalse(OntologyProjectManager.getDefault().existsOntologyProject(PROJECT_ID));
    }
    
    @Test
    public void testRenameOntologyProject() throws Exception {
        OntologyProjectManager.getDefault().createOntologyProject(PROJECT_ID, TestOntologyProject.LANGUAGE_ID, new Properties());
        IOntologyProject project = OntologyProjectManager.getDefault().getOntologyProject(PROJECT_ID);
        _listener.waitForEvent(true);
        project.renameProject(PROJECT_ID_RENAMED);
        _listener.waitForEvent(false);
        Assert.assertEquals(PROJECT_ID, _listener._projectName);
        Assert.assertEquals(PROJECT_ID_RENAMED, _listener._newProjectName);
        Assert.assertFalse(OntologyProjectManager.getDefault().existsOntologyProject(PROJECT_ID));
        Assert.assertTrue(OntologyProjectManager.getDefault().existsOntologyProject(PROJECT_ID_RENAMED));
        project.dispose(true);
    }

    @Test
    public void testRenameProjectResource() throws Exception {
        OntologyProjectManager.getDefault().createOntologyProject(PROJECT_ID, TestOntologyProject.LANGUAGE_ID, new Properties());
        IOntologyProject ontoProject = OntologyProjectManager.getDefault().getOntologyProject(PROJECT_ID);
        IProject project = ontoProject.getResource();
        IProjectDescription description = project.getDescription();
        description.setName(PROJECT_ID_RENAMED);
        project.move(description, true, new NullProgressMonitor());
        Assert.assertFalse(OntologyProjectManager.getDefault().existsOntologyProject(PROJECT_ID));
        Assert.assertTrue(OntologyProjectManager.getDefault().existsOntologyProject(PROJECT_ID_RENAMED));
        ontoProject.dispose(true);
    }
}
