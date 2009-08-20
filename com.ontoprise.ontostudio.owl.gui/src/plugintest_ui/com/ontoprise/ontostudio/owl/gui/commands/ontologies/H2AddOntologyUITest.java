/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 06.04.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands.ontologies;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.neontoolkit.gui.navigator.TestTreeViewerListener;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;

/**
 * @author werner
 *
 */
@SuppressWarnings("nls")
@Ignore("H2 configuration broken due to Migration")
public class H2AddOntologyUITest extends AbstractOWLPluginTest {

    /**
     * @param properties
     */
    public H2AddOntologyUITest(Properties properties) {
        super(properties);
    }

//    protected boolean isH2Edb(OntologyManager manager) throws KAON2Exception {
//        return manager.getParameter(IConfig.STORAGE).equals(IConfig.Storage.H2.toString());
//    }
    
    @Test
    public void testRemoveAddOntologyWithH2() throws Exception {
        
        String c1 = createQualifiedIdentifier("c1", DEFAULT_NS);
        String onto = "http://test.ontoprise.de#localOnto";

        TestTreeViewerListener listener = new TestTreeViewerListener();
        listener.attachToNavigator();

//        OntologyManager manager = DatamodelPlugin.getDefault().getOntologyManager(PROJECT_ID);
//        if (!isH2Edb(manager)) {
//            return;
//        }
//        if (manager.getAvailableOntologyURIs().contains(onto)) {
//            new AddOWLOntology(PROJECT_ID, new String[] {onto}).run();
//        } else if (manager.getOntology(onto) == null) {
//            new CreateOntology(PROJECT_ID, onto, DEFAULT_NS, "").run();
//        }
//        
//        listener.getTreeViewer().expandToLevel(2);
//        new CreateRootClazz(PROJECT_ID, onto, c1).run();
//        
//        Assert.assertTrue(manager.getOntology(onto).isPersistent());
//        
//        new RemoveOntology(PROJECT_ID, onto, false).run();
//        
//        if (manager.getAvailableOntologyURIs().contains(onto)) {
//            new AddOWLOntology(PROJECT_ID, new String[]{onto}).run();
//        } else if (manager.getOntology(onto) == null) {
//            new CreateOntology(PROJECT_ID, onto, DEFAULT_NS, "").run();
//        }
//        
//        int rootClazzCount = new GetRootClazzes(PROJECT_ID, onto).getResultCount();
//        Assert.assertEquals(1, rootClazzCount);
    }

}
