/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.annotationProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Assume;
import org.junit.Test;
import org.neontoolkit.core.ParameterizedConfiguration;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetSubAnnotationPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetSuperAnnotationPropertyHits;

/**
 * @author werner
 *
 */
public class AnnotationPropertyTaxonomyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public AnnotationPropertyTaxonomyTest(Properties properties) {
        super(properties);
    }
    
    @Test
    public void testCreateEditAnnotationPropertyTaxonomy() throws Exception {
        Assume.assumeTrue(OWLManchesterProjectFactory.FACTORY_ID.equals(getProperties().get(ParameterizedConfiguration.PROJECT_FACTORY_ID_KEY)));
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$
        String op3 = createQualifiedIdentifier("op3", DEFAULT_NS); //$NON-NLS-1$

        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();

        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op3, op1).run();
        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op3, op2).run();
        
        String[][] subOPHits = new GetSubAnnotationPropertyHits(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        Assert.assertEquals(1, subOPHits.length);
        
        String expectedAxiomText = new StringBuilder("[subAnnotationPropertyOf ").append(op3).append(" ").append(op1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] expected = new String[]{expectedAxiomText, ONTOLOGY_URI};
        assertUnsortedArrayEquals(expected, subOPHits[0]);
        
        String[][] superOPHits = new GetSuperAnnotationPropertyHits(PROJECT_ID, ONTOLOGY_URI, op3).getResults();
        Assert.assertEquals(2, superOPHits.length);
        
        String expectedAxiomText1 = new StringBuilder("[subAnnotationPropertyOf ").append(op3).append(" ").append(op1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String expectedAxiomText2 = new StringBuilder("[subAnnotationPropertyOf ").append(op3).append(" ").append(op2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[][] expected2 = new String[][]{{expectedAxiomText1, ONTOLOGY_URI}, {expectedAxiomText2, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected2, superOPHits);
    }

}
