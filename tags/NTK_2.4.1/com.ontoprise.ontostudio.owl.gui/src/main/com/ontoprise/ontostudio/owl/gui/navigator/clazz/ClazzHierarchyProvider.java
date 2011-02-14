/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.clazz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.AbstractComplexTreeViewer;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalOWLEntityTreeElementComparator;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetRootClazzes;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSubClazzes;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * Provider for classes and their hierarchy in the OntologyNavigator.
 */

public class ClazzHierarchyProvider extends DefaultTreeDataProvider {

    protected static Logger _log = Logger.getLogger(ClazzHierarchyProvider.class);

    private static final String FOLDER_NAME = Messages.ClazzFolderTreeElement_0;

    private OWLModel _owlModel;
    private OWLAxiomListener _axiomListener;
    private IPropertyChangeListener _propertyChangeListener;

    protected OWLAxiomListener getAxiomListener() {
        if (_axiomListener == null) {
            _axiomListener = new OWLAxiomListener() {

                public void modelChanged(final OWLChangeEvent event) {
                    BusyIndicator.showWhile(null, new Runnable() {
                        public void run() {
                            getViewer().getControl().getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    OWLOntology onto = event.getChangedOntology();
                                    String ontologyId = OWLUtilities.toString(onto.getOntologyID());
                                    String projectId = event.getProjectId();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("modelChanged() called with " + OWLUtilities.toString(onto.getOntologyID())); //$NON-NLS-1$
                                    }
                                    Object[] expandedElements = getViewer().getExpandedElements();
                                    ClazzFolderTreeElement classesFolder = getFolder(ontologyId, projectId);
                                    getViewer().refresh(classesFolder);
                                    if (_log.isDebugEnabled())
                                        _log.debug("Refreshed " + ontologyId); //$NON-NLS-1$
                                    getViewer().setExpandedElements(expandedElements);
                                    // also refresh imported and importing ontologies
                                    try {
                                        boolean showImported = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_IMPORTED);
                                        if (showImported) {
                                            Set<OWLModel> importedOntos = OWLModelFactory.getOWLModel(ontologyId, projectId).getAllImportedOntologies();
                                            Set<OWLModel> importingOntos = OWLModelFactory.getOWLModel(ontologyId, projectId).getAllImportingOntologies();

                                            for (OWLModel o: importedOntos) {
                                                expandedElements = getViewer().getExpandedElements();
                                                classesFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(classesFolder);
                                                if (_log.isDebugEnabled())
                                                    _log.debug("Refreshed imported " + o.getOntologyURI()); //$NON-NLS-1$
                                                getViewer().setExpandedElements(expandedElements);
                                            }
                                            for (OWLModel o: importingOntos) {
                                                expandedElements = getViewer().getExpandedElements();
                                                classesFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(classesFolder);
                                                if (_log.isDebugEnabled())
                                                    _log.debug("Refreshed importing " + o.getOntologyURI()); //$NON-NLS-1$
                                                getViewer().setExpandedElements(expandedElements);
                                            }
                                        }
                                    } catch (NeOnCoreException e) {
                                        new NeonToolkitExceptionHandler().handleException(e);
                                    }
                                }
                            });
                        }
                    });
                }
            };
        }
        return _axiomListener;
    }

    private IPropertyChangeListener getPropertyChangeListener() {
        if (_propertyChangeListener == null) {
            _propertyChangeListener = new IPropertyChangeListener() {

                // Listeners for the events that change the namespace and display
                // language settings
                public void propertyChange(final PropertyChangeEvent event) {
                    if (event.getProperty().equals(OWLModelPlugin.SHOW_IMPORTED)) {
                        getViewer().getControl().getDisplay().syncExec(new Runnable() {
                            public void run() {
                                AbstractComplexTreeViewer viewer = getViewer();
                                TreeItem[] items = viewer.getTree().getItems();
                                for (TreeItem item: items) {
                                    if (item.getData() instanceof OWLProjectTreeElement) {
                                        TreeItem[] children = item.getItems();
                                        for (TreeItem child: children) {
                                            if (child.getData() instanceof OntologyTreeElement) {
                                                OntologyTreeElement ontoTreeElement = (OntologyTreeElement) child.getData();
                                                boolean importsOntology = false;
                                                try {
                                                    Set<OWLModel> importedOntos = OWLModelFactory.getOWLModel(ontoTreeElement.getOntologyUri(), ontoTreeElement.getProjectName()).getAllImportedOntologies();
                                                    if (importedOntos != null && importedOntos.size() > 0) {
                                                        importsOntology = true;
                                                    }
                                                } catch (NeOnCoreException e) {
                                                    // nothing to do, assume no imports
                                                }
                                                if (importsOntology) {
                                                    getViewer().collapseToLevel(ontoTreeElement, 0);
                                                    getViewer().refresh(ontoTreeElement);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } else if (event.getProperty().equals(OWLPlugin.NAMESPACE_PREFERENCE)) {
                        BusyIndicator.showWhile(null, new Runnable() {
                            public void run() {
                                getViewer().updateLabels();
                            }
                        });
                    }
                }
            };
        }
        return _propertyChangeListener;
    }

    public ClazzHierarchyProvider() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        IPreferenceStore store = OWLModelPlugin.getDefault().getPreferenceStore();
        store.removePropertyChangeListener(getPropertyChangeListener());
        if (_owlModel != null) {
            try {
                _owlModel.removeAxiomListener(getAxiomListener());
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildren(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount) {
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();

        try {
            _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
            registerListeners(projectId, ontologyId);
        } catch (NeOnCoreException e1) {
            // ignore
        }
        
//        int clazzCount = 0;
//        try {
//            if (parentElement instanceof ClazzFolderTreeElement) {
//                clazzCount = new GetRootClazzes(projectId, ontologyId).getResultCount();
//            } else if (parentElement instanceof ClazzTreeElement) {
//                String parentId = ((AbstractOwlEntityTreeElement) parentElement).getId();
//                clazzCount = new GetSubClazzes(projectId, ontologyId, parentId).getResultCount();
//            }
//        } catch (CommandException e) {
//            new NeonToolkitExceptionHandler().handleException(e);
//        }        
        try {
            String parentId = ((AbstractOwlEntityTreeElement) parentElement).getId();
            String[] subClazzUris = new GetSubClazzes(projectId, ontologyId, parentId).getResults();
            ArrayList<ClazzTreeElement> clazzNodesList = new ArrayList<ClazzTreeElement>();
            
            for (String clazzUri: subClazzUris) {
                ClazzTreeElement cte = null;
                if (clazzUri.equals(OWLConstants.OWL_THING_URI)) {
                    Set<OWLClass> subElems = _owlModel.getAllSubClasses(clazzUri);
                    for (Iterator<OWLClass> iter = subElems.iterator(); iter.hasNext();) {
                        OWLClass element = (OWLClass) iter.next();
                        cte = new ClazzTreeElement(element, ontologyId, projectId, this);
                        clazzNodesList.add(cte);
                    }
                } else {
                    OWLClass clazz = OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toIRI(clazzUri));
                    cte = new ClazzTreeElement(clazz, ontologyId, projectId, this);
                    clazzNodesList.add(cte);
                }
            }
            Collections.sort(clazzNodesList, new AlphabeticalOWLEntityTreeElementComparator<ClazzTreeElement>());
            return clazzNodesList.toArray(new ClazzTreeElement[clazzNodesList.size()]);

        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new ClazzTreeElement[0];
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new ClazzTreeElement[0];
        }
    }

    @SuppressWarnings("unchecked")
    private void registerListeners(String projectId, String ontologyId) {
        Class[] clazzes = new Class[] {OWLSubClassOfAxiom.class, OWLEquivalentClassesAxiom.class, OWLDeclarationAxiom.class, OWLDataPropertyDomainAxiom.class, OWLObjectPropertyDomainAxiom.class, OWLObjectPropertyRangeAxiom.class, OWLClassAssertionAxiom.class};
        // AxiomListener for changed axioms
        try {
            _owlModel.addAxiomListener(getAxiomListener(), clazzes);
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }

        // Listener for preference changes
        IPreferenceStore modelStore = OWLModelPlugin.getDefault().getPreferenceStore();
        modelStore.addPropertyChangeListener(getPropertyChangeListener());

        IPreferenceStore guiStore = OWLPlugin.getDefault().getPreferenceStore();
        guiStore.addPropertyChangeListener(getPropertyChangeListener());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public int getChildCount(ITreeElement parentElement) {
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();

        try {
            _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
            registerListeners(projectId, ontologyId);
        } catch (NeOnCoreException e1) {
            // ignore
        }
        
        int clazzCount = 0;

        try {
            if (parentElement instanceof ClazzFolderTreeElement) {
                clazzCount = new GetRootClazzes(projectId, ontologyId).getResultCount();
            } else if (parentElement instanceof ClazzTreeElement) {
                String parentId = ((AbstractOwlEntityTreeElement) parentElement).getId();
                clazzCount = new GetSubClazzes(projectId, ontologyId, parentId).getResultCount();
            }
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }

        return clazzCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();

        try {
            _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
            registerListeners(projectId, ontologyId);
        } catch (NeOnCoreException e1) {
        }
        ArrayList<ClazzTreeElement> clazzNodesList = new ArrayList<ClazzTreeElement>();

        try {
            String[] rootClazzUris = new GetRootClazzes(projectId, ontologyId).getResults();
            List<OWLClass> rootClazzes = new ArrayList<OWLClass>();

            for (String uri: rootClazzUris) {
                rootClazzes.add(OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toIRI(uri)));
            }

            for (OWLClass clazz: rootClazzes) {
                ClazzTreeElement cte = new ClazzTreeElement(clazz, ontologyId, projectId, this);
                clazzNodesList.add(cte);
            }

        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new ITreeElement[0];
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new ITreeElement[0];
        }

        Collections.sort(clazzNodesList, new AlphabeticalOWLEntityTreeElementComparator<ClazzTreeElement>());
        return clazzNodesList.toArray(new ClazzTreeElement[clazzNodesList.size()]);
    }

//    private int getNamedClazzesCount(Set<OWLClassExpression> superDescriptions) {
//        int counter = 0;
//        for (OWLClassExpression desc: superDescriptions) {
//            if (desc instanceof OWLClass) {
//                counter++;
//            }
//        }
//        return counter;
//    }

    public ClazzFolderTreeElement getFolder(String ontologyUri, String projectName) {
        ClazzFolderProvider prov = (ClazzFolderProvider) getExtensionHandler().getProvider(ClazzFolderProvider.class);
        ClazzFolderTreeElement el = new ClazzFolderTreeElement(projectName, ontologyUri, prov);
        return el;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getImage(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public Image getImage(ITreeElement element) {
        if (FOLDER_NAME.equals(element.toString())) {
            return getFolderImage();
        } else {
            return getClazzImage();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getText(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public String getText(ITreeElement element) {
        return ((AbstractOwlEntityTreeElement) element).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDragSupported()
     */
    public boolean isDragSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDropSupported()
     */
    public boolean isDropSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getPathElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public TreeElementPath[] getPathElements(ITreeElement element) {
        List<TreeElementPath> paths = new ArrayList<TreeElementPath>();
        try {
            if (element instanceof ClazzTreeElement) {
                ClazzTreeElement clazz = (ClazzTreeElement) element;
                Set<OWLClass> superClazzes = OWLModelFactory.getOWLModel(clazz.getOntologyUri(), clazz.getProjectName()).getSuperClassesInClassHierarchy(clazz.getId());
                paths.add(new TreeElementPath(new ITreeElement[] {element}));
                boolean finished = false;
                while (!finished) {
                    finished = true;
                    finished = doGetPathElements(paths, clazz, superClazzes, finished);
                }

            }
        } catch (NeOnCoreException e) {
            return new TreeElementPath[0];
        } catch (CloneNotSupportedException e) {
            return new TreeElementPath[0];
        }
        return (TreeElementPath[]) paths.toArray(new TreeElementPath[0]);
    }

    private boolean doGetPathElements(List<TreeElementPath> paths, ClazzTreeElement clazz, Set<OWLClass> superClazzes, boolean finished) throws CloneNotSupportedException, NeOnCoreException {
        for (int i = 0; i < paths.size(); i++) {
            TreeElementPath currentPath = (TreeElementPath) paths.get(i);
            String topOfPath = ((ClazzTreeElement) currentPath.get(0)).getId();
            for (OWLClass superClazz: superClazzes) {
                Set<OWLClass> localSuperClazzes = OWLModelFactory.getOWLModel(clazz.getOntologyUri(), clazz.getProjectName()).getSuperClassesInClassHierarchy(superClazz.getIRI().toString());
                if (topOfPath.equals(clazz.getId())) {
                    TreeElementPath clonedPath = (TreeElementPath) currentPath.clone();
                    ClazzTreeElement parent = new ClazzTreeElement(superClazz, clazz.getOntologyUri(), clazz.getProjectName(), this);
                    ITreeElement[] currentElements = currentPath.toArray();
                    List<ITreeElement> currentElementList = new ArrayList<ITreeElement>();
                    for (ITreeElement element: currentElements) {
                        currentElementList.add(element);
                    }
                    if (!currentElementList.contains(parent)) {
                        currentPath.insert(0, parent);
                    } else {
                        return true;
                    }
                    finished = false;
                    if (!paths.contains(currentPath)) {
                        paths.add(currentPath);
                    }
                    currentPath = clonedPath;
                    while (!finished) {
                        finished = true;
                        finished = doGetPathElements(paths, parent, localSuperClazzes, finished);
                    }
                }
            }
        }
        return finished;
    }

    private Image getClazzImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
    }

    private Image getFolderImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.FOLDER);
    }
}
