/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 28.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.model.commands.GetPropertyAttribute;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.SetPropertyAttribute;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;

/**
 * @author werner
 * 
 */
public class PropertyAttributesTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public PropertyAttributesTest(Properties properties) {
        super(properties);
    }

    @Test
    public void testSetUnsetObjectPropertyAttributes() throws Exception {
        String op1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$

        new CreateObjectProperty(PROJECT_ID, ONTOLOGY_URI, op1, null).run();

        new SetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, op1, OWLCommandUtils.OBJECT_PROP, OWLCommandUtils.FUNCTIONAL, true).run();
        boolean isFunctional = new GetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, op1, OWLCommandUtils.FUNCTIONAL, false).getAttributeValue();

        Assert.assertTrue(isFunctional);

        new SetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, op1, OWLCommandUtils.OBJECT_PROP, OWLCommandUtils.FUNCTIONAL, false).run();
        isFunctional = new GetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, op1, OWLCommandUtils.FUNCTIONAL, false).getAttributeValue();

        Assert.assertFalse(isFunctional);

    }

    @Test
    public void testSetUnsetDataPropertyAttributes() throws Exception {
        String dp1 = createQualifiedIdentifier("op1", DEFAULT_NS); //$NON-NLS-1$

        new CreateDataProperty(PROJECT_ID, ONTOLOGY_URI, dp1, null).run();

        new SetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, dp1, OWLCommandUtils.DATA_PROP, OWLCommandUtils.FUNCTIONAL, true).run();
        boolean isFunctional = new GetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, dp1, OWLCommandUtils.FUNCTIONAL, false).getAttributeValue();

        Assert.assertTrue(isFunctional);
        
        new SetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, dp1, OWLCommandUtils.DATA_PROP, OWLCommandUtils.FUNCTIONAL, false).run();
        isFunctional = new GetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, dp1, OWLCommandUtils.FUNCTIONAL, false).getAttributeValue();

        Assert.assertFalse("expected not functional", isFunctional); //$NON-NLS-1$

        try {
            new SetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, dp1, OWLCommandUtils.DATA_PROP, OWLCommandUtils.INVERSE_FUNCTIONAL, true).run();
            Assert.fail("expected exception here"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected exception here
        }

        try {
            new SetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, dp1, OWLCommandUtils.DATA_PROP, OWLCommandUtils.SYMMETRIC, true).run();
            Assert.fail("expected exception here"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected exception here
        }

        try {
            new SetPropertyAttribute(PROJECT_ID, ONTOLOGY_URI, dp1, OWLCommandUtils.DATA_PROP, OWLCommandUtils.TRANSITIVE, true).run();
            Assert.fail("expected exception here"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected exception here
        }
    }

}
