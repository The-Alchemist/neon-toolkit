/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
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
    public void test() throws NeOnCoreException{
        String ontologyIRI = "http://test.org/ontology#"; //$NON-NLS-1$
//        ontology.
//        PrefixManager pm = new DefaultPrefixManager(ontologyIRI);

        String AString = ontologyIRI + "A"; //$NON-NLS-1$
        String BString = ontologyIRI + "B"; //$NON-NLS-1$
        String CString = ontologyIRI + "C"; //$NON-NLS-1$
        String DString = ontologyIRI + "D"; //$NON-NLS-1$
        
        OWLClass A = _f.getOWLClass(IRI.create(AString));
        OWLClass B = _f.getOWLClass(IRI.create(BString));
        OWLClass C = _f.getOWLClass(IRI.create(CString));
        OWLClass D = _f.getOWLClass(IRI.create(DString));
        OWLClassExpression E1 = OWLUtilities.description( "ObjectIntersectionOf(" + AString + " " + BString + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println(E1.toString());
    }
    @Test
    public void addToImportedOntologies(){
        String ontologyURI ="http://test.org/ontology#";
        try {
            OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = ontologyManager.createOntology(IRI.create(ontologyURI));
            OWLDataFactory factory = OWLManager.getOWLDataFactory();
        ontologyManager.applyChange(new AddImport(ontology, factory.getOWLImportsDeclaration(IRI.create(ontologyURI + "2"))));
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }
}
