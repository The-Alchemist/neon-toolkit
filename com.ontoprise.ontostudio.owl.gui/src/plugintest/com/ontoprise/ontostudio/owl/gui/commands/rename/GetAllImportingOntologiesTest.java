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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;

import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/**
 * @author krekeler
 *
 */
public class GetAllImportingOntologiesTest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public GetAllImportingOntologiesTest(Properties properties) {
        super(properties);
    }

    @Test
    public void test() throws Exception {
        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
        OWLModel importedModel = OWLModelFactory.getOWLModel(IMPORTED_ONTOLOGY_URI, PROJECT_ID);
        assertEquals(Collections.emptySet(), model.getAllImportingOntologies());
        assertEquals(Collections.singleton(importedModel), model.getAllImportedOntologies());
        assertEquals(Collections.singleton(model), importedModel.getAllImportingOntologies());
        assertEquals(Collections.emptySet(), importedModel.getAllImportedOntologies());
        
        String importingOntologyURI = "http://test.org/importingOntology#"; //$NON-NLS-1$
        new CreateOntology(PROJECT_ID, importingOntologyURI, importingOntologyURI, "").run(); //$NON-NLS-1$
        OWLModel importingModel = OWLModelFactory.getOWLModel(importingOntologyURI, PROJECT_ID);
        assertEquals(Collections.emptySet(), importingModel.getAllImportingOntologies());
        assertEquals(Collections.emptySet(), importingModel.getAllImportedOntologies());
        
        importingModel.addToImports(model);
        assertEquals(Collections.emptySet(), importingModel.getAllImportingOntologies());
        assertEquals(new LinkedHashSet<OWLModel>(Arrays.asList(model, importedModel)), importingModel.getAllImportedOntologies());

        assertEquals(new LinkedHashSet<OWLModel>(Arrays.asList(importingModel, model)), importedModel.getAllImportingOntologies());
    }
}
