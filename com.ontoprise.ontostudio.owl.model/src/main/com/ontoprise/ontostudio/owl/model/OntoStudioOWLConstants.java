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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OntoStudioOWLConstants {

    public static final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#"; //$NON-NLS-1$
    public static final String OWL_URI = "http://www.w3.org/2002/07/owl#"; //$NON-NLS-1$

    /*
     * RDF Constants
     */
    public static final String RDFS_COMMENT = RDFS_URI + "comment"; //$NON-NLS-1$
    public static final String RDFS_LABEL = RDFS_URI + "label"; //$NON-NLS-1$
    public static final String RDFS_SEE_ALSO = RDFS_URI + "seeAlso"; //$NON-NLS-1$
    public static final String RDFS_IS_DEFINED_BY = RDFS_URI + "isDefinedBy"; //$NON-NLS-1$

    /*
     * OWL Constants
     */
    public static final String OWL_PRIOR_VERSION = OWL_URI + "priorVersion"; //$NON-NLS-1$
    public static final String OWL_VERSION_INFO = OWL_URI + "versionInfo"; //$NON-NLS-1$
    public static final String OWL_DEFAULT_LANGUAGE = OWL_URI + "defaultLanguage"; //$NON-NLS-1$
    public static final String OWL_BACKWARD_COMPATIBLE_WITH = OWL_URI + "backwardCompatibleWith"; //$NON-NLS-1$
    public static final String OWL_INCOMPATIBLE_WITH = OWL_URI + "incompatibleWith"; //$NON-NLS-1$

    public static final Set<String> OWL_STANDARD_ANNOTATION_PROPERTIES = new HashSet<String>(Arrays.asList(new String[] {RDFS_COMMENT, RDFS_LABEL, RDFS_SEE_ALSO, RDFS_IS_DEFINED_BY, OWL_VERSION_INFO}));

    public static final Set<String> OWL_STANDARD_ONTOLOGY_ANNOTATION_PROPERTIES = new HashSet<String>(Arrays.asList(new String[] {RDFS_LABEL, RDFS_SEE_ALSO, RDFS_IS_DEFINED_BY, OWL_PRIOR_VERSION, OWL_BACKWARD_COMPATIBLE_WITH, OWL_INCOMPATIBLE_WITH}));
}
