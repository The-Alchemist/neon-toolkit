/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.match;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.SearchPlugin;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * @author Nico Stieler
 */
public class AxiomSearchMatch extends OwlSearchMatch {

    private IndividualView _individualView;

    private OWLAxiom _axiom;

    public AxiomSearchMatch(ITreeElement element, OWLAxiom axiom) {
        super(element);
        _axiom = axiom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#getImage()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Image getImage() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        if (element instanceof ClazzTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
        } else if (element instanceof IIndividualTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
        } else if (element instanceof ObjectPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
        } else if (element instanceof DataPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
        } else if (element instanceof AnnotationPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
        } else if (element instanceof DatatypeTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
        }
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.HAS_VALUE);
    }

    protected IndividualView getInstanceView() {
        if (_individualView == null) {
            try {
                _individualView = (IndividualView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IndividualView.ID);
            } catch (PartInitException e) {
                SearchPlugin.logError(org.neontoolkit.search.Messages.InstanceSearchMatch_0, e);
            }
        }
        return _individualView;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void show(int index) {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
        if(getMatch() instanceof IIndividualTreeElement) {
            if (getInstanceView() != null) {
//                _individualView.selectionChanged(
//                        NavigatorSearchMatch.getNavigator(), 
//                        new StructuredSelection(_classMatch.getMatch()));
                _individualView.getTreeViewer().setSelection(new StructuredSelection(getMatch()));
            }
        } else {
            super.show(index);
        }
    }

//    @SuppressWarnings("unchecked")
    @Override
    public String toString() {

        return _axiom.toString();
//        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
//
//        String subject = ""; //$NON-NLS-1$
//        String axiomString = ""; //$NON-NLS-1$
//        String projectName = ""; //$NON-NLS-1$
//        String ontology = ""; //$NON-NLS-1$
//
//        try {
//            if(element != null) {
//                OWLModel owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
//                int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
//                OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
//                subject = OWLGUIUtilities.getEntityLabel((String[]) element.getEntity().accept(visitor));
//                axiomString = OWLGUIUtilities.getEntityLabel((String[]) _axiom.accept(visitor));
//                projectName = element.getProjectName();
//                ontology = element.getOntologyUri();
//                return _axiom.toString();
////////                return subject + ": " + axiomString + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + ontology + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + projectName + "]  "; //$NON-NLS-1$ //$NON-NLS-2$
//////                String output;
////////                if(_axiom instanceof 
////////                        OWLAnnotationAssertionAxiom, 
////////                        OWLAnnotationAxiom, 
////////                        OWLAnnotationPropertyDomainAxiom, 
////////                        OWLAnnotationPropertyRangeAxiom, 
////////                        OWLAsymmetricObjectPropertyAxiom, 
////////                        OWLClassAssertionAxiom, 
////////                        OWLClassAxiom, 
////////                        OWLDataPropertyAssertionAxiom, 
////////                        OWLDataPropertyAxiom, 
////////                        OWLDataPropertyCharacteristicAxiom, 
////////                        OWLDataPropertyDomainAxiom, 
////////                        OWLDataPropertyRangeAxiom, 
////////                        OWLDatatypeDefinitionAxiom, 
////////                        OWLDeclarationAxiom, 
////////                        OWLDifferentIndividualsAxiom, 
////////                        OWLDisjointClassesAxiom, 
////////                        OWLDisjointDataPropertiesAxiom, 
////////                        OWLDisjointObjectPropertiesAxiom, 
////////                        OWLDisjointUnionAxiom, 
////////                        OWLEquivalentClassesAxiom, 
////////                        OWLEquivalentDataPropertiesAxiom, 
////////                        OWLEquivalentObjectPropertiesAxiom, 
////////                        OWLFunctionalDataPropertyAxiom, 
////////                        OWLFunctionalObjectPropertyAxiom, 
////////                        OWLHasKeyAxiom, 
////////                        OWLIndividualAxiom, 
////////                        OWLInverseFunctionalObjectPropertyAxiom, 
////////                        OWLInverseObjectPropertiesAxiom, 
////////                        OWLIrreflexiveObjectPropertyAxiom, 
////////                        OWLLogicalAxiom, 
////////                        OWLNaryAxiom, 
////////                        OWLNaryClassAxiom, 
////////                        OWLNaryIndividualAxiom, 
////////                        OWLNaryPropertyAxiom<P>, 
////////                        OWLNegativeDataPropertyAssertionAxiom, 
////////                        OWLNegativeObjectPropertyAssertionAxiom, 
////////                        OWLObjectPropertyAssertionAxiom, 
////////                        OWLObjectPropertyAxiom, 
////////                        OWLObjectPropertyCharacteristicAxiom, 
////////                        OWLObjectPropertyDomainAxiom, 
////////                        OWLObjectPropertyRangeAxiom, 
////////                        OWLPropertyAssertionAxiom<P,O>, 
////////                        OWLPropertyAxiom, 
////////                        OWLPropertyDomainAxiom<P>, 
////////                        OWLPropertyRangeAxiom<P,R>, 
////////                        OWLReflexiveObjectPropertyAxiom, 
////////                        OWLSameIndividualAxiom, 
////////                        OWLSubAnnotationPropertyOfAxiom, 
////////                        OWLSubClassOfAxiom, 
////////                        OWLSubDataPropertyOfAxiom, 
////////                        OWLSubObjectPropertyOfAxiom, 
////////                        OWLSubPropertyAxiom<P>, 
////////                        OWLSubPropertyChainOfAxiom, 
////////                        OWLSymmetricObjectPropertyAxiom, 
////////                        OWLTransitiveObjectPropertyAxiom, 
////////                        OWLUnaryPropertyAxiom<P>, 
////////                        SWRLRule;
//////                output = _axiom.toString();
////////                System.out.println("xxxxxxxxxxxxxxxxxxx");
////////                System.out.println(output);
//////                
//////                if(_axiom.toString().contains("Declaration")){
////////                    Declaration(OWLClass(<http://www.aktors.org/ontology/support#Thing>))
//////                    String[][] spo = new String[3][3];
//////                    int s0,s1,s2,s3;
//////                    s0 = 0;
//////                    s1 = output.indexOf("Declaration(");
//////                    s2 = output.lastIndexOf("(");
//////                    s3 = output.indexOf("))");
//////                    
//////                    spo[0][0] = output.substring(s0, s1+12);
//////                    spo[1][0] = output.substring(s1+12, s2+1);
//////                    spo[2][0] = output.substring(s2+1, s3);
//////
//////                    spo[0][1] = spo[0][0];
//////                    spo[0][2] = spo[0][0];
//////                    spo[1][1] = spo[1][0];
//////                    spo[1][2] = spo[1][0];
//////                    if( spo[2][0].contains("#")){
//////                        String help = new String(spo[2][0]);
//////                        if(help.startsWith("<"))
//////                            help = help.substring(1);
//////                        if(help.endsWith(">"))
//////                            help = help.substring(0,help.length()-1);
//////                        String[] split = help.split("#");
////////                        OWLGUIUtilities.getEntityLabel()
////////                        OWLGUIUtilities.get
//////                        spo[2][1] = split[1];
//////                        spo[2][2] = split[0].substring(split[0].lastIndexOf("/") + 1) + ":" + split[1];
//////                        
//////                    }else{
//////                        spo[2][1] = spo[2][0];
//////                        spo[2][2] = spo[2][0];
//////                    }
//////                    String ending = "))" + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + ontology + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + projectName + "]  ";//$NON-NLS-1$
//////                    String[] out = new String[]{spo[0][0] + spo[1][0] + spo[2][0] + ending,spo[0][1] + spo[1][1] + spo[2][1] + ending,spo[0][2] + spo[1][2] + spo[2][2] + ending};
////////                    System.out.println(OWLGUIUtilities.getEntityLabel(out));
//////                    return OWLGUIUtilities.getEntityLabel(out);
//////                }else{
//////                    String[][] spo = new String[3][3];
//////                    int s0,s1,s2,s3;
//////                    s0 = 0;
//////                    s1 = output.indexOf("(");
//////                    s2 = output.indexOf(" ");
//////                    s3 = output.indexOf(")");
//////                    
//////                    spo[1][0] = output.substring(s0, s1);
//////                    spo[0][0] = output.substring(s1+1, s2);
//////                    spo[2][0] = output.substring(s2+1, s3);
//////                    
//////                    if( spo[0][0].contains("#")){
//////                        String help = new String(spo[0][0]);
//////                        if(help.startsWith("<"))
//////                            help = help.substring(1);
//////                        if(help.endsWith(">"))
//////                            help = help.substring(0,help.length()-1);
//////                        String[] split = help.split("#");
////////                        OWLGUIUtilities.getEntityLabel()
////////                        OWLGUIUtilities.get
//////                        spo[0][1] = split[1];
//////                        spo[0][2] = split[0].substring(split[0].lastIndexOf("/") + 1) + ":" + split[1];
//////                        
//////                    }else{
//////                        spo[0][1] = spo[0][0];
//////                        spo[0][2] = spo[0][0];
//////                    }
//////
//////                    if( spo[1][0].contains("#")){
//////                        String help = new String(spo[1][0]);
//////                        if(help.startsWith("<"))
//////                            help = help.substring(1);
//////                        if(help.endsWith(">"))
//////                            help = help.substring(0,help.length()-1);
//////                        String[] split = help.split("#");
////////                        OWLGUIUtilities.getEntityLabel()
////////                        OWLGUIUtilities.get
//////                        spo[1][1] = split[1];
//////                        spo[1][2] = split[0].substring(split[0].lastIndexOf("/") + 1) + ":" + split[1];
//////                        
//////                    }else{
//////                        spo[1][1] = spo[1][0];
//////                        spo[1][2] = spo[1][0];
//////                    }
//////
//////                    if( spo[2][0].contains("#")){
//////                        String help = new String(spo[2][0]);
//////                        if(help.startsWith("<"))
//////                            help = help.substring(1);
//////                        if(help.endsWith(">"))
//////                            help = help.substring(0,help.length()-1);
//////                        String[] split = help.split("#");
////////                        OWLGUIUtilities.getEntityLabel()
////////                        OWLGUIUtilities.get
//////                        spo[2][1] = split[1];
//////                        spo[2][2] = split[0].substring(split[0].lastIndexOf("/") + 1) + ":" + split[1];
//////                        
//////                    }else{
//////                        spo[2][1] = spo[2][0];
//////                        spo[2][2] = spo[2][0];
//////                    }
////////                    System.out.println(s + ", " + p  + ", " + o);
//////                    output = subject + ": " + axiomString + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + ontology + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + projectName + "]  "; //$NON-NLS-1$ //$NON-NLS-2$
////////                    output = _axiom + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + ontology + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + projectName + "]  "; //$NON-NLS-1$
//////                    output = OWLGUIUtilities.getEntityLabel(spo[0]) + " " + OWLGUIUtilities.getEntityLabel(spo[1])  + " " + OWLGUIUtilities.getEntityLabel(spo[2])+ com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + ontology + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + projectName + "]  ";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
//////                    return output;
////                }
//            }
//        } catch (NeOnCoreException e) {
//            // nothing to do
//        }
//        return _axiom.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#setFocus()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setFocus() {
        if (getInstanceView() != null &&  (getMatch() instanceof IIndividualTreeElement)) {
            _individualView.setFocus();
        } else {
            super.setFocus();
        }
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AxiomSearchMatch)) {
            return false;
        }
        AxiomSearchMatch that = (AxiomSearchMatch) object;
        
        return this._axiom.equals(that._axiom);
    }

    @Override
    public int hashCode() {
        return _axiom.hashCode();
    }

}
