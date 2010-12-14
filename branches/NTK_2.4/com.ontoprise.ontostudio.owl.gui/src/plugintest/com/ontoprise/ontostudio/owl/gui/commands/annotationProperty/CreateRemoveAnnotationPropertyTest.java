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
import org.neontoolkit.core.command.CommandException;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetRootAnnotationProperties;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetSubAnnotationPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.MoveAnnotationProperty;

/**
 * @author werner
 * 
 */
public class CreateRemoveAnnotationPropertyTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateRemoveAnnotationPropertyTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testSimpleCreateAnnotationProperty() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$

        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();
        readAndDispatch();

        String[] rootProperties = new GetRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(2, rootProperties.length);
        assertUnsortedArrayEquals(new String[] {op1, op2}, rootProperties);
    }

    @Test
    public void testSimpleCreateMoveAnnotationProperty() throws Exception {
        Assume.assumeTrue(OWLManchesterProjectFactory.FACTORY_ID.equals(getProperties().get(ParameterizedConfiguration.PROJECT_FACTORY_ID_KEY)));
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$
        String op3 = createQualifiedIdentifier("op3", DEFAULT_NS); //$NON-NLS-1$

        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op2, null).run();
        readAndDispatch();

        String[] rootProperties = new GetRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(2, rootProperties.length);
        assertUnsortedArrayEquals(new String[] {op1, op2}, rootProperties);

        try {
            new MoveAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op2, null, op1).run();
        } catch (CommandException e) {
            // expecting exception here
            Assert.assertTrue(true);
        }

        rootProperties = new GetRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootProperties.length);
        assertUnsortedArrayEquals(new String[] {op1}, rootProperties);

        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op3, op1).run();
        rootProperties = new GetRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootProperties.length);
        assertUnsortedArrayEquals(new String[] {op1}, rootProperties);
        

        String[][] subOPHits = new GetSubAnnotationPropertyHits(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        Assert.assertEquals(2, subOPHits.length);
    }
}
