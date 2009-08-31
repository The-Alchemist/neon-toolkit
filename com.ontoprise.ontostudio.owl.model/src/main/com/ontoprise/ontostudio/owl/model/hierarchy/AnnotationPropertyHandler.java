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
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.model.util.Cast;

public class AnnotationPropertyHandler implements EntityHierarchyUpdater.EntityHandler<OWLAnnotationProperty> {
    private static final Set<AxiomType<? extends OWLAxiom>> HANDLED_AXIOMS = Cast.cast(Collections.singleton(AxiomType.SUB_ANNOTATION_PROPERTY_OF));
    
    @Override
    public Class<? extends OWLAnnotationProperty> getEntityType() {
        return OWLAnnotationProperty.class;
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
        if (!(axiom instanceof OWLSubAnnotationPropertyOfAxiom)) {
            return false;
        }
        OWLSubAnnotationPropertyOfAxiom concreteAxiom = (OWLSubAnnotationPropertyOfAxiom)axiom;
        return concreteAxiom.getSubProperty() instanceof OWLAnnotationProperty && concreteAxiom.getSuperProperty() instanceof OWLAnnotationProperty;
    }

    @Override
    public OWLAnnotationProperty getDeclaredEntity(OWLAxiom axiom) {
        assert(isNodeAxiom(axiom));
        return null;
    }
    
    @Override
    public List<OWLAnnotationProperty> getChildEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        return Collections.singletonList(((OWLSubAnnotationPropertyOfAxiom)axiom).getSubProperty());
    }

    @Override
    public List<OWLAnnotationProperty> getParentEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        return Collections.singletonList(((OWLSubAnnotationPropertyOfAxiom)axiom).getSuperProperty());
    }

    @Override
    public boolean handleNodeEntity(OWLAnnotationProperty entity) {
        return true;
    }

}