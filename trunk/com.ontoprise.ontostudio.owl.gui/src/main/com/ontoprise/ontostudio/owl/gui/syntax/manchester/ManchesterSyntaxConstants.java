/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.syntax.manchester;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLFacet;

public abstract class ManchesterSyntaxConstants {

    /*
     * Class Constructors
     */
    public static final String OR = "or"; //$NON-NLS-1$
    public static final String AND = "and"; //$NON-NLS-1$
    public static final String NOT = "not"; //$NON-NLS-1$

    /*
     * Restrictions
     */
    public static final String MIN = "min"; //$NON-NLS-1$
    public static final String MAX = "max"; //$NON-NLS-1$
    public static final String EXACTLY = "exactly"; //$NON-NLS-1$
//    public static final String CARDINALITY = "cardinality"; //$NON-NLS-1$
//    public static final String AT_LEAST = "atLeast"; //$NON-NLS-1$
//    public static final String AT_MOST = "atMost"; //$NON-NLS-1$

    public static final String SOME_VALUES_FROM = "some"; //$NON-NLS-1$
    public static final String ALL_VALUES_FROM = "only"; //$NON-NLS-1$
    public static final String HAS_VALUE = "value"; //$NON-NLS-1$

    public static final String INVERSE_OF = "inverseOf"; //$NON-NLS-1$
    public static final String SELF = "Self"; //$NON-NLS-1$

    /*
     * Facettes
     */
    public static final String LENGTH = "length"; //$NON-NLS-1$
    public static final String MINLENGTH = "minLength"; //$NON-NLS-1$
    public static final String MAXLENGTH = "maxLength"; //$NON-NLS-1$
    public static final String PATTERN = "pattern"; //$NON-NLS-1$
    public static final String EQ = "="; //$NON-NLS-1$
    public static final String LTE = "<="; //$NON-NLS-1$
    public static final String LT = "<"; //$NON-NLS-1$
    public static final String GTE = ">="; //$NON-NLS-1$
    public static final String GT = ">"; //$NON-NLS-1$
    public static final String DIGITS = "digits"; //$NON-NLS-1$
    public static final String FRACTION = "fraction"; //$NON-NLS-1$

    /*
     * Brackets
     */
    public static final String OPENING_BRACKET = "["; //$NON-NLS-1$
    public static final String CLOSING_BRACKET = "]"; //$NON-NLS-1$
    public static final String OPENING_PARANTHESIS = "("; //$NON-NLS-1$
    public static final String CLOSING_PARANTHESIS = ")"; //$NON-NLS-1$
    public static final String OPENING_BRACE = "{"; //$NON-NLS-1$
    public static final String CLOSING_BRACE = "}"; //$NON-NLS-1$

    /*
     * Ontology keywords
     */
    public static final String ONTOLOGY = "Ontology:"; //$NON-NLS-1$
    public static final String NAMESPACE = "Namespace:"; //$NON-NLS-1$
    public static final String IMPORT = "Import:"; //$NON-NLS-1$

    public static final String ANNOTATIONS = "Annotations:"; //$NON-NLS-1$

    /*
     * Frame keywords
     */
    public static final String EQUIVALENTCLASSES = "EquivalentClasses:"; //$NON-NLS-1$
    public static final String DISJOINTCLASSES = "DisjointClasses:"; //$NON-NLS-1$
    public static final String EQUIVALENTOBJECTPROPERTIES = "EquivalentObjectProperties:"; //$NON-NLS-1$
    public static final String DISJOINTOBJECTPROPERTIES = "DisjointObjectProperties:"; //$NON-NLS-1$
    public static final String EQUIVALENTDATAPROPERTIES = "EquivalentDataProperties:"; //$NON-NLS-1$
    public static final String DISJOINTDATAPROPERTIES = "DisjointDataProperties:"; //$NON-NLS-1$
    public static final String SAMEINDIVIDUAL = "SameIndividual:"; //$NON-NLS-1$
    public static final String DIFFERENTINDIVIDUALS = "DifferentIndividuals:"; //$NON-NLS-1$

    public static final String CLASS = "Class:"; //$NON-NLS-1$
    public static final String SUBCLASSOF = "SubClassOf:"; //$NON-NLS-1$
    public static final String EQUIVALENTTO = "EquivalentTo:"; //$NON-NLS-1$
    public static final String DISJOINTWITH = "DisjointWith:"; //$NON-NLS-1$
    public static final String DISJOINTUNIONOF = "DisjointUnionOf:"; //$NON-NLS-1$

    public static final String OBJECTPROPERTY = "ObjectProperty:"; //$NON-NLS-1$
    public static final String DOMAIN = "Domain:"; //$NON-NLS-1$
    public static final String RANGE = "Range:"; //$NON-NLS-1$
    public static final String CHARACTERISTIC = "Characteristics:"; //$NON-NLS-1$
    public static final String SUBPROPERTTYOF = "SubPropertyOf:"; //$NON-NLS-1$
    public static final String INVERSES = "Inverses:"; //$NON-NLS-1$
    public static final String SUBPROPERTYCHAIN = "SubPropertyChain:"; //$NON-NLS-1$

    public static final String FUNCTIONAL = "Functional"; //$NON-NLS-1$
    public static final String INVERSEFUNCTIONAL = "InverseFunctional"; //$NON-NLS-1$
    public static final String REFLEXIVE = "Reflexive"; //$NON-NLS-1$
    public static final String IRREFLEXIVE = "Irreflexive"; //$NON-NLS-1$
    public static final String SYMMETRIC = "Symmetric"; //$NON-NLS-1$
    public static final String ASYMMETRIC = "Asymmetric"; //$NON-NLS-1$
    public static final String TRANSITIVE = "Transitive"; //$NON-NLS-1$

    public static final String O = "o"; //$NON-NLS-1$

    public static final String DATAPROPERTY = "DataProperty:"; //$NON-NLS-1$

    public static final String DATATYPE = "Datatype:"; //$NON-NLS-1$

    public static final String INDIVIDUAL = "Individual:"; //$NON-NLS-1$
    public static final String TYPES = "Types:"; //$NON-NLS-1$
    public static final String FACTS = "Facts:"; //$NON-NLS-1$
    public static final String SAMEAS = "SameAs:"; //$NON-NLS-1$
    public static final String DIFFERENTFROM = "DifferentFrom:"; //$NON-NLS-1$

    public static final String ANNOTATIONPROPERTY = "OWLAnnotationProperty:"; //$NON-NLS-1$

    /*
     * Other
     */
    // "@"
    // "^^"
    // ","
    // ":"
    public static String[] getClassConstructorKeywords() {
        return new String[] {OR, AND, NOT};
    }

    public static String[] getRestrictionKeywords() {
        return new String[] {MIN, MAX, EXACTLY, SOME_VALUES_FROM, ALL_VALUES_FROM, HAS_VALUE, INVERSE_OF, SELF};
    }

    public static String[] getBrackets() {
        return new String[] {OPENING_BRACKET, CLOSING_BRACKET, OPENING_PARANTHESIS, CLOSING_PARANTHESIS, OPENING_BRACE, CLOSING_BRACE};
    }

    public static String[] getFacetKeywords() {
        return new String[] {LENGTH, MINLENGTH, MAXLENGTH, PATTERN, DIGITS, FRACTION};
    }

    public static String[] getFacets() {
        return new String[] {EQ, LTE, LT, GTE, GT};
    }

    public static String[] getFramesKeywords() {
        return new String[] {ONTOLOGY, NAMESPACE, IMPORT, CLASS, OBJECTPROPERTY, DATAPROPERTY, INDIVIDUAL, EQUIVALENTCLASSES, DISJOINTCLASSES, EQUIVALENTOBJECTPROPERTIES, EQUIVALENTDATAPROPERTIES, EQUIVALENTDATAPROPERTIES, DISJOINTDATAPROPERTIES, SAMEINDIVIDUAL, DIFFERENTINDIVIDUALS};
    }

    public static String[] getFrameSlotKeywords() {
        return new String[] {ANNOTATIONS,
                SUBCLASSOF,
                EQUIVALENTTO,
                DISJOINTWITH,
                DISJOINTUNIONOF,
                DOMAIN,
                RANGE,
                CHARACTERISTIC,
                SUBPROPERTTYOF,
                INVERSES,
                SUBPROPERTYCHAIN,
                FUNCTIONAL,
                INVERSEFUNCTIONAL,
                REFLEXIVE,
                IRREFLEXIVE,
                SYMMETRIC,
                ASYMMETRIC,
                TRANSITIVE,
                O,
                TYPES,
                FACTS,
                SAMEAS,
                DIFFERENTFROM};
    }

    public static boolean isClassConstructorKeyword(String keyword) {
        String[] array = getClassConstructorKeywords();
        return checkKeyword(keyword, array);
    }

    public static boolean isRestrictionKeyword(String keyword) {
        String[] array = getRestrictionKeywords();
        return checkKeyword(keyword, array);
    }

    public static boolean isFacetKeyword(String keyword) {
        String[] array = getFacetKeywords();
        return checkKeyword(keyword, array);
    }

    public static boolean isFacet(String keyword) {
        String[] array = getFacets();
        return checkKeyword(keyword, array);
    }

    public static boolean isBracket(String text) {
        String[] brackets = getBrackets();
        return checkKeyword(text, brackets);
    }

    private static boolean checkKeyword(String keyword, String[] array) {
        if (keyword == null) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            if (keyword.equals(array[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param facet
     * @return
     */
    public static String getFacetString(OWLFacet facet) {
        String facetString;
        if (facet == OWLFacet.LENGTH) {
            facetString = LENGTH;
        } else if (facet == OWLFacet.MIN_LENGTH) {
            facetString = MINLENGTH;
        } else if (facet == OWLFacet.MAX_LENGTH) {
            facetString = MAXLENGTH;
        } else if (facet == OWLFacet.PATTERN) {
            facetString = PATTERN;
        } else if (facet == OWLFacet.MIN_INCLUSIVE) {
            facetString = LTE;
        } else if (facet == OWLFacet.MIN_EXCLUSIVE) {
            facetString = LT;
        } else if (facet == OWLFacet.MAX_INCLUSIVE) {
            facetString = GTE;
        } else if (facet == OWLFacet.MAX_EXCLUSIVE) {
            facetString = GT;
        } else if (facet == OWLFacet.TOTAL_DIGITS) {
            facetString = DIGITS;
        } else {
            facetString = FRACTION;
        }
        return facetString;
    }

    public static String getPropertyAttributeString(OWLObjectPropertyCharacteristicAxiom axiom) {
        if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
            return FUNCTIONAL;
        } else if (axiom instanceof OWLAsymmetricObjectPropertyAxiom) {
            return ASYMMETRIC;
        } else if (axiom instanceof OWLInverseFunctionalObjectPropertyAxiom) {
            return INVERSEFUNCTIONAL;
        } else if (axiom instanceof OWLIrreflexiveObjectPropertyAxiom) {
            return IRREFLEXIVE;
        } else if (axiom instanceof OWLReflexiveObjectPropertyAxiom) {
            return REFLEXIVE;
        } else if (axiom instanceof OWLSymmetricObjectPropertyAxiom) {
            return SYMMETRIC;
        } else if (axiom instanceof OWLTransitiveObjectPropertyAxiom) {
            return TRANSITIVE;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public static String getPropertyAttributeString(OWLDataPropertyCharacteristicAxiom axiom) {
        if (axiom instanceof OWLFunctionalDataPropertyAxiom) {
            return FUNCTIONAL;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param object
     * @return
     */
    public static String getEntityTypeString(OWLEntity object) {
        String entityType;
        if (object instanceof OWLClass) {
            entityType = ManchesterSyntaxConstants.CLASS;
        } else if (object instanceof OWLObjectProperty) {
            entityType = ManchesterSyntaxConstants.OBJECTPROPERTY;
        } else if (object instanceof OWLDataProperty) {
            entityType = ManchesterSyntaxConstants.DATAPROPERTY;
        } else if (object instanceof OWLIndividual) {
            entityType = ManchesterSyntaxConstants.INDIVIDUAL;
        } else if (object instanceof OWLAnnotationProperty) {
            entityType = ManchesterSyntaxConstants.ANNOTATIONPROPERTY;
        } else if (object instanceof OWLDatatype) {
            // should not reach this point
            return null;
        } else {
            // should not reach this point
            return null;
        }
        return entityType;
    }

}
