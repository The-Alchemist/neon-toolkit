/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.visitors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

/**
 * Default implementation of the visitor containing an empty method for each object.
 * 
 * @author Nico Stieler
 */
public class OWLKAON2VisitorAdapter implements OWLObjectVisitorEx<Object> {
    public Object visit(OWLAnnotationProperty object) {
        return null;
    }
    public Object visit(OWLDataProperty object) {
        return null;
    }
    public Object visit(OWLObjectProperty object) {
        return null;
    }
    public Object visit(OWLDatatype object) {
        return null;
    }
    public Object visit(OWLClass object) {
        return null;
    }
    public Object visit(OWLObjectInverseOf object) {
        return null;
    }
    public Object visit(OWLLiteral object) {
        return null;
    }
    public Object visit(OWLDataComplementOf object) {
        return null;
    }
    public Object visit(OWLDataOneOf object) {
        return null;
    }
    public Object visit(OWLDataAllValuesFrom object) {
        return null;
    }
    public Object visit(OWLDataSomeValuesFrom object) {
        return null;
    }
    public Object visit(OWLDatatypeRestriction object) {
        return null;
    }
    public Object visit(OWLDataCardinalityRestriction object) {
        return null;
    }
    public Object visit(OWLDataHasValue object) {
        return null;
    }
    public Object visit(OWLObjectAllValuesFrom object) {
        return null;
    }
    public Object visit(OWLObjectSomeValuesFrom object) {
        return null;
    }
    public Object visit(OWLObjectHasSelf object) {
        return null;
    }
    public Object visit(OWLObjectCardinalityRestriction object) {
        return null;
    }
    public Object visit(OWLObjectOneOf object) {
        return null;
    }
    public Object visit(OWLObjectHasValue object) {
        return null;
    }
    public Object visit(OWLObjectComplementOf object) {
        return null;
    }
    public Object visit(OWLObjectUnionOf object) {
        return null;
    }
    public Object visit(OWLObjectIntersectionOf object) {
        return null;
    }
    public Object visit(OWLSubClassOfAxiom object) {
        return null;
    }
    public Object visit(OWLEquivalentClassesAxiom object) {
        return null;
    }
    public Object visit(OWLDisjointClassesAxiom object) {
        return null;
    }
    public Object visit(OWLDisjointUnionAxiom object) {
        return null;
    }
    public Object visit(OWLDataPropertyCharacteristicAxiom object) {
        return null;
    }
    public Object visit(OWLDataPropertyDomainAxiom object) {
        return null;
    }
    public Object visit(OWLDataPropertyRangeAxiom object) {
        return null;
    }
    public Object visit(OWLSubDataPropertyOfAxiom object) {
        return null;
    }
    public Object visit(OWLEquivalentDataPropertiesAxiom object) {
        return null;
    }
    public Object visit(OWLDisjointDataPropertiesAxiom object) {
        return null;
    }
    public Object visit(OWLObjectPropertyCharacteristicAxiom object) {
        return null;
    }
    public Object visit(OWLObjectPropertyDomainAxiom object) {
        return null;
    }
    public Object visit(OWLObjectPropertyRangeAxiom object) {
        return null;
    }
    public Object visit(OWLSubObjectPropertyOfAxiom object) {
        return null;
    }
    public Object visit(OWLEquivalentObjectPropertiesAxiom object) {
        return null;
    }
    public Object visit(OWLDisjointObjectPropertiesAxiom object) {
        return null;
    }
    public Object visit(OWLInverseObjectPropertiesAxiom object) {
        return null;
    }
    public Object visit(OWLSameIndividualAxiom object) {
        return null;
    }
    public Object visit(OWLDifferentIndividualsAxiom object) {
        return null;
    }
    public Object visit(OWLDataPropertyAssertionAxiom object) {
        return null;
    }
    public Object visit(OWLNegativeDataPropertyAssertionAxiom object) {
        return null;
    }
    public Object visit(OWLObjectPropertyAssertionAxiom object) {
        return null;
    }
    public Object visit(OWLNegativeObjectPropertyAssertionAxiom object) {
        return null;
    }
    public Object visit(OWLClassAssertionAxiom object) {
        return null;
    }
    public Object visit(OWLAnnotationAssertionAxiom object) {
        return null;
    }
    public Object visit(OWLDeclarationAxiom object) {
        return null;
    }
    @Override
    public Object visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLFunctionalDataPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLSubPropertyChainOfAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(SWRLRule rule) {
        return null;
    }
    @Override
    public Object visit(OWLObjectMinCardinality desc) {
        return null;
    }
    @Override
    public Object visit(OWLObjectExactCardinality desc) {
        return null;
    }
    @Override
    public Object visit(OWLObjectMaxCardinality desc) {
        return null;
    }
    @Override
    public Object visit(OWLDataMinCardinality desc) {
        return null;
    }
    @Override
    public Object visit(OWLDataExactCardinality desc) {
        return null;
    }
    @Override
    public Object visit(OWLDataMaxCardinality desc) {
        return null;
    }
    @Override
    public Object visit(OWLFacetRestriction node) {
        return null;
    }
    @Override
    public Object visit(SWRLClassAtom node) {
        return null;
    }
    @Override
    public Object visit(SWRLDataRangeAtom node) {
        return null;
    }
    @Override
    public Object visit(SWRLObjectPropertyAtom node) {
        return null;
    }
    @Override
    public Object visit(SWRLDataPropertyAtom node) {
        return null;
    }
    @Override
    public Object visit(SWRLBuiltInAtom node) {
        return null;
    }
    @Override
    public Object visit(SWRLVariable node) {
        return null;
    }
    @Override
    public Object visit(SWRLIndividualArgument node) {
        return null;
    }
    @Override
    public Object visit(SWRLLiteralArgument node) {
        return null;
    }
    @Override
    public Object visit(SWRLSameIndividualAtom node) {
        return null;
    }
    @Override
    public Object visit(SWRLDifferentIndividualsAtom node) {
        return null;
    }
    @Override
    public Object visit(OWLOntology ontology) {
        return null;
    }
    @Override
    public Object visit(OWLHasKeyAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLDatatypeDefinitionAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return null;
    }
    @Override
    public Object visit(OWLDataIntersectionOf node) {
        return null;
    }
    @Override
    public Object visit(OWLDataUnionOf node) {
        return null;
    }
    @Override
    public Object visit(OWLNamedIndividual individual) {
        return null;
    }
    @Override
    public Object visit(OWLAnnotation node) {
        return null;
    }
    @Override
    public Object visit(IRI iri) {
        return null;
    }
    @Override
    public Object visit(OWLAnonymousIndividual individual) {
        return null;
    }
}
