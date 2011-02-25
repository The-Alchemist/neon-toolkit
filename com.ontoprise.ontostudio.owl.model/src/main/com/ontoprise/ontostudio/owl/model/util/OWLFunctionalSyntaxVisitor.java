/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.model.util;

import java.io.Writer;

import org.coode.owlapi.functionalrenderer.OWLObjectRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
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
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
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
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

import com.ontoprise.ontostudio.owl.model.OWLConstants;

/**
 * @author Nico Stieler
 * Created on: 18.01.2011
 */

public class OWLFunctionalSyntaxVisitor implements OWLObjectVisitorEx<Object> {
    private static final IRI OWL_THING_IRI = IRI.create(OWLConstants.OWL_THING_URI);
    
    private OWLObjectRenderer renderer;
    
    public OWLFunctionalSyntaxVisitor(Writer writer, OWLOntology ontology) {
        this.renderer = new OWLObjectRenderer(ontology.getOWLOntologyManager(), ontology, writer);
    }
    @Override
    public Object visit(OWLNamedIndividual object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLAnnotationProperty object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataProperty object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectProperty object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDatatype object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLClass object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectInverseOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLLiteral object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataComplementOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataOneOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDatatypeRestriction object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLFacetRestriction object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataAllValuesFrom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataSomeValuesFrom object) {
        renderer.visit(object);
        return null;
    }
    public Object visit(OWLDataCardinalityRestriction object) {
        if (object instanceof OWLDataMinCardinality) {
            visit((OWLDataMinCardinality)object);
        } else if (object instanceof OWLDataMaxCardinality) {
            visit((OWLDataMaxCardinality)object);
        } else if (object instanceof OWLDataExactCardinality) {
            visit((OWLDataExactCardinality)object);
        } else
            System.out.println("invalid cardinality type!"); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(OWLDataHasValue object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectAllValuesFrom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectSomeValuesFrom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectHasSelf object) {
        renderer.visit(object);
        return null;
    }
    @SuppressWarnings("unused")
    private boolean isOWLThing(OWLClassExpression description) {
        return description instanceof OWLClass && OWL_THING_IRI.equals(((OWLClass)description).getIRI());
    }
    public Object visit(OWLObjectCardinalityRestriction object) {
        if (object instanceof OWLObjectMinCardinality) {
            visit((OWLObjectMinCardinality)object);
        } else if (object instanceof OWLObjectMaxCardinality) {
            visit((OWLObjectMaxCardinality)object);
        } else if (object instanceof OWLObjectExactCardinality) {
            visit((OWLObjectExactCardinality)object);
        }else
            System.out.println("invalid attribute type!"); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(OWLObjectOneOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectHasValue object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectComplementOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectUnionOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectIntersectionOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLSubClassOfAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLEquivalentClassesAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDisjointClassesAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDisjointUnionAxiom object) {
        renderer.visit(object);
        return null;
    }
    public Object visit(OWLDataPropertyCharacteristicAxiom object) {
      if (object instanceof OWLFunctionalDataPropertyAxiom){
          visit((OWLFunctionalDataPropertyAxiom)object);
      }else
          throw new IllegalStateException("Invalid attribute type!"); //$NON-NLS-1$
      return null;
  }
    @Override
    public Object visit(OWLDataPropertyDomainAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataPropertyRangeAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLSubDataPropertyOfAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLEquivalentDataPropertiesAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDisjointDataPropertiesAxiom object) {
        renderer.visit(object);
        return null;
    }
    public Object visit(OWLObjectPropertyCharacteristicAxiom object) {
        if (object instanceof OWLFunctionalObjectPropertyAxiom)
            visit((OWLFunctionalObjectPropertyAxiom)object);
        else if (object instanceof OWLInverseFunctionalObjectPropertyAxiom)
            visit((OWLInverseFunctionalObjectPropertyAxiom)object);
        else if (object instanceof OWLSymmetricObjectPropertyAxiom)
            visit((OWLFunctionalObjectPropertyAxiom)object);
        else if (object instanceof OWLTransitiveObjectPropertyAxiom)
            visit((OWLFunctionalObjectPropertyAxiom)object);
        else if (object instanceof OWLReflexiveObjectPropertyAxiom)
            visit((OWLFunctionalObjectPropertyAxiom)object);
        else if (object instanceof OWLIrreflexiveObjectPropertyAxiom)
            visit((OWLFunctionalObjectPropertyAxiom)object);
        else if (object instanceof OWLAsymmetricObjectPropertyAxiom)
            visit((OWLFunctionalObjectPropertyAxiom)object);
        else
            throw new IllegalStateException("Invalid attribute type!"); //$NON-NLS-1$
        return null;
    }
    @Override
    public Object visit(OWLObjectPropertyDomainAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectPropertyRangeAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLSubObjectPropertyOfAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLEquivalentObjectPropertiesAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDisjointObjectPropertiesAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLInverseObjectPropertiesAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLSameIndividualAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDifferentIndividualsAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataPropertyAssertionAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLNegativeDataPropertyAssertionAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectPropertyAssertionAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLNegativeObjectPropertyAssertionAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLClassAssertionAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLAnnotation object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLAnnotationAssertionAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDeclarationAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLAsymmetricObjectPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLReflexiveObjectPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLFunctionalObjectPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLSymmetricObjectPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLFunctionalDataPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLTransitiveObjectPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLIrreflexiveObjectPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLInverseFunctionalObjectPropertyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLSubPropertyChainOfAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLRule object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectMinCardinality object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectExactCardinality object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLObjectMaxCardinality object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataMinCardinality object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataExactCardinality object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataMaxCardinality object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLClassAtom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLDataRangeAtom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLObjectPropertyAtom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLDataPropertyAtom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLBuiltInAtom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLVariable object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLIndividualArgument object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLLiteralArgument object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLSameIndividualAtom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(SWRLDifferentIndividualsAtom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLOntology object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLHasKeyAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDatatypeDefinitionAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLSubAnnotationPropertyOfAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLAnnotationPropertyDomainAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLAnnotationPropertyRangeAxiom object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataIntersectionOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLDataUnionOf object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(IRI  object) {
        renderer.visit(object);
        return null;
    }
    @Override
    public Object visit(OWLAnonymousIndividual  object) {
        renderer.visit(object);
        return null;
    }
}

