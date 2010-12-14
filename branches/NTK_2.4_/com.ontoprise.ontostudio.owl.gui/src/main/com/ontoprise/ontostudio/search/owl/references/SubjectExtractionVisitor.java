/*****************************************************************************
 * Copyright (c) 2010 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package com.ontoprise.ontostudio.search.owl.references;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * @author mer
 * Created on: 12.02.2010
 */
public class SubjectExtractionVisitor implements OWLAxiomVisitor {

    private OWLObject _subject = null;
    
    @Override
    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        _subject = axiom.getSubProperty();
    }
    
    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        _subject = axiom.getSubject();
    }
    
    @Override
    public void visit(SWRLRule rule) {
    }
    
    @Override
    public void visit(OWLDatatypeDefinitionAxiom axiom) {
        _subject = axiom.getDatatype();
    }
    
    @Override
    public void visit(OWLHasKeyAxiom axiom) {
        _subject = axiom.getClassExpression();
    }
    
    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        _subject = axiom.getFirstProperty();
    }
    
    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        _subject = axiom.getSuperProperty();
    }
    
    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        _subject = axiom.getIndividuals().iterator().next();
    }
    
    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        _subject = axiom.getSubProperty();
    }
    
    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        _subject = axiom.getSubject();
    }
    
    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        _subject = axiom.getClassExpressions().iterator().next();
    }
    
    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        _subject = axiom.getIndividual();
    }
    
    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        _subject = axiom.getProperties().iterator().next();
    }
    
    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        _subject = axiom.getClassExpressions().iterator().next();
    }
    
    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        _subject = axiom.getSubProperty();
    }
    
    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        _subject = axiom.getSubject();
    }
    
    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        _subject = axiom.getProperties().iterator().next();
    }
    
    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        _subject = axiom.getProperties().iterator().next();
    }
    
    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        _subject = axiom.getIndividuals().iterator().next();
    }
    
    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        _subject = axiom.getSubject();
    }
    
    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        _subject = axiom.getProperties().iterator().next();
    }
    
    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLDisjointClassesAxiom axiom) {
        _subject = axiom.getClassExpressions().iterator().next();
    }
    
    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        _subject = axiom.getProperty();
    }
    
    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        _subject = axiom.getSubject();
    }
    
    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        _subject = axiom.getSubClass();
    }
    
    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        _subject = axiom.getEntity();
    }
    
    public OWLObject getSubject() {
        return _subject;
    }
}
