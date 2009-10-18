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
import org.semanticweb.owlapi.model.OWLIndividual;
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
import org.semanticweb.owlapi.model.OWLStringLiteral;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTypedLiteral;
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


/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
public class GetInterfaceTypeVisitor implements OWLObjectVisitorEx<Object> {

    public Object visit(OWLIndividual object) {
        return OWLIndividual.class;
    }
    public Object visit(OWLAnnotationProperty object) {
        return OWLAnnotationProperty.class;
    }
    public Object visit(OWLDataProperty object) {
        return OWLDataProperty.class;
    }
    public Object visit(OWLObjectProperty object) {
        return OWLObjectProperty.class;
    }
    public Object visit(OWLDatatype object) {
        return OWLDatatype.class;
    }
    public Object visit(OWLClass object) {
        return OWLClass.class;
    }
    public Object visit(OWLObjectInverseOf object) {
        return OWLObjectInverseOf.class;
    }
    public Object visit(OWLLiteral object) {
        return OWLLiteral.class;
    }
    public Object visit(OWLDataComplementOf object) {
        return OWLDataComplementOf.class;
    }
    public Object visit(OWLDataOneOf object) {
        return OWLDataOneOf.class;
    }
    public Object visit(OWLDatatypeRestriction object) {
        return OWLDatatypeRestriction.class;
    }
    public Object visit(OWLDataAllValuesFrom object) {
        return OWLDataAllValuesFrom.class;
    }
    public Object visit(OWLDataSomeValuesFrom object) {
        return OWLDataSomeValuesFrom.class;
    }
    public Object visit(OWLDataCardinalityRestriction object) {
        return OWLDataCardinalityRestriction.class;
    }
    public Object visit(OWLDataHasValue object) {
        return OWLDataHasValue.class;
    }
    public Object visit(OWLObjectAllValuesFrom object) {
        return OWLObjectAllValuesFrom.class;
    }
    public Object visit(OWLObjectSomeValuesFrom object) {
        return OWLObjectSomeValuesFrom.class;
    }
    public Object visit(OWLObjectHasSelf object) {
        return OWLObjectHasSelf.class;
    }
    public Object visit(OWLObjectCardinalityRestriction object) {
        return OWLObjectCardinalityRestriction.class;
    }
    public Object visit(OWLObjectOneOf object) {
        return OWLObjectOneOf.class;
    }
    public Object visit(OWLObjectHasValue object) {
        return OWLObjectHasValue.class;
    }
    public Object visit(OWLObjectComplementOf object) {
        return OWLObjectComplementOf.class;
    }
    public Object visit(OWLObjectUnionOf object) {
        return OWLObjectUnionOf.class;
    }
    public Object visit(OWLObjectIntersectionOf object) {
        return OWLObjectIntersectionOf.class;
    }
    public Object visit(OWLSubClassOfAxiom object) {
        return OWLSubClassOfAxiom.class;
    }
    public Object visit(OWLEquivalentClassesAxiom object) {
        return OWLEquivalentClassesAxiom.class;
    }
    public Object visit(OWLDisjointClassesAxiom object) {
        return OWLDisjointClassesAxiom.class;
    }
    public Object visit(OWLDisjointUnionAxiom object) {
        return OWLDisjointUnionAxiom.class;
    }
    public Object visit(OWLDataPropertyCharacteristicAxiom object) {
        return OWLDataPropertyCharacteristicAxiom.class;
    }
    public Object visit(OWLDataPropertyDomainAxiom object) {
        return OWLDataPropertyDomainAxiom.class;
    }
    public Object visit(OWLDataPropertyRangeAxiom object) {
        return OWLDataPropertyRangeAxiom.class;
    }
    public Object visit(OWLSubDataPropertyOfAxiom object) {
        return OWLSubDataPropertyOfAxiom.class;
    }
    public Object visit(OWLEquivalentDataPropertiesAxiom object) {
        return OWLEquivalentDataPropertiesAxiom.class;
    }
    public Object visit(OWLDisjointDataPropertiesAxiom object) {
        return OWLDisjointDataPropertiesAxiom.class;
    }
    public Object visit(OWLObjectPropertyCharacteristicAxiom object) {
        return OWLObjectPropertyCharacteristicAxiom.class;
    }
    public Object visit(OWLObjectPropertyDomainAxiom object) {
        return OWLObjectPropertyDomainAxiom.class;
    }
    public Object visit(OWLObjectPropertyRangeAxiom object) {
        return OWLObjectPropertyRangeAxiom.class;
    }
    public Object visit(OWLSubObjectPropertyOfAxiom object) {
        return OWLSubObjectPropertyOfAxiom.class;
    }
    public Object visit(OWLEquivalentObjectPropertiesAxiom object) {
        return OWLEquivalentObjectPropertiesAxiom.class;
    }
    public Object visit(OWLDisjointObjectPropertiesAxiom object) {
        return OWLDisjointObjectPropertiesAxiom.class;
    }
    public Object visit(OWLInverseObjectPropertiesAxiom object) {
        return OWLInverseObjectPropertiesAxiom.class;
    }
    public Object visit(OWLSameIndividualAxiom object) {
        return OWLSameIndividualAxiom.class;
    }
    public Object visit(OWLDifferentIndividualsAxiom object) {
        return OWLDifferentIndividualsAxiom.class;
    }
    public Object visit(OWLDataPropertyAssertionAxiom object) {
        return OWLDataPropertyAssertionAxiom.class;
    }
    public Object visit(OWLNegativeDataPropertyAssertionAxiom object) {
        return OWLNegativeDataPropertyAssertionAxiom.class;
    }
    public Object visit(OWLObjectPropertyAssertionAxiom object) {
        return OWLObjectPropertyAssertionAxiom.class;
    }
    public Object visit(OWLNegativeObjectPropertyAssertionAxiom object) {
        return OWLNegativeObjectPropertyAssertionAxiom.class;
    }
    public Object visit(OWLClassAssertionAxiom object) {
        return OWLClassAssertionAxiom.class;
    }
    public Object visit(OWLAnnotationAssertionAxiom object) {
        return OWLAnnotationAssertionAxiom.class;
    }
    public Object visit(OWLDeclarationAxiom object) {
        return OWLDeclarationAxiom.class;
    }
    @Override
    public Object visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return OWLAsymmetricObjectPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return OWLReflexiveObjectPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return OWLFunctionalObjectPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return OWLSymmetricObjectPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLFunctionalDataPropertyAxiom axiom) {
        return OWLFunctionalDataPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return OWLTransitiveObjectPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return OWLIrreflexiveObjectPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return OWLInverseFunctionalObjectPropertyAxiom.class;
    }
    @Override
    public Object visit(OWLSubPropertyChainOfAxiom axiom) {
        return OWLSubPropertyChainOfAxiom.class;
    }
    @Override
    public Object visit(SWRLRule rule) {
        return SWRLRule.class;
    }
    @Override
    public Object visit(OWLObjectMinCardinality desc) {
        return OWLObjectMinCardinality.class;
    }
    @Override
    public Object visit(OWLObjectExactCardinality desc) {
        return OWLObjectExactCardinality.class;
    }
    @Override
    public Object visit(OWLObjectMaxCardinality desc) {
        return OWLObjectMaxCardinality.class;
    }
    @Override
    public Object visit(OWLDataMinCardinality desc) {
        return OWLDataMinCardinality.class;
    }
    @Override
    public Object visit(OWLDataExactCardinality desc) {
        return OWLDataExactCardinality.class;
    }
    @Override
    public Object visit(OWLDataMaxCardinality desc) {
        return OWLDataMaxCardinality.class;
    }
    @Override
    public Object visit(OWLTypedLiteral node) {
        return OWLTypedLiteral.class;
    }
    @Override
    public Object visit(OWLStringLiteral node) {
        return OWLStringLiteral.class;
    }
    @Override
    public Object visit(OWLFacetRestriction node) {
        return OWLFacetRestriction.class;
    }
    @Override
    public Object visit(SWRLClassAtom node) {
        return SWRLClassAtom.class;
    }
    @Override
    public Object visit(SWRLDataRangeAtom node) {
        return SWRLDataRangeAtom.class;
    }
    @Override
    public Object visit(SWRLObjectPropertyAtom node) {
        return SWRLObjectPropertyAtom.class;
    }
    @Override
    public Object visit(SWRLDataPropertyAtom node) {
        return SWRLDataPropertyAtom.class;
    }
    @Override
    public Object visit(SWRLBuiltInAtom node) {
        return SWRLBuiltInAtom.class;
    }
    @Override
    public Object visit(SWRLVariable node) {
        return SWRLVariable.class;
    }
    @Override
    public Object visit(SWRLIndividualArgument node) {
        return SWRLIndividualArgument.class;
    }
    @Override
    public Object visit(SWRLLiteralArgument node) {
        return SWRLLiteralArgument.class;
    }
    @Override
    public Object visit(SWRLSameIndividualAtom node) {
        return SWRLSameIndividualAtom.class;
    }
    @Override
    public Object visit(SWRLDifferentIndividualsAtom node) {
        return SWRLDifferentIndividualsAtom.class;
    }
    @Override
    public Object visit(OWLOntology ontology) {
        return OWLOntology.class;
    }
    @Override
    public Object visit(OWLHasKeyAxiom axiom) {
        return OWLHasKeyAxiom.class;
    }
    @Override
    public Object visit(OWLDatatypeDefinitionAxiom axiom) {
        return OWLDatatypeDefinitionAxiom.class;
    }
    @Override
    public Object visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return OWLSubAnnotationPropertyOfAxiom.class;
    }
    @Override
    public Object visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return OWLAnnotationPropertyDomainAxiom.class;
    }
    @Override
    public Object visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return OWLAnnotationPropertyRangeAxiom.class;
    }
    @Override
    public Object visit(OWLDataIntersectionOf node) {
        return OWLDataIntersectionOf.class;
    }
    @Override
    public Object visit(OWLDataUnionOf node) {
        return OWLDataUnionOf.class;
    }
    @Override
    public Object visit(OWLNamedIndividual individual) {
        return OWLNamedIndividual.class;
    }
    @Override
    public Object visit(OWLAnnotation node) {
        return OWLAnnotation.class;
    }
    @Override
    public Object visit(IRI iri) {
        return IRI.class;
    }
    @Override
    public Object visit(OWLAnonymousIndividual individual) {
        return OWLAnonymousIndividual.class;
    }
}
