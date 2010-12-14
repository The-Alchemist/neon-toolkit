/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.objectProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.IsObjectProperty;

/**
 * @author werner
 *
 */
public class IsObjectPropertyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public IsObjectPropertyTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testWithValidObjectProperty() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        Assert.assertTrue(new IsObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1).isObjectProperty());
    }
    
    @Test
    public void testWithoutObjectProperty() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        Assert.assertFalse(new IsObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1).isObjectProperty());
    }
    
    
    @Test
    public void testWithImported() throws Exception {
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        new CreateObjectProperty(PROJECT_ID, IMPORTED_ONTOLOGY_URI, dp1, null).run();
        Assert.assertTrue(new IsObjectProperty(PROJECT_ID, ONTOLOGY_URI, dp1).isObjectProperty());
    }
}
