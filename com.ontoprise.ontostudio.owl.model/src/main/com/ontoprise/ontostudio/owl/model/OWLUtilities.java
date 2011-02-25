/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.coode.owlapi.functionalparser.ParseException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.SWRLRule;

import com.ontoprise.ontostudio.owl.model.util.Cast;
import com.ontoprise.ontostudio.owl.model.util.InternalParser;
import com.ontoprise.ontostudio.owl.model.util.InternalParserException;
import com.ontoprise.ontostudio.owl.model.util.InternalParserFunctionalSyntax;
import com.ontoprise.ontostudio.owl.model.util.OWLFunctionalSyntaxVisitor;

/**
 * 
 * @author Nico Stieler
 */

public class OWLUtilities {
    private static final IRI SWRL_RULE_IRI_IRI = IRI.create("http://www.semanticweb.org/owlapi#iri"); //$NON-NLS-1$
    private static final IRI OWL_THING_URI = IRI.create(OWLConstants.OWL_THING_URI);
    private static final IRI OWL_NOTHING_URI = IRI.create(OWLConstants.OWL_NOTHING_URI);
    
    public static boolean isOWLThing(OWLClassExpression description) {
        return description instanceof OWLClass && OWL_THING_URI.equals(((OWLClass)description).getIRI());
    }

    public static boolean isOWLNothing(OWLClassExpression description) {
        return description instanceof OWLClass && OWL_NOTHING_URI.equals(((OWLClass)description).getIRI());
    }
    
    /**
     * Creates a Java {@link URI} from an <b>correct</b> uri String. Otherwise this method will throw a exception.
     * 
     * <p>Boxes {@link URISyntaxException} in an {@link OWLRuntimeException}.
     *  
     * @param uri
     * @return
     */
    public static URI toURI(String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new OWLRuntimeException(e);
        }
    }
    public static IRI toIRI(String iri) {
        if (iri == null) {
            return null;
        }
        try {
            return IRI.create(iri);
        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof URISyntaxException) {
                throw new OWLRuntimeException(e.getCause());
            }
            throw new OWLRuntimeException(e);
        }
    }
    public static IRI owlFuntionalStyleSyntaxIRIToIRI(String iri, OWLOntology ontology) throws NeOnCoreException{
        return new InternalParserFunctionalSyntax(ontology).getIRI(iri);
    }
//  public static String IRIToOwlFuntionalStyleSyntaxIRI(IRI iri, OWLOntology ontology) throws NeOnCoreException{//NICO insert this method
//  return 
//}
//  public static String IRIToOwlFuntionalStyleSyntaxIRI(String iri, OWLOntology ontology) throws NeOnCoreException{//NICO insert this method
//  return toString(object, ontology);
//}
    /**
     * Returns true iff description represents an OWL restriction on a property.
     * 
     * @param description
     * @return
     */
    public static boolean isRestriction(OWLClassExpression description) {
        return (description instanceof OWLObjectSomeValuesFrom) || (description instanceof OWLObjectAllValuesFrom)
                || (description instanceof OWLObjectHasValue)
                || (description instanceof OWLObjectCardinalityRestriction)
                || (description instanceof OWLDataSomeValuesFrom)
                || (description instanceof OWLDataAllValuesFrom)
                || (description instanceof OWLDataHasValue)
                || (description instanceof OWLDataCardinalityRestriction)
                || (description instanceof OWLObjectHasSelf);
    }

    /**
     * Returns true iff description represents a named OWL class
     * 
     * @param description
     * @return
     */
    public static boolean isNamedClass(OWLClassExpression description) {
        return (description instanceof OWLClass);
    }

    private static <E extends OWLLiteral> E constant(String constant, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
        try {
//            new InternalParserFunctionalSyntax().getConstant(constant);
            return new InternalParser(constant, namespaces, factory).parseOWLConstant();//NICO insert lite 
            
        } catch (InternalParserException e) {
            throw new InternalNeOnException(e);
        }
    }
    public static OWLLiteral constant(String constant, OWLModel model) throws NeOnCoreException {
        return constant(constant, model.getNamespaces(), model.getOWLDataFactory());
    }
    /**
     * gets a textual representation of an axiom <br>
     * creates the appropriate axiom object for it
     * 
     * @param axiomText
     * @param ontology
     * @return
     * @throws NeOnCoreException
     */
    public static OWLAxiom axiom(String axiomText, OWLOntology ontology) throws NeOnCoreException {
        try {
            return new InternalParserFunctionalSyntax(ontology).getAxiom(axiomText);
        } catch (ParseException e) {
            throw new InternalNeOnException(e);
        }
    }
    /**
     * gets a textual representation of an description <br>
     * creates the appropriate description object for it
     * 
     * @param description
     * @param ontology
     * @return
     * @throws NeOnCoreException
     */
    public static OWLClassExpression description(String description, OWLOntology ontology) throws NeOnCoreException {
        try {
            return new InternalParserFunctionalSyntax(ontology).getDescription(description);
        } catch (ParseException e) {
            throw new InternalNeOnException(e);
        }
    }
    /**
     * gets a textual representation of an data range <br>
     * creates the appropriate data range object for it
     * 
     * @param dataRange
     * @param ontology
     * @return
     * @throws NeOnCoreException
     */
    public static OWLDataRange dataRange(String dataRange, OWLOntology ontology) throws NeOnCoreException {
        try {
            return new InternalParserFunctionalSyntax(ontology).getDataRange(dataRange);
        } catch (ParseException e) {
            throw new InternalNeOnException(e);
        }
    }
    /**
     * gets a textual representation of an individual <br>
     * creates the appropriate data individual for it
     * 
     * @param individual
     * @param ontology
     * @return
     * @throws NeOnCoreException
     */
    public static OWLIndividual individual(String individual, OWLOntology ontology) throws NeOnCoreException {
        try {
            return new InternalParserFunctionalSyntax(ontology).getIndividual(individual);
        } catch (ParseException e) {
            throw new InternalNeOnException(e);
        }
    }
    /**
     * gets a textual representation of an object property <br>
     * creates the appropriate data object property for it
     * 
     * @param objectProperty
     * @param ontology
     * @return
     * @throws NeOnCoreException
     */
    public static OWLObjectProperty objectProperty(String objectProperty, OWLOntology ontology) throws NeOnCoreException {
        try {
            return new InternalParserFunctionalSyntax(ontology).getObjectProperty(objectProperty);
        } catch (ParseException e) {
            throw new InternalNeOnException(e);
        }
    }
    /**
     * gets a textual representation of an annotation property <br>
     * creates the appropriate annotation property object for it
     * 
     * @param annotationProperty
     * @param ontology
     * @return
     * @throws NeOnCoreException
     */
    public static OWLAnnotationProperty annotationProperty(String annotationProperty, OWLOntology ontology) throws NeOnCoreException {
        try {
            return new InternalParserFunctionalSyntax(ontology).getAnnotationProperty(annotationProperty);
        } catch (ParseException e) {
            throw new InternalNeOnException(e);
        }
    }
    /**
     * gets a textual representation of an data property <br>
     * creates the appropriate data range property for it
     * 
     * @param dataProperty
     * @param ontology
     * @return
     * @throws NeOnCoreException
     */
    public static OWLDataProperty dataProperty(String dataProperty, OWLOntology ontology) throws NeOnCoreException {
        try {
            return new InternalParserFunctionalSyntax(ontology).getDataProperty(dataProperty);
        } catch (ParseException e) {
            throw new InternalNeOnException(e);
        }
    }
    public static String toString(OWLOntologyID id) {
        if(id.getDefaultDocumentIRI() == null ) return null;
        return id.getDefaultDocumentIRI().toURI().toString();
    }
    public static List<String> toString(List<OWLObjectPropertyExpression> chain, OWLOntology ontology) {//NICO TODO check usage
        List<String> result = new ArrayList<String>();
        for (OWLObjectPropertyExpression e: chain) {
            result.add(toString(e, ontology));
        }
        return result;
    }
    /**
     * gets an OWL object and creates the textual representation for it (OWL functional syntax)
     * 
     * @param object
     * @param buffer
     * @param ontology
     * @return 
     * @throws NeOnCoreException
     */
    public static String toString(OWLObject object, OWLOntology ontology) {
        StringBuffer buffer = new StringBuffer();
        toString(object, buffer, ontology);
        return buffer.toString();
    }
    /**
     * gets an OWL object and creates the textual representation for it (OWL functional syntax)
     * 
     * @param object
     * @param buffer
     * @param ontology
     * @throws NeOnCoreException
     */
    public static void toString(OWLObject object, StringBuffer buffer, OWLOntology ontology) {
        StringWriter writer = new StringWriter();
        object.accept(new OWLFunctionalSyntaxVisitor(writer, ontology));
        buffer.append(writer.toString());
    }
    /**
     * Get all descriptions from a set of <code>ClassMember</code> axioms which are actually named classes.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getDescription() == D and D is a named class}
     */
    public static Set<OWLClass> getClassMemberNamedDescriptions(Set<OWLClassAssertionAxiom> source) {
        Set<OWLClass> result = new HashSet<OWLClass>();
        for (OWLClassAssertionAxiom axiom: source) {
            if (axiom.getClassExpression() instanceof OWLClass) {
                result.add((OWLClass) axiom.getClassExpression());
            }
        }
        return result;
    }

    /**
     * Get all descriptions from a set of <code>ClassMember</code> axioms which are actually non named classes.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getDescription() == D and D is not a named class}
     */
    public static Set<OWLClassExpression> getClassMemberComplexDescriptions(Set<OWLClassAssertionAxiom> source) {
        Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();
        for (OWLClassAssertionAxiom axiom: source) {
            if (!(axiom.getClassExpression() instanceof OWLClass)) {
                result.add(axiom.getClassExpression());
            }
        }
        return result;
    }
    /**
     * Get all ranges from a set of <code>DataPropertyRange</code> axioms.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getRange() == D }
     */
    public static Set<OWLDataRange> getDataPropertyRangeRanges(Set<OWLDataPropertyRangeAxiom> source) {
        Set<OWLDataRange> result = new HashSet<OWLDataRange>();
        for (OWLDataPropertyRangeAxiom axiom: source) {
            result.add(axiom.getRange());
        }
        return result;
    }
    /**
     * Get all domains from a set of <code>DataPropertyDomain</code> axioms.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getDomain() == D }
     */
    public static Set<OWLClassExpression> getDataPropertyDomainDomains(Set<OWLDataPropertyDomainAxiom> source) {
        Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();
        for (OWLDataPropertyDomainAxiom axiom: source) {
            result.add(axiom.getDomain());
        }
        return result;
    }
    /**
     * Get all domains from a set of <code>DataPropertyDomain</code> axioms.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getDomain() == D }
     */
    public static Set<OWLClassExpression> getObjectPropertyDomainDomains(Set<OWLObjectPropertyDomainAxiom> source) {
        Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();
        for (OWLObjectPropertyDomainAxiom axiom: source) {
            result.add(axiom.getDomain());
        }
        return result;
    }
    /**
     * Get all individual from a set of <code>DifferentIndividuals</code> axioms.
     * 
     * @param source The list of axioms to extract items from.
     * @return { I | A in <code>source</code> and A.getIndividuals().contains(I) }
     */
    public static Set<OWLIndividual> getDifferentIndividualsIndividuals(Set<OWLDifferentIndividualsAxiom> source) {
        Set<OWLIndividual> result = new HashSet<OWLIndividual>();
        for (OWLDifferentIndividualsAxiom axiom: source) {
            result.addAll(axiom.getIndividuals());
        }
        return result;
    }
    /**
     * Get all descriptions from a set of <code>DisjointClasses</code> axioms.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getDescriptions().contains(D) }
     */
    public static Set<OWLClassExpression> getDisjointClassesDescriptions(Set<OWLDisjointClassesAxiom> source) {
        Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();
        for (OWLDisjointClassesAxiom axiom: source) {
            result.addAll(axiom.getClassExpressions());
        }
        return result;
    }
    /**
     * Get all descriptions from a set of <code>EquivalentClasses</code> axioms.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getDescriptions().contains(D)) }
     */
    public static Set<OWLClass> getEquivalentClassesNamedDescriptions(Set<OWLEquivalentClassesAxiom> source) {
        Set<OWLClass> result = new HashSet<OWLClass>();
        for (OWLEquivalentClassesAxiom axiom: source) {
            for (OWLClassExpression description: axiom.getClassExpressions()) {
                if (description instanceof OWLClass) {
                    result.add((OWLClass) description);
                }
            }
        }
        return result;
    }
    /**
     * Get all data properties from a set of <code>EquivalentDataProperties</code> axioms which are named data properties.
     * 
     * @param source The list of axioms to extract items from.
     * @return { D | A in <code>source</code> and A.getDataProperties().contains(D)) }
     */
    public static Set<OWLDataProperty> getEquivalentDataPropertiesNamedDataProperties(Set<OWLEquivalentDataPropertiesAxiom> source) {
        Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
        for (OWLEquivalentDataPropertiesAxiom axiom: source) {
            for (OWLDataPropertyExpression item: axiom.getProperties()) {
                if (item instanceof OWLDataPropertyExpression) {
                    result.add((OWLDataProperty) item);
                }
            }
        }
        return result;
    }
    /**
     * Gets the property for a description if this description is a restriction. Otherwise it return null.
     * 
     * @return the property for the passed description (if it has a property). Can be a list of property expressions (for property chains). Can be a data- or
     *         objectProperty for non chained property restrictions. Can be a objectPropertyExpression for inversed objectProperties in restrictions. Only
     *         restrictions do have properties, other descriptions return null
     */
    public static Object getProperty(OWLClassExpression description) {
        if (!isRestriction(description)) {
            // only restrictions do have properties, other descriptions return null
            return null;
        }

        if (description instanceof OWLDataAllValuesFrom) {
            return ((OWLDataAllValuesFrom) description).getProperty();

        } else if (description instanceof OWLDataCardinalityRestriction) {
            return ((OWLDataCardinalityRestriction) description).getProperty();

        } else if (description instanceof OWLDataHasValue) {
            return ((OWLDataHasValue) description).getProperty();

        } else if (description instanceof OWLDataSomeValuesFrom) {
            return ((OWLDataSomeValuesFrom) description).getProperty();

        } else if (description instanceof OWLObjectAllValuesFrom) {
            return ((OWLObjectAllValuesFrom) description).getProperty(); // MER: shouldn't we have a list here, too???

        } else if (description instanceof OWLObjectCardinalityRestriction) {
            return ((OWLObjectCardinalityRestriction) description).getProperty();

        } else if (description instanceof OWLObjectHasValue) {
            return ((OWLObjectHasValue) description).getProperty();

        } else if (description instanceof OWLObjectSomeValuesFrom) {
            return ((OWLObjectSomeValuesFrom) description).getProperty(); // MER: shouldn't we have a list here, too???

        } else if (description instanceof OWLObjectHasSelf) {
            return ((OWLObjectHasSelf) description).getProperty();

        } else {
            // must not reach this point
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    public static <E extends OWLEntity> Set<E> getReferencedEntities(OWLOntology ontology, Class<E> entityType) {
        if (OWLEntity.class.equals(entityType)) {
            Set<E> result = new LinkedHashSet<E>();
            ((Set)result).addAll(getReferencedEntities(ontology, OWLClass.class));
            ((Set)result).addAll(getReferencedEntities(ontology, OWLDataProperty.class));
            ((Set)result).addAll(getReferencedEntities(ontology, OWLNamedIndividual.class));
            ((Set)result).addAll(getReferencedEntities(ontology, OWLObjectProperty.class));
            ((Set)result).addAll(getReferencedEntities(ontology, OWLAnnotationProperty.class));
            return result;
        } else if (OWLClass.class.equals(entityType)) {
            return Cast.cast(ontology.getClassesInSignature());
        } else if (OWLDataProperty.class.equals(entityType)) {
            return Cast.cast(ontology.getDataPropertiesInSignature());
        } else if (OWLNamedIndividual.class.equals(entityType)) {
            return Cast.cast(ontology.getIndividualsInSignature());
        } else if (OWLObjectProperty.class.equals(entityType)) {
            return Cast.cast(ontology.getObjectPropertiesInSignature());
        } else if (OWLAnnotationProperty.class.equals(entityType)) {
            return Cast.cast(ontology.getAnnotationPropertiesInSignature());
        } else {
            throw new UnsupportedOperationException();
        }
    }
    public static IRI getIRI(SWRLRule rule) {
        for (OWLAnnotation a: rule.getAnnotations()) {
            if (SWRL_RULE_IRI_IRI.equals(a.getProperty().getIRI())) {
                if (a.getValue() instanceof OWLLiteral){
                    OWLLiteral owlLiteral = (OWLLiteral)a.getValue();
                    if(owlLiteral.isRDFPlainLiteral()) { 
                        String literal = owlLiteral.getLiteral();
                        if (literal != null && literal.startsWith("<") && literal.endsWith(">")) { //$NON-NLS-1$ //$NON-NLS-2$
                            return IRI.create(literal.substring(1, literal.length() - 2));
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }
    
    //NICO start remove

//    private static <E extends OWLAxiom> E axiom(String axiomText, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
//        try {
//            return new InternalParser(axiomText, namespaces, factory).parseOWLAxiom();
//        } catch (InternalParserException e) {
//           throw new InternalNeOnException(e);
//        }
//    }
//    private static <E extends OWLClassExpression> E description(String description, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
//        try {
//            return new InternalParser(description, namespaces, factory).parseOWLDescription();
//        } catch (InternalParserException e) {
//            throw new InternalNeOnException(e);
//        }
//    }   

//  private static <E extends OWLDataRange> E dataRange(String dataRange, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
//      try {
//          return new InternalParser(dataRange, namespaces, factory).parseOWLDataRange();
//      } catch (InternalParserException e) {
//          throw new InternalNeOnException(e);
//      }
//  }
//  public static OWLAxiom axiom(String axiomText, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) throws NeOnCoreException {
////      return axiom(axiomText, namespaces, factory);
//      return axiom(axiomText, model);
//  }
//  public static OWLClassExpression description(String description, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) throws NeOnCoreException {
////    return description(description, namespaces, factory);
//    return description(description, model);
//}
//  public static OWLLiteral constant(String constant, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) throws NeOnCoreException {
////      return constant(constant, namespaces, factory);
//      return constant(constant, model);
//  }
//  public static OWLDataRange dataRange(String dataRange, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) throws NeOnCoreException {
////      return dataRange(dataRange, namespaces, factory);
//      return dataRange(dataRange, model);
//   }
//  public static OWLAxiom axiom(String axiomText, OWLModel model) throws NeOnCoreException {
//      return axiom(axiomText, model.getOntology());
//  }
//  public static OWLClassExpression description(String description, OWLModel model) throws NeOnCoreException {
//      return description(description, model.getOntology());
//  }
    

//  public static OWLDataRange dataRange(String dataRange, OWLModel model) throws NeOnCoreException {
//      return dataRange(dataRange, model.getOntology());
//  }

//  public static OWLLiteral constant(String constant, OWLOntology ontology) throws NeOnCoreException {
//      //NICO using OWLSyntax: parser
//      try {
//          return new InternalParserFunctionalSyntax(ontology).getConstant(constant);
//      } catch (ParseException e) {
//          throw new InternalNeOnException(e);
//      }
//  }
//    private static OWLIndividual individual(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory) {
//        return factory.getOWLNamedIndividual(toIRI(namespaces.expandString(unexpandedURI)));
//    }
//    public static OWLObjectProperty objectPropertyFromURI(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory) {
//        return factory.getOWLObjectProperty(toIRI(namespaces.expandString(unexpandedURI)));
//    }
//    public static OWLAnnotationProperty annotationPropertyFromURI(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory) {
//        return factory.getOWLAnnotationProperty(toIRI(namespaces.expandString(unexpandedURI)));
//    }
//    public static OWLDataProperty dataPropertyFromURI(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory) {
//        return factory.getOWLDataProperty(toIRI(namespaces.expandString(unexpandedURI)));
//    }
//
//    private static OWLIndividual individual(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) 
//    throws NeOnCoreException {
//        return individual(unexpandedURI, namespaces, factory, model.getOntology());
////        return individual(unexpandedURI, namespaces, factory);
//    }
//    private static OWLObjectProperty objectProperty(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) 
//    throws NeOnCoreException {
//      return objectProperty(unexpandedURI, namespaces, factory, model.getOntology());
////      return objectProperty(unexpandedURI, namespaces, factory);
//    }
//    private static OWLAnnotationProperty annotationProperty(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) 
//    throws NeOnCoreException {
//        return annotationProperty(unexpandedURI, namespaces, factory, model.getOntology());
////        return annotationProperty(unexpandedURI, namespaces, factory);
//    }
//    private static OWLDataProperty dataProperty(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory, OWLModel model) 
//    throws NeOnCoreException {
//        return dataProperty(unexpandedURI, namespaces, factory, model.getOntology());
////        return dataProperty(unexpandedURI, namespaces, factory);
//    }
//    private static OWLIndividual individual(String individual, OWLNamespaces namespaces, OWLDataFactory factory, OWLOntology ontology) throws NeOnCoreException {
//        return individual(individual, ontology);
//    }
//    public static OWLObjectProperty objectProperty(String objectProperty, OWLNamespaces namespaces, OWLDataFactory factory, OWLOntology ontology) throws NeOnCoreException {
//        return objectProperty(objectProperty, ontology);
//    }
//    public static OWLAnnotationProperty annotationProperty(String annotationProperty, OWLNamespaces namespaces, OWLDataFactory factory, OWLOntology ontology) throws NeOnCoreException {
//        return annotationProperty(annotationProperty, ontology);
//    }
//    public static OWLDataProperty dataProperty(String dataProperty, OWLNamespaces namespaces, OWLDataFactory factory, OWLOntology ontology) throws NeOnCoreException {
//        return dataProperty(dataProperty, ontology);
//    }
//    public static OWLIndividual individual(String individual, OWLOntology ontology) throws NeOnCoreException {
//        //NICO using OWLSyntax: parser
//        try {
//            InternalParserFunctionalSyntax p = new InternalParserFunctionalSyntax(ontology);
//            OWLIndividual indi =  p.getIndividual(individual);
//            String x = toString(indi, ontology);
//            System.out.println(x);
//            System.out.println(p.getIRI(x));
//            System.out.println("done");
//            
//            return indi;
//        } catch (ParseException e) {
//            throw new InternalNeOnException(e);
//        }
//    }

//  private static String toString(OWLObject object) {//NICO TODO check usage
//      return toString(object, (OWLNamespaces)OWLNamespaces.INSTANCE);
//  }
//  private static String toString(OWLObject object, OWLNamespaces namespaces) {//NICO TODO check usage
//      StringBuffer buffer = new StringBuffer();
//      toString((OWLObject)object, buffer, namespaces);
//      return buffer.toString();
//  }

//  public static void toString(OWLObject object, StringBuffer buffer) { //NICO unused CODE
//      toString(object, buffer, (OWLNamespaces)OWLNamespaces.INSTANCE);
//  }
//  private static void toString(OWLObject object, StringBuffer buffer, OWLNamespaces namespaces) {//NICO TODO check usage
//      object.accept(new OWLFormattingVisitor(buffer, namespaces));
//  }
//
//  // methods to use while testing
//  public static String toString(OWLObject object, OWLModel model, OWLModel model2) {
////      return toString(object);
//      return toString(object, model);
//  }
//  public static String toString(OWLObject object, OWLOntology ontology, OWLOntology model2) {
////      return toString(object);
//      return toString(object, ontology);
//  }
//  public static String toString(OWLObject object, OWLNamespaces namespaces, OWLModel model) {
////      return toString(object, namespaces);
//      return toString(object, model);
//  }
//  public static String toString(OWLObject object, OWLNamespaces namespaces, OWLOntology ontology) {
////      return toString(object, namespaces);
//      return toString(object, ontology);
//  }
//  public static void toString(OWLObject object, StringBuffer buffer, OWLNamespaces namespaces, OWLModel model) {
//      toString(object, buffer, model);
//  }
//  // methods you should use
//  public static String toString(OWLObject object, OWLModel model) {
//      StringBuffer buffer = new StringBuffer();
//      toString(object, buffer, model);
//      return buffer.toString();
//  }

//  public static void toString(OWLObject object, StringBuffer buffer, OWLModel model) {
//  try {
//      toString(object, buffer, model.getOntology());
//  } catch (NeOnCoreException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//  }
//}
//    /**
//     * @param string
//     */
//    private static void test(OWLObject object, String text, OWLOntology ontology) {
//        text = IRIUtils.ensureValidIRISyntax(text);
//        try {
//            if(object instanceof OWLAxiom){
//                OWLAxiom object2 = axiom(text, ontology);
//                if(!object2.equals(object)){
//                    System.err.println(object2 + " != " + object); //$NON-NLS-1$
//                }
//            }else if(object instanceof OWLClassExpression){
//                OWLClassExpression object2 = description(text, ontology);
//                if(!object2.equals(object)){
//                    System.err.println(object2 + " != " + object); //$NON-NLS-1$
//                }
//            }else if(object instanceof OWLDataRange){
//                OWLDataRange object2 = dataRange(text, ontology);
//                if(!object2.equals(object)){
//                    System.err.println(object2 + " != " + object); //$NON-NLS-1$
//                }
//            }else if(object instanceof OWLIndividual){
//                OWLIndividual object2 = individual(text, ontology);
//                if(!object2.equals(object)){
//                    System.err.println(object2 + " != " + object); //$NON-NLS-1$
//                }
//            }else if(object instanceof OWLObjectProperty){
//                OWLObjectProperty object2 = objectProperty(text, ontology);
//                if(!object2.equals(object)){
//                    System.err.println(object2 + " != " + object); //$NON-NLS-1$
//                }
//            }else if(object instanceof OWLAnnotationProperty){
//                OWLAnnotationProperty object2 = annotationProperty(text, ontology);
//                if(!object2.equals(object)){
//                    System.err.println(object2 + " != " + object); //$NON-NLS-1$
//                }
//            }else if(object instanceof OWLDataProperty){
//                OWLDataProperty object2 = dataProperty(text, ontology);
//                if(!object2.equals(object)){
//                    System.err.println(object2 + " != " + object); //$NON-NLS-1$
//                }
//            }else{
//                System.err.println(object + "does not fit to these Types"); //$NON-NLS-1$
//            }
//        } catch (NeOnCoreException e) {
//            System.err.println(text + " throws exception");
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}
