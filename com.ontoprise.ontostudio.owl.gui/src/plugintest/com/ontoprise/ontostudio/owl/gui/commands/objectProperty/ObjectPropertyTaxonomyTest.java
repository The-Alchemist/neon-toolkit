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
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateEquivalentObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetEquivalentObjectPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSubObjectPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSuperObjectPropertyHits;

/**
 * @author werner
 *
 */
public class ObjectPropertyTaxonomyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public ObjectPropertyTaxonomyTest(Properties properties) {
        super(properties);
    }
    
    @Test
    public void testCreateEditObjectPropertyTaxonomy() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$
        String op3 = createQualifiedIdentifier("op3", DEFAULT_NS); //$NON-NLS-1$

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op3, op1).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op3, op2).run();
        
        String[][] subOPHits = new GetSubObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        Assert.assertEquals(1, subOPHits.length);
        
        String expectedAxiomText = new StringBuilder("[subObjectPropertyOf ").append(op3).append(" ").append(op1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] expected = new String[]{expectedAxiomText, ONTOLOGY_URI};
        assertUnsortedArrayEquals(expected, subOPHits[0]);
        
        String[][] superOPHits = new GetSuperObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op3).getResults();
        Assert.assertEquals(2, superOPHits.length);
        
        String expectedAxiomText1 = new StringBuilder("[subObjectPropertyOf ").append(op3).append(" ").append(op1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String expectedAxiomText2 = new StringBuilder("[subObjectPropertyOf ").append(op3).append(" ").append(op2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[][] expected2 = new String[][]{{expectedAxiomText1, ONTOLOGY_URI}, {expectedAxiomText2, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected2, superOPHits);
    }
    
    @Test 
    public void testCreateEquivalentObjectProperties() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();
        
        new CreateEquivalentObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, op2).run();
        
        String[][] equivalentOPHits1 = new GetEquivalentObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        String[][] equivalentOPHits2 = new GetEquivalentObjectPropertyHits(PROJECT_ID, ONTOLOGY_URI, op2).getResults();
        
        Assert.assertEquals(1, equivalentOPHits1.length);
        Assert.assertEquals(1, equivalentOPHits2.length);
        
        String expected = new StringBuilder("[objectEquivalent ").append(op1).append(" ").append(op2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Assert.assertEquals(equivalentOPHits1[0][0], expected);
        Assert.assertEquals(equivalentOPHits2[0][0], expected);
    }

}
