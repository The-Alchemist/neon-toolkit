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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.internal.properties.PropertyPageInfo;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.gui.properties.EntityPropertiesView;
import org.neontoolkit.gui.properties.IMainPropertyPage;
import org.neontoolkit.gui.properties.IPropertyPage;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.ui.NavigatorSearchMatch;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNaryIndividualAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.individualview.NamedIndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.properties.AnnotationPropertyTab;
import com.ontoprise.ontostudio.owl.gui.properties.SourceViewTab;
import com.ontoprise.ontostudio.owl.gui.properties.annotationProperty.AnnotationPropertyPage2;
import com.ontoprise.ontostudio.owl.gui.properties.clazz.ClazzTaxonomyPropertyPage2;
import com.ontoprise.ontostudio.owl.gui.properties.dataProperty.DataPropertyPropertyPage2;
import com.ontoprise.ontostudio.owl.gui.properties.dataProperty.DataPropertyTaxonomyPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.datatypes.DatatypePropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.individual.IndividualPropertyPage2;
import com.ontoprise.ontostudio.owl.gui.properties.individual.IndividualTaxonomyPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.objectProperty.ObjectPropertyPropertyPage2;
import com.ontoprise.ontostudio.owl.gui.properties.objectProperty.ObjectPropertyTaxonomyPropertyPage;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * @author Nico Stieler
 */
public class AxiomSearchMatch extends OWLComplexSearchMatch {

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

  /*
   * TODO (maybe - need to be discussed):
   * jump to the "other" entity
   * 
   * if you are looking for the references (Find References) you maybe want to see entries of the "other" entity
   * 
   * e.g.
   * Find References of C:
   * 
   * C subclassOf D 
   * --> so far: jumps to the Taxonomy of C 
   * --> perhaps of interest: Taxonomy of D
   * 
   * greater effort
   */
    
    
    @SuppressWarnings("unchecked")
    @Override
    public void show(int index) {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
        EntityPropertiesView epv = null;
        IPropertyPage tab = null;
        
        if(getMatch() instanceof IIndividualTreeElement && getInstanceView() != null){//INDIVIDUALS
            if (getInstanceView() != null) {
                MTreeView nav = NavigatorSearchMatch.getNavigator();
                ClassSearchMatch classMatch = getClassMatch();
                Object match = getClassMatch().getMatch();
                _individualView.selectionChanged(NavigatorSearchMatch.getNavigator(), new StructuredSelection(getClassMatch().getMatch()));
                _individualView.getTreeViewer().setSelection(new StructuredSelection(getMatch()));
            }
            try{
                epv = getPropertyView();
                String classId = NamedIndividualViewItem.class.toString();
                IMainPropertyPage mainTab = null;
                if(classId != null){
                    String beginClassId = "class "; //$NON-NLS-1$
                    if(classId.startsWith(beginClassId)){
                        classId = classId.substring(beginClassId.length());
                    }
                    for(PropertyPageInfo page : epv.getPropertyActivators()){
                        try{
                            if(classId.equals(page.getActivator())){
                                if(page.getPropertyPage() instanceof IMainPropertyPage){
                                    mainTab = (IMainPropertyPage) page.getPropertyPage();
                                    break;
                                }else{
                                    mainTab = page.getPropertyPage().getMainPage();
                                    break;
                                }
                            }
                        } catch (CoreException e) {
                        }
                    }
                }
                if(mainTab == null){
                    IPropertyPage currentTab = epv.getCurrentSelectedTab();
                    if(currentTab == null)//NICO to fix, starting with the second click currentTab is null
                        throw new NullPointerException();
                    
                    if(currentTab instanceof IMainPropertyPage){
                        mainTab = (IMainPropertyPage) currentTab;
                    }else{
                        mainTab = currentTab.getMainPage();
                    }
           		}
                if(mainTab != null){
                    if(isCorrespondingTab(mainTab)){
                        tab = mainTab;
                    }else{
                        for(IPropertyPage page : mainTab.getSubPages()){
                            if(isCorrespondingTab(page)){
                                tab = page;
                                break;
                            }
                        }
                    }
                }
            }catch (NullPointerException e) {
                //nothing to do: does not for sure jump to the correct tab 
            }
            if(epv != null && tab != null){
                epv.setCurrentSelectedTab(tab);
            }
        } else {//ALL THE REST
            super.show(index);
            epv = getPropertyView();
            IPropertyPage currentTab = epv.getCurrentSelectedTab();
            IMainPropertyPage mainTab = null;
            if(currentTab instanceof IMainPropertyPage){
                mainTab = (IMainPropertyPage) currentTab;
            }else{
                mainTab = currentTab.getMainPage();
            }
            if(mainTab != null){
                if(isCorrespondingTab(mainTab)){
                    tab = mainTab;
                }else{
                    for(IPropertyPage page : mainTab.getSubPages()){
                        if(isCorrespondingTab(page)){
                            tab = page;
                            break;
                        }
                    }
                }
            }
            if(epv != null && tab != null){
                epv.setCurrentSelectedTab(tab);
            }
            super.show(index);
        }
    }

    @Override
    public String toString() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        try {
            if(element != null) {
                OWLModel owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
                int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
                Object accept = _axiom.accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle));
                return OWLGUIUtilities.getEntityLabel((String[]) accept).toString();
            }
        }catch (NeOnCoreException e) {
          // nothing to do
        }
        return _axiom.toString();
    }
    @Override
    @SuppressWarnings("unchecked")
    protected boolean isCorrespondingTab(IPropertyPage page){
        
        if(_axiom instanceof OWLIndividualAxiom){
            if(_axiom instanceof OWLPropertyAssertionAxiom){
            //          OWLDataPropertyAssertionAxiom
            //          OWLNegativeDataPropertyAssertionAxiom
            //          OWLObjectPropertyAssertionAxiom
            //          OWLNegativeObjectPropertyAssertionAxiom
                return page instanceof IndividualPropertyPage2;
            }else if(_axiom instanceof OWLNaryIndividualAxiom){
            //          OWLDifferentIndividualsAxiom
            //          OWLSameIndividualAxiom
                return page instanceof IndividualTaxonomyPropertyPage;
            }else if(_axiom instanceof OWLClassAssertionAxiom){
                return page instanceof IndividualTaxonomyPropertyPage;
            }else if(_axiom instanceof OWLAnnotationAssertionAxiom){
                return page instanceof AnnotationPropertyTab; 
            }
        }else if(_axiom instanceof OWLDeclarationAxiom){
                return page instanceof IndividualTaxonomyPropertyPage;
        }else if(_axiom instanceof OWLClassAxiom && getMatch() instanceof ClazzTreeElement){
            if(_axiom instanceof OWLSubClassOfAxiom){
                return page instanceof ClazzTaxonomyPropertyPage2;
            }else if(_axiom instanceof OWLDisjointClassesAxiom
                    || _axiom instanceof OWLEquivalentClassesAxiom){
                return page instanceof ClazzTaxonomyPropertyPage2;
            }
                /* else
                 * TODO: space for not supported:
                 *      - OWLDisjointUnionAxiom
                 *      - OWLNaryAxiom
                 */
            
        }else if(_axiom instanceof OWLObjectPropertyAxiom && getMatch() instanceof ObjectPropertyTreeElement){
            if(_axiom instanceof OWLDisjointObjectPropertiesAxiom 
                    || _axiom instanceof OWLEquivalentObjectPropertiesAxiom 
                    || _axiom instanceof OWLInverseObjectPropertiesAxiom 
                    || _axiom instanceof OWLSubObjectPropertyOfAxiom 
                    || _axiom instanceof OWLSubPropertyChainOfAxiom){
                return page instanceof ObjectPropertyTaxonomyPropertyPage;
            }else if(_axiom instanceof OWLObjectPropertyCharacteristicAxiom){
//                      OWLAsymmetricObjectPropertyAxiom
//                      OWLFunctionalObjectPropertyAxiom
//                      OWLInverseFunctionalObjectPropertyAxiom
//                      OWLIrreflexiveObjectPropertyAxiom
//                      OWLReflexiveObjectPropertyAxiom
//                      OWLSymmetricObjectPropertyAxiom
//                      OWLTransitiveObjectPropertyAxiom
                return page instanceof ObjectPropertyPropertyPage2;
            }else if(_axiom instanceof OWLObjectPropertyDomainAxiom ||
                _axiom instanceof OWLObjectPropertyRangeAxiom){
                return page instanceof ObjectPropertyPropertyPage2;
            }
        }else if(_axiom instanceof OWLDataPropertyAxiom && getMatch() instanceof DataPropertyTreeElement){
            if(_axiom instanceof OWLDataPropertyCharacteristicAxiom){
                //                      OWLFunctionalDataPropertyAxiom  
                return page instanceof DataPropertyPropertyPage2;
            }else if(_axiom instanceof OWLDataPropertyDomainAxiom 
                        || _axiom instanceof OWLDataPropertyRangeAxiom){
                return page instanceof DataPropertyPropertyPage2;  
            }else if(_axiom instanceof OWLEquivalentDataPropertiesAxiom
                        || _axiom instanceof OWLDisjointDataPropertiesAxiom
                        || _axiom instanceof OWLSubDataPropertyOfAxiom){
                return page instanceof DataPropertyTaxonomyPropertyPage;   
            }
        }else if(_axiom instanceof OWLAnnotationAxiom && getMatch() instanceof AnnotationPropertyTreeElement){
            if(_axiom instanceof OWLAnnotationPropertyDomainAxiom 
                    || _axiom instanceof OWLAnnotationPropertyRangeAxiom){
                return page instanceof AnnotationPropertyPage2; 
            }else if(_axiom instanceof OWLSubAnnotationPropertyOfAxiom){
                return page instanceof SourceViewTab;    //TODO later taxonomy
            }
        }else if(_axiom instanceof OWLDatatypeDefinitionAxiom && getMatch() instanceof DatatypeTreeElement){
            return page instanceof DatatypePropertyPage;  
        }else if(_axiom instanceof OWLAnnotationAssertionAxiom){
                return page instanceof AnnotationPropertyTab; 
        }
        return false;
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#setFocus()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setFocus() {
        if ((getMatch() instanceof IIndividualTreeElement) && getInstanceView() != null) {
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
    @SuppressWarnings("unchecked")
    @Override
    protected ClassSearchMatch getClassMatch() {
        IIndividualTreeElement match = (IIndividualTreeElement) getMatch();
        OWLClass clazz = null;
        String ontology = match.getOntologyUri();
        String project = match.getProjectName();
        Set<OWLEntity> entities = null;
        try {
            entities = OWLModelFactory.getOWLModel(ontology, project).getEntity(match.getClazz());
        } catch (NeOnCoreException e) {
            e.printStackTrace();
        }
        if (entities != null) {
            for (OWLEntity entity: entities) {
                if (entity instanceof OWLClass) {
                    clazz = (OWLClass) entity;
                    break;
                }
            }
        }
        ITreeDataProvider provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class);
        ClazzTreeElement clazzTreeElement = new ClazzTreeElement(clazz, ontology, project, provider);
        _classMatch = new ClassSearchMatch(clazzTreeElement);
        return super.getClassMatch();
    }
}
