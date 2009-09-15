/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.neontoolkit.core.command.CommandException;

/**
 * @author werner
 *
 */
public class CreateValidUriTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public CreateValidUriTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testCreateValidUris() throws Exception {
        String input = "a"; //$NON-NLS-1$
        String expectedOutcome = "http://www.NewOnto1.org#a"; //$NON-NLS-1$
        
        try {
            String outcome = new CreateValidUri(PROJECT_ID, ONTOLOGY_URI, input, DEFAULT_NS).getResult();
            Assert.assertEquals(expectedOutcome, outcome);
        } catch (CommandException e) {
            Assert.fail("Failed creating qualified identifier"); //$NON-NLS-1$
        }
        Assert.assertTrue(true);
        
    }
}
