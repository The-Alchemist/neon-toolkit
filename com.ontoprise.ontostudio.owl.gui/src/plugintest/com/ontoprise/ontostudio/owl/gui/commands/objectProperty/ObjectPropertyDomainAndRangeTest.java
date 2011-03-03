/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.objectProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectPropertyDomain;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectPropertyRange;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetObjectPropertyDomains;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetObjectPropertyRanges;

/**
 * @author werner
 * @author Nico Stieler
 *
 */
public class ObjectPropertyDomainAndRangeTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public ObjectPropertyDomainAndRangeTest(Properties properties) {
        super(properties);
    }
    
    @Test
    public void testCreateRemoveObjectPropertyDomainAndRange() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectPropertyDomain(PROJECT_ID, ONTOLOGY_URI, op1, c1).run();
        new CreateObjectPropertyRange(PROJECT_ID, ONTOLOGY_URI, op1, c2).run();
        readAndDispatch();
        
        String[][] domains = new GetObjectPropertyDomains(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        String[][] ranges = new GetObjectPropertyRanges(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        Assert.assertEquals(1, domains.length);
        Assert.assertEquals(1, ranges.length);
        
        OWLObjectPropertyDomainAxiom domain = (OWLObjectPropertyDomainAxiom) OWLUtilities.axiom(domains[0][0]);
        Assert.assertEquals(c1, OWLUtilities.toString(domain.getDomain()));
        
        OWLObjectPropertyRangeAxiom range = (OWLObjectPropertyRangeAxiom) OWLUtilities.axiom(ranges[0][0]);
        Assert.assertEquals(c2, OWLUtilities.toString(range.getRange()));
        
        String domainAxiom = new StringBuilder("[objectDomain ").append(op1).append(" ").append(c1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String rangeAxiom = new StringBuilder("[objectRange ").append(op1).append(" ").append(c2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[0], new String[]{domainAxiom, rangeAxiom}).run();

        domains = new GetObjectPropertyDomains(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        ranges = new GetObjectPropertyRanges(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        Assert.assertEquals(0, domains.length);
        Assert.assertEquals(0, ranges.length);
    }

    @Test
    public void testComplexDomainAndRange() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String c3 = createQualifiedIdentifier("c3", DEFAULT_NS); //$NON-NLS-1$
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$
        
        String complexDomainAndRange = "[or " + c2 + " " + c3 + "]";   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();
        new CreateObjectPropertyDomain(PROJECT_ID, ONTOLOGY_URI, op1, complexDomainAndRange).run();
        new CreateObjectPropertyRange(PROJECT_ID, ONTOLOGY_URI, op1, complexDomainAndRange).run();
        
        String[][] domains = new GetObjectPropertyDomains(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        String[][] ranges = new GetObjectPropertyRanges(PROJECT_ID, ONTOLOGY_URI, op1).getResults();
        Assert.assertEquals(1, domains.length);
        Assert.assertEquals(1, ranges.length);
    }
}
