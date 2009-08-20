/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 26.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.clazz;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetEquivalentClazzHits;

/**
 * @author werner
 *
 */
public class EquivalentDisjointClazzesTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public EquivalentDisjointClazzesTest(Properties properties) {
        super(properties);
    }

    @Test
    public void simpleTestCreateRemoveEquivalentClazzes() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String c3 = createQualifiedIdentifier("c3", DEFAULT_NS); //$NON-NLS-1$
        
        String axiom = new StringBuilder("[equivalent ").append(c1).append(" ").append(c2).append("]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[]{axiom}, new String[0]).run();
        readAndDispatch();
        
        String[][] results1 = new GetEquivalentClazzHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        String[][] results2 = new GetEquivalentClazzHits(PROJECT_ID, ONTOLOGY_URI, c2).getResults();
        
        Assert.assertEquals(1, results1.length);
        Assert.assertEquals(1, results2.length);
        
        axiom = new StringBuilder("[equivalent ").append(c1).append(" [and [oneOf ").append(c2).append(" ").append(c3).append("]]]").toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        new ApplyChanges(PROJECT_ID, ONTOLOGY_URI, new String[]{axiom}, new String[0]).run();
        readAndDispatch();

        results1 = new GetEquivalentClazzHits(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        results2 = new GetEquivalentClazzHits(PROJECT_ID, ONTOLOGY_URI, c2).getResults();

        Assert.assertEquals(2, results1.length);
        Assert.assertEquals(1, results2.length);
    }
}
