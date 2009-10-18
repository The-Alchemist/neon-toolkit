/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.imports;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.junit.Assume;
import org.junit.Test;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.project.IOntologyProject;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProject;

/**
 * @author krekeler
 * Created on: 28.09.2009
 */
public class ImportTest extends AbstractOWLPluginTest {

    public ImportTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testOntologyWithNonExistingImport() throws Exception {
        IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(PROJECT_ID);
        Assume.assumeTrue(project instanceof OWLManchesterProject);
        String[] ontologies = project.getOntologies();
        Set<String> availableOntologies = project.getAvailableOntologyURIs();
        try {
            ((OWLManchesterProject)project).importOntologies(new URI[] {getFile("resources/testfiles/OWL/ontologyWithNonExistingImport.owl").toURI()}, false); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue(true);
        }
        assertArrayEquals(ontologies, project.getOntologies());
        assertEquals(availableOntologies, project.getAvailableOntologyURIs());
    }
}
