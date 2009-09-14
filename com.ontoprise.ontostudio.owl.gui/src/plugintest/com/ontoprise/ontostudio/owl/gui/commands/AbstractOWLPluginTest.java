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

import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.ParameterizedConfiguration;
import org.neontoolkit.core.PluginTestDatamodel;
import org.neontoolkit.core.ParameterizedConfiguration.DefaultConfiguration;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.project.CreateProject;
import org.neontoolkit.core.command.project.RemoveProject;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.TestOntologyProject;
import org.neontoolkit.core.project.TestOntologyProjectFactory;

import com.ontoprise.ontostudio.owl.gui.navigator.project.AddOWLOntology;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.imports.AddOntologyToImports;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/*
 * Created on 18.07.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * Abstract superclass for plugin tests that need a project and an ontology. This abstract class creates a project and an ontology before any of the tests is started.
 * After each test, all information contained in the module is deleted.
 */
@RunWith(ParameterizedConfiguration.class)
public class AbstractOWLPluginTest extends BasePluginTest  {
    protected static final String ONTOLOGY_URI = "http://test.ontoprise.de#ontology"; //$NON-NLS-1$
    protected static final String IMPORTED_ONTOLOGY_URI = "http://test.ontoprise.de#importedOntology"; //$NON-NLS-1$
    protected static final String DEFAULT_NS = "http://www.NewOnto1.org#"; //$NON-NLS-1$
    protected static final String INVALID_ID = "http://test.ontoprise.de#INVALID_ID"; //$NON-NLS-1$
    protected static final String SYSTEM_INTERNAL_NAMESPACE = "http://schema.ontoprise.com/reserved#"; //$NON-NLS-1$

    @DefaultConfiguration
    public static Properties getDefaultConfigurations() {
        Properties properties = new Properties();

//        // KAON2
//        properties.put(ParameterizedConfiguration.PROJECT_FACTORY_ID_KEY, "org.neontoolkit.datamodel.project.OWLProjectFactory"); //$NON-NLS-1$
//        properties.put("OntologyLanguage", "OWL"); //$NON-NLS-1$ //$NON-NLS-2$
//        properties.put("Indexer", "on"); //$NON-NLS-1$ //$NON-NLS-2$
//        String indexerKey = UUID.randomUUID().toString();
//        properties.put("indexer.key", indexerKey);
//        // ... RAM
//        properties.put("Storage", "RAM.choose"); //$NON-NLS-1$ //$NON-NLS-2$
//        // ... H2
////        properties.put("Storage", "H2"); //$NON-NLS-1$ //$NON-NLS-2$
////        properties.put("H2.URL", "jdbc:h2:mem:test");
////        properties.put("H2.User", "sa");
////        properties.put("H2.Password", "");
        
        // Manchester
        properties.put(ParameterizedConfiguration.PROJECT_FACTORY_ID_KEY, OWLManchesterProjectFactory.FACTORY_ID);
        
        return properties;

    }
    
    protected static Properties _properties;

    public AbstractOWLPluginTest(Properties properties) {
        _properties = properties;
    }
    
    protected void readAndDispatch() {
        // this method is deprecated since each command executes a read and dispatch
    }

    public static void createProject() throws CommandException {
        if (!ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_ID).exists()) {
            Properties properties = getProperties();
            new CreateProject(PROJECT_ID, getProjectFactoryId(properties), properties).run();
        }
    }
    
    private static String getProjectFactoryId(Properties properties) {
        if (properties != null) {
            String id = (String)properties.get(ParameterizedConfiguration.PROJECT_FACTORY_ID_KEY);
            if (id != null) {
                return id;
            }
        }
        return OWLManchesterProjectFactory.FACTORY_ID;
    }

    public static void createOntology() throws CommandException {
        try {
            IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(PROJECT_ID);
            if (project.getAvailableOntologyURIs().contains(ONTOLOGY_URI)) {
                new AddOWLOntology(PROJECT_ID, new String[] {ONTOLOGY_URI}).run();
            } else {
                new CreateOntology(PROJECT_ID, ONTOLOGY_URI, DEFAULT_NS, "").run(); //$NON-NLS-1$
//                project.createOntology(ONTOLOGY_URI, DEFAULT_NS);
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
        // TODO: migration
        //throw new UnsupportedOperationException("TODO: migration");
//        OntologyManager manager = DatamodelPlugin.getDefault().getOntologyManager(PROJECT_ID);
//        if (manager.getAvailableOntologyURIs().contains(ONTOLOGY_URI)) {
//            new AddOWLOntology(PROJECT_ID, new String[] {ONTOLOGY_URI}).run();
//        } else if (manager.getOntology(ONTOLOGY_URI) == null) {
//            new CreateOntology(PROJECT_ID, ONTOLOGY_URI, DEFAULT_NS, "").run(); //$NON-NLS-1$
//        }
    }

    public static void createImportedOntology() throws CommandException {
        try {
            IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(PROJECT_ID);
            if (project.getAvailableOntologyURIs().contains(IMPORTED_ONTOLOGY_URI)) {
                new AddOWLOntology(PROJECT_ID, new String[] {IMPORTED_ONTOLOGY_URI}).run();
            } else {
                new CreateOntology(PROJECT_ID, IMPORTED_ONTOLOGY_URI, DEFAULT_NS, "").run(); //$NON-NLS-1$
//                project.createOntology(IMPORTED_ONTOLOGY_URI, DEFAULT_NS);
            }
            new AddOntologyToImports(PROJECT_ID, ONTOLOGY_URI, IMPORTED_ONTOLOGY_URI).run();
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
        // TODO: migration
        //throw new UnsupportedOperationException("TODO: migration");
//        OntologyManager manager = DatamodelPlugin.getDefault().getOntologyManager(PROJECT_ID);
//        if (manager.getAvailableOntologyURIs().contains(IMPORTED_ONTOLOGY_URI)) {
//            new AddOWLOntology(PROJECT_ID, new String[] {IMPORTED_ONTOLOGY_URI}).run();
//        } else if (manager.getOntology(IMPORTED_ONTOLOGY_URI) == null) {
//            new CreateOntology(PROJECT_ID, IMPORTED_ONTOLOGY_URI, DEFAULT_NS, "").run(); //$NON-NLS-1$
//        }
//        new AddOntologyToImports(PROJECT_ID, ONTOLOGY_URI, IMPORTED_ONTOLOGY_URI).run();
    }

    public static String createQualifiedIdentifier(String id, String defaultNamespace) {
        try {
            return new CreateValidUri(PROJECT_ID, ONTOLOGY_URI, id, defaultNamespace).getResult();
        } catch (CommandException e) {
            return INVALID_ID;
        } catch (NeOnCoreException e) {
            return INVALID_ID;
        }
    }

    protected static Properties getProperties() {
        if (_properties == null) {
            _properties = new Properties();
        }
        return _properties;
    }

    @After
    public void cleanOntologyAfterTest() throws Exception {
        IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(PROJECT_ID);
        for (String ontologyURI: project.getAvailableOntologyURIs()) {
            project.removeOntology(ontologyURI, true);
        }
//        try {
//            
//            //OWLModel owlModel = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
//            // TODO: migration
//            if (true) throw new UnsupportedOperationException("TODO: migration");
////            Set<Ontology> openOntologies = DatamodelPlugin.getDefault().getOntologyManager(PROJECT_ID).getOntologies();
////            DatamodelPlugin.getDefault().getOntologyManager(PROJECT_ID).deleteOntologies(openOntologies);
//            //owlModel.cleanCaches();
//        } catch (NeOnCoreException e) {
//            // nothing to do, ontology has been removed and owlModel is no longer available
//        }
    }

    @Before
    public void startTest() throws Exception {
        PluginTestDatamodel.logStartTest(this);
        createProject();
        createOntology();
        createImportedOntology();
    }
    
    @BeforeClass
    public static void startTests() {
        NeOnCorePlugin.getDefault().addOntologyProjectFactory(TestOntologyProject.LANGUAGE_ID, new TestOntologyProjectFactory());
    }

    @AfterClass
    public static void stop() throws Exception {
        new RemoveProject(PROJECT_ID, true).run();
    }

}
