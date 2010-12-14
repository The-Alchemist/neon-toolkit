/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateDataPropertyMember;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetDataPropertyMemberHits;

/**
 * @author werner
 * 
 */
public class DataPropertyMemberHitsTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public DataPropertyMemberHitsTest(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("nls")
    @Test
    public void testCreateRemoveDataPropertyMembers() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c2, i2).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();

        String[][] results = new GetDataPropertyMemberHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(0, results.length);

        String[] newValue = new String[] {"my value", "de"}; //$NON-NLS-1$//$NON-NLS-2$
        new CreateDataPropertyMember(PROJECT_ID, ONTOLOGY_URI, i1, dp1, OWLConstants.RDFS_LITERAL, newValue).run();

        results = new GetDataPropertyMemberHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(1, results.length);
        String expectedAxiom = new StringBuilder("[dataMember ").append(dp1).append(" ").append(i1).append(" ").append("\"my value\"@de").append("]").toString();
        String[] expected = new String[] {expectedAxiom, ONTOLOGY_URI};
        assertUnsortedArrayEquals(expected, results[0]);

        newValue = new String[] {"my value2", OWLCommandUtils.EMPTY_LANGUAGE}; //$NON-NLS-1$
        new CreateDataPropertyMember(PROJECT_ID, ONTOLOGY_URI, i1, dp1, OWLConstants.RDFS_LITERAL, newValue).run();

        results = new GetDataPropertyMemberHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(2, results.length);
        String expectedAxiom2 = new StringBuilder("[dataMember ").append(dp1).append(" ").append(i1).append(" ").append("\"my value2\"^^<http://www.w3.org/2001/XMLSchema#string>").append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        expected = new String[] {expectedAxiom, ONTOLOGY_URI};
        String[] expected2 = new String[] {expectedAxiom2, ONTOLOGY_URI};
        List<String[]> expectedAxioms = new ArrayList<String[]>();
        expectedAxioms.add(expected);
        expectedAxioms.add(expected2);
        assertUnsortedArrayEquals(expectedAxioms.toArray(new String[2][2]), results);
    }
}
