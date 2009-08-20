/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 14.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.clazz;

import static org.junit.Assert.assertArrayEquals;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateSubClazz;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetRootClazzes;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSubClazzes;
import com.ontoprise.ontostudio.owl.model.commands.clazz.RemoveClazz;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.RemoveIndividual;

/**
 * @author werner
 * 
 */
@SuppressWarnings("nls")
public class CreateRemoveClazzesTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateRemoveClazzesTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testOWLThing() throws Exception {
        String owlThing = OWLNamespaces.OWL_NS + "Thing";
        String i = "http://test.org/ontology#i";
        String R = "http://test.org/ontology#R";
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, owlThing).run();
        assertArrayEquals(new String[] { }, new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults());
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, owlThing, i).run();
        assertArrayEquals(new String[] { }, new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults());
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, R, owlThing).run();
        assertArrayEquals(new String[] { R }, new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults());
    }
    
    /**
     * Related to issue 12871.
     */
    @Test
    public void testImportedRootClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        new CreateRootClazz(PROJECT_ID, IMPORTED_ONTOLOGY_URI, c1).run();
        assertArrayEquals(new String[] { c1 }, new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults());
    }
    
    @Test
    public void testSimpleCreateRemoveClazzes() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        readAndDispatch();

        String[] rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(2, rootClazzes.length);
        assertUnsortedArrayEquals(new String[] {c1, c2}, rootClazzes);

        new RemoveClazz(PROJECT_ID, ONTOLOGY_URI, c1, null).run();
        readAndDispatch();

        rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootClazzes.length);
        Assert.assertEquals(rootClazzes[0], c2);
    }

    @Test
    public void testCreateRemoveClazzesWithSubClazzes() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c2, c1).run();
        readAndDispatch();
        String[] rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        String[] subClazzes = new GetSubClazzes(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(1, subClazzes.length);
        Assert.assertEquals(1, rootClazzes.length);

        new RemoveClazz(PROJECT_ID, ONTOLOGY_URI, c1, null).run();
        readAndDispatch();

        rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        // c1 should still be root class, since the axiom [subClassOf c2 c1] implies that
        Assert.assertEquals(1, rootClazzes.length);
        Assert.assertEquals(rootClazzes[0], c1);
    }
    
    /**
     *  Classes
     *     |- c1
     *         |- c2 
     *         |- c3
     *         
     *          remove c1 with all referencing axioms
     *          
     * @throws Exception
     */
    @Test
    public void testCreateRemoveClazzesWithSubClazzesRemoveAll() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String c3 = createQualifiedIdentifier("c3", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c2, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c3, c1).run();
        readAndDispatch();
        String[] rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        String[] subClazzes = new GetSubClazzes(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(2, subClazzes.length);
        assertUnsortedArrayEquals(new String[]{c2, c3}, subClazzes);
        Assert.assertEquals(1, rootClazzes.length);

        boolean removeAllAxioms = true;
        new RemoveClazz(PROJECT_ID, ONTOLOGY_URI, c1, null, removeAllAxioms).run();
        readAndDispatch();
        
        // no more root classes should exist, since AxiomsForEntitiesCollector is
        // used, and all axioms referencing this URI should be removed.
        rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(0, rootClazzes.length);
    }

    @Test
    public void testImplicitRootClazzes() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c2, c1).run();
        readAndDispatch();

        String[] rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootClazzes.length);
        Assert.assertEquals(rootClazzes[0], c1);
    }
    
    @Test
    public void testApplyChanges() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String c3 = createQualifiedIdentifier("c3", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c2, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c3, c1).run();
        readAndDispatch();

        String[] rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootClazzes.length);
        Assert.assertEquals(rootClazzes[0], c1);
        
        String rootClazzDeclaration = "[classDeclaration " + c1 + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        String rootClazzDeclaration2 = "[classDeclaration " + c2 + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        String subClazzC2 = "[subClassOf " + c2 + " " + c1 + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String subClazzC3 = "[subClassOf " + c3 + " " + c1 + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        String[] addChanges = new String[] {rootClazzDeclaration2};
        String[] removeChanges = new String[] {rootClazzDeclaration, subClazzC2, subClazzC3};
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, addChanges, removeChanges).run();
        readAndDispatch();
        
        rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootClazzes.length);
        Assert.assertEquals(c2, rootClazzes[0]);
        
        String[] subClazzes = new GetSubClazzes(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(0, subClazzes.length);
        subClazzes = new GetSubClazzes(PROJECT_ID, ONTOLOGY_URI, c2).getResults();
        Assert.assertEquals(0, subClazzes.length);
    }

    @Test
    public void testDeleteClazzWithIndividuals() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i2).run();
        readAndDispatch();
        
        String[] individuals = new GetIndividuals(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        assertUnsortedArrayEquals(new String[]{i1, i2}, individuals);
        
        new RemoveIndividual(PROJECT_ID, ONTOLOGY_URI, c1, new String[]{i2}).run();
        readAndDispatch();

        individuals = new GetIndividuals(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        assertUnsortedArrayEquals(new String[]{i1}, individuals);
    }

    /**
     * Classes
     *    |- c1
     *        |- c2 (i1)
     *            |- c4 (i2)
     *        |- c3
     *        
     * @throws Exception
     */
    @Test
    public void testDeleteClazzWithSubtree() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String c3 = createQualifiedIdentifier("c3", DEFAULT_NS); //$NON-NLS-1$
        String c4 = createQualifiedIdentifier("c4", DEFAULT_NS); //$NON-NLS-1$
        
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$

        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c2, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c3, c1).run();
        new CreateSubClazz(PROJECT_ID, ONTOLOGY_URI, c4, c2).run();
        readAndDispatch();
        
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c2, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c4, i2).run();
        readAndDispatch();
        
        String[] rootClazzes = new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults();
        Assert.assertEquals(1, rootClazzes.length);
        Assert.assertEquals(rootClazzes[0], c1);
        
        String[] subClazzesC1 = new GetSubClazzes(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(2, subClazzesC1.length);
        assertUnsortedArrayEquals(new String[]{c2, c3}, subClazzesC1);
        
        String[] subClazzesC2 = new GetSubClazzes(PROJECT_ID, ONTOLOGY_URI, c2).getResults();
        Assert.assertEquals(1, subClazzesC2.length);
        assertUnsortedArrayEquals(new String[]{c4}, subClazzesC2);

        boolean removeAllAxioms = true;
        new RemoveClazz(PROJECT_ID, ONTOLOGY_URI, c2, c1, removeAllAxioms).run();
        readAndDispatch();
        
    }
}
