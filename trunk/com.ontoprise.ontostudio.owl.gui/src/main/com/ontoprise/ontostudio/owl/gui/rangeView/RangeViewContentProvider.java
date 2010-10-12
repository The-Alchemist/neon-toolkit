/*****************************************************************************
 * based on com.ontoprise.ontostudio.owl.gui.domainview.DomainViewContentProvider 
 * developed by ontoprise GmbH
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.rangeView;

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
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetPropertiesForRangeHits;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;
/**
 * 
 * @author Nico Stieler
 * Created on: 08.10.2010
 */
public class RangeViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    public static final int INDENT = 5;

    /**
     * The items to display;
     */
//    private AbstractOwlEntityTreeElement[] _items;
    private PropertyTreeElement[] _items;

    /**
     * The view displaying the properties for classes (an instance of RangeView)
     */
    private TreeViewer _propertyTree;
    private IPropertyChangeListener _guiListener;
    private IPropertyChangeListener _owlListener;
    private IPreferenceStore _guiStore;
    private IPreferenceStore _owlStore;

    String _selectedRange;
    String _ontologyUri;
    String _projectId;

    protected int _style;

    private OWLAxiomListener _axiomListener;

    protected OWLAxiomListener getAxiomListener() {
        if (_axiomListener == null) {
            _axiomListener = new OWLAxiomListener() {

                public void modelChanged(OWLChangeEvent event) {
                    _propertyTree.getTree().getDisplay().syncExec(new Runnable() {
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

    public RangeViewContentProvider(TreeViewer treeView) {
        this._propertyTree = treeView;
        _guiStore = NeOnUIPlugin.getDefault().getPreferenceStore();
        _owlStore = OWLPlugin.getDefault().getPreferenceStore();
        _guiListener = new IPropertyChangeListener() {

            // Listens to the events that change the namespace and update instance properties
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _propertyTree.getTree().getDisplay().syncExec(new Runnable() {
                        public void run() {
                            _propertyTree.refresh();
                        }
                    });
                }
            }

        };

        _owlListener = new IPropertyChangeListener() {

            // Listens to the events that change the namespace and update instance properties
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _propertyTree.getTree().getDisplay().syncExec(new Runnable() {
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
                OWLNamedObject elem = (OWLClass) array[0];
                if (elem.getIRI().toString().equals(_selectedRange) && array[1].equals(_ontologyUri) && array[2].equals(_projectId)) {
                    return;
                }
                _selectedRange = elem.getIRI().toString();
                _ontologyUri = (String) array[1];
                _projectId = (String) array[2];
                updateItems();
            }
        } else {
            _projectId = null;
            _selectedRange = ""; //$NON-NLS-1$
        }
    }

    public boolean update() {
        updateItems();
        return true;
    }

    private void updateItems() {
        if (_selectedRange == null || _ontologyUri == null) {
            return;
        }

        try {
            String[][] _propertyHits = new GetPropertiesForRangeHits(_projectId, _ontologyUri, _selectedRange).getResults();
             _items = new PropertyTreeElement[_propertyHits.length];
            
            ITreeDataProvider treeDataProvider = null;
            
            int i = 0;
            for (String[] hit: _propertyHits) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                boolean isImported = !ontologyUri.equals(_ontologyUri);
                OWLAxiom axiom = (OWLAxiom) OWLUtilities.axiom(axiomText, OWLNamespaces.EMPTY_INSTANCE, OWLModelFactory.getOWLDataFactory(_projectId));
                
                OWLEntity property;
                if(axiom instanceof OWLAnnotationPropertyRangeAxiom) {
                    property = ((OWLAnnotationPropertyRangeAxiom)axiom).getProperty();
                    _items[i] = new AnnotationPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider);
                    _items[i++].setIsImported(isImported);
                    
                } else if(axiom instanceof OWLDataPropertyRangeAxiom) {
                    try {
                        property = (OWLDataProperty)((OWLDataPropertyRangeAxiom)axiom).getProperty();
                        _items[i] = new DataPropertyTreeElement(property, _ontologyUri, _projectId, treeDataProvider);
                        _items[i++].setIsImported(isImported);
                    } catch (ClassCastException e) {
                        // ignore, in case of DataPropertyExpression
                        continue;
                    }
                    
                } else if(axiom instanceof OWLObjectPropertyRangeAxiom) {
                    try {
                        property = (OWLObjectProperty)((OWLObjectPropertyRangeAxiom)axiom).getProperty();
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
