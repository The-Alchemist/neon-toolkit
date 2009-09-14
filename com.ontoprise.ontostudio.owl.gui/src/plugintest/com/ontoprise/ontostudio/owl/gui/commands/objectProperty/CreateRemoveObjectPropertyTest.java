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
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetRootObjectProperties;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSubObjectProperties;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.MoveObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.RemoveObjectProperty;

/**
 * @author werner
 * 
 */
public class CreateRemoveObjectPropertyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateRemoveObjectPropertyTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testSimpleCreateMoveObjectProperty() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$
        String op3 = createQualifiedIdentifier("op3", DEFAULT_NS); //$NON-NLS-1$

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();

        String[] rootObjectProperties = new GetRootObjectProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(2, rootObjectProperties.length);
        assertUnsortedArrayEquals(new String[] {op1, op2}, rootObjectProperties);

        new MoveObjectProperty(PROJECT_ID, ONTOLOGY_URI, op2, null, op1).run();

        rootObjectProperties = new GetRootObjectProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootObjectProperties.length);
        Assert.assertEquals(rootObjectProperties[0], op1);
        
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op3, op1).run();
        
        String[] subObjectProperties = new GetSubObjectProperties(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        Assert.assertEquals(2, subObjectProperties.length);
        assertUnsortedArrayEquals(new String[] {op2, op3}, subObjectProperties);
    }
    
    @Test
    public void testSimpleCreateRemoveObjectProperty() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$
        String op3 = createQualifiedIdentifier("op3", DEFAULT_NS); //$NON-NLS-1$

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op3, op2).run();
        
        new RemoveObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        String[] rootObjectProperties = new GetRootObjectProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootObjectProperties.length);
        
        new RemoveObjectProperty(PROJECT_ID, ONTOLOGY_URI, op3, op2).run();
        readAndDispatch();
        
        rootObjectProperties = new GetRootObjectProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootObjectProperties.length);
        
        String[] subObjectProperties = new GetSubObjectProperties(PROJECT_ID, ONTOLOGY_URI, op2).getResults();
        Assert.assertEquals(0, subObjectProperties.length);
        
    }

}
