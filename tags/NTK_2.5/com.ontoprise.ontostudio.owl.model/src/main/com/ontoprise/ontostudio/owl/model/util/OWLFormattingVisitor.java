/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.*;

import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * 
 * @author Nico Stieler
 */
public class OWLFormattingVisitor implements OWLObjectVisitorEx<Object> {
    private static final IRI OWL_THING_IRI = IRI.create(OWLConstants.OWL_THING_URI);
    
    private Appendable m_target;
    private OWLNamespaces m_namespaces;
    private static String escapeString(String string) {
        StringBuffer buffer=new StringBuffer();
        for (int i=0;i<string.length();i++) {
            char c=string.charAt(i);
            if (c=='"')
                buffer.append("\\\""); //$NON-NLS-1$
            else if (c=='\\')
                buffer.append("\\\\"); //$NON-NLS-1$
            else
                buffer.append(c);
        }
        return buffer.toString();
    }
    public OWLFormattingVisitor(Appendable target,OWLNamespaces namespaces) {
        m_target=target;
        m_namespaces=namespaces;
        if (m_namespaces==null)
            m_namespaces=OWLNamespaces.EMPTY_INSTANCE;
    }
    private void append(String string) {
        try {
            m_target.append(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void append(StringBuffer buffer) {
        try {
            m_target.append(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void append(char c) {
        try {
            m_target.append(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object visit(OWLNamedIndividual object) {
        visit(object.getIRI());
        return null;
    }

    public Object visit(OWLAnnotationProperty object) {
        visit(object.getIRI());
        return null;
    }

    public Object visit(OWLDataProperty object) {
        visit(object.getIRI());
        return null;
    }

    public Object visit(OWLObjectProperty object) {
        visit(object.getIRI());
        return null;
    }

    public Object visit(OWLDatatype object) {
        visit(object.getIRI());
        return null;
    }

    public Object visit(OWLClass object) {
        visit(object.getIRI());
        return null;
    }

    public Object visit(OWLObjectInverseOf object) {
        append("[inv "); //$NON-NLS-1$
        object.getInverse().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLLiteral object) {
        String literal = escapeString(object.getLiteral());
        OWLDatatype datatype = object.getDatatype();
        if(object.isRDFPlainLiteral()) {
            String lang = object.getLang();
            append('"');
            append(literal);
            append('"');
            if(!lang.isEmpty()){
                append('@');
                append(lang);
            }
        }else{
            append('"');
            append(literal);
            append("\"^^<"); //$NON-NLS-1$
            append(datatype.getIRI().toString());
            append('>');
        }
        return null;
    }

    public Object visit(OWLDataComplementOf object) {
        append("[not "); //$NON-NLS-1$
        object.getDataRange().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataOneOf object) {
        append("[oneOf"); //$NON-NLS-1$
        for (OWLLiteral literalValue : object.getValues()) {
            append(' ');
            literalValue.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDatatypeRestriction object) {
        append("[datatypeRestriction "); //$NON-NLS-1$
        object.getDatatype().accept(this);
        for (OWLFacetRestriction facetRestriction: object.getFacetRestrictions()) {
            append(' ');
            visit(facetRestriction);
        }
        append(']');
        return null;
    }
    
    public Object visit(OWLFacetRestriction object) {
        append("[facetRestriction "); //$NON-NLS-1$
        append(object.getFacet().toString());
        append(' ');
        StringBuffer buffer=new StringBuffer();
        object.getFacetValue().accept(this);
        append(buffer);
        append(']');
        return null;
    }

    public Object visit(OWLDataAllValuesFrom object) {
        append("[dataAll"); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataSomeValuesFrom object) {
        append("[dataSome"); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataCardinalityRestriction object) {
        if (object instanceof OWLDataMinCardinality) {
            append("[dataAtLeast "); //$NON-NLS-1$
        } else if (object instanceof OWLDataMaxCardinality) {
            append("[dataAtMost "); //$NON-NLS-1$
        } else if (object instanceof OWLDataExactCardinality) {
            append("[dataExactly "); //$NON-NLS-1$
        } else {
            throw new IllegalStateException("Invalid cardinality type."); //$NON-NLS-1$
        }
        append(Integer.toString(object.getCardinality()));
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataHasValue object) {
        append("[dataHasValue "); //$NON-NLS-1$
        object.getProperty().accept(this);
        append(' ');
        object.getValue().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectAllValuesFrom object) {
        append("[all "); //$NON-NLS-1$
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectSomeValuesFrom object) {
        append("[some "); //$NON-NLS-1$
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectHasSelf object) {
        append("[self "); //$NON-NLS-1$
        object.getProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectCardinalityRestriction object) {
        OWLClassExpression m_description=object.getFiller();
        if (object instanceof OWLObjectMinCardinality) {
            append("[atLeast "); //$NON-NLS-1$
        } else if (object instanceof OWLObjectMaxCardinality) {
            append("[atMost "); //$NON-NLS-1$
        } else if (object instanceof OWLObjectExactCardinality) {
            append("[exactly "); //$NON-NLS-1$
        } else {
            throw new IllegalStateException("Invalid cardinality type."); //$NON-NLS-1$
        }
        append(Integer.toString(object.getCardinality()));
        append(' ');
        object.getProperty().accept(this);
        if (!isOWLThing(m_description)) {
            append(' ');
            m_description.accept(this);
        }
        append(']');
        return null;
    }
    
    private boolean isOWLThing(OWLClassExpression description) {
        return description instanceof OWLClass && OWL_THING_IRI.equals(((OWLClass)description).getIRI());
    }

    public Object visit(OWLObjectOneOf object) {
        append("[oneOf"); //$NON-NLS-1$
        for (OWLIndividual individual : object.getIndividuals()) {
            append(' ');
            individual.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLObjectHasValue object) {
        append("[hasValue "); //$NON-NLS-1$
        object.getProperty().accept(this);
        append(' ');
        object.getValue().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectComplementOf object) {
        append("[not "); //$NON-NLS-1$
        object.getOperand().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectUnionOf object) {
        append("[or"); //$NON-NLS-1$
        for (OWLClassExpression description : object.getOperands()) {
            append(' '); 
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLObjectIntersectionOf object) {
        append("[and"); //$NON-NLS-1$
        for (OWLClassExpression description : object.getOperands()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLSubClassOfAxiom object) {
        append("["); //$NON-NLS-1$
        appendAnnotations("subClassOf", object); //$NON-NLS-1$
        append(' ');
        object.getSubClass().accept(this);
        append(' ');
        object.getSuperClass().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLEquivalentClassesAxiom object) {
        append("["); //$NON-NLS-1$
        appendAnnotations("equivalent", object); //$NON-NLS-1$
        for (OWLClassExpression description : object.getClassExpressions()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointClassesAxiom object) {
        append('[');
        appendAnnotations("disjoint", object); //$NON-NLS-1$
        for (OWLClassExpression description : object.getClassExpressions()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointUnionAxiom object) {
        append("["); //$NON-NLS-1$
        appendAnnotations("disjointUnion", object); //$NON-NLS-1$
        append(' ');
        object.getOWLClass().accept(this);
        for (OWLClassExpression description : object.getClassExpressions()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }
    
    private void appendAnnotations(String head, OWLAxiom object){
        if(!object.getAnnotations().isEmpty()){
            append('{');
        
            for(OWLAnnotation annotation : object.getAnnotations()){
                append(' ');
                annotation.accept(this);
            }
            append('}');
        }
        append(head);
    }

    public Object visit(OWLDataPropertyCharacteristicAxiom object) {
        if (object instanceof OWLFunctionalDataPropertyAxiom){
            append('[');
            appendAnnotations("dataFunctional", object); //$NON-NLS-1$
            append(' ');
        }else
            append("[invalid attribute type!"); //$NON-NLS-1$
        object.getProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataPropertyDomainAxiom object) {
        append('[');
        appendAnnotations("dataDomain", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getDomain().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataPropertyRangeAxiom object) {
        append('[');
        appendAnnotations("dataRange", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getRange().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLSubDataPropertyOfAxiom object) {
        append('[');
        appendAnnotations("subDataPropertyOf", object); //$NON-NLS-1$
        append(' ');
        object.getSubProperty().accept(this);
        append(' ');
        object.getSuperProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLEquivalentDataPropertiesAxiom object) {
        append('[');
        appendAnnotations("dataEquivalent", object); //$NON-NLS-1$
        for (OWLDataPropertyExpression dataProperty : object.getProperties()) {
            append(' ');
            dataProperty.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointDataPropertiesAxiom object) {
        append('[');
        appendAnnotations("dataDisjoint", object); //$NON-NLS-1$
        for (OWLDataPropertyExpression dataProperty : object.getProperties()) {
            append(' ');
            dataProperty.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLObjectPropertyCharacteristicAxiom object) {
        append('[');
        String head = null;
        if (object instanceof OWLFunctionalObjectPropertyAxiom)
            head = "objectFunctional"; //$NON-NLS-1$
        else if (object instanceof OWLInverseFunctionalObjectPropertyAxiom)
            head = "objectInverseFunctional"; //$NON-NLS-1$
        else if (object instanceof OWLSymmetricObjectPropertyAxiom)
            head = "objectSymmetric"; //$NON-NLS-1$
        else if (object instanceof OWLTransitiveObjectPropertyAxiom)
            head = "objectTransitive"; //$NON-NLS-1$
        else if (object instanceof OWLReflexiveObjectPropertyAxiom)
            head = "objectReflexive"; //$NON-NLS-1$
        else if (object instanceof OWLIrreflexiveObjectPropertyAxiom)
            head = "objectIrreflexive"; //$NON-NLS-1$
        else if (object instanceof OWLAsymmetricObjectPropertyAxiom)
            head = "objectAsymmetric"; //$NON-NLS-1$
        else
            append("invalid attribute type!"); //$NON-NLS-1$
        
        appendAnnotations(head, object);
        append(' ');
        object.getProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectPropertyDomainAxiom object) {
        append('[');
        appendAnnotations("objectDomain", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getDomain().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectPropertyRangeAxiom object) {
        append('[');
        appendAnnotations("objectRange", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getRange().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLSubObjectPropertyOfAxiom object) {
        append('[');
        appendAnnotations("subObjectPropertyOf", object); //$NON-NLS-1$
        append(' ');
        object.getSubProperty().accept(this);
        append(' ');
        object.getSuperProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLEquivalentObjectPropertiesAxiom object) {
        append('[');
        appendAnnotations("objectEquivalent", object); //$NON-NLS-1$
        for (OWLObjectPropertyExpression objectProperty : object.getProperties()) {
            append(' ');
            objectProperty.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointObjectPropertiesAxiom object) {
        append('[');
        appendAnnotations("objectDisjoint", object); //$NON-NLS-1$
        for (OWLObjectPropertyExpression objectProperty : object.getProperties()) {
            append(' ');
            objectProperty.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLInverseObjectPropertiesAxiom object) {
        append('[');
        appendAnnotations("objectInverse", object); //$NON-NLS-1$
        append(' ');
        object.getFirstProperty().accept(this);
        append(' ');
        object.getSecondProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLSameIndividualAxiom object) {
        append('[');
        appendAnnotations("same", object); //$NON-NLS-1$
        for (OWLIndividual individual : object.getIndividuals()) {
            append(' ');
            individual.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDifferentIndividualsAxiom object) {
        append('[');
        appendAnnotations("different", object); //$NON-NLS-1$
        for (OWLIndividual individual : object.getIndividuals()) {
            append(' ');
            individual.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDataPropertyAssertionAxiom object) {
        append('[');
        appendAnnotations("dataMember", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getSubject().accept(this);
        append(' ');
        object.getObject().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLNegativeDataPropertyAssertionAxiom object) {
        append('[');
        appendAnnotations("negativeDataMember", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getSubject().accept(this);
        append(' ');
        object.getObject().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectPropertyAssertionAxiom object) {
        append('[');
        appendAnnotations("objectMember", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getSubject().accept(this);
        append(' ');
        object.getObject().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLNegativeObjectPropertyAssertionAxiom object) {
        append('[');
        appendAnnotations("negativeObjectMember", object); //$NON-NLS-1$
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getSubject().accept(this);
        append(' ');
        object.getObject().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLClassAssertionAxiom object) {
        append('[');
        appendAnnotations("classMember", object); //$NON-NLS-1$
        append(' ');
        object.getClassExpression().accept(this);
        append(' ');
        object.getIndividual().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLAnnotation object) {
        append('[');
        object.getProperty().accept(this);
        append(' ');
        OWLAnnotationValue value = object.getValue();
        if (value instanceof IRI || value instanceof OWLAnonymousIndividual) {
            append('[');
            value.accept(this);
            append(']');
        }
        else {
            value.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLAnnotationAssertionAxiom object) { //TODO
        String head = null;
        OWLAnnotationSubject subject=object.getSubject();
        if (subject instanceof OWLDataProperty)
            head = "dataAnnotation"; //$NON-NLS-1$
        else if (subject instanceof OWLObjectProperty)
            head = "objectAnnotation"; //$NON-NLS-1$
        else if (subject instanceof OWLNamedIndividual)
            head = "namedIndividualAnnotation"; //$NON-NLS-1$
        else if (subject instanceof OWLClass)
            head = "classAnnotation"; //$NON-NLS-1$
        else if (subject instanceof OWLDatatype)
            head = "datatypeAnnotation"; //$NON-NLS-1$
        else if (subject instanceof OWLAnnotationProperty)
            head = "annotationAnnotationProperty"; //$NON-NLS-1$
        else if (subject instanceof OWLAnonymousIndividual)
            head = "annotationAnonymousIndividual"; //$NON-NLS-1$
        else if (subject instanceof IRI)
            head = "annotationIRI"; //$NON-NLS-1$
        else if (subject instanceof OWLAnnotation)
            head = "annotationAnnotation"; //$NON-NLS-1$
        else
            throw new IllegalStateException("Unknown entity type. "+subject.getClass().getName()); //$NON-NLS-1$
        
        append('[');
        appendAnnotations(head, object);
        append(' ');
        
        OWLAnnotation annotation = object.getAnnotation();
        annotation.getProperty().accept(this);
        append(' ');
        subject.accept(this);
        append(' ');
        if(annotation.getValue() instanceof IRI){
            append('[');
            annotation.getValue().accept(this);
            append(']');
        }else{
            annotation.getValue().accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDeclarationAxiom object) {
        String head = null;
        OWLEntity m_entity=object.getEntity();
        if (m_entity instanceof OWLDataProperty)
            head = "dataDeclaration"; //$NON-NLS-1$
        else if (m_entity instanceof OWLObjectProperty)
            head = "objectDeclaration"; //$NON-NLS-1$
        else if (m_entity instanceof OWLIndividual)
            head = "individualDeclaration"; //$NON-NLS-1$
        else if (m_entity instanceof OWLClassExpression)
            head = "classDeclaration"; //$NON-NLS-1$
        else if (m_entity instanceof OWLDatatype)
            head = "datatypeDeclaration"; //$NON-NLS-1$
        else if (m_entity instanceof OWLAnnotationProperty)
            head = "annotationDeclaration"; //$NON-NLS-1$
        else
            throw new IllegalStateException("Unknown entity type."); //$NON-NLS-1$
        
        append('[');
        appendAnnotations(head, object);
        append(' ');
        
        m_entity.accept(this);
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        visit((OWLObjectPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLReflexiveObjectPropertyAxiom axiom) {
        visit((OWLObjectPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLFunctionalObjectPropertyAxiom axiom) {
        visit((OWLObjectPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLSymmetricObjectPropertyAxiom axiom) {
        visit((OWLObjectPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLFunctionalDataPropertyAxiom axiom) {
        visit((OWLDataPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLTransitiveObjectPropertyAxiom axiom) {
        visit((OWLObjectPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        visit((OWLObjectPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        visit((OWLObjectPropertyCharacteristicAxiom)axiom);
        return null;
    }
    @Override
    public Object visit(OWLSubPropertyChainOfAxiom axiom) {
        append('[');
        appendAnnotations("subPropertyChainOf", axiom); //$NON-NLS-1$
        append(' ');
        append("[objectPropertyChain"); //$NON-NLS-1$
        for(OWLObjectPropertyExpression objectProperty : axiom.getPropertyChain()){
            append(' ');
            objectProperty.accept(this);
        }
        append(']');
        append(' ');
        axiom.getSuperProperty().accept(this);
        append(']');
        return null;
    }
    @Override
    public Object visit(SWRLRule rule) {
        append('[');
        appendAnnotations("rule", rule); //$NON-NLS-1$
        append(' ');
        if(OWLUtilities.getIRI(rule) != null){
            visit(OWLUtilities.getIRI(rule));
            append(' ');
        }
        
        Set<SWRLAtom> antecedent=Cast.cast(rule.getBody());       
        Set<SWRLAtom> consequent= Cast.cast(rule.getHead());
        append('[');
        for (SWRLAtom atom : antecedent) {
            atom.accept(this);
            append(' ');
        }
        append("] ["); //$NON-NLS-1$
        for (SWRLAtom atom : consequent) {
            atom.accept(this);
            append(' ');
        }
        append("]]"); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(OWLObjectMinCardinality desc) {
        visit((OWLObjectCardinalityRestriction)desc);
        return null;
    }
    @Override
    public Object visit(OWLObjectExactCardinality desc) {
        visit((OWLObjectCardinalityRestriction)desc);
        return null;
    }
    @Override
    public Object visit(OWLObjectMaxCardinality desc) {
        visit((OWLObjectCardinalityRestriction)desc);
        return null;
    }
    @Override
    public Object visit(OWLDataMinCardinality desc) {
        visit((OWLDataCardinalityRestriction)desc);
        return null;
    }
    @Override
    public Object visit(OWLDataExactCardinality desc) {
        visit((OWLDataCardinalityRestriction)desc);
        return null;
    }
    @Override
    public Object visit(OWLDataMaxCardinality desc) {
        visit((OWLDataCardinalityRestriction)desc);
        return null;
    }
    @Override
    public Object visit(SWRLClassAtom node) {
        appendSWRLArguments("swrlClass",  node.getPredicate(), node.getAllArguments()); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(SWRLDataRangeAtom node) {
        appendSWRLArguments("swrlDataRange",  node.getPredicate(), node.getAllArguments()); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(SWRLObjectPropertyAtom node) {
        appendSWRLArguments("swrlObjectProperty",  node.getPredicate(), node.getAllArguments()); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(SWRLDataPropertyAtom node) {
        appendSWRLArguments("swrlDataValuedProperty",  node.getPredicate(), node.getAllArguments()); //$NON-NLS-1$
        return null;
    }
   
    @Override
    public Object visit(SWRLBuiltInAtom node) {
        append("[swrlBuiltIn"); //$NON-NLS-1$
        append(' ');
        visit(node.getPredicate());
        append(' ');
        
        for(SWRLArgument object : node.getAllArguments()){
            object.accept(this);
            append(' ');
        }
        append(']');
        return null;
    }
    @Override
    public Object visit(SWRLVariable node) {
        append("[swrlVariable"); //$NON-NLS-1$
        append(' ');
        visit(node.getIRI());
        append(']');
        return null;
    }
    @Override
    public Object visit(SWRLIndividualArgument node) {
        node.getIndividual().accept(this);
        return null;
    }
    @Override
    public Object visit(SWRLLiteralArgument node) {
        node.getLiteral().accept(this);
        return null;
    }
    @Override
    public Object visit(SWRLSameIndividualAtom node) {
        appendSWRLArguments("swrlSameAs", null, node.getAllArguments()); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(SWRLDifferentIndividualsAtom node) {
        appendSWRLArguments("swrlDifferentFrom", null, node.getAllArguments()); //$NON-NLS-1$
        return null;
    }
    
    private void appendSWRLArguments(String head, OWLObject predicate, Collection<? extends SWRLArgument> arguments){
        append("["+head); //$NON-NLS-1$
        append(' ');
        if(predicate != null){
            predicate.accept(this);
            append(' ');
        }
        
        for(SWRLArgument object : arguments){
            object.accept(this);
            append(' ');
        }
        append(']');
    }
    
    @Override
    public Object visit(OWLOntology ontology) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Object visit(OWLHasKeyAxiom axiom) {
        append('[');
        appendAnnotations("hasKey", axiom); //$NON-NLS-1$
        append(' ');
        axiom.getClassExpression().accept(this);
        append(' ');
        append('[');
        for(OWLObjectPropertyExpression propertyExpression : axiom.getObjectPropertyExpressions()){
            propertyExpression.accept(this);
            append(' ');
        }
        append("]["); //$NON-NLS-1$
        for(OWLDataPropertyExpression propertyExpression : axiom.getDataPropertyExpressions()){
            propertyExpression.accept(this);
            append(' ');
        }
        append("]]"); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(OWLDatatypeDefinitionAxiom axiom) {
        append('[');
        appendAnnotations("datatypeDefinition", axiom); //$NON-NLS-1$
        append(' ');
        axiom.getDatatype().accept(this);
        append(' ');
        axiom.getDataRange().accept(this);
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        append('[');
        appendAnnotations("subAnnotationPropertyOf", axiom); //$NON-NLS-1$
        append(' ');
        axiom.getSubProperty().accept(this);
        append(' ');
        axiom.getSuperProperty().accept(this);
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLAnnotationPropertyDomainAxiom axiom) {
        append('[');
        appendAnnotations("annotationPropertyDomain", axiom); //$NON-NLS-1$
        append(' ');
        axiom.getProperty().accept(this);
        append(' ');
        axiom.getDomain().accept(this);
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLAnnotationPropertyRangeAxiom axiom) {
        append('[');
        appendAnnotations("annotationPropertyRange", axiom); //$NON-NLS-1$
        append(' ');
        axiom.getProperty().accept(this);
        append(' ');
        axiom.getRange().accept(this);
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLDataIntersectionOf node) {
        append("[dataIntersectionOf "); //$NON-NLS-1$
        
        for(OWLDataRange dataRange : node.getOperands()){
            append(' ');
            dataRange.accept(this);
        }
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLDataUnionOf node) {
        append("[dataUnionOf "); //$NON-NLS-1$
        
        for(OWLDataRange dataRange : node.getOperands()){
            append(' ');
            dataRange.accept(this);
        }
        append(']');
        return null;
    }

    
    @Override
    public Object visit(IRI iri) {
        append(m_namespaces.abbreviateAsNamespace(iri.toString()));
        return null;
    }
    @Override
    public Object visit(OWLAnonymousIndividual individual) {
        append(individual.getID().getID());
        return null;
    }
}
