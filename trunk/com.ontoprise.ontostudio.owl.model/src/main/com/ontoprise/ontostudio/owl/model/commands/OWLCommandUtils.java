/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class OWLCommandUtils {

    public static final String EMPTY_LANGUAGE = "--"; //$NON-NLS-1$
    public static final String INCL = "INCL"; //$NON-NLS-1$
    public static final String EQUIV = "EQUIV"; //$NON-NLS-1$
    public static final String SOME = "some"; //$NON-NLS-1$
    public static final String ONLY = "only"; //$NON-NLS-1$
    public static final String HAS_VALUE = "has value"; //$NON-NLS-1$
    public static final String AT_LEAST_MIN = "min. cardinality"; //$NON-NLS-1$
    public static final String AT_MOST_MAX = "max. cardinality"; //$NON-NLS-1$
    public static final String EXACTLY_CARDINALITY = "exact cardinality"; //"EXACTLY/CARDINALITY"; //$NON-NLS-1$
    public static final String DATA_PROP = "dataProp"; //$NON-NLS-1$
    public static final String OBJECT_PROP = "objectProp"; //$NON-NLS-1$

    public static final String FUNCTIONAL = "functional"; //$NON-NLS-1$
    public static final String INVERSE_FUNCTIONAL = "inverse_functional"; //$NON-NLS-1$
    public static final String REFLEXIVE = "reflexive"; //$NON-NLS-1$
    public static final String IRREFLEXIVE = "irreflexive"; //$NON-NLS-1$
    public static final String SYMMETRIC = "symmetric"; //$NON-NLS-1$
    public static final String ASYMMETRIC = "asymmetric"; //$NON-NLS-1$
    public static final String TRANSITIVE = "transitive"; //$NON-NLS-1$

    public static final String HAS_SELF = "has self"; //$NON-NLS-1$

    public static OWLAxiom createAxiom(String clazzId, String clazzType, String quantor, String propertyId, String range, String quantity, String ontologyId, String project) throws NeOnCoreException {
        OWLOntology ontology = OWLModelFactory.getOWLModel(ontologyId, project).getOntology();
        return (OWLAxiom) OWLUtilities.axiom(createAxiomText(clazzId, clazzType, quantor, propertyId, range, quantity, ontologyId, project), ontology);
    }

    public static String createAxiomText(String clazzId, String clazzType, String quantor, String propertyId, String range, String quantity, String ontologyId, String project) throws NeOnCoreException {
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyId, project);
        OWLOntology ontology = owlModel.getOntology();
        OWLDataFactory factory = owlModel.getOWLDataFactory();
//        String expandedURI = ns.expandString(propertyId);

        if (((range == null) || range.equals("")) && !quantor.equals(HAS_SELF)) { //$NON-NLS-1$
            range = OWLConstants.OWL_THING_URI;
        }

        OWLAxiom axiom = null;
        OWLClassExpression desc = null;
        try {
            if (isDataProperty(propertyId, ontologyId, project)) { //isDataProperty(expandedURI, ontologyId, project)
                OWLDataPropertyExpression property = factory.getOWLDataProperty(OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(propertyId, ontology));
                if (quantor.equals(HAS_VALUE)) {
                    desc = factory.getOWLDataHasValue(property, OWLUtilities.constant(range, owlModel));
                } else {
                    OWLDataRange propertyRange = OWLUtilities.dataRange(range, ontology);
                    if (quantor.equals(ONLY)) {
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
                OWLObjectPropertyExpression property = factory.getOWLObjectProperty(OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(propertyId, ontology));
                if (quantor.equals(HAS_VALUE)) {
                    desc = factory.getOWLObjectHasValue(property, OWLUtilities.individual(range, owlModel.getOntology()));
//                    desc = factory.getOWLObjectHasValue(property, OWLUtilities.individual(IRIUtils.ensureValidIRISyntax(range), owlModel.getOntology()));
                } else if (quantor.equals(HAS_SELF)){
                    desc = factory.getOWLObjectHasSelf((OWLObjectPropertyExpression)property);
                } else {
                    OWLClassExpression propertyRange = OWLUtilities.description(range, owlModel.getOntology());
                    if (quantor.equals(ONLY)) {
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
                axiom = factory.getOWLSubClassOfAxiom(OWLUtilities.description(clazzId, ontology), desc);
            } else {
                axiom = factory.getOWLEquivalentClassesAxiom(OWLUtilities.description(clazzId, ontology), desc);
            }
        } catch (IllegalArgumentException iae) {
            throw new InternalNeOnException(iae);
        }
        return OWLUtilities.toString(axiom, ontology);
    }

    public static OWLAxiom createAxiom(String subClazzDescription, String superClazzDescription, String clazzType, String ontologyId, String project) throws NeOnCoreException {
        OWLOntology ontology = OWLModelFactory.getOWLModel(ontologyId, project).getOntology();
        return (OWLAxiom) OWLUtilities.axiom(createAxiomText(subClazzDescription, superClazzDescription, clazzType, ontologyId, project), ontology);
    }

    public static String createAxiomText(String subClazzDescription, String superClazzDescription, String clazzType, String ontologyId, String project) throws NeOnCoreException {
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyId, project);
        OWLOntology ontology = owlModel.getOntology();
        OWLDataFactory factory = owlModel.getOWLDataFactory();
        OWLClassExpression subClazz = OWLUtilities.description(subClazzDescription, ontology);
        OWLClassExpression superClazz = OWLUtilities.description(superClazzDescription, ontology);
        OWLAxiom axiom;
        if (clazzType.equals(INCL)) {
            axiom = factory.getOWLSubClassOfAxiom(subClazz, superClazz);
        } else {
            axiom = factory.getOWLEquivalentClassesAxiom(subClazz, superClazz);
        }
        return OWLUtilities.toString(axiom, ontology);
    }

    public static boolean isDataProperty(String propertyUri, String ontologyUri, String projectId) throws NeOnCoreException {
        String expandedURI = OWLModelFactory.getOWLModel(ontologyUri, projectId).getNamespaces().expandString(propertyUri);
        OWLDataProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLDataProperty(OWLUtilities.toIRI(expandedURI));
        Set<OWLDataProperty> properties = OWLModelFactory.getOWLModel(ontologyUri, projectId).getAllDataProperties();
        return properties.contains(prop);
    }

}
