/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.hierarchy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.model.util.Cast;

public class ObjectPropertyHandler implements EntityHierarchyUpdater.EntityHandler<OWLObjectPropertyExpression> {
    private static final Set<AxiomType<? extends OWLAxiom>> HANDLED_AXIOMS = Cast.cast(Collections.singleton(AxiomType.SUB_OBJECT_PROPERTY));
    
    @Override
    public Class<? extends OWLObjectPropertyExpression> getEntityType() {
        return OWLObjectProperty.class;
    }
    
    @Override
    public Set<AxiomType<? extends OWLAxiom>> getHandledAxiomTypes() {
        return HANDLED_AXIOMS;
    }
    
    @Override
    public boolean isNodeAxiom(OWLAxiom axiom) {
        return false;
    }

    @Override
    public boolean isEdgeAxiom(OWLAxiom axiom) {
        if (!(axiom instanceof OWLSubObjectPropertyOfAxiom)) {
            return false;
        }
        OWLSubObjectPropertyOfAxiom concreteAxiom = (OWLSubObjectPropertyOfAxiom)axiom;
        return concreteAxiom.getSuperProperty() instanceof OWLObjectProperty && concreteAxiom.getSubProperty() instanceof OWLObjectProperty;
    }

    @Override
    public OWLObjectPropertyExpression getDeclaredEntity(OWLAxiom axiom) {
        assert(isNodeAxiom(axiom));
        return null;
    }
    
    @Override
    public List<OWLObjectPropertyExpression> getChildEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        return Collections.singletonList(((OWLSubObjectPropertyOfAxiom)axiom).getSubProperty());
    }

    @Override
    public List<OWLObjectPropertyExpression> getParentEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        return Collections.singletonList(((OWLSubObjectPropertyOfAxiom)axiom).getSuperProperty());
    }

    @Override
    public boolean handleNodeEntity(OWLObjectPropertyExpression entity) {
        return entity instanceof OWLObjectProperty;
    }

}
