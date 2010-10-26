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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.gui.properties.EntityPropertiesView;
import org.neontoolkit.gui.properties.IMainPropertyPage;
import org.neontoolkit.gui.properties.IPropertyPage;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.SearchPlugin;
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
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
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
//        String perspective = OWLPerspective.ID; //needed for 
        IPropertyPage tab = null;
//        OWLEntity jumpToEntity = null;
        ClassSearchMatch classMatch = null;

        
        if(getMatch() instanceof IIndividualTreeElement && getInstanceView() != null){
            IIndividualTreeElement match = (IIndividualTreeElement)getMatch();
            OWLClass clazz = null;
            String ontology = match.getOntologyUri();
            String project = match.getProjectName();
            Set<OWLEntity> entities = null;
            try {
                entities = OWLModelFactory.getOWLModel(ontology, project).getEntity(match.getClazz());
            } catch (NeOnCoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(entities != null){
                for(OWLEntity entity : entities){
                    if(entity instanceof OWLClass){
                        clazz = (OWLClass) entity;
                        break;
                    }
                }
            }
            ITreeDataProvider provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class);
            ClazzTreeElement clazzTreeElement = new ClazzTreeElement(clazz, ontology, project, provider);
            classMatch = new ClassSearchMatch(clazzTreeElement);

//          jump to IndividualView
            if (getInstanceView() != null) {
                PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
                _individualView.selectionChanged(
                        NavigatorSearchMatch.getNavigator(), 
                        new StructuredSelection(classMatch.getMatch()));
                _individualView.getTreeViewer().setSelection(new StructuredSelection(getMatch()));
            }
            try{
//                perspective = OWLPerspective.ID;
                epv = getPropertyView();
                IPropertyPage currentTab = epv.getCurrentSelectedTab();
                IMainPropertyPage mainTab = null;
                
                if(currentTab == null)//NICO to fix, starting with the second click currentTab is null
                    throw new NullPointerException();
                
                if(currentTab instanceof IMainPropertyPage){
                    mainTab = (IMainPropertyPage) currentTab;
                }else{
                    mainTab = currentTab.getMainPage();
                }
    //          jump to correct Tab
                if(_axiom instanceof OWLIndividualAxiom){
        //          OWLDataPropertyAssertionAxiom
        //          OWLNegativeDataPropertyAssertionAxiom
        //          OWLObjectPropertyAssertionAxiom
        //          OWLNegativeObjectPropertyAssertionAxiom
                if(_axiom instanceof OWLPropertyAssertionAxiom){
                        if(mainTab != null){
                            if(mainTab instanceof IndividualPropertyPage2){
                                tab = mainTab;
                            }else{
                                for(IPropertyPage page : mainTab.getSubPages()){
                                    if(page instanceof IndividualPropertyPage2){
                                        tab = page;
                                        break;
                                    }
                                }
                            }
                        }  
                    }else{ 
                        //          OWLDifferentIndividualsAxiom
                        //          OWLSameIndividualAxiom
                        if(_axiom instanceof OWLNaryIndividualAxiom){
                            if(mainTab != null){
                                if(mainTab instanceof IndividualTaxonomyPropertyPage){
                                    tab = mainTab;
                                }else{
                                    for(IPropertyPage page : mainTab.getSubPages()){
                                        if(page instanceof IndividualTaxonomyPropertyPage){
                                            tab = page;
                                            break;
                                        }
                                    }
                                }
                            }  
                        }else{
                            if(_axiom instanceof OWLClassAssertionAxiom){
                                if(mainTab != null){
                                    if(mainTab instanceof IndividualTaxonomyPropertyPage){
                                        tab = mainTab;
                                    }else{
                                        for(IPropertyPage page : mainTab.getSubPages()){
                                            if(page instanceof IndividualTaxonomyPropertyPage){
                                                tab = page;
                                                break;
                                            }
                                        }
                                    }
                                }  
                            }else{
                                System.out.println(_axiom.getClass() + " not supported yet(OWLIndividualAxiom)"); //$NON-NLS-1$
                                }
                            }
                        }
                }else{
                    if(_axiom instanceof OWLDeclarationAxiom){
                        if(mainTab != null){
                            if(mainTab instanceof IndividualTaxonomyPropertyPage){
                                tab = mainTab;
                            }else{
                                for(IPropertyPage page : mainTab.getSubPages()){
                                    if(page instanceof IndividualTaxonomyPropertyPage){
                                        tab = page;
                                        break;
                                    }
                                }
                            }
                        }  
                    }
                }
            }catch (Exception e) {
                System.out.println("############# does not jump to the correct Tab for sure #############"); //$NON-NLS-1$ TODO: has to be done
                //nothing to do: does not jump to the correct Tab for sure
            }
            if(epv != null && tab != null){
                epv.setCurrentSelectedTab(tab);
            }
        } else {
            super.show(index);
            if(_axiom instanceof OWLClassAxiom){
                if(getMatch() instanceof ClazzTreeElement){
//                    perspective = OWLPerspective.ID;
                    epv = getPropertyView();
                    IPropertyPage currentTab = epv.getCurrentSelectedTab();
                    IMainPropertyPage mainTab = null;
                    if(currentTab instanceof IMainPropertyPage){
                        mainTab = (IMainPropertyPage) currentTab;
                    }else{
                        mainTab = currentTab.getMainPage();
                    }
                    if(_axiom instanceof OWLSubClassOfAxiom){
        //                    OWLSubClassOfAxiom subClassAxiom = ((OWLSubClassOfAxiom)_axiom);
        //                    ClazzTreeElement match = (ClazzTreeElement)getMatch();
        //                    Set<OWLClass> subClasses = subClassAxiom.getSubClass().getClassesInSignature();
        //                    Set<OWLClass> superClasses = subClassAxiom.getSuperClass().getClassesInSignature();
                            
        //                    findEntityInSigniture:
        //                    {
        //                        for(OWLClass subClass : subClasses){
        //                            System.out.println(subClass.getIRI());
        //                            if(subClass.getIRI().toString().equals(match.getId())){
        //                                System.out.println("gefunden");
        //                                for(OWLClass superClass : superClasses){
        //                                    jumpToEntity = superClass;
        //                                    break findEntityInSigniture;
        //                                }
        //                            }else{
        //                                System.out.println("nicht gefunden");
        //                            }
        //                        }
        //                        for(OWLClass superClass : superClasses){
        //                            System.out.println(superClass.getIRI());
        //                            if(superClass.getIRI().toString().equals(match.getId())){
        //                                System.out.println("gefunden");
        //                                for(OWLClass subClass : subClasses){
        //                                    jumpToEntity = superClass;
        //                                    break findEntityInSigniture;
        //                                }
        //                            }else{
        //                                System.out.println("nicht gefunden");
        //                            }
        //                        }
        //                        jumpToEntity = (OWLClass)match.getEntity();
        //                    }
        
                            if(mainTab != null){
                                for(IPropertyPage page : mainTab.getSubPages()){
                                    if(page instanceof ClazzTaxonomyPropertyPage2){
                                        tab = page;
                                        break;
                                    }
                                }
                            }
        //                }
                    }else{
                        if(_axiom instanceof OWLDisjointClassesAxiom){
                            if(mainTab != null){
                                for(IPropertyPage page : mainTab.getSubPages()){
                                    if(page instanceof ClazzTaxonomyPropertyPage2){
                                        tab = page;
                                        break;
                                    }
                                }
                            }  
                        }else{
                            if(_axiom instanceof OWLEquivalentClassesAxiom){
                                if(mainTab != null){
                                    for(IPropertyPage page : mainTab.getSubPages()){
                                        if(page instanceof ClazzTaxonomyPropertyPage2){
                                            tab = page;
                                            break;
                                        }
                                    }
                                }  
                            }else{
                                System.out.println(_axiom.getClass() + " not supported yet(OWLClassAxiom)"); //$NON-NLS-1$
                                /*
                                 * TODO: space for not supported:
                                 *      - OWLDisjointUnionAxiom
                                 *      - OWLNaryAxiom
                                 */
                            }
                        }
                    }
                }
            }else{
                if(_axiom instanceof OWLObjectPropertyAxiom){
                    if(getMatch() instanceof ObjectPropertyTreeElement){
//                        perspective = OWLPerspective.ID;
                        epv = getPropertyView();
                        IPropertyPage currentTab = epv.getCurrentSelectedTab();
                        IMainPropertyPage mainTab = null;
                        if(currentTab instanceof IMainPropertyPage){
                            mainTab = (IMainPropertyPage) currentTab;
                        }else{
                            mainTab = currentTab.getMainPage();
                        }
                    if(_axiom instanceof OWLDisjointObjectPropertiesAxiom 
                            || _axiom instanceof OWLEquivalentObjectPropertiesAxiom 
                            || _axiom instanceof OWLInverseObjectPropertiesAxiom 
                            || _axiom instanceof OWLSubObjectPropertyOfAxiom 
                            || _axiom instanceof OWLSubPropertyChainOfAxiom){
                            if(mainTab != null){
                                if(mainTab instanceof ObjectPropertyTaxonomyPropertyPage){
                                    tab = mainTab;
                                }else{
                                    for(IPropertyPage page : mainTab.getSubPages()){
                                        if(page instanceof ObjectPropertyTaxonomyPropertyPage){
                                            tab = page;
                                            break;
                                        }
                                    }
                                }
                            }
                        }else{                    
    //                      OWLAsymmetricObjectPropertyAxiom
    //                      OWLFunctionalObjectPropertyAxiom
    //                      OWLInverseFunctionalObjectPropertyAxiom
    //                      OWLIrreflexiveObjectPropertyAxiom
    //                      OWLReflexiveObjectPropertyAxiom
    //                      OWLSymmetricObjectPropertyAxiom
    //                      OWLTransitiveObjectPropertyAxiom
                            if(_axiom instanceof OWLObjectPropertyCharacteristicAxiom){
                                if(mainTab != null){
                                    if(mainTab instanceof ObjectPropertyPropertyPage2){
                                        tab = mainTab;
                                    }else{
                                        for(IPropertyPage page : mainTab.getSubPages()){
                                            if(page instanceof ObjectPropertyPropertyPage2){
                                                tab = page;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }else{
                                if(_axiom instanceof OWLObjectPropertyDomainAxiom ||
                                    _axiom instanceof OWLObjectPropertyRangeAxiom){
                                    if(mainTab != null){
                                        if(mainTab instanceof ObjectPropertyPropertyPage2){
                                            tab = mainTab;
                                        }else{
                                            for(IPropertyPage page : mainTab.getSubPages()){
                                                if(page instanceof ObjectPropertyPropertyPage2){
                                                    tab = page;
                                                    break;
                                                }
                                            }
                                        }
                                    }  
                                }else{
                                    System.out.println(_axiom.getClass() + " not supported yet(OWLObjectPropertyAxiom)"); //$NON-NLS-1$
                                }
                            }
                        }
                    }
                }else{
                    if(_axiom instanceof OWLDataPropertyAxiom){
                        if(getMatch() instanceof DataPropertyTreeElement){
//                            perspective = OWLPerspective.ID;
                            epv = getPropertyView();
                            IPropertyPage currentTab = epv.getCurrentSelectedTab();
                            IMainPropertyPage mainTab = null;
                            if(currentTab instanceof IMainPropertyPage){
                                mainTab = (IMainPropertyPage) currentTab;
                            }else{
                                mainTab = currentTab.getMainPage();
                            }
//                          OWLFunctionalDataPropertyAxiom
                        if(_axiom instanceof OWLDataPropertyCharacteristicAxiom){
                                if(mainTab != null){
                                    if(mainTab instanceof DataPropertyPropertyPage2){
                                        tab = mainTab;
                                    }else{
                                        for(IPropertyPage page : mainTab.getSubPages()){
                                            if(page instanceof DataPropertyPropertyPage2){
                                                tab = page;
                                                break;
                                            }
                                        }
                                    }
                                }  
                            }else{
                                if(_axiom instanceof OWLDataPropertyDomainAxiom 
                                        || _axiom instanceof OWLDataPropertyRangeAxiom){
                                    if(mainTab != null){
                                        if(mainTab instanceof DataPropertyPropertyPage2){
                                            tab = mainTab;
                                        }else{
                                            for(IPropertyPage page : mainTab.getSubPages()){
                                                if(page instanceof DataPropertyPropertyPage2){
                                                    tab = page;
                                                    break;
                                                }
                                            }
                                        }
                                    }  
                                }else{
                                    if(_axiom instanceof OWLEquivalentDataPropertiesAxiom
//                                                || _axiom instanceof OWLDisjointDataPropertiesAxiom
                                            || _axiom instanceof OWLSubDataPropertyOfAxiom){
                                        if(mainTab != null){
                                            if(mainTab instanceof DataPropertyTaxonomyPropertyPage){
                                                tab = mainTab;
                                            }else{
                                                for(IPropertyPage page : mainTab.getSubPages()){
                                                    if(page instanceof DataPropertyTaxonomyPropertyPage){
                                                        tab = page;
                                                        break;
                                                    }
                                                }
                                            }
                                        }   
                                    }else{
                                        System.out.println(_axiom.getClass() + " not supported yet(OWLDataPropertyAxiom)"); //$NON-NLS-1$
                                    }
                                }
                            }
                        }
                    }else{
                        if(_axiom instanceof OWLAnnotationAxiom){
                            if(getMatch() instanceof AnnotationPropertyTreeElement){
//                                perspective = OWLPerspective.ID;
                                epv = getPropertyView();
                                IPropertyPage currentTab = epv.getCurrentSelectedTab();
                                IMainPropertyPage mainTab = null;
                                if(currentTab instanceof IMainPropertyPage){
                                    mainTab = (IMainPropertyPage) currentTab;
                                }else{
                                    mainTab = currentTab.getMainPage();
                                }
                                if(_axiom instanceof OWLAnnotationAssertionAxiom){
                                    if(mainTab != null){
                                        if(mainTab instanceof AnnotationPropertyPage2){
                                            tab = mainTab;
                                        }else{
                                            for(IPropertyPage page : mainTab.getSubPages()){
                                                if(page instanceof AnnotationPropertyPage2){
                                                    tab = page;
                                                    break;
                                                }
                                            }
                                        }
                                    }  
                                }else{
                                    if(_axiom instanceof OWLAnnotationPropertyDomainAxiom 
                                            || _axiom instanceof OWLAnnotationPropertyRangeAxiom){
                                        if(mainTab != null){
                                            if(mainTab instanceof AnnotationPropertyPage2){
                                                tab = mainTab;
                                            }else{
                                                for(IPropertyPage page : mainTab.getSubPages()){
                                                    if(page instanceof AnnotationPropertyPage2){
                                                        tab = page;
                                                        break;
                                                    }
                                                }
                                            }
                                        }  
                                    }else{
                                        if(_axiom instanceof OWLSubAnnotationPropertyOfAxiom){
                                            if(mainTab != null){
                                                if(mainTab instanceof SourceViewTab){
                                                    tab = mainTab;
                                                }else{
                                                    for(IPropertyPage page : mainTab.getSubPages()){//TODO later taxonomy
                                                        if(page instanceof SourceViewTab){
                                                            tab = page;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }    
                                        }else{
                                            System.out.println(_axiom.getClass() + " not supported yet(OWLDataPropertyAxiom)"); //$NON-NLS-1$
                                        }
                                    }
                                }
                            }
                        }else{
                            if(_axiom instanceof OWLDatatypeDefinitionAxiom){
                                if(getMatch() instanceof DatatypeTreeElement){
//                                    perspective = OWLPerspective.ID;
                                    epv = getPropertyView();
                                    IPropertyPage currentTab = epv.getCurrentSelectedTab();
                                    IMainPropertyPage mainTab = null;
                                    if(currentTab instanceof IMainPropertyPage){
                                        mainTab = (IMainPropertyPage) currentTab;
                                    }else{
                                        mainTab = currentTab.getMainPage();
                                    }
                                    if(mainTab != null){
                                        if(mainTab instanceof DatatypePropertyPage){
                                            tab = mainTab;
                                        }else{
                                            for(IPropertyPage page : mainTab.getSubPages()){
                                                if(page instanceof DatatypePropertyPage){
                                                    tab = page;
                                                    break;
                                                }
                                            }
                                        }
                                    }  
                                }
                            }
                            else{
                                if(_axiom instanceof OWLDeclarationAxiom){//NICO
                                    //nothing to do : no specific tab should be used
                                }else{
                                    System.out.println("###################: " + _axiom.getClass()); //$NON-NLS-1$
                                }
                            }
                        }
                    }
                }
            }
            //change tab to tab
            if(epv != null && tab != null){
                epv.setCurrentSelectedTab(tab);
            }
            super.show(index);
        }
    }

    @SuppressWarnings("unchecked")
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

//    OWLAnnotationAssertionAxiom, 
//    OWLAnnotationAxiom, 
//    OWLAnnotationPropertyDomainAxiom, 
//    OWLAnnotationPropertyRangeAxiom, 
//    OWLAsymmetricObjectPropertyAxiom, 
//    OWLClassAssertionAxiom, 
//    OWLClassAxiom, 
//    OWLDataPropertyAssertionAxiom, 
//    OWLDataPropertyAxiom, 
//    OWLDataPropertyCharacteristicAxiom, 
//    OWLDataPropertyDomainAxiom, 
//    OWLDataPropertyRangeAxiom, 
//    OWLDatatypeDefinitionAxiom, 
//    OWLDeclarationAxiom, 
//    OWLDifferentIndividualsAxiom, 
//    OWLDisjointClassesAxiom, 
//    OWLDisjointDataPropertiesAxiom, 
//    OWLDisjointObjectPropertiesAxiom, 
//    OWLDisjointUnionAxiom, 
//    OWLEquivalentClassesAxiom, 
//    OWLEquivalentDataPropertiesAxiom, 
//    OWLEquivalentObjectPropertiesAxiom, 
//    OWLFunctionalDataPropertyAxiom, 
//    OWLFunctionalObjectPropertyAxiom, 
//    OWLHasKeyAxiom, 
//    OWLIndividualAxiom, 
//    OWLInverseFunctionalObjectPropertyAxiom, 
//    OWLInverseObjectPropertiesAxiom, 
//    OWLIrreflexiveObjectPropertyAxiom, 
//    OWLLogicalAxiom, 
//    OWLNaryAxiom, 
//    OWLNaryClassAxiom, 
//    OWLNaryIndividualAxiom, 
//    OWLNaryPropertyAxiom<P>, 
//    OWLNegativeDataPropertyAssertionAxiom, 
//    OWLNegativeObjectPropertyAssertionAxiom, 
//    OWLObjectPropertyAssertionAxiom, 
//    OWLObjectPropertyAxiom, 
//    OWLObjectPropertyCharacteristicAxiom, 
//    OWLObjectPropertyDomainAxiom, 
//    OWLObjectPropertyRangeAxiom, 
//    OWLPropertyAssertionAxiom<P,O>, 
//    OWLPropertyAxiom, 
//    OWLPropertyDomainAxiom<P>, 
//    OWLPropertyRangeAxiom<P,R>, 
//    OWLReflexiveObjectPropertyAxiom, 
//    OWLSameIndividualAxiom, 
//    OWLSubAnnotationPropertyOfAxiom, 
//    OWLSubClassOfAxiom, 
//    OWLSubDataPropertyOfAxiom, 
//    OWLSubObjectPropertyOfAxiom, 
//    OWLSubPropertyAxiom<P>, 
//    OWLSubPropertyChainOfAxiom, 
//    OWLSymmetricObjectPropertyAxiom, 
//    OWLTransitiveObjectPropertyAxiom, 
//    OWLUnaryPropertyAxiom<P>, 
//    SWRLRule;

    
}
