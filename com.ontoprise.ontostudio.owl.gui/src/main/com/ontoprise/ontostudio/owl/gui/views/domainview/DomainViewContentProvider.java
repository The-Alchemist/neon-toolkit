/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.domainview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyExtraDomainRangeinfoTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetPropertiesForDomainHits;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;
/**
 * 
 * @author Nico Stieler
 */
public class DomainViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    public static final int INDENT = 5;

    /**
     * The items to display;
     */
    private PropertyExtraDomainRangeinfoTreeElement[] _items;

    /**
     * The view displaying the properties for classes (an instance of DomainView)
     */
    private TreeViewer _propertyTree;
    private IPropertyChangeListener _guiListener;
    private IPropertyChangeListener _owlListener;
    private IPreferenceStore _guiStore;
    private IPreferenceStore _owlStore;

    ClazzTreeElement _selectedClazzTreeElement;
    String _ontologyUri;
    String _projectId;

    protected int _style;

    private OWLAxiomListener _axiomListener;

    protected OWLAxiomListener getAxiomListener() {
        if (_axiomListener == null) {
            _axiomListener = new OWLAxiomListener() {

                @Override
                public void modelChanged(OWLChangeEvent event) {
                    _propertyTree.getTree().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            forceUpdate();
                            _propertyTree.refresh();
                        }
                    });
                }

            };
        }
        return _axiomListener;
    }

    public DomainViewContentProvider(TreeViewer treeView) {
        this._propertyTree = treeView;
        _guiStore = NeOnUIPlugin.getDefault().getPreferenceStore();
        _owlStore = OWLPlugin.getDefault().getPreferenceStore();
        _guiListener = new IPropertyChangeListener() {

            // Listens to the events that change the namespace and update instance properties
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _propertyTree.getTree().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            _propertyTree.refresh();
                        }
                    });
                }
                if (event.getProperty().equals(OWLPlugin.SHOW_PROPERTIES_OF_ALL_SUPERCLASSES_IN_DOMAIN_VIEW_PREFERENCE)) {
                    forceUpdate();
                    _propertyTree.refresh();
              }
            }

        };

        _owlListener = new IPropertyChangeListener() {

            // Listens to the events that change the namespace and update instance properties
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _propertyTree.getTree().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            _propertyTree.refresh();
                        }
                    });
                }
            }

        };
        _guiStore.addPropertyChangeListener(_guiListener);
        _owlStore.addPropertyChangeListener(_owlListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object parent) {
        if (_items == null) {
            return new PropertyExtraDomainRangeinfoTreeElement[0];
        }
        return _items;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof Object[]) {
            Object[] array = (Object[]) newInput;
            if (array[0] instanceof ClazzTreeElement) {
                ClazzTreeElement elem = (ClazzTreeElement) array[0];
                if (_selectedClazzTreeElement != null && elem.getId().equals(getSelectedClazz()) && array[1].equals(_ontologyUri) && array[2].equals(_projectId)) {
                    return;
                }
                _selectedClazzTreeElement = elem;
                _ontologyUri = (String) array[1];
                _projectId = (String) array[2];
                updateItems();
            }
        } else {
            _projectId = null;
            _selectedClazzTreeElement = null;
        }
    }

    public boolean update() {
        updateItems();
        return true;
    } 
    /**
     * @param element 
     * @return
     */
    private HashSet<String> determineClasses(ClazzTreeElement element) {
        HashSet<String> set = new HashSet<String>();
        set.add(element.getId());
        
        if(NeOnUIPlugin.getDefault().getPreferenceStore().getBoolean(OWLPlugin.SHOW_PROPERTIES_OF_ALL_SUPERCLASSES_IN_DOMAIN_VIEW_PREFERENCE)){
            ITreeDataProvider provider = element.getProvider();
            if(provider instanceof ClazzHierarchyProvider){
                ClazzHierarchyProvider clazzHierarchyProvider = (ClazzHierarchyProvider)provider;
                TreeElementPath[] pathElem = clazzHierarchyProvider.getPathElements(element);
        
                for(TreeElementPath path : pathElem){
                    for(ITreeElement parent : path.toArray()){
                        if(parent instanceof ClazzTreeElement){
                            set.add(((ClazzTreeElement)parent).getId());
                        }
                    }
                }
            }
        }
        return set;
    }
    private void updateItems() {
        if (_selectedClazzTreeElement == null || _selectedClazzTreeElement.getEntity() == null || getSelectedClazz() == null || _ontologyUri == null) {
            return;
        }

        try {
            HashSet<String> domainClasses = determineClasses(_selectedClazzTreeElement);

            LinkedList<String[]> resultsList = new LinkedList<String[]>();
            String[][] _propertyHits = null;
            
            for(String domainClass : domainClasses){
                for(String[] array2 : new GetPropertiesForDomainHits(_projectId, _ontologyUri, domainClass).getResults()){
                    String[] array = new String[array2.length + 1];
                    for(int i = 0; i < array2.length; i++)
                        array[i] = array2[i];
                    array[array.length - 1] = domainClass;
                    resultsList.add(array);
                }
            }
            
            if(resultsList != null){
                _propertyHits = resultsList.toArray(new String[resultsList.size()][]);
            }
            HashMap<OWLEntity,PropertyExtraDomainRangeinfoTreeElement> propertyItemList = new HashMap<OWLEntity,PropertyExtraDomainRangeinfoTreeElement>();
            
            ITreeDataProvider treeDataProvider = null;

            for (String[] hit: _propertyHits) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];
                String domainClass = hit[hit.length - 1];

                boolean isImported = !ontologyUri.equals(_ontologyUri);
                OWLAxiom axiom = OWLUtilities.axiom(axiomText);
                OWLEntity property = null;
                PropertyExtraDomainRangeinfoTreeElement treeElement = propertyItemList.get(property);

                if(treeElement == null){
                    if(axiom instanceof OWLAnnotationPropertyDomainAxiom) {
                        property = ((OWLAnnotationPropertyDomainAxiom)axiom).getProperty();
                        treeElement = new AnnotationPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider, (OWLClass)_selectedClazzTreeElement.getEntity(), domainClass);
                        
                    } else if(axiom instanceof OWLDataPropertyDomainAxiom) {
                        try {
                            property = (OWLDataProperty)((OWLDataPropertyDomainAxiom)axiom).getProperty();
                            treeElement = new DataPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider, (OWLClass)_selectedClazzTreeElement.getEntity(), domainClass);
                        } catch (ClassCastException e) {
                            // ignore, in case of DataPropertyExpression
                            continue;
                        }
                        
                    } else if(axiom instanceof OWLObjectPropertyDomainAxiom) {
                        try {
                            property = (OWLObjectProperty)((OWLObjectPropertyDomainAxiom)axiom).getProperty();
                            treeElement = new ObjectPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider, (OWLClass)_selectedClazzTreeElement.getEntity(), domainClass);
                        } catch (ClassCastException e) {
                            // ignore, in case of ObjectPropertyExpression
                            continue;
                        }
                    } else {
                        //ignore
                        continue;
                    }
                }else{
                    treeElement.add((OWLClass) _selectedClazzTreeElement.getEntity(), domainClass);
                }

                treeElement.setIsImported(isImported);
                treeElement.resetIsDirect(
                        (_selectedClazzTreeElement != null && 
                         _selectedClazzTreeElement.getEntity() != null && 
                         _selectedClazzTreeElement.getEntity() instanceof OWLClass)
                         ? ((OWLClass) _selectedClazzTreeElement.getEntity()).toStringID().equals(domainClass)
                         : false);
                if(property != null)
                    propertyItemList.put(property, treeElement);
            }
            _items = new PropertyExtraDomainRangeinfoTreeElement[propertyItemList.size()];
            int i = 0;
            for(OWLEntity key : propertyItemList.keySet())
                _items[i++] = propertyItemList.get(key);
//          Arrays.sort(_items);
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        } 
    }
    

    public void forceUpdate() {
        if (_projectId == null) {
            return;
        }
        updateItems();
    }

    @Override
    public Object getParent(Object child) {
        return null;
    }

    @Override
    public Object[] getChildren(Object parent) {
        String projectId = ((PropertyExtraDomainRangeinfoTreeElement) parent).getProjectName();
        String ontologyId = ((PropertyExtraDomainRangeinfoTreeElement) parent).getOntologyUri();

        registerAxiomListener(projectId, ontologyId);
        return new Object[0];
    }

    @Override
    public boolean hasChildren(Object parent) {
        String projectId = ((PropertyExtraDomainRangeinfoTreeElement) parent).getProjectName();
        String ontologyId = ((PropertyExtraDomainRangeinfoTreeElement) parent).getOntologyUri();

        registerAxiomListener(projectId, ontologyId);
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        _guiStore.removePropertyChangeListener(_guiListener);
        _owlStore.removePropertyChangeListener(_owlListener);
    }

    /**
     * Returns the current class the view is displaying the properties of.
     */
    public String getSelectedClazz() {
        if(_selectedClazzTreeElement == null)
            return ""; //$NON-NLS-1$
        return _selectedClazzTreeElement.getId();
    }
    public void setStyle(int style) {
        _style = style;
        updateItems();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerAxiomListener(String projectId, String ontologyId) {
        try {
            Class[] clazzes = new Class[] {OWLClassAssertionAxiom.class};
            OWLModelFactory.getOWLModel(ontologyId, projectId).addAxiomListener(getAxiomListener(), clazzes);
        } catch (NeOnCoreException e1) {
            new NeonToolkitExceptionHandler().handleException(e1);
        }
    }
}
