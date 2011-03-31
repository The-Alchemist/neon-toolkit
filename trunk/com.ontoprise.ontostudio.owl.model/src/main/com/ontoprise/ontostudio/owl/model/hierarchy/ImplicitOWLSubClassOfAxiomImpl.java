/**
 * written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.model.hierarchy;

import java.util.Collection;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

/**
 * @author Nico Stieler
 * Created on: 25.03.2011
 */
public class ImplicitOWLSubClassOfAxiomImpl extends OWLSubClassOfAxiomImpl {

    /**
     * @param dataFactory
     * @param subClass
     * @param superClass
     * @param annotations
     */
    public ImplicitOWLSubClassOfAxiomImpl(OWLDataFactory dataFactory, OWLClassExpression subClass, OWLClassExpression superClass) {
        super(dataFactory, subClass, superClass, new LinkedList<OWLAnnotation>());
        // TODO Auto-generated constructor stub
    }

}
