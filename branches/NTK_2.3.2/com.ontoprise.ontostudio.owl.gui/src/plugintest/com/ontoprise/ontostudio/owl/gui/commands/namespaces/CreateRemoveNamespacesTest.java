/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.namespaces;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.ExistsPrefix;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetDefaultNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetNamespaceForPrefix;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetPrefixForNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetRegisteredPrefixes;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.RemoveNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.SetDefaultNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.SetNamespacePrefix;

/**
 * @author werner
 *
 */
public class CreateRemoveNamespacesTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateRemoveNamespacesTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testGetPrefixForNamespace() throws Exception {
        String prefix1 = "prefix1"; //$NON-NLS-1$
        String namespace = "http://test.org/ontology#"; //$NON-NLS-1$
        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
        new SetDefaultNamespace(PROJECT_ID, ONTOLOGY_URI, namespace).run();
        assertEquals(null, model.getNamespaces().getPrefixForNamespace(namespace));
        new SetNamespacePrefix(PROJECT_ID, ONTOLOGY_URI, prefix1, namespace).run();
        assertEquals(prefix1, model.getNamespaces().getPrefixForNamespace(namespace));
        new RemoveNamespace(PROJECT_ID, ONTOLOGY_URI, prefix1).run();
        assertEquals(null, model.getNamespaces().getPrefixForNamespace(namespace));
    }
    
    @Test
    public void testUnregisterNamespaceWithMultiplePrefixes() throws Exception {
        String prefix1 = "prefix1"; //$NON-NLS-1$
        String prefix2 = "prefix2"; //$NON-NLS-1$
        String namespace = "http://test.org/ontology#"; //$NON-NLS-1$
        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
        new SetNamespacePrefix(PROJECT_ID, ONTOLOGY_URI, prefix1, namespace).run();
        assertEquals(prefix1, model.getNamespaces().getPrefixForNamespace(namespace));
        new SetNamespacePrefix(PROJECT_ID, ONTOLOGY_URI, prefix2, namespace).run();
        new RemoveNamespace(PROJECT_ID, ONTOLOGY_URI, prefix1).run();
        assertEquals(prefix2, model.getNamespaces().getPrefixForNamespace(namespace));
    }
    
    @Test
    public void testDefaultNamespaces() throws Exception {
        String defaultNs = new GetDefaultNamespace(PROJECT_ID, ONTOLOGY_URI).getResult();
        Assert.assertEquals(DEFAULT_NS, defaultNs);
        
        String newDefaultNamespace = "http://www.TestDefaultNamespace.org#"; //$NON-NLS-1$
        new SetDefaultNamespace(PROJECT_ID, ONTOLOGY_URI, newDefaultNamespace).run();
        defaultNs = new GetDefaultNamespace(PROJECT_ID, ONTOLOGY_URI).getResult();
        Assert.assertEquals(newDefaultNamespace, defaultNs);
        
        new SetDefaultNamespace(PROJECT_ID, ONTOLOGY_URI, null).run();
        defaultNs = new GetDefaultNamespace(PROJECT_ID, ONTOLOGY_URI).getResult();
        Assert.assertNull(defaultNs);
        Assert.assertFalse(new LinkedHashSet<String>(Arrays.asList(new GetRegisteredPrefixes(PROJECT_ID, ONTOLOGY_URI).getResults())).contains(OWLNamespaces.DEFAULT_NAMESPACE_PREFIX));
    }

    @Test
    public void testPrefixes() throws Exception {
        String[] prefixes = new GetRegisteredPrefixes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(9, prefixes.length);
        
        String prefix = "prfx"; //$NON-NLS-1$
        String namespace = "http://test.prefix.org#"; //$NON-NLS-1$
        new SetNamespacePrefix(PROJECT_ID, ONTOLOGY_URI, prefix, namespace).run();
        readAndDispatch();
        
        prefixes = new GetRegisteredPrefixes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(9 + 1, prefixes.length);
        
        String ns = new GetNamespaceForPrefix(PROJECT_ID, ONTOLOGY_URI, prefix).getResult();
        Assert.assertEquals(namespace, ns);
        
        boolean exists = new ExistsPrefix(PROJECT_ID, ONTOLOGY_URI, prefix).getExists();
        Assert.assertTrue(exists);

        exists = new ExistsPrefix(PROJECT_ID, ONTOLOGY_URI, "nonExistantPrefix").getExists(); //$NON-NLS-1$
        Assert.assertFalse(exists);
        
        String prfx = new GetPrefixForNamespace(PROJECT_ID, ONTOLOGY_URI, namespace).getResult();
        Assert.assertEquals(prefix, prfx);
    }
}
