/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.annotations;

import static org.junit.Assert.assertArrayEquals;

import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
import com.ontoprise.ontostudio.owl.model.commands.annotations.AddEntityAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.AddOntologyAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.EditEntityAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetEntityAnnotationHits;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetOntologyAnnotations;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.rename.RenameOWLIndividual;

/**
 * @author werner
 *
 */
@SuppressWarnings("nls")
public class AnnotationsTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public AnnotationsTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testLabelAnnotation() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        readAndDispatch();

        String annotationProperty = "rdfs:label"; //$NON-NLS-1$
        String value = "some label"; //$NON-NLS-1$
        String range = "xsd:string"; //$NON-NLS-1$
        String lang = "en"; //$NON-NLS-1$
        String[] newValues = new String[] {annotationProperty, value, range, lang};
        new AddEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, c1, newValues).run();
        
        OWLClass entity = OWLModelFactory.getOWLDataFactory(PROJECT_ID).getOWLClass(IRI.create(c1));
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        int oldIDDisplayPreference = store.getInt(NeOnUIPlugin.ID_DISPLAY_PREFERENCE);
        try {
            store.setValue(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, OWLPlugin.DISPLAY_LANGUAGE);
            String oldSpecificLanguagePreference = store.getString(OWLPlugin.SPECIFIC_LANGUAGE_PREFERENCE);
            try {
                store.setValue(OWLPlugin.SPECIFIC_LANGUAGE_PREFERENCE, "en");
                String[] labels = OWLGUIUtilities.getIdArray(entity, ONTOLOGY_URI, PROJECT_ID, false);
                assertArrayEquals(new String[] { value, "c1" }, labels);
            } finally {
                store.setValue(OWLPlugin.SPECIFIC_LANGUAGE_PREFERENCE, oldSpecificLanguagePreference);
            }
        } finally {
            store.setValue(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, oldIDDisplayPreference);
        }
    }
    
    @Test
    public void testAnnotations() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        readAndDispatch();

        String annotationProperty = "rdfs:label"; //$NON-NLS-1$
        String value = "some label"; //$NON-NLS-1$
        String range = "xsd:string"; //$NON-NLS-1$
        String lang = "en"; //$NON-NLS-1$
        String[] newValues = new String[] {annotationProperty, value, range, lang};
        new AddEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, c1, newValues).run();

        newValues = new String[] {annotationProperty, value, range, ""};  //$NON-NLS-1$
        new AddEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, c1, newValues).run();
        
        String[][] results = new GetEntityAnnotationHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(2, results.length);
        String axiom = "[annotationIRI rdfs:label " + c1 + " \"some label\"@en]"; //$NON-NLS-1$ //$NON-NLS-2$
        String axiom2 = "[annotationIRI rdfs:label " + c1 + " \"some label\"^^<http://www.w3.org/2001/XMLSchema#string>]"; //$NON-NLS-1$ //$NON-NLS-2$
        String[][] expected = new String[][] {
                {axiom, ONTOLOGY_URI},
                {axiom2, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, results);
        
        String newAnnotationProperty = "rdfs:comment"; //$NON-NLS-1$
        newValues = new String[] {newAnnotationProperty, value, range, OWLCommandUtils.EMPTY_LANGUAGE};
        new EditEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, c1, axiom, newValues).run();
        
        results = new GetEntityAnnotationHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(2, results.length);
        axiom = "[annotationIRI rdfs:comment " + c1 + " \"some label\"^^<http://www.w3.org/2001/XMLSchema#string>]"; //$NON-NLS-1$ //$NON-NLS-2$
        expected = new String[][] {
                {axiom, ONTOLOGY_URI},
                {axiom2, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, results);
        
        // remove axiom
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[0], new String[]{axiom}).run();

        results = new GetEntityAnnotationHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(1, results.length);
        expected = new String[][] {
                {axiom2, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, results);
    }
    
    @Test
    public void testClazzAnnotations() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        readAndDispatch();
        String expectedAxiom = "[annotationIRI rdfs:label " + c1 + " \"some label\"@en]"; //$NON-NLS-1$ //$NON-NLS-2$
        
        doTestEntityAnnotation(c1, expectedAxiom);
    }
    
    @Test
    public void testClazzAnnotationsWithIndividualValue() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS);
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS);
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        readAndDispatch();
        String owlIndividual = "http://schema.ontoprise.com/reserved#owlIndividual";
        String expectedAxiom = "[annotationIRI rdfs:seeAlso " + c1 + " [" + i1 + "]]";
        
        String annotationProperty = "rdfs:seeAlso";
        String value = i1;
        String range = owlIndividual;
        String language = "";
        String[] newValues = new String[] {annotationProperty, value, range, language};
        
        new AddEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, c1, newValues).run();

        String[][] results = new GetEntityAnnotationHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(1, results.length);
        String[][] expected = new String[][] {
                {expectedAxiom, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, results);
    }

    @Test
    public void testRenameIndividual() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS);
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS);
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS);
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        readAndDispatch();
        String owlIndividual = "http://schema.ontoprise.com/reserved#owlIndividual";
        String expectedAxiom = "[annotationIRI rdfs:seeAlso " + i1 + " [" + i1 + "]]";
        
        String annotationProperty = "rdfs:seeAlso";
        String value = i1;
        String range = owlIndividual;
        String language = "";
        String[] newValues = new String[] {annotationProperty, value, range, language};
        
        new AddEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, i1, newValues).run();

        String[][] results = new GetEntityAnnotationHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(1, results.length);
        String[][] expected = new String[][] {
                {expectedAxiom, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, results);

        String expectedAxiom2 = "[annotationIRI rdfs:seeAlso " + i2 + " [" + i2 + "]]";
        new RenameOWLIndividual(PROJECT_ID, ONTOLOGY_URI, i1, i2).run();
        String[][] results2 = new GetEntityAnnotationHits(PROJECT_ID, ONTOLOGY_URI, i2).getResults();
        Assert.assertEquals(1, results2.length);
        String[][] expected2 = new String[][] {
                {expectedAxiom2, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected2, results2);
    }
    
    @Test
    public void testObjectPropertyAnnotations() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, c1, null).run();
        readAndDispatch();
        String expectedAxiom = "[annotationIRI rdfs:label " + c1 + " \"some label\"@en]"; //$NON-NLS-1$ //$NON-NLS-2$
        
        doTestEntityAnnotation(c1, expectedAxiom);
    }
    
    @Test
    public void testDataPropertyAnnotations() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$

        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, c1, null).run();
        readAndDispatch();
        String expectedAxiom = "[annotationIRI rdfs:label " + c1 + " \"some label\"@en]"; //$NON-NLS-1$ //$NON-NLS-2$
        
        doTestEntityAnnotation(c1, expectedAxiom);
    }
    
    @Test
    public void testAnnotationPropertyAnnotations() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$

        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, c1, null).run();
        readAndDispatch();
        String expectedAxiom = "[annotationIRI rdfs:label " + c1 + " \"some label\"@en]"; //$NON-NLS-1$ //$NON-NLS-2$
        
        doTestEntityAnnotation(c1, expectedAxiom);
    }
    
    @Test
    public void testIndividualAnnotations() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        readAndDispatch();
        String expectedAxiom = "[annotationIRI rdfs:label " + i1 + " \"some label\"@en]"; //$NON-NLS-1$ //$NON-NLS-2$
        
        doTestEntityAnnotation(i1, expectedAxiom);
    }
    
    @Test
    public void testOntologyAnnotations() throws Exception {
        String expectedAxiom = "[annotationIRI rdfs:label " + GetOntologyAnnotations.DUMMY_ONTOLOGY_URI + " \"some label\"@en]"; //$NON-NLS-1$ //$NON-NLS-2$
        
        String annotationProperty = "rdfs:label"; //$NON-NLS-1$
        String value = "some label"; //$NON-NLS-1$
        String range = "xsd:string"; //$NON-NLS-1$
        String lang = "en"; //$NON-NLS-1$
        String[] newValues = new String[] {annotationProperty, value, range, lang};
        
        new AddOntologyAnnotation(PROJECT_ID, ONTOLOGY_URI, newValues).run();

        String[] results = new GetOntologyAnnotations(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, results.length);
        String[] expected = new String[] {expectedAxiom};
        assertUnsortedArrayEquals(expected, results);
    }
    
    private void doTestEntityAnnotation(String entityUri, String expectedAxiom) throws CommandException, NeOnCoreException {
        String annotationProperty = "rdfs:label"; //$NON-NLS-1$
        String value = "some label"; //$NON-NLS-1$
        String range = "xsd:string"; //$NON-NLS-1$
        String lang = "en"; //$NON-NLS-1$
        String[] newValues = new String[] {annotationProperty, value, range, lang};
        
        new AddEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, entityUri, newValues).run();

        String[][] results = new GetEntityAnnotationHits(PROJECT_ID, ONTOLOGY_URI, entityUri).getResults();
        Assert.assertEquals(1, results.length);
        String[][] expected = new String[][] {
                {expectedAxiom, ONTOLOGY_URI}};
        assertUnsortedArrayEquals(expected, results);
    }
}
