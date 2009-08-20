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
package com.ontoprise.ontostudio.owl.gui.commands.individual;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateObjectPropertyMember;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetObjectPropertyMemberHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;

/**
 * @author werner
 *
 */
public class ObjectPropertyMemberHitsTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public ObjectPropertyMemberHitsTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testCreateRemoveObjectPropertyMembers() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c2, i2).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        
        String[][] results = new GetObjectPropertyMemberHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(0, results.length);
        
        new CreateObjectPropertyMember(PROJECT_ID, ONTOLOGY_URI, i1, op1, i2).run();
        
        results = new GetObjectPropertyMemberHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(1, results.length);
        String expectedAxiom = new StringBuilder("[objectMember ").append(op1).append(" ").append(i1).append(" ").append(i2).append("]").toString();  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String[] expected = new String[]{expectedAxiom, ONTOLOGY_URI};
        assertUnsortedArrayEquals(expected, results[0]);
    }
}
