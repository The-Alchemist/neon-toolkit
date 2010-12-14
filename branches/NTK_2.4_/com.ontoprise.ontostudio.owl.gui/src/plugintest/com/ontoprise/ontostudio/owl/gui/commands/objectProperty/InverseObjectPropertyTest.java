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
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateInverseObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetInverseObjectPropertyHits;

/**
 * @author werner
 *
 */
public class InverseObjectPropertyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public InverseObjectPropertyTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testInverseObjectProperties() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();
        readAndDispatch();

        String[][] results1 = new GetInverseObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        String[][] results2 = new GetInverseObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op2).getResults();

        Assert.assertEquals(0, results1.length);
        Assert.assertEquals(0, results2.length);
        
        new CreateInverseObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, op2).run();
        readAndDispatch();
        
        results1 = new GetInverseObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        results2 = new GetInverseObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op2).getResults();
        
        Assert.assertEquals(1, results1.length);
        Assert.assertEquals(1, results2.length);
    }
}
