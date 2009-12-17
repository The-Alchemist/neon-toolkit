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

import java.util.Collections;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

/**
 * A visitor for OWL axioms that returns the items from an axiom for a specific member of the axiom.
 * 
 * @author krekeler
 * 
 */
public class GetMemberVisitor extends OWLKAON2VisitorAdapter {
    /** The member identifier. */
    protected String _member;

    /**
     * 
     * @param member The member identifier.
     */
    public GetMemberVisitor(String member) {
        _member = member;
    }

    @Override
    public Object visit(OWLClassAssertionAxiom object) {
        if ("description".equals(_member)) { //$NON-NLS-1$
            return object.getClassExpression();
        } else if ("individual".equals(_member)) { //$NON-NLS-1$
            return object.getIndividual();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDataPropertyCharacteristicAxiom object) {
        if ("dataProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("attribute".equals(_member)) { //$NON-NLS-1$
            // TODO: migration, not used at all (?)
            throw new UnsupportedOperationException("TODO: migration");
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDataPropertyDomainAxiom object) {
        if ("dataProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("domain".equals(_member)) { //$NON-NLS-1$
            return object.getDomain();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDataPropertyAssertionAxiom object) {
        if ("dataProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("sourceIndividual".equals(_member)) { //$NON-NLS-1$
            return object.getSubject();
        } else if ("targetValue".equals(_member)) { //$NON-NLS-1$
            return object.getObject();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDataPropertyRangeAxiom object) {
        if ("dataProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("range".equals(_member)) { //$NON-NLS-1$
            return object.getRange();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDeclarationAxiom object) {
        if ("entity".equals(_member)) { //$NON-NLS-1$
            return object.getEntity();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDifferentIndividualsAxiom object) {
        if ("individuals".equals(_member)) { //$NON-NLS-1$
            return object.getIndividuals();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDisjointClassesAxiom object) {
        if ("descriptions".equals(_member)) { //$NON-NLS-1$
            return object.getClassExpressions();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDisjointDataPropertiesAxiom object) {
        if ("dataProperties".equals(_member)) { //$NON-NLS-1$
            return object.getProperties();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDisjointObjectPropertiesAxiom object) {
        if ("objectProperties".equals(_member)) { //$NON-NLS-1$
            return object.getProperties();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLDisjointUnionAxiom object) {
        if ("descriptions".equals(_member)) { //$NON-NLS-1$
            return object.getClassExpressions();
        } else if ("owlClass".equals(_member)) { //$NON-NLS-1$
            return object.getOWLClass();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLAnnotationAssertionAxiom object) {
        if ("annotationProperty".equals(_member)) { //$NON-NLS-1$
            return object.getAnnotation().getProperty();
        } else if ("annotationValue".equals(_member)) { //$NON-NLS-1$
            return object.getAnnotation().getValue();
        } else if ("entity".equals(_member)) { //$NON-NLS-1$
            return object.getSubject();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLEquivalentClassesAxiom object) {
        if ("descriptions".equals(_member)) { //$NON-NLS-1$
            return object.getClassExpressions();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLEquivalentDataPropertiesAxiom object) {
        if ("dataProperties".equals(_member)) { //$NON-NLS-1$
            return object.getProperties();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLEquivalentObjectPropertiesAxiom object) {
        if ("objectProperties".equals(_member)) { //$NON-NLS-1$
            return object.getProperties();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLInverseObjectPropertiesAxiom object) {
        if ("first".equals(_member)) { //$NON-NLS-1$
            return object.getFirstProperty();
        } else if ("second".equals(_member)) { //$NON-NLS-1$
            return object.getSecondProperty();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLNegativeDataPropertyAssertionAxiom object) {
        if ("dataProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("sourceIndividual".equals(_member)) { //$NON-NLS-1$
            return object.getSubject();
        } else if ("targetValue".equals(_member)) { //$NON-NLS-1$
            return object.getObject();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLNegativeObjectPropertyAssertionAxiom object) {
        if ("objectProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("sourceIndividual".equals(_member)) { //$NON-NLS-1$
            return object.getSubject();
        } else if ("targetIndividual".equals(_member)) { //$NON-NLS-1$
            return object.getObject();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLObjectPropertyCharacteristicAxiom object) {
        if ("objectProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("attribute".equals(_member)) { //$NON-NLS-1$
            // TODO: migration, not used at all (?)
            throw new UnsupportedOperationException("TODO: migration");
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLObjectPropertyDomainAxiom object) {
        if ("objectProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("domain".equals(_member)) { //$NON-NLS-1$
            return object.getDomain();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLObjectPropertyAssertionAxiom object) {
        if ("objectProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("sourceIndividual".equals(_member)) { //$NON-NLS-1$
            return object.getSubject();
        } else if ("targetIndividual".equals(_member)) { //$NON-NLS-1$
            return object.getObject();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLObjectPropertyRangeAxiom object) {
        if ("objectProperty".equals(_member)) { //$NON-NLS-1$
            return object.getProperty();
        } else if ("range".equals(_member)) { //$NON-NLS-1$
            return object.getRange();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLSameIndividualAxiom object) {
        if ("individuals".equals(_member)) { //$NON-NLS-1$
            return object.getIndividuals();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLSubClassOfAxiom object) {
        if ("subDescription".equals(_member)) { //$NON-NLS-1$
            return object.getSubClass();
        } else if ("superDescription".equals(_member)) { //$NON-NLS-1$
            return object.getSuperClass();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLSubDataPropertyOfAxiom object) {
        if ("subDataProperty".equals(_member)) { //$NON-NLS-1$
            return object.getSubProperty();
        } else if ("superDataProperty".equals(_member)) { //$NON-NLS-1$
            return object.getSuperProperty();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLSubObjectPropertyOfAxiom object) {
        if ("subObjectProperties".equals(_member)) { //$NON-NLS-1$
            return Collections.singleton(object.getSubProperty());
        } else if ("superObjectProperty".equals(_member)) { //$NON-NLS-1$
            return object.getSuperProperty();
        }
        return super.visit(object);
    }

    @Override
    public Object visit(OWLSubAnnotationPropertyOfAxiom object) {
        if ("subAnnotationProperty".equals(_member)) { //$NON-NLS-1$
            return object.getSubProperty();
        } else if ("superAnnotationProperty".equals(_member)) { //$NON-NLS-1$
            return object.getSuperProperty();
        }
        return super.visit(object);
    }
    
    @Override
    public Object visit(OWLSubPropertyChainOfAxiom object) {
        if ("subObjectProperties".equals(_member)) { //$NON-NLS-1$
            return object.getPropertyChain();
        } else if ("superObjectProperty".equals(_member)) { //$NON-NLS-1$
            return object.getSuperProperty();
        }
        return super.visit(object);
    }
}
