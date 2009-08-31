/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 21.06.2009
 * Created by: krekeler
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.util.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.obo.parser.OBOOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLOntologyOutputTarget;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.StringInputSource;
import org.semanticweb.owlapi.io.StringOutputTarget;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.uulm.ecs.ai.owlapi.krssparser.KRSS2OntologyFormat;

/**
 * @author krekeler
 *
 */
@SuppressWarnings("nls")
public class OWLFileUtilitiesTest {

    @Test
    public void testGetOntologyInfoForAnonymousOntology() throws Exception {
        Set<String> unsupportedFormats = new LinkedHashSet<String>();
        unsupportedFormats.add(new OWLXMLOntologyFormat().toString());
        unsupportedFormats.add(new KRSS2OntologyFormat().toString());
        unsupportedFormats.add(new ManchesterOWLSyntaxOntologyFormat().toString());
        unsupportedFormats.add(new OBOOntologyFormat().toString());
        unsupportedFormats.add(new TurtleOntologyFormat().toString());
        unsupportedFormats.add(new RDFXMLOntologyFormat().toString());

        Set<String> lateOntologyIDSetterFormats = new LinkedHashSet<String>();
        lateOntologyIDSetterFormats.add(new RDFXMLOntologyFormat().toString());
        
        
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = manager.getOWLDataFactory();
        OWLClass A = f.getOWLClass(IRI.create("http://test.org/ontology#A"));
        OWLClass B = f.getOWLClass(IRI.create("http://test.org/ontology#B"));
        OWLAxiom axiom = f.getOWLSubClassOfAxiom(A, B);
        
        OWLOntology ontology = manager.createOntology();
        manager.addAxiom(ontology, axiom);
        for (OWLOntologyFormat format: OWLFileUtilities.getParsableOntologyFormats()) {
            if (!unsupportedFormats.contains(format.toString())) {
                if (!lateOntologyIDSetterFormats.contains(format.toString())) {
                    testOntologyFormat(manager, ontology, format, 1);
                }
                testOntologyFormat(manager, ontology, format, -1);
            }
        }
    }

    @Test
    public void testGetOntologyInfo() throws Exception {
        Set<String> unsupportedFormats = new LinkedHashSet<String>();
        unsupportedFormats.add(new KRSS2OntologyFormat().toString());
        unsupportedFormats.add(new OBOOntologyFormat().toString());
        unsupportedFormats.add(new TurtleOntologyFormat().toString());
        unsupportedFormats.add(new OWLFunctionalSyntaxOntologyFormat().toString());

        Set<String> lateOntologyIDSetterFormats = new LinkedHashSet<String>();
        lateOntologyIDSetterFormats.add(new RDFXMLOntologyFormat().toString());
        
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = manager.getOWLDataFactory();
        OWLClass A = f.getOWLClass(IRI.create("http://test.org/ontology#A"));
        OWLClass B = f.getOWLClass(IRI.create("http://test.org/ontology#B"));
        OWLAxiom axiom = f.getOWLSubClassOfAxiom(A, B);
        
        OWLOntology ontology = manager.createOntology(IRI.create("http://test.org/ontology#"));
        manager.addAxiom(ontology, axiom);
        OWLOntology importingOntology = manager.createOntology(IRI.create("http://test.org/importingOntology#"));
        manager.addAxiom(importingOntology, axiom);
        manager.applyChange(new AddImport(importingOntology, f.getOWLImportsDeclaration(IRI.create("http://test.org/ontology#"))));
        Set<OWLOntologyFormat> parsableOntologyFormats = 
            OWLFileUtilities.getParsableOntologyFormats();
            //Collections.singleton((OWLOntologyFormat)new OWLFunctionalSyntaxOntologyFormat()); 
        for (OWLOntologyFormat format: parsableOntologyFormats) {
            if (!unsupportedFormats.contains(format.toString())) {
                if (!lateOntologyIDSetterFormats.contains(format.toString())) {
                    testOntologyFormat(manager, ontology, format, 1);
                    testOntologyFormat(manager, importingOntology, format, 1);
                }
                testOntologyFormat(manager, ontology, format, -1);
                testOntologyFormat(manager, importingOntology, format, -1);
            }
        }
    }
    
    private void testOntologyFormat(OWLOntologyManager manager, OWLOntology ontology, OWLOntologyFormat format, int addAxiomThreshold) throws Exception {
        OWLOntologyOutputTarget target = new StringOutputTarget();
        manager.saveOntology(ontology, format, target);
        
        OWLOntologyManager checkManager = OWLManager.createOWLOntologyManager();
        checkManager.setSilentMissingImportsHandling(true);
        checkManager.addIRIMapper(new OWLOntologyIRIMapper() {
            @Override
            public URI getPhysicalURI(IRI ontologyIRI) {
                try {
                    return new URI("unkownScheme", "unkownSPP", "unknownFragment");
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        OWLOntologyID expectedOntologyID =  checkManager.loadOntology(new StringInputSource(target.toString())).getOntologyID();
        if (expectedOntologyID.isAnonymous() && !ontology.getOntologyID().isAnonymous()
                || !expectedOntologyID.isAnonymous() && !expectedOntologyID.equals(ontology.getOntologyID())) {
            Logger.getLogger(getClass()).warn(String.format("Ontology format '%1$s' does not keep the ontology id. Original id: '%2$s'. Round tripped id: '%3$s'.", format.toString(), ontology.getOntologyID(), expectedOntologyID));
        }
        
        OWLOntologyInfo info = OWLFileUtilities.getOntologyInfo(new StringInputSource(target.toString()), addAxiomThreshold);
        if (expectedOntologyID.isAnonymous()) {
            assertTrue(info.getOntologyID().isAnonymous());
        } else {
            assertEquals(expectedOntologyID, info.getOntologyID());
        }
        assertEquals(format.toString(), info.getOntologyFormat().toString());
    }
}
