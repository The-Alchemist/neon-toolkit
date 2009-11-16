/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.syntax.manchester;

import java.util.Iterator;
import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
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
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLStringLiteral;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTypedLiteral;

import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.visitors.OWLKAON2VisitorAdapter;

/**
 * KAON2 visitor which returns for each ontology element an array containing the element's string representations with and without namespaces. 
 *      URI 
 *      local name 
 *      QName
 *      
 * @author Michael
 */
public class ManchesterSyntaxVisitor extends OWLKAON2VisitorAdapter {

    private final OWLClass OWL_CLASS;
    private static final String RDFS_LABEL = OWLNamespaces.RDFS_NS + "label"; //$NON-NLS-1$

    private static final String SPACE = " "; //$NON-NLS-1$

    private final OWLNamespaces _namespaces;
    private final OWLModel _owlModel;
    private final String _language;
    
    private String getProjectId() throws NeOnCoreException {
        return _owlModel.getProjectId();
    }

    public ManchesterSyntaxVisitor(OWLModel owlModel) {
        this(owlModel, "en"); //$NON-NLS-1$
    }

    public ManchesterSyntaxVisitor(OWLModel owlModel, String language) {
        _owlModel = owlModel;
        _language = language;

        OWLNamespaces ns = OWLNamespaces.INSTANCE;

        if (owlModel != null) {
            try {
                ns = owlModel.getNamespaces();
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
        }
        _namespaces = ns;
        try {
            OWL_CLASS = OWLModelFactory.getOWLDataFactory(getProjectId()).getOWLClass(OWLUtilities.toURI(OWLConstants.OWL_THING_URI));
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String getURI(String uri) {
        return new StringBuilder("<").append(uri).append(">").toString(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected static String getLocalName(String uri) {
        return OWLNamespaces.guessLocalName(uri);
    }

    protected String getQName(String uri) {
        String string = _namespaces.abbreviateAsNamespace(uri);
        if (_namespaces.getAbbreviationPrefix(uri) == null && uri.equals(string)) {
            return getURI(uri);
        } else {
            return string;
        }
    }

    protected String getLabel(String uri) {
        String result = getQName(uri);

        if (_owlModel == null || _language == null) {
            return result;
        }

        try {
            Set<OWLAnnotationValue> annotations = _owlModel.getAnnotations(uri, RDFS_LABEL);
            String label = getLocalizedLiteral(annotations);
            if(label != null) result = label;
        } catch (NeOnCoreException e) {
            // log.error()
        }
        return result;
    }
    
    private String getLocalizedLiteral(Set<OWLAnnotationValue> annotations){
        String result = null;
        for (OWLAnnotationValue value: annotations) {
            if (value instanceof OWLStringLiteral) {
                OWLStringLiteral untypedConstant = (OWLStringLiteral)value;
                String lang = untypedConstant.getLang();
                if (_language.equals(lang)) {
                    result = untypedConstant.getLiteral();
                    break;
                }
            }
        }
        return result;
    }

    private static String escapeLiteral(String literal) {
        String y;
        y = literal.replaceAll("\\\\", "\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
        y = y.replaceAll(new Character('"').toString(), "\\\\\""); // MER this is strange should actually only be "\\\"" resulting in a backslash followed by a //$NON-NLS-1$
                                                                   // double quote
        return y;
    }

    public static String quoteLiteral(String literal) {
        StringBuilder y = new StringBuilder("\""); //$NON-NLS-1$
        y = y.append(escapeLiteral(literal)).append("\""); //$NON-NLS-1$
        return y.toString();
    }

    protected String[] createStandardArray(String uri) {
        return new String[] {getURI(uri), getLocalName(uri), getQName(uri), getLabel(uri)};
    }

    protected String[] createSingle(String value) {
        return new String[] {value, value, value, value};
    }

    private String[] addPrefixToArray(String prefix, String[] array) {
        String[] newArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = new StringBuilder(prefix).append(SPACE).append(array[i]).toString();
        }
        return newArray;
    }

    private String[] appendArrays(String[]... array) {
        int len = array[0].length;

        String[] newArray = new String[len];
        for (int i = 0; i < len; i++) {
            newArray[i] = array[0][i];
        }

        int j = 0;
        for (String[] strings: array) {
            if (j == 0) {
                j = 1;
                continue;
            }
            for (int i = 0; i < len; i++) {
                newArray[i] = new StringBuilder(newArray[i]).append(SPACE).append(strings[i]).toString();
            }
        }
        return newArray;
    }

    private String[] bracketArray(String open, String[] array, String close) {
        String[] newArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = new StringBuilder(open).append(array[i]).append(close).toString();
        }
        return newArray;
    }

    private String[] bracketArrayIfNeeded(OWLPropertyRange description, String open, String[] array, String close) {
        if (description instanceof OWLClass) {
            return array;
        } else if (description instanceof OWLObjectOneOf) {
            return array;
        } else if (description instanceof OWLObjectComplementOf) {
            return array;
        }else if (description instanceof OWLDataRange){
            return array;
        } else {
            return bracketArray(open, array, close);
        }
    }

    /**
     * @param first
     * @param descriptions
     * @return
     */
    private String[] createList(String[] first, Set<OWLClassExpression> descriptions) {
        String[] y = first;
        
        if(descriptions.size()>1) {
            for (OWLClassExpression desc: descriptions) {
                if(desc instanceof OWLClass) {
                    y = appendArrays(y, (String[]) desc.accept(this));  
                } else {
                    y = appendArrays(y, bracketArray("(", (String[]) desc.accept(this), ")"));  //$NON-NLS-1$//$NON-NLS-2$
                }
            }
        } else {
            for (OWLClassExpression desc: descriptions) {
                y = appendArrays(y, (String[]) desc.accept(this));
            }
        }
        return y;
    }


    @Override
    public String[] visit(OWLOntology object) {
        String uri = OWLUtilities.toString(object.getOntologyID());
        return createStandardArray(uri);
    }

    @Override
    public Object visit(OWLNamedIndividual object) {
        String uri = object.getURI().toString();
        return createStandardArray(uri);
    }
    
    @Override
    public Object visit(OWLAnonymousIndividual object) {
        String id = object.getID().toString();
        try {
            String label = getLocalizedLiteral(_owlModel.getAnnotations(object, RDFS_LABEL));
            return new String[] {getURI(id), getLocalName(id), getQName(id), (label!=null)?label:getQName(id)};
        } catch (NeOnCoreException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @Override
    public String[] visit(OWLDataProperty object) {
        String uri = object.getURI().toString();
        return createStandardArray(uri);
    }

    @Override
    public String[] visit(OWLObjectProperty object) {
        String uri = object.getURI().toString();
        return createStandardArray(uri);
    }

    @Override
    public String[] visit(OWLAnnotationProperty object) {
        String uri = object.getURI().toString();
        return createStandardArray(uri);
    }

    @Override
    public String[] visit(OWLDatatype object) {
        String uri = object.getURI().toString();
        return createStandardArray(uri);
    }

    @Override
    public String[] visit(OWLClass object) {
        String uri = object.getURI().toString();
        return createStandardArray(uri);
    }

    @Override
    public String[] visit(OWLObjectInverseOf object) {
        OWLObjectPropertyExpression prop = object.getInverse();
        return addPrefixToArray(ManchesterSyntaxConstants.INVERSE_OF, (String[]) prop.accept(this));
    }

    @Override
    public String[] visit(OWLLiteral object) {
        
        if (!object.isTyped()) {
            OWLStringLiteral untypedConstant = (OWLStringLiteral)object;
            String string = untypedConstant.getLiteral();
            String language = untypedConstant.getLang();
            if (language != null) {
                return createSingle(new StringBuilder(quoteLiteral(string)).append("@").append(language).toString()); //$NON-NLS-1$
            } else {
                return createSingle(quoteLiteral(string));
            }
        } else {
            OWLTypedLiteral typedConstant = (OWLTypedLiteral)object;
            OWLDatatype datatype = typedConstant.getDatatype();
            if ((OWLConstants.XSD_LONG).equals(datatype.getURI().toString())) {
                return createSingle(typedConstant.getLiteral());
            } else if ((OWLConstants.XSD_DOUBLE).equals(datatype.getURI().toString())) {
                String literal = typedConstant.getLiteral();
                if (!literal.contains(".") && !literal.contains("E") && !literal.contains("e")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    literal = literal + ".0"; //$NON-NLS-1$
                }
                return createSingle(literal);
            }

            String literal = typedConstant.getLiteral();
            String xsdTypeURI = datatype.getURI().toString();
            return addPrefixToArray(new StringBuilder(quoteLiteral(literal)).append("^^").toString(), createStandardArray(xsdTypeURI)); //$NON-NLS-1$
        }
    }

    @Override
    public String[] visit(OWLDataAllValuesFrom object) {
        OWLDataPropertyExpression exp = object.getProperty();
        String[] dataProp = (String[]) exp.accept(this);

        // this seems to be missed in the Manchester Syntax spec
        // Iterator<DataPropertyExpression> iterator = exps.iterator();
        // iterator.next();
        // while(iterator.hasNext()) {
        // DataPropertyExpression dataPropertyExpression = (DataPropertyExpression) iterator.next();
        // String[] temp = (String[])dataPropertyExpression.accept(this);
        // result = appendArrays(result, temp);
        // }

        String[] dataRange = (String[]) object.getFiller().accept(this);

        return appendArrays(dataProp, addPrefixToArray(ManchesterSyntaxConstants.ALL_VALUES_FROM, dataRange));
    }

    @Override
    public String[] visit(OWLDataOneOf object) {
        Set<OWLLiteral> descs = object.getValues();
        Iterator<OWLLiteral> iter = descs.iterator();
        String[] result = (String[]) iter.next().accept(this);

        while (iter.hasNext()) {
            String[] temp = (String[]) iter.next().accept(this);
            result = appendArrays(result, createSingle(","), temp); //$NON-NLS-1$
        }

        return bracketArray("{", result, "}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public String[] visit(OWLDataSomeValuesFrom object) {
        OWLDataPropertyExpression exp = object.getProperty();
        String[] dataProp = (String[]) exp.accept(this);

        // this seems to be missed in the Manchester Syntax spec
        // Iterator<DataPropertyExpression> iterator = exps.iterator();
        // iterator.next();
        // while(iterator.hasNext()) {
        // DataPropertyExpression dataPropertyExpression = (DataPropertyExpression) iterator.next();
        // String[] temp = (String[])dataPropertyExpression.accept(this);
        // dataProp = appendArrays(dataProp, temp);
        // }

        String[] dataRange = (String[]) object.getFiller().accept(this);

        return appendArrays(dataProp, addPrefixToArray(ManchesterSyntaxConstants.SOME_VALUES_FROM, dataRange));
    }

    @Override
    public String[] visit(OWLDataCardinalityRestriction object) {
        String cardinalityType = ""; //$NON-NLS-1$
        String cardinality = ((Integer) object.getCardinality()).toString();
        if (object instanceof OWLDataMinCardinality) {
            cardinalityType = ManchesterSyntaxConstants.MIN;
        } else if (object instanceof OWLDataMaxCardinality) {
            cardinalityType = ManchesterSyntaxConstants.MAX;
        } else if (object instanceof OWLDataExactCardinality) {
            cardinalityType = ManchesterSyntaxConstants.CARDINALITY;
        } else {
            throw new IllegalStateException();
        }

        String[] dataProp = (String[]) object.getProperty().accept(this);

        String[] temp = appendArrays(dataProp, addPrefixToArray(cardinalityType, createSingle(cardinality)));

        if (object.getFiller() != null) {
            return appendArrays(temp, (String[]) object.getFiller().accept(this));
        } else {
            return temp;
        }
    }

    @Override
    public String[] visit(OWLDataHasValue object) {
        String[] dataProp = (String[]) object.getProperty().accept(this);
        String[] literalValue = (String[]) object.getValue().accept(this);

        return appendArrays(dataProp, addPrefixToArray(ManchesterSyntaxConstants.HAS_VALUE, literalValue));
    }

    @Override
    public String[] visit(OWLObjectSomeValuesFrom object) {
        String[] objProp = (String[]) object.getProperty().accept(this);
        String[] desc = (String[]) object.getFiller().accept(this);

        desc = bracketArrayIfNeeded(object.getFiller(), "(", desc, ")"); //$NON-NLS-1$ //$NON-NLS-2$

        return appendArrays(objProp, createSingle(ManchesterSyntaxConstants.SOME_VALUES_FROM), desc);
    }

    @Override
    public String[] visit(OWLObjectAllValuesFrom object) {
        String[] objProp = (String[]) object.getProperty().accept(this);
        String[] desc = (String[]) object.getFiller().accept(this);

        desc = bracketArrayIfNeeded(object.getFiller(), "(", desc, ")"); //$NON-NLS-1$ //$NON-NLS-2$

        return appendArrays(objProp, addPrefixToArray(ManchesterSyntaxConstants.ALL_VALUES_FROM, desc));
    }

    @Override
    public String[] visit(OWLObjectCardinalityRestriction object) {
        String cardinalityType = ""; //$NON-NLS-1$
        String cardinality = ((Integer) object.getCardinality()).toString();
        if (object instanceof OWLObjectMinCardinality) {
            cardinalityType = ManchesterSyntaxConstants.MIN;
        } else if (object instanceof OWLObjectMaxCardinality) {
            cardinalityType = ManchesterSyntaxConstants.MAX;
        } else if (object instanceof OWLObjectExactCardinality) {
            cardinalityType = ManchesterSyntaxConstants.CARDINALITY;
        } else {
            throw new IllegalStateException();
        }

        String[] objProp = (String[]) object.getProperty().accept(this);
        String[] temp = appendArrays(objProp, addPrefixToArray(cardinalityType, createSingle(cardinality)));

        if (object.getFiller() != null && !object.getFiller().equals(OWL_CLASS)) {
            String[] desc = (String[]) object.getFiller().accept(this);

            desc = bracketArrayIfNeeded(object.getFiller(), "(", desc, ")"); //$NON-NLS-1$ //$NON-NLS-2$

            return appendArrays(temp, desc);

        } else {
            return temp;
        }
    }

    @Override
    public String[] visit(OWLObjectOneOf object) {
        Set<OWLIndividual> descs = object.getIndividuals();
        Iterator<OWLIndividual> iter = descs.iterator();
        String[] result = (String[]) iter.next().accept(this);

        while (iter.hasNext()) {
            String[] temp = (String[]) iter.next().accept(this);
            result = appendArrays(result, createSingle(","), temp); //$NON-NLS-1$
        }

        return bracketArray("{", result, "}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public String[] visit(OWLObjectHasSelf object) {
        String[] objProp = (String[]) object.getProperty().accept(this);

        return appendArrays(objProp, createSingle("Self")); //$NON-NLS-1$
    }

    @Override
    public String[] visit(OWLObjectHasValue object) {
        String[] objProp = (String[]) object.getProperty().accept(this);
        String[] ind = (String[]) object.getValue().accept(this);
        return appendArrays(objProp, addPrefixToArray(ManchesterSyntaxConstants.HAS_VALUE, ind));
    }

    @Override
    public String[] visit(OWLObjectComplementOf object) {
        String[] desc = (String[]) object.getOperand().accept(this);

        desc = bracketArrayIfNeeded(object.getOperand(), "(", desc, ")"); //$NON-NLS-1$ //$NON-NLS-2$

        return addPrefixToArray(ManchesterSyntaxConstants.NOT, desc);
    }

    @Override
    public String[] visit(OWLObjectUnionOf object) {
        Set<OWLClassExpression> descs = object.getOperands();
        Iterator<OWLClassExpression> iter = descs.iterator();
        OWLClassExpression desc = iter.next();
        String[] result = (String[]) desc.accept(this);

        result = bracketArrayIfNeeded(desc, "(", result, ")"); //$NON-NLS-1$ //$NON-NLS-2$

        while (iter.hasNext()) {
            desc = iter.next();
            String[] temp = (String[]) desc.accept(this);

            temp = bracketArrayIfNeeded(desc, "(", temp, ")"); //$NON-NLS-1$ //$NON-NLS-2$

            result = appendArrays(appendArrays(result, createSingle(ManchesterSyntaxConstants.OR)), temp);
        }
        return result;
    }
    

    @Override
    public String[] visit(OWLDataUnionOf object) {
        Set<OWLDataRange> descs = object.getOperands();
        Iterator<OWLDataRange> iter = descs.iterator();
        OWLDataRange desc = iter.next();
        String[] result = (String[]) desc.accept(this);

        result = bracketArrayIfNeeded(desc, "(", result, ")"); //$NON-NLS-1$ //$NON-NLS-2$

        while (iter.hasNext()) {
            desc = iter.next();
            String[] temp = (String[]) desc.accept(this);

            temp = bracketArrayIfNeeded(desc, "(", temp, ")"); //$NON-NLS-1$ //$NON-NLS-2$

            result = appendArrays(appendArrays(result, createSingle(ManchesterSyntaxConstants.OR)), temp);
        }
        return result;
    }

    @Override
    public String[] visit(OWLObjectIntersectionOf object) {
        Set<OWLClassExpression> descs = object.getOperands();
        Iterator<OWLClassExpression> iter = descs.iterator();
        OWLClassExpression desc = iter.next();
        String[] result = (String[]) desc.accept(this);

        result = bracketArrayIfNeeded(desc, "(", result, ")"); //$NON-NLS-1$ //$NON-NLS-2$

        while (iter.hasNext()) {
            desc = iter.next();
            String[] temp = (String[]) desc.accept(this);

            temp = bracketArrayIfNeeded(desc, "(", temp, ")"); //$NON-NLS-1$ //$NON-NLS-2$

            result = appendArrays(appendArrays(result, createSingle(ManchesterSyntaxConstants.AND)), temp);
        }
        return result;
    }

    @Override
    public Object visit(OWLDataComplementOf object) {
        String[] range = (String[]) object.getDataRange().accept(this);

        return addPrefixToArray(ManchesterSyntaxConstants.NOT, range);
    }

    @Override
    public Object visit(OWLDatatypeRestriction object) {
        String[] range = (String[]) object.getDatatype().accept(this);
        
        Set<OWLFacetRestriction> facetRestrictions = object.getFacetRestrictions();
        if (facetRestrictions.size() > 1) {
            // TODO Migration
            throw new UnsupportedOperationException();
        }
        OWLFacetRestriction facetRestriction = facetRestrictions.iterator().next();
        String[] values = createSingle((String)facetRestriction.getFacetValue().accept(new ManchesterSyntaxVisitorForConstantsInFacettes()));

        String facetString = ManchesterSyntaxConstants.getFacetString(facetRestriction.getFacet());
        return appendArrays(range, bracketArray("[", addPrefixToArray(facetString, values), "]")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public Object visit(OWLSubClassOfAxiom object) {
        String[] subClass = (String[]) object.getSubClass().accept(this);
        String[] superClass = (String[]) object.getSuperClass().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.CLASS), subClass, createSingle(ManchesterSyntaxConstants.SUBCLASSOF), superClass);
    }
    
    @Override
    public Object visit(OWLObjectMinCardinality desc) {
        String[] property = (String[]) desc.getProperty().accept(this);
        String[] card = createSingle(String.valueOf(desc.getCardinality()));
        String[] filler = (String[]) desc.getFiller().accept(this);
        
        return appendArrays(property, card, filler);
    }
    
    @Override
    public Object visit(OWLObjectMaxCardinality desc) {
        String[] property = (String[]) desc.getProperty().accept(this);
        String[] card = createSingle(String.valueOf(desc.getCardinality()));
        String[] filler = (String[]) desc.getFiller().accept(this);
        
        return appendArrays(property, card, filler);
    }
    
    @Override
    public Object visit(OWLObjectExactCardinality desc) {
        String[] property = (String[]) desc.getProperty().accept(this);
        String[] card = createSingle(String.valueOf(desc.getCardinality()));
        String[] filler = (String[]) desc.getFiller().accept(this);
        
        return appendArrays(property, card, filler);
    }

    @Override
    public Object visit(OWLEquivalentClassesAxiom object) {
        String[] classes = createSingle(ManchesterSyntaxConstants.EQUIVALENTCLASSES);
        classes = createList(classes,object.getClassExpressions());

        return classes;
    }

    @Override
    public Object visit(OWLDisjointClassesAxiom object) {
        String[] classes = createSingle(ManchesterSyntaxConstants.DISJOINTCLASSES);

        classes = createList(classes, object.getClassExpressions());

        return classes;
    }

    @Override
    public Object visit(OWLDisjointUnionAxiom object) {
//            String[] clazz = (String[]) object.getOWLClass().accept(this);
//
//        String[] array = appendArrays(createSingle(ManchesterSyntaxConstants.CLASS), clazz, createSingle(ManchesterSyntaxConstants.DISJOINTUNIONOF));
//
//        for (Description desc: object.getDescriptions()) {
//            array = appendArrays(array, (String[]) desc.accept(this));
//        }
//
//        return array;
    
        String[] disjointUnion = (String[]) object.getOWLClass().accept(this);
        String[] prefix =appendArrays(createSingle(ManchesterSyntaxConstants.CLASS), disjointUnion, createSingle(ManchesterSyntaxConstants.DISJOINTUNIONOF));
        
        String[] classes = createList(prefix, object.getClassExpressions());
        return classes;
    }

    @Override
    public Object visit(OWLDataPropertyCharacteristicAxiom object) {
        String[] property = (String[]) object.getProperty().accept(this);

        String attributeString = ManchesterSyntaxConstants.getPropertyAttributeString(object);

        return appendArrays(createSingle(ManchesterSyntaxConstants.DATAPROPERTY), property, createSingle(ManchesterSyntaxConstants.CHARACTERISTIC), createSingle(attributeString));
    }

    @Override
    public Object visit(OWLDataPropertyDomainAxiom object) {
        String[] property = (String[]) object.getProperty().accept(this);
        String[] desc = (String[]) object.getDomain().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.DATAPROPERTY), property, createSingle(ManchesterSyntaxConstants.DOMAIN), desc);
    }

    @Override
    public Object visit(OWLDataPropertyRangeAxiom object) {
        String[] property = (String[]) object.getProperty().accept(this);
        String[] desc = (String[]) object.getRange().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.DATAPROPERTY), property, createSingle(ManchesterSyntaxConstants.RANGE), desc);
    }

    @Override
    public Object visit(OWLSubDataPropertyOfAxiom object) {
        String[] property = (String[]) object.getSubProperty().accept(this);
        String[] superProp = (String[]) object.getSuperProperty().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.DATAPROPERTY), property, createSingle(ManchesterSyntaxConstants.SUBPROPERTTYOF), superProp);
    }

    @Override
    public Object visit(OWLEquivalentDataPropertiesAxiom object) {
        String[] array = createSingle(ManchesterSyntaxConstants.EQUIVALENTDATAPROPERTIES);

        for (OWLDataPropertyExpression prop: object.getProperties()) {
            array = appendArrays(array, (String[]) prop.accept(this));
        }

        return array;
    }

    @Override
    public Object visit(OWLDisjointDataPropertiesAxiom object) {
        String[] array = createSingle(ManchesterSyntaxConstants.DISJOINTDATAPROPERTIES);

        for (OWLDataPropertyExpression prop: object.getProperties()) {
            array = appendArrays(array, (String[]) prop.accept(this));
        }

        return array;

    }

    @Override
    public Object visit(OWLObjectPropertyCharacteristicAxiom object) {
        String[] property = (String[]) object.getProperty().accept(this);

        String attributeString = ManchesterSyntaxConstants.getPropertyAttributeString(object);

        return appendArrays(createSingle(ManchesterSyntaxConstants.OBJECTPROPERTY), property, createSingle(ManchesterSyntaxConstants.CHARACTERISTIC), createSingle(attributeString));
    }

    @Override
    public Object visit(OWLObjectPropertyDomainAxiom object) {
        String[] property = (String[]) object.getProperty().accept(this);
        String[] desc = (String[]) object.getDomain().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.OBJECTPROPERTY), property, createSingle(ManchesterSyntaxConstants.DOMAIN), desc);
    }

    @Override
    public Object visit(OWLObjectPropertyRangeAxiom object) {
        String[] property = (String[]) object.getProperty().accept(this);
        String[] desc = (String[]) object.getRange().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.OBJECTPROPERTY), property, createSingle(ManchesterSyntaxConstants.RANGE), desc);
    }

    @Override
    public Object visit(OWLSubObjectPropertyOfAxiom object) {
        String[] subProp = (String[]) object.getSubProperty().accept(this);
        String[] superProperty = (String[]) object.getSuperProperty().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.OBJECTPROPERTY), subProp, createSingle(ManchesterSyntaxConstants.SUBPROPERTTYOF), superProperty);
    }

    @Override
    public Object visit(OWLEquivalentObjectPropertiesAxiom object) {
        String[] array = createSingle(ManchesterSyntaxConstants.EQUIVALENTOBJECTPROPERTIES);

        for (OWLObjectPropertyExpression prop: object.getProperties()) {
            array = appendArrays(array, (String[]) prop.accept(this));
        }

        return array;
    }

    @Override
    public Object visit(OWLDisjointObjectPropertiesAxiom object) {
        String[] array = createSingle(ManchesterSyntaxConstants.DISJOINTOBJECTPROPERTIES);

        for (OWLObjectPropertyExpression prop: object.getProperties()) {
            array = appendArrays(array, (String[]) prop.accept(this));
        }

        return array;
    }

    @Override
    public Object visit(OWLInverseObjectPropertiesAxiom object) {
        String[] prop = (String[]) object.getFirstProperty().accept(this);
        String[] inverse = (String[]) object.getSecondProperty().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.OBJECTPROPERTY), prop, createSingle(ManchesterSyntaxConstants.INVERSES), inverse);
    }

    @Override
    public Object visit(OWLSameIndividualAxiom object) {
        String[] array = createSingle(ManchesterSyntaxConstants.SAMEINDIVIDUAL);

        for (OWLIndividual i: object.getIndividuals()) {
            array = appendArrays(array, (String[]) i.accept(this));
        }

        return array;
    }

    @Override
    public Object visit(OWLStringLiteral node) {
        String[] values = createSingle(node.getLiteral());
        String[] lang = createSingle(node.getLang());
        // FIXME correct Manchester syntax
        return appendArrays(values, lang);
    }
    
    @Override
    public Object visit(OWLDifferentIndividualsAxiom object) {
        String[] array = createSingle(ManchesterSyntaxConstants.DIFFERENTINDIVIDUALS);

        for (OWLIndividual i: object.getIndividuals()) {
            array = appendArrays(array, (String[]) i.accept(this));
        }

        return array;
    }

    @Override
    public Object visit(OWLDataPropertyAssertionAxiom object) {
        String[] individual = (String[]) object.getSubject().accept(this);
        String[] property = (String[]) object.getProperty().accept(this);
        String[] value = (String[]) object.getObject().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.INDIVIDUAL), individual, createSingle(ManchesterSyntaxConstants.FACTS), property, value);
    }

    @Override
    public Object visit(OWLNegativeDataPropertyAssertionAxiom object) {
        String[] individual = (String[]) object.getSubject().accept(this);
        String[] property = (String[]) object.getProperty().accept(this);
        String[] value = (String[]) object.getObject().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.INDIVIDUAL), individual, createSingle(ManchesterSyntaxConstants.FACTS), createSingle(ManchesterSyntaxConstants.NOT), property, value);
    }

    @Override
    public Object visit(OWLObjectPropertyAssertionAxiom object) {
        String[] individual = (String[]) object.getSubject().accept(this);
        String[] property = (String[]) object.getProperty().accept(this);
        String[] value = (String[]) object.getObject().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.INDIVIDUAL), individual, createSingle(ManchesterSyntaxConstants.FACTS), property, value);
    }

    @Override
    public Object visit(OWLNegativeObjectPropertyAssertionAxiom object) {
        String[] individual = (String[]) object.getSubject().accept(this);
        String[] property = (String[]) object.getProperty().accept(this);
        String[] value = (String[]) object.getObject().accept(this);

        return appendArrays(createSingle(ManchesterSyntaxConstants.INDIVIDUAL), individual, createSingle(ManchesterSyntaxConstants.FACTS), createSingle(ManchesterSyntaxConstants.NOT), property, value);
    }

    @Override
    public Object visit(OWLClassAssertionAxiom object) {
        return appendArrays(createSingle(ManchesterSyntaxConstants.INDIVIDUAL), (String[]) object.getIndividual().accept(this), createSingle(ManchesterSyntaxConstants.TYPES), (String[]) object.getClassExpression().accept(this));
    }

    @Override
    public Object visit(OWLAnnotation object) {
        return appendArrays((String[]) object.getProperty().accept(this), (String[]) object.getValue().accept(this));
    }

    @Override
    public Object visit(OWLAnnotationAssertionAxiom object) {
        Object subj = object.getSubject();
        String[] entityType = new String[0];
        String[] subject = new String[0];
        if (subj instanceof IRI) {
             entityType = createSingle(""); //$NON-NLS-1$
             subject = createSingle(((IRI)subj).toString());
        } else if (subj instanceof OWLEntity) {
            entityType = createSingle(ManchesterSyntaxConstants.getEntityTypeString((OWLEntity)object.getSubject()));

            if (entityType == null) {
                return null;
            }
            subject = (String[]) object.getSubject().accept(this);
        } else {
            if (subj == null) {
                return null;
            }
            entityType = createSingle(subj.toString());
        }

        return appendArrays(entityType, subject, createSingle(ManchesterSyntaxConstants.ANNOTATIONS), (String[]) object.getAnnotation().getProperty().accept(this), (String[]) object.getAnnotation().getValue().accept(this));
    }
    
    @Override
    public Object visit(OWLTypedLiteral node) {
        String[] value = createSingle(node.getLiteral());
        String[] datatype = (String[]) node.getDatatype().accept(this);
        
        return appendArrays(value, datatype);
    }

    @Override
    public Object visit(OWLDeclarationAxiom object) {
        String entityType = ManchesterSyntaxConstants.getEntityTypeString(object.getEntity());

        if (entityType == null) {
            return null;
        }

        return appendArrays(createSingle(entityType), (String[]) object.getEntity().accept(this));
    }
    
    @Override
    public Object visit(OWLFunctionalObjectPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.FUNCTIONAL);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
    
    @Override
    public Object visit(OWLFunctionalDataPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.FUNCTIONAL);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
    
    @Override
    public Object visit(OWLTransitiveObjectPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.TRANSITIVE);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
    
    @Override
    public Object visit(OWLSymmetricObjectPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.SYMMETRIC);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
    
    @Override
    public Object visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.ASYMMETRIC);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
    
    @Override
    public Object visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.INVERSEFUNCTIONAL);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
    
    @Override
    public Object visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.IRREFLEXIVE);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
    
    @Override
    public Object visit(OWLReflexiveObjectPropertyAxiom axiom) {
        String[] functional = createSingle(ManchesterSyntaxConstants.REFLEXIVE);
        String[] propertyUri = (String[]) axiom.getProperty().accept(this);
        return appendArrays(functional, propertyUri);
    }
}
