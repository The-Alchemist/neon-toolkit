/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.domainview;

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
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
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
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
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
//    private AbstractOwlEntityTreeElement[] _items;
    private PropertyTreeElement[] _items;

    /**
     * The view displaying the properties for classes (an instance of DomainView)
     */
    private TreeViewer _propertyTree;
    private IPropertyChangeListener _guiListener;
    private IPropertyChangeListener _owlListener;
    private IPreferenceStore _guiStore;
    private IPreferenceStore _owlStore;

    String _selectedClazz;
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
            return new PropertyTreeElement[0];
        }
        return _items;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof Object[]) {
            Object[] array = (Object[]) newInput;
            if (array[0] instanceof OWLClass) {
                OWLClass elem = (OWLClass) array[0];
                if (elem.getIRI().toString().equals(_selectedClazz) && array[1].equals(_ontologyUri) && array[2].equals(_projectId)) {
                    return;
                }
                _selectedClazz = elem.getIRI().toString();//OWLUTIL??
                _ontologyUri = (String) array[1];
                _projectId = (String) array[2];
                updateItems();
            }
        } else {
            _projectId = null;
            _selectedClazz = ""; //$NON-NLS-1$
        }
    }

    public boolean update() {
        updateItems();
        return true;
    }

    private void updateItems() {
        if (_selectedClazz == null || _ontologyUri == null) {
            return;
        }

        try {
            
            String[][] _propertyHits = new GetPropertiesForDomainHits(_projectId, _ontologyUri, _selectedClazz).getResults();
            _items = new PropertyTreeElement[_propertyHits.length];
            
            ITreeDataProvider treeDataProvider = null;
            
            int i = 0;
            for (String[] hit: _propertyHits) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                boolean isImported = !ontologyUri.equals(_ontologyUri);
//                OWLAxiom axiom = (OWLAxiom) OWLUtilities.axiom(axiomText, 
//                        OWLNamespaces.EMPTY_INSTANCE, 
//                        OWLModelFactory.getOWLDataFactory(_projectId), 
//                        OWLModelFactory.getOWLModel(_ontologyUri,_projectId));
                OWLAxiom axiom = OWLUtilities.axiom(axiomText, OWLModelFactory.getOWLModel(_ontologyUri,_projectId).getOntology());
                OWLEntity property;
                if(axiom instanceof OWLAnnotationPropertyDomainAxiom) {
                    property = ((OWLAnnotationPropertyDomainAxiom)axiom).getProperty();
                    _items[i] = new AnnotationPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider);
                    _items[i++].setIsImported(isImported);
                    
                } else if(axiom instanceof OWLDataPropertyDomainAxiom) {
                    try {
                        property = (OWLDataProperty)((OWLDataPropertyDomainAxiom)axiom).getProperty();
                        _items[i] = new DataPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider);
                        _items[i++].setIsImported(isImported);
                    } catch (ClassCastException e) {
                        // ignore, in case of DataPropertyExpression
                        continue;
                    }
                    
                } else if(axiom instanceof OWLObjectPropertyDomainAxiom) {
                    try {
                        property = (OWLObjectProperty)((OWLObjectPropertyDomainAxiom)axiom).getProperty();
                        _items[i] = new ObjectPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider);
                        _items[i++].setIsImported(isImported);

                    } catch (ClassCastException e) {
                        // ignore, in case of ObjectPropertyExpression
                        continue;
                    }
                } else {
                    //ignore
                    continue;
                }
                
//                Arrays.sort(_items);
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

    @Override
    public Object getParent(Object child) {
        return null;
    }

    @Override
    public Object[] getChildren(Object parent) {
        String projectId = ((IProjectElement) parent).getProjectName();
        String ontologyId = ((IOntologyElement) parent).getOntologyUri();

        registerAxiomListener(projectId, ontologyId);
        return new Object[0];
    }

    @Override
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
        _owlStore.removePropertyChangeListener(_owlListener);
    }

    public void setStyle(int style) {
        _style = style;
        updateItems();
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
