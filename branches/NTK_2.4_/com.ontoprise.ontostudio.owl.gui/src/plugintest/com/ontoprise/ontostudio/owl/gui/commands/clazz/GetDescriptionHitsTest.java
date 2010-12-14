/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.clazz;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.imports.GetImportedOntologies;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetDescriptionHits;
import com.ontoprise.ontostudio.owl.model.commands.individual.RemoveIndividual;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/**
 * @author werner
 * 
 */
public class GetDescriptionHitsTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public GetDescriptionHitsTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testGetDescriptionHits() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i2).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c2, i1).run();
        readAndDispatch();

        String[][] results = new GetDescriptionHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        Assert.assertEquals(2, results.length);
        String[][] results2 = new GetDescriptionHits(PROJECT_ID, ONTOLOGY_URI, i2).getResults();
        Assert.assertEquals(1, results2.length);

        new RemoveIndividual(PROJECT_ID, ONTOLOGY_URI, c1, new String[] {i1}, false).run();
        readAndDispatch();

        results = new GetDescriptionHits(PROJECT_ID, ONTOLOGY_URI, i1).getResults();
        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
        OWLNamespaces namespaces = model.getNamespaces();
        OWLDataFactory factory = model.getOWLDataFactory();
        OWLClassAssertionAxiom clazzMember = (OWLClassAssertionAxiom) OWLUtilities.axiom(results[0][0], namespaces, factory);

        Assert.assertEquals(1, results.length);
        Assert.assertEquals(c2, OWLUtilities.toString(clazzMember.getClassExpression()));

        results2 = new GetDescriptionHits(PROJECT_ID, ONTOLOGY_URI, i2).getResults();
        Assert.assertEquals(1, results2.length);
    }

    @Test
    public void testWithImportedOntology() throws Exception {
        String defaultNs2 = "http://www.NewOnto2.org#"; //$NON-NLS-1$
        OWLModelPlugin.getDefault().getPreferenceStore().setValue(OWLModelPlugin.SHOW_IMPORTED, true);
        IOntologyProject ontoProject = OntologyProjectManager.getDefault().getOntologyProject(PROJECT_ID);
        if (ontoProject.getAdapter(OWLOntologyManager.class).getOntology(IRI.create(IMPORTED_ONTOLOGY_URI)) == null) {
            new CreateOntology(PROJECT_ID, IMPORTED_ONTOLOGY_URI, defaultNs2, "").run(); //$NON-NLS-1$
        }
        String[] importedOntos = new GetImportedOntologies(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, importedOntos.length);
        Assert.assertEquals(IMPORTED_ONTOLOGY_URI, importedOntos[0]);

        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$

        String c2_imported = createQualifiedIdentifier("c2", defaultNs2); //$NON-NLS-1$
        String i3_imported = createQualifiedIdentifier("i3", defaultNs2); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i2).run();

        new CreateRootClazz(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c2_imported).run();
        new CreateIndividual(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c2_imported, i3_imported).run();

        String[][] results = new GetDescriptionHits(PROJECT_ID, ONTOLOGY_URI, i2).getResults();
        Assert.assertEquals(1, results.length);

        new CreateIndividual(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c2_imported, i2).run();
        results = new GetDescriptionHits(PROJECT_ID, ONTOLOGY_URI, i2).getResults();
        Assert.assertEquals(2, results.length);

        String[] resultOntos = new String[] {results[0][1], results[1][1]};
        String[] expected = new String[] {ONTOLOGY_URI, IMPORTED_ONTOLOGY_URI};
        assertUnsortedArrayEquals(expected, resultOntos);
    }
}
