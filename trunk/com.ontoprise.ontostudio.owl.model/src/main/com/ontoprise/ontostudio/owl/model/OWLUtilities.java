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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLStringLiteral;
import org.semanticweb.owlapi.model.SWRLRule;

import com.ontoprise.ontostudio.owl.model.util.Cast;
import com.ontoprise.ontostudio.owl.model.util.InternalParser;
import com.ontoprise.ontostudio.owl.model.util.InternalParserException;
import com.ontoprise.ontostudio.owl.model.util.OWLFormattingVisitor;

public class OWLUtilities {
    private static final URI SWRL_RULE_IRI_URI = URI.create("http://www.semanticweb.org/owlapi#iri"); //$NON-NLS-1$
    private static final URI OWL_THING_URI;
    static {
        try {
            OWL_THING_URI = new URI(OWLConstants.OWL_THING_URI);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private static final URI OWL_NOTHING_URI;
    static {
        try {
            OWL_NOTHING_URI = new URI(OWLConstants.OWL_NOTHING_URI);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean isOWLThing(OWLClassExpression description) {
        return description instanceof OWLClass && OWL_THING_URI.equals(((OWLClass)description).getURI());
    }

    public static boolean isOWLNothing(OWLClassExpression description) {
        return description instanceof OWLClass && OWL_NOTHING_URI.equals(((OWLClass)description).getURI());
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

    /**
     * gets a textual repr. of an axiom <br>
     * [subClassOf InterestingPizza [and Pizza [all hasTopping [or FruitTopping HerbSpiceTopping NutTopping SauceTopping VegetableTopping CheeseTopping]]]] and
     * creates the appropriate Axiom object for it
     * 
     * @param axiomText
     * @param namespaces
     * @return
     * @throws NeOnCoreException
     */
    public static <E extends OWLAxiom> E axiom(String axiomText, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
        try {
            return new InternalParser(axiomText, namespaces, factory).parseOWLAxiom();
        } catch (InternalParserException e) {
           throw new InternalNeOnException(e);
        }
    }
    public static <E extends OWLClassExpression> E description(String description, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
        try {
            return new InternalParser(description, namespaces, factory).parseOWLDescription();
        } catch (InternalParserException e) {
            throw new InternalNeOnException(e);
        }
    }
    public static <E extends OWLLiteral> E constant(String constant, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
        try {
            return new InternalParser(constant, namespaces, factory).parseOWLConstant();
        } catch (InternalParserException e) {
            throw new InternalNeOnException(e);
        }
    }
    public static <E extends OWLDataRange> E dataRange(String dataRange, OWLNamespaces namespaces, OWLDataFactory factory) throws NeOnCoreException {
        try {
            return new InternalParser(dataRange, namespaces, factory).parseOWLDataRange();
        } catch (InternalParserException e) {
            throw new InternalNeOnException(e);
        }
    }
    
    public static OWLIndividual individual(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory) {
        return factory.getOWLNamedIndividual(toURI(namespaces.expandString(unexpandedURI)));
    }
    public static OWLObjectProperty objectProperty(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory) {
        return factory.getOWLObjectProperty(toURI(namespaces.expandString(unexpandedURI)));
    }
    public static OWLDataProperty dataProperty(String unexpandedURI, OWLNamespaces namespaces, OWLDataFactory factory) {
        return factory.getOWLDataProperty(toURI(namespaces.expandString(unexpandedURI)));
    }

    public static String toString(OWLOntologyID id) {
        if(id.getDefaultDocumentIRI() == null ) return null;
        return id.getDefaultDocumentIRI().toURI().toString();
    }
    
    public static List<String> toString(List<OWLObjectPropertyExpression> chain) {
        List<String> result = new ArrayList<String>();
        for (OWLObjectPropertyExpression e: chain) {
            result.add(toString(e));
        }
        return result;
    }
    
    public static String toString(OWLObject object) {
        return toString(object, (OWLNamespaces)OWLNamespaces.INSTANCE);
    }
    public static String toString(OWLObject object, OWLNamespaces namespaces) {
        StringBuffer buffer = new StringBuffer();
        toString((OWLObject)object, buffer, namespaces);
        return buffer.toString();
    }

    public static void toString(OWLObject object, StringBuffer buffer) {
        toString(object, buffer, (OWLNamespaces)OWLNamespaces.INSTANCE);
    }
    public static void toString(OWLObject object, StringBuffer buffer, OWLNamespaces namespaces) {
        object.accept(new OWLFormattingVisitor(buffer, namespaces));
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

    public static <E extends OWLEntity> Set<E> getReferencedEntities(OWLOntology ontology, Class<E> entityType) {
        if (OWLEntity.class.equals(entityType)) {
            return Cast.cast(ontology.getReferencedEntities());
        } else if (OWLClass.class.equals(entityType)) {
            return Cast.cast(ontology.getReferencedClasses());
        } else if (OWLDataProperty.class.equals(entityType)) {
            return Cast.cast(ontology.getReferencedDataProperties());
        } else if (OWLIndividual.class.equals(entityType)) {
            return Cast.cast(ontology.getReferencedIndividuals());
        } else if (OWLObjectProperty.class.equals(entityType)) {
            return Cast.cast(ontology.getReferencedObjectProperties());
        } else if (OWLAnnotationProperty.class.equals(entityType)) {
            return Cast.cast(ontology.getReferencedAnnotationProperties());
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    public static IRI getIRI(SWRLRule rule) {
        for (OWLAnnotation a: rule.getAnnotations()) {
            if (SWRL_RULE_IRI_URI.equals(a.getProperty().getURI())) {
                if (a.getValue() instanceof OWLStringLiteral) {
                    String literal = ((OWLStringLiteral)a.getValue()).getLiteral();
                    if (literal != null && literal.startsWith("<") && literal.endsWith(">")) { //$NON-NLS-1$ //$NON-NLS-2$
                        return IRI.create(literal.substring(1, literal.length() - 2));
                    }
                }
                return null;
            }
        }
        return null;
    }
}
