/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;

import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;
import com.ontoprise.ontostudio.owl.model.hierarchy.AnnotationPropertyHandler;
import com.ontoprise.ontostudio.owl.model.hierarchy.DataPropertyHandler;
import com.ontoprise.ontostudio.owl.model.hierarchy.EntityHierarchy;
import com.ontoprise.ontostudio.owl.model.hierarchy.EntityHierarchyUpdater;
import com.ontoprise.ontostudio.owl.model.hierarchy.OWLClassHandler;
import com.ontoprise.ontostudio.owl.model.hierarchy.ObjectPropertyHandler;
import com.ontoprise.ontostudio.owl.model.util.Cast;
import com.ontoprise.ontostudio.owl.model.util.Filter;
import com.ontoprise.ontostudio.owl.model.visitors.GetInterfaceTypeVisitor;
import com.ontoprise.ontostudio.owl.model.visitors.GetMemberVisitor;

/**
 * This class is a utility class to simplify the access to the OWL datamodel from GUI plugins.
 * 
 * @author Thomas Krekeler
 * @author Michael Erdmann
 * @author Nico Stieler
 * 
 */
@SuppressWarnings("nls")
public class OWLModelCore implements OWLModel {
    private static Logger _log = Logger.getLogger(OWLModelCore.class);
    private static final Map<Class<?>,Class<? extends OWLEntity>> _primaryTypeByHierarchyType = new LinkedHashMap<Class<?>,Class<? extends OWLEntity>>();
    static {
        _primaryTypeByHierarchyType.put(OWLDataPropertyExpression.class, OWLDataProperty.class);
        _primaryTypeByHierarchyType.put(OWLObjectPropertyExpression.class, OWLObjectProperty.class);
    }

    /** The ontology manager managing <code>_ontology</code> . */
    private final OWLOntologyManager _manager;
    /** The ontology wrapped by this object. */
    private final OWLOntology _ontology;
    /** The project to which the wrapped ontology belongs to. */
    private final IOntologyProject _ontologyProject;
    /** Indicates if the model has been closed. */
    private boolean _closed = false;
    /** Cache for the ontology {@link OWLNamespaces} object. */
    private OWLNamespaces _namespaces;
    /** Cache for {@link OWLSubClassOfAxiom} axioms contained in the ontology (not including the ones imported). */
    private Set<OWLSubClassOfAxiom> _subClassOfAxioms;
    /** Cache for {@link OWLEntity}'s contained in the ontology (not including the ones imported). */
    private Map<Class<? extends OWLEntity>,Set<? extends OWLEntity>> _entities;

    private Map<Class<?>,EntityHierarchyUpdater<?>> _hierarchyUpdaters;
    
    private static String getOntologyURI(OWLOntology ontology) {
        return ontology.getOntologyID().getOntologyIRI().toURI().toString();
    }
    
    @Override
    public void cleanCaches() {
        cleanCaches(false);
    }

    public static boolean containsInConjunctionOrDisjunction(OWLClass clazz, OWLClassExpression domain) {
        if(domain instanceof OWLClass){
            return clazz.equals(domain); 
        }else if(domain instanceof OWLObjectIntersectionOf || domain instanceof OWLObjectUnionOf){
            for(OWLClassExpression operand : ((OWLNaryBooleanClassExpression)domain).getOperands())
                if(containsInConjunctionOrDisjunction(clazz,operand))
                    return true;                
        }
        return false;
    }
    
    private void cleanCaches(boolean changesAffectImportedOntologiesOnly) {
        cleanHierarchies(!changesAffectImportedOntologiesOnly);
        if (!changesAffectImportedOntologiesOnly) {
            cleanNamespaces();
        }
        if (_subClassOfAxioms != null) {
            _subClassOfAxioms.clear();
            _subClassOfAxioms = null;
        }
        if (_entities != null) {
            _entities.clear();
            _entities = null;
        }
    }
    
    private <E> EntityHierarchy<E> getHierarchy(Class<E> type, boolean includeImportedOntologies) throws NeOnCoreException {
        assertHierarchy(type, includeImportedOntologies);
        return Cast.cast(_hierarchyUpdaters.get(type).getHierarchy());
    }
    
    private <E> void assertHierarchy(Class<E> type, boolean includeImportedOntologies) throws NeOnCoreException {
        if (_hierarchyUpdaters.containsKey(type)) {
            if (_hierarchyUpdaters.get(type).isIncludeImportedOntologies() == includeImportedOntologies) {
                _hierarchyUpdaters.get(type).getHierarchy();
                return;
            }
            _hierarchyUpdaters.get(type).close();
        }
        OWLDataFactory factory = getOWLDataFactory();
        EntityHierarchyUpdater.EntityHandler<E> entityHandler = null;
        if (OWLDataPropertyExpression.class.equals(type)) {
            entityHandler = Cast.cast(new DataPropertyHandler());
        } else if (OWLObjectPropertyExpression.class.equals(type)) {
            entityHandler = Cast.cast(new ObjectPropertyHandler());
        } else if (OWLAnnotationProperty.class.equals(type)) {
            entityHandler = Cast.cast(new AnnotationPropertyHandler());
        } else if (OWLClassExpression.class.equals(type)) {
            entityHandler = Cast.cast(new OWLClassHandler(factory));
        } else {
            throw new IllegalArgumentException();
        }
        _hierarchyUpdaters.put(type, new EntityHierarchyUpdater<E>(this, factory, includeImportedOntologies, entityHandler));
    }
    
    private void assertSubClassOfCache() {
        if (_subClassOfAxioms != null) {
            return;
        }
        _subClassOfAxioms = new LinkedHashSet<OWLSubClassOfAxiom>(_ontology.getAxioms(AxiomType.SUBCLASS_OF));
    }
    
    private void assertEntityCache() {
        if (_entities != null) {
            return;
        }
        _entities = new LinkedHashMap<Class<? extends OWLEntity>,Set<? extends OWLEntity>>();
        for (Class<? extends OWLEntity> type: new Class[] { OWLAnnotationProperty.class, OWLDataProperty.class, OWLObjectProperty.class, OWLClass.class, OWLNamedIndividual.class, OWLDatatype.class }) {
            _entities.put(type, new LinkedHashSet<OWLEntity>());
        }
        OWLObjectVisitorEx<?> typeDetector = new GetInterfaceTypeVisitor();
        Set<OWLEntity> allEntities = _ontology.getSignature();
        for (OWLEntity entity: allEntities) {
            Class<? extends OWLEntity> type = Cast.cast(entity.accept(typeDetector));
            Set<OWLEntity> entities = Cast.cast(_entities.get(type));
            if (entities != null) {
                entities.add(entity);
            }
        }
    }

    private void assertNamespaces() throws NeOnCoreException {
        if (_namespaces == null) {
            _namespaces = new OWLNamespaces();
            for (Map.Entry<String,String> entry: getOntologyProject().getNamespacePrefixes(getOntologyURI()).entrySet()) {
                _namespaces.registerPrefix(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * The list of listeners for axiom/entity events.
     */
    private ListenerList _axiomListeners = new ListenerList();

    private Map<OWLAxiomListener,Set<Class<? extends OWLAxiom>>> _listenerClassMapping = new LinkedHashMap<OWLAxiomListener,Set<Class<? extends OWLAxiom>>>();

    /**
     * Indicates if the request methods for entities/axioms should include entities/axioms from imported ontologies.
     * 
     * @return
     */
    private boolean getIncludeImportedOntologies() {
        return OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_IMPORTED);
    }

    private boolean getShowAxiomsInGui() {
        return OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS);
    }

    /**
     * Indicates in which way hits for entities or descriptions should be grouped.
     * 
     * @return At the moment a constant value.
     */
    private OWLModel.ItemHitsGrouping getGroupBy() {
        if (getShowAxiomsInGui()) {
            return OWLModel.ItemHitsGrouping.OnePerAxiom;
        } else {
            return OWLModel.ItemHitsGrouping.OnePerOntology;
        }
    }

    /** A filter returning true iff a given <code>ObjectPropertyExpression</code> is of type <code>ObjectProperty</code>. */
    private static final Filter<OWLObjectPropertyExpression> OBJECT_PROPERTY_FILTER = new Filter<OWLObjectPropertyExpression>() {
        @Override
        public boolean matches(OWLObjectPropertyExpression item) {
            return item instanceof OWLObjectProperty;
        }
    };

    /** A filter returning true iff a given <code>OWLDataRange</code> is of type <code>OWLDataRange</code>. */
    private static final Filter<OWLDataRange> DATARANGE_FILTER = new Filter<OWLDataRange>() {//NICO is this FILTER needed?
        @Override
        public boolean matches(OWLDataRange item) {
            return item instanceof OWLDataRange;
        }
    };
    /** A filter returning true iff a given <code>DataPropertyExpression</code> is of type <code>DataProperty</code>. */
    private static final Filter<OWLDataPropertyExpression> DATA_PROPERTY_FILTER = new Filter<OWLDataPropertyExpression>() {
        @Override
        public boolean matches(OWLDataPropertyExpression item) {
            return item instanceof OWLDataProperty;
        }
    };

    /** A filter returning true iff a given <code>Description</code> is a restriction on a property. */
    private static final Filter<OWLClassExpression> RESTRICTION_FILTER = new Filter<OWLClassExpression>() {
        @Override
        public boolean matches(OWLClassExpression item) {
            return OWLUtilities.isRestriction(item);
        }
    };

    /** A filter returning true iff a given <code>Description</code> is no restriction on a property. */
    private static final Filter<OWLClassExpression> NO_RESTRICTION_FILTER = new Filter<OWLClassExpression>() {
        @Override
        public boolean matches(OWLClassExpression item) {
            return !OWLUtilities.isRestriction(item);
        }
    };
    /** A filter returning true iff a given <code>Description</code> is a restriction on a property. */
    private static final Filter<OWLClassExpression> COMPLEX_DESCRIPTION_AND_NO_RESTRICTION_FILTER = new Filter<OWLClassExpression>() {
        @Override
        public boolean matches(OWLClassExpression item) {
            return (!OWLUtilities.isRestriction(item) && !(item instanceof OWLClass));
        }
    };

    /** A filter returning true iff a given <code>Description</code> is nor an <code>OWLClass</code> neither a restriction on a property. */
    private static final Filter<OWLClassExpression> UNNAMED_NON_RESTRICTION_FILTER = new Filter<OWLClassExpression>() {
        @Override
        public boolean matches(OWLClassExpression item) {
            return !(item instanceof OWLClass) && !OWLUtilities.isRestriction(item);
        }
    };

    /** A filter returning true iff a given <code>Description</code> is an <code>OWLClass</code>. */
    private static final Filter<OWLClassExpression> NAMED_DESCRIPTION_FILTER = new Filter<OWLClassExpression>() {
        @Override
        public boolean matches(OWLClassExpression item) {
            return item instanceof OWLClass;
        }
    };

    /** A filter returning true iff a given <code>Description</code> is not an <code>OWLClass</code>, i.e. a complex class description. */
    private final static Filter<OWLClassExpression> COMPLEX_DESCRIPTION_FILTER = new Filter<OWLClassExpression>() {
        @Override
        public boolean matches(OWLClassExpression item) {
            return !(item instanceof OWLClass);
        }
    };

    /**
     * This is a wrapper class for <code>KAON2Exception</code>s intended to be used within the <code>matches</code> method of a <code>Filter</code> since the
     * <code>matches</code> method do not allow <code>KAON2Exception</code>s to be thrown.
     * 
     * @author krekeler
     * 
     */
    private static class KAON2ExceptionWrapper extends RuntimeException {
        private static final long serialVersionUID = 9092881628560393626L;

        public KAON2ExceptionWrapper(NeOnCoreException e) {
            super(e);
        }
    }

    /**
     * The default implementation for <code>AxiomRequest<AxiomType></code>.<br/>
     * <br/>
     * This implementation iterates over all relevant ontologies and starts a 'native' request for axioms of type <code>AxiomType</code>.<br/>
     * <br/>
     * You may specify conditions for the 'native' request, see the constructor. Additionally you can filter the set of axioms returned by the 'native' request
     * afterwards by passing a filter to the constructor.
     * 
     * @author krekeler
     * 
     * @param <A>
     */
    private class AxiomRequestCore<A extends OWLAxiom> implements AxiomRequest<A> {
        /** The runtime type information about the axiom type for the 'native' request. */
        private org.semanticweb.owlapi.model.AxiomType<? extends OWLAxiom> _requestedAxiomType;
        /** The filter to apply on the result of the 'native' request. */
        private Filter<A> _filter;
        /** The (identifiers of the) conditions to set for the 'native' request. */
        private Collection<String> _conditions;

        /**
         * Uses no filter on the axioms after the 'native' request. See
         * <code>AxiomRequestCore(Class<AxiomType> requestedAxiomType, Filter<AxiomType> filter, String...conditions)</code> for other details.
         * 
         * @param requestedAxiomType
         * @param conditions
         */
        public AxiomRequestCore(org.semanticweb.owlapi.model.AxiomType<? extends OWLAxiom> requestedAxiomType, String... conditions) {
            _requestedAxiomType = requestedAxiomType;
            _filter = null;
            _conditions = Arrays.asList(conditions);
        }

        /**
         * @param requestedAxiomType The runtime type information for the 'native' request.
         * @param filter A filter to apply on the axioms returned by the 'native' request. An axiom is included within the result iff the filter matches for
         *            that axiom.
         * @param conditions A list of conditions to apply to the 'native' request before starting it. These are only the identifiers for the conditions, the
         *            values have to be passed when calling one of the <code>getAxioms</code> methods and must match in count.
         */
        public AxiomRequestCore(org.semanticweb.owlapi.model.AxiomType<? extends OWLAxiom> requestedAxiomType, Filter<A> filter, String... conditions) {
            _requestedAxiomType = requestedAxiomType;
            _filter = filter;
            _conditions = Arrays.asList(conditions);
        }
        
        protected org.semanticweb.owlapi.model.AxiomType<? extends OWLAxiom> getRequestedAxiomType() {
            return _requestedAxiomType;
        }
        
        protected Filter<A> getFilter() {
            return _filter;
        }
        
        protected Collection<String> getConditions() {
            return _conditions;
        }
        
        @SuppressWarnings("unused")
        protected Iterable<A> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getAxioms(_requestedAxiomType));
        }

        @Override
        public Set<LocatedItem<A>> getLocatedAxioms(boolean includeImportedOntologies, Object... parameters) throws NeOnCoreException {
            try {
                Set<LocatedItem<A>> result = new HashSet<LocatedItem<A>>();
                Set<OWLModel> relevantOntologies = getRelevantOntologies(includeImportedOntologies);
                for (OWLModel model: relevantOntologies) {
                    for (A axiom: getAxioms(model.getOntology(), parameters)) {
                        if (_filter == null || _filter.matches(axiom)) {
                            result.add(new LocatedItemCore<A>(axiom, model.getOntologyURI()));
                        }
                    }
                }
                return result;
            } catch (KAON2ExceptionWrapper e) {
                throw (NeOnCoreException) e.getCause();
            }
        }

        @Override
        public Set<A> getAxioms(boolean includeImportedOntologies, Object... parameters) throws NeOnCoreException {
            Set<A> result = new HashSet<A>();
            for (LocatedItem<A> locatedAxiom: getLocatedAxioms(includeImportedOntologies, parameters)) {
                result.add(locatedAxiom.getItem());
            }
            return result;
        }

        @Override
        public Set<A> getAxioms(Object... parameters) throws NeOnCoreException {
            return getAxioms(getIncludeImportedOntologies(), parameters);
        }

        @Override
        public Set<LocatedItem<A>> getLocatedAxioms(Object... parameters) throws NeOnCoreException {
            return getLocatedAxioms(getIncludeImportedOntologies(), parameters);
        }
    }
    
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // josp 2009-06-10: Strange compile error, if next line is a javadoc comment: 
    // ERROR in OWLModelCore.java (at line 0)
    // Internal compiler error
    // at org.eclipse.jdt.internal.compiler.ast.Javadoc.resolveTypeParameterTags(Javadoc.java:531)
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    /*
     * An axiom request which asks all relevant ontologies if they contain a given axiom. Each occurrence is added to the result.
     * 
     * @author krekeler
     * 
     * @param <AxiomType>
     */

    /*
     * What follows are the <code>AxiomRequest</code>s primarily used within this class.<br/> <br/> The naming convention is as follows:<br/> If asking for
     * axioms of type AxiomType with the additional conditions condition0, condition1, ..., conditionN the name for the AxiomRequest is
     * AxiomType_condition0_condition1_..._conditionN_Request.<br/> <br/> Example: <code>SubClassOf_subDescription </code> is the <code>AxiomRequest</code>
     * which asks for <code>SubClassOf</code> axioms for which <code>getSubDescription()</code> is as specified within the parameters when calling
     * <code>getAxioms</code> on <code>SubClassOf_subDescription</code>.
     */
    private final AxiomRequest<OWLClassAssertionAxiom> ClassMember_description_Request = new AxiomRequestCore<OWLClassAssertionAxiom>(AxiomType.CLASS_ASSERTION, "description") {
        @Override
        protected Iterable<OWLClassAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getClassAssertionAxioms((OWLClass)parameters[0]);
        }
    };
    private final AxiomRequest<OWLClassAssertionAxiom> ClassMember_individual_Request = new AxiomRequestCore<OWLClassAssertionAxiom>(AxiomType.CLASS_ASSERTION, "individual") {
        @Override
        protected Iterable<OWLClassAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getClassAssertionAxioms((OWLIndividual)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDataPropertyCharacteristicAxiom> OWLFunctionalDataPropertyAxiom_dataProperty_Request = new AxiomRequestCore<OWLDataPropertyCharacteristicAxiom>(AxiomType.FUNCTIONAL_DATA_PROPERTY, "dataProperty") {
        @Override
        protected Iterable<OWLDataPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getFunctionalDataPropertyAxioms((OWLDataPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLDataPropertyDomainAxiom> DataPropertyDomain_dataProperty_Request = new AxiomRequestCore<OWLDataPropertyDomainAxiom>(AxiomType.DATA_PROPERTY_DOMAIN, "dataProperty") {
        @Override
        protected Iterable<OWLDataPropertyDomainAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDataPropertyDomainAxioms((OWLDataProperty)parameters[0]);
        }
    }; 
    private final AxiomRequest<OWLDataPropertyRangeAxiom> DataPropertyRange_dataProperty_Request = new AxiomRequestCore<OWLDataPropertyRangeAxiom>(AxiomType.DATA_PROPERTY_RANGE, "dataProperty") {
        @Override
        protected Iterable<OWLDataPropertyRangeAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDataPropertyRangeAxioms((OWLDataProperty)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDataPropertyDomainAxiom> DataPropertyDomain_domain_Request = new AxiomRequestCore<OWLDataPropertyDomainAxiom>(AxiomType.DATA_PROPERTY_DOMAIN, "domain") {
        @Override
        protected Iterable<OWLDataPropertyDomainAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLClass clazz = (OWLClass)parameters[0];
//            OWLClassExpression clazz = (OWLClassExpression)parameters[0];
            Set<OWLDataPropertyDomainAxiom> result = new LinkedHashSet<OWLDataPropertyDomainAxiom>();
            for (OWLDataPropertyDomainAxiom axiom: ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
                if (containsInConjunctionOrDisjunction(clazz,axiom.getDomain())) {
                    result.add(axiom);
                }
//                if (clazz.equals(axiom.getDomain())) {
//                    result.add(axiom);
//                }
            }
            return result;
        }

    }; 
    private final AxiomRequest<OWLDataPropertyRangeAxiom> DataPropertyRange_range_Request = new AxiomRequestCore<OWLDataPropertyRangeAxiom>(AxiomType.DATA_PROPERTY_RANGE, "range") {
        @Override
        protected Iterable<OWLDataPropertyRangeAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLDatatype dataType = (OWLDatatype)parameters[0];
            Set<OWLDataPropertyRangeAxiom> result = new LinkedHashSet<OWLDataPropertyRangeAxiom>();
            for (OWLDataPropertyRangeAxiom axiom: ontology.getAxioms(AxiomType.DATA_PROPERTY_RANGE)) {
                if (dataType.equals(axiom.getRange())) {
                    result.add(axiom);
                }
            }
            return result;
        }
    };
    private final AxiomRequest<OWLDataPropertyAssertionAxiom> DataPropertyMember_dataProperty_sourceIndividual_Request = new AxiomRequestCore<OWLDataPropertyAssertionAxiom>(AxiomType.DATA_PROPERTY_ASSERTION, "dataProperty", "sourceIndividual") {
        @Override
        protected Iterable<OWLDataPropertyAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLDataPropertyExpression property = (OWLDataPropertyExpression)parameters[0];
            Set<OWLDataPropertyAssertionAxiom> result = new LinkedHashSet<OWLDataPropertyAssertionAxiom>();
            for (OWLDataPropertyAssertionAxiom axiom: ontology.getDataPropertyAssertionAxioms((OWLIndividual)parameters[1])) {
                if (property.equals(axiom.getProperty())) {
                    result.add(axiom);
                }
            }
            return result;
        }
    }; 
    private final AxiomRequest<OWLDataPropertyAssertionAxiom> DataPropertyMember_sourceIndividual_Request = new AxiomRequestCore<OWLDataPropertyAssertionAxiom>(AxiomType.DATA_PROPERTY_ASSERTION, "sourceIndividual") {
        @Override
        protected Iterable<OWLDataPropertyAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDataPropertyAssertionAxioms((OWLIndividual)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDataPropertyAssertionAxiom> DataPropertyMember_property_Request = new AxiomRequestCore<OWLDataPropertyAssertionAxiom>(AxiomType.DATA_PROPERTY_ASSERTION, "property") {
        @Override
        protected Iterable<OWLDataPropertyAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            Set<OWLDataPropertyAssertionAxiom> result = new LinkedHashSet<OWLDataPropertyAssertionAxiom>();
            if(parameters[0] instanceof OWLDataProperty) {
                OWLDataProperty property = (OWLDataProperty) parameters[0];
                for (OWLDataPropertyAssertionAxiom axiom: ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION)) {
                    if (property.equals(axiom.getProperty())) {
                        result.add(axiom);
                    }
                }
            }
            return result;
        }
    };
    private final AxiomRequest<OWLDifferentIndividualsAxiom> DifferentIndividuals_individuals_Request = new AxiomRequestCore<OWLDifferentIndividualsAxiom>(AxiomType.DIFFERENT_INDIVIDUALS, "individuals") {
        @Override
        protected Iterable<OWLDifferentIndividualsAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDifferentIndividualAxioms((OWLIndividual)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDisjointClassesAxiom> DisjointClasses_descriptions_Request = new AxiomRequestCore<OWLDisjointClassesAxiom>(AxiomType.DISJOINT_CLASSES, "descriptions") {
        @Override
        protected Iterable<OWLDisjointClassesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDisjointClassesAxioms((OWLClass)parameters[0]);
        }
    };
    private static Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(final OWLOntology ontology, final OWLAnnotationSubject subject) {
        return ontology.getAnnotationAssertionAxioms(subject);
    }
    private final AxiomRequest<OWLAnnotationAssertionAxiom> Annotation_annotationProperty_Request = new AxiomRequestCore<OWLAnnotationAssertionAxiom>(AxiomType.ANNOTATION_ASSERTION, "annotationProperty", "subject") {
        @Override
        protected Iterable<OWLAnnotationAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLAnnotationProperty property = (OWLAnnotationProperty)parameters[0];
            OWLAnnotationSubject subject = (OWLAnnotationSubject)parameters[1];
            Set<OWLAnnotationAssertionAxiom> result = new LinkedHashSet<OWLAnnotationAssertionAxiom>();
            for (OWLAnnotationAssertionAxiom axiom: getAnnotationAssertionAxioms(ontology, subject)) {
                if (property.equals(axiom.getProperty())) {
                    result.add(axiom);
                }
            }
            return result;
        }
    };
    private final AxiomRequest<OWLAnnotationPropertyDomainAxiom> AnnotationPropertyDomain_domain_Request = new AxiomRequestCore<OWLAnnotationPropertyDomainAxiom>(AxiomType.ANNOTATION_PROPERTY_DOMAIN, "domain") {
        @Override
        protected Iterable<OWLAnnotationPropertyDomainAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            Set<OWLAnnotationPropertyDomainAxiom> result = new LinkedHashSet<OWLAnnotationPropertyDomainAxiom>();
            if(parameters[0] instanceof OWLClass) {
                IRI clazz = ((OWLClass)parameters[0]).getIRI();
                for (OWLAnnotationPropertyDomainAxiom axiom: ontology.getAxioms(AxiomType.ANNOTATION_PROPERTY_DOMAIN)) {
                    if (clazz.equals(axiom.getDomain())) {
                        result.add(axiom);
                    }
                }
            }
            return result;
        }
    }; 


    private final AxiomRequest<OWLAnnotationPropertyRangeAxiom> AnnotationPropertyRange_range_Request = new AxiomRequestCore<OWLAnnotationPropertyRangeAxiom>(AxiomType.ANNOTATION_PROPERTY_RANGE, "range") {
        @Override
        protected Iterable<OWLAnnotationPropertyRangeAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            Set<OWLAnnotationPropertyRangeAxiom> result = new LinkedHashSet<OWLAnnotationPropertyRangeAxiom>();
            if(parameters[0] instanceof OWLClass) {
                IRI clazz = ((OWLClass)parameters[0]).getIRI();
                for (OWLAnnotationPropertyRangeAxiom axiom: ontology.getAxioms(AxiomType.ANNOTATION_PROPERTY_RANGE)) {
                    if (clazz.equals(axiom.getRange())) {
                        result.add(axiom);
                    }
                }
            }
            return result;
        }
    }; 
    private final AxiomRequest<OWLAnnotationAssertionAxiom> EntityAnnotation_entity_Request = new AxiomRequestCore<OWLAnnotationAssertionAxiom>(AxiomType.ANNOTATION_ASSERTION, "entity") {
        @Override
        protected Iterable<OWLAnnotationAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return getAnnotationAssertionAxioms(ontology, (OWLAnnotationSubject)parameters[0]);
        }
    };

    private final AxiomRequest<OWLAnnotationAssertionAxiom> EntityAnnotation_property_Request = new AxiomRequestCore<OWLAnnotationAssertionAxiom>(AxiomType.ANNOTATION_ASSERTION, "property") {
        @Override
        protected Iterable<OWLAnnotationAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLAnnotationProperty property = (OWLAnnotationProperty)parameters[0];
            Set<OWLAnnotationAssertionAxiom> result = new LinkedHashSet<OWLAnnotationAssertionAxiom>();
            for (OWLAnnotationAssertionAxiom axiom: ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
                if (property.equals(axiom.getProperty())) {
                    result.add(axiom);
                }
            }
            return result;
        }
    };

    private final AxiomRequest<OWLEquivalentClassesAxiom> EquivalentClasses_descriptions_Request = new AxiomRequestCore<OWLEquivalentClassesAxiom>(AxiomType.EQUIVALENT_CLASSES, "descriptions") {
        @Override
        protected Iterable<OWLEquivalentClassesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getEquivalentClassesAxioms((OWLClass)parameters[0]);
        }
    };
    private final AxiomRequest<OWLEquivalentDataPropertiesAxiom> EquivalentDataProperties_dataProperties_Request = new AxiomRequestCore<OWLEquivalentDataPropertiesAxiom>(AxiomType.EQUIVALENT_DATA_PROPERTIES, "dataProperties") {
        @Override
        protected Iterable<OWLEquivalentDataPropertiesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getEquivalentDataPropertiesAxioms((OWLDataProperty)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDisjointDataPropertiesAxiom> DisjointDataProperties_dataProperties_Request = new AxiomRequestCore<OWLDisjointDataPropertiesAxiom>(AxiomType.DISJOINT_DATA_PROPERTIES, "dataProperties") {
        @Override
        protected Iterable<OWLDisjointDataPropertiesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDisjointDataPropertiesAxioms((OWLDataProperty)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDatatypeDefinitionAxiom> SubDatatype_Request = new AxiomRequestCore<OWLDatatypeDefinitionAxiom>(AxiomType.DATATYPE_DEFINITION, "datatype") {
        @Override
        protected Iterable<OWLDatatypeDefinitionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDatatypeDefinitions((OWLDatatype)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDatatypeDefinitionAxiom> SuperDatatype_Request = new AxiomRequestCore<OWLDatatypeDefinitionAxiom>(AxiomType.DATATYPE_DEFINITION, "datatype") {
        @Override
        protected Iterable<OWLDatatypeDefinitionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            Set<OWLDatatypeDefinitionAxiom> axioms = new HashSet<OWLDatatypeDefinitionAxiom>();
            Set<OWLDatatypeDefinitionAxiom> fittingAxioms = new HashSet<OWLDatatypeDefinitionAxiom>();
            Set<OWLDatatype> datatypes = ontology.getDatatypesInSignature();
            for(OWLDatatype type : datatypes)
                axioms.addAll(ontology.getDatatypeDefinitions(type));
            for(OWLDatatypeDefinitionAxiom axiom : axioms)
                if(axiom.getDataRange().equals((OWLDatatype)parameters[0]))
                    fittingAxioms.add(axiom);
            return fittingAxioms;
        }
    };
    private final AxiomRequest<OWLEquivalentObjectPropertiesAxiom> EquivalentObjectProperties_objectProperties_Request = new AxiomRequestCore<OWLEquivalentObjectPropertiesAxiom>(AxiomType.EQUIVALENT_OBJECT_PROPERTIES, "objectProperties") {
        @Override
        protected Iterable<OWLEquivalentObjectPropertiesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getEquivalentObjectPropertiesAxioms((OWLObjectPropertyExpression)parameters[0]);
        }
    };
    private final AxiomRequest<OWLDisjointObjectPropertiesAxiom> DisjointObjectProperties_objectProperties_Request = new AxiomRequestCore<OWLDisjointObjectPropertiesAxiom>(AxiomType.DISJOINT_OBJECT_PROPERTIES, "objectProperties") {
        @Override
        protected Iterable<OWLDisjointObjectPropertiesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDisjointObjectPropertiesAxioms((OWLObjectPropertyExpression)parameters[0]);
        }
    };
    private final AxiomRequest<OWLInverseObjectPropertiesAxiom> InverseObjectProperties_first_Request = new AxiomRequestCore<OWLInverseObjectPropertiesAxiom>(AxiomType.INVERSE_OBJECT_PROPERTIES, "first") {
        @Override
        protected Iterable<OWLInverseObjectPropertiesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLObjectPropertyExpression objectProperty = (OWLObjectPropertyExpression)parameters[0];
            Set<OWLInverseObjectPropertiesAxiom> result = new LinkedHashSet<OWLInverseObjectPropertiesAxiom>();
            for (OWLInverseObjectPropertiesAxiom axiom: ontology.getInverseObjectPropertyAxioms(objectProperty)) {
                if (objectProperty.equals(axiom.getFirstProperty())) {
                    result.add(axiom);
                }
            }
            return result;
        }
    };
    private final AxiomRequest<OWLInverseObjectPropertiesAxiom> InverseObjectProperties_second_Request = new AxiomRequestCore<OWLInverseObjectPropertiesAxiom>(AxiomType.INVERSE_OBJECT_PROPERTIES, "second") {
        @Override
        protected Iterable<OWLInverseObjectPropertiesAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLObjectPropertyExpression objectProperty = (OWLObjectPropertyExpression)parameters[0];
            Set<OWLInverseObjectPropertiesAxiom> result = new LinkedHashSet<OWLInverseObjectPropertiesAxiom>();
            for (OWLInverseObjectPropertiesAxiom axiom: ontology.getInverseObjectPropertyAxioms(objectProperty)) {
                if (objectProperty.equals(axiom.getSecondProperty())) {
                    result.add(axiom);
                }
            }
            return result;
        }
    };
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLTransitiveObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.TRANSITIVE_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getTransitiveObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLFunctionalObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.FUNCTIONAL_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getFunctionalObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLSymmetricObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.SYMMETRIC_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getSymmetricObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLAsymmetricObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.SYMMETRIC_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getAsymmetricObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLReflexiveObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.REFLEXIVE_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getReflexiveObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLIrreflexiveObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getIrreflexiveObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLInverseFunctionalObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getInverseFunctionalObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    @SuppressWarnings("unused")
    private final AxiomRequest<OWLObjectPropertyCharacteristicAxiom> OWLAntiSymmetricObjectPropertyAxiom_property_Request = new AxiomRequestCore<OWLObjectPropertyCharacteristicAxiom>(AxiomType.ASYMMETRIC_OBJECT_PROPERTY, "property") {
        @Override
        protected Iterable<OWLObjectPropertyCharacteristicAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return Cast.cast(ontology.getAsymmetricObjectPropertyAxioms((OWLObjectPropertyExpression)parameters[0]));
        }
    };
    private final AxiomRequest<OWLObjectPropertyDomainAxiom> ObjectPropertyDomain_objectProperty_Request = new AxiomRequestCore<OWLObjectPropertyDomainAxiom>(AxiomType.OBJECT_PROPERTY_DOMAIN, "objectProperty") {
        @Override
        protected Iterable<OWLObjectPropertyDomainAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getObjectPropertyDomainAxioms((OWLObjectPropertyExpression)parameters[0]);
        }
    };
    private final AxiomRequest<OWLObjectPropertyDomainAxiom> ObjectPropertyDomain_domain_Request = new AxiomRequestCore<OWLObjectPropertyDomainAxiom>(AxiomType.OBJECT_PROPERTY_DOMAIN, "domain") {
        @Override
        protected Iterable<OWLObjectPropertyDomainAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLClass clazz = (OWLClass)parameters[0];
            Set<OWLObjectPropertyDomainAxiom> result = new LinkedHashSet<OWLObjectPropertyDomainAxiom>();
            for (OWLObjectPropertyDomainAxiom axiom: ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
                if (containsInConjunctionOrDisjunction(clazz, axiom.getDomain())) {
                    result.add(axiom);
                }
//                if (clazz.equals(axiom.getDomain())) {
//                    result.add(axiom);
//                }
            }
            return result;
        }
    }; 
    private final AxiomRequest<OWLObjectPropertyRangeAxiom> ObjectPropertyRange_range_Request = new AxiomRequestCore<OWLObjectPropertyRangeAxiom>(AxiomType.OBJECT_PROPERTY_RANGE, "range") {
        @Override
        protected Iterable<OWLObjectPropertyRangeAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLClass clazz = (OWLClass)parameters[0];
            Set<OWLObjectPropertyRangeAxiom> result = new LinkedHashSet<OWLObjectPropertyRangeAxiom>();
            for (OWLObjectPropertyRangeAxiom axiom: ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
                if (containsInConjunctionOrDisjunction(clazz, axiom.getRange())) {
                    result.add(axiom);
                    }
//                if (clazz.equals(axiom.getRange())) {
//                    result.add(axiom);
//                }
            }
            return result;
        }

    }; 
    private final AxiomRequest<OWLObjectPropertyAssertionAxiom> ObjectPropertyMember_objectProperty_sourceIndividual_Request = new AxiomRequestCore<OWLObjectPropertyAssertionAxiom>(AxiomType.OBJECT_PROPERTY_ASSERTION, "objectProperty", "sourceIndividual") {
        @Override
        protected Iterable<OWLObjectPropertyAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLObjectPropertyExpression property = (OWLObjectPropertyExpression)parameters[0];
            Set<OWLObjectPropertyAssertionAxiom> result = new LinkedHashSet<OWLObjectPropertyAssertionAxiom>();
            for (OWLObjectPropertyAssertionAxiom axiom: ontology.getObjectPropertyAssertionAxioms((OWLIndividual)parameters[1])) {
                if (property.equals(axiom.getProperty())) {
                    result.add(axiom);
                }
            }
            return result;
        }
    }; 
    private final AxiomRequest<OWLObjectPropertyAssertionAxiom> ObjectPropertyMember_property_Request = new AxiomRequestCore<OWLObjectPropertyAssertionAxiom>(AxiomType.OBJECT_PROPERTY_ASSERTION, "property") {
        @Override
        protected Iterable<OWLObjectPropertyAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            Set<OWLObjectPropertyAssertionAxiom> result = new LinkedHashSet<OWLObjectPropertyAssertionAxiom>();
            if(parameters[0] instanceof OWLObjectProperty) {
                OWLObjectProperty property = (OWLObjectProperty) parameters[0];
                for (OWLObjectPropertyAssertionAxiom axiom: ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
                    if (property.equals(axiom.getProperty())) {
                        result.add(axiom);
                    }
                }
            }
            return result;
        }
    };
    private final AxiomRequest<OWLObjectPropertyAssertionAxiom> ObjectPropertyMember_sourceIndividual_Request = new AxiomRequestCore<OWLObjectPropertyAssertionAxiom>(AxiomType.OBJECT_PROPERTY_ASSERTION, "sourceIndividual") {
        @Override
        protected Iterable<OWLObjectPropertyAssertionAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getObjectPropertyAssertionAxioms((OWLIndividual)parameters[0]);
        }
    };
    private final AxiomRequest<OWLObjectPropertyRangeAxiom> ObjectPropertyRange_objectProperty_Request = new AxiomRequestCore<OWLObjectPropertyRangeAxiom>(AxiomType.OBJECT_PROPERTY_RANGE, "objectProperty") {
        @Override
        protected Iterable<OWLObjectPropertyRangeAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getObjectPropertyRangeAxioms((OWLObjectPropertyExpression)parameters[0]);
        }
    };

    private final AxiomRequest<OWLAnnotationPropertyRangeAxiom> AnnotationPropertyRange_annotationProperty_Request = new AxiomRequestCore<OWLAnnotationPropertyRangeAxiom>(AxiomType.ANNOTATION_PROPERTY_RANGE, "annotationProperty") {
        @Override
        protected Iterable<OWLAnnotationPropertyRangeAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getAnnotationPropertyRangeAxioms((OWLAnnotationProperty)parameters[0]);
        }
    };
    private final AxiomRequest<OWLAnnotationPropertyDomainAxiom> AnnotationPropertyDomain_annotationProperty_Request = new AxiomRequestCore<OWLAnnotationPropertyDomainAxiom>(AxiomType.ANNOTATION_PROPERTY_DOMAIN, "annotationProperty") {
        @Override
        protected Iterable<OWLAnnotationPropertyDomainAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getAnnotationPropertyDomainAxioms((OWLAnnotationProperty)parameters[0]);
        }
    };
    
    private final AxiomRequest<OWLSameIndividualAxiom> SameIndividual_individuals_Request = new AxiomRequestCore<OWLSameIndividualAxiom>(AxiomType.SAME_INDIVIDUAL, "individuals") {
        @Override
        protected Iterable<OWLSameIndividualAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getSameIndividualAxioms((OWLIndividual)parameters[0]);
        }
    };
    private final AxiomRequest<OWLSubClassOfAxiom> SubClassOf_subDescription_Request = new AxiomRequestCore<OWLSubClassOfAxiom>(AxiomType.SUBCLASS_OF, "subDescription") {
        @Override
        protected Iterable<OWLSubClassOfAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getSubClassAxiomsForSubClass((OWLClass)parameters[0]);
        }
    };
    private final AxiomRequest<OWLSubClassOfAxiom> SubClassOf_superDescription_Request = new AxiomRequestCore<OWLSubClassOfAxiom>(AxiomType.SUBCLASS_OF, "superDescription") {
        @Override
        protected Iterable<OWLSubClassOfAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getSubClassAxiomsForSuperClass((OWLClass)parameters[0]);
        }
    };
    private final AxiomRequest<OWLSubDataPropertyOfAxiom> SubDataPropertyOf_subDataProperty_Request = new AxiomRequestCore<OWLSubDataPropertyOfAxiom>(AxiomType.SUB_DATA_PROPERTY, "subDataProperty") {
        @Override
        protected Iterable<OWLSubDataPropertyOfAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getDataSubPropertyAxiomsForSubProperty((OWLDataProperty)parameters[0]);
        }
    };
    private final AxiomRequest<OWLSubObjectPropertyOfAxiom> OWLObjectSubPropertyAxiom_subProperty_Request = new AxiomRequestCore<OWLSubObjectPropertyOfAxiom>(AxiomType.SUB_OBJECT_PROPERTY, "subObjectProperties") {
        @Override
        protected Iterable<OWLSubObjectPropertyOfAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getObjectSubPropertyAxiomsForSubProperty((OWLObjectPropertyExpression)parameters[0]);
        }
    };
    private final AxiomRequest<OWLSubAnnotationPropertyOfAxiom> SubAnnotationPropertyOf_subProperty_Request = new AxiomRequestCore<OWLSubAnnotationPropertyOfAxiom>(AxiomType.SUB_ANNOTATION_PROPERTY_OF, "superAnnotationProperty") {
        @Override
        protected Iterable<OWLSubAnnotationPropertyOfAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getSubAnnotationPropertyOfAxioms((OWLAnnotationProperty)parameters[0]);
        }
    };
    private final AxiomRequest<OWLSubPropertyChainOfAxiom> OWLSubPropertyChainOfAxiom_superDescription_Request = new AxiomRequestCore<OWLSubPropertyChainOfAxiom>(AxiomType.SUB_PROPERTY_CHAIN_OF, "superObjectProperty") {
        @Override
        protected Iterable<OWLSubPropertyChainOfAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            OWLObjectPropertyExpression superProperty = (OWLObjectPropertyExpression)parameters[0];
            Set<OWLSubPropertyChainOfAxiom> result = new LinkedHashSet<OWLSubPropertyChainOfAxiom>();
            for (OWLSubPropertyChainOfAxiom axiom: ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
                if (superProperty.equals(axiom.getSuperProperty()))
                    result.add(axiom);
            }
            return result;
        }
    };
    /**
     * Request for <code>OWLAxiom</code>s which contain a at least one <code>OWLEntity</code> from a given set of <code>OWLEntity</code>s.
     */
    private final AxiomRequest<OWLAxiom> OWLAxiom_containsEntity_Request = new AxiomRequestCore<OWLAxiom>(null) {
        @Override
        protected Iterable<OWLAxiom> getAxioms(OWLOntology ontology, Object[] parameters) throws NeOnCoreException {
            return ontology.getReferencingAxioms((OWLEntity)parameters[0]);
        }
    };

    /**
     * The default implementation for <code>ItemCollectorCore</code>.<br/>
     * <br/>
     * This implementation gets all items from the given set of axioms, which are related to the axioms in a specific member role. For example, the axiom
     * <code>SubClassOf</code> has the member method <code>getSubDescription()</code>. So, one can use an <code>ItemCollectorCore</code> to get all descriptions
     * (items) which are related to the axioms as sub description.
     * 
     * @author krekeler
     * 
     * @param <ItemType>
     * @param <A>
     */
    private class ItemCollectorCore<ItemType, A extends OWLAxiom> implements ItemCollector<ItemType,A> {
        /** The member to collect items from. */
        private String _member;
        /** The runtime type info about the member. */
        private Class<ItemType> _memberType;
        /** A filter to apply to the resulting set. */
        private Filter<ItemType> _filter;

        /**
         * See <code>ItemCollectorCore(String, Class<ItemType>, Filter<ItemType>)</code>. This constructor associates no filter with the item collector.
         * 
         * @param member
         * @param memberType
         */
        public ItemCollectorCore(String member, Class<ItemType> memberType) {
            _member = member;
            _memberType = memberType;
            _filter = null;
        }

        /**
         * 
         * @param member The member (field/property) of the axioms to collect items from.
         * @param memberType The runtime type info for member.
         * @param filter A filter to apply an the resulting set.
         */
        public ItemCollectorCore(String member, Class<ItemType> memberType, Filter<ItemType> filter) {
            _member = member;
            _memberType = memberType;
            _filter = filter;
        }

        private class ExcludeFilter<E> implements Filter<E> {
            private E _item;

            public ExcludeFilter(E item) {
                if (item == null) {
                    throw new IllegalArgumentException();
                }
                _item = item;
            }

            @Override
            public boolean matches(E item) {
                return !_item.equals(item);
            }

        }

        private Filter<ItemType> createPerCallFilter(Object... parameters) {
            if (parameters == null || parameters.length == 0) {
                return null;
            }
            if (parameters.length == 1) {
                @SuppressWarnings("unchecked")
                ItemType item = (ItemType) parameters[0];
                if (!_memberType.isInstance(item)) {
                    throw new IllegalArgumentException();
                }
                return new ExcludeFilter<ItemType>(item);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<ItemHits<ItemType,A>> getItemHits(OWLModel.ItemHitsGrouping groupBy, Set<LocatedItem<A>> source, Object... parameters) {
            return getItemHits(_member, _memberType, source, groupBy, _filter, createPerCallFilter(parameters));
        }

        @Override
        public Set<ItemHits<ItemType,A>> getItemHits(AxiomRequest<A> source, Object[] sourceParameters, Object... parameters) throws NeOnCoreException {
            return getItemHits(_member, _memberType, source.getLocatedAxioms(getIncludeImportedOntologies(), sourceParameters), getGroupBy(), _filter, createPerCallFilter(parameters));
        }

        @Override
        public Set<ItemType> getItems(OWLModel.ItemHitsGrouping groupBy, Set<LocatedItem<A>> source, Object... parameters) {
            return getItemsFromItemHits(getItemHits(groupBy, source, parameters));
        }

        @Override
        public Set<ItemType> getItems(AxiomRequest<A> source, Object[] sourceParameters, Object... parameters) throws NeOnCoreException {
            return getItemsFromItemHits(getItemHits(source, sourceParameters, parameters));
        }

        private Set<ItemHits<ItemType,A>> getItemHits(String member, Class<ItemType> memberType, Set<LocatedItem<A>> source, OWLModel.ItemHitsGrouping groupBy, Filter<ItemType> filter, Filter<ItemType> perCallFilter) {
            switch (groupBy) {
                case OnePerAxiom:
                    return getItemHitsPerAxiom(member, memberType, source, filter, perCallFilter);
                case OnePerOntology:
                    return getItemHitsPerOntology(member, memberType, source, filter, perCallFilter);
                case OneForThisOneForOtherOntologies:
                    return getItemHitsOneForThisOneForOtherOntologies(member, memberType, source, filter, perCallFilter);
                case OneAtAll:
                    return getItemHitsUndifferentiated(member, memberType, source, filter, perCallFilter);
                default:
                    throw new UnsupportedOperationException();
            }
        }

        private Set<ItemHits<ItemType,A>> getItemHitsOneForThisOneForOtherOntologies(String member, Class<ItemType> memberType, Set<LocatedItem<A>> axioms, Filter<ItemType> filter, Filter<ItemType> perCallFilter) {
            Map<ItemType,Map<Boolean,Set<LocatedItem<A>>>> axiomsByEntity = new HashMap<ItemType,Map<Boolean,Set<LocatedItem<A>>>>();
            for (LocatedItem<A> locatedAxiom: axioms) {
                Collection<ItemType> entities = getItems(member, memberType, locatedAxiom.getItem(), filter, perCallFilter);
                for (ItemType entity: entities) {
                    if (!axiomsByEntity.containsKey(entity)) {
                        axiomsByEntity.put(entity, new HashMap<Boolean,Set<LocatedItem<A>>>());
                    }
                    Map<Boolean,Set<LocatedItem<A>>> axiomsByOntology = axiomsByEntity.get(entity);
                    Boolean key = (locatedAxiom.getOntologyURI().equals(getOntologyURI(getOntology()))) ? Boolean.TRUE : Boolean.FALSE;
                    if (!axiomsByOntology.containsKey(key)) {
                        axiomsByOntology.put(key, new HashSet<LocatedItem<A>>());
                    }
                    axiomsByOntology.get(key).add(locatedAxiom);
                }
            }
            Set<ItemHits<ItemType,A>> result = new HashSet<ItemHits<ItemType,A>>();
            for (Entry<ItemType,Map<Boolean,Set<LocatedItem<A>>>> entry: axiomsByEntity.entrySet()) {
                for (Set<LocatedItem<A>> values: entry.getValue().values()) {
                    result.add(new ItemHitsCore<ItemType,A>(entry.getKey(), values));
                }
            }
            return result;
        }

        private Set<ItemHits<ItemType,A>> getItemHitsPerOntology(String member, Class<ItemType> memberType, Set<LocatedItem<A>> axioms, Filter<ItemType> filter, Filter<ItemType> perCallFilter) {
            Map<ItemType,Map<String,Set<LocatedItem<A>>>> axiomsByEntity = new HashMap<ItemType,Map<String,Set<LocatedItem<A>>>>();
            for (LocatedItem<A> locatedAxiom: axioms) {
                Collection<ItemType> entities = getItems(member, memberType, locatedAxiom.getItem(), filter, perCallFilter);
                for (ItemType entity: entities) {
                    if (!axiomsByEntity.containsKey(entity)) {
                        axiomsByEntity.put(entity, new HashMap<String,Set<LocatedItem<A>>>());
                    }
                    Map<String,Set<LocatedItem<A>>> axiomsByOntology = axiomsByEntity.get(entity);
                    if (!axiomsByOntology.containsKey(locatedAxiom.getOntologyURI())) {
                        axiomsByOntology.put(locatedAxiom.getOntologyURI(), new HashSet<LocatedItem<A>>());
                    }
                    axiomsByOntology.get(locatedAxiom.getOntologyURI()).add(locatedAxiom);
                }
            }
            Set<ItemHits<ItemType,A>> result = new HashSet<ItemHits<ItemType,A>>();
            for (Entry<ItemType,Map<String,Set<LocatedItem<A>>>> entry: axiomsByEntity.entrySet()) {
                for (Set<LocatedItem<A>> values: entry.getValue().values()) {
                    result.add(new ItemHitsCore<ItemType,A>(entry.getKey(), values));
                }
            }
            return result;
        }

        private Set<ItemHits<ItemType,A>> getItemHitsUndifferentiated(String member, Class<ItemType> memberType, Set<LocatedItem<A>> axioms, Filter<ItemType> filter, Filter<ItemType> perCallFilter) {
            Map<ItemType,Set<LocatedItem<A>>> axiomsByEntity = new HashMap<ItemType,Set<LocatedItem<A>>>();
            for (LocatedItem<A> locatedAxiom: axioms) {
                Collection<ItemType> entities = getItems(member, memberType, locatedAxiom.getItem(), filter, perCallFilter);
                for (ItemType entity: entities) {
                    if (!axiomsByEntity.containsKey(entity)) {
                        Set<LocatedItem<A>> set = new HashSet<LocatedItem<A>>();
                        set.add(locatedAxiom);
                        axiomsByEntity.put(entity, set);
                    } else {
                        axiomsByEntity.get(entity).add(locatedAxiom);
                    }
                }
            }
            Set<ItemHits<ItemType,A>> result = new HashSet<ItemHits<ItemType,A>>();
            for (Entry<ItemType,Set<LocatedItem<A>>> entry: axiomsByEntity.entrySet()) {
                result.add(new ItemHitsCore<ItemType,A>(entry.getKey(), entry.getValue()));
            }
            return result;
        }

        private Set<ItemHits<ItemType,A>> getItemHitsPerAxiom(String member, Class<ItemType> memberType, Set<LocatedItem<A>> axioms, Filter<ItemType> filter, Filter<ItemType> perCallFilter) {
            Set<ItemHits<ItemType,A>> result = new HashSet<ItemHits<ItemType,A>>();
            for (LocatedItem<A> locatedAxiom: axioms) {
                Collection<ItemType> entities = getItems(member, memberType, locatedAxiom.getItem(), filter, perCallFilter);
                for (ItemType entity: entities) {
                    result.add(new ItemHitsCore<ItemType,A>(entity, locatedAxiom));
                }
            }
            return result;
        }

        /**
         * Get entities related to an axiom.
         * 
         * @param <ItemType>
         * @param member A string identifying the member of <code>axiom</code> to request.
         * @param memberType The type of entity returned by the method identified by <code>member</code>.
         * @param axiom The axiom to ask for its member.
         * @return If the member of <code>axiom</code> is of type <code>memberType</code>, a singleton set with the entity as entry.<br/>
         *         Otherwise the set which is returned by the member method associtated with <code>member</code>.<br/>
         */
        private Collection<ItemType> getItems(String member, Class<ItemType> memberType, OWLAxiom axiom, Filter<ItemType> filter, Filter<ItemType> perCallFilter) {
            Object result = axiom.accept(getItemCollectorVisitor(member));
            if (result == null) {
                // this should not happen...
                result = axiom.accept(getItemCollectorVisitor(member)); // for debugging purposes
                throw new UnsupportedOperationException();
            }
            if (memberType.isInstance(result)) {
                @SuppressWarnings("unchecked")
                ItemType cast = (ItemType) result;
                if (filter != null && !filter.matches(cast) || perCallFilter != null && !perCallFilter.matches(cast)) {
                    return Cast.cast(Collections.EMPTY_SET);
                } else {
                    return Collections.singleton(cast);
                }
            } else {
                // TODO (tkr 20080411): check the runtime type of the set
                Collection<ItemType> asSet = Cast.cast(result);
                if (filter == null && perCallFilter == null) {
                    return asSet;
                } else {
                    Collection<ItemType> filteredResult = new ArrayList<ItemType>(asSet.size());
                    for (ItemType item: asSet) {
                        if ((filter == null || filter.matches(item)) && (perCallFilter == null || perCallFilter.matches(item))) {
                            filteredResult.add(item);
                        }
                    }
                    return filteredResult;
                }
            }
        }
    }

    private static <ItemType,A extends OWLAxiom> Set<ItemType> getItemsFromItemHits(Set<ItemHits<ItemType,A>> source) {
        Set<ItemType> result = new HashSet<ItemType>();
        for (ItemHits<ItemType,A> occurrence: source) {
            result.add(occurrence.getItem());
        }
        return result;
    }

    /*
     * The item collectors widely used within this class.<br/> <br/> Similarly as for <code>AxiomRequest</code>s, the naming convention is as followes:<br/> Let
     * <code>AxiomType</code> be the type of axioms to collect items from.<br/> Let <i>member</i> be the member for the axioms to get items from.<br/> Let
     * <i>FilterDescription</i> be an additional description for the filter which is beeing applied on the resulting set.<br/> Then the name for the item
     * collector is AxiomType_member_FilterDescription_Collector.
     */
    private final ItemCollector<OWLClassExpression,OWLClassAssertionAxiom> ClassMember_description_NamedDescriptionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLClassAssertionAxiom>("description", OWLClassExpression.class, NAMED_DESCRIPTION_FILTER);
    private final ItemCollector<OWLClassExpression,OWLClassAssertionAxiom> ClassMember_description_ComplexDescriptionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLClassAssertionAxiom>("description", OWLClassExpression.class, COMPLEX_DESCRIPTION_FILTER);
    private final ItemCollector<OWLIndividual,OWLClassAssertionAxiom> ClassMember_individual_Collector = new ItemCollectorCore<OWLIndividual,OWLClassAssertionAxiom>("individual", OWLIndividual.class);
    private final ItemCollector<OWLAnnotationValue,OWLAnnotationAssertionAxiom> EntityAnnotation_annotationValue_Collector = new ItemCollectorCore<OWLAnnotationValue,OWLAnnotationAssertionAxiom>("annotationValue", OWLAnnotationValue.class);
    private final ItemCollector<OWLClassExpression,OWLEquivalentClassesAxiom> EquivalentClasses_descriptions_ComplexDescriptionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLEquivalentClassesAxiom>("descriptions", OWLClassExpression.class, COMPLEX_DESCRIPTION_FILTER);
    private final ItemCollector<OWLClassExpression,OWLEquivalentClassesAxiom> EquivalentClasses_descriptions_NamedDescriptionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLEquivalentClassesAxiom>("descriptions", OWLClassExpression.class, NAMED_DESCRIPTION_FILTER);
    private final ItemCollector<OWLClassExpression,OWLEquivalentClassesAxiom> EquivalentClasses_descriptions_RestrictionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLEquivalentClassesAxiom>("descriptions", OWLClassExpression.class, RESTRICTION_FILTER);
    private final ItemCollector<OWLClassExpression,OWLEquivalentClassesAxiom> EquivalentClasses_descriptions_No_Restrictions_Collector = new ItemCollectorCore<OWLClassExpression,OWLEquivalentClassesAxiom>("descriptions", OWLClassExpression.class, COMPLEX_DESCRIPTION_AND_NO_RESTRICTION_FILTER);
    private final ItemCollector<OWLDataRange,OWLDatatypeDefinitionAxiom> SubDatatype_Collector = new ItemCollectorCore<OWLDataRange,OWLDatatypeDefinitionAxiom>("dataRange", OWLDataRange.class);
    private final ItemCollector<OWLDataRange,OWLDatatypeDefinitionAxiom> SuperDatatype_Collector = new ItemCollectorCore<OWLDataRange,OWLDatatypeDefinitionAxiom>("datatype", OWLDataRange.class);//NICO TODO OWLDatatype.class/OWLRange.class/OWLDatatypeDefinition.class
    private final ItemCollector<OWLObjectPropertyExpression,OWLEquivalentObjectPropertiesAxiom> EquivalentObjectProperties_namedObjectProperties_Collector = new ItemCollectorCore<OWLObjectPropertyExpression,OWLEquivalentObjectPropertiesAxiom>("objectProperties", OWLObjectPropertyExpression.class, OBJECT_PROPERTY_FILTER);
    private final ItemCollector<OWLDataPropertyExpression,OWLEquivalentDataPropertiesAxiom> EquivalentDataProperties_namedDataProperties_Collector = new ItemCollectorCore<OWLDataPropertyExpression,OWLEquivalentDataPropertiesAxiom>("dataProperties", OWLDataPropertyExpression.class, DATA_PROPERTY_FILTER);
    private final ItemCollector<OWLObjectPropertyExpression,OWLDisjointObjectPropertiesAxiom> DisjointObjectProperties_namedObjectProperties_Collector = new ItemCollectorCore<OWLObjectPropertyExpression,OWLDisjointObjectPropertiesAxiom>("objectProperties", OWLObjectPropertyExpression.class, OBJECT_PROPERTY_FILTER);
    private final ItemCollector<OWLDataPropertyExpression,OWLDisjointDataPropertiesAxiom> DisjointDataProperties_namedDataProperties_Collector = new ItemCollectorCore<OWLDataPropertyExpression,OWLDisjointDataPropertiesAxiom>("dataProperties", OWLDataPropertyExpression.class, DATA_PROPERTY_FILTER);
    private final ItemCollector<OWLClassExpression,OWLDataPropertyDomainAxiom> DataPropertyDomain_domain_Collector = new ItemCollectorCore<OWLClassExpression,OWLDataPropertyDomainAxiom>("domain", OWLClassExpression.class);
    private final ItemCollector<OWLDataRange,OWLDataPropertyRangeAxiom> DataPropertyRange_range_Collector = new ItemCollectorCore<OWLDataRange,OWLDataPropertyRangeAxiom>("range", OWLDataRange.class);
//    private final ItemCollector<OWLClassExpression,OWLDataPropertyRangeAxiom> DataPropertyRange_range_Collector = new ItemCollectorCore<OWLClassExpression,OWLDataPropertyRangeAxiom>("range", OWLClassExpression.class);
    private final ItemCollector<OWLIndividual,OWLDifferentIndividualsAxiom> DifferentIndividuals_individuals_Collector = new ItemCollectorCore<OWLIndividual,OWLDifferentIndividualsAxiom>("individuals", OWLIndividual.class);
    private final ItemCollector<OWLClassExpression,OWLDisjointClassesAxiom> DisjointClasses_descriptions_Collector = new ItemCollectorCore<OWLClassExpression,OWLDisjointClassesAxiom>("descriptions", OWLClassExpression.class);
    private final ItemCollector<OWLObjectPropertyExpression,OWLInverseObjectPropertiesAxiom> InverseObjectProperties_namedFirst_Collector = new ItemCollectorCore<OWLObjectPropertyExpression,OWLInverseObjectPropertiesAxiom>("first", OWLObjectPropertyExpression.class, OBJECT_PROPERTY_FILTER);
    private final ItemCollector<OWLObjectPropertyExpression,OWLInverseObjectPropertiesAxiom> InverseObjectProperties_namedSecond_Collector = new ItemCollectorCore<OWLObjectPropertyExpression,OWLInverseObjectPropertiesAxiom>("second", OWLObjectPropertyExpression.class, OBJECT_PROPERTY_FILTER);
    private final ItemCollector<OWLClassExpression,OWLObjectPropertyDomainAxiom> ObjectPropertyDomain_domain_Collector = new ItemCollectorCore<OWLClassExpression,OWLObjectPropertyDomainAxiom>("domain", OWLClassExpression.class);
    private final ItemCollector<OWLClassExpression,OWLObjectPropertyRangeAxiom> ObjectPropertyRange_range_Collector = new ItemCollectorCore<OWLClassExpression,OWLObjectPropertyRangeAxiom>("range", OWLClassExpression.class);
    private final ItemCollector<IRI,OWLAnnotationPropertyDomainAxiom> AnnotationPropertyDomain_domain_Collector = new ItemCollectorCore<IRI,OWLAnnotationPropertyDomainAxiom>("domain", IRI.class);
    private final ItemCollector<IRI,OWLAnnotationPropertyRangeAxiom> AnnotationPropertyRange_range_Collector = new ItemCollectorCore<IRI,OWLAnnotationPropertyRangeAxiom>("range", IRI.class);

    private final ItemCollector<OWLIndividual,OWLSameIndividualAxiom> SameIndividual_individuals_Collector = new ItemCollectorCore<OWLIndividual,OWLSameIndividualAxiom>("individuals", OWLIndividual.class);
    private final ItemCollector<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom> SubObjectPropertyOf_subObjectProperties_NamedObjectPropertiesOnly_Collector = new ItemCollectorCore<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom>("subObjectProperties", OWLObjectPropertyExpression.class, OBJECT_PROPERTY_FILTER);
    private final ItemCollector<List,OWLSubPropertyChainOfAxiom> OWLSubPropertyChainOfAxiom_subObjectProperties_Collector = new ItemCollectorCore<List,OWLSubPropertyChainOfAxiom>("subObjectProperties", List.class);
    private final ItemCollector<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom> SubObjectPropertyOf_superObjectProperty_NamedObjectPropertiesOnly_Collector = new ItemCollectorCore<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom>("superObjectProperty", OWLObjectPropertyExpression.class, OBJECT_PROPERTY_FILTER);
    private final ItemCollector<OWLDataPropertyExpression,OWLSubDataPropertyOfAxiom> SubDataPropertyOf_subDataProperty_NamedDataPropertiesOnly_Collector = new ItemCollectorCore<OWLDataPropertyExpression,OWLSubDataPropertyOfAxiom>("subDataProperty", OWLDataPropertyExpression.class, DATA_PROPERTY_FILTER);
    private final ItemCollector<OWLDataPropertyExpression,OWLSubDataPropertyOfAxiom> SubDataPropertyOf_superDataProperty_NamedDataPropertiesOnly_Collector = new ItemCollectorCore<OWLDataPropertyExpression,OWLSubDataPropertyOfAxiom>("superDataProperty", OWLDataPropertyExpression.class, DATA_PROPERTY_FILTER);
    private final ItemCollector<OWLAnnotationProperty,OWLSubAnnotationPropertyOfAxiom> SubAnnotationPropertyOf_subAnnotationProperty_Collector = new ItemCollectorCore<OWLAnnotationProperty,OWLSubAnnotationPropertyOfAxiom>("subAnnotationProperty", OWLAnnotationProperty.class);
    private final ItemCollector<OWLAnnotationProperty,OWLSubAnnotationPropertyOfAxiom> SubAnnotationPropertyOf_superAnnotationProperty_Collector = new ItemCollectorCore<OWLAnnotationProperty,OWLSubAnnotationPropertyOfAxiom>("superAnnotationProperty", OWLAnnotationProperty.class);
    private final ItemCollector<OWLClassExpression,OWLSubClassOfAxiom> SubClassOf_subDescription_Collector = new ItemCollectorCore<OWLClassExpression,OWLSubClassOfAxiom>("subDescription", OWLClassExpression.class);
    private final ItemCollector<OWLClassExpression,OWLSubClassOfAxiom> SubClassOf_superDescription_Collector = new ItemCollectorCore<OWLClassExpression,OWLSubClassOfAxiom>("superDescription", OWLClassExpression.class);
    private final ItemCollector<OWLClassExpression,OWLSubClassOfAxiom> SubClassOf_superDescription_RestrictionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLSubClassOfAxiom>("superDescription", OWLClassExpression.class, RESTRICTION_FILTER);
    private final ItemCollector<OWLClassExpression,OWLSubClassOfAxiom> SubClassOf_superDescription_No_Restrictions_Collector = new ItemCollectorCore<OWLClassExpression,OWLSubClassOfAxiom>("superDescription", OWLClassExpression.class, NO_RESTRICTION_FILTER);
    private final ItemCollector<OWLClassExpression,OWLSubClassOfAxiom> SubClassOf_superDescription_UnnamedNonRestrictionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLSubClassOfAxiom>("superDescription", OWLClassExpression.class, UNNAMED_NON_RESTRICTION_FILTER);
    private final ItemCollector<OWLClassExpression,OWLSubClassOfAxiom> SubClassOf_superDescription_NamedDescriptionsOnly_Collector = new ItemCollectorCore<OWLClassExpression,OWLSubClassOfAxiom>("superDescription", OWLClassExpression.class, NAMED_DESCRIPTION_FILTER);

    private final ItemCollector<OWLDataProperty,OWLDataPropertyDomainAxiom> DataPropertyDomain_property_Collector = new ItemCollectorCore<OWLDataProperty,OWLDataPropertyDomainAxiom>("dataProperty", OWLDataProperty.class);
    private final ItemCollector<OWLObjectProperty,OWLObjectPropertyDomainAxiom> ObjectPropertyDomain_property_Collector = new ItemCollectorCore<OWLObjectProperty,OWLObjectPropertyDomainAxiom>("objectProperty", OWLObjectProperty.class);
    private final ItemCollector<OWLAnnotationProperty,OWLAnnotationPropertyDomainAxiom> AnnotationPropertyDomain_property_Collector = new ItemCollectorCore<OWLAnnotationProperty,OWLAnnotationPropertyDomainAxiom>("annotationProperty", OWLAnnotationProperty.class);

    private final ItemCollector<OWLDataProperty,OWLDataPropertyRangeAxiom> DataPropertyRange_property_Collector = new ItemCollectorCore<OWLDataProperty,OWLDataPropertyRangeAxiom>("dataProperty", OWLDataProperty.class); 
    private final ItemCollector<OWLObjectProperty,OWLObjectPropertyRangeAxiom> ObjectPropertyRange_property_Collector = new ItemCollectorCore<OWLObjectProperty,OWLObjectPropertyRangeAxiom>("objectProperty", OWLObjectProperty.class);
    private final ItemCollector<OWLAnnotationProperty,OWLAnnotationPropertyRangeAxiom> AnnotationPropertyRange_property_Collector = new ItemCollectorCore<OWLAnnotationProperty,OWLAnnotationPropertyRangeAxiom>("annotationProperty", OWLAnnotationProperty.class);
    
    
    /** A map of <code>ItemCollectorVisitor</code> indexed by the member name for which they collect items. */
    private final Map<String,GetMemberVisitor> _itemCollectorVisitors = new HashMap<String,GetMemberVisitor>();

    private GetMemberVisitor getItemCollectorVisitor(String member) {
        synchronized (_itemCollectorVisitors) {
            if (!_itemCollectorVisitors.containsKey(member)) {
                _itemCollectorVisitors.put(member, new GetMemberVisitor(member));
            }
            return _itemCollectorVisitors.get(member);
        }
    }

    /**
     * Auto box the parameters into an Object array.
     * 
     * @param parameters The parameters to box.
     * @return <code>parameters</code> as an Object array.
     */
    private Object[] autoBox(Object... parameters) {
        return parameters;
    }

    private void markDirty(boolean dirty) {
        if (dirty) {
            try {
                getOntologyProject().setOntologyDirty(getOntologyURI(), dirty);
            } catch (NeOnCoreException e) {
                new NeonToolkitExceptionHandler().handleException(e);
            }
        }
    }

    private Set<OWLModel> getRelevantOntologies(boolean includeImportedOntologies) {
        try {
            if (!includeImportedOntologies) {
                return Collections.singleton((OWLModel)this);
            } else {
                Set<OWLModel> result = new LinkedHashSet<OWLModel>(getAllImportedOntologies());
                result.add(this);
                return result;
            }
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void ontologyUpdated(final OWLOntology updatedOntology, final List<? extends OWLAxiomChange> changes, final Set<OWLEntity> potentiallyAddedEntities, final Set<OWLEntity> removedEntities) {
        markDirty(getOntology() == updatedOntology);

        for (EntityHierarchyUpdater<?> updater: _hierarchyUpdaters.values()) {
            if (updater != null) {
                updater.processEvent(updatedOntology, changes, potentiallyAddedEntities, removedEntities);
            }
        }
        
        if (_subClassOfAxioms != null) {
            if (getOntology() == updatedOntology) {
            for (OWLAxiomChange event: changes) {
                if (event.getAxiom() instanceof OWLSubClassOfAxiom) {
                    OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom)event.getAxiom();
                    if (event instanceof AddAxiom) {
                        if (!_subClassOfAxioms.add(axiom)) {
                            _log.error("Adding sub class relation already contained in the cache, ignoring: " + OWLUtilities.toString(axiom));
                        }
                    } else {
                        if (!_subClassOfAxioms.remove(axiom)) {
                            _log.error("Removing sub class relation not contained in the cache, ignoring: " + OWLUtilities.toString(axiom));
                        }
                    }
                }
            }
            }
        }
        if (_entities != null) {
            if (getOntology() == updatedOntology) {
            OWLObjectVisitorEx visitor = new GetInterfaceTypeVisitor();
            for (OWLEntity entity: potentiallyAddedEntities) {
                Class<? extends OWLObject> type = Cast.cast(entity.accept(visitor));
                if (_entities.containsKey(type)) {
                    if (!((Set<OWLEntity>)_entities.get(type)).add(entity)) {
                        // can happen regularly since entity is only a potential added and may be known before
                    }
                }
            }
            for (OWLEntity entity: removedEntities) {
                Class<? extends OWLObject> type = Cast.cast(entity.accept(visitor));
                if (_entities.containsKey(type)) {
                    if (!_entities.get(type).remove(entity)) {
                        _log.error("Removing entity not contained in the cache, ignoring: " + OWLUtilities.toString(entity));
                    }
                }
            }
            }
        }
        
        // notify registered listeners
        Object[] aListener = _axiomListeners.getListeners();
        // bugfix: instead of calling modelChanged() for every single OntologyChangeEvent, we
        // only call it once, with all relevant OntologyChangeEvents for that listener. This
        // lead to problems during import.
        for (int i = 0; i < aListener.length; i++) {
            try {
                List<OWLAxiomChange> relevantChanges = new ArrayList<OWLAxiomChange>();
                for (OWLAxiomChange changeEvent: changes) {
                    OWLAxiom axiom = (OWLAxiom)changeEvent.getAxiom();
                    Set<Class<? extends OWLAxiom>> clazzes = _listenerClassMapping.get(aListener[i]);
                    for (Class<? extends OWLAxiom> element: clazzes) {
                        if (element.isAssignableFrom(axiom.getClass())) {
                            relevantChanges.add(changeEvent);
                        }

                    }
                }
                OWLChangeEvent event = new OWLChangeEvent(getOntology(), updatedOntology, relevantChanges, potentiallyAddedEntities, removedEntities, getProjectId());
                
                // WEHI 2009-01-22: listeners must always be notified, concept of relevantChanges is not working, 
                // since in OWL all changes could be relevant. E.g.: an ObjectProperty is shown in ObjectProperty folder, 
                // but no declaration exists for this property, only a subclass axiom containing it. now renaming this
                // property will not be reflected in UI, since the listener in ObjectPropertyHierarchyProvider is only
                // interested in:
                //  - SubObjectPropertyOf 
                //  - EquivalentObjectProperties 
                //  - InverseObjectProperties 
                //  - Declaration
                
//                if (relevantChanges.size() > 0) { // see comment above!
                    ((OWLAxiomListener) aListener[i]).modelChanged(event);
//                }
            } catch (Exception e) {
                _log.error(Messages.getString("OWLModelCore.0"), e);
            }
        }
    }
    
    /**
     * Creates a new OwlModel instance for the given ontology and project IDs and caches the object for later use.
     * 
     * @param ontologyId
     * @param projectId the project ID representing a KAON2Connection
     * @return
     */
    OWLModelCore(OWLOntology ontology, OWLOntologyManager manager, IOntologyProject ontologyProject) {
        _ontologyProject = ontologyProject;
        _manager = manager;
        if (_manager == null) {
            throw new IllegalArgumentException(Messages.getString("OWLModelCore.NullOwlManagerError"));
        }
        _ontology = ontology;
        if (_ontology == null) {
            throw new IllegalArgumentException(Messages.getString("OWLModelCore.NullOwlModelError"));
        }
        _hierarchyUpdaters = new LinkedHashMap<Class<?>,EntityHierarchyUpdater<?>>();
    }

    @Override
    public boolean isClosed() {
        return _closed;
    }

    @SuppressWarnings("unused")
    private void close() {
        if (!_closed) {
            _closed = true;
            cleanCaches();
        }
    }

    @Override
    public void addAxiomListener(OWLAxiomListener listener, Class<? extends OWLAxiom>[] clazzes) {
        _axiomListeners.add(listener);
        Set<Class<? extends OWLAxiom>> listenerSet = _listenerClassMapping.get(listener);
        if (listenerSet == null) {
            listenerSet = new LinkedHashSet<Class<? extends OWLAxiom>>();
        }
        for (int i = 0; i < clazzes.length; i++) {
            listenerSet.add(clazzes[i]);
        }
        _listenerClassMapping.put(listener, listenerSet);
    }

    @Override
    public void removeAxiomListener(OWLAxiomListener listener) {
        _axiomListeners.remove(listener);
        _listenerClassMapping.remove(listener);
    }

    @Override
    public OWLOntology getOntology() {
        return _ontology;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.owl.datamodel.OWLModel#getProjectId()
     */
    @Override
    public String getProjectId() throws NeOnCoreException {
        return getOntologyProject().getName();
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // OWL ENTITIES
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _ENTITY_METHODS______________________() {
    }

    @Override
    public Set<OWLAnnotationAssertionAxiom> getAnnotations(String owlEntityId) throws NeOnCoreException {
        String expandedURI = getNamespaces().expandString(owlEntityId);
        return EntityAnnotation_entity_Request.getAxioms(OWLUtilities.toIRI(expandedURI));
    }

    @Override
    public Set<LocatedItem<OWLAnnotationAssertionAxiom>> getAnnotationHits(String owlEntityId) throws NeOnCoreException {
        
        String expandedURI = getNamespaces().expandString(owlEntityId);
        return EntityAnnotation_entity_Request.getLocatedAxioms(OWLUtilities.toIRI(expandedURI));
    }

    @Override
    public Set<OWLAnnotationValue> getAnnotations(String owlEntityId, String annotationPropertyId) throws NeOnCoreException {
        String expandedURI = getNamespaces().expandString(owlEntityId);
        return EntityAnnotation_annotationValue_Collector.getItems(Annotation_annotationProperty_Request, autoBox(annotationProperty(annotationPropertyId), IRI.create(expandedURI)));
    }
    
    // ///////////////////////////////////////////////////////////////////////
    // 
    // OWL AnonymousIndividual
    //
    // ///////////////////////////////////////////////////////////////////////
    
    @Override
    public Set<OWLAnnotationValue> getAnnotations(OWLAnonymousIndividual anonymousIndividual, String annotationPropertyId) throws NeOnCoreException {
        return EntityAnnotation_annotationValue_Collector.getItems(Annotation_annotationProperty_Request, autoBox(annotationProperty(annotationPropertyId), anonymousIndividual));
    }

    @Override
    public Set<LocatedItem<OWLAnnotationAssertionAxiom>> getAnnotationHits(OWLAnnotationSubject annotationSubject) throws NeOnCoreException {
        return EntityAnnotation_entity_Request.getLocatedAxioms(annotationSubject);
    }
    
    @Override
    public Set<LocatedItem<OWLAnnotationAssertionAxiom>> getAnnotationHitsForAnnotationProperty(OWLAnnotationProperty annotationProperty) throws NeOnCoreException {
        return EntityAnnotation_property_Request.getLocatedAxioms(annotationProperty);
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // OWL ONTOLOGY
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _ONTOLOGY_METHODS______________________() {
    }

    @Override
    public Set<OWLModel> getImportedOntologies() throws NeOnCoreException {
        return getOWLModels(getOntologyProject().getImportedOntologyURIs(getOntologyURI()));
    }

    @Override
    public Set<String> getImportedOntologiesURIs() throws NeOnCoreException {
        return getOntologyProject().getImportedOntologyURIs(getOntologyURI());
    }

    @Override
    public Set<OWLModel> getAllImportedOntologies() throws NeOnCoreException {
        return getOWLModels(getOntologyProject().getAllImportedOntologyURIs(getOntologyURI()));
    }

    @Override
    public Set<String> getAllImportedOntologiesURIs() throws NeOnCoreException {
        return getOntologyProject().getAllImportedOntologyURIs(getOntologyURI());
    }

    @Override
    public Set<OWLModel> getAllImportingOntologies() throws NeOnCoreException {
        return getOWLModels(getOntologyProject().getAllImportingOntologyURIs(getOntologyURI()));
    }
    
    private Set<OWLModel> getOWLModels(Set<String> ontologyURIs) throws NeOnCoreException {
        Set<OWLModel> result = new LinkedHashSet<OWLModel>();
        for (String ontology: ontologyURIs) {
            try {
                OWLModel onto = OWLModelFactory.getOWLModel(ontology, getProjectId());
                if(onto != null) {
                    result.add(onto);
                }
            } catch (RuntimeException e) {
                if(e.getMessage().equals(Messages.getString("OWLModelCore.NullOwlModelError"))) {
                    //ignore
                } else {
                    throw e;
                }
            }
        }
        return result;
    }

    @Override
    public String getDefaultNamespace() throws NeOnCoreException {
        return getNamespaces().getDefaultNamespace();
    }

    @Override
    public OWLNamespaces getNamespaces() throws NeOnCoreException {
        assertNamespaces();
        return _namespaces;
    }

    @Override
    public void setDefaultNamespace(String namespace) throws NeOnCoreException {
        getOntologyProject().setDefaultNamespace(getOntologyURI(), namespace);
    }

    @Override
    public void setNamespacePrefix(String prefix, String namespace) throws NeOnCoreException {
        getOntologyProject().setNamespacePrefix(getOntologyURI(), prefix, namespace);
    }

    @Override
    public Set<OWLAnnotation> getOntologyAnnotations() throws NeOnCoreException {
        return _ontology.getAnnotations();
    }

    @Override
    public Set<Object> getOntologyAnnotations(String annotationPropertyId) throws NeOnCoreException {
        Set<OWLAnnotation> annotations = getOntologyAnnotations();
        Set<Object> y = new HashSet<Object>();
        OWLAnnotationProperty annotationProperty = annotationProperty(annotationPropertyId);

        for (OWLAnnotation annotation: annotations) {
            if (annotation.getProperty().equals(annotationProperty)) {
                y.add(annotation.getValue());
            }
        }
        return y;
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // OWL CLASSES
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _CLASS_METHODS______________________() {
    }

    @Override
    public Set<OWLClass> getRootClasses() throws NeOnCoreException {
        EntityHierarchy<OWLClassExpression> hierarchy = getHierarchy(OWLClassExpression.class, getIncludeImportedOntologies());
        Set<OWLClassExpression> roots = hierarchy.getRootEquivalenceClassRepresentatives();
        roots = new LinkedHashSet<OWLClassExpression>(roots);
        assert(getOWLClasses(roots).size() == roots.size());
        return Cast.cast(roots);
    }
    
    private static Set<OWLClass> getOWLClasses(Set<OWLClassExpression> descriptions) {
        Set<OWLClass> result = new LinkedHashSet<OWLClass>();
        for (OWLClassExpression d: descriptions) {
            if (d instanceof OWLClass) {
                result.add((OWLClass)d);
            }
        }
        return result;
    }

    @Override
    public boolean isRootClass(OWLClass clazz) throws NeOnCoreException {
        return getRootClasses().contains(clazz);
    }
    
    private <M extends OWLObject,A extends OWLAxiom> Map<M,Set<A>> createByMemberMap(Class<M> dummy, Set<A> axioms, OWLObjectVisitorEx<?> getMemberVisitor) {
        Map<M,Set<A>> result = new LinkedHashMap<M,Set<A>>();
        for (A axiom: axioms) {
            M member = Cast.cast(axiom.accept(getMemberVisitor));
            if (!result.containsKey(member)) {
                result.put(member, new LinkedHashSet<A>(Collections.singleton(axiom)));
            } else {
                result.get(member).add(axiom);
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    private boolean isComplexRootDescriptionNoSubClassOfOccurrenceCheck(OWLClassExpression description, Map<OWLClassExpression,Set<OWLSubClassOfAxiom>> consideredAxiomsBySubDescription, Map<OWLClassExpression,Set<OWLSubClassOfAxiom>> consideredAxiomsBySuperDescription) throws NeOnCoreException {
        // named classes are not complex...
        if (description instanceof OWLClass) {
            return false;
        }

        // description is no complex root description if it has a super description which is not equal to itself or owl:Thing
        if (consideredAxiomsBySubDescription.containsKey(description)) {
            for (OWLSubClassOfAxiom axiom: consideredAxiomsBySubDescription.get(description)) {
                OWLClassExpression superDescription = axiom.getSuperClass();
                if (!superDescription.equals(description) && !superDescription.equals(OWLConstants.OWL_THING_URI)) {
                    return false;
                }
            }
        }
        // description is no complex root description if it has named sub descriptions which are not equal to owl:Thing
        if (consideredAxiomsBySuperDescription.containsKey(description)) {
            for (OWLSubClassOfAxiom axiom: consideredAxiomsBySuperDescription.get(description)) {
                OWLClassExpression subDescription = axiom.getSubClass();
                if (!subDescription.equals(description) && !subDescription.equals(OWLConstants.OWL_THING_URI) && (subDescription instanceof OWLClass)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isRootDataProperty(OWLDataProperty property) throws NeOnCoreException {
        return getRootDataProperties().contains(property);
    }

    @Override
    public boolean isRootObjectProperty(OWLObjectProperty property) throws NeOnCoreException {
        return getRootObjectProperties().contains(property);
    }

    @Override
    public Set<OWLDatatype> getAllDatatypes() throws NeOnCoreException {
        return getEntities(OWLDatatype.class);
    }
    @Override
    public Set<OWLDatatype> getAllDatatypes(boolean includeImported) throws NeOnCoreException {
        return getEntities(OWLDatatype.class, includeImported);
    }
    @Override
    public Set<OWLClass> getAllClasses() throws NeOnCoreException {
        return getEntities(OWLClass.class);
    }

    @Override
    public Set<OWLClass> getAllClasses(boolean includeImported) throws NeOnCoreException {
        return getEntities(OWLClass.class, includeImported);
    }
    @Override
    public Set<OWLIndividual> getAllUnassertedIndividuals() throws NeOnCoreException{
        Set<OWLIndividual> allIndividuals = getAllIndividuals();
        Set<OWLIndividual> allUnassertedIndividuals = new HashSet<OWLIndividual>();
        for(OWLIndividual individual : allIndividuals){
            if(getClasses(individual.toStringID()).isEmpty()){
                allUnassertedIndividuals.add(individual);
            }
        }
        return allUnassertedIndividuals;
    }
    @Override
    public Set<OWLIndividual> getAllIndividuals() throws NeOnCoreException {
        return Cast.cast(getEntities(OWLNamedIndividual.class));
    }

    @Override
    public Set<OWLIndividual> getAllIndividuals(boolean includeImported) throws NeOnCoreException {
        return Cast.cast(getEntities(OWLNamedIndividual.class, includeImported));
    }

    @Override
    public Set<OWLClass> getSubClasses(String classId) throws NeOnCoreException {
        return getOWLClasses(getSubDescriptions(owlClass(classId)));
    }

    private Set<OWLClassExpression> getSubDescriptions(OWLClassExpression description) throws NeOnCoreException {
        EntityHierarchy.Node<OWLClassExpression> node = getHierarchy(OWLClassExpression.class, getIncludeImportedOntologies()).getNode(description);
        if (node == null) {
            return Collections.emptySet();
        }
        Set<OWLClassExpression> result = node.getChildren();
        result = new LinkedHashSet<OWLClassExpression>(result);
        result.remove(description);
        return result;
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSubDescriptionHits(String classId) throws NeOnCoreException {
        return Cast.cast(SubClassOf_subDescription_Collector.getItemHits(SubClassOf_superDescription_Request, autoBox(owlClass(classId))));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSuperDescriptionHits(String subClassId) throws NeOnCoreException {
        return Cast.cast(SubClassOf_superDescription_Collector.getItemHits(SubClassOf_subDescription_Request, autoBox(owlClass(subClassId))));
    }

    @Override
    public Set<OWLClass> getSuperClassesInClassHierarchy(String subClassId) throws NeOnCoreException {
        return getOWLClasses(getSuperDescriptionsInClassHierarchy(owlClass(subClassId)));
    }

    @Override
    public Set<OWLClassExpression> getSuperDescriptionsInClassHierarchy(OWLClassExpression subDescription) throws NeOnCoreException {
        EntityHierarchy.Node<OWLClassExpression> node = getHierarchy(OWLClassExpression.class, getIncludeImportedOntologies()).getNode(subDescription);
        if (node == null) {
            return Collections.emptySet();
        }
        Set<OWLClassExpression> result = node.getParents();
        result = new LinkedHashSet<OWLClassExpression>(result);
        result.remove(subDescription);
        return result;
    }
    
    private void cleanNamespaces() {
        _namespaces = null;
    }
    
    private void cleanHierarchies(boolean includeNonClosureHierarchy) {
        if (_hierarchyUpdaters != null) {
            for (EntityHierarchyUpdater<?> updater: _hierarchyUpdaters.values()) {
                updater.close();
            }
            _hierarchyUpdaters.clear();
        }
    }

    @Override
    public Set<OWLClass> getAllSubClasses(String classId) throws NeOnCoreException {
        Set<OWLClass> result = new HashSet<OWLClass>();
        // note: classId is by default excluded, however, if classId is a subclass of itself, it will be added later
        getAllSubClasses(owlClass(classId), result, false);
        return result;
    }

    private void getAllSubClasses(OWLClass source, Set<OWLClass> result, boolean addSourceToResult) throws NeOnCoreException {
        if (addSourceToResult) {
            result.add(source);
        }
        Set<OWLClass> subClasses = getSubClasses(source.getIRI().toString());
        for (OWLClass subClass: subClasses) {
            if (!result.contains(subClass)) {
                getAllSubClasses(subClass, result, true);
            }
        }
    }

    @Override
    public Set<OWLClass> getSuperClasses(String classId) throws NeOnCoreException {
        return Cast.cast(SubClassOf_superDescription_NamedDescriptionsOnly_Collector.getItems(SubClassOf_subDescription_Request, autoBox(owlClass(classId))));
    }

    @Override
    public Set<ItemHits<OWLClass,OWLSubClassOfAxiom>> getSuperClassHits(String classId) throws NeOnCoreException {
        return Cast.cast(SubClassOf_superDescription_NamedDescriptionsOnly_Collector.getItemHits(SubClassOf_subDescription_Request, autoBox(owlClass(classId))));
    }

    @Override
    public Set<OWLClass> getEquivalentClasses(String classId) throws NeOnCoreException {
        return Cast.cast(EquivalentClasses_descriptions_NamedDescriptionsOnly_Collector.getItems(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId)));
    }

    @Override
    public Set<ItemHits<OWLClass,OWLEquivalentClassesAxiom>> getEquivalentClassesHits(String classId) throws NeOnCoreException {
        return Cast.cast(EquivalentClasses_descriptions_NamedDescriptionsOnly_Collector.getItemHits(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId)));
    }

    @Override
    public Set<OWLClassExpression> getSuperDescriptions(String classId) throws NeOnCoreException {
        return SubClassOf_superDescription_UnnamedNonRestrictionsOnly_Collector.getItems(SubClassOf_subDescription_Request, autoBox(owlClass(classId)));
    }

    @Override
    public Set<OWLClassExpression> getEquivalentDescriptions(String classId) throws NeOnCoreException {
        return EquivalentClasses_descriptions_ComplexDescriptionsOnly_Collector.getItems(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> getEquivalentDescriptionHits(String classId) throws NeOnCoreException {
        return EquivalentClasses_descriptions_ComplexDescriptionsOnly_Collector.getItemHits(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }

    @Override
    public Set<OWLClassExpression> getSuperRestrictions(String classId) throws NeOnCoreException {
        return SubClassOf_superDescription_RestrictionsOnly_Collector.getItems(SubClassOf_subDescription_Request, autoBox(owlClass(classId)));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSuperRestrictionHits(String classId) throws NeOnCoreException {
        return SubClassOf_superDescription_RestrictionsOnly_Collector.getItemHits(SubClassOf_subDescription_Request, autoBox(owlClass(classId)));
    }

    @Override
    public Set<OWLClassExpression> getSuperDescriptionsWithoutRestrictions(String classId) throws NeOnCoreException {
        return SubClassOf_superDescription_No_Restrictions_Collector.getItems(SubClassOf_subDescription_Request, autoBox(owlClass(classId)));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSuperDescriptionHitsWithoutRestrictionHits(String classId) throws NeOnCoreException {
        return SubClassOf_superDescription_No_Restrictions_Collector.getItemHits(SubClassOf_subDescription_Request, autoBox(owlClass(classId)));
    }

    @Override
    public Set<OWLClassExpression> getEquivalentRestrictions(String classId) throws NeOnCoreException {
        return EquivalentClasses_descriptions_RestrictionsOnly_Collector.getItems(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }
    @Override
    public Set<OWLClassExpression> getEquivalentDescriptionsWithoutRestrictions(String classId) throws NeOnCoreException {
        return EquivalentClasses_descriptions_No_Restrictions_Collector.getItems(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> getEquivalentRestrictionHits(String classId) throws NeOnCoreException {
        return EquivalentClasses_descriptions_RestrictionsOnly_Collector.getItemHits(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> getEquivalentDescriptionHitsWithoutRestrictionHits(String classId) throws NeOnCoreException {
        return EquivalentClasses_descriptions_No_Restrictions_Collector.getItemHits(EquivalentClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }


    @Override
    public Set<OWLClassExpression> getDisjointDescriptions(String classId) throws NeOnCoreException {
        return DisjointClasses_descriptions_Collector.getItems(DisjointClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLDisjointClassesAxiom>> getDisjointDescriptionHits(String classId) throws NeOnCoreException {
        return DisjointClasses_descriptions_Collector.getItemHits(DisjointClasses_descriptions_Request, autoBox(owlClass(classId)), owlClass(classId));
    }

    @Override
    public Set<OWLIndividual> getIndividuals(String classId) throws NeOnCoreException {
        return ClassMember_individual_Collector.getItems(ClassMember_description_Request, autoBox(owlClass(classId)));
    }

    @Override
    public Set<OWLIndividual> getAllIndividuals(String classId) throws NeOnCoreException {
        Set<OWLIndividual> result = new HashSet<OWLIndividual>();
        Set<OWLClass> closure = getAllSubClasses(classId);
        closure.add(owlClass(classId));
        for (OWLClass clazz: closure) {
            result.addAll(getIndividuals(clazz.getIRI().toString()));
        }
        return result;
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // COMPLEX OWL CLASSES
    //
    // ///////////////////////////////////////////////////////////////////////

    @Override
    public Set<OWLClassExpression> getComplexClasses() throws NeOnCoreException {
        Set<OWLSubClassOfAxiom> axioms = getSubClassOfAxioms();
        Map<OWLClassExpression,Set<OWLSubClassOfAxiom>> axiomsBySubDescription = createByMemberMap(OWLClassExpression.class, axioms, getItemCollectorVisitor("subDescription"));
        Map<OWLClassExpression,Set<OWLSubClassOfAxiom>> axiomsBySuperDescription = createByMemberMap(OWLClassExpression.class, axioms, getItemCollectorVisitor("superDescription"));
        Set<OWLClassExpression> descriptionsInSubClassOfAxioms = new LinkedHashSet<OWLClassExpression>();
        for (OWLSubClassOfAxiom axiom: axioms) {
            descriptionsInSubClassOfAxioms.add(axiom.getSubClass());
            descriptionsInSubClassOfAxioms.add(axiom.getSuperClass());
        }
        Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();
        for (OWLClassExpression description: descriptionsInSubClassOfAxioms) {
            if (isComplexRootDescriptionNoSubClassOfOccurrenceCheck(description, axiomsBySubDescription, axiomsBySuperDescription)) {
                result.add(description);
            }
        }
        return result;
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // PROPERTIES
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _PROPERTY_METHODS______________________() {
    }

    @Override
    public Set<OWLObjectProperty> getRootObjectProperties() throws NeOnCoreException {
        return getRootProperties(OWLObjectPropertyExpression.class, OWLObjectProperty.class);
    }

    @Override
    public Set<OWLObjectProperty> getAllObjectProperties() throws NeOnCoreException {
        return getEntities(OWLObjectProperty.class);
    }
    
    @Override
    public Set<OWLObjectProperty> getAllObjectProperties(boolean includeImported) throws NeOnCoreException {
        return getEntities(OWLObjectProperty.class, includeImported);
    }

    @Override
    public Set<OWLDataProperty> getAllDataProperties() throws NeOnCoreException {
        return getEntities(OWLDataProperty.class);
    }

    @Override
    public Set<OWLDataProperty> getAllDataProperties(boolean includeImported) throws NeOnCoreException {
        return getEntities(OWLDataProperty.class, includeImported);
    }

    @Override
    public Set<OWLAnnotationProperty> getAllAnnotationProperties() throws NeOnCoreException {
        return getEntities(OWLAnnotationProperty.class);
    }
    
    @Override
    public Set<OWLAnnotationProperty> getAllAnnotationProperties(boolean includeImported) throws NeOnCoreException {
        return getEntities(OWLAnnotationProperty.class, includeImported);
    }

    @Override
    public Set<OWLAnnotationProperty> getAllOntologyAnnotationProperties() throws NeOnCoreException {
        return getAllAnnotationProperties();
    }

    private <E,P extends E> Set<P> getRootProperties(Class<E> type, Class<P> primaryType) throws NeOnCoreException {
        EntityHierarchy<E> hierarchy = getHierarchy(type, getIncludeImportedOntologies());
        Set<P> result = new LinkedHashSet<P>();
        for (E e: hierarchy.getRootEquivalenceClassRepresentatives()) {
            if (primaryType.isInstance(e)) {
                P p = Cast.cast(e);
                result.add(p);
            }
        }
        return result;
    }
    
    private <E,P extends E,A extends OWLAxiom> Set<LocatedItem<A>> getLocatedChildEdgeAxioms(Class<E> type, Class<A> axiomType, P entity) throws NeOnCoreException {
        EntityHierarchy<E> hierarchy = getHierarchy(type, getIncludeImportedOntologies());
        EntityHierarchy.Node<E> node = hierarchy.getNode(entity);
        if (node == null) {
            return new LinkedHashSet<LocatedItem<A>>();
        }
        Set<LocatedItem<A>> locatedAxioms = new LinkedHashSet<LocatedItem<A>>();
        for (E e: node.getChildren()) {
            for (OWLAxiom axiom: node.getChildRelationDefiningAxioms(e)) {
                if (axiomType.isInstance(axiom)) {
                    for (OWLOntology ontology: hierarchy.getOntologyIDs(axiom)) {
                        String ontologyURI = getOntologyURI(ontology);
                        A a = Cast.cast(axiom);
                        LocatedItemCore<A> locatedAxiom = new LocatedItemCore<A>(a, ontologyURI);
                        locatedAxioms.add(locatedAxiom);
                    }
                }
            }
        }
        return locatedAxioms;
    }

    @Override
    public Set<OWLDataProperty> getRootDataProperties() throws NeOnCoreException {
        return getRootProperties(OWLDataPropertyExpression.class, OWLDataProperty.class);
    }

    @Override
    public Set<OWLAnnotationProperty> getRootAnnotationProperties() throws NeOnCoreException {
        return getRootProperties(OWLAnnotationProperty.class, OWLAnnotationProperty.class);
    }

    @Override
    public Set<List<OWLObjectProperty>> getSubPropertyChains(String propertyId) throws NeOnCoreException {
        return Cast.cast(OWLSubPropertyChainOfAxiom_subObjectProperties_Collector.getItems(OWLSubPropertyChainOfAxiom_superDescription_Request, autoBox(objectProperty(propertyId))));
    }
    
    @Override
    public Set<ItemHits<List<OWLObjectPropertyExpression>,OWLSubPropertyChainOfAxiom>> getSubPropertyChainOfHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(OWLSubPropertyChainOfAxiom_subObjectProperties_Collector.getItemHits(OWLSubPropertyChainOfAxiom_superDescription_Request, autoBox(objectProperty(propertyId))));
    }

    @Override
    public Set<OWLObjectProperty> getSubObjectProperties(String propertyId) throws NeOnCoreException {
        return getItemsFromItemHits(getSubObjectPropertyHits(propertyId));
    }

    @Override
    public Set<ItemHits<OWLObjectProperty,OWLSubObjectPropertyOfAxiom>> getSubObjectPropertyHits(String propertyId) throws NeOnCoreException {
        EntityHierarchyUpdater<?> updater = _hierarchyUpdaters.get(OWLObjectPropertyExpression.class);
        if(updater != null)
            updater.refresh();
        Set<LocatedItem<OWLSubObjectPropertyOfAxiom>> locatedAxioms = getLocatedChildEdgeAxioms(OWLObjectPropertyExpression.class, OWLSubObjectPropertyOfAxiom.class, OWLModelFactory.getOWLDataFactory(getProjectId()).getOWLObjectProperty(OWLUtilities.toIRI(propertyId)));
        return Cast.cast(SubObjectPropertyOf_subObjectProperties_NamedObjectPropertiesOnly_Collector.getItemHits(getGroupBy(), locatedAxioms));
    }
    
    @Override
    public Set<OWLDataProperty> getSubDataProperties(String propertyId) throws NeOnCoreException {
        return getItemsFromItemHits(getSubDataPropertyHits(propertyId));
    }

    @Override
    public Set<ItemHits<OWLDataProperty,OWLSubDataPropertyOfAxiom>> getSubDataPropertyHits(String propertyId) throws NeOnCoreException {
        EntityHierarchyUpdater<?> updater = _hierarchyUpdaters.get(OWLDataPropertyExpression.class);
        if(updater != null)
            updater.refresh();
        Set<LocatedItem<OWLSubDataPropertyOfAxiom>> locatedAxioms = getLocatedChildEdgeAxioms(OWLDataPropertyExpression.class, OWLSubDataPropertyOfAxiom.class, OWLModelFactory.getOWLDataFactory(getProjectId()).getOWLDataProperty(OWLUtilities.toIRI(propertyId)));
        return Cast.cast(SubDataPropertyOf_subDataProperty_NamedDataPropertiesOnly_Collector.getItemHits(getGroupBy(), locatedAxioms));
    }

    @Override
    public Set<OWLObjectProperty> getSuperObjectProperties(String propertyId) throws NeOnCoreException {
        return Cast.cast(SubObjectPropertyOf_superObjectProperty_NamedObjectPropertiesOnly_Collector.getItems(OWLObjectSubPropertyAxiom_subProperty_Request, autoBox(objectProperty(propertyId))));
    }

    @Override
    public Set<ItemHits<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom>> getSuperObjectPropertyHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(SubObjectPropertyOf_superObjectProperty_NamedObjectPropertiesOnly_Collector.getItemHits(OWLObjectSubPropertyAxiom_subProperty_Request, autoBox(objectProperty(propertyId))));
    }

    @Override
    public Set<OWLDataProperty> getSuperDataProperties(String propertyId) throws NeOnCoreException {
        return Cast.cast(SubDataPropertyOf_superDataProperty_NamedDataPropertiesOnly_Collector.getItems(SubDataPropertyOf_subDataProperty_Request, autoBox(dataProperty(propertyId))));
    }

    @Override
    public Set<ItemHits<OWLDataPropertyExpression,OWLSubDataPropertyOfAxiom>> getSuperDataPropertyHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(SubDataPropertyOf_superDataProperty_NamedDataPropertiesOnly_Collector.getItemHits(SubDataPropertyOf_subDataProperty_Request, autoBox(dataProperty(propertyId))));
    }

    @Override
    public Set<OWLObjectProperty> getEquivalentObjectProperties(String propertyId) throws NeOnCoreException {
        return Cast.cast(EquivalentObjectProperties_namedObjectProperties_Collector.getItems(EquivalentObjectProperties_objectProperties_Request, autoBox(objectProperty(propertyId)), objectProperty(propertyId)));
    }
    @Override
    public Set<OWLObjectProperty> getDisjointObjectProperties(String propertyId) throws NeOnCoreException {
        return Cast.cast(DisjointObjectProperties_namedObjectProperties_Collector.getItems(DisjointObjectProperties_objectProperties_Request, autoBox(objectProperty(propertyId)), objectProperty(propertyId)));
    }

    @Override
    public Set<ItemHits<OWLDatatype,OWLDatatypeDefinitionAxiom>> getEquivalentDatatypeHits(String DatatypeUri) throws NeOnCoreException {
        Set<ItemHits<OWLDatatype,OWLDatatypeDefinitionAxiom>> set = new HashSet<ItemHits<OWLDatatype,OWLDatatypeDefinitionAxiom>>();
        set.addAll((Collection<? extends ItemHits<OWLDatatype,OWLDatatypeDefinitionAxiom>>) Cast.cast(SubDatatype_Collector.getItemHits(SubDatatype_Request, autoBox(datatype(DatatypeUri)), datatype(DatatypeUri))));
        set.addAll((Collection<? extends ItemHits<OWLDatatype,OWLDatatypeDefinitionAxiom>>) Cast.cast(SuperDatatype_Collector.getItemHits(SuperDatatype_Request, autoBox(datatype(DatatypeUri)), datatype(DatatypeUri))));
        return set;
    }
    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentObjectPropertiesAxiom>> getEquivalentObjectPropertyHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(EquivalentObjectProperties_namedObjectProperties_Collector.getItemHits(EquivalentObjectProperties_objectProperties_Request, autoBox(objectProperty(propertyId)), objectProperty(propertyId)));
    }
    @Override
    public Set<ItemHits<OWLClassExpression,OWLDisjointObjectPropertiesAxiom>> getDisjointObjectPropertyHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(DisjointObjectProperties_namedObjectProperties_Collector.getItemHits(DisjointObjectProperties_objectProperties_Request, autoBox(objectProperty(propertyId)), objectProperty(propertyId)));
    }

    @Override
    public Set<OWLDataProperty> getEquivalentDataProperties(String propertyId) throws NeOnCoreException {
        return Cast.cast(EquivalentDataProperties_namedDataProperties_Collector.getItems(EquivalentDataProperties_dataProperties_Request, autoBox(dataProperty(propertyId)), dataProperty(propertyId)));
    }

    @Override
    public Set<OWLDataProperty> getDisjointDataProperties(String propertyId) throws NeOnCoreException {
        return Cast.cast(DisjointDataProperties_namedDataProperties_Collector.getItems(DisjointDataProperties_dataProperties_Request, autoBox(dataProperty(propertyId)), dataProperty(propertyId)));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentDataPropertiesAxiom>> getEquivalentDataPropertyHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(EquivalentDataProperties_namedDataProperties_Collector.getItemHits(EquivalentDataProperties_dataProperties_Request, autoBox(dataProperty(propertyId)), dataProperty(propertyId)));
    }
    @Override
    public Set<ItemHits<OWLClassExpression,OWLDisjointDataPropertiesAxiom>> getDisjointDataPropertyHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(DisjointDataProperties_namedDataProperties_Collector.getItemHits(DisjointDataProperties_dataProperties_Request, autoBox(dataProperty(propertyId)), dataProperty(propertyId)));
    }

    @Override
    public Set<OWLObjectProperty> getInverseObjectProperties(String propertyId) throws NeOnCoreException {
        Set<OWLObjectProperty> seconds = Cast.cast(InverseObjectProperties_namedSecond_Collector.getItems(InverseObjectProperties_first_Request, autoBox(objectProperty(propertyId))));
        Set<OWLObjectProperty> firsts = Cast.cast(InverseObjectProperties_namedFirst_Collector.getItems(InverseObjectProperties_second_Request, autoBox(objectProperty(propertyId))));
        firsts.addAll(seconds);
        return firsts;
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLInverseObjectPropertiesAxiom>> getInverseObjectPropertyHits(String propertyId) throws NeOnCoreException {
        Set<ItemHits<OWLClassExpression,OWLInverseObjectPropertiesAxiom>> seconds = Cast.cast(InverseObjectProperties_namedSecond_Collector.getItemHits(InverseObjectProperties_first_Request, autoBox(objectProperty(propertyId))));
        Set<ItemHits<OWLClassExpression,OWLInverseObjectPropertiesAxiom>> firsts = Cast.cast(InverseObjectProperties_namedFirst_Collector.getItemHits(InverseObjectProperties_second_Request, autoBox(objectProperty(propertyId))));
        firsts.addAll(seconds);
        return firsts;
    }

    @Override
    public Set<OWLClassExpression> getDomainDescriptions(String propertyId) throws NeOnCoreException {
        // TODO (tkr 20080409): make this a bit nicer by making an entity request for propertyId and iteration over the result
        Set<OWLClassExpression> domains = ObjectPropertyDomain_domain_Collector.getItems(ObjectPropertyDomain_objectProperty_Request, autoBox(objectProperty(propertyId)));
        domains.addAll(DataPropertyDomain_domain_Collector.getItems(DataPropertyDomain_dataProperty_Request, autoBox(dataProperty(propertyId))));
        return domains;
    }
//    @Override //NICO removed
//    public Set<OWLClassExpression> getRangeDescriptions(String propertyId) throws NeOnCoreException {
//        // TODO (tkr 20080409): make this a bit nicer by making an entity request for propertyId and iteration over the result
//        Set<OWLClassExpression> ranges = ObjectPropertyRange_range_Collector.getItems(ObjectPropertyRange_objectProperty_Request, autoBox(objectProperty(propertyId)));
//        ranges.addAll(DataPropertyRange_range_Collector.getItems(DataPropertyRange_dataProperty_Request, autoBox(dataProperty(propertyId))));
//        return ranges;
//    }

    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesForDomain(String classId) throws NeOnCoreException {
        Set<OWLAnnotationProperty> properties =
            AnnotationPropertyDomain_property_Collector.getItems(AnnotationPropertyDomain_domain_Request, autoBox(owlClass(classId)));
        return properties;            
    }
    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesForRange(String classId) throws NeOnCoreException {
        Set<OWLAnnotationProperty> properties =
            AnnotationPropertyRange_property_Collector.getItems(AnnotationPropertyRange_range_Request, autoBox(owlClass(classId)));
        return properties;            
    }

    @Override
    public Set<ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyDomainAxiom>> getAnnotationPropertiesForDomainHits(String classId) throws NeOnCoreException {
        Set<ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyDomainAxiom>> properties =
            AnnotationPropertyDomain_property_Collector.getItemHits(AnnotationPropertyDomain_domain_Request, autoBox(owlClass(classId)));
        return properties;            
    }
    @Override
    public Set<ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyRangeAxiom>> getAnnotationPropertiesForRangeHits(String classId) throws NeOnCoreException {
        Set<ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyRangeAxiom>> properties =
            AnnotationPropertyRange_property_Collector.getItemHits(AnnotationPropertyRange_range_Request, autoBox(owlClass(classId)));
        return properties;            
    }

    @Override
    public Set<ItemHits<IRI,OWLAnnotationPropertyDomainAxiom>> getAnnotationPropertyDomainHits(String propertyId) throws NeOnCoreException {
        Set<ItemHits<IRI,OWLAnnotationPropertyDomainAxiom>> domains = AnnotationPropertyDomain_domain_Collector.getItemHits(AnnotationPropertyDomain_annotationProperty_Request, autoBox(annotationProperty(propertyId)));
        return domains;
    }

    @Override
    public Set<ItemHits<IRI,OWLAnnotationPropertyRangeAxiom>> getAnnotationPropertyRangeHits(String propertyId) throws NeOnCoreException {
        Set<ItemHits<IRI,OWLAnnotationPropertyRangeAxiom>> ranges = AnnotationPropertyRange_range_Collector.getItemHits(AnnotationPropertyRange_annotationProperty_Request, autoBox(annotationProperty(propertyId)));
        return ranges;
    }
    @Override
    public Set<OWLDataProperty> getDataPropertiesForDomain(String classId) throws NeOnCoreException{
        Set<OWLDataProperty> properties =
            DataPropertyDomain_property_Collector.getItems(DataPropertyDomain_domain_Request, autoBox(owlClass(classId)));
        return properties;       
    }
    @Override
    public Set<ItemHits<OWLDataProperty,OWLDataPropertyDomainAxiom>> getDataPropertiesForDomainHits(String classId) throws NeOnCoreException {
        Set<ItemHits<OWLDataProperty,OWLDataPropertyDomainAxiom>> properties =
            DataPropertyDomain_property_Collector.getItemHits(DataPropertyDomain_domain_Request, autoBox(owlClass(classId)));
        return properties;            
    }

    @Override
    public Set<OWLDataProperty> getDataPropertiesForRange(String classId) throws NeOnCoreException{
        Set<OWLDataProperty> properties =
            DataPropertyRange_property_Collector.getItems(DataPropertyRange_range_Request, autoBox(owlClass(classId)));
        return properties;           
    }
    @Override
    public Set<ItemHits<OWLDataProperty,OWLDataPropertyRangeAxiom>> getDataPropertiesForRangeHits(String classId) throws NeOnCoreException {
        Set<ItemHits<OWLDataProperty,OWLDataPropertyRangeAxiom>> properties =
            DataPropertyRange_property_Collector.getItemHits(DataPropertyRange_range_Request, autoBox(owlDatatype(classId)));
        return properties;            
    }

    @Override
    public Set<OWLObjectProperty> getObjectPropertiesForDomain(String classId) throws NeOnCoreException{
        Set<OWLObjectProperty> properties =
            ObjectPropertyDomain_property_Collector.getItems(ObjectPropertyDomain_domain_Request, autoBox(owlClass(classId)));
        return properties;            
    }
    @Override
    public Set<OWLObjectProperty> getObjectPropertiesForRange(String classId) throws NeOnCoreException{
        Set<OWLObjectProperty> properties =
            ObjectPropertyRange_property_Collector.getItems(ObjectPropertyRange_range_Request, autoBox(owlClass(classId)));
        return properties;            
    }

    @Override
    public Set<ItemHits<OWLObjectProperty,OWLObjectPropertyDomainAxiom>> getObjectPropertiesForDomainHits(String classId) throws NeOnCoreException {
        Set<ItemHits<OWLObjectProperty,OWLObjectPropertyDomainAxiom>> properties =
            ObjectPropertyDomain_property_Collector.getItemHits(ObjectPropertyDomain_domain_Request, autoBox(owlClass(classId)));
        return properties;            
    }

    @Override
    public Set<ItemHits<OWLObjectProperty,OWLObjectPropertyRangeAxiom>> getObjectPropertiesForRangeHits(String classId) throws NeOnCoreException {
        Set<ItemHits<OWLObjectProperty,OWLObjectPropertyRangeAxiom>> properties =
            ObjectPropertyRange_property_Collector.getItemHits(ObjectPropertyRange_range_Request, autoBox(owlClass(classId)));
        return properties;            
    }
    @Override
    public Set<ItemHits<OWLClassExpression,OWLObjectPropertyDomainAxiom>> getObjectPropertyDomainHits(String propertyId) throws NeOnCoreException {
        // TODO (tkr 20080409): make this a bit nicer by making an entity request for propertyId and iteration over the result
        Set<ItemHits<OWLClassExpression,OWLObjectPropertyDomainAxiom>> domains = ObjectPropertyDomain_domain_Collector.getItemHits(ObjectPropertyDomain_objectProperty_Request, autoBox(objectProperty(propertyId)));
        return domains;
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLObjectPropertyRangeAxiom>> getObjectPropertyRangeHits(String propertyId) throws NeOnCoreException {
        // TODO (tkr 20080409): make this a bit nicer by making an entity request for propertyId and iteration over the result
        Set<ItemHits<OWLClassExpression,OWLObjectPropertyRangeAxiom>> ranges = ObjectPropertyRange_range_Collector.getItemHits(ObjectPropertyRange_objectProperty_Request, autoBox(objectProperty(propertyId)));
        return ranges;
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLDataPropertyDomainAxiom>> getDataPropertyDomainHits(String propertyId) throws NeOnCoreException {
        // TODO (tkr 20080409): make this a bit nicer by making an entity request for propertyId and iteration over the result
        Set<ItemHits<OWLClassExpression,OWLDataPropertyDomainAxiom>> domains = DataPropertyDomain_domain_Collector.getItemHits(DataPropertyDomain_dataProperty_Request, autoBox(dataProperty(propertyId)));
        return domains;
    }
//    @Override //NICO removed
//    public Set<ItemHits<OWLClassExpression,OWLDataPropertyRangeAxiom>> getDataPropertyRangeHits(String propertyId) throws NeOnCoreException {
//        // TODO (tkr 20080409): make this a bit nicer by making an entity request for propertyId and iteration over the result
//        Set<ItemHits<OWLClassExpression,OWLDataPropertyRangeAxiom>> ranges = DataPropertyRange_range_Collector.getItemHits(DataPropertyRange_dataProperty_Request, autoBox(dataProperty(propertyId)));
//        return ranges;
//    }

    @Override
    public Set<OWLDataRange> getDataPropertyDataRanges(String propertyId) throws NeOnCoreException {
        return DataPropertyRange_range_Collector.getItems(DataPropertyRange_dataProperty_Request, autoBox(dataProperty(propertyId)));
    }

    @Override
    public Set<ItemHits<OWLDataRange,OWLDataPropertyRangeAxiom>> getDataPropertyDataRangeHits(String propertyId) throws NeOnCoreException {
        return DataPropertyRange_range_Collector.getItemHits(DataPropertyRange_dataProperty_Request, autoBox(dataProperty(propertyId)));
    }

    @Override
    public Set<OWLClassExpression> getObjectPropertyRangeDescriptions(String propertyId) throws NeOnCoreException {
        return ObjectPropertyRange_range_Collector.getItems(ObjectPropertyRange_objectProperty_Request, autoBox(objectProperty(propertyId)));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLObjectPropertyRangeAxiom>> getObjectPropertyRangeDescriptionHits(String propertyId) throws NeOnCoreException {
        return ObjectPropertyRange_range_Collector.getItemHits(ObjectPropertyRange_objectProperty_Request, autoBox(objectProperty(propertyId)));
    }

    @Override
    public boolean isFunctional(String propertyId) throws NeOnCoreException {
        Set<OWLObjectPropertyCharacteristicAxiom> functionalObjectPropertyAttributes = OWLFunctionalObjectPropertyAxiom_property_Request.getAxioms(objectProperty(propertyId));
        if (functionalObjectPropertyAttributes.size() > 0) {
            return true;
        }
        Set<OWLDataPropertyCharacteristicAxiom> functionalDataPropertyAttributes = OWLFunctionalDataPropertyAxiom_dataProperty_Request.getAxioms(dataProperty(propertyId));
        return functionalDataPropertyAttributes.size() > 0;
    }

    @Override
    public boolean isFunctional(String propertyId, boolean includeImported) throws NeOnCoreException {
        Set<OWLObjectPropertyCharacteristicAxiom> functionalObjectPropertyAttributes = OWLFunctionalObjectPropertyAxiom_property_Request.getAxioms(includeImported, objectProperty(propertyId));
        if (functionalObjectPropertyAttributes.size() > 0) {
            return true;
        }
        Set<OWLDataPropertyCharacteristicAxiom> functionalDataPropertyAttributes = OWLFunctionalDataPropertyAxiom_dataProperty_Request.getAxioms(includeImported, dataProperty(propertyId));
        return functionalDataPropertyAttributes.size() > 0;
    }

    @Override
    public boolean isInverseFunctional(String propertyId) throws NeOnCoreException {
        return OWLInverseFunctionalObjectPropertyAxiom_property_Request.getAxioms(objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isInverseFunctional(String propertyId, boolean includeImported) throws NeOnCoreException {
        return OWLInverseFunctionalObjectPropertyAxiom_property_Request.getAxioms(includeImported, objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isReflexive(String propertyId) throws NeOnCoreException {
        return OWLReflexiveObjectPropertyAxiom_property_Request.getAxioms(objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isReflexive(String propertyId, boolean includeImported) throws NeOnCoreException {
        return OWLReflexiveObjectPropertyAxiom_property_Request.getAxioms(includeImported, objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isIrreflexive(String propertyId) throws NeOnCoreException {
        return OWLIrreflexiveObjectPropertyAxiom_property_Request.getAxioms(objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isIrreflexive(String propertyId, boolean includeImported) throws NeOnCoreException {
        return OWLIrreflexiveObjectPropertyAxiom_property_Request.getAxioms(includeImported, objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isTransitive(String propertyId) throws NeOnCoreException {
        return OWLTransitiveObjectPropertyAxiom_property_Request.getAxioms(objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isTransitive(String propertyId, boolean includeImported) throws NeOnCoreException {
        return OWLTransitiveObjectPropertyAxiom_property_Request.getAxioms(includeImported, objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isSymmetric(String propertyId) throws NeOnCoreException {
        return OWLSymmetricObjectPropertyAxiom_property_Request.getAxioms(objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isSymmetric(String propertyId, boolean includeImported) throws NeOnCoreException {
        return OWLSymmetricObjectPropertyAxiom_property_Request.getAxioms(includeImported, objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isAsymmetric(String propertyId) throws NeOnCoreException {
        return OWLAsymmetricObjectPropertyAxiom_property_Request.getAxioms(objectProperty(propertyId)).size() > 0;
    }

    @Override
    public boolean isAsymmetric(String propertyId, boolean includeImported) throws NeOnCoreException {
        return OWLAsymmetricObjectPropertyAxiom_property_Request.getAxioms(includeImported, objectProperty(propertyId)).size() > 0;
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // INSTANCES
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _INSTANCE_METHODS______________________() {
    }

    @Override
    public Set<OWLClassExpression> getDescriptions(String individualId) throws NeOnCoreException {
        return ClassMember_description_ComplexDescriptionsOnly_Collector.getItems(ClassMember_individual_Request, autoBox(individual(individualId)));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> getDescriptionHits(String individualId) throws NeOnCoreException {
        return ClassMember_description_ComplexDescriptionsOnly_Collector.getItemHits(ClassMember_individual_Request, autoBox(individual(individualId)));
    }

    @Override
    public Set<OWLClass> getClasses(String individualId) throws NeOnCoreException {
        return Cast.cast(ClassMember_description_NamedDescriptionsOnly_Collector.getItems(ClassMember_individual_Request, autoBox(individual(individualId))));
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> getClassHits(String individualId) throws NeOnCoreException {
        return Cast.cast(ClassMember_description_NamedDescriptionsOnly_Collector.getItemHits(ClassMember_individual_Request, autoBox(individual(individualId))));
    }

    @Override
    public Set<OWLIndividual> getEquivalentIndividuals(String individualId) throws NeOnCoreException {
        return SameIndividual_individuals_Collector.getItems(SameIndividual_individuals_Request, autoBox(individual(individualId)), individual(individualId));
    }

    @Override
    public Set<ItemHits<OWLIndividual,OWLSameIndividualAxiom>> getEquivalentIndividualHits(String individualId) throws NeOnCoreException {
        return Cast.cast(SameIndividual_individuals_Collector.getItemHits(SameIndividual_individuals_Request, autoBox(individual(individualId)), individual(individualId)));
    }

    @Override
    public Set<OWLIndividual> getDifferentIndividuals(String individualId) throws NeOnCoreException {
        return DifferentIndividuals_individuals_Collector.getItems(DifferentIndividuals_individuals_Request, autoBox(individual(individualId)), individual(individualId));
    }

    @Override
    public Set<ItemHits<OWLIndividual,OWLDifferentIndividualsAxiom>> getDifferentIndividualHits(String individualId) throws NeOnCoreException {
        return Cast.cast(DifferentIndividuals_individuals_Collector.getItemHits(DifferentIndividuals_individuals_Request, autoBox(individual(individualId)), individual(individualId)));
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyMembers(String individualId) throws NeOnCoreException {
        return DataPropertyMember_sourceIndividual_Request.getAxioms(individual(individualId));
    }

    @Override
    public Set<LocatedItem<OWLDataPropertyAssertionAxiom>> getDataPropertyMemberHits(String individualId) throws NeOnCoreException {
        return DataPropertyMember_sourceIndividual_Request.getLocatedAxioms(individual(individualId));
    }

    @Override
    public Set<LocatedItem<OWLDataPropertyAssertionAxiom>> getDataPropertyMemberHitsForProperty(OWLDataProperty property) throws NeOnCoreException {
        return DataPropertyMember_property_Request.getLocatedAxioms(property);
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyMembers(String individualId, String propertyId) throws NeOnCoreException {
        return DataPropertyMember_dataProperty_sourceIndividual_Request.getAxioms(dataProperty(propertyId), individual(individualId));
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyMembers(String individualId) throws NeOnCoreException {
        return ObjectPropertyMember_sourceIndividual_Request.getAxioms(individual(individualId));
    }

    @Override
    public Set<LocatedItem<OWLObjectPropertyAssertionAxiom>> getObjectPropertyMemberHits(String individualId) throws NeOnCoreException {
        return ObjectPropertyMember_sourceIndividual_Request.getLocatedAxioms(individual(individualId));
    }

    @Override
    public Set<LocatedItem<OWLObjectPropertyAssertionAxiom>> getObjectPropertyMemberHitsForProperty(OWLObjectProperty property) throws NeOnCoreException {
        return ObjectPropertyMember_property_Request.getLocatedAxioms(property);
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyValues(String individualId, String propertyId) throws NeOnCoreException {
        return ObjectPropertyMember_objectProperty_sourceIndividual_Request.getAxioms(objectProperty(propertyId), individual(individualId));
    }

    @Override
    public Set<OWLAxiom> getAxioms(OWLEntity entity, boolean includeImportedOntologies) throws NeOnCoreException {
        return OWLAxiom_containsEntity_Request.getAxioms(includeImportedOntologies, entity);
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // LISTENERS
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _LISTENER_METHODS______________________() {
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // WRITE METHODS
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _WRITE_METHODS__________________________() {
    }

    @Override
    public void addEntity(OWLEntity newEntity) throws NeOnCoreException {
        applyChanges(Collections.singletonList(getAddAxiom(declaration(newEntity))));
    }

    @Override
    public void addAxiom(OWLAxiom newAxiom) throws NeOnCoreException {
        applyChanges(Collections.singletonList(getAddAxiom(newAxiom)));
    }

    @Override
    public void addAxioms(Collection<OWLAxiom> newAxioms) throws NeOnCoreException {
        List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
        for (OWLAxiom axiom: newAxioms) {
            changes.add(getAddAxiom(axiom));
        }
        applyChanges(changes);
    }

    @Override
    public void removeAxiom(OWLAxiom oldAxiom) throws NeOnCoreException {
        applyChanges(Collections.singletonList(getRemoveAxiom(oldAxiom)));
    }

    @Override
    public void removeAxioms(Collection<OWLAxiom> oldAxioms) throws NeOnCoreException {
        List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
        for (OWLAxiom axiom: oldAxioms) {
            changes.add(getRemoveAxiom(axiom));
        }
        applyChanges(changes);
    }

    @Override
    public void replaceAxiom(OWLAxiom oldAxiom, OWLAxiom newAxiom) throws NeOnCoreException {
        List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
        changes.add(getRemoveAxiom(oldAxiom));
        changes.add(getAddAxiom(newAxiom));
        applyChanges(changes);
    }

    @Override
    public void replaceAxioms(Collection<OWLAxiom> oldAxioms, Collection<OWLAxiom> newAxioms) throws NeOnCoreException {
        List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
        for (OWLAxiom axiom: oldAxioms) {
            changes.add(getRemoveAxiom(axiom));
        }
        for (OWLAxiom axiom: newAxioms) {
            changes.add(getAddAxiom(axiom));
        }
        applyChanges(changes);
    }
    
    private List<OWLAxiomChange> createRemoveChangeEvents(Collection<? extends OWLAxiom> axioms) {
        List<OWLAxiomChange> result = new ArrayList<OWLAxiomChange>();
        for (OWLAxiom axiom: axioms) {
            result.add(getRemoveAxiom(axiom));
        }
        return result;
    }

    @Override
    public void delEntity(OWLEntity oldEntity, IProgressMonitor monitor) throws NeOnCoreException,InterruptedException,InvocationTargetException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        for (OWLModel model: getRelevantOntologies(getIncludeImportedOntologies())) {
            List<OWLAxiomChange> changes = createRemoveChangeEvents(model.getReferencingAxioms(oldEntity));
            model.applyChanges(changes);
        }
    }

    @Override
    public void renameEntity(OWLEntity oldEntity, String newUri, IProgressMonitor monitor) throws NeOnCoreException,InterruptedException,InvocationTargetException {
        if (oldEntity == null || newUri == null) {
            throw new IllegalArgumentException();
        }
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        OWLObjectDuplicator replaceVisitor = new OWLObjectDuplicator(Collections.singletonMap((OWLEntity)oldEntity, OWLUtilities.toIRI(newUri)), getOWLDataFactory());

        List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
        Set<OWLAxiom> axioms = getOntology().getReferencingAxioms(oldEntity);
        monitor.beginTask(Messages.getString("OWLModelCore.85"), axioms.size());
        for (OWLAxiom a: axioms) {
            changes.add(getRemoveAxiom(a));
            changes.add(getAddAxiom((OWLAxiom)replaceVisitor.duplicateObject(a)));
            monitor.worked(1);
        }
        applyChanges(changes);

        // change annotation subjects
        List<OWLAxiomChange> annotationSubjectChanges = new ArrayList<OWLAxiomChange>();
        Set<OWLAnnotationAssertionAxiom> annotationAssertions = getOntology().getAnnotationAssertionAxioms(oldEntity.getIRI());
        IRI newIRI = IRI.create(newUri);
        for (OWLAnnotationAssertionAxiom a: annotationAssertions) {
            annotationSubjectChanges.add(getRemoveAxiom(a));
            annotationSubjectChanges.add(getAddAxiom(getOWLDataFactory().getOWLAnnotationAssertionAxiom(a.getAnnotation().getProperty(), newIRI, a.getAnnotation().getValue())));
            monitor.worked(1);
        }
        applyChanges(annotationSubjectChanges);

        // change annotation values... sadly we have to iterate over all annotation assertions 
        // since we cannot set a condition on the annotation value
        List<OWLAxiomChange> annotationValueChanges = new ArrayList<OWLAxiomChange>();
        Set<OWLAnnotationAssertionAxiom> annotationAssertions2 = getOntology().getAxioms(AxiomType.ANNOTATION_ASSERTION);
        for (OWLAnnotationAssertionAxiom a: annotationAssertions2) {
            if(a.getValue().toString().equals(OWLUtilities.toString(oldEntity))){
                annotationValueChanges.add(getRemoveAxiom(a));
                annotationValueChanges.add(getAddAxiom(getOWLDataFactory().getOWLAnnotationAssertionAxiom(a.getAnnotation().getProperty(), a.getSubject(), newIRI)));
            }
            monitor.worked(1);
        }
        applyChanges(annotationValueChanges);
    }

    // ///////////////////////////////////////////////////////////////////////
    // 
    // HELPER METHODS
    //
    // ///////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void _HELPER_METHODS________________________() {
    }

    @Override
    public Set<OWLEntity> getEntity(String owlEntityId) throws NeOnCoreException {
        IRI uri = OWLUtilities.toIRI(owlEntityId);
        Set<OWLEntity> result = new LinkedHashSet<OWLEntity>();
        for (OWLModel model: getRelevantOntologies(getIncludeImportedOntologies())) {
            OWLOntology ontology = model.getOntology();
            if (ontology.containsClassInSignature(uri)) {
                result.add(getOWLDataFactory().getOWLClass(uri));
            }
            if (ontology.containsDataPropertyInSignature(uri)) {
                result.add(getOWLDataFactory().getOWLDataProperty(uri));
            }
            if (ontology.containsDatatypeInSignature(uri)) {
                result.add(getOWLDataFactory().getOWLDatatype(uri));
            }
            if (ontology.containsIndividualInSignature(uri)) {
                result.add(getOWLDataFactory().getOWLNamedIndividual(uri));
            }
            if (ontology.containsObjectPropertyInSignature(uri)) {
                result.add(getOWLDataFactory().getOWLObjectProperty(uri));
            }
            if (ontology.containsAnnotationPropertyInSignature(uri)) {
                result.add(getOWLDataFactory().getOWLAnnotationProperty(uri));
            }
        }
        return result;
    }

    private OWLAnnotationProperty annotationProperty(String uri) throws NeOnCoreException {
        return getOWLDataFactory().getOWLAnnotationProperty(OWLUtilities.toIRI(uri));
    }

    private OWLDatatype datatype(String uri) throws NeOnCoreException {
        return getOWLDataFactory().getOWLDatatype(OWLUtilities.toIRI(uri));
    }
    private OWLObjectProperty objectProperty(String uri) throws NeOnCoreException {
        return getOWLDataFactory().getOWLObjectProperty(OWLUtilities.toIRI(uri));
    }

    private OWLDataProperty dataProperty(String uri) throws NeOnCoreException {
        return getOWLDataFactory().getOWLDataProperty(OWLUtilities.toIRI(uri));
    }

    private OWLIndividual individual(String uri) throws NeOnCoreException {
        return OWLUtilities.individual(uri);
    }

    private OWLClass owlClass(String uri) throws NeOnCoreException {
        return getOWLDataFactory().getOWLClass(OWLUtilities.toIRI(uri));
    }
    private OWLDatatype owlDatatype(String uri) throws NeOnCoreException {
        OWLDatatype oWLDatatype = getOWLDataFactory().getOWLDatatype(OWLUtilities.toIRI(uri));
        return oWLDatatype;
    }

    private OWLDeclarationAxiom declaration(OWLEntity entity) throws NeOnCoreException {
        return getOWLDataFactory().getOWLDeclarationAxiom(entity);
    }

    @Override
    public Set<OWLAnnotationProperty> getSubAnnotationProperties(String propertyId) throws NeOnCoreException {
        return getItemsFromItemHits(getSubAnnotationPropertyHits(propertyId));
    }

    @Override
    public Set<ItemHits<OWLAnnotationProperty,OWLSubAnnotationPropertyOfAxiom>> getSubAnnotationPropertyHits(String propertyId) throws NeOnCoreException {
        Set<LocatedItem<OWLSubAnnotationPropertyOfAxiom>> locatedAxioms = getLocatedChildEdgeAxioms(OWLAnnotationProperty.class, OWLSubAnnotationPropertyOfAxiom.class, OWLModelFactory.getOWLDataFactory(getProjectId()).getOWLAnnotationProperty(OWLUtilities.toIRI(propertyId)));
        return Cast.cast(SubAnnotationPropertyOf_subAnnotationProperty_Collector.getItemHits(getGroupBy(), locatedAxioms));
    }

    @Override
    public Set<OWLAnnotationProperty> getSuperAnnotationProperties(String propertyId) throws NeOnCoreException {
        return Cast.cast(SubAnnotationPropertyOf_superAnnotationProperty_Collector.getItems(SubAnnotationPropertyOf_subProperty_Request, autoBox(annotationProperty(propertyId))));
    }

    @Override
    public Set<ItemHits<OWLAnnotationProperty,OWLSubAnnotationPropertyOfAxiom>> getSuperAnnotationPropertyHits(String propertyId) throws NeOnCoreException {
        return Cast.cast(SubAnnotationPropertyOf_superAnnotationProperty_Collector.getItemHits(SubAnnotationPropertyOf_subProperty_Request, autoBox(annotationProperty(propertyId))));
    }
    
    private Set<OWLSubClassOfAxiom> getSubClassOfAxioms() {
        Set<OWLSubClassOfAxiom> axioms = new LinkedHashSet<OWLSubClassOfAxiom>();
        for (OWLModel model: getRelevantOntologies(getIncludeImportedOntologies())) {
            OWLModelCore modelCore = getModelCore(model);
            modelCore.assertSubClassOfCache();
            axioms.addAll(modelCore._subClassOfAxioms);
        }
        return axioms;
    }

    @Override
    public <E extends OWLObject> Set<E> getEntities(Class<E> type, boolean includeImportedOntologies) throws NeOnCoreException {
        Set<E> entities = new LinkedHashSet<E>();
        for (OWLModel model: getRelevantOntologies(includeImportedOntologies)) {
            OWLModelCore modelCore = getModelCore(model);
            modelCore.assertEntityCache();
            Set<? extends OWLEntity> x = modelCore._entities.get(type);
            Set<E> entitiesInModel = Cast.cast(x);
            entities.addAll(entitiesInModel);
        }
        return entities;
    }
    
    private <E extends OWLObject> Set<E> getEntities(Class<E> type) throws NeOnCoreException {
        return getEntities(type, getIncludeImportedOntologies());
    }
    
    private OWLModelCore getModelCore(OWLModel model) {
        if (model instanceof ConnectionFailureAwareOWLModel) {
            return getModelCore(((ConnectionFailureAwareOWLModel)model).getOWLModel());
        } else if (model instanceof OWLModelCore) {
            return (OWLModelCore)model;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public OWLDataFactory getOWLDataFactory() throws NeOnCoreException {
        return OWLModelFactory.getOWLDataFactory(getProjectId());
    }

    @Override
    public String getOntologyURI() {
        return getOntologyURI(getOntology());
    }
    
    @Override
    public void applyChanges(List<OWLAxiomChange> changes) throws NeOnCoreException {
        try {
            _manager.applyChanges((List<? extends OWLOntologyChange>)changes);
        } catch (OWLOntologyChangeException e) {
            throw new InternalNeOnException(e);
        }
    }
    
    @Override
    public void addAnnotation(OWLAnnotation annotation) throws NeOnCoreException {
        try {
            _manager.applyChange(new AddOntologyAnnotation(_ontology, annotation));
        } catch (OWLOntologyChangeException e) {
            throw new InternalNeOnException(e);
        }
    }
    
    @Override
    public void removeAnnotation(OWLAnnotation annotation) throws NeOnCoreException {
        try {
            _manager.applyChange(new RemoveOntologyAnnotation(_ontology, annotation));
        } catch (OWLOntologyChangeException e) {
            throw new InternalNeOnException(e);
        }
    }


    @Override
    public void addToImports(OWLModel newOntology) throws NeOnCoreException {
        getOntologyProject().addToImportedOntologies(getOntologyURI(), Collections.singleton(newOntology.getOntologyURI()));
    }

    @Override
    public void removeFromImports(OWLModel oldOntology) throws NeOnCoreException {
        getOntologyProject().removeFromImportedOntologies(getOntologyURI(), Collections.singleton(oldOntology.getOntologyURI()));
    }

    @Override
    public void renameOntology(String newUri) throws NeOnCoreException {
        try {
            _manager.applyChange(new SetOntologyID(getOntology(), new OWLOntologyID(IRI.create(newUri))));
        } catch (OWLOntologyChangeException e) {
            throw new InternalNeOnException(e);
        }
    }

    @Override
    public boolean containsAxiom(OWLAxiom axiom, boolean includeImportedOntologies) throws NeOnCoreException {
        if (!includeImportedOntologies) {
            return getOntology().containsAxiom(axiom);
        } else {
            if (getOntology().containsAxiom(axiom)) {
                return true;
            }
            for (OWLModel model: getImportedOntologies()) {
                if (model.containsAxiom(axiom, false)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity) {
        return getReferencingAxioms(owlEntity, false);
    }
 
    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity, boolean includeImported) {
        Set<OWLAxiom> referencingAxioms = new HashSet<OWLAxiom>();
        referencingAxioms.addAll(getReferencingAnnotationAssertionAxioms(owlEntity.getIRI(), includeImported));
        referencingAxioms.addAll(_ontology.getReferencingAxioms(owlEntity, includeImported));
        return referencingAxioms;
    }
 
    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLIndividual individual) throws NeOnCoreException {
        return getReferencingAxioms(individual, false);
    }

    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLIndividual individual, boolean includeImported) throws NeOnCoreException {
        Set<OWLAxiom> referencingAxioms = new HashSet<OWLAxiom>();
        referencingAxioms.addAll(getReferencingAnnotationAssertionAxioms(IRI.create(individual.toStringID()), includeImported));
        if (individual instanceof OWLAnonymousIndividual) {
            referencingAxioms.addAll(_ontology.getReferencingAxioms((OWLAnonymousIndividual)individual));
        }else{
            referencingAxioms.addAll(_ontology.getReferencingAxioms((OWLEntity)individual, includeImported));
        }
        return referencingAxioms;
    }
    public Set<OWLAnnotationAssertionAxiom> getReferencingAnnotationAssertionAxioms(OWLAnnotationSubject subject, boolean includeImported){
        return _ontology.getAnnotationAssertionAxioms(subject);
    }

    @Override
    public String getPhysicalURI() throws NeOnCoreException {
        return getOntologyProject().getPhysicalURIForOntology(getOntologyURI());
    }

    @Override
    public OWLAxiomChange getAddAxiom(OWLAxiom axiom) {
        return new AddAxiom(_ontology, axiom);
    }

    @Override
    public OWLAxiomChange getRemoveAxiom(OWLAxiom axiom) {
        return new RemoveAxiom(_ontology, axiom);
    }
    
    @Override
    public IOntologyProject getOntologyProject() throws NeOnCoreException {
        return _ontologyProject;
    }

    @Override
    public void namespacePrefixChanged(String prefix, String namespace) throws NeOnCoreException {
        if (_namespaces != null) {
            if (namespace != null) {
                _namespaces.registerPrefix(prefix, namespace);
                if (OWLNamespaces.DEFAULT_NAMESPACE_PREFIX.equals(prefix)) {
                    // just to make it sure
                    _namespaces.setDefaultNamespace(namespace);
                }
            } else {
                _namespaces.unregisterPrefix(prefix);
                if (OWLNamespaces.DEFAULT_NAMESPACE_PREFIX.equals(prefix)) {
                    // just to make it sure
                    _namespaces.setDefaultNamespace(null);
                }
            }
        }
    }

    @Override
    public Set<OWLAnnotationAssertionAxiom> getAllAnnotationAxioms() throws NeOnCoreException {
            return _ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION);
    }

    @Override
    public Set<OWLAnnotationAssertionAxiom> getAllAnnotationAxioms(boolean includeImported) throws NeOnCoreException {
            return _ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION, includeImported);
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getAllDataPropertyAssertionAxioms() throws NeOnCoreException {
        return _ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getAllDataPropertyAssertionAxioms(boolean includeImported) throws NeOnCoreException {
        return _ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION, includeImported);
    }
}
