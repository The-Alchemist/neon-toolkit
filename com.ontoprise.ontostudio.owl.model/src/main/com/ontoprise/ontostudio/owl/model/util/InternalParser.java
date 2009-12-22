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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLTypedLiteral;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.OWLFacet;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;


/**
 * @author krekeler
 *
 */
public class InternalParser {
    private OWLNamespaces _namespaces;
    private StreamTokenizer _tokenizer;
    private OWLDataFactory _f;

    public InternalParser(String object, OWLNamespaces namespaces, OWLDataFactory factory) throws InternalParserException  {
        _namespaces = namespaces;
        _tokenizer = createStreamTokenizer(new StringReader(object));
        nextToken();
        _f = factory;
    }

    public <E extends OWLAxiom> E parseOWLAxiom()  throws InternalParserException {
        return Cast.cast(parseAxiom());
    }

    public <E extends OWLClassExpression> E parseOWLDescription() throws InternalParserException  {
        return Cast.cast(parseDescription());
    }

    public <E extends OWLDataRange> E parseOWLDataRange() throws InternalParserException  {
        return Cast.cast(parseDataRange());
    }

    public <E extends OWLLiteral> E parseOWLConstant() throws InternalParserException  {
        return Cast.cast(parseConstant());
    }
    
    public <E extends OWLIndividual> E parseOWLIndividual() throws InternalParserException  {
        return Cast.cast(parseIndividual());
    }
    
    private InternalParserException createException(String message) {
        return new InternalParserException(message);
    }
    private InternalParserException createException(String message,Throwable cause) {
        return new InternalParserException(message,cause);
    }
    
    private OWLAnnotationProperty parseAnnotationProperty()  throws InternalParserException {
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        String uri=_namespaces.expandString(_tokenizer.sval);
        OWLAnnotationProperty result=_f.getOWLAnnotationProperty(OWLUtilities.toURI(uri));
        nextToken();
        return result;
    }
    

    private OWLAnnotationPropertyRangeAxiom parseAnnotationPropertyRange(Set<OWLAnnotation> annotations)  throws InternalParserException {
        nextToken();
        OWLAnnotationProperty annotationProperty = parseAnnotationProperty();
        IRI iri = parseIRI();
        return _f.getOWLAnnotationPropertyRangeAxiom(annotationProperty, iri, notNullOWLAnnotations(annotations));
    }
    
    private OWLAnnotationPropertyDomainAxiom parseAnnotationPropertyDomain(Set<OWLAnnotation> annotations) throws InternalParserException  {
        nextToken();
        OWLAnnotationProperty annotationProperty = parseAnnotationProperty();
        IRI iri = parseIRI();
        return _f.getOWLAnnotationPropertyDomainAxiom(annotationProperty, iri, notNullOWLAnnotations(annotations));
    }
    
    private OWLAxiom parseSubAnnotationProperty(Set<OWLAnnotation> annotations) throws InternalParserException  {
        nextToken();
        OWLAnnotationProperty subProperty = parseAnnotationProperty();
        OWLAnnotationProperty superProperty = parseAnnotationProperty();
        return _f.getOWLSubAnnotationPropertyOfAxiom(subProperty, superProperty, notNullOWLAnnotations(annotations));
    }
    
    private OWLDataProperty parseDataProperty() throws InternalParserException  {
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        String uri=_namespaces.expandString(_tokenizer.sval);
        OWLDataProperty result=_f.getOWLDataProperty(OWLUtilities.toURI(uri));
        nextToken();
        return result;
    }

    private OWLDataComplementOf parseDataNot() throws InternalParserException {
        nextToken();
        OWLDataRange dataRange=parseDataRange();
        return _f.getOWLDataComplementOf(dataRange);
    }

    private OWLDataOneOf parseDataOneOf() throws InternalParserException  {
        nextToken();
        Set<OWLLiteral> literalValues=new LinkedHashSet<OWLLiteral>();
        while (_tokenizer.ttype!=']')
            literalValues.add(parseConstant());
        return _f.getOWLDataOneOf(literalValues);
    }


    private OWLFacetRestriction parseDataRangeFacetRestriction() throws InternalParserException {
        checkTokenType(_tokenizer, '[');
        nextToken();
        if(!"facetRestriction".equalsIgnoreCase(_tokenizer.sval)) throw createException("Unknown DataRangeRestriction '"+_tokenizer.sval+"'. facetRestriction expected");
        nextToken();
        OWLFacet facet = OWLFacet.getFacetByShortName(_tokenizer.sval);
        nextToken();
        OWLTypedLiteral value = (OWLTypedLiteral)parseConstant();
        checkTokenType(_tokenizer, ']');
        nextToken();
        return _f.getOWLFacetRestriction(facet, value);
    }
    
    private OWLDatatypeRestriction parseDatatypeRestriction() throws InternalParserException {
        nextToken();
        OWLDatatype dataRange=parseDatatype();
        Set<OWLFacetRestriction> facetRestrictions=new LinkedHashSet<OWLFacetRestriction>();
        while (_tokenizer.ttype!=']')
            facetRestrictions.add(parseDataRangeFacetRestriction());
        return _f.getOWLDatatypeRestriction(dataRange, facetRestrictions);
    }
    
    private OWLDataRange parseDataRange() throws InternalParserException {
        if (_tokenizer.ttype=='[') {
            nextToken();
            checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
            OWLDataRange result;
            if ("not".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataNot();
            else if ("oneOf".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataOneOf();
            else if ("datatypeRestriction".equalsIgnoreCase(_tokenizer.sval))
                result=parseDatatypeRestriction();
            else if ("dataUnionOf".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataUnionOf();
            else if ("dataIntersectionOf".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataIntersectionOf();
            else
                throw createException("Unknown data range '"+_tokenizer.sval+"'.");
            checkTokenType(_tokenizer,']');
            nextToken();
            return result;
        }
        else if (_tokenizer.ttype==StreamTokenizer.TT_WORD)
            return parseDatatype();
        else {
            String tokenAsString;
            switch (_tokenizer.ttype) {
            case StreamTokenizer.TT_NUMBER:
                tokenAsString=String.valueOf(_tokenizer.nval);
                break;
            case StreamTokenizer.TT_EOF:
                tokenAsString="<EOF>";
                break;
            default:
                tokenAsString="'"+String.valueOf((char)_tokenizer.ttype)+"'";
                break;
            }
            throw createException("Unexpected token "+tokenAsString+".");
        }
    }

    private OWLDataRange parseDataIntersectionOf() throws InternalParserException {
        nextToken();
        
        Set<OWLDataRange> dataRanges = new LinkedHashSet<OWLDataRange>();
        while(_tokenizer.ttype!=']')
            dataRanges.add(parseDataRange());
        
        return _f.getOWLDataIntersectionOf(dataRanges);
    }

    private OWLDataRange parseDataUnionOf() throws InternalParserException {
        nextToken();
        Set<OWLDataRange> dataRanges = new LinkedHashSet<OWLDataRange>();
        while (_tokenizer.ttype != ']')
            dataRanges.add(parseDataRange());

        return _f.getOWLDataUnionOf(dataRanges);
    }

    private OWLDatatype parseDatatype() throws InternalParserException  {
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        String uri=_namespaces.expandString(_tokenizer.sval);
        OWLDatatype result=_f.getOWLDatatype(OWLUtilities.toURI(uri));
        nextToken();
        return result;
    }
    
    private OWLAxiom parseDatatypeDefinition(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLDatatype datatype = parseDatatype();
        OWLDataRange dataRange = parseDataRange();
        return _f.getOWLDatatypeDefinitionAxiom(datatype, dataRange, notNullOWLAnnotations(annotations));
    }

    private IRI parseIRI() throws InternalParserException  {
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        String uri=_namespaces.expandString(_tokenizer.sval);
        IRI result=_f.getIRI(OWLUtilities.toURI(uri));
        nextToken();
        return result;
    }

    private OWLIndividual parseIndividual() throws InternalParserException {
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        if(_tokenizer.sval.startsWith("_:")) { //$NON-NLS-1$
            return parseAnonymousIndividual();
        } else {
            return parseNamedIndividual();
        }
    }
    
    private OWLAnonymousIndividual parseAnonymousIndividual() throws InternalParserException{
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        OWLAnonymousIndividual result = _f.getOWLAnonymousIndividual(_tokenizer.sval.substring(2));
        nextToken();
        return result;
    }
    
    private OWLNamedIndividual parseNamedIndividual() throws InternalParserException{
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        String uri=_namespaces.expandString(_tokenizer.sval);
        OWLNamedIndividual result=_f.getOWLNamedIndividual(OWLUtilities.toURI(uri));
        nextToken();
        return result;
    }

    private OWLObjectProperty parseObjectProperty() throws InternalParserException {
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        String uri=_namespaces.expandString(_tokenizer.sval);
        OWLObjectProperty result=_f.getOWLObjectProperty(OWLUtilities.toURI(uri));
        nextToken();
        return result;
    }
    
    private OWLObjectInverseOf parseInverseObjectProperty() throws InternalParserException {
        nextToken();
        OWLObjectProperty objectProperty=parseObjectProperty();
        return _f.getOWLObjectInverseOf(objectProperty);
    }
    
    private OWLObjectPropertyExpression parseObjectPropertyExpression() throws InternalParserException {
        if (_tokenizer.ttype=='[') {
            nextToken();
            checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
            OWLObjectPropertyExpression result;
            if ("inv".equalsIgnoreCase(_tokenizer.sval))
                result=parseInverseObjectProperty();
            else
                throw createException("Unknown object property expression '"+_tokenizer.sval+"'.");
            checkTokenType(_tokenizer,']');
            nextToken();
            return result;
        }
        else if (_tokenizer.ttype==StreamTokenizer.TT_WORD)
            return parseObjectProperty();
        else {
            String tokenAsString;
            switch (_tokenizer.ttype) {
            case StreamTokenizer.TT_NUMBER:
                tokenAsString=String.valueOf(_tokenizer.nval);
                break;
            case StreamTokenizer.TT_EOF:
                tokenAsString="<EOF>";
                break;
            default:
                tokenAsString="'"+String.valueOf((char)_tokenizer.ttype)+"'";
                break;
            }
            throw createException("Unexpected token "+tokenAsString+".");
        }
    }
    
    
    
    private OWLDataAllValuesFrom parseDataAll() throws InternalParserException {
        nextToken();
        List<String> names=new ArrayList<String>();
        while (_tokenizer.ttype==StreamTokenizer.TT_WORD) {
            names.add(_namespaces.expandString(_tokenizer.sval));
            nextToken();
        }
        if (names.size() != 2) throw new IllegalArgumentException();
        OWLDataRange dataRange;
        if (_tokenizer.ttype==']') {
            if (names.size()==0)
                throw createException("Data range expected.");
            String dataRangeName=names.remove(names.size()-1);
            dataRange=_f.getOWLDatatype(OWLUtilities.toURI(dataRangeName));
        }
        else
            dataRange=parseDataRange();
        OWLDataPropertyExpression[] dataProperties=new OWLDataPropertyExpression[names.size()];
        for (int i=0;i<names.size();i++)
            dataProperties[i]=_f.getOWLDataProperty(OWLUtilities.toURI(names.get(i)));
        return _f.getOWLDataAllValuesFrom(dataProperties[0],dataRange);
    }

    private OWLDataSomeValuesFrom parseDataSome() throws InternalParserException {
        nextToken();
        List<String> names=new ArrayList<String>();
        while (_tokenizer.ttype==StreamTokenizer.TT_WORD) {
            names.add(_namespaces.expandString(_tokenizer.sval));
            nextToken();
        }
        if (names.size() != 2) throw new IllegalArgumentException();
        OWLDataRange dataRange;
        if (_tokenizer.ttype==']') {
            if (names.size()==0)
                throw createException("Data range expected.");
            String dataRangeName=names.remove(names.size()-1);
            dataRange=_f.getOWLDatatype(OWLUtilities.toURI(dataRangeName));
        }
        else
            dataRange=parseDataRange();
        OWLDataPropertyExpression[] dataProperties=new OWLDataPropertyExpression[names.size()];
        for (int i=0;i<names.size();i++)
            dataProperties[i]=_f.getOWLDataProperty(OWLUtilities.toURI(names.get(i)));
        return _f.getOWLDataSomeValuesFrom(dataProperties[0],dataRange);
    }

    private OWLLiteral parseConstant() throws InternalParserException {
        try {
            if (isNumberToken()) {
                double result=getNumber();
                nextToken();
                return _f.getOWLTypedLiteral(result);
            }
            else if (_tokenizer.ttype==StreamTokenizer.TT_WORD) {
                if ("<null>".equalsIgnoreCase(_tokenizer.sval)) {
                    nextToken();
                    return null;
                }
                throw createException("Invalid token '"+_tokenizer.sval+"'.");
            }
            else if (_tokenizer.ttype=='"') {
                PositionReader reader=((StreamTokenizerWithSpecialMinusCharacterHandling)_tokenizer).getReader();
                reader.mark(10000000);
                nextToken();
                while (_tokenizer.ttype!='"') {
                    switch(_tokenizer.ttype) {
                    case StreamTokenizer.TT_EOF: {
                        throw createException("End of input stream while parsing literal value.");
                    }
                    case StreamTokenizer.TT_EOL: {
                        // do nothing
                        break;
                    }
                    case StreamTokenizer.TT_NUMBER: {
                        // do nothing
                        break;
                    }
                    case StreamTokenizer.TT_WORD: {
                        // do nothing
                        break;
                    }
                    default: {
                        if (_tokenizer.ttype=='\\') {
                            // escaped quotes must not end the while loop
                            nextToken();
                        }
                    }
                    }
                    nextToken();
                }
                long end=reader.getPosition();
                // go back to the marked position
                reader.reset();
                StringBuffer buffer=new StringBuffer();
                for (long i=reader.getMark()+1;i<end;) {
                    char c=(char)reader.read();
                    i++;
                    if (c=='\\') {
                        char sequence=(char)reader.read();
                        i++;
                        if (sequence=='\\')
                            buffer.append('\\');
                        else if (sequence=='"')
                            buffer.append('"');
                        else if (sequence=='n')
                            buffer.append('\n');
                        else if (sequence=='r')
                            buffer.append('\r');
                        else if (sequence=='t')
                            buffer.append('\t');
                        else
                            throw createException("Unrecognized escape sequence at position "+reader.getPosition()+" while parsing literal value.");
                    } else {
                        buffer.append(c);
                    }
                }
                reader.read(); // skip the end quote
                String stringValue=buffer.toString();
                nextToken();
                if (_tokenizer.ttype==StreamTokenizer.TT_WORD && _tokenizer.sval.startsWith("^^")) {
                    String datatypeURI=_tokenizer.sval.substring(2);
                    if (datatypeURI.startsWith("<")) {
                        if (!datatypeURI.endsWith(">"))
                            throw createException("Invalid datatype URI '"+datatypeURI+"'.");
                        datatypeURI=datatypeURI.substring(1,datatypeURI.length()-1);
                    }
                    nextToken();
                    datatypeURI=_namespaces.expandString(datatypeURI);
                    return _f.getOWLTypedLiteral(stringValue, _f.getOWLDatatype(OWLUtilities.toURI(datatypeURI)));
                }
                else if (_tokenizer.ttype==StreamTokenizer.TT_WORD && _tokenizer.sval.startsWith("@")) {
                    String language=_tokenizer.sval.substring(1);
                    nextToken();
                    return _f.getOWLStringLiteral((stringValue.equals("null"))?null:stringValue, (language.equals("null"))?null:language);
                }
                else
                    return _f.getOWLStringLiteral((stringValue.equals("null"))?null:stringValue, null);
            }
            else
                throw createException("Invalid token '"+Character.valueOf((char)_tokenizer.ttype)+"'.");
        } catch (IOException e) {
            throw createException("Error reading input stream.",e);
        }
    }
    
    private OWLDataHasValue parseDataHasValue() throws InternalParserException {
        nextToken();
        OWLDataPropertyExpression dataProperty=parseDataProperty();
        OWLLiteral constant=parseConstant();
        return _f.getOWLDataHasValue(dataProperty,constant);
    }
    
    private OWLHasKeyAxiom parseHasKey(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLClassExpression classExpresseion = parseDescription();
        checkTokenType(_tokenizer, '[');
        nextToken();
        Set<OWLPropertyExpression<?,?>> propertyExpressions = new LinkedHashSet<OWLPropertyExpression<?,?>>();
        while(_tokenizer.ttype != ']'){
            propertyExpressions.add(parseObjectProperty());
        }
        nextToken();
        checkTokenType(_tokenizer, '[');
        nextToken();
        while(_tokenizer.ttype != ']'){
            propertyExpressions.add(parseDataProperty());
        }
        nextToken();
        
        return _f.getOWLHasKeyAxiom(classExpresseion, propertyExpressions, notNullOWLAnnotations(annotations));
    }

    private OWLDataCardinalityRestriction parseDataCardinality() throws InternalParserException {
        String type=_tokenizer.sval;
        nextToken();
        int cardinalityType;
        int cardinality;
        if ("dataAtLeast".equalsIgnoreCase(type)) {
            checkNumberToken();
            cardinalityType=0;
            cardinality=(int)getNumber();
            nextToken();
        }
        else if ("dataAtMost".equalsIgnoreCase(type)) {
            checkNumberToken();
            cardinalityType=1;
            cardinality=(int)getNumber();
            nextToken();
        }
        else if ("dataExactly".equalsIgnoreCase(type)) {
            checkNumberToken();
            cardinalityType=2;
            cardinality=(int)getNumber();
            nextToken();
        }
        else
            throw createException("Invalid cardinality type '"+type+"'.");
        OWLDataPropertyExpression dataProperty=parseDataProperty();
        OWLDataRange dataRange;
        if (_tokenizer.ttype==']')
            dataRange=_f.getOWLDatatype(OWLUtilities.toURI(OWLNamespaces.RDFS_NS+"literal"));
        else
            dataRange=parseDataRange();
        switch (cardinalityType) {
            case 0:
                return _f.getOWLDataMinCardinality(cardinality, dataProperty, dataRange);
            case 1:
                return _f.getOWLDataMaxCardinality(cardinality, dataProperty, dataRange);
            case 2:
                return _f.getOWLDataExactCardinality(cardinality, dataProperty, dataRange);
            default:
                throw new IllegalStateException();
        }
    }

    private OWLObjectComplementOf parseObjectNot() throws InternalParserException {
        nextToken();
        OWLClassExpression description=parseDescription();
        return _f.getOWLObjectComplementOf(description);
    }

    private OWLObjectIntersectionOf parseObjectAnd() throws InternalParserException {
        nextToken();
        Set<OWLClassExpression> descriptions=new LinkedHashSet<OWLClassExpression>();
        while (_tokenizer.ttype!=']')
            descriptions.add(parseDescription());
        return _f.getOWLObjectIntersectionOf(descriptions);
    }

    private OWLObjectUnionOf parseObjectOr() throws InternalParserException {
        nextToken();
        Set<OWLClassExpression> descriptions=new LinkedHashSet<OWLClassExpression>();
        while (_tokenizer.ttype!=']')
            descriptions.add(parseDescription());
        return _f.getOWLObjectUnionOf(descriptions);
    }

    private OWLObjectAllValuesFrom parseObjectAll() throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLClassExpression description=parseDescription();
        return _f.getOWLObjectAllValuesFrom(objectProperty,description);
    }

    private OWLObjectSomeValuesFrom parseObjectSome() throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLClassExpression description=parseDescription();
        return _f.getOWLObjectSomeValuesFrom(objectProperty,description);
    }

    private OWLObjectHasSelf parseObjectExistsSelf() throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        return _f.getOWLObjectHasSelf(objectProperty);
    }

    private OWLObjectCardinalityRestriction parseObjectCardinality() throws InternalParserException {
        String type=_tokenizer.sval;
        nextToken();
        int cardinalityType;
        int cardinality;
        if ("atLeast".equalsIgnoreCase(type)) {
            checkNumberToken();
            cardinalityType=0;
            cardinality=(int)getNumber();
            nextToken();
        }
        else if ("atMost".equalsIgnoreCase(type)) {
            checkNumberToken();
            cardinalityType=1;
            cardinality=(int)getNumber();
            nextToken();
        }
        else if ("exactly".equalsIgnoreCase(type)) {
            checkNumberToken();
            cardinalityType=2;
            cardinality=(int)getNumber();
            nextToken();
        }
        else
            throw createException("Invalid cardinality type '"+type+"'.");
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLClassExpression description;
        if (_tokenizer.ttype==']')
            description=_f.getOWLThing();
        else
            description=parseDescription();
        switch (cardinalityType) {
            case 0:
                return _f.getOWLObjectMinCardinality(cardinality, objectProperty, description);
            case 1:
                return _f.getOWLObjectMaxCardinality(cardinality, objectProperty, description);
            case 2:
                return _f.getOWLObjectExactCardinality(cardinality, objectProperty, description);
            default:
                throw new IllegalStateException();
        }
    }

    private OWLObjectHasValue parseObjectHasValue() throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLIndividual individual=parseIndividual();
        return _f.getOWLObjectHasValue(objectProperty,individual);
    }

    private OWLObjectOneOf parseObjectOneOf() throws InternalParserException {
        nextToken();
        Set<OWLIndividual> individuals=new LinkedHashSet<OWLIndividual>();
        while (_tokenizer.ttype!=']')
            individuals.add(parseIndividual());
        return _f.getOWLObjectOneOf(individuals);
    }
    
    private OWLClass parseOWLClass() throws InternalParserException {
        checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
        String uri=_namespaces.expandString(_tokenizer.sval);
        OWLClass result=_f.getOWLClass(OWLUtilities.toURI(uri));
        nextToken();
        return result;
    }
    
    private OWLClassExpression parseDescription() throws InternalParserException {
        if (_tokenizer.ttype=='[') {
            nextToken();
            checkTokenType(_tokenizer,StreamTokenizer.TT_WORD);
            OWLClassExpression result;
            if ("dataAll".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataAll();
            else if ("dataSome".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataSome();
            else if ("dataHasValue".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataHasValue();
            else if ("dataAtLeast".equalsIgnoreCase(_tokenizer.sval) || "dataAtMost".equalsIgnoreCase(_tokenizer.sval) || "dataExactly".equalsIgnoreCase(_tokenizer.sval))
                result=parseDataCardinality();
            else if ("not".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectNot();
            else if ("and".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectAnd();
            else if ("or".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectOr();
            else if ("all".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectAll();
            else if ("some".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectSome();
            else if ("self".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectExistsSelf();
            else if ("atLeast".equalsIgnoreCase(_tokenizer.sval) || "atMost".equalsIgnoreCase(_tokenizer.sval) || "exactly".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectCardinality();
            else if ("hasValue".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectHasValue();
            else if ("oneOf".equalsIgnoreCase(_tokenizer.sval))
                result=parseObjectOneOf();
            else
                throw createException("Unknown description '"+_tokenizer.sval+"'.");
            checkTokenType(_tokenizer,']');
            nextToken();
            return result;
        }
        else if (_tokenizer.ttype==StreamTokenizer.TT_WORD)
            return parseOWLClass();
        else {
            String tokenAsString;
            switch (_tokenizer.ttype) {
            case StreamTokenizer.TT_NUMBER:
                tokenAsString=String.valueOf(_tokenizer.nval);
                break;
            case StreamTokenizer.TT_EOF:
                tokenAsString="<EOF>";
                break;
            default:
                tokenAsString="'"+String.valueOf((char)_tokenizer.ttype)+"'";
                break;
            }
            throw createException("Unexpected token "+tokenAsString+".");
        }
    }
    
    private void checkNumberToken() throws InternalParserException {
        if (!isNumberToken()) {
            String nextTokenAsString;
            switch (_tokenizer.ttype) {
            case StreamTokenizer.TT_WORD:
                nextTokenAsString='\''+_tokenizer.sval+'\'';
                break;
            case StreamTokenizer.TT_NUMBER:
                nextTokenAsString=String.valueOf(_tokenizer.nval);
                break;
            case StreamTokenizer.TT_EOF:
                nextTokenAsString="<EOF>";
                break;
            default:
                nextTokenAsString="'"+String.valueOf((char)_tokenizer.ttype)+"'";
                break;
            }
            String expectedTokenAsString;
            expectedTokenAsString="Number";
            throw createException(expectedTokenAsString+" expected but "+nextTokenAsString+" found.");
        }
    }
    private boolean isNumberToken() {
        // TODO 2008-07-08 (tkr): this is a pretty inefficient implementation, do it better...
        if (_tokenizer.ttype != StreamTokenizer.TT_WORD || _tokenizer.sval == null) {
            return false;
        }
        try {
            Double.parseDouble(_tokenizer.sval);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Get the current word token as number.<br/>
     * <br/>
     * <em>Precondition</em>: <code>isNumber()</code> must hold.
     * @param _tokenizer
     * @return <code>tokenizer.sval</code> as number.
     */
    private double getNumber() {
        assert(isNumberToken());
        return Double.parseDouble(_tokenizer.sval);
    }
    private void checkTokenType(StreamTokenizer tokenizer,int expectedTokenType) throws InternalParserException {
        if (tokenizer.ttype!=expectedTokenType) {
            String nextTokenAsString;
            switch (tokenizer.ttype) {
            case StreamTokenizer.TT_WORD:
                nextTokenAsString='\''+tokenizer.sval+'\'';
                break;
            case StreamTokenizer.TT_NUMBER:
                nextTokenAsString=String.valueOf(tokenizer.nval);
                break;
            case StreamTokenizer.TT_EOF:
                nextTokenAsString="<EOF>";
                break;
            default:
                nextTokenAsString="'"+String.valueOf((char)tokenizer.ttype)+"'";
                break;
            }
            String expectedTokenAsString;
            switch (expectedTokenType) {
            case StreamTokenizer.TT_WORD:
                expectedTokenAsString="Word";
                break;
            case StreamTokenizer.TT_NUMBER:
                expectedTokenAsString="Number";
                break;
            case StreamTokenizer.TT_EOF:
                expectedTokenAsString="<EOF>";
                break;
            default:
                expectedTokenAsString=String.valueOf((char)expectedTokenType);
                break;
            }
            throw createException(expectedTokenAsString+" expected but "+nextTokenAsString+" found.");
        }
    }
    private void nextToken() throws InternalParserException {
        try {
            _tokenizer.nextToken();
        }
        catch (IOException error) {
            throw createException("Error reading input stream.",error);
        }
    }
    /**
     * Wraps a <code>Reader</code> and provides a position info.
     * 
     * @author krekeler
     */
    private static class PositionReader extends Reader {
        protected Reader m_source;
        protected long m_position;
        protected long m_mark;
        public PositionReader(Reader source) {
            m_source=source;
            m_position=0;
            m_mark=-1;
        }
        public long getPosition() {
            return m_position;
        }
        public long getMark() {
            return m_mark;
        }
        @Override
        public void close() throws IOException {
            m_source.close();
        }
        @Override
        public void mark(int readAheadLimit) throws IOException {
            m_source.mark(readAheadLimit);
            m_mark=m_position;
        }
        @Override
        public boolean markSupported() {
            return m_source.markSupported();
        }
        @Override
        public int read() throws IOException {
            int c=m_source.read();
            m_position++;
            return c;
        }
        @Override
        public int read(char[] cbuf) throws IOException {
            int count=m_source.read(cbuf);
            if (count!=-1)
                m_position+=count;
            return count;
        }
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int count=m_source.read(cbuf, off, len);
            if (count!=-1)
                m_position+=count;
            return count;
        }
        @Override
        public int read(CharBuffer target) throws IOException {
            int count=m_source.read(target);
            if (count!=-1)
                m_position+=count;
            return count;
        }
        @Override
        public boolean ready() throws IOException {
            return m_source.ready();
        }
        @Override
        public void reset() throws IOException {
            m_source.reset();
            if (m_mark!=-1) {
                m_position=m_mark;
            }
        }
        @Override
        public long skip(long n) throws IOException {
            long count=m_source.skip(n);
            if (count!=-1)
                m_position+=count;
            return count;
        }
    }
    /**
     * This is a helper class which handles an issue with the original <code>StreamTokenizer</code> implementation,
     * which behaves strange on the character '-':
     * if parsing "-" and <code>parseNumber()</code> is on, <code>ttype</code> equals '-', but if <code>parseNumber()</code> is off,
     * <code>ttype</code> equals -3 and <code>sval</code> contains "-".<br/>
     * <br/>
     * 
     * @author krekeler
     */
    private static class StreamTokenizerWithSpecialMinusCharacterHandling extends StreamTokenizer {
        protected PositionReader m_reader;
        private static StreamTokenizerWithSpecialMinusCharacterHandling createInstance(Reader r) {
            if (!r.markSupported())
                r=new BufferedReader(r);
            return new StreamTokenizerWithSpecialMinusCharacterHandling(new PositionReader(r));
        }
        protected StreamTokenizerWithSpecialMinusCharacterHandling(PositionReader r) {
            super(r);
            m_reader=r;
        }
        @Override
        public int nextToken() throws IOException {
            super.nextToken();
            if (ttype==TT_WORD && "-".equals(sval)) {
                ttype='-';
            }
            return ttype;
        }
        public PositionReader getReader() {
            return m_reader;
        }
    }
    private static StreamTokenizer createStreamTokenizer(Reader reader) {
        // 2008-07-08 (tkr): Because parsing numbers is now done "manually" we need a special handling for the '-' character
        StreamTokenizer tokenizer=StreamTokenizerWithSpecialMinusCharacterHandling.createInstance(reader);
        tokenizer.resetSyntax();
        // 2008-08-09 (tkr): Parsing of literal values is done "manually"
        //tokenizer.quoteChar('"');
        tokenizer.whitespaceChars(' ',' ');
        tokenizer.whitespaceChars('\t','\t');
        tokenizer.eolIsSignificant(false);
        // 2008-07-08 (tkr): Parsing numbers is now done "manually"
        //tokenizer.parseNumbers();

        // 2008-11-13 (tkr): Allow any non special characters
        boolean allowAll = true;
        if (allowAll) {
            // Note that 
            //    - minus '-' (negation of literals) and 
            //    - ':', '<', >', '_', and '=' (fmolecule)
            //    - '@' (StringWithLanguage)
            // are special chars which are also word chars, but already were word chars in the original implementation.
            // Note that \r and \n are non special chars which are no word chars and have not been before.
            // Note that '*' is a special char (fmolecule) and is now included as word char, but was not a word char before.
            
            // zero
            tokenizer.wordChars(1, 8);
            // tab, cariage return
            tokenizer.wordChars(11, 12);
            // line feed
            tokenizer.wordChars(14, 31);
            // space
            tokenizer.wordChars(33, 33);
            // quote
            tokenizer.wordChars(35, 90);
            // '[', '\', ']'
            tokenizer.wordChars(94, 122);
            // '{', '|', '}'
            tokenizer.wordChars(126, Integer.MAX_VALUE);
            return tokenizer;
        }
        
        // 2008-11-13 (tkr): old word chars
        tokenizer.wordChars('A','Z');
        tokenizer.wordChars('a','z');
        tokenizer.wordChars('0','9');
        tokenizer.wordChars('_','_');
        tokenizer.wordChars('-','-');
        tokenizer.wordChars('!','!');
        tokenizer.wordChars('.','.');
        tokenizer.wordChars('~','~');
        tokenizer.wordChars('\'','\'');
        tokenizer.wordChars('(','(');
        tokenizer.wordChars(')',')');
        tokenizer.wordChars('*','*');
        tokenizer.wordChars(',',',');
        tokenizer.wordChars(';',';');
        tokenizer.wordChars(':',':');
        tokenizer.wordChars('$','$');
        tokenizer.wordChars('&','&');
        tokenizer.wordChars('+','+');
        tokenizer.wordChars('=','=');
        tokenizer.wordChars('?','?');
        tokenizer.wordChars('/','/');
        tokenizer.wordChars('@','@');
        tokenizer.wordChars('%','%');
        tokenizer.wordChars('<','<');
        tokenizer.wordChars('>','>');
        tokenizer.wordChars('#','#');
        tokenizer.wordChars('^','^');
        return tokenizer;
    }
    
    
    private OWLAnnotationAssertionAxiom parseAnnotation(Set<OWLAnnotation> annotations) throws InternalParserException {
        String annotationType=_tokenizer.sval;
        nextToken();
        OWLAnnotationProperty annotationProperty=parseAnnotationProperty();
        OWLAnnotationSubject subject;
        if ("annotationIRI".equalsIgnoreCase(annotationType))
            subject=parseIRI();
        else if ("annotationAnonymousIndividual".equalsIgnoreCase(annotationType))
            subject=parseAnonymousIndividual();
        else if ("annotationAnnotation".equalsIgnoreCase(annotationType))
            subject=parseOWLAnnotation(null);
        else
            throw createException("Invalid annotation type '"+annotationType+"'.");
        if (_tokenizer.ttype=='[') {
            nextToken();
            IRI annotationValue=parseIRI();
            checkTokenType(_tokenizer,']');
            nextToken();
            return _f.getOWLAnnotationAssertionAxiom(annotationProperty, subject, annotationValue, notNullOWLAnnotations(annotations));
        }
        else
            return _f.getOWLAnnotationAssertionAxiom(annotationProperty, subject, parseConstant(), notNullOWLAnnotations(annotations));
    }

    private OWLDeclarationAxiom parseDeclaration(Set<OWLAnnotation> annotations) throws InternalParserException {
        String annotationType=_tokenizer.sval;
        nextToken();
        OWLEntity entity;
        if ("dataDeclaration".equalsIgnoreCase(annotationType))
            entity=parseDataProperty();
        else if ("objectDeclaration".equalsIgnoreCase(annotationType))
            entity=parseObjectProperty();
        else if ("individualDeclaration".equalsIgnoreCase(annotationType))
            entity=parseNamedIndividual();
        else if ("classDeclaration".equalsIgnoreCase(annotationType))
            entity=parseOWLClass();
        else if ("datatypeDeclaration".equalsIgnoreCase(annotationType))
            entity=parseDatatype();
        else if ("annotationDeclaration".equalsIgnoreCase(annotationType)) {
            entity=parseAnnotationProperty();
        }
        else
            throw createException("Invalid annotation type '"+annotationType+"'.");
        return _f.getOWLDeclarationAxiom(entity, notNullOWLAnnotations(annotations));
    }

    private OWLClassAssertionAxiom parseClassMember(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLClassExpression description=parseDescription();
        OWLIndividual individual=parseIndividual();
        return _f.getOWLClassAssertionAxiom(description, individual, notNullOWLAnnotations(annotations));
    }

    private OWLDataPropertyCharacteristicAxiom parseDataPropertyAttribute(Set<OWLAnnotation> annotations) throws InternalParserException {
        int attribute;
        if ("dataFunctional".equalsIgnoreCase(_tokenizer.sval))
            attribute=0;
        else
            throw createException("Invalid data property attribute.");
        nextToken();
        OWLDataPropertyExpression dataProperty=parseDataProperty();
        switch (attribute) {
            case 0:
                return _f.getOWLFunctionalDataPropertyAxiom(dataProperty, notNullOWLAnnotations(annotations));
            default:
                throw new IllegalStateException();
        }
    }

    private OWLDataPropertyDomainAxiom parseDataPropertyDomain(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLDataPropertyExpression dataProperty=parseDataProperty();
        OWLClassExpression domain=parseDescription();
        return _f.getOWLDataPropertyDomainAxiom(dataProperty,domain, notNullOWLAnnotations(annotations));
    }

    private OWLDataPropertyAssertionAxiom parseDataPropertyMember(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLDataPropertyExpression dataProperty=parseDataProperty();
        OWLIndividual sourceIndividual=parseIndividual();
        OWLLiteral targetValue=parseConstant();
        return _f.getOWLDataPropertyAssertionAxiom(dataProperty,sourceIndividual,targetValue, notNullOWLAnnotations(annotations));
    }

    private OWLNegativeDataPropertyAssertionAxiom parseNegativeDataPropertyMember(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLDataPropertyExpression dataProperty=parseDataProperty();
        OWLIndividual sourceIndividual=parseIndividual();
        OWLLiteral targetValue=parseConstant();
        return _f.getOWLNegativeDataPropertyAssertionAxiom(dataProperty,sourceIndividual,targetValue, notNullOWLAnnotations(annotations));
    }

    private OWLDataPropertyRangeAxiom parseDataPropertyRange(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLDataPropertyExpression dataProperty=parseDataProperty();
        OWLDataRange range=parseDataRange();
        return _f.getOWLDataPropertyRangeAxiom(dataProperty,range, notNullOWLAnnotations(annotations));
    }

    private OWLDifferentIndividualsAxiom parseDifferentIndividuals(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLIndividual> individuals=new LinkedHashSet<OWLIndividual>();
        while (_tokenizer.ttype!=']')
            individuals.add(parseIndividual());
        return _f.getOWLDifferentIndividualsAxiom(individuals, notNullOWLAnnotations(annotations));
    }

    private OWLDisjointClassesAxiom parseDisjointClasses(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLClassExpression> descriptions=new LinkedHashSet<OWLClassExpression>();
        while (_tokenizer.ttype!=']')
            descriptions.add(parseDescription());
        return _f.getOWLDisjointClassesAxiom(descriptions, notNullOWLAnnotations(annotations));
    }

    private Set<OWLAnnotation> parseOWLAnnotations(Set<OWLAnnotation> oldAnnotations) throws InternalParserException{
        Set<OWLAnnotation> annotations = new LinkedHashSet<OWLAnnotation>();
            nextToken();
            while(_tokenizer.ttype!='}'){
               
                if(_tokenizer.ttype == '[') annotations.add(parseOWLAnnotation(oldAnnotations));
                else if(_tokenizer.ttype=='{')parseOWLAnnotations(annotations);
                else nextToken();
            }
        
        return annotations;
    }
    private OWLAnnotation parseOWLAnnotation(Set<OWLAnnotation> annotations) throws InternalParserException{
        nextToken();
        OWLAnnotationProperty annotationProperty = parseAnnotationProperty();
        OWLLiteral value = parseConstant();
        return _f.getOWLAnnotation(annotationProperty, value, notNullOWLAnnotations(annotations));
    }
    
    private OWLDisjointUnionAxiom parseDisjointUnion(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLClass owlClass=parseOWLClass();
        Set<OWLClassExpression> descriptions=new LinkedHashSet<OWLClassExpression>();
        while (_tokenizer.ttype!=']')
            descriptions.add(parseDescription());
        return _f.getOWLDisjointUnionAxiom(owlClass,descriptions, notNullOWLAnnotations(annotations));
    }

    private OWLEquivalentClassesAxiom parseEquivalentClasses(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLClassExpression> descriptions=new LinkedHashSet<OWLClassExpression>();
        while (_tokenizer.ttype!=']')
            descriptions.add(parseDescription());
        return _f.getOWLEquivalentClassesAxiom(descriptions, notNullOWLAnnotations(annotations));
    }

    private OWLEquivalentDataPropertiesAxiom parseEquivalentDataProperties(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLDataPropertyExpression> dataProperties=new LinkedHashSet<OWLDataPropertyExpression>();
        while (_tokenizer.ttype!=']')
            dataProperties.add(parseDataProperty());
        return _f.getOWLEquivalentDataPropertiesAxiom(dataProperties, notNullOWLAnnotations(annotations));
    }

    private OWLDisjointDataPropertiesAxiom parseDisjointDataProperties(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLDataPropertyExpression> dataProperties=new LinkedHashSet<OWLDataPropertyExpression>();
        while (_tokenizer.ttype!=']')
            dataProperties.add(parseDataProperty());
        return _f.getOWLDisjointDataPropertiesAxiom(dataProperties, notNullOWLAnnotations(annotations));
    }

    private OWLEquivalentObjectPropertiesAxiom parseEquivalentObjectProperties(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLObjectPropertyExpression> objectProperties=new LinkedHashSet<OWLObjectPropertyExpression>();
        while (_tokenizer.ttype!=']')
            objectProperties.add(parseObjectPropertyExpression());
        return _f.getOWLEquivalentObjectPropertiesAxiom(objectProperties, notNullOWLAnnotations(annotations));
    }

    private OWLDisjointObjectPropertiesAxiom parseDisjointObjectProperties(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLObjectPropertyExpression> objectProperties=new LinkedHashSet<OWLObjectPropertyExpression>();
        while (_tokenizer.ttype!=']')
            objectProperties.add(parseObjectPropertyExpression());
        return _f.getOWLDisjointObjectPropertiesAxiom(objectProperties, notNullOWLAnnotations(annotations));
    }

    private OWLObjectPropertyCharacteristicAxiom parseObjectPropertyAttribute(Set<OWLAnnotation> annotations) throws InternalParserException {
        int attribute;
        if ("objectFunctional".equalsIgnoreCase(_tokenizer.sval))
            attribute=0;
        else if ("objectInverseFunctional".equalsIgnoreCase(_tokenizer.sval))
            attribute=1;
        else if ("objectSymmetric".equalsIgnoreCase(_tokenizer.sval))
            attribute=2;
        else if ("objectTransitive".equalsIgnoreCase(_tokenizer.sval))
            attribute=3;
        else if ("objectReflexive".equalsIgnoreCase(_tokenizer.sval))
            attribute=4;
        else if ("objectIrreflexive".equalsIgnoreCase(_tokenizer.sval))
            attribute=5;
        else if ("objectAsymmetric".equalsIgnoreCase(_tokenizer.sval))
            attribute=6;
        else
            throw createException("Invalid object property attribute.");
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        switch (attribute) {
            case 0:
                return _f.getOWLFunctionalObjectPropertyAxiom(objectProperty, notNullOWLAnnotations(annotations));
            case 1:
                return _f.getOWLInverseFunctionalObjectPropertyAxiom(objectProperty, notNullOWLAnnotations(annotations));
            case 2:
                return _f.getOWLSymmetricObjectPropertyAxiom(objectProperty, notNullOWLAnnotations(annotations));
            case 3:
                return _f.getOWLTransitiveObjectPropertyAxiom(objectProperty, notNullOWLAnnotations(annotations));
            case 4:
                return _f.getOWLReflexiveObjectPropertyAxiom(objectProperty, notNullOWLAnnotations(annotations));
            case 5:
                return _f.getOWLIrreflexiveObjectPropertyAxiom(objectProperty, notNullOWLAnnotations(annotations));
            case 6:
                return _f.getOWLAsymmetricObjectPropertyAxiom(objectProperty, notNullOWLAnnotations(annotations));
            default:
                throw new IllegalStateException();
        }
    }

    private OWLObjectPropertyDomainAxiom parseObjectPropertyDomain(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLClassExpression domain=parseDescription();
        return _f.getOWLObjectPropertyDomainAxiom(objectProperty,domain, notNullOWLAnnotations(annotations));
    }

    private OWLObjectPropertyAssertionAxiom parseObjectPropertyMember(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLIndividual sourceIndividual=parseIndividual();
        OWLIndividual targetIndividual=parseIndividual();
        return _f.getOWLObjectPropertyAssertionAxiom(objectProperty,sourceIndividual,targetIndividual, notNullOWLAnnotations(annotations));
    }

    private OWLNegativeObjectPropertyAssertionAxiom parseNegativeObjectPropertyMember(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLIndividual sourceIndividual=parseIndividual();
        OWLIndividual targetIndividual=parseIndividual();
        return _f.getOWLNegativeObjectPropertyAssertionAxiom(objectProperty,sourceIndividual,targetIndividual, notNullOWLAnnotations(annotations));
    }

    private OWLObjectPropertyRangeAxiom parseObjectPropertyRange(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression objectProperty=parseObjectPropertyExpression();
        OWLClassExpression range=parseDescription();
        return _f.getOWLObjectPropertyRangeAxiom(objectProperty,range, notNullOWLAnnotations(annotations));
    }

    private OWLSameIndividualAxiom parseSameIndividualCore(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        Set<OWLIndividual> individuals=new LinkedHashSet<OWLIndividual>();
        while (_tokenizer.ttype!=']')
            individuals.add(parseIndividual());
        return _f.getOWLSameIndividualAxiom(individuals, notNullOWLAnnotations(annotations));
    }

    private OWLSubClassOfAxiom parseSubClassOf(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLClassExpression subDescription=parseDescription();
        OWLClassExpression superDescription=parseDescription();
        return _f.getOWLSubClassOfAxiom(subDescription,superDescription, notNullOWLAnnotations(annotations));
    }

    private OWLSubDataPropertyOfAxiom parseSubDataPropertyOf(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLDataPropertyExpression subDataProperty=parseDataProperty();
        OWLDataPropertyExpression superDataProperty=parseDataProperty();
        return _f.getOWLSubDataPropertyOfAxiom(subDataProperty,superDataProperty, notNullOWLAnnotations(annotations));
    }
    
    private OWLSubObjectPropertyOfAxiom parseSubObjectPropertyOf(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression subObjectProperty=parseObjectPropertyExpression();
        OWLObjectPropertyExpression superObjectProperty=parseObjectPropertyExpression();
        return _f.getOWLSubObjectPropertyOfAxiom(subObjectProperty,superObjectProperty, notNullOWLAnnotations(annotations));
    }
    
    private OWLAxiom parseSubPropertyChainOf(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        List<OWLObjectPropertyExpression> subObjectProperties=new ArrayList<OWLObjectPropertyExpression>();
        if (_tokenizer.ttype=='[') {
            nextToken();
            if(!"objectPropertyChain".equalsIgnoreCase(_tokenizer.sval)) throw createException("Invalid token "+_tokenizer.sval+". objectPropertyChain expected");
            nextToken();
            while (_tokenizer.ttype!=']') {
                OWLObjectPropertyExpression subObjectProperty=parseObjectPropertyExpression();
                subObjectProperties.add(subObjectProperty);
            }
            nextToken();
        }
        
        OWLObjectPropertyExpression superObjectProperty=parseObjectPropertyExpression();
        return _f.getOWLSubPropertyChainOfAxiom(subObjectProperties, superObjectProperty, notNullOWLAnnotations(annotations));
    }

    private OWLInverseObjectPropertiesAxiom parseInverseObjectProperties(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression first=parseObjectPropertyExpression();
        OWLObjectPropertyExpression second=parseObjectPropertyExpression();
        return _f.getOWLInverseObjectPropertiesAxiom(first,second, notNullOWLAnnotations(annotations));
    }
    
    private OWLAxiom parseRule(Set<OWLAnnotation> annotations) throws InternalParserException {
        nextToken();
        
        IRI ruleUri = null;
        if(_tokenizer.ttype == StreamTokenizer.TT_WORD){
            ruleUri = parseIRI(_tokenizer.sval);
        }
        
        checkTokenType(_tokenizer, '[');
        nextToken();
        Set<SWRLAtom> antecedent = new LinkedHashSet<SWRLAtom>();
        while (_tokenizer.ttype!=']') {
            antecedent.add(parseRuleAtom());
        }
        nextToken();
        
        checkTokenType(_tokenizer, '[');
        nextToken();
        Set<SWRLAtom> consequent = new LinkedHashSet<SWRLAtom>();
        while (_tokenizer.ttype!=']') {
            consequent.add(parseRuleAtom());
        }
        nextToken();
        
        if(ruleUri == null)
            return _f.getSWRLRule(antecedent, consequent);
        else
            return _f.getSWRLRule(ruleUri, antecedent, consequent);
    }

    private IRI parseIRI(String sval) throws InternalParserException {
        String uri=_namespaces.expandString(_tokenizer.sval);
        nextToken();
        return OWLUtilities.toIRI(uri);
    }

    private SWRLAtom parseRuleAtom() throws InternalParserException {
        checkTokenType(_tokenizer,'[');
        nextToken();
        SWRLAtom result;
        if ("swrlDataValuedProperty".equalsIgnoreCase(_tokenizer.sval))
            result=parseSWRLDataValueProperty();
        else if ("swrlDifferentFrom".equalsIgnoreCase(_tokenizer.sval))
            result=parseSWRLDifferentFrom();
        else if ("swrlObjectProperty".equalsIgnoreCase(_tokenizer.sval))
            result=parseSWRLObjectProperty();
        else if("swrlSameAs".equalsIgnoreCase(_tokenizer.sval))
            result=parseSWRLSameAs();
        else if("swrlClass".equalsIgnoreCase(_tokenizer.sval))
            result=parseSWRLClass();
        else if("swrlDataRange".equalsIgnoreCase(_tokenizer.sval))
            result=parseSWRLDataRange();
        else if("swrlBuiltIn".equalsIgnoreCase(_tokenizer.sval))
            result=parseSWRLBuiltIn();
        else
            throw createException("Unknown SWRLAtom '"+_tokenizer.sval+"'.");
        checkTokenType(_tokenizer,']');
        nextToken();
        
        
        return result;
        
    }

    /**
     * @return
     * @throws InternalParserException 
     */
    private SWRLBuiltInAtom parseSWRLBuiltIn() throws InternalParserException {
        nextToken();
        IRI builtIn = parseIRI(_tokenizer.sval);
        List<SWRLDArgument> args = new LinkedList<SWRLDArgument>();
        while(_tokenizer.ttype != ']'){
            args.add(parseSWRLAtomDObject());
        }
        return _f.getSWRLBuiltInAtom(builtIn, args);
    }

    /**
     * @return
     * @throws InternalParserException 
     */
    private SWRLDataRangeAtom parseSWRLDataRange() throws InternalParserException {
        nextToken();
        OWLDataRange dataRange = parseDataRange();
        SWRLDArgument dAtom = parseSWRLAtomDObject();
        return _f.getSWRLDataRangeAtom(dataRange, dAtom);
    }

    /**
     * @return
     * @throws InternalParserException 
     */
    private SWRLClassAtom parseSWRLClass() throws InternalParserException {
        nextToken();
        OWLClassExpression classExpression = parseDescription();
        SWRLIArgument iAtom = parseSWRLAtomIObject();
        return _f.getSWRLClassAtom(classExpression, iAtom);
    }

    /**
     * @return
     * @throws InternalParserException 
     */
    private SWRLSameIndividualAtom parseSWRLSameAs() throws InternalParserException {
        nextToken();
        SWRLIArgument iAtom0 = parseSWRLAtomIObject();
        SWRLIArgument iAtom1 = parseSWRLAtomIObject();
        return _f.getSWRLSameIndividualAtom(iAtom0, iAtom1);
    }

    private SWRLObjectPropertyAtom parseSWRLObjectProperty() throws InternalParserException {
        nextToken();
        OWLObjectPropertyExpression property = parseObjectPropertyExpression();
        SWRLIArgument iAtom0 = parseSWRLAtomIObject();
        SWRLIArgument iAtom1 = parseSWRLAtomIObject();
        return _f.getSWRLObjectPropertyAtom(property, iAtom0, iAtom1);
    }

    private SWRLDifferentIndividualsAtom parseSWRLDifferentFrom() throws InternalParserException {
        nextToken();
        SWRLIArgument iAtom0 = parseSWRLAtomIObject();
        SWRLIArgument iAtom1 = parseSWRLAtomIObject();
        return _f.getSWRLDifferentIndividualsAtom(iAtom0, iAtom1);
    }

    private SWRLDataPropertyAtom parseSWRLDataValueProperty() throws InternalParserException {
        nextToken();
        OWLDataProperty dataProperty = parseDataProperty();
        SWRLIArgument individualObject = parseSWRLAtomIObject();
        SWRLDArgument dataObject = parseSWRLAtomDObject();
        return _f.getSWRLDataPropertyAtom((OWLDataPropertyExpression)dataProperty, individualObject, dataObject);
    }

    private SWRLDArgument parseSWRLAtomDObject() throws InternalParserException {
        SWRLDArgument result = null;
        if(_tokenizer.ttype == '['){
            nextToken();
            if("swrlVariable".equalsIgnoreCase(_tokenizer.sval))
                result = parseSWRLVarable();
            else 
                throw createException("Unexpected SWRLAtom '"+_tokenizer.sval+"'. Expected \"swrlVariable\"");
            nextToken();
        }else if(_tokenizer.ttype == '"'){
            return _f.getSWRLLiteralArgument(parseConstant());
        }else
            throw createException("Unexpected token "+String.valueOf((char)_tokenizer.ttype)+". Expected \'[\'  or Constant");
        
        return result;
    }

    private SWRLIArgument parseSWRLAtomIObject() throws InternalParserException {
        SWRLIArgument result = null;
        if(_tokenizer.ttype == '['){
            nextToken();
            if("swrlVariable".equalsIgnoreCase(_tokenizer.sval))
                result = parseSWRLVarable();            
            else
                throw createException("Unexpected SWRLAtom '"+_tokenizer.sval+"'. Expect \"swrlVariable\"");
            nextToken();
        }else if(_tokenizer.ttype == StreamTokenizer.TT_WORD){
            result =  _f.getSWRLIndividualArgument(parseIndividual());
        }else
            throw createException("Unexpected token "+String.valueOf((char)_tokenizer.ttype)+". Expected \'[\'  or Individual URI");
        
        return result;
    }
    
    private SWRLVariable parseSWRLVarable() throws InternalParserException{
        nextToken();
        return _f.getSWRLVariable(parseIRI(_tokenizer.sval));
    }

    private OWLAxiom parseAxiom() throws InternalParserException {
        checkTokenType(_tokenizer,'[');
        nextToken();
        
        Set<OWLAnnotation> annotations = null;
        if(_tokenizer.ttype=='{'){
            annotations = parseOWLAnnotations(null);
            nextToken();
        }
        
        OWLAxiom result;
        if ("dataAnnotation".equalsIgnoreCase(_tokenizer.sval) 
                || "objectAnnotation".equalsIgnoreCase(_tokenizer.sval) 
                || "namedIndividualAnnotation".equalsIgnoreCase(_tokenizer.sval) 
                || "classAnnotation".equalsIgnoreCase(_tokenizer.sval)  
                || "datatypeAnnotation".equalsIgnoreCase(_tokenizer.sval) 
                || "annotationAnnotationProperty".equalsIgnoreCase(_tokenizer.sval) 
                || "annotationIRI".equalsIgnoreCase(_tokenizer.sval)
                || "annotationAnonymousIndividual".equalsIgnoreCase(_tokenizer.sval)
                || "annotationAnnotation".equalsIgnoreCase(_tokenizer.sval))
            result=parseAnnotation(annotations);
        else if ("dataDeclaration".equalsIgnoreCase(_tokenizer.sval) || "objectDeclaration".equalsIgnoreCase(_tokenizer.sval) || "classDeclaration".equalsIgnoreCase(_tokenizer.sval) || "individualDeclaration".equalsIgnoreCase(_tokenizer.sval)  || "classDeclaration".equalsIgnoreCase(_tokenizer.sval) || "datatypeDeclaration".equalsIgnoreCase(_tokenizer.sval) || "annotationDeclaration".equalsIgnoreCase(_tokenizer.sval))
            result=parseDeclaration(annotations);
        else if ("classMember".equalsIgnoreCase(_tokenizer.sval))
            result=parseClassMember(annotations);
        else if ("dataFunctional".equalsIgnoreCase(_tokenizer.sval))
            result=parseDataPropertyAttribute(annotations);
        else if ("dataDomain".equalsIgnoreCase(_tokenizer.sval))
            result=parseDataPropertyDomain(annotations);
        else if ("dataMember".equalsIgnoreCase(_tokenizer.sval))
            result=parseDataPropertyMember(annotations);
        else if ("negativeDataMember".equalsIgnoreCase(_tokenizer.sval))
            result=parseNegativeDataPropertyMember(annotations);
        else if ("dataRange".equalsIgnoreCase(_tokenizer.sval))
            result=parseDataPropertyRange(annotations);
        else if ("different".equalsIgnoreCase(_tokenizer.sval))
            result=parseDifferentIndividuals(annotations);
        else if ("disjoint".equalsIgnoreCase(_tokenizer.sval))
            result=parseDisjointClasses(annotations);
        else if ("disjointUnion".equalsIgnoreCase(_tokenizer.sval))
            result=parseDisjointUnion(annotations);
        else if ("equivalent".equalsIgnoreCase(_tokenizer.sval))
            result=parseEquivalentClasses(annotations);
        else if ("dataEquivalent".equalsIgnoreCase(_tokenizer.sval))
            result=parseEquivalentDataProperties(annotations);
        else if ("dataDisjoint".equalsIgnoreCase(_tokenizer.sval))
            result=parseDisjointDataProperties(annotations);
        else if ("objectEquivalent".equalsIgnoreCase(_tokenizer.sval))
            result=parseEquivalentObjectProperties(annotations);
        else if ("objectDisjoint".equalsIgnoreCase(_tokenizer.sval))
            result=parseDisjointObjectProperties(annotations);
        else if ("objectFunctional".equalsIgnoreCase(_tokenizer.sval) || "objectInverseFunctional".equalsIgnoreCase(_tokenizer.sval) || "objectSymmetric".equalsIgnoreCase(_tokenizer.sval) || "objectTransitive".equalsIgnoreCase(_tokenizer.sval) || "objectReflexive".equalsIgnoreCase(_tokenizer.sval) || "objectIrreflexive".equalsIgnoreCase(_tokenizer.sval) || "objectAsymmetric".equalsIgnoreCase(_tokenizer.sval))
            result=parseObjectPropertyAttribute(annotations);
        else if ("objectDomain".equalsIgnoreCase(_tokenizer.sval))
            result=parseObjectPropertyDomain(annotations);
        else if ("objectMember".equalsIgnoreCase(_tokenizer.sval))
            result=parseObjectPropertyMember(annotations);
        else if ("negativeObjectMember".equalsIgnoreCase(_tokenizer.sval))
            result=parseNegativeObjectPropertyMember(annotations);
        else if ("objectRange".equalsIgnoreCase(_tokenizer.sval))
            result=parseObjectPropertyRange(annotations);
        else if ("same".equalsIgnoreCase(_tokenizer.sval))
            result=parseSameIndividualCore(annotations);
        else if ("subClassOf".equalsIgnoreCase(_tokenizer.sval))
            result=parseSubClassOf(annotations);
        else if ("subDataPropertyOf".equalsIgnoreCase(_tokenizer.sval))
            result=parseSubDataPropertyOf(annotations);
        else if ("subObjectPropertyOf".equalsIgnoreCase(_tokenizer.sval))
            result=parseSubObjectPropertyOf(annotations);
        else if ("subPropertyChainOf".equalsIgnoreCase(_tokenizer.sval))
            result=parseSubPropertyChainOf(annotations);
        else if ("objectInverse".equalsIgnoreCase(_tokenizer.sval))
            result=parseInverseObjectProperties(annotations);
        else if ("rule".equalsIgnoreCase(_tokenizer.sval))
            result=parseRule(annotations);
        else if ("annotationPropertyRange".equalsIgnoreCase(_tokenizer.sval))
            result=parseAnnotationPropertyRange(annotations);
        else if ("annotationPropertyDomain".equalsIgnoreCase(_tokenizer.sval))
            result=parseAnnotationPropertyDomain(annotations);
        else if ("subAnnotationPropertyOf".equalsIgnoreCase(_tokenizer.sval))
            result=parseSubAnnotationProperty(annotations);
        else if ("datatypeDefinition".equalsIgnoreCase(_tokenizer.sval))
            result=parseDatatypeDefinition(annotations);
        else if ("hasKey".equalsIgnoreCase(_tokenizer.sval))
            result=parseHasKey(annotations);
        else
            throw createException("Unknown axiom '"+_tokenizer.sval+"'.");
        checkTokenType(_tokenizer,']');
        nextToken();
        
        
        return result;
    }



    private static Set<OWLAnnotation> notNullOWLAnnotations(Set<OWLAnnotation> annotations){
        if(annotations==null)return Collections.emptySet();
        else return annotations;
    }
}
