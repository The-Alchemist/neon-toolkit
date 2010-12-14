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
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetAnnotationHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetDataPropertyMemberHitsForProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetObjectPropertyMemberHitsForProperty;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * @author Michael Erdmann
 */
public class PropertyMemberViewContentProvider implements IStructuredContentProvider {

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

    OWLNamedObject _selectedProperty;
    String _ontologyUri;
    String _projectId;
    Text _textBox;
    
    protected int _style;

    private OWLAxiomListener _axiomListener;

    protected OWLAxiomListener getAxiomListener() {
        if (_axiomListener == null) {
            _axiomListener = new OWLAxiomListener() {

                public void modelChanged(OWLChangeEvent event) {
                    _table.getDisplay().syncExec(new Runnable() {
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
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _table.getDisplay().syncExec(new Runnable() {
                        public void run() {
                            _tableViewer.refresh();
                        }
                    });
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
    public Object[] getElements(Object parent) {
        if (_items == null) {
            return new PropertyTreeElement[0];
        }
        return _items;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof Object[]) {
            Object[] array = (Object[]) newInput;
            if (array[0] instanceof OWLNamedObject) {
                OWLNamedObject property = (OWLNamedObject) array[0];
                if (property.equals(_selectedProperty) && array[1].equals(_ontologyUri) && array[2].equals(_projectId)) {
                    return;
                }
                _selectedProperty = property;
                _ontologyUri = (String) array[1];
                _projectId = (String) array[2];
                _textBox = (Text) array[3];
                
                updateItems();
            }
        }
    }

    private void updateItems() {
        int type = -1;
        
        if (_selectedProperty == null || _ontologyUri == null) {
            return;
        }

        try {
            String[][] results;
            if (_selectedProperty instanceof OWLAnnotationProperty) {
                type = 0;
                results = new GetAnnotationHits(_projectId, _ontologyUri, (OWLAnnotationProperty) _selectedProperty).getResults();
            } else  if (_selectedProperty instanceof OWLDataProperty) {
                type = 1;
                results = new GetDataPropertyMemberHitsForProperty(_projectId, _ontologyUri, (OWLDataProperty) _selectedProperty).getResults();
            } else if (_selectedProperty instanceof OWLObjectProperty) {
                type = 2;
                results = new GetObjectPropertyMemberHitsForProperty(_projectId, _ontologyUri, (OWLObjectProperty) _selectedProperty).getResults();
            } else {
                results = null;
            }

            if(results == null) {
                //sth. is wrong
                return;
            }
                
            _items = new PropertyMember[results.length];
            
            String[] idArray;
            try {
                idArray = OWLGUIUtilities.getIdArray(_selectedProperty, _ontologyUri, _projectId);
            } catch (NeOnCoreException e) {
                idArray = new String[] {((OWLEntity)_selectedProperty).getIRI().toString()};
            }
            String labelText = Messages.PropertyMemberViewContentProvider_0 + OWLGUIUtilities.getEntityLabel(idArray);

            int i = 0;
            for (String[] hit: results) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                OWLAxiom axiom = OWLUtilities.axiom(axiomText, OWLNamespaces.EMPTY_INSTANCE, OWLModelFactory.getOWLDataFactory(_projectId));
                
                OWLObject subject = null;
                OWLObject value = null;
                switch (type) {
                    case 0:
                        //annotation property
                        subject = ((OWLAnnotationAssertionAxiom)axiom).getSubject();
                        value = ((OWLAnnotationAssertionAxiom)axiom).getValue();
                        break;

                    case 1:
                        //data property
                        subject = ((OWLDataPropertyAssertionAxiom)axiom).getSubject();
                        value = ((OWLDataPropertyAssertionAxiom)axiom).getObject();
                        break;

                    case 2:
                        //object property
                        subject = ((OWLObjectPropertyAssertionAxiom)axiom).getSubject();
                        value = ((OWLObjectPropertyAssertionAxiom)axiom).getObject();
                        break;

                    default:
                        break;
                }

                boolean isImported = !ontologyUri.equals(_ontologyUri);
                
                _items[i++] = new PropertyMember(subject, value, isImported, _ontologyUri, _projectId);
                _textBox.setText(labelText + " (" + i + ")");  //$NON-NLS-1$ //$NON-NLS-2$
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
}
