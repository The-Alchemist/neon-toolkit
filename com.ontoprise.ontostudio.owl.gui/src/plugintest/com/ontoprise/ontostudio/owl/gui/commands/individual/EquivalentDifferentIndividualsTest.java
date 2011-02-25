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

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateDifferentIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateSameIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.EditDifferentIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.EditEquivalentIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetDifferentIndividualHits;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetEquivalentIndividualHits;

/**
 * @author werner
 * 
 */
public class EquivalentDifferentIndividualsTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public EquivalentDifferentIndividualsTest(Properties properties) {
        super(properties);
    }

    @Test
    public void simpleTestCreateRemoveEquivalentIndividuals() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$
        String i3 = createQualifiedIdentifier("i3", DEFAULT_NS); //$NON-NLS-1$
        String i4 = createQualifiedIdentifier("i4", DEFAULT_NS); //$NON-NLS-1$

        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
        OWLOntology ontology = model.getOntology();

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i2).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i3).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i4).run();

        new CreateSameIndividuals(PROJECT_ID, ONTOLOGY_URI, new String[] {i1, i2}).run();
        String[][] equivalentIndividualHits = new GetEquivalentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        String expectedAxiomText = new StringBuilder("[same ").append(i1).append(" ").append(i2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[][] expected = new String[][] {{expectedAxiomText, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, equivalentIndividualHits);

        new EditEquivalentIndividuals(PROJECT_ID, ONTOLOGY_URI, expectedAxiomText, i2, i3).run();

        equivalentIndividualHits = new GetEquivalentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        OWLSameIndividualAxiom sameIndividual = (OWLSameIndividualAxiom) OWLUtilities.axiom(equivalentIndividualHits[0][0], ontology);
        Set<OWLIndividual> individuals = sameIndividual.getIndividuals();
        Set<OWLIndividual> expectedIndividuals = new HashSet<OWLIndividual>();
        expectedIndividuals.add(OWLModelFactory.getOWLDataFactory(PROJECT_ID).getOWLNamedIndividual(OWLUtilities.toIRI(i1)));
        expectedIndividuals.add(OWLModelFactory.getOWLDataFactory(PROJECT_ID).getOWLNamedIndividual(OWLUtilities.toIRI(i3)));

        assertUnsortedArrayEquals(expectedIndividuals.toArray(new OWLIndividual[2]), individuals.toArray(new OWLIndividual[individuals.size()]));

        String actualAxiomText = new StringBuilder("[same ").append(i1).append(" ").append(i3).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[0], new String[] {actualAxiomText}).run();

        equivalentIndividualHits = new GetEquivalentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(0, equivalentIndividualHits.length);
    }

    @Test
    public void simpleTestCreateRemoveDifferentIndividuals() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$
        String i3 = createQualifiedIdentifier("i3", DEFAULT_NS); //$NON-NLS-1$
        String i4 = createQualifiedIdentifier("i4", DEFAULT_NS); //$NON-NLS-1$

        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
        OWLOntology ontology = model.getOntology();

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i2).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i3).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i4).run();

        new CreateDifferentIndividuals(PROJECT_ID, ONTOLOGY_URI, new String[] {i1, i2}).run();
        String[][] equivalentIndividualHits = new GetDifferentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        String expectedAxiomText = new StringBuilder("[different ").append(i1).append(" ").append(i2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[][] expected = new String[][] {{expectedAxiomText, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, equivalentIndividualHits);

        new EditDifferentIndividuals(PROJECT_ID, ONTOLOGY_URI, expectedAxiomText, i2, i3).run();

        equivalentIndividualHits = new GetDifferentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        OWLDifferentIndividualsAxiom differentIndividuals = (OWLDifferentIndividualsAxiom) OWLUtilities.axiom(equivalentIndividualHits[0][0], ontology);
        Set<OWLIndividual> individuals = differentIndividuals.getIndividuals();
        Set<OWLIndividual> expectedIndividuals = new HashSet<OWLIndividual>();
        expectedIndividuals.add(OWLModelFactory.getOWLDataFactory(PROJECT_ID).getOWLNamedIndividual(OWLUtilities.toIRI(i1)));
        expectedIndividuals.add(OWLModelFactory.getOWLDataFactory(PROJECT_ID).getOWLNamedIndividual(OWLUtilities.toIRI(i3)));

        assertUnsortedArrayEquals(expectedIndividuals.toArray(new OWLIndividual[2]), individuals.toArray(new OWLIndividual[individuals.size()]));

        String actualAxiomText = new StringBuilder("[different ").append(i1).append(" ").append(i3).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[0], new String[] {actualAxiomText}).run();

        equivalentIndividualHits = new GetDifferentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(0, equivalentIndividualHits.length);
    }

    @Test
    public void testInverseAxioms() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i2).run();

        new CreateSameIndividuals(PROJECT_ID, ONTOLOGY_URI, new String[] {i1, i2}).run();
        String[][] equivalentIndividualHits = new GetEquivalentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();

        Assert.assertEquals(1, equivalentIndividualHits.length);

        String axiomText1 = new StringBuilder("[same ").append(i1).append(" ").append(i2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String axiomText2 = new StringBuilder("[same ").append(i2).append(" ").append(i1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[0], new String[] {axiomText1}).run();

        equivalentIndividualHits = new GetEquivalentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();

        Assert.assertEquals(0, equivalentIndividualHits.length);
        new CreateSameIndividuals(PROJECT_ID, ONTOLOGY_URI, new String[] {i1, i2}).run();
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[0], new String[] {axiomText2}).run();

        equivalentIndividualHits = new GetEquivalentIndividualHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();

        Assert.assertEquals(0, equivalentIndividualHits.length);
    }
}
