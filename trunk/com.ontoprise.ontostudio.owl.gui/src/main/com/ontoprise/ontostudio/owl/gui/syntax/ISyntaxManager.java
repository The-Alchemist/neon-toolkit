/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.syntax;

import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

public interface ISyntaxManager {

    /**
     * Returns a String representation of the provided <code>OWLEntity</code> accorging to the Syntax the concrete manager represents.
     * 
     * @param p
     * @return
     */
    String getTextualRepresentation(OWLEntity p, OWLModel owlModel);

    /**
     * Returns a String representation of the provided <code>Description</code> accorging to the Syntax the concrete manager represents.
     * 
     * @param p
     * @return
     */
    String getTextualRepresentation(OWLClassExpression p, OWLModel owlModel);

    /**
     * Returns a String representation of the provided <code>DataRange</code> accorging to the Syntax the concrete manager represents.
     * 
     * @param p
     * @return
     */
    String getTextualRepresentation(OWLDataRange p, OWLModel owlModel);

    /**
     * Returns an <code>URI</code> after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    String parseUri(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns a <code>Description</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    OWLClassExpression parseDescription(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns a <code>DataRange</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    OWLDataRange parseDataRange(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns a <code>Constant</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    OWLLiteral parseConstant(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns an <code>Individual</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    OWLIndividual parseIndividual(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns an <code>DataProperty</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    OWLDataProperty parseDataProperty(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns an <code>ObjectProperty</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    OWLObjectPropertyExpression parseObjectProperty(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns a List of <code>ObjectPropertyExpression</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    List<OWLObjectPropertyExpression> parseObjectPropertyChain(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns an <code>OWLAnnotationProperty</code> object after parsing the input string.
     * 
     * @param input
     * @param ontologyURI
     * @param projectID
     * @return
     */
    OWLAnnotationProperty parseAnnotationProperty(String input, OWLModel owlModel) throws NeOnCoreException;

    /**
     * Returns a <code>ProposalProvider</code> with code assist functionality according to the Syntax the concrete manager represents.
     * 
     * @return
     */
    IContentProposalProvider getProposalProvider();

    /**
     * Returns an <code>ITokenScanner</code> that is used for SyntaxHighlighting.
     * 
     * @return
     */
    ITokenScanner getComplexClassScanner(OWLNamespaces namespaces);

    /**
     * Returns the name of this syntax.
     * 
     * @return
     */
    String getSyntaxName();

    /**
     * Sets the name of this syntax. This is called when initializing the extension point <code>com.ontoprise.ontostudio.owl.gui.syntaxDefinition</code> with
     * the <code>name</code> attribute of <code>manager</code> element.
     */
    void setSyntaxName(String syntaxName);

    /**
     * Returns a visitor initialized with the passed ontology object, that will visit a <code>Predicate</code> and return a String[] of size 4 where<br/>
     * array[0] = complete URI<br/>
     * array[1] = local name<br/>
     * array[2] = QName<br/>
     * array[3] = label in current language<br/>
     * 
     * @return
     */
    OWLObjectVisitorEx getVisitor(OWLModel owlModel);

    /**
     * Returns a visitor initialized with the passed ontology object, that will visit a <code>Predicate</code> and return a String representing the ID depending
     * on the passed <code>idDisplayStyle</code>.
     * 
     * @return
     */
    OWLObjectVisitorEx getVisitor(OWLModel owlModel, int idDisplayStyle);
}
