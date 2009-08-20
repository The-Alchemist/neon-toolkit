/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 27.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.dataProperty;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataPropertyDomain;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataPropertyRange;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetDataPropertyDomains;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetDataPropertyRanges;

/**
 * @author werner
 *
 */
public class DataPropertyDomainAndRangeTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public DataPropertyDomainAndRangeTest(Properties properties) {
        super(properties);
    }
    
    @Test
    public void testCreateRemoveObjectPropertyDomainAndRange() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        new CreateDataPropertyDomain(PROJECT_ID, ONTOLOGY_URI, dp1, c1).run();
        new CreateDataPropertyRange(PROJECT_ID, ONTOLOGY_URI, dp1, c2).run();
        readAndDispatch();
        
        String[][] domains = new GetDataPropertyDomains(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        String[][] ranges = new GetDataPropertyRanges(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        Assert.assertEquals(1, domains.length);
        Assert.assertEquals(1, ranges.length);
        
        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
        OWLNamespaces namespaces = model.getNamespaces();
        OWLDataFactory factory = model.getOWLDataFactory();
        OWLDataPropertyDomainAxiom domain = (OWLDataPropertyDomainAxiom) OWLUtilities.axiom(domains[0][0], namespaces, factory);
        Assert.assertEquals(c1, OWLUtilities.toString(domain.getDomain()));
        
        OWLDataPropertyRangeAxiom range = (OWLDataPropertyRangeAxiom) OWLUtilities.axiom(ranges[0][0], namespaces, factory);
        Assert.assertEquals(c2, OWLUtilities.toString(range.getRange()));
        
        String domainAxiom = new StringBuilder("[dataDomain ").append(dp1).append(" ").append(c1).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String rangeAxiom = new StringBuilder("[dataRange ").append(dp1).append(" ").append(c2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[0], new String[]{domainAxiom, rangeAxiom}).run();

        domains = new GetDataPropertyDomains(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        ranges = new GetDataPropertyRanges(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        Assert.assertEquals(0, domains.length);
        Assert.assertEquals(0, ranges.length);
    }
    
    @Test
    public void testComplexDomain() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String c3 = createQualifiedIdentifier("c3", DEFAULT_NS); //$NON-NLS-1$
        String dp1 = createQualifiedIdentifier("dp1", DEFAULT_NS); //$NON-NLS-1$
        
        String complexDomain = "[or " + c2 + " " + c3 + "]";   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();
        new CreateDataPropertyDomain(PROJECT_ID, ONTOLOGY_URI, dp1, complexDomain).run();
        new CreateDataPropertyRange(PROJECT_ID, ONTOLOGY_URI, dp1, c1).run();
        
        String[][] domains = new GetDataPropertyDomains(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        String[][] ranges = new GetDataPropertyRanges(PROJECT_ID, ONTOLOGY_URI, dp1).getResults();
        Assert.assertEquals(1, domains.length);
        Assert.assertEquals(1, ranges.length);
    }

}
