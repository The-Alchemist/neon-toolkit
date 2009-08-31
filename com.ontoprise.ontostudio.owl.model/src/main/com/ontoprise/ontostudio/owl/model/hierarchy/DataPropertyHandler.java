/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 26.01.2009
 * Created by: krekeler
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.hierarchy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.model.util.Cast;

public class DataPropertyHandler implements EntityHierarchyUpdater.EntityHandler<OWLDataPropertyExpression> {
    private static final Set<AxiomType<? extends OWLAxiom>> HANDLED_AXIOMS = Cast.cast(Collections.singleton(AxiomType.SUB_DATA_PROPERTY));
    
    @Override
    public Class<? extends OWLDataPropertyExpression> getEntityType() {
        return OWLDataProperty.class;
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
        if (!(axiom instanceof OWLSubDataPropertyOfAxiom)) {
            return false;
        }
        OWLSubDataPropertyOfAxiom concreteAxiom = (OWLSubDataPropertyOfAxiom)axiom;
        return concreteAxiom.getSubProperty() instanceof OWLDataProperty && concreteAxiom.getSuperProperty() instanceof OWLDataProperty;
    }

    @Override
    public OWLDataPropertyExpression getDeclaredEntity(OWLAxiom axiom) {
        assert(isNodeAxiom(axiom));
        return null;
    }
    
    @Override
    public List<OWLDataPropertyExpression> getChildEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        return Collections.singletonList(((OWLSubDataPropertyOfAxiom)axiom).getSubProperty());
    }

    @Override
    public List<OWLDataPropertyExpression> getParentEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        return Collections.singletonList(((OWLSubDataPropertyOfAxiom)axiom).getSuperProperty());
    }

    @Override
    public boolean handleNodeEntity(OWLDataPropertyExpression entity) {
        return entity instanceof OWLDataProperty;
    }

}