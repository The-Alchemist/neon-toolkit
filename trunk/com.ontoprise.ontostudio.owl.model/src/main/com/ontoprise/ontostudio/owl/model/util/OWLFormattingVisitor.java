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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.*;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;


public class OWLFormattingVisitor implements OWLObjectVisitorEx<Object> {
    private static final URI OWL_THING_URI;
    static {
        try {
            OWL_THING_URI = new URI(OWLNamespaces.OWL_NS + "Thing");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private Appendable m_target;
    private OWLNamespaces m_namespaces;
    private static String escapeString(String string) {
        StringBuffer buffer=new StringBuffer();
        for (int i=0;i<string.length();i++) {
            char c=string.charAt(i);
            if (c=='"')
                buffer.append("\\\"");
            else if (c=='\\')
                buffer.append("\\\\");
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
    
    private void visit(URI uri) {
        append(m_namespaces.abbreviateAsNamespace(uri.toString()));
    }

    public Object visit(OWLNamedIndividual object) {
        visit(object.getURI());
        return null;
    }

    public Object visit(OWLAnnotationProperty object) {
        visit(object.getURI());
        return null;
    }

    public Object visit(OWLDataProperty object) {
        visit(object.getURI());
        return null;
    }

    public Object visit(OWLObjectProperty object) {
        visit(object.getURI());
        return null;
    }

    public Object visit(OWLDatatype object) {
        visit(object.getURI());
        return null;
    }

    public Object visit(OWLClass object) {
        visit(object.getURI());
        return null;
    }

    public Object visit(OWLObjectInverseOf object) {
        append("[inv ");
        object.getInverse().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLLiteral object) {
        if (object.isTyped()) {
            visit((OWLTypedLiteral)object);
        } else {
            visit((OWLStringLiteral)object);
        }
        return null;
    }

    public Object visit(OWLDataComplementOf object) {
        append("[not ");
        object.getDataRange().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataOneOf object) {
        append("[oneOf");
        for (OWLLiteral literalValue : object.getValues()) {
            append(' ');
            literalValue.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDatatypeRestriction object) {
        append("[datatypeRestriction ");
        object.getDatatype().accept(this);
        for (OWLFacetRestriction facetRestriction: object.getFacetRestrictions()) {
            append(' ');
            visit(facetRestriction);
        }
        append(']');
        return null;
    }
    
    public Object visit(OWLFacetRestriction object) {
        append("[facetRestriction ");
        append(object.getFacet().toString());
        append(' ');
        StringBuffer buffer=new StringBuffer();
        object.getFacetValue().accept(this);
        append(buffer);
        append(']');
        return null;
    }

    public Object visit(OWLDataAllValuesFrom object) {
        append("[dataAll");
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataSomeValuesFrom object) {
        append("[dataSome");
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataCardinalityRestriction object) {
        if (object instanceof OWLDataMinCardinality) {
            append("[dataAtLeast ");
        } else if (object instanceof OWLDataMaxCardinality) {
            append("[dataAtMost ");
        } else if (object instanceof OWLDataExactCardinality) {
            append("[dataExactly ");
        } else {
            throw new IllegalStateException("Invalid cardinality type.");
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
        append("[dataHasValue ");
        object.getProperty().accept(this);
        append(' ');
        object.getValue().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectAllValuesFrom object) {
        append("[all ");
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectSomeValuesFrom object) {
        append("[some ");
        object.getProperty().accept(this);
        append(' ');
        object.getFiller().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectHasSelf object) {
        append("[self ");
        object.getProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectCardinalityRestriction object) {
        OWLClassExpression m_description=object.getFiller();
        if (object instanceof OWLObjectMinCardinality) {
            append("[atLeast ");
        } else if (object instanceof OWLObjectMaxCardinality) {
            append("[atMost ");
        } else if (object instanceof OWLObjectExactCardinality) {
            append("[exactly ");
        } else {
            throw new IllegalStateException("Invalid cardinality type.");
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
        return description instanceof OWLClass && OWL_THING_URI.equals(((OWLClass)description).getURI());
    }

    public Object visit(OWLObjectOneOf object) {
        append("[oneOf");
        for (OWLIndividual individual : object.getIndividuals()) {
            append(' ');
            individual.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLObjectHasValue object) {
        append("[hasValue ");
        object.getProperty().accept(this);
        append(' ');
        object.getValue().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectComplementOf object) {
        append("[not ");
        object.getOperand().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectUnionOf object) {
        append("[or");
        for (OWLClassExpression description : object.getOperands()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLObjectIntersectionOf object) {
        append("[and");
        for (OWLClassExpression description : object.getOperands()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLSubClassOfAxiom object) {
        append("[");
        appendAnnotations("subClassOf", object);
        append(' ');
        object.getSubClass().accept(this);
        append(' ');
        object.getSuperClass().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLEquivalentClassesAxiom object) {
        append("[");
        appendAnnotations("equivalent", object);
        for (OWLClassExpression description : object.getClassExpressions()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointClassesAxiom object) {
        append('[');
        appendAnnotations("disjoint", object);
        for (OWLClassExpression description : object.getClassExpressions()) {
            append(' ');
            description.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointUnionAxiom object) {
        append("[");
        appendAnnotations("disjointUnion", object);
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
            appendAnnotations("dataFunctional", object);
            append(' ');
        }else
            append("[invalid attribute type!");
        object.getProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataPropertyDomainAxiom object) {
        append('[');
        appendAnnotations("dataDomain", object);
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getDomain().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLDataPropertyRangeAxiom object) {
        append('[');
        appendAnnotations("dataRange", object);
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getRange().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLSubDataPropertyOfAxiom object) {
        append('[');
        appendAnnotations("subDataPropertyOf", object);
        append(' ');
        object.getSubProperty().accept(this);
        append(' ');
        object.getSuperProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLEquivalentDataPropertiesAxiom object) {
        append('[');
        appendAnnotations("dataEquivalent", object);
        for (OWLDataPropertyExpression dataProperty : object.getProperties()) {
            append(' ');
            dataProperty.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointDataPropertiesAxiom object) {
        append('[');
        appendAnnotations("dataDisjoint", object);
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
            head = "objectFunctional";
        else if (object instanceof OWLInverseFunctionalObjectPropertyAxiom)
            head = "objectInverseFunctional";
        else if (object instanceof OWLSymmetricObjectPropertyAxiom)
            head = "objectSymmetric";
        else if (object instanceof OWLTransitiveObjectPropertyAxiom)
            head = "objectTransitive";
        else if (object instanceof OWLReflexiveObjectPropertyAxiom)
            head = "objectReflexive";
        else if (object instanceof OWLIrreflexiveObjectPropertyAxiom)
            head = "objectIrreflexive";
        else if (object instanceof OWLAsymmetricObjectPropertyAxiom)
            head = "objectAsymmetric";
        else
            append("invalid attribute type!");
        
        appendAnnotations(head, object);
        append(' ');
        object.getProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectPropertyDomainAxiom object) {
        append('[');
        appendAnnotations("objectDomain", object);
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getDomain().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLObjectPropertyRangeAxiom object) {
        append('[');
        appendAnnotations("objectRange", object);
        append(' ');
        object.getProperty().accept(this);
        append(' ');
        object.getRange().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLSubObjectPropertyOfAxiom object) {
        append('[');
        appendAnnotations("subObjectPropertyOf", object);
        append(' ');
        object.getSubProperty().accept(this);
        append(' ');
        object.getSuperProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLEquivalentObjectPropertiesAxiom object) {
        append('[');
        appendAnnotations("objectEquivalent", object);
        for (OWLObjectPropertyExpression objectProperty : object.getProperties()) {
            append(' ');
            objectProperty.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDisjointObjectPropertiesAxiom object) {
        append('[');
        appendAnnotations("objectDisjoint", object);
        for (OWLObjectPropertyExpression objectProperty : object.getProperties()) {
            append(' ');
            objectProperty.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLInverseObjectPropertiesAxiom object) {
        append('[');
        appendAnnotations("objectInverse", object);
        append(' ');
        object.getFirstProperty().accept(this);
        append(' ');
        object.getSecondProperty().accept(this);
        append(']');
        return null;
    }

    public Object visit(OWLSameIndividualAxiom object) {
        append('[');
        appendAnnotations("same", object);
        for (OWLIndividual individual : object.getIndividuals()) {
            append(' ');
            individual.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDifferentIndividualsAxiom object) {
        append('[');
        appendAnnotations("different", object);
        for (OWLIndividual individual : object.getIndividuals()) {
            append(' ');
            individual.accept(this);
        }
        append(']');
        return null;
    }

    public Object visit(OWLDataPropertyAssertionAxiom object) {
        append('[');
        appendAnnotations("dataMember", object);
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
        appendAnnotations("negativeDataMember", object);
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
        appendAnnotations("objectMember", object);
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
        appendAnnotations("negativeObjectMember", object);
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
        appendAnnotations("classMember", object);
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
            head = "dataAnnotation";
        else if (subject instanceof OWLObjectProperty)
            head = "objectAnnotation";
        else if (subject instanceof OWLNamedIndividual)
            head = "namedIndividualAnnotation";
        else if (subject instanceof OWLClass)
            head = "classAnnotation";
        else if (subject instanceof OWLDatatype)
            head = "datatypeAnnotation";
        else if (subject instanceof OWLAnnotationProperty)
            head = "annotationAnnotationProperty";
        else if (subject instanceof OWLAnonymousIndividual)
            head = "annotationAnonymousIndividual";
        else if (subject instanceof IRI)
            head = "annotationIRI";
        else if (subject instanceof OWLAnnotation)
            head = "annotationAnnotation";
        else
            throw new IllegalStateException("Unknown entity type. "+subject.getClass().getName());
        
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
            head = "dataDeclaration";
        else if (m_entity instanceof OWLObjectProperty)
            head = "objectDeclaration";
        else if (m_entity instanceof OWLIndividual)
            head = "individualDeclaration";
        else if (m_entity instanceof OWLClassExpression)
            head = "classDeclaration";
        else if (m_entity instanceof OWLDatatype)
            head = "datatypeDeclaration";
        else if (m_entity instanceof OWLAnnotationProperty)
            head = "annotationDeclaration";
        else
            throw new IllegalStateException("Unknown entity type.");
        
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
        appendAnnotations("subPropertyChainOf", axiom);
        append(' ');
        append("[objectPropertyChain");
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
        appendAnnotations("rule", rule);
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
        append("] [");
        for (SWRLAtom atom : consequent) {
            atom.accept(this);
            append(' ');
        }
        append("]]");
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
    public Object visit(OWLTypedLiteral node) {
        String literal = escapeString(node.getLiteral());
        OWLTypedLiteral typedConstant = node;
        OWLDatatype datatype = typedConstant.getDatatype();
        append('"');
        append(literal);
        append("\"^^<");
        append(datatype.getURI().toString());
        append('>');
        return null;
    }
    @Override
    public Object visit(OWLStringLiteral node) {
        String literal = escapeString(node.getLiteral());
        OWLStringLiteral untypedConstant = node;
        String lang = untypedConstant.getLang();
        append('"');
        append(literal);
        append('"');
        append('@');
        append(lang);
        return null;
    }
    
    @Override
    public Object visit(SWRLClassAtom node) {
        appendSWRLArguments("swrlClass",  node.getPredicate(), node.getAllArguments());
        return null;
    }
    @Override
    public Object visit(SWRLDataRangeAtom node) {
        appendSWRLArguments("swrlDataRange",  node.getPredicate(), node.getAllArguments());
        return null;
    }
    @Override
    public Object visit(SWRLObjectPropertyAtom node) {
        appendSWRLArguments("swrlObjectProperty",  node.getPredicate(), node.getAllArguments());
        return null;
    }
    @Override
    public Object visit(SWRLDataPropertyAtom node) {
        appendSWRLArguments("swrlDataValuedProperty",  node.getPredicate(), node.getAllArguments());
        return null;
    }
   
    @Override
    public Object visit(SWRLBuiltInAtom node) {
        append("[swrlBuiltIn");
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
        append('?');
        visit(node.getIRI());
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
        appendSWRLArguments("swrlSameAs", null, node.getAllArguments());
        return null;
    }
    @Override
    public Object visit(SWRLDifferentIndividualsAtom node) {
        appendSWRLArguments("swrlDifferentFrom", null, node.getAllArguments());
        return null;
    }
    
    private void appendSWRLArguments(String head, OWLObject predicate, Collection<? extends SWRLArgument> arguments){
        append("["+head);
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
        appendAnnotations("hasKey", axiom);
        append(' ');
        axiom.getClassExpression().accept(this);
        append(' ');
        append('[');
        for(OWLObjectPropertyExpression propertyExpression : axiom.getObjectPropertyExpressions()){
            propertyExpression.accept(this);
            append(' ');
        }
        append("][");
        for(OWLDataPropertyExpression propertyExpression : axiom.getDataPropertyExpressions()){
            propertyExpression.accept(this);
            append(' ');
        }
        append("]]");
        return null;
    }
    @Override
    public Object visit(OWLDatatypeDefinitionAxiom axiom) {
        append('[');
        appendAnnotations("datatypeDefinition", axiom);
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
        appendAnnotations("subAnnotationPropertyOf", axiom);
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
        appendAnnotations("annotationPropertyDomain", axiom);
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
        appendAnnotations("annotationPropertyRange", axiom);
        append(' ');
        axiom.getProperty().accept(this);
        append(' ');
        axiom.getRange().accept(this);
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLDataIntersectionOf node) {
        append("[dataIntersectionOf ");
        
        for(OWLDataRange dataRange : node.getOperands()){
            append(' ');
            dataRange.accept(this);
        }
        append(']');
        return null;
    }
    @Override
    public Object visit(OWLDataUnionOf node) {
        append("[dataUnionOf ");
        
        for(OWLDataRange dataRange : node.getOperands()){
            append(' ');
            dataRange.accept(this);
        }
        append(']');
        return null;
    }
    
    @Override
    public Object visit(IRI iri) {
        visit(iri.toURI());
        return null;
    }
    @Override
    public Object visit(OWLAnonymousIndividual individual) {
        append(individual.getID().toString());
        return null;
    }
}
