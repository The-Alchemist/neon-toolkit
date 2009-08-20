/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 20.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.clazz;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.clazz.AddRestriction;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.clazz.EditRestriction;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetEquivalentRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSuperRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;

/**
 * @author werner
 *
 */
public class CreateEditRemoveRestrictionsTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateEditRemoveRestrictionsTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testDataSomeAllSubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("hasParent", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        
        String type = "xsd:integer"; //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.SOME, 
                dp1, 
                type, 
                ""}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [dataSome ").append(dp1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);

        values = new String[]{
                OWLCommandUtils.ALL, 
                dp1, 
                type, 
                ""}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom2 = new StringBuilder("[subClassOf ").append(c1).append(" [dataAll ").append(dp1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(2, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[][]{
                {expectedAxiom.toString(), ONTOLOGY_URI},
                {expectedAxiom2.toString(), ONTOLOGY_URI}},
                superRestrictionHits);
    }

    @Test
    public void testDataCardinalitySubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("hasParent", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        
        String type = "owl:Thing"; //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.EXACTLY_CARDINALITY, 
                dp1, 
                type, 
                "33"}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [dataExactly 33 ").append(dp1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }

    @Test
    public void testDataAtLeastSubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("hasParent", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        
        String type = "owl:Thing"; //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.AT_LEAST_MIN, 
                dp1, 
                type, 
                "33"}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [dataAtLeast 33 ").append(dp1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }
    
    @Test
    public void testDataAtMostSubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("hasParent", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        
        String type = "owl:Thing"; //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.AT_MOST_MAX, 
                dp1, 
                type, 
                "33"}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [dataAtMost 33 ").append(dp1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }

    @Test 
    public void testComplexRange() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("hasParent", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        
        String type = "[or " + c1 + " " + c2 + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] values = new String[]{
                OWLCommandUtils.AT_MOST_MAX, 
                op1, 
                type, 
                "33"}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [atMost 33 ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }

    @Test
    public void testObjectSomeAllSubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("hasBase", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        
        String type = c2;
        String[] values = new String[]{
                OWLCommandUtils.SOME, 
                op1, 
                type, 
                ""}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [some ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);

        values = new String[]{
                OWLCommandUtils.ALL, 
                op1, 
                type, 
                ""}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom2 = new StringBuilder("[subClassOf ").append(c1).append(" [all ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(2, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[][]{
                {expectedAxiom.toString(), ONTOLOGY_URI},
                {expectedAxiom2.toString(), ONTOLOGY_URI}},
                superRestrictionHits);
    }

    @Test
    public void testObjectCardinalitySubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("hasBase", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        
        String type = c2;
        String cardinality = "3";  //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.EXACTLY_CARDINALITY, 
                op1, 
                type, 
                cardinality};
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [exactly ").append(cardinality).append(" ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }

    @Test
    public void testObjectAtMostSubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("hasBase", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        
        String type = c2;
        String atMost = "3";  //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.AT_MOST_MAX, 
                op1, 
                type, 
                atMost};
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [atMost ").append(atMost).append(" ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }

    @Test
    public void testObjectAtLeastSubClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("hasBase", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        
        String type = c2;
        String atLeast = "3";  //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.AT_LEAST_MIN, 
                op1, 
                type, 
                atLeast};
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [atLeast ").append(atLeast).append(" ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }

    @Test
    public void testObjectSomeAllEquivalentClass() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("hasBase", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        
        String type = c2;
        String[] values = new String[]{
                OWLCommandUtils.SOME, 
                op1, 
                type, 
                ""}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.EQUIV).run();
        
        
        String[][] superRestrictionHits = new GetEquivalentRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[equivalent ").append(c1).append(" [some ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);

        values = new String[]{
                OWLCommandUtils.ALL, 
                op1, 
                type, 
                ""}; //$NON-NLS-1$
        
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.EQUIV).run();
        
        superRestrictionHits = new GetEquivalentRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom2 = new StringBuilder("[equivalent ").append(c1).append(" [all ").append(op1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(2, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[][]{
                {expectedAxiom.toString(), ONTOLOGY_URI},
                {expectedAxiom2.toString(), ONTOLOGY_URI}},
                superRestrictionHits);
    }
    
    @Test
    public void testCreateEdit() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("hasParent", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        
        String type = "xsd:integer"; //$NON-NLS-1$
        String[] values = new String[]{
                OWLCommandUtils.SOME, 
                dp1, 
                type, 
                ""}; //$NON-NLS-1$
        new AddRestriction(PROJECT_ID, ONTOLOGY_URI, c1, values, OWLCommandUtils.INCL).run();
        
        String[][] superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        StringBuilder expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [dataSome ").append(dp1).append(" ").append(type).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]{expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);

        String[] newValues = new String[] {
                OWLCommandUtils.SOME, 
                dp1, 
                "xsd:int",  //$NON-NLS-1$
                ""}; //$NON-NLS-1$
        String desc = new StringBuilder("[dataSome ").append(dp1).append(" ").append(type).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new EditRestriction(PROJECT_ID, ONTOLOGY_URI, OWLCommandUtils.INCL, c1, newValues, desc).run();
        
        expectedAxiom = new StringBuilder("[subClassOf ").append(c1).append(" [dataSome ").append(dp1).append(" ").append("xsd:int").append("]]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        superRestrictionHits = new GetSuperRestrictionHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(1, superRestrictionHits.length);
        assertUnsortedArrayEquals(new String[]
                {expectedAxiom.toString(), ONTOLOGY_URI}, superRestrictionHits[0]);
    }

}
