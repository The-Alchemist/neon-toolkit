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

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.MoveIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.RemoveIndividual;

/**
 * @author werner
 *
 */
public class CreateMoveIndividualTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateMoveIndividualTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testCreateMoveRemoveIndividual() throws Exception {
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
        String c2 = createQualifiedIdentifier("c2", DEFAULT_NS); //$NON-NLS-1$
        String i1 = createQualifiedIdentifier("i1", DEFAULT_NS); //$NON-NLS-1$
        String i2 = createQualifiedIdentifier("i2", DEFAULT_NS); //$NON-NLS-1$
        String i3 = createQualifiedIdentifier("i3", DEFAULT_NS); //$NON-NLS-1$
        
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
        new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c2).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c1, i1).run();
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c2, i2).run();
        readAndDispatch();
        
        new MoveIndividual(PROJECT_ID, ONTOLOGY_URI, i1, c1, c2).run();
        readAndDispatch();

        String[] individuals = new GetIndividuals(PROJECT_ID, ONTOLOGY_URI, c1).getResults();
        Assert.assertEquals(0, individuals.length);

        individuals = new GetIndividuals(PROJECT_ID, ONTOLOGY_URI, c2).getResults();
        Assert.assertEquals(2, individuals.length);
        assertUnsortedArrayEquals(new String[] {i1, i2}, individuals);
        
        new CreateIndividual(PROJECT_ID, ONTOLOGY_URI, c2, i3).run();
        readAndDispatch();
        new RemoveIndividual(PROJECT_ID, ONTOLOGY_URI, c2, new String[]{i1, i2}).run();
        readAndDispatch();
        individuals = new GetIndividuals(PROJECT_ID, ONTOLOGY_URI, c2).getResults();
        Assert.assertEquals(1, individuals.length);
        assertUnsortedArrayEquals(new String[] {i3}, individuals);
    }
}
