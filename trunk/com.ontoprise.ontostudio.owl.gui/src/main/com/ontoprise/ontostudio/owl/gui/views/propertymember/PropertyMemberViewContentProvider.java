/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.propertymember;

import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetAnnotationHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetDataPropertyMemberHitsForProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetObjectPropertyMemberHitsForProperty;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * @author Michael Erdmann
 * @author Nico Stieler
 */
public class PropertyMemberViewContentProvider implements IStructuredContentProvider {
    public static final int ANNOTATION_PROPERTY=0;
    public static final int DATA_PROPERTY=1;
    public static final int OBJECT_PROPERTY=2;
    /**
     * The items to display;
     */
    private PropertyMember[] _items;

    /**
     * The view displaying the properties for classes (an instance of DomainView)
     */
    private TableViewer _tableViewer;
    private Table _table;
    
    private IPropertyChangeListener _guiListener;
    private IPreferenceStore _guiStore;
    private IPreferenceStore _owlStore;
    
    private boolean _isThirdColumnNedded;

    PropertyTreeElement _selectedProperty;
    String _ontologyUri;
    String _projectId;
    Text _textBox;
    
    protected int _style;

    private OWLAxiomListener _axiomListener;

    protected OWLAxiomListener getAxiomListener() {
        if (_axiomListener == null) {
            _axiomListener = new OWLAxiomListener() {

                @Override
                public void modelChanged(OWLChangeEvent event) {
                    _table.getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            forceUpdate();
                            _tableViewer.refresh();
                        }
                    });
                }

            };
        }
        return _axiomListener;
    }

    public PropertyMemberViewContentProvider(TableViewer tableViewer) {
        _tableViewer = tableViewer;
        _table = _tableViewer.getTable();
        _guiStore = NeOnUIPlugin.getDefault().getPreferenceStore();
        _owlStore = OWLPlugin.getDefault().getPreferenceStore();
        
        _guiListener = new IPropertyChangeListener() {
            // Listens to the events that change the namespace and update instance properties
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _table.getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            _tableViewer.refresh();
                        }
                    });
                }
                if (event.getProperty().equals(OWLPlugin.SHOW_PROPERTY_MEMBERS_OF_ALL_SUBPROPERTIES_PREFERENCE)) {
                    forceUpdate();
                    _tableViewer.refresh();
              }
            }
        };
        _guiStore.addPropertyChangeListener(_guiListener);
        _owlStore.addPropertyChangeListener(_guiListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object parent) {
        if (_items == null) {
            return new PropertyTreeElement[0];
        }
        return _items;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof Object[]) {
            Object[] array = (Object[]) newInput;
            if (array[0] instanceof PropertyTreeElement) {
                PropertyTreeElement property = (PropertyTreeElement) array[0];
                if (property.equals(_selectedProperty) && array[1].equals(_ontologyUri) && array[2].equals(_projectId)) {
                    return;
                }
                _selectedProperty = property;
                _ontologyUri = (String) array[1];
                _projectId = (String) array[2];
                _textBox = (Text) array[3];
                
                updateItems();
            }else if(array[0] instanceof PropertyMember){
                //nothing to do
            }else{
                _items = null;
                _textBox.setText(Messages.PropertyMemberView_0);
            }
        }
    }

    /**
     * @param element 
     * @param type 
     * @return
     */
    private HashSet<OWLEntity> determineAllProperties(PropertyTreeElement element, int type) {
        HashSet<OWLEntity> set = new HashSet<OWLEntity>();
        addSubpropertyToProperties(element, set, type);
        return set;
    }

    /**
     * @param element
     */
    private boolean addSubpropertyToProperties(PropertyTreeElement element, HashSet<OWLEntity> set, int type) {
        if(!set.add(element.getEntity()))
            return false;

      if(NeOnUIPlugin.getDefault().getPreferenceStore().getBoolean(OWLPlugin.SHOW_PROPERTY_MEMBERS_OF_ALL_SUBPROPERTIES_PREFERENCE)){
          boolean newSubclasses = false;        
          ITreeDataProvider provider = element.getProvider();
          switch (type) {
              case ANNOTATION_PROPERTY:
                  if(provider instanceof AnnotationPropertyHierarchyProvider){
                      AnnotationPropertyHierarchyProvider propertyProvider = (AnnotationPropertyHierarchyProvider)provider;
                      for(ITreeElement child : propertyProvider.getChildren(element, 0, 0)){
                          if(child instanceof PropertyTreeElement){
                              addSubpropertyToProperties((PropertyTreeElement)child, set, type);
                          }
                      }
                  }
                  break;
              case DATA_PROPERTY:
                  if(provider instanceof DataPropertyHierarchyProvider){
                      DataPropertyHierarchyProvider propertyProvider = (DataPropertyHierarchyProvider)provider;
                      for(ITreeElement child : propertyProvider.getChildren(element, 0, 0)){
                          if(child instanceof PropertyTreeElement){
                              addSubpropertyToProperties((PropertyTreeElement)child, set, type);
                          }
                      }
                  }
                  break;
              case OBJECT_PROPERTY:
                  if(provider instanceof ObjectPropertyHierarchyProvider){
                      ObjectPropertyHierarchyProvider propertyProvider = (ObjectPropertyHierarchyProvider)provider;
                      for(ITreeElement child : propertyProvider.getChildren(element, 0, 0)){
                          if(child instanceof PropertyTreeElement){
                              addSubpropertyToProperties((PropertyTreeElement)child, set, type);
                          }
                      }
                  }
                  break;
        }
          return newSubclasses;
      }else{
          return false;
      } 
    }
    private void updateItems() {
        int type = -1;
        
        if (_selectedProperty == null || _ontologyUri == null) {
            return;
        }

        try {
            LinkedList<String[][]> resultsList = null;
            String[][] results = null;
            
            if (_selectedProperty instanceof AnnotationPropertyTreeElement && _selectedProperty.getEntity() instanceof OWLAnnotationProperty) {
                type = ANNOTATION_PROPERTY;
                resultsList = new LinkedList<String[][]>();
                for(OWLEntity entity : determineAllProperties(_selectedProperty, type)){
                    resultsList.add(new GetAnnotationHits(_projectId, _ontologyUri, (OWLAnnotationProperty) entity).getResults());
                }
            } else  if (_selectedProperty instanceof DataPropertyTreeElement && _selectedProperty.getEntity() instanceof OWLDataProperty) {
                type = DATA_PROPERTY;
                resultsList = new LinkedList<String[][]>();
                for(OWLEntity entity : determineAllProperties(_selectedProperty, type)){
                    resultsList.add(new GetDataPropertyMemberHitsForProperty(_projectId, _ontologyUri, (OWLDataProperty) entity).getResults());
                }
            } else if (_selectedProperty instanceof ObjectPropertyTreeElement && _selectedProperty.getEntity() instanceof OWLObjectProperty) {
                type = OBJECT_PROPERTY;
                resultsList = new LinkedList<String[][]>();
                for(OWLEntity entity : determineAllProperties(_selectedProperty, type)){
                    resultsList.add(new GetObjectPropertyMemberHitsForProperty(_projectId, _ontologyUri, (OWLObjectProperty) entity).getResults());
                }
            } else {
                results = null;
            }
            if(resultsList != null){
                LinkedList<String[]> list = new LinkedList<String[]>();
                for(String[][] array : resultsList){
                    for(String[] array2 : array){
                        list.add(array2);
                    }
                }
                results = list.toArray(new String[list.size()][]);
            }
            if(results == null) {
                //sth. is wrong
                return;
            }
                
            _items = new PropertyMember[results.length];
            
            String[] idArray;
            try {
                idArray = OWLGUIUtilities.getIdArray(_selectedProperty.getEntity(), _ontologyUri, _projectId);
            } catch (NeOnCoreException e) {
                idArray = new String[] {((OWLEntity)_selectedProperty).getIRI().toString()};
            }
            String labelText = Messages.PropertyMemberViewContentProvider_0 + OWLGUIUtilities.getEntityLabel(idArray);

            int i = 0;
            for (String[] hit: results) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                OWLAxiom axiom = OWLUtilities.axiom(axiomText);

                OWLObject subject = null;
                OWLObject property = null;
                OWLObject value = null;
                switch (type) {
                    case ANNOTATION_PROPERTY:
                        subject = ((OWLAnnotationAssertionAxiom)axiom).getSubject();
                        property = ((OWLAnnotationAssertionAxiom)axiom).getProperty();
                        value = ((OWLAnnotationAssertionAxiom)axiom).getValue();
                        break;

                    case DATA_PROPERTY:
                        subject = ((OWLDataPropertyAssertionAxiom)axiom).getSubject();
                        property = ((OWLDataPropertyAssertionAxiom)axiom).getProperty();
                        value = ((OWLDataPropertyAssertionAxiom)axiom).getObject();
                        break;

                    case OBJECT_PROPERTY:
                        subject = ((OWLObjectPropertyAssertionAxiom)axiom).getSubject();
                        property = ((OWLObjectPropertyAssertionAxiom)axiom).getProperty();
                        value = ((OWLObjectPropertyAssertionAxiom)axiom).getObject();
                        break;

                    default:
                        break;
                }

                boolean isImported = !ontologyUri.equals(_ontologyUri);
                
                _items[i++] = new PropertyMember(subject, property,  _selectedProperty.getEntity(), value, isImported, _ontologyUri, _projectId);
            }

            _textBox.setText(labelText + " (" + _items.length + ")");  //$NON-NLS-1$ //$NON-NLS-2$
            
            if(NeOnUIPlugin.getDefault().getPreferenceStore().getBoolean(OWLPlugin.SHOW_PROPERTY_MEMBERS_OF_ALL_SUBPROPERTIES_PREFERENCE)){
                int all = 0;
                int direct = 0;
                for(PropertyMember pM : _items){
                    all++;
                    if(pM.isDirect()){
                        direct++;
                    }
                }
                _textBox.setText(labelText + " " + direct + "|" + all + "");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            
            if(_items.length>0) {
                _tableViewer.setInput(_items);
            } else {
                _textBox.setText(labelText + " (" + Messages.PropertyMemberViewContentProvider_1 + ")");  //$NON-NLS-1$ //$NON-NLS-2$
                _tableViewer.getTable().clearAll();
            }
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

    public Object getParent(Object child) {
        return null;
    }

    public Object[] getChildren(Object parent) {
        String projectId = ((IProjectElement) parent).getProjectName();
        String ontologyId = ((IOntologyElement) parent).getOntologyUri();

        registerAxiomListener(projectId, ontologyId);
        return new Object[0];
    }

    public boolean hasChildren(Object parent) {
        String projectId = ((IProjectElement) parent).getProjectName();
        String ontologyId = ((IOntologyElement) parent).getOntologyUri();

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
        _owlStore.removePropertyChangeListener(_guiListener);
    }

    @SuppressWarnings("unchecked")
    private void registerAxiomListener(String projectId, String ontologyId) {
        try {
            Class[] clazzes = new Class[] {OWLClassAssertionAxiom.class};
            OWLModelFactory.getOWLModel(ontologyId, projectId).addAxiomListener(getAxiomListener(), clazzes);
        } catch (NeOnCoreException e1) {
            new NeonToolkitExceptionHandler().handleException(e1);
        }
    }

    /**
     * @return the isThirdColumnNedded
     */
    public boolean isThirdColumnNedded() {
        return _isThirdColumnNedded;
    }
}
