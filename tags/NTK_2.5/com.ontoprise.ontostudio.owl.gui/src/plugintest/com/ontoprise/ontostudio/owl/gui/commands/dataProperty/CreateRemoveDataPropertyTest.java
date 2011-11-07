/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.dataProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetRootDataProperties;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetSubDataProperties;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.MoveDataProperty;

/**
 * @author werner
 * 
 */
public class CreateRemoveDataPropertyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateRemoveDataPropertyTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testSimpleCreateMoveDataProperty() throws Exception {
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        String dp2 = createQualifiedIdentifier("dp2", DEFAULT_NS); //$NON-NLS-1$
        String dp3 = createQualifiedIdentifier("dp3", DEFAULT_NS); //$NON-NLS-1$

        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp2, null).run();

        String[] rootProperties = new GetRootDataProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(2, rootProperties.length);
        assertUnsortedArrayEquals(new String[] {dp1, dp2}, rootProperties);

        new MoveDataProperty(PROJECT_ID, ONTOLOGY_URI, dp2, null, dp1).run();

        rootProperties = new GetRootDataProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootProperties.length);
        Assert.assertEquals(rootProperties[0], dp1);
        
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp3, dp1).run();
        
        String[] subProperties = new GetSubDataProperties(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        Assert.assertEquals(2, subProperties.length);
        assertUnsortedArrayEquals(new String[] {dp2, dp3}, subProperties);
    }
    
}
