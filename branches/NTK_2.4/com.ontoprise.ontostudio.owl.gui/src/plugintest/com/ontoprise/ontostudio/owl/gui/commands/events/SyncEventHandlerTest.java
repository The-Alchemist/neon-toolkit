/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands.events;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.commands.AbstractOWLPluginTest;

/**
 * @author krekeler
 *
 */
@Ignore("Migration problems")
public class SyncEventHandlerTest extends AbstractOWLPluginTest {

    public SyncEventHandlerTest(Properties properties) {
        super(properties);
    }
    
    /**
     * Related to issue 11936.
     */
    @Test
    @Ignore("Migration problems")
    public void testIssue11936() throws Exception {
//        final boolean[] modelChangedCalled = new boolean[1];
//        final boolean[] syncExecCalled = new boolean[1];
//        
//        OWLModel model = OWLModelFactory.getOWLModel(ONTOLOGY_URI, PROJECT_ID);
//        OWLAxiomListener axiomListener = new OWLAxiomListener() {
//            @Override
//            public void modelChanged(OWLChangeEvent event) {
//                modelChangedCalled[0] = true;
//            }
//        };
//        model.addAxiomListener(axiomListener, new Class[] { OWLAxiom.class });
//        try {
//            OntologyManager manager = OWLModelFactory.getKaon2Connection(PROJECT_ID);
//            DatamodelListener listener = new DatamodelListener() {
//                @Override
//                public void handleEvent(DatamodelEvent event) {
//                    Display.getDefault().syncExec(new Runnable() {
//                        @Override
//                        public void run() {
//                            syncExecCalled[0] = true;
//                        }
//                    });
//                }
//            };
//            manager.addDatamodelListener(listener, null);
//            try {
//                String c1 = createQualifiedIdentifier("c1", DEFAULT_NS); //$NON-NLS-1$
//                new CreateRootClazz(PROJECT_ID, ONTOLOGY_URI, c1).run();
//
//                assertUnsortedArrayEquals(new String[] {c1}, new GetRootClazzes(PROJECT_ID, ONTOLOGY_URI).getResults());
//                assertTrue(modelChangedCalled[0]);
//                assertTrue(syncExecCalled[0]);
//            } finally {
//                manager.removeDatamodelListener(listener);
//            }
//        } finally {
//            model.removeAxiomListener(axiomListener);
//        }
    }
}
