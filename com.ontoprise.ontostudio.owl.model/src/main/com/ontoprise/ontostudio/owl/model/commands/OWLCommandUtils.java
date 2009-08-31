/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 20.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands;

import java.util.Set;

import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author werner
 * 
 */
public class OWLCommandUtils {

    public static final String EMPTY_LANGUAGE = "--"; //$NON-NLS-1$
    public static final String INCL = "INCL"; //$NON-NLS-1$
    public static final String EQUIV = "EQUIV"; //$NON-NLS-1$
    public static final String SOME = "SOME"; //$NON-NLS-1$
    public static final String ALL = "ALL"; //$NON-NLS-1$
    public static final String HAS_VALUE = "HAS_VALUE"; //$NON-NLS-1$
    public static final String AT_LEAST_MIN = "AT_LEAST/MIN"; //$NON-NLS-1$
    public static final String AT_MOST_MAX = "AT_MOST/MAX"; //$NON-NLS-1$
    public static final String EXACTLY_CARDINALITY = "EXACTLY/CARD."; //"EXACTLY/CARDINALITY"; //$NON-NLS-1$
    public static final String DATA_PROP = "dataProp"; //$NON-NLS-1$
    public static final String OBJECT_PROP = "objectProp"; //$NON-NLS-1$

    public static final String FUNCTIONAL = "functional"; //$NON-NLS-1$
    public static final String INVERSE_FUNCTIONAL = "inverse_functional"; //$NON-NLS-1$
    public static final String SYMMETRIC = "symmetric"; //$NON-NLS-1$
    public static final String TRANSITIVE = "transitive"; //$NON-NLS-1$

    public static OWLAxiom createAxiom(String clazzId, String clazzType, String quantor, String propertyId, String range, String quantity, String ontologyId, String project) throws NeOnCoreException {
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyId, project);
        return (OWLAxiom) OWLUtilities.axiom(createAxiomText(clazzId, clazzType, quantor, propertyId, range, quantity, ontologyId, project), owlModel.getNamespaces(), owlModel.getOWLDataFactory());
    }

    public static String createAxiomText(String clazzId, String clazzType, String quantor, String propertyId, String range, String quantity, String ontologyId, String project) throws NeOnCoreException {
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyId, project);
        OWLDataFactory factory = owlModel.getOWLDataFactory();
        OWLNamespaces ns = owlModel.getNamespaces();
        String expandedURI = ns.expandString(propertyId);

        if ((range == null) || range.equals("")) { //$NON-NLS-1$
            range = OWLConstants.OWL_THING_URI;
        }

        OWLAxiom axiom = null;
        OWLClassExpression desc = null;
        try {
            if (isDataProperty(expandedURI, ontologyId, project)) {
                OWLDataPropertyExpression property = factory.getOWLDataProperty(OWLUtilities.toURI(expandedURI));
                if (quantor.equals(HAS_VALUE)) {
                    desc = factory.getOWLDataHasValue(property, OWLUtilities.constant(range, ns, factory));
                } else {
                    OWLDataRange propertyRange = OWLUtilities.dataRange(range, ns, factory);
                    if (quantor.equals(ALL)) {
                        desc = factory.getOWLDataAllValuesFrom((OWLDataPropertyExpression)property, (OWLDataRange)propertyRange);
                    } else if (quantor.equals(SOME)) {
                        desc = factory.getOWLDataSomeValuesFrom((OWLDataPropertyExpression)property, (OWLDataRange)propertyRange);
                    } else if (quantity == null || quantity.equals("")) { //$NON-NLS-1$
                        // user has to enter min cardinality first
                        return null;
                    } else if (quantor.equals(AT_LEAST_MIN)) {
                        desc = factory.getOWLDataMinCardinality(Integer.parseInt(quantity), property, propertyRange);
                    } else if (quantor.equals(AT_MOST_MAX)) {
                        desc = factory.getOWLDataMaxCardinality(Integer.parseInt(quantity), property, propertyRange);
                    } else if (quantor.equals(EXACTLY_CARDINALITY)) {
                        desc = factory.getOWLDataExactCardinality(Integer.parseInt(quantity), property, propertyRange);
                    } else {
                        // TODO error: unknown quantor
                        return null;
                    }
                }
            } else {
                OWLObjectPropertyExpression property = factory.getOWLObjectProperty(OWLUtilities.toURI(expandedURI));
                if (quantor.equals(HAS_VALUE)) {
                    desc = factory.getOWLObjectHasValue(property, OWLUtilities.individual(range, ns, factory));
                } else {
                    OWLClassExpression propertyRange = OWLUtilities.description(range, ns, factory);
                    if (quantor.equals(ALL)) {
                        desc = factory.getOWLObjectAllValuesFrom((OWLObjectPropertyExpression)property, (OWLClassExpression)propertyRange);
                    } else if (quantor.equals(SOME)) {
                        desc = factory.getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression)property, (OWLClassExpression)propertyRange);
                    } else if (quantity == null || quantity.equals("")) { //$NON-NLS-1$
                        // user has to enter min cardinality first
                        return null;
                    } else if (quantor.equals(AT_LEAST_MIN)) {
                        desc = factory.getOWLObjectMinCardinality(Integer.parseInt(quantity), property, propertyRange);
                    } else if (quantor.equals(AT_MOST_MAX)) {
                        desc = factory.getOWLObjectMaxCardinality(Integer.parseInt(quantity), property, propertyRange);
                    } else if (quantor.equals(EXACTLY_CARDINALITY)) {
                        desc = factory.getOWLObjectExactCardinality(Integer.parseInt(quantity), property, propertyRange);
                    } else {
                        // TODO error: unknown quantor
                        return null;
                    }
                }
            }
            

            if (clazzType.equals(INCL)) {
                axiom = factory.getOWLSubClassOfAxiom(OWLUtilities.description(clazzId, ns, factory), desc);
            } else {
                axiom = factory.getOWLEquivalentClassesAxiom(OWLUtilities.description(clazzId, ns, factory), desc);
            }
        } catch (IllegalArgumentException iae) {
            throw new InternalNeOnException(iae);
        }
        return OWLUtilities.toString(axiom, ns);
    }

    public static OWLAxiom createAxiom(String subClazzDescription, String superClazzDescription, String clazzType, String ontologyId, String project) throws NeOnCoreException {
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyId, project);
        return (OWLAxiom) OWLUtilities.axiom(createAxiomText(subClazzDescription, superClazzDescription, clazzType, ontologyId, project), owlModel.getNamespaces(), owlModel.getOWLDataFactory());
    }

    public static String createAxiomText(String subClazzDescription, String superClazzDescription, String clazzType, String ontologyId, String project) throws NeOnCoreException {
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyId, project);
        OWLNamespaces ns = owlModel.getNamespaces();
        OWLDataFactory factory = owlModel.getOWLDataFactory();
        OWLClassExpression subClazz = OWLUtilities.description(subClazzDescription, ns, factory);
        OWLClassExpression superClazz = OWLUtilities.description(superClazzDescription, ns, factory);
        OWLAxiom axiom;
        if (clazzType.equals(INCL)) {
            axiom = factory.getOWLSubClassOfAxiom(subClazz, superClazz);
        } else {
            axiom = factory.getOWLEquivalentClassesAxiom(subClazz, superClazz);
        }
        return OWLUtilities.toString(axiom, ns);
    }

    public static boolean isDataProperty(String propertyUri, String ontologyUri, String projectId) throws NeOnCoreException {
        String expandedURI = OWLModelFactory.getOWLModel(ontologyUri, projectId).getNamespaces().expandString(propertyUri);
        OWLDataProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLDataProperty(OWLUtilities.toURI(expandedURI));
        Set<OWLDataProperty> properties = OWLModelFactory.getOWLModel(ontologyUri, projectId).getAllDataProperties();
        return properties.contains(prop);
    }

}
