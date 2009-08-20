/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 30.03.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.objectProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.IndividualProposalProvider;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.PropertyProposalProvider;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;

/**
 * @author werner
 *
 */
@SuppressWarnings("nls")
public class AutoCompletionTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public AutoCompletionTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testOPAutocompleteWithImported() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("op2", DEFAULT_NS); //$NON-NLS-1$

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectProperty(PROJECT_ID, IMPORTED_ONTOLOGY_URI, op2, null).run();

        PropertyProposalProvider proposalProvider = new PropertyProposalProvider(OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID), PropertyProposalProvider.OBJECT_PROPERTY_STYLE);
        IContentProposal[] props = proposalProvider.getProposals("op", 0);
        Assert.assertEquals(2, props.length);
    }

    @Test
    public void testDPAutocompleteWithImported() throws Exception {
        String op1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("dp2", DEFAULT_NS); //$NON-NLS-1$

        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateDataProperty(PROJECT_ID, IMPORTED_ONTOLOGY_URI, op2, null).run();

        PropertyProposalProvider proposalProvider = new PropertyProposalProvider(OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID), PropertyProposalProvider.DATA_PROPERTY_STYLE);
        IContentProposal[] props = proposalProvider.getProposals("dp", 0);
        Assert.assertEquals(2, props.length);
    }

    @Test
    public void testAPAutocompleteWithImported() throws Exception {
        String op1 = createQualifiedIdentifier("ap1", DEFAULT_NS); //$NON-NLS-1$
        String op2 = createQualifiedIdentifier("ap2", DEFAULT_NS); //$NON-NLS-1$

        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateAnnotationProperty(PROJECT_ID, IMPORTED_ONTOLOGY_URI, op2, null).run();

        PropertyProposalProvider proposalProvider = new PropertyProposalProvider(OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID), PropertyProposalProvider.ANNOTATION_PROPERTY_STYLE);
        IContentProposal[] props = proposalProvider.getProposals("ap", 0);
        Assert.assertEquals(2, props.length);
    }

    @Test
    public void testIndividualAutocompleteWithImported() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$

        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c1, i2).run();

        IndividualProposalProvider proposalProvider = new IndividualProposalProvider(OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID));
        IContentProposal[] props = proposalProvider.getProposals("i", 0);
        Assert.assertEquals(2, props.length);
    }
}
