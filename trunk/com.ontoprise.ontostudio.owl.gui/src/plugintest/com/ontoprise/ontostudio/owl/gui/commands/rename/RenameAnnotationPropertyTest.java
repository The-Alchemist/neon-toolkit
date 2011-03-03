/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.rename;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetRootAnnotationProperties;
import com.ontoprise.ontostudio.owl.model.commands.annotations.AddEntityAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetEntityAnnotationHits;
import com.ontoprise.ontostudio.owl.model.commands.rename.RenameOWLAnnotationProperty;

@SuppressWarnings("nls")
public class RenameAnnotationPropertyTest extends AbstractOWLPluginTest {

    public RenameAnnotationPropertyTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testRenameAnnotationPropertyDeclaration() throws Exception {
        String oldURI = createQualifiedIdentifier("oldURI", DEFAULT_NS); //$NON-NLS-1$
        String newURI = createQualifiedIdentifier("newURI", DEFAULT_NS); //$NON-NLS-1$

        assertArrayEquals(new String[] { }, getRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI));
        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, oldURI, null).run();
        assertArrayEquals(new String[] { oldURI }, getRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI));
        new RenameOWLAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, oldURI, newURI).run();
        assertArrayEquals(new String[] { newURI }, getRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI));
    }

    @Test
    public void testRenameAnnotationPropertyAnnotation() throws Exception {
        String oldURI = createQualifiedIdentifier("oldURI", DEFAULT_NS); //$NON-NLS-1$
        String newURI = createQualifiedIdentifier("newURI", DEFAULT_NS); //$NON-NLS-1$

        assertEquals(0, getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, oldURI).size());
        assertEquals(0, getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, newURI).size());
        assertArrayEquals(new String[] { }, getRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI));
        
        new CreateAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, oldURI, null).run();
        new AddEntityAnnotation(PROJECT_ID, ONTOLOGY_URI, oldURI, new String[] { oldURI, OWLNamespaces.XSD_NS + "string", "hello", "" }).run();
        assertArrayEquals(new String[] { oldURI }, getRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI));

        assertEquals(1, getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, oldURI).size());
        assertEquals(IRI.create(oldURI), getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, oldURI).iterator().next().getSubject());
        assertEquals(0, getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, newURI).size());
        
        new RenameOWLAnnotationProperty(PROJECT_ID, ONTOLOGY_URI, oldURI, newURI).run();
        assertArrayEquals(new String[] { newURI }, getRootAnnotationProperties(PROJECT_ID, ONTOLOGY_URI));
        assertEquals(0, getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, oldURI).size());
        assertEquals(1, getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, newURI).size());
        assertEquals(IRI.create(newURI), getAnnotationAssertions(PROJECT_ID, ONTOLOGY_URI, newURI).iterator().next().getSubject());
    }

    private Set<OWLAnnotationAssertionAxiom> getAnnotationAssertions(String projectId, String ontologyURI, String entityURI) throws NeOnCoreException, CommandException {
        OWLModel model = OWLModelFactory.getOWLModel(ontologyURI, projectId);
        GetEntityAnnotationHits getEntityAnnotationHitsNew = new GetEntityAnnotationHits(projectId, ontologyURI, entityURI);
        getEntityAnnotationHitsNew.run();
        Set<OWLAnnotationAssertionAxiom> result = new LinkedHashSet<OWLAnnotationAssertionAxiom>();
        for (String[] item: getEntityAnnotationHitsNew.getResults()) {
            result.add((OWLAnnotationAssertionAxiom)OWLUtilities.axiom(item[0]));
        }
        return result;
    }
    
    private String[] getRootAnnotationProperties(String projectId, String ontologyURI) throws CommandException {
        GetRootAnnotationProperties getRootAnnotationProperties = new GetRootAnnotationProperties(projectId, ontologyURI);
        getRootAnnotationProperties.run();
        return getRootAnnotationProperties.getResults();
    }
}
