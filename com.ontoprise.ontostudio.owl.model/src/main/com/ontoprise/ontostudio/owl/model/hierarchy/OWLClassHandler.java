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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;

public class OWLClassHandler implements EntityHierarchyUpdater.EntityHandler<OWLClassExpression> {
    @SuppressWarnings("unchecked")
    private static final Set<AxiomType<? extends OWLAxiom>> HANDLED_AXIOMS = Collections.unmodifiableSet(new LinkedHashSet<AxiomType<? extends OWLAxiom>>(Arrays.asList(AxiomType.SUBCLASS, AxiomType.EQUIVALENT_CLASSES)));
    private static class Edge {
        private OWLClassExpression _child;
        private OWLClassExpression _parent;
        public Edge(OWLClassExpression child, OWLClassExpression parent) {
            _child = child;
            _parent = parent;
        }
        public OWLClassExpression getChild() {
            return _child;
        }
        public OWLClassExpression getParent() {
            return _parent;
        }
    }
    
    private final OWLDataFactory _factory;
    public OWLClassHandler(OWLDataFactory factory) {
        _factory = factory;
    }
    
    @Override
    public Class<? extends OWLClassExpression> getEntityType() {
        return OWLClass.class;
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
        if (axiom instanceof OWLSubClassOfAxiom) {
            OWLClassExpression subDescription = ((OWLSubClassOfAxiom)axiom).getSubClass();
            OWLClassExpression superDescription = ((OWLSubClassOfAxiom)axiom).getSuperClass();

            if (isThingOrNothing(subDescription) || isThingOrNothing(superDescription)) {
                return false;
            }
            return true;
        } else if (axiom instanceof OWLEquivalentClassesAxiom) {
            return true;
        }
        return false;
    }
    
    private boolean isThingOrNothing(OWLClassExpression description) {
        return _factory.getOWLThing().equals(description) || _factory.getOWLNothing().equals(description);
    }

    @Override
    public OWLClassExpression getDeclaredEntity(OWLAxiom axiom) {
        assert(isNodeAxiom(axiom));
        return null;
    }
    
    @Override
    public List<OWLClassExpression> getChildEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        List<OWLClassExpression> result = new ArrayList<OWLClassExpression>();
        for (Edge edge: getEdges(axiom)) {
            result.add(edge.getChild());
        }
        return result;
    }

    @Override
    public List<OWLClassExpression> getParentEntities(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        List<OWLClassExpression> result = new ArrayList<OWLClassExpression>();
        for (Edge edge: getEdges(axiom)) {
            result.add(edge.getParent());
        }
        return result;
    }
    
    private Set<Edge> getEdges(OWLAxiom axiom) {
        assert(isEdgeAxiom(axiom));
        if (axiom instanceof OWLSubClassOfAxiom) {
            OWLClassExpression subDescription = ((OWLSubClassOfAxiom)axiom).getSubClass();
            OWLClassExpression superDescription = ((OWLSubClassOfAxiom)axiom).getSuperClass();

            if (superDescription instanceof OWLClass) {
                return Collections.singleton(new Edge(subDescription, superDescription));
            } else if (subDescription instanceof OWLClass) {
                Set<Edge> result = new LinkedHashSet<Edge>();
                for (OWLClass namedClass: getNamedClasses(superDescription)) {
                    result.add(new Edge(subDescription, namedClass));
                }
                return result;
            } else {
                return Collections.emptySet();
            }
        } else if (axiom instanceof OWLEquivalentClassesAxiom) {
            List<OWLClassExpression> descriptions = new ArrayList<OWLClassExpression>(((OWLEquivalentClassesAxiom)axiom).getClassExpressions());

            Set<Edge> result = new LinkedHashSet<Edge>();
            // for each pair { x,y } in descriptions
            for (int i = 0; i < descriptions.size(); i++) {
                OWLClassExpression x = descriptions.get(i);
                if (isThingOrNothing(x)) {
                    continue;
                }
                
                for (int j = i + 1; j < descriptions.size(); j++) {
                    OWLClassExpression y = descriptions.get(j);
                    if (isThingOrNothing(y)) {
                        continue;
                    }

                    if (x instanceof OWLClass && y instanceof OWLClass) {
                        // ignored
                    } else if (x instanceof OWLClass) {
                        for (OWLClass parent: getNamedClasses(y)) {
                            result.add(new Edge(x, parent));
                        }
                    } else if (y instanceof OWLClass) {
                        for (OWLClass parent: getNamedClasses(x)) {
                            result.add(new Edge(y, parent));
                        }
                    } else {
                        // ignored
                    }
                }
            }
            return result;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * the method interprets the passed description and returns all named classes in this restriction that can act as superClasses semantically.
     * 
     * I think only Intersections (and nested intersections) qualify for this.
     * 
     * @param desc
     * @return
     */
    private static Set<OWLClass> getNamedClasses(OWLClassExpression desc) {
        Set<OWLClass> y = new LinkedHashSet<OWLClass>();

        if (desc instanceof OWLObjectIntersectionOf) {
            OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) desc;
            Set<OWLClassExpression> descSet = and.getOperands();
            for (OWLClassExpression innerDesc: descSet) {
                if (innerDesc instanceof OWLClass) {
                    y.add((OWLClass) innerDesc);
                } else {
                    y.addAll(getNamedClasses(innerDesc));
                }
            }
        }
        return y;
    }

    @Override
    public boolean handleNodeEntity(OWLClassExpression entity) {
        return !OWLUtilities.isOWLNothing(entity) && !OWLUtilities.isOWLThing(entity);
    }
}
