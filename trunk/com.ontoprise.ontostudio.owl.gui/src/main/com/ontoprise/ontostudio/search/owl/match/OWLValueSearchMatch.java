/**
 *
 */
package com.ontoprise.ontostudio.search.owl.match;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.neontoolkit.gui.internal.properties.PropertyPageInfo;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.properties.EntityPropertiesView;
import org.neontoolkit.gui.properties.IMainPropertyPage;
import org.neontoolkit.gui.properties.IPropertyPage;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.ui.NavigatorSearchMatch;

import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.individualview.NamedIndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.properties.AnnotationPropertyTab;
import com.ontoprise.ontostudio.owl.gui.properties.ontology.OntologyAnnotationsPropertyPage2;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * @author Nico Stieler
 * Created on: 28.10.2010
 */
public abstract class OWLValueSearchMatch extends OWLComplexSearchMatch {


    /**
     * @param element
     */
    public OWLValueSearchMatch(ITreeElement element) {
        super(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#show(int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void show(int index) {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
        EntityPropertiesView epv = null;
        IPropertyPage tab = null;
        
        if (getMatch() instanceof IIndividualTreeElement) {
            if (getInstanceView() != null) {
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
  //          jump to correct Tab
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
//                System.out.println("############# does not for sure jump to the correct Tab #############"); //$NON-NLS-1$ TODO: has to be done
                //nothing to do: does not for sure jump to the correct Tab 
            }
            if(epv != null && tab != null){
                epv.setCurrentSelectedTab(tab);
            }
        } else {
            super.show(index);
            String entityId = null;
            if (getMatch() instanceof ClazzTreeElement) {
                entityId = ClazzTreeElement.class.toString();
            } else if (getMatch() instanceof ObjectPropertyTreeElement) {
                entityId = ObjectPropertyTreeElement.class.toString();
            } else if (getMatch() instanceof DataPropertyTreeElement) {
                entityId = DataPropertyTreeElement.class.toString();
            } else if (getMatch() instanceof AnnotationPropertyTreeElement) {
                entityId = AnnotationPropertyTreeElement.class.toString();
            } else if (getMatch() instanceof DatatypeTreeElement) {
                entityId = DatatypeTreeElement.class.toString();
            } else if (getMatch() instanceof OntologyTreeElement) { //NICO Ontology
                entityId = OntologyTreeElement.class.toString();
            }
            IMainPropertyPage mainTab = null;
            if(entityId != null){
                String beginEntityId = "class "; //$NON-NLS-1$
                if(entityId.startsWith(beginEntityId)){
                    entityId = entityId.substring(beginEntityId.length());
                }
                epv = getPropertyView();
                for(PropertyPageInfo page : epv.getPropertyActivators()){
                    try{
                        if(entityId.equals(page.getActivator())){
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
            if(mainTab == null && epv != null){
                IPropertyPage currentTab = epv.getCurrentSelectedTab();
                if(currentTab instanceof IMainPropertyPage){
                    mainTab = (IMainPropertyPage) currentTab;
                }else{
                    mainTab = currentTab.getMainPage();
                }
            }
            if (getMatch() instanceof ClazzTreeElement
                    || getMatch() instanceof ObjectPropertyTreeElement
                    || getMatch() instanceof DataPropertyTreeElement
                    || getMatch() instanceof AnnotationPropertyTreeElement
                    || getMatch() instanceof DatatypeTreeElement) {

                if(mainTab != null){
                    if(mainTab instanceof AnnotationPropertyTab){
                        tab = mainTab;
                    }else{
                        for(IPropertyPage page : mainTab.getSubPages()){
                            if(page instanceof AnnotationPropertyTab){
                                tab = page;
                                break;
                            }
                        }
                    }
                }
            }else{
                if (getMatch() instanceof OntologyTreeElement) {

                    if(mainTab != null){
                        if(mainTab instanceof OntologyAnnotationsPropertyPage2){
                            tab = mainTab;
                        }else{
                            for(IPropertyPage page : mainTab.getSubPages()){
                                if(page instanceof OntologyAnnotationsPropertyPage2){
                                    tab = page;
                                    break;
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
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#setFocus()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setFocus() {
        if (getInstanceView() != null && (getMatch() instanceof IIndividualTreeElement)) {
            _individualView.setFocus();
        } else {
            super.setFocus();
        }
    }
}
