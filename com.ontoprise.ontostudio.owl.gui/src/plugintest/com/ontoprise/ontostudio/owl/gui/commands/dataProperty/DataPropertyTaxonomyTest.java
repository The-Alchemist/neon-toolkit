/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 29.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.dataProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateEquivalentDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetEquivalentDataPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetSubDataPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetSuperDataPropertyHits;

/**
 * @author werner
 *
 */
public class DataPropertyTaxonomyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public DataPropertyTaxonomyTest(Properties properties) {
        super(properties);
    }
    
    @Test
    public void testCreateEditDataPropertyTaxonomy() throws Exception {
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        String dp2 = createQualifiedIdentifier("dp2", DEFAULT_NS); //$NON-NLS-1$
        String dp3 = createQualifiedIdentifier("dp3", DEFAULT_NS); //$NON-NLS-1$

        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp2, null).run();

        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp3, dp1).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp3, dp2).run();
        
        String[][] subOPHits = new GetSubDataPropertyHits(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        Assert.assertEquals(1, subOPHits.length);
        
        String expectedAxiomText = new StringBuilder("[subDataPropertyOf ").append(dp3).append(" ").append(dp1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] expected = new String[]{expectedAxiomText, ONTOLOGY_URI};
        assertUnsortedArrayEquals(expected, subOPHits[0]);
        
        String[][] superOPHits = new GetSuperDataPropertyHits(PROJECT_ID, ONTOLOGY_URI, dp3).getResults();
        Assert.assertEquals(2, superOPHits.length);
        
        String expectedAxiomText1 = new StringBuilder("[subDataPropertyOf ").append(dp3).append(" ").append(dp1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String expectedAxiomText2 = new StringBuilder("[subDataPropertyOf ").append(dp3).append(" ").append(dp2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[][] expected2 = new String[][]{{expectedAxiomText1, ONTOLOGY_URI}, {expectedAxiomText2, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected2, superOPHits);
    }
    
    @Test 
    public void testCreateEquivalentDataProperties() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();
        
        new CreateEquivalentDataProperty(PROJECT_ID, ONTOLOGY_URI, op1, op2).run();
        
        String[][] equivalentOPHits1 = new GetEquivalentDataPropertyHits(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        String[][] equivalentOPHits2 = new GetEquivalentDataPropertyHits(PROJECT_ID, ONTOLOGY_URI, op2).getResults();
        
        Assert.assertEquals(1, equivalentOPHits1.length);
        Assert.assertEquals(1, equivalentOPHits2.length);
        
        String expected = new StringBuilder("[dataEquivalent ").append(op1).append(" ").append(op2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Assert.assertEquals(equivalentOPHits1[0][0], expected);
        Assert.assertEquals(equivalentOPHits2[0][0], expected);
    }

}
