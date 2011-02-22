/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
/**
 * 
 * @author Nico Stieler
 */
public class RestrictionOnPropertyWriter {

    /**
     * Structures OWL restrictions on properties for ontology elements to be written in an Entity Property Pages of those elements (e.g. of an OWL class or a
     * complex class expression).
     * 
     * @param description represents ontology elements defined in KAON2:
     *            <ul>
     *            <li>ObjectSome
     *            <li>ObjectAll
     *            <li>ObjectHasValue
     *            <li>ObjectCardinality
     *            <li>DataSome
     *            <li>DataAll
     *            <li>DataHasValue
     *            <li>DataCardinality
     *            </ul>
     * @return structured restriction as an array list the List has always this sequence
     *         <ul>
     *         <li>Quantifier/type of restriction (see above)
     *         <li>representation of property*
     *         <li>the "range" of the description*
     *         <li>card
     *         </ul>
     *         *=means that the value is a string array representing the entity in different serializations [uri, localName, qName]
     * 
     */
    public static ArrayList<String[]> performRestriction(OWLClassExpression description, OWLNamespaces ns, OWLObjectVisitorEx visitor, OWLEntity currentClass) {
        ArrayList<String[]> resultArray = new ArrayList<String[]>();

        if (description instanceof OWLObjectSomeValuesFrom) {
            OWLObjectSomeValuesFrom newDesc = (OWLObjectSomeValuesFrom) description;
            resultArray.add(new String[] {OWLCommandUtils.SOME});
            resultArray.add((String[]) newDesc.getProperty().accept(visitor));
            resultArray.add((String[]) newDesc.getFiller().accept(visitor));
            resultArray.add(null);

        } else if (description instanceof OWLObjectAllValuesFrom) {
            OWLObjectAllValuesFrom newDesc = (OWLObjectAllValuesFrom) description;
            resultArray.add(new String[] {OWLCommandUtils.ONLY});
            resultArray.add((String[]) newDesc.getProperty().accept(visitor));
            resultArray.add((String[]) newDesc.getFiller().accept(visitor));
            resultArray.add(null);

        } else if (description instanceof OWLObjectHasValue) {
            OWLObjectHasValue newDesc = (OWLObjectHasValue) description;
            resultArray.add(new String[] {OWLCommandUtils.HAS_VALUE});
            resultArray.add((String[]) newDesc.getProperty().accept(visitor));
            resultArray.add((String[]) newDesc.getValue().accept(visitor));
            resultArray.add(null);
            
        } else if (description instanceof OWLObjectHasSelf){
            OWLObjectHasSelf newDesc = (OWLObjectHasSelf) description;
            resultArray.add(new String[] {OWLCommandUtils.HAS_SELF});
            resultArray.add((String[]) newDesc.getProperty().accept(visitor));
            resultArray.add(new String[]{"", ""});// (String[]) currentClass.accept(visitor));
            resultArray.add(null);

        } else if (description instanceof OWLObjectCardinalityRestriction) {
            OWLObjectCardinalityRestriction newDesc = (OWLObjectCardinalityRestriction) description;
            String cardType = ""; //$NON-NLS-1$
            String card = ((Integer) newDesc.getCardinality()).toString();
            if (newDesc instanceof OWLObjectMinCardinality) {
                cardType = OWLCommandUtils.AT_LEAST_MIN;
            } else if (newDesc instanceof OWLObjectMaxCardinality) {
                cardType = OWLCommandUtils.AT_MOST_MAX;
            } else if (newDesc instanceof OWLObjectExactCardinality) {
                cardType = OWLCommandUtils.EXACTLY_CARDINALITY;
            } else {
                new IllegalStateException();
            }

            resultArray.add(new String[] {cardType});
            resultArray.add((String[]) newDesc.getProperty().accept(visitor));
            resultArray.add((String[]) newDesc.getFiller().accept(visitor));
            resultArray.add(new String[] {card});

        } else if (description instanceof OWLDataSomeValuesFrom) {
            OWLDataSomeValuesFrom newDesc = (OWLDataSomeValuesFrom) description;
            OWLDataPropertyExpression exp = newDesc.getProperty();

            String[] array = (String[]) exp.accept(visitor);

            resultArray.add(new String[] {OWLCommandUtils.SOME});
            resultArray.add(array);
            resultArray.add((String[]) newDesc.getFiller().accept(visitor));
            resultArray.add(null);
            resultArray.add(null);

        } else if (description instanceof OWLDataAllValuesFrom) {
            OWLDataAllValuesFrom newDesc = (OWLDataAllValuesFrom) description;
            OWLDataPropertyExpression exp = newDesc.getProperty();

            String[] array = (String[]) exp.accept(visitor);

            resultArray.add(new String[] {OWLCommandUtils.ONLY});
            resultArray.add(array);
            resultArray.add((String[]) newDesc.getFiller().accept(visitor));
            resultArray.add(null);
            resultArray.add(null);

        } else if (description instanceof OWLDataHasValue) {
            OWLDataHasValue newDesc = (OWLDataHasValue) description;

            resultArray.add(new String[] {OWLCommandUtils.HAS_VALUE});
            resultArray.add((String[]) newDesc.getProperty().accept(visitor));
            resultArray.add((String[]) newDesc.getValue().accept(visitor));
            resultArray.add(null);

        } else if (description instanceof OWLDataCardinalityRestriction) {
            OWLDataCardinalityRestriction newDesc = (OWLDataCardinalityRestriction) description;
            String cardType = ""; //$NON-NLS-1$
            String card = ((Integer) newDesc.getCardinality()).toString();
            if (newDesc instanceof OWLDataMinCardinality) {
                cardType = OWLCommandUtils.AT_LEAST_MIN;
            } else if (newDesc instanceof OWLDataMaxCardinality) {
                cardType = OWLCommandUtils.AT_MOST_MAX;
            } else if (newDesc instanceof OWLDataExactCardinality) {
                cardType = OWLCommandUtils.EXACTLY_CARDINALITY;
            } else {
                new IllegalStateException();
            }

            resultArray.add(new String[] {cardType});
            resultArray.add((String[]) newDesc.getProperty().accept(visitor));
            resultArray.add((String[]) newDesc.getFiller().accept(visitor));
            resultArray.add(new String[] {card});
        }
        return resultArray;
    }
}
