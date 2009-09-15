/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assume;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.StringInputSource;
import org.semanticweb.owlapi.io.StringOutputTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLStringLiteral;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTypedLiteral;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLIndividualVariable;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLLiteralVariable;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;


/**
 * @author krekeler
 *
 */
@SuppressWarnings("nls")
@RunWith(Theories.class)
public class InternalParserTest {
    private static final OWLDataFactory _f = OWLManager.createOWLOntologyManager().getOWLDataFactory();
    
    private static URI uri(String namespace, String localPart) {
        try {
            return new URI(namespace + localPart);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private static URI uri(String localPart) {
        return uri("http://test.org/ontology#", localPart);
    }
    private static IRI iri(String localPart) {
        return _f.getIRI(uri(localPart));
    }
    private static URI xsdURI(String localPart) {
        return uri(Namespaces.XSD.toString(), localPart);
    }
    
    private static <E> Set<E> asSet(E...items) {
        return new LinkedHashSet<E>(Arrays.asList(items));
    }

    private String getFieldName(Object staticObject) throws IllegalAccessException {
        for (Field f: getClass().getFields()) {
            if (f.get(null) == staticObject) {
                return f.getName();
            }
        }
        return null;
    }

    @DataPoint public static final OWLDatatype xsdString = _f.getOWLDatatype(xsdURI("string"));
    @DataPoint public static final OWLDatatype xsdBoolean = _f.getOWLDatatype(xsdURI("boolean"));
    @DataPoint public static final OWLDatatype xsdDecimal = _f.getOWLDatatype(xsdURI("decimal"));
    @DataPoint public static final OWLDatatype xsdFloat= _f.getOWLDatatype(xsdURI("float"));
    @DataPoint public static final OWLDatatype xsdDouble = _f.getOWLDatatype(xsdURI("double"));
    @DataPoint public static final OWLDatatype xsdDuration = _f.getOWLDatatype(xsdURI("duration"));
    @DataPoint public static final OWLDatatype xsdDateTime = _f.getOWLDatatype(xsdURI("dateTime"));
    @DataPoint public static final OWLDatatype xsdTime = _f.getOWLDatatype(xsdURI("time"));
    @DataPoint public static final OWLDatatype xsdDate = _f.getOWLDatatype(xsdURI("date"));
    @DataPoint public static final OWLDatatype xsdGYearMonth = _f.getOWLDatatype(xsdURI("gYearMonth"));
    @DataPoint public static final OWLDatatype xsdGYear = _f.getOWLDatatype(xsdURI("gYear"));
    @DataPoint public static final OWLDatatype xsdGMonthDay = _f.getOWLDatatype(xsdURI("gMonthDay"));
    @DataPoint public static final OWLDatatype xsdGDay = _f.getOWLDatatype(xsdURI("gDay"));
    @DataPoint public static final OWLDatatype xsdGMonth = _f.getOWLDatatype(xsdURI("gMonth"));
    @DataPoint public static final OWLDatatype xsdHexBinary = _f.getOWLDatatype(xsdURI("hexBinary"));
    @DataPoint public static final OWLDatatype xsdBase64Binary = _f.getOWLDatatype(xsdURI("base64Binary"));
    @DataPoint public static final OWLDatatype xsdAnyURI = _f.getOWLDatatype(xsdURI("anyURI"));
    @DataPoint public static final OWLDatatype xsdQName = _f.getOWLDatatype(xsdURI("QName"));
    @DataPoint public static final OWLDatatype xsdNOTATION = _f.getOWLDatatype(xsdURI("NOTATION"));
    @DataPoint public static final OWLDatatype xsdNormalizedString = _f.getOWLDatatype(xsdURI("normalizedString"));
    @DataPoint public static final OWLDatatype xsdToken = _f.getOWLDatatype(xsdURI("token"));
    @DataPoint public static final OWLDatatype xsdLanguage = _f.getOWLDatatype(xsdURI("language"));
    @DataPoint public static final OWLDatatype xsdNMTOKEN = _f.getOWLDatatype(xsdURI("NMTOKEN"));
    @DataPoint public static final OWLDatatype xsdNMTOKENS = _f.getOWLDatatype(xsdURI("NMTOKENS"));
    @DataPoint public static final OWLDatatype xsdName = _f.getOWLDatatype(xsdURI("Name"));
    @DataPoint public static final OWLDatatype xsdNCName = _f.getOWLDatatype(xsdURI("NCName"));
    @DataPoint public static final OWLDatatype xsdID = _f.getOWLDatatype(xsdURI("ID"));
    @DataPoint public static final OWLDatatype xsdIDREF = _f.getOWLDatatype(xsdURI("IDREF"));
    @DataPoint public static final OWLDatatype xsdIDREFS = _f.getOWLDatatype(xsdURI("IDREFS"));
    @DataPoint public static final OWLDatatype xsdENTITY = _f.getOWLDatatype(xsdURI("ENTITY"));
    @DataPoint public static final OWLDatatype xsdENTITIES = _f.getOWLDatatype(xsdURI("ENTITIES"));
    @DataPoint public static final OWLDatatype xsdInteger = _f.getOWLDatatype(xsdURI("integer"));
    @DataPoint public static final OWLDatatype xsdNonPositiveInteger = _f.getOWLDatatype(xsdURI("nonPositiveInteger"));
    @DataPoint public static final OWLDatatype xsdNegativeInteger = _f.getOWLDatatype(xsdURI("negativeInteger"));
    @DataPoint public static final OWLDatatype xsdLong = _f.getOWLDatatype(xsdURI("long"));
    @DataPoint public static final OWLDatatype xsdInt = _f.getOWLDatatype(xsdURI("int"));
    @DataPoint public static final OWLDatatype xsdShort = _f.getOWLDatatype(xsdURI("short"));
    @DataPoint public static final OWLDatatype xsdByte = _f.getOWLDatatype(xsdURI("byte"));
    @DataPoint public static final OWLDatatype xsdNonNegativeInteger = _f.getOWLDatatype(xsdURI("nonNegativeInteger"));
    @DataPoint public static final OWLDatatype xsdUnsignedLong = _f.getOWLDatatype(xsdURI("unsignedLong"));
    @DataPoint public static final OWLDatatype xsdUnsignedInt = _f.getOWLDatatype(xsdURI("unsignedInt"));
    @DataPoint public static final OWLDatatype xsdUnsignedShort = _f.getOWLDatatype(xsdURI("unsignedShort"));
    @DataPoint public static final OWLDatatype xsdUnsignedByte = _f.getOWLDatatype(xsdURI("unsignedByte"));
    @DataPoint public static final OWLDatatype xsdPositiveInteger = _f.getOWLDatatype(xsdURI("positiveInteger"));
    
    // OWLConstants
    @DataPoint public static final OWLStringLiteral untypedConstant0 = _f.getOWLStringLiteral("lexical value", "en");
    @DataPoint public static final OWLStringLiteral untypedConstant1 = _f.getOWLStringLiteral("emtpy language", "");
    @DataPoint public static final OWLStringLiteral untypedConstant2 = _f.getOWLStringLiteral("null language", null);
    @DataPoint public static final OWLTypedLiteral typedConstant0 = _f.getOWLTypedLiteral("lexical value");
    @DataPoint public static final OWLTypedLiteral typedConstant1 = _f.getOWLTypedLiteral(true);
    @DataPoint public static final OWLTypedLiteral typedConstant2 = _f.getOWLTypedLiteral(false);
    @DataPoint public static final OWLTypedLiteral typedConstant3 = _f.getOWLTypedLiteral(0d);
    @DataPoint public static final OWLTypedLiteral typedConstant4 = _f.getOWLTypedLiteral(0f);
    @DataPoint public static final OWLTypedLiteral typedConstant5 = _f.getOWLTypedLiteral("" + Integer.MIN_VALUE, xsdInt);
    @DataPoint public static final OWLTypedLiteral typedConstant6 = _f.getOWLTypedLiteral("" + (Short.MIN_VALUE - 1), xsdInt);
    @DataPoint public static final OWLTypedLiteral typedConstant7 = _f.getOWLTypedLiteral("" + Short.MIN_VALUE, xsdShort);
    @DataPoint public static final OWLTypedLiteral typedConstant8 = _f.getOWLTypedLiteral("" + (Byte.MIN_VALUE - 1), xsdShort);
    @DataPoint public static final OWLTypedLiteral typedConstant9 = _f.getOWLTypedLiteral("" + Byte.MIN_VALUE, xsdByte);
    @DataPoint public static final OWLTypedLiteral typedConstant10 = _f.getOWLTypedLiteral("" + -1, xsdByte);
    @DataPoint public static final OWLTypedLiteral typedConstant11 = _f.getOWLTypedLiteral("" + 0, xsdUnsignedByte);
    @DataPoint public static final OWLTypedLiteral typedConstant12 = _f.getOWLTypedLiteral("" + Byte.MAX_VALUE, xsdUnsignedByte);
    @DataPoint public static final OWLTypedLiteral typedConstant13 = _f.getOWLTypedLiteral("" + (Byte.MAX_VALUE + 1), xsdUnsignedShort);
    @DataPoint public static final OWLTypedLiteral typedConstant14 = _f.getOWLTypedLiteral("" + Short.MAX_VALUE, xsdUnsignedShort);
    @DataPoint public static final OWLTypedLiteral typedConstant15 = _f.getOWLTypedLiteral("" + (Short.MAX_VALUE + 1), xsdUnsignedInt);
    @DataPoint public static final OWLTypedLiteral typedConstant16 = _f.getOWLTypedLiteral("" + Integer.MAX_VALUE, xsdUnsignedInt);
    
    
    @DataPoint public static final OWLAnnotation constantAnnotation0 = _f.getOWLAnnotation(_f.getOWLAnnotationProperty(uri("constantAnnotation")), untypedConstant0);
    @DataPoint public static final OWLAnnotation constantAnnotation1 = _f.getOWLAnnotation(_f.getOWLAnnotationProperty(uri("constantAnnotation")), typedConstant0);
    @DataPoint public static final OWLAnnotation objectAnnotation0 = _f.getOWLAnnotation(_f.getOWLAnnotationProperty(uri("objectAnntotation")), _f.getIRI(uri("iri")));
    @DataPoint public static final OWLAnnotationProperty annotationProperty0 = _f.getOWLAnnotationProperty(uri("annotationProperty0"));
    @DataPoint public static final OWLAnnotationProperty annotationProperty1 = _f.getOWLAnnotationProperty(uri("annotationProperty1"));

    @DataPoint public static final OWLNamedIndividual individual0 = _f.getOWLNamedIndividual(uri("individual0"));
    @DataPoint public static final OWLNamedIndividual individual1 = _f.getOWLNamedIndividual(uri("individual1"));
    @DataPoint public static final OWLNamedIndividual individual2 = _f.getOWLNamedIndividual(uri("individual2"));
    @DataPoint public static final OWLAnonymousIndividual anonymousIndividual0 = _f.getOWLAnonymousIndividual("anonymousIndividual0");
    @DataPoint public static final OWLAnonymousIndividual anonymousIndividual1 = _f.getOWLAnonymousIndividual("anonymousIndividual1");
    @DataPoint public static final OWLDataProperty dataProperty0 = _f.getOWLDataProperty(uri("dataProperty0"));
    @DataPoint public static final OWLDataProperty dataProperty1 = _f.getOWLDataProperty(uri("dataProperty1"));
    @DataPoint public static final OWLDataProperty dataProperty2 = _f.getOWLDataProperty(uri("dataProperty2"));
    @DataPoint public static final OWLObjectProperty objectProperty0 = _f.getOWLObjectProperty(uri("objectProperty0"));
    @DataPoint public static final OWLObjectProperty objectProperty1 = _f.getOWLObjectProperty(uri("objectProperty1"));
    @DataPoint public static final OWLObjectProperty objectProperty2 = _f.getOWLObjectProperty(uri("objectProperty2"));
    @DataPoint public static final OWLObjectProperty objectProperty3 = _f.getOWLObjectProperty(uri("objectProperty3"));
    @DataPoint public static final OWLObjectInverseOf objectPropertyInverse0 = _f.getOWLObjectInverseOf(objectProperty0);
    
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction0 = _f.getOWLFacetRestriction(OWLFacet.FRACTION_DIGITS, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction1 = _f.getOWLFacetRestriction(OWLFacet.LENGTH, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction2 = _f.getOWLFacetRestriction(OWLFacet.MAX_EXCLUSIVE, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction3 = _f.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction4 = _f.getOWLFacetRestriction(OWLFacet.MAX_LENGTH, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction5 = _f.getOWLFacetRestriction(OWLFacet.MIN_EXCLUSIVE, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction6 = _f.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction7 = _f.getOWLFacetRestriction(OWLFacet.MIN_LENGTH, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction8 = _f.getOWLFacetRestriction(OWLFacet.PATTERN, 1f);
    @DataPoint public static final OWLFacetRestriction dataRangeFacetRestriction9 = _f.getOWLFacetRestriction(OWLFacet.TOTAL_DIGITS, 1f);
    
    
    // OWLDataRanges
    @DataPoint public static final OWLDataComplementOf dataComplementOf0 = _f.getOWLDataComplementOf(xsdInt);
    @DataPoint public static final OWLDataOneOf dataOneOf0 = _f.getOWLDataOneOf(untypedConstant0);
    @DataPoint public static final OWLDataOneOf dataOneOf1 = _f.getOWLDataOneOf(untypedConstant0, untypedConstant1);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction0 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction0);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction1 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction1);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction2 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction2);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction3 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction3);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction4 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction4);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction5 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction5);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction6 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction6);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction7 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction7);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction8 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction8);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction9 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction9);
    @DataPoint public static final OWLDatatypeRestriction dataRangeRestriction10 = _f.getOWLDatatypeRestriction(xsdString, dataRangeFacetRestriction0, dataRangeFacetRestriction1);
    @DataPoint public static final OWLDataUnionOf dataUnionOf0 = _f.getOWLDataUnionOf(dataRangeRestriction0, dataRangeRestriction1);
    @DataPoint public static final OWLDataUnionOf dataUnionOf1 = _f.getOWLDataUnionOf(dataRangeRestriction0, dataRangeRestriction10);
    @DataPoint public static final OWLDataIntersectionOf dataIntersectionOf0 = _f.getOWLDataIntersectionOf(dataRangeRestriction0, dataRangeRestriction1);
    @DataPoint public static final OWLDataIntersectionOf dataIntersectionOf1 = _f.getOWLDataIntersectionOf(dataRangeRestriction0, dataRangeRestriction10);
    
    // OWLDescriptions
    @DataPoint public static final OWLClass owlClass0 = _f.getOWLClass(uri("class0"));
    @DataPoint public static final OWLClass owlClass1 = _f.getOWLClass(uri("class1"));
    @DataPoint public static final OWLClass owlClass2 = _f.getOWLClass(uri("class2"));
    @DataPoint public static final OWLClass owlClass3 = _f.getOWLClass(uri("class3"));
    @DataPoint public static final OWLObjectIntersectionOf objectIntersectionOf0 = _f.getOWLObjectIntersectionOf(owlClass0, owlClass1);
    @DataPoint public static final OWLObjectIntersectionOf objectIntersectionOf1 = _f.getOWLObjectIntersectionOf(owlClass0, owlClass1, owlClass2);
    @DataPoint public static final OWLObjectUnionOf objectUnionOf0 = _f.getOWLObjectUnionOf(owlClass0, owlClass1);
    @DataPoint public static final OWLObjectUnionOf objectUnionOf1 = _f.getOWLObjectUnionOf(owlClass0, owlClass1, owlClass2);
    @DataPoint public static final OWLObjectComplementOf objectComplementOf0 = _f.getOWLObjectComplementOf(owlClass0);
    @DataPoint public static final OWLObjectOneOf objectOneOf0 = _f.getOWLObjectOneOf(individual0, individual1);
    @DataPoint public static final OWLObjectOneOf objectOneOf1 = _f.getOWLObjectOneOf(individual0, individual1, individual2);
    @DataPoint public static final OWLObjectOneOf objectOneOf2 = _f.getOWLObjectOneOf(anonymousIndividual0, anonymousIndividual0);
    @DataPoint public static final OWLObjectOneOf objectOneOf3 = _f.getOWLObjectOneOf(anonymousIndividual0, anonymousIndividual1, individual2);
    @DataPoint public static final OWLObjectHasSelf objectSelfRestriction0 = _f.getOWLObjectHasSelf(objectProperty0);
    @DataPoint public static final OWLObjectHasSelf objectSelfRestriction1 = _f.getOWLObjectHasSelf(objectPropertyInverse0);
    @DataPoint public static final OWLDataExactCardinality dataExactCardinalityRestriction0 = _f.getOWLDataExactCardinality(1, dataProperty0);
    @DataPoint public static final OWLDataExactCardinality dataExactCardinalityRestriction1 = _f.getOWLDataExactCardinality(1, dataProperty0, xsdInt);
    @DataPoint public static final OWLDataMaxCardinality dataMaxCardinalityRestriction0 = _f.getOWLDataMaxCardinality(1, dataProperty0);
    @DataPoint public static final OWLDataMinCardinality dataMinCardinalityRestriction0 = _f.getOWLDataMinCardinality(1, dataProperty0);
    @DataPoint public static final OWLObjectExactCardinality objectExactCardinalityRestriction0 = _f.getOWLObjectExactCardinality(1, objectProperty0);
    @DataPoint public static final OWLObjectExactCardinality objectExactCardinalityRestriction1 = _f.getOWLObjectExactCardinality(1, objectProperty0, owlClass0);
    @DataPoint public static final OWLObjectMaxCardinality objectMaxCardinalityRestriction0 = _f.getOWLObjectMaxCardinality(1, objectProperty0);
    @DataPoint public static final OWLObjectMinCardinality objectMinCardinalityRestriction0 = _f.getOWLObjectMinCardinality(1, objectProperty0);
    @DataPoint public static final OWLDataAllValuesFrom dataAllRestriction0 = _f.getOWLDataAllValuesFrom(dataProperty0, xsdInt);
    @DataPoint public static final OWLDataSomeValuesFrom dataSomeRestriction0 = _f.getOWLDataSomeValuesFrom(dataProperty0, xsdInt);
    @DataPoint public static final OWLObjectAllValuesFrom objectAllRestriction0 = _f.getOWLObjectAllValuesFrom(objectProperty0, owlClass0);
    @DataPoint public static final OWLObjectSomeValuesFrom objectSomeRestriction0 = _f.getOWLObjectSomeValuesFrom(objectProperty0, owlClass0);
    
    // SWRLAtomObjects
    @DataPoint public static final SWRLLiteralArgument atomConstantObject0 = _f.getSWRLLiteralArgument(untypedConstant0);
    @DataPoint public static final SWRLLiteralArgument atomConstantObject1 = _f.getSWRLLiteralArgument(typedConstant0);
    @DataPoint public static final SWRLLiteralArgument atomConstantObject2 = _f.getSWRLLiteralArgument(typedConstant1);
    @DataPoint public static final SWRLLiteralVariable atomDVariable0 = _f.getSWRLLiteralVariable(iri("atomDVariable0"));
    @DataPoint public static final SWRLIndividualArgument atomIndividualObject0 = _f.getSWRLIndividualArgument(individual0);
    @DataPoint public static final SWRLIndividualArgument atomIndividualObject1 = _f.getSWRLIndividualArgument(individual1);
    @DataPoint public static final SWRLIndividualArgument atomIndividualObject2 = _f.getSWRLIndividualArgument(individual2);
    @DataPoint public static final SWRLIndividualArgument atomIndividualObject3 = _f.getSWRLIndividualArgument(anonymousIndividual0);
    @DataPoint public static final SWRLIndividualVariable atomIVariable0 = _f.getSWRLIndividualVariable(iri("atomIVariable0"));
    
    // SWRLAtoms
    @DataPoint public static final SWRLDataPropertyAtom dataValuedPropertyAtom0 = _f.getSWRLDataPropertyAtom(dataProperty0, atomIndividualObject0, atomConstantObject0);
    @DataPoint public static final SWRLDataPropertyAtom dataValuedPropertyAtom1 = _f.getSWRLDataPropertyAtom(dataProperty0, atomIVariable0, atomConstantObject0);
    @DataPoint public static final SWRLDataPropertyAtom dataValuedPropertyAtom2 = _f.getSWRLDataPropertyAtom(dataProperty0, atomIndividualObject0, atomDVariable0);
    @DataPoint public static final SWRLDifferentIndividualsAtom differentFromAtom0 = _f.getSWRLDifferentFromAtom(atomIndividualObject0, atomIndividualObject1);
    @DataPoint public static final SWRLDifferentIndividualsAtom differentFromAtom1 = _f.getSWRLDifferentFromAtom(atomIVariable0, atomIndividualObject1);
    @DataPoint public static final SWRLDifferentIndividualsAtom differentFromAtom2 = _f.getSWRLDifferentFromAtom(atomIndividualObject0, atomIVariable0);
    @DataPoint public static final SWRLObjectPropertyAtom objectPropertyAtom0 = _f.getSWRLObjectPropertyAtom(objectProperty0, atomIndividualObject0, atomIndividualObject0);
    @DataPoint public static final SWRLObjectPropertyAtom objectPropertyAtom1 = _f.getSWRLObjectPropertyAtom(objectProperty0, atomIVariable0, atomIndividualObject0);
    @DataPoint public static final SWRLObjectPropertyAtom objectPropertyAtom2 = _f.getSWRLObjectPropertyAtom(objectProperty0, atomIndividualObject0, atomIVariable0);
    @DataPoint public static final SWRLSameIndividualAtom sameAsAtom0 = _f.getSWRLSameAsAtom(atomIndividualObject0, atomIndividualObject1);
    @DataPoint public static final SWRLSameIndividualAtom sameAsAtom1 = _f.getSWRLSameAsAtom(atomIVariable0, atomIndividualObject1);
    @DataPoint public static final SWRLSameIndividualAtom sameAsAtom2 = _f.getSWRLSameAsAtom(atomIndividualObject0, atomIVariable0);
    @DataPoint public static final SWRLClassAtom classAtom0 = _f.getSWRLClassAtom(owlClass0, atomIndividualObject0);
    @DataPoint public static final SWRLClassAtom classAtom1 = _f.getSWRLClassAtom(owlClass0, atomIVariable0);
    @DataPoint public static final SWRLDataRangeAtom dataRangeAtom0 = _f.getSWRLDataRangeAtom(xsdString, atomConstantObject1);
    @DataPoint public static final SWRLDataRangeAtom dataRangeAtom1 = _f.getSWRLDataRangeAtom(xsdString, atomDVariable0);
    @DataPoint public static final SWRLBuiltInAtom builtInAtom0 = _f.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.DATE.getIRI(), Arrays.asList((SWRLDArgument)atomConstantObject0, (SWRLDArgument)atomConstantObject1));
    
    // OWLLogicalAxioms
    @DataPoint public static final SWRLRule swrlRule0 = _f.getSWRLRule(iri("swrlRule0"), Collections.singleton(dataValuedPropertyAtom0), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule1 = _f.getSWRLRule(iri("swrlRule1"), Collections.singleton(dataValuedPropertyAtom1), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule2 = _f.getSWRLRule(iri("swrlRule2"), Collections.singleton(dataValuedPropertyAtom2), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule3 = _f.getSWRLRule(iri("swrlRule3"), Collections.singleton(differentFromAtom0), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule4 = _f.getSWRLRule(iri("swrlRule4"), Collections.singleton(differentFromAtom1), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule5 = _f.getSWRLRule(iri("swrlRule5"), Collections.singleton(differentFromAtom2), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule6 = _f.getSWRLRule(iri("swrlRule6"), Collections.singleton(objectPropertyAtom0), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule7 = _f.getSWRLRule(iri("swrlRule7"), Collections.singleton(objectPropertyAtom1), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule8 = _f.getSWRLRule(iri("swrlRule8"), Collections.singleton(objectPropertyAtom2), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule9 = _f.getSWRLRule(iri("swrlRule9"), Collections.singleton(sameAsAtom0), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule10 = _f.getSWRLRule(iri("swrlRule10"), Collections.singleton(sameAsAtom1), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule11 = _f.getSWRLRule(iri("swrlRule11"), Collections.singleton(sameAsAtom2), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule12 = _f.getSWRLRule(iri("swrlRule12"), Collections.singleton(classAtom0), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule13 = _f.getSWRLRule(iri("swrlRule13"), Collections.singleton(classAtom1), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule14 = _f.getSWRLRule(iri("swrlRule14"), Collections.singleton(dataRangeAtom0), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final SWRLRule swrlRule15 = _f.getSWRLRule(iri("swrlRule15"), Collections.singleton(dataRangeAtom1), Collections.singleton(dataValuedPropertyAtom0));
    @SuppressWarnings("unchecked")
    @DataPoint public static final SWRLRule swrlRule16 = _f.getSWRLRule(iri("swrlRule16"), asSet(dataValuedPropertyAtom0, differentFromAtom0), asSet(dataValuedPropertyAtom0));
    @SuppressWarnings("unchecked")
    @DataPoint public static final SWRLRule swrlRule17 = _f.getSWRLRule(iri("swrlRule17"), Collections.singleton(dataValuedPropertyAtom0), asSet(dataValuedPropertyAtom0, differentFromAtom0));
//    XXX equals from a SWRLRule with a SWRLBuiltInAtom never ends
//    @DataPoint public static final SWRLRule swrlRule18 = _f.getSWRLRule(uri("swrlRule18"), Collections.singleton(builtInAtom0), Collections.singleton(dataValuedPropertyAtom0));
    @DataPoint public static final OWLDisjointClassesAxiom disjointClassesAxiom0 = _f.getOWLDisjointClassesAxiom(owlClass0, owlClass1);
    @DataPoint public static final OWLDisjointClassesAxiom disjointClassesAxiom1 = _f.getOWLDisjointClassesAxiom(asSet(owlClass0, owlClass1, owlClass2));
    @DataPoint public static final OWLDisjointUnionAxiom disjointUnionAxiom0 = _f.getOWLDisjointUnionAxiom(owlClass0, asSet(owlClass1, owlClass2));
    @DataPoint public static final OWLDisjointUnionAxiom disjointUnionAxiom1 = _f.getOWLDisjointUnionAxiom(owlClass0, asSet(owlClass1, owlClass2, owlClass3));
    @DataPoint public static final OWLEquivalentClassesAxiom equivalentClassesAxiom0 = _f.getOWLEquivalentClassesAxiom(owlClass0, owlClass1);
    @DataPoint public static final OWLEquivalentClassesAxiom equivalentClassesAxiom1 = _f.getOWLEquivalentClassesAxiom(asSet(owlClass0, owlClass1, owlClass2));
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom0 = _f.getOWLSubClassOfAxiom(owlClass0, owlClass1);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom1 = _f.getOWLSubClassOfAxiom(objectIntersectionOf0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom2 = _f.getOWLSubClassOfAxiom(objectIntersectionOf1, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom3 = _f.getOWLSubClassOfAxiom(objectUnionOf0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom4 = _f.getOWLSubClassOfAxiom(objectUnionOf1, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom5 = _f.getOWLSubClassOfAxiom(objectComplementOf0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom6 = _f.getOWLSubClassOfAxiom(objectOneOf0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom7 = _f.getOWLSubClassOfAxiom(objectOneOf1, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom8 = _f.getOWLSubClassOfAxiom(objectSelfRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom9 = _f.getOWLSubClassOfAxiom(objectSelfRestriction1, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom10 = _f.getOWLSubClassOfAxiom(dataExactCardinalityRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom11 = _f.getOWLSubClassOfAxiom(dataExactCardinalityRestriction1, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom12 = _f.getOWLSubClassOfAxiom(dataMaxCardinalityRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom13 = _f.getOWLSubClassOfAxiom(dataMinCardinalityRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom14 = _f.getOWLSubClassOfAxiom(objectExactCardinalityRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom15 = _f.getOWLSubClassOfAxiom(objectExactCardinalityRestriction1, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom16 = _f.getOWLSubClassOfAxiom(objectMaxCardinalityRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom17 = _f.getOWLSubClassOfAxiom(objectMinCardinalityRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom18 = _f.getOWLSubClassOfAxiom(dataAllRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom19 = _f.getOWLSubClassOfAxiom(dataSomeRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom20 = _f.getOWLSubClassOfAxiom(objectAllRestriction0, owlClass0);
    @DataPoint public static final OWLSubClassOfAxiom subClassAxiom21 = _f.getOWLSubClassOfAxiom(objectSomeRestriction0, owlClass0);
    @DataPoint public static final OWLFunctionalDataPropertyAxiom functionalDataPropertyAxiom0 = _f.getOWLFunctionalDataPropertyAxiom(dataProperty0);
    @DataPoint public static final OWLDataPropertyDomainAxiom dataPropertyDomainAxiom0 = _f.getOWLDataPropertyDomainAxiom(dataProperty0, owlClass0);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom0 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, xsdInt);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom1 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataComplementOf0);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom2 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataOneOf0);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom3 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataOneOf1);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom4 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction0);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom5 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction1);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom6 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction2);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom7 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction3);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom8 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction4);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom9 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction5);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom10 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction6);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom11 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction7);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom12 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction8);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom13 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction9);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom14 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataRangeRestriction10);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom15 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataUnionOf0);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom16 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataUnionOf1);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom17 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataIntersectionOf0);
    @DataPoint public static final OWLDataPropertyRangeAxiom dataPropertyRangeAxiom18 = _f.getOWLDataPropertyRangeAxiom(dataProperty0, dataIntersectionOf1);
    @DataPoint public static final OWLSubDataPropertyOfAxiom dataSubPropertyAxiom0 = _f.getOWLSubDataPropertyOfAxiom(dataProperty0, dataProperty1);
    @DataPoint public static final OWLDisjointDataPropertiesAxiom disjointDataPropertiesAxiom0 = _f.getOWLDisjointDataPropertiesAxiom(asSet(dataProperty0, dataProperty1));
    @DataPoint public static final OWLDisjointDataPropertiesAxiom disjointDataPropertiesAxiom1 = _f.getOWLDisjointDataPropertiesAxiom(asSet(dataProperty0, dataProperty1, dataProperty2));
    @DataPoint public static final OWLEquivalentDataPropertiesAxiom equivalentDataPropertiesAxiom0 = _f.getOWLEquivalentDataPropertiesAxiom(asSet(dataProperty0, dataProperty1));
    @DataPoint public static final OWLEquivalentDataPropertiesAxiom equivalentDataPropertiesAxiom1 = _f.getOWLEquivalentDataPropertiesAxiom(asSet(dataProperty0, dataProperty1, dataProperty2));
    @DataPoint public static final OWLClassAssertionAxiom classAssertionAxiom0 = _f.getOWLClassAssertionAxiom(owlClass0, individual0);
    @DataPoint public static final OWLClassAssertionAxiom classAssertionAxiom1 = _f.getOWLClassAssertionAxiom(owlClass0, anonymousIndividual0);
    @DataPoint public static final OWLDifferentIndividualsAxiom differentIndividualsAxiom0 = _f.getOWLDifferentIndividualsAxiom(individual0, individual1);
    @DataPoint public static final OWLDifferentIndividualsAxiom differentIndividualsAxiom1 = _f.getOWLDifferentIndividualsAxiom(anonymousIndividual0, anonymousIndividual1);
    @DataPoint public static final OWLDifferentIndividualsAxiom differentIndividualsAxiom2 = _f.getOWLDifferentIndividualsAxiom(individual0, individual1, individual2);
    @DataPoint public static final OWLDifferentIndividualsAxiom differentIndividualsAxiom3 = _f.getOWLDifferentIndividualsAxiom(individual0, anonymousIndividual0, anonymousIndividual1);
    @DataPoint public static final OWLSameIndividualAxiom sameIndividualsAxiom0 = _f.getOWLSameIndividualAxiom(asSet(individual0, individual1));
    @DataPoint public static final OWLSameIndividualAxiom sameIndividualsAxiom1 = _f.getOWLSameIndividualAxiom(asSet(individual0, individual1, individual2));
    @DataPoint public static final OWLSameIndividualAxiom sameIndividualsAxiom2 = _f.getOWLSameIndividualAxiom(asSet(anonymousIndividual0, anonymousIndividual1));
    @DataPoint public static final OWLSameIndividualAxiom sameIndividualsAxiom3 = _f.getOWLSameIndividualAxiom(individual0, anonymousIndividual0, anonymousIndividual1);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom0 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, untypedConstant0);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom0u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, untypedConstant0);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom1 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, untypedConstant1);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom1u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, untypedConstant1);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom2a = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, untypedConstant2);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom2au = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, untypedConstant2);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom3 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant0);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom3u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant0);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom4 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant1);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom4u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant1);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom5 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant2);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom5u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant2);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom6 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant3);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom6u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant3);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom7 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant4);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom7u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant4);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom8 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant5);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom8u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant5);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom9 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant6);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom9u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant6);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom10 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant7);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom10u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant7);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom11 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant8);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom11u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant8);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom12 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant9);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom12u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant9);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom13 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant10);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom13u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant10);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom14 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant11);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom14u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant11);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom15 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant12);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom15u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant12);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom16 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant13);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom16u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant13);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom17 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant14);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom17u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant14);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom18 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant15);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom18u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant15);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom19 = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, individual0, typedConstant16);
    @DataPoint public static final OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom19u = _f.getOWLDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, typedConstant16);
    @DataPoint public static final OWLNegativeDataPropertyAssertionAxiom negativeDataPropertyAssertionAxiom0 = _f.getOWLNegativeDataPropertyAssertionAxiom(dataProperty0, individual0, untypedConstant0);
    @DataPoint public static final OWLNegativeDataPropertyAssertionAxiom negativeDataPropertyAssertionAxiom0u = _f.getOWLNegativeDataPropertyAssertionAxiom(dataProperty0, anonymousIndividual0, untypedConstant0);
    @DataPoint public static final OWLNegativeObjectPropertyAssertionAxiom negativeObjectPropertyAssertionAxiom0 = _f.getOWLNegativeObjectPropertyAssertionAxiom(objectProperty0, individual0, individual1);
    @DataPoint public static final OWLNegativeObjectPropertyAssertionAxiom negativeObjectPropertyAssertionAxiom0u = _f.getOWLNegativeObjectPropertyAssertionAxiom(objectProperty0, anonymousIndividual0, individual1);
    @DataPoint public static final OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom0 = _f.getOWLObjectPropertyAssertionAxiom(objectProperty0, individual0, individual1);
    @DataPoint public static final OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom0u = _f.getOWLObjectPropertyAssertionAxiom(objectProperty0, anonymousIndividual0, individual1);
    @DataPoint public static final OWLDisjointObjectPropertiesAxiom disjointObjectPropertiesAxiom0 = _f.getOWLDisjointObjectPropertiesAxiom(asSet(objectProperty0, objectProperty1));
    @DataPoint public static final OWLDisjointObjectPropertiesAxiom disjointObjectPropertiesAxiom1 = _f.getOWLDisjointObjectPropertiesAxiom(asSet(objectProperty0, objectProperty1, objectProperty2));
    @DataPoint public static final OWLEquivalentObjectPropertiesAxiom equivalentObjectPropertiesAxiom0 = _f.getOWLEquivalentObjectPropertiesAxiom(asSet(objectProperty0, objectProperty1));
    @DataPoint public static final OWLEquivalentObjectPropertiesAxiom equivalentObjectPropertiesAxiom1 = _f.getOWLEquivalentObjectPropertiesAxiom(asSet(objectProperty0, objectProperty1, objectProperty2));
    @DataPoint public static final OWLInverseObjectPropertiesAxiom inverseObjectPropertiesAxiom0 = _f.getOWLInverseObjectPropertiesAxiom(objectProperty0, objectProperty1);
    @DataPoint public static final OWLSubPropertyChainOfAxiom objectPropertyChainSubPropertyAxiom0 = _f.getOWLSubPropertyChainOfAxiom(Arrays.asList(objectProperty0, objectProperty1), objectProperty2);
    @DataPoint public static final OWLSubPropertyChainOfAxiom objectPropertyChainSubPropertyAxiom1 = _f.getOWLSubPropertyChainOfAxiom(Arrays.asList(objectProperty0, objectProperty1, objectProperty2), objectProperty3);
    @DataPoint public static final OWLAsymmetricObjectPropertyAxiom antiSymmetricObjectPropertyAxiom0 = _f.getOWLAsymmetricObjectPropertyAxiom(objectProperty0);
    @DataPoint public static final OWLFunctionalObjectPropertyAxiom functionalObjectPropertyAxiom0 = _f.getOWLFunctionalObjectPropertyAxiom(objectProperty0);
    @DataPoint public static final OWLInverseFunctionalObjectPropertyAxiom inverseFunctionalObjectPropertyAxiom0 = _f.getOWLInverseFunctionalObjectPropertyAxiom(objectProperty0);
    @DataPoint public static final OWLIrreflexiveObjectPropertyAxiom irreflexiveObjectPropertyAxiom0 = _f.getOWLIrreflexiveObjectPropertyAxiom(objectProperty0);
    @DataPoint public static final OWLReflexiveObjectPropertyAxiom reflexiveObjectPropertyAxiom0 = _f.getOWLReflexiveObjectPropertyAxiom(objectProperty0);
    @DataPoint public static final OWLSymmetricObjectPropertyAxiom symmetricObjectPropertyAxiom0 = _f.getOWLSymmetricObjectPropertyAxiom(objectProperty0);
    @DataPoint public static final OWLTransitiveObjectPropertyAxiom transitiveObjectPropertyAxiom0 = _f.getOWLTransitiveObjectPropertyAxiom(objectProperty0);
    @DataPoint public static final OWLObjectPropertyDomainAxiom objectPropertyDomainAxiom0 = _f.getOWLObjectPropertyDomainAxiom(objectProperty0, owlClass0);
    @DataPoint public static final OWLObjectPropertyRangeAxiom objectPropertyRangeAxiom = _f.getOWLObjectPropertyRangeAxiom(objectProperty0, owlClass0);
    @DataPoint public static final OWLSubObjectPropertyOfAxiom objectSubPropertyAxiom = _f.getOWLSubObjectPropertyOfAxiom(objectProperty0, objectProperty1);
    
    // Non logical OWLAxioms
    @DataPoint public static final OWLAnnotationAssertionAxiom entityAnnotationAxiom0 = _f.getOWLAnnotationAssertionAxiom(owlClass0.getIRI(), constantAnnotation0);
    @DataPoint public static final OWLAnnotationAssertionAxiom entityAnnotationAxiom1 = _f.getOWLAnnotationAssertionAxiom(owlClass0.getIRI(), constantAnnotation1);
    @DataPoint public static final OWLAnnotationAssertionAxiom entityAnnotationAxiom2 = _f.getOWLAnnotationAssertionAxiom(owlClass0.getIRI(), objectAnnotation0);
    @DataPoint public static final OWLAnnotationAssertionAxiom annotationAxiom0 = _f.getOWLAnnotationAssertionAxiom(iri("annotationAxiom2"), objectAnnotation0);
    @DataPoint public static final OWLAnnotationAssertionAxiom annotationAxiom1 = _f.getOWLAnnotationAssertionAxiom(anonymousIndividual0, objectAnnotation0);
    @DataPoint public static final OWLAnnotationAssertionAxiom annotationAxiom2 = _f.getOWLAnnotationAssertionAxiom(individual0.getIRI(), objectAnnotation0);
    @DataPoint public static final OWLAnnotationAssertionAxiom annotationAxiom3 = _f.getOWLAnnotationAssertionAxiom(dataProperty0.getIRI(), objectAnnotation0);
    @DataPoint public static final OWLAnnotationAssertionAxiom annotationAxiom4 = _f.getOWLAnnotationAssertionAxiom(objectProperty0.getIRI(), objectAnnotation0);
//    @DataPoint public static final OWLAnnotationAssertionAxiom annotationAxiom5 = _f.getOWLAnnotationAssertionAxiom(constantAnnotation0, objectAnnotation0);
    @DataPoint public static final OWLAnnotationPropertyRangeAxiom annotationPropertyRangeAxiom0 = _f.getOWLAnnotationPropertyRangeAxiom(annotationProperty0, iri("annotationPropertyRangeAxiom0"));
    @DataPoint public static final OWLAnnotationPropertyDomainAxiom annotationPropertyDomainAxiom0 = _f.getOWLAnnotationPropertyDomainAxiom(annotationProperty0, iri("annotationPropertyDomainAxiom0"));
    @DataPoint public static final OWLSubAnnotationPropertyOfAxiom subAnnotationPropertyOfAxiom0 = _f.getOWLSubAnnotationPropertyOfAxiom(annotationProperty0, annotationProperty1);
    @DataPoint public static final OWLDatatypeDefinitionAxiom datatypeDefinitionAxiom0 = _f.getOWLDatatypeDefinitionAxiom(xsdString, dataRangeRestriction0);
    @DataPoint public static final OWLHasKeyAxiom hasKeyAxiom0 = _f.getOWLHasKeyAxiom(owlClass0, objectProperty0, objectProperty1);
    @DataPoint public static final OWLHasKeyAxiom hasKeyAxiom1 = _f.getOWLHasKeyAxiom(owlClass0, dataProperty0, dataProperty1);
    @DataPoint public static final OWLHasKeyAxiom hasKeyAxiom2 = _f.getOWLHasKeyAxiom(owlClass0, dataProperty0, objectProperty0, dataProperty1, objectProperty1, dataProperty2, objectProperty2);
    @DataPoint public static final OWLDeclarationAxiom declarationAxiom0 = _f.getOWLDeclarationAxiom(owlClass0);
    @DataPoint public static final OWLDeclarationAxiom declarationAxiom1 = _f.getOWLDeclarationAxiom(xsdInt);
    @DataPoint public static final OWLDeclarationAxiom declarationAxiom2 = _f.getOWLDeclarationAxiom(individual0);
    @DataPoint public static final OWLDeclarationAxiom declarationAxiom3 = _f.getOWLDeclarationAxiom(dataProperty0);
    @DataPoint public static final OWLDeclarationAxiom declarationAxiom4 = _f.getOWLDeclarationAxiom(objectProperty0);
    @DataPoint public static final OWLDeclarationAxiom declarationAxiom5 = _f.getOWLDeclarationAxiom(annotationProperty0);

    @Theory
    public void testOWLAxiom(OWLAxiom object) throws Exception {
//        System.out.println(object);
        
        testSerialization(object);
        testSerialization(object.getAnnotatedAxiom(new LinkedHashSet<OWLAnnotation>(Arrays.asList(constantAnnotation0))));
        testSerialization(object.getAnnotatedAxiom(new LinkedHashSet<OWLAnnotation>(Arrays.asList(constantAnnotation0, constantAnnotation1))));
    }
    
    private void testSerialization(OWLAxiom object) throws Exception {
        StringBuilder target = new StringBuilder();
        OWLFormattingVisitor serializer = new OWLFormattingVisitor(target, OWLNamespaces.EMPTY_INSTANCE);
        object.accept(serializer);
        String serialization = target.toString();
//        System.out.println(serialization);
        InternalParser parser = new InternalParser(serialization, OWLNamespaces.EMPTY_INSTANCE, _f);
        OWLAxiom parsedObject = parser.parseOWLAxiom();
        assertEquals(object, parsedObject);
    }

    @Theory
    public void testStoring(OWLAxiom object) throws Exception {
        Set<OWLAxiom> currentlyUnsupportedAxioms = new LinkedHashSet<OWLAxiom>(Arrays.asList(
                swrlRule0,
                swrlRule1,
                swrlRule2,
                swrlRule3,
                swrlRule4,
                swrlRule5,
                swrlRule6,
                swrlRule7,
                swrlRule8,
                swrlRule9,
                swrlRule10,
                swrlRule11,
                swrlRule12,
                swrlRule13,
                swrlRule14,
                swrlRule15,
                swrlRule16,
                swrlRule17,
                equivalentClassesAxiom1,
                dataPropertyRangeAxiom2,
                dataPropertyRangeAxiom3,
                equivalentDataPropertiesAxiom1,
                classAssertionAxiom1,
                differentIndividualsAxiom1,
                differentIndividualsAxiom3,
                sameIndividualsAxiom1,
                sameIndividualsAxiom2,
                sameIndividualsAxiom3,
                dataPropertyAssertionAxiom0u,
                dataPropertyAssertionAxiom1u,
                dataPropertyAssertionAxiom2au,
                dataPropertyAssertionAxiom3u,
                dataPropertyAssertionAxiom4u,
                dataPropertyAssertionAxiom5u,
                dataPropertyAssertionAxiom6u,
                dataPropertyAssertionAxiom7u,
                dataPropertyAssertionAxiom8u,
                dataPropertyAssertionAxiom9u,
                dataPropertyAssertionAxiom10u,
                dataPropertyAssertionAxiom11u,
                dataPropertyAssertionAxiom12u,
                dataPropertyAssertionAxiom13u,
                dataPropertyAssertionAxiom14u,
                dataPropertyAssertionAxiom15u,
                dataPropertyAssertionAxiom16u,
                dataPropertyAssertionAxiom17u,
                dataPropertyAssertionAxiom18u,
                dataPropertyAssertionAxiom19u,
                negativeDataPropertyAssertionAxiom0u,
                negativeObjectPropertyAssertionAxiom0u,
                objectPropertyAssertionAxiom0u,
                equivalentObjectPropertiesAxiom1,
                annotationAxiom1,
                datatypeDefinitionAxiom0,
                declarationAxiom1
                ));
        Assume.assumeTrue(!currentlyUnsupportedAxioms.contains(object));
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = manager.createOntology(IRI.create("http://test.org/ontology#"));
            manager.addAxiom(ontology, object);
            StringOutputTarget testTarget = new StringOutputTarget();
            manager.saveOntology(ontology, new RDFXMLOntologyFormat(), testTarget);
            manager.removeOntology(ontology);
            ontology = manager.loadOntology(new StringInputSource(testTarget.toString()));
            Set<OWLAxiom> allAxioms = ontology.getAxioms();
            Set<OWLAxiom> axioms;
            if (!Collections.singleton(object).equals(allAxioms)) {
                // skip declaration axioms
                if (!(object instanceof OWLDeclarationAxiom)) {
                    Set<OWLAxiom> newAxioms = new LinkedHashSet<OWLAxiom>();
                    for (OWLAxiom axiom: allAxioms) {
                        if (!(axiom instanceof OWLDeclarationAxiom)) {
                            newAxioms.add(axiom);
                        }
                    }
                    axioms = newAxioms;
                } else {
                    axioms = allAxioms;
                }
            } else {
                axioms = allAxioms;
            }
            if (!Collections.singleton(object).equals(axioms)) {
                // debugging
                System.out.println(getFieldName(object) + ",");
            }
            assertEquals(Collections.singleton(object), axioms);
        } catch (Exception e) {
            // debugging
            System.out.println(getFieldName(object) + ",");
            throw e;
        }
    }
}
