/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.properties.EntityPropertiesView;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzFolderTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetIndividuals;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * 
 * @author Nico Stieler
 */
public class IndividualViewContentProvider implements IStructuredContentProvider,ITreeContentProvider {

    public static final int INDENT = 5;

    /**
     * The items to display;
     */
//    private AbstractOwlEntityTreeElement[] _items;
    private IIndividualTreeElement[] _items;

    /**
     * The view displaying the individuals (an instance of IndividualView)
     */
    private TreeViewer _individualTree;
    private IPropertyChangeListener _guiListener;
    private IPropertyChangeListener _owlListener;
    private IPreferenceStore _guiStore;
    private IPreferenceStore _owlStore;

    String _selectedClazz;
    String _ontologyUri;
    String _projectId;

    /**
     * An temporary item for inserting new individuals
     */
    protected IIndividualTreeElement _newItem;
    protected int _style;

    private OWLAxiomListener _axiomListener;

    protected OWLAxiomListener getAxiomListener() {
        if (_axiomListener == null) {
            _axiomListener = new OWLAxiomListener() {

                @Override
                public void modelChanged(OWLChangeEvent event) {

                    _individualTree.getTree().getDisplay().syncExec(new Runnable() {
                        public void run() {
                            forceUpdate();
                            _individualTree.refresh();
                        }
                    });

                }

            };
        }
        return _axiomListener;
    }

    public IndividualViewContentProvider(TreeViewer treeView) {
        this._individualTree = treeView;
        _guiStore = NeOnUIPlugin.getDefault().getPreferenceStore();
        _owlStore = OWLPlugin.getDefault().getPreferenceStore();
        _guiListener = new IPropertyChangeListener() {

            // Listenes to the events that change the namespace and update instance properties
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _individualTree.getTree().getDisplay().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            // refreshElements();
                            // setDirty();
                            _individualTree.refresh();
                        }
                    });
                }
            }
        };

        _owlListener = new IPropertyChangeListener() {
            // Listenes to the events that change the namespace and update instance properties
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    _individualTree.getTree().getDisplay().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            _individualTree.refresh();
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
        // String projectId = ((IProjectElement)parent).getProjectName();
        // String ontologyId = ((IModuleElement)parent).getOntologyUri();
        //
        // registerAxiomListener(projectId, ontologyId);
        if (_items == null) {
            return new IIndividualTreeElement[0];
        }
        return _items;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof Object[]) {
            Object[] array = (Object[]) newInput;
            if (array[0] instanceof ClazzTreeElement) {
                ClazzTreeElement elem = (ClazzTreeElement) array[0];
                if (array[1].equals(_projectId) && elem.getId().equals(_selectedClazz) && elem.getOntologyUri().equals(_ontologyUri)) {
                    return;
                }
                _projectId = (String) array[1];
                _selectedClazz = elem.getId();
                _ontologyUri = elem.getOntologyUri();
                updateItems();
            } else if (array[0] instanceof ClazzFolderTreeElement) {
                ClazzFolderTreeElement elem = (ClazzFolderTreeElement) array[0];
                _projectId = (String) array[1];
                _selectedClazz = OWLConstants.OWL_THING_URI;
                _ontologyUri = elem.getOntologyUri();
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
            String[] individualUris = new GetIndividuals(_projectId, _ontologyUri, _selectedClazz).getResults();
            IIndividualTreeElement[] oldItems = (IIndividualTreeElement[]) getElements(null);
            _items = new IIndividualTreeElement[individualUris.length];

            int i = 0;
            for (String individualUri: individualUris) {
                OWLOntology ontology = OWLModelFactory.getOWLModel(_ontologyUri, _projectId).getOntology();
//                if(individualUri.startsWith("_:"))individualUri = individualUri.substring(2); //$NON-NLS-1$
//                OWLIndividual individual = OWLModelFactory.getOWLDataFactory(_projectId).getOWLNamedIndividual(OWLUtilities.toIRI(individualUri));
                boolean newIndividuum = true;
                int oldPos = 0;
                for(oldPos = 0 ; oldPos < oldItems.length ; oldPos++){
                    if(oldItems[oldPos] != null && OWLUtilities.toString(oldItems[oldPos].getIndividual(), ontology).equals(i)){
                        newIndividuum = false;
                        break;
                    }
                }
                if(newIndividuum){
                    OWLIndividual individual = OWLUtilities.individual(individualUri, ontology);
                    _items[i++] = IndividualItem.createNewInstance(individual, _selectedClazz, _ontologyUri, _projectId);
                }else{
                    _items[i++] = oldItems[oldPos];
                    oldItems[oldPos] = null;
                }
            }
            
            if(oldItems != null && oldItems.length > 0 && oldItems[0] != null && _items[0] != null){
                System.err.println("select " + _items[0] + " or its class " + _items[0].getClazz());
            }
            Arrays.sort(_items, new Comparator<IIndividualTreeElement>() {
                @Override
                public int compare(IIndividualTreeElement o1, IIndividualTreeElement o2) {
                    return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
                }
            }
            );
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

    /**
     * Called if the editing of a newly inserted individual is finished. The temporary item will be deleted and the individual will be stored in the datamodel.
     * 
     * @throws ControlException
     * @throws NeOnCoreException
     */
    public void editingFinished() throws CommandException, NeOnCoreException {
        if (_newItem == null) {
            return;
        }
        String newURI = _newItem.getId(); 
        Set<OWLEntity> entities = OWLModelFactory.getOWLModel(_ontologyUri, _projectId).getEntity(newURI);
        for (OWLEntity entity: entities) {
            if (entity.getIRI().toString().equals(newURI)) {
                if(!(entity instanceof OWLIndividual)) {
                    MessageDialog.openInformation(_individualTree.getTree().getShell(), Messages.NewClazzHandler_0, Messages.NewClazzHandler_1);
                    editingCancelled();
                    _newItem = null;
                    return;
                }
            }
        }

        new CreateIndividual(_projectId, _ontologyUri, _newItem.getClazz(), newURI).perform();

    }

    /**
     * Called if the editing of a newly inserted instance is cancelled. The temporary item will be deleted and the instance will not be stored in the datamodel.
     * 
     * @throws ControlException
     */
    public void editingCancelled() {
        if (_newItem == null) {
            return;
        }
        Object[] oldItems = _items;
        _items = new IIndividualTreeElement[oldItems.length - 1];
        System.arraycopy(oldItems, 1, _items, 0, _items.length);
        _newItem = null;
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
     * Returns the current class the view is displaying the individuals of.
     */
    public String getSelectedClazz() {
        return _selectedClazz;
    }

    /**
     * Returns the module of the concept the view is displaying the instances of.
     * 
     * @return
     */
    public String getSelectedModule() {
        return _ontologyUri;
    }

    /**
     * Creates the temporary item for inserting new individuals
     */
    public IIndividualTreeElement<OWLNamedIndividual> createNewEditableItem() {
        update(0, 60);
        Object[] oldItems = _items;
        String localName = Messages.IndividualViewContentProvider_0 + System.currentTimeMillis(); 
        String uri = localName;
        String newUri = ""; //$NON-NLS-1$
        OWLModel owlModel = null;
        try {
            owlModel = OWLModelFactory.getOWLModel(_ontologyUri, _projectId);
            newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(uri, owlModel);
        } catch (NeOnCoreException e) {
            newUri = uri;
        }
        OWLNamedIndividual individual;
        try {
            individual = OWLModelFactory.getOWLDataFactory(_projectId).getOWLNamedIndividual(OWLUtilities.toIRI(newUri));
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
        _newItem = new NamedIndividualViewItem(individual, _selectedClazz, _ontologyUri, _projectId); 
        _items = new IIndividualTreeElement[oldItems.length + 1];
        _items[0] = _newItem;
        System.arraycopy(oldItems, 0, _items, 1, oldItems.length);
        return _newItem;
    }

    public boolean update(int index, int amount) {
        if (_items == null) {
            return false;
        }
        for (int i = index; i <= index + amount && i < _items.length; i++) {
            if ("".equals(_items[i])) { //$NON-NLS-1$
                int lowerIndex = _newItem == null ? Math.max(index - INDENT, 0) : Math.max(index - INDENT, 1);
                for (int j = 0; j < lowerIndex; j++) {
                    _items[j] = null;
                }
                updateItems(lowerIndex, amount + 2 * INDENT);
                for (int j = lowerIndex + amount + 2 * INDENT; j < _items.length; j++) {
                    _items[j] = null;
                }
                return true;
            }
        }
        return false;
    }

    private void updateItems(int index, int amount) {
        try {
            if (_selectedClazz == null || _ontologyUri == null) {
                return;
            }
            List<IIndividualTreeElement> directInstances = null;
            directInstances = new ArrayList<IIndividualTreeElement>();
            String[] individualUris = new GetIndividuals(_projectId, _ontologyUri, _selectedClazz).getResults();
            for (String individualUri: individualUris) {
                OWLIndividual individual = OWLUtilities.individual(individualUri, OWLModelFactory.getOWLModel(_ontologyUri, _projectId).getOntology());
                directInstances.add(IndividualItem.createNewInstance(individual, _selectedClazz, _ontologyUri, _projectId));
            }
            _items = new IIndividualTreeElement[directInstances.size()];
            int i = 0;
            for (IIndividualTreeElement ind: directInstances) {
                _items[i] = ind;
                i++;
            }
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
    }

    public void setStyle(int style) {
        _style = style;
        updateItems(0, 60);
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
