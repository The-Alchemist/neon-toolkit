/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 17.03.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.dataProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.IsDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;

/**
 * @author werner
 *
 */
public class IsDataPropertyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public IsDataPropertyTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testWithValidDataProperty() throws Exception {
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        Assert.assertTrue(new IsDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1).isDataProperty());
    }
    
    @Test
    public void testWithoutDataProperty() throws Exception {
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        Assert.assertFalse(new IsDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1).isDataProperty());
    }
    
    @Test
    public void testWithImported() throws Exception {
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        new CreateDataProperty(PROJECT_ID, IMPORTED_ONTOLOGY_URI, dp1, null).run();
        Assert.assertTrue(new IsDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1).isDataProperty());
    }
    
}
