/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.model;

import org.junit.Test;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * @author Nico Stieler
 * Created on: 16.02.2011
 */
public class testOWLModelCore {
    private static final OWLDataFactory _f = OWLManager.createOWLOntologyManager().getOWLDataFactory();
    
    @Test
    public void test() throws OWLOntologyCreationException, NeOnCoreException{
        String ontologyIRI = "http://test.org/ontology#";
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.createOntology(IRI.create(ontologyIRI)); 
//        ontology.
//        PrefixManager pm = new DefaultPrefixManager(ontologyIRI);

        String AString = ontologyIRI + "A";
        String BString = ontologyIRI + "B";
        String CString = ontologyIRI + "C";
        String DString = ontologyIRI + "D";
        
        OWLClass A = _f.getOWLClass(IRI.create(AString));
        OWLClass B = _f.getOWLClass(IRI.create(BString));
        OWLClass C = _f.getOWLClass(IRI.create(CString));
        OWLClass D = _f.getOWLClass(IRI.create(DString));
        OWLClassExpression E1 = OWLUtilities.description( "ObjectIntersectionOf(" + AString + " " + BString + ")", ontology);
        System.out.println(E1.toString());
    }
}
