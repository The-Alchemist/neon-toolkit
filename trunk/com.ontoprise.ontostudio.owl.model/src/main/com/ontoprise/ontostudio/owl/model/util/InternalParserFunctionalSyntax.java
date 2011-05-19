/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.model.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxParser;
import org.coode.owlapi.functionalparser.ParseException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;

/**
 * @author Nico Stieler
 * Created on: 21.01.2011
 */
public class InternalParserFunctionalSyntax {

    private static OWLOntology ontology;
    /**
     * @return the ontology
     */
    public static OWLOntology getOntology() {
        return ontology;
    }


    private static OWLOntologyManager ontologyManager;
    private static OWLOntologyFormat ontologyFormat;
    static{ 
        ontologyManager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = ontologyManager.getOWLDataFactory();
        try {
            ontology = ontologyManager.createOntology(IRI.create("http://www.test.org/test/testontologies/testontology")); //$NON-NLS-1$
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }         
        ontologyFormat = new RDFXMLOntologyFormat();

    }
    
    private OWLFunctionalSyntaxParser parser;
    /**
     * @param model
     */
    public InternalParserFunctionalSyntax() {
    }

    /**
     * @param axiomText
     * @throws NeOnCoreException 
     */
    private void prepareParser(String input) throws NeOnCoreException {
        updateInput(input);
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        this.parser.setUp(ontologyManager, ontology, new OWLOntologyLoaderConfiguration());
        this.parser.setPrefixes(new ManchesterOWLSyntaxPrefixNameShortFormProvider(ontologyFormat).getPrefixManager());
    }
    /**
     * @param axiomText
     * @throws NeOnCoreException 
     */
    private void updateInput(String input) throws NeOnCoreException {
        try {
            this.parser = 
                new OWLFunctionalSyntaxParser(
                        new ByteArrayInputStream(input.getBytes("UTF-8"))); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new InternalNeOnException("problems with parsing the given String: " + input, e); //$NON-NLS-1$
        }
    }
    

    /**
     * @param axiomText
     * @throws NeOnCoreException 
     */
    public IRI getIRI(String string) throws NeOnCoreException {
        prepareParser(string);
        return parser.getIRI(string);
    }
    
    /**
     * @param axiomText
     * @return
     * @throws NeOnCoreException 
     * @throws ParseException 
     */
    public OWLAxiom getAxiom(String axiom) throws NeOnCoreException, ParseException {
         prepareParser(axiom);
         return parser.Axiom();
    }
    /**
     * @param description
     * @return
     * @throws NeOnCoreException 
     * @throws ParseException 
     */
    public OWLClassExpression getDescription(String description) throws NeOnCoreException, ParseException {
        prepareParser(description);
        return parser.ClassExpression();
    }

    /**
     * @param constant
     * @return
     */
    public OWLLiteral getConstant(String constant) throws NeOnCoreException, ParseException {
        prepareParser(constant);
        return parser.Literal();
    }


    /**
     * @param dataRange
     * @return
     */
    public OWLDataRange getDataRange(String dataRange) throws NeOnCoreException, ParseException {
        prepareParser(dataRange);
        return parser.DataRange();
    }


    /**
     * @param unexpandedURI
     * @return
     * @throws ParseException 
     * @throws NeOnCoreException 
     */
    public OWLIndividual getIndividual(String individual) throws ParseException, NeOnCoreException{
        prepareParser(individual);
        return parser.Individual();
    }


    /**
     * @param unexpandedURI
     * @return
     * @throws ParseException 
     * @throws NeOnCoreException 
     */
    public OWLObjectProperty getObjectProperty(String objectProperty) throws ParseException, NeOnCoreException{
        prepareParser(objectProperty);
        return parser.ObjectPropertyIRI();
    }


    /**
     * @param unexpandedURI
     * @return
     * @throws NeOnCoreException 
     * @throws ParseException 
     */
    public OWLDataProperty getDataProperty(String dataProperty) throws NeOnCoreException, ParseException {
        prepareParser(dataProperty);
        return parser.DataPropertyIRI();
    }


    /**
     * @param annotationProperty
     * @return
     * @throws NeOnCoreException 
     * @throws ParseException 
     */
    public OWLAnnotationProperty getAnnotationProperty(String annotationProperty) throws NeOnCoreException, ParseException {
        annotationProperty = ifItDoesNotStartWithItShouldStartWith("AnnotationProperty ", annotationProperty); //$NON-NLS-1$
        prepareParser(annotationProperty);
        return parser.AnnotationProperty();
    }


    /**
     * @param string
     */
    private String ifItDoesNotStartWithItShouldStartWith(String start, String value) {
        if(value.startsWith(start)){
            return value;
        }else{
            return start + "(" + value+ ")"; //$NON-NLS-1$//$NON-NLS-2$
        }
    }

}
