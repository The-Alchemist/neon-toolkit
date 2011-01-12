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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author krekeler
 *
 */
@SuppressWarnings("nls")
public interface OWLConstants {
    public static final String XSD_DATE_TIME_STAMP = OWLNamespaces.XSD_NS + "dateTimeStamp";
    public static final String XSD_DATE_TIME = OWLNamespaces.XSD_NS + "dateTime";
    public static final String XSD_ANY_URI = OWLNamespaces.XSD_NS + "anyURI";
    public static final String XSD_BASE64_BINARY = OWLNamespaces.XSD_NS + "base64Binary";
    public static final String XSD_HEX_BINARY = OWLNamespaces.XSD_NS + "hexBinary";
    public static final String XSD_BOOLEAN = OWLNamespaces.XSD_NS + "boolean";
    public static final String XSD_NMTOKEN = OWLNamespaces.XSD_NS + "NMTOKEN";
    public static final String XSD_NC_NAME = OWLNamespaces.XSD_NS + "NCName";
    public static final String XSD_NAME = OWLNamespaces.XSD_NS + "Name";
    public static final String XSD_LANGUAGE = OWLNamespaces.XSD_NS + "language";
    public static final String XSD_TOKEN = OWLNamespaces.XSD_NS + "token";
    public static final String XSD_NORMALIZED_STRING = OWLNamespaces.XSD_NS + "normalizedString";
    public static final String XSD_STRING = OWLNamespaces.XSD_NS + "string";
    public static final String XSD_FLOAT = OWLNamespaces.XSD_NS + "float";
    public static final String XSD_DOUBLE = OWLNamespaces.XSD_NS + "double";
    public static final String XSD_UNSIGNED_BYTE = OWLNamespaces.XSD_NS + "unsignedByte";
    public static final String XSD_UNSIGNED_SHORT = OWLNamespaces.XSD_NS + "unsignedShort";
    public static final String XSD_UNSIGNED_INT = OWLNamespaces.XSD_NS + "unsignedInt";
    public static final String XSD_UNSIGNED_LONG = OWLNamespaces.XSD_NS + "unsignedLong";
    public static final String XSD_BYTE = OWLNamespaces.XSD_NS + "byte";
    public static final String XSD_SHORT = OWLNamespaces.XSD_NS + "short";
    public static final String XSD_INT = OWLNamespaces.XSD_NS + "int";
    public static final String XSD_LONG = OWLNamespaces.XSD_NS + "long";
    public static final String XSD_NEGATIVE_INTEGER = OWLNamespaces.XSD_NS + "negativeInteger";
    public static final String XSD_POSITIVE_INTEGER = OWLNamespaces.XSD_NS + "positiveInteger";
    public static final String XSD_NON_POSITIVE_INTEGER = OWLNamespaces.XSD_NS + "nonPositiveInteger";
    public static final String XSD_NON_NEGATIVE_INTEGER = OWLNamespaces.XSD_NS + "nonNegativeInteger";
    public static final String XSD_INTEGER = OWLNamespaces.XSD_NS + "integer";
    public static final String XSD_DECIMAL = OWLNamespaces.XSD_NS + "decimal";
    public static final String RDF_TEXT = OWLNamespaces.RDF_NS + "text";
    public static final String RDF_PLAIN_LITERAL = OWLNamespaces.RDF_NS + "PlainLiteral";
    public static final String RDFS_LITERAL = OWLNamespaces.RDFS_NS + "Literal";
    public static final String RDF_XML_LITERAL = OWLNamespaces.RDFS_NS + "XMLLiteral";
    public static final String OWL_RATIONAL = OWLNamespaces.OWL_NS + "rational";
    public static final String OWL_REAL = OWLNamespaces.OWL_NS + "real";

    final String OWL_THING_URI = OWLNamespaces.OWL_NS + "Thing";
    final String OWL_NOTHING_URI = OWLNamespaces.OWL_NS + "Nothing";
    Set<String> OWL_DATATYPE_URIS = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(
            OWL_REAL,
            OWL_RATIONAL,
            XSD_DECIMAL,
            XSD_INTEGER,
            XSD_NON_NEGATIVE_INTEGER,
            XSD_NON_POSITIVE_INTEGER,
            XSD_POSITIVE_INTEGER,
            XSD_NEGATIVE_INTEGER,
            XSD_LONG,
            XSD_INT,
            XSD_SHORT,
            XSD_BYTE,
            XSD_UNSIGNED_LONG,
            XSD_UNSIGNED_INT,
            XSD_UNSIGNED_SHORT,
            XSD_UNSIGNED_BYTE,
            XSD_DOUBLE,
            XSD_FLOAT,
            XSD_STRING,
            XSD_NORMALIZED_STRING,
            XSD_TOKEN,
            XSD_LANGUAGE,
            XSD_NAME,
            XSD_NC_NAME,
            XSD_NMTOKEN,
            XSD_BOOLEAN,
            XSD_HEX_BINARY,
            XSD_BASE64_BINARY,
            XSD_ANY_URI,
            XSD_DATE_TIME,
            XSD_DATE_TIME_STAMP,
            RDFS_LITERAL,
            RDF_XML_LITERAL,
            RDF_TEXT
            )));
    
    public static final String OWL_EXTENSION = ".owl"; //$NON-NLS-1$
    public static final String OWL2_EXTENSION = ".owl2"; //$NON-NLS-1$
    public static final String RDF_EXTENSION = ".rdf"; //$NON-NLS-1$
    public static final String RDFS_EXTENSION = ".rdfs"; //$NON-NLS-1$
    public static final String OWLXML_EXTENSION = ".owlx"; //$NON-NLS-1$
    public static final String MANCHESTER_SYNTAX_EXTENSION = ".omn"; //$NON-NLS-1$
    public static final String FUNCTIONAL_SYNTAX_EXTENSION = ".owl2"; //$NON-NLS-1$
    public static final String TURTLE_EXTENSION = ".ttl"; //$NON-NLS-1$
}
