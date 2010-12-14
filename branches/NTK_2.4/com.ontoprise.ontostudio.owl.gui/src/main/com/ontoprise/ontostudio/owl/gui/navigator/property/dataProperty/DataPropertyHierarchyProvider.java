/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.IEntityElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalOWLEntityTreeElementComparator;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetRootDataProperties;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetSubDataProperties;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * Provider for concepts and their hierarchy in the OntologyNavigator.
 */
public class DataPropertyHierarchyProvider extends DefaultTreeDataProvider {

    protected static Logger _log = Logger.getLogger(DataPropertyHierarchyProvider.class);

    private static final String FOLDER_NAME = "Datatype Properties"; //$NON-NLS-1$

    private OWLModel _owlModel;
    private OWLAxiomListener _axiomListener;

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
                                    DataPropertyFolderTreeElement dataPropFolder = getFolder(ontologyId, projectId);
                                    getViewer().refresh(dataPropFolder);
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
                                                dataPropFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(dataPropFolder);
                                                if (_log.isDebugEnabled())
                                                    _log.debug("Refreshed imported " + o.getOntologyURI()); //$NON-NLS-1$
                                                getViewer().setExpandedElements(expandedElements);
                                            }
                                            for (OWLModel o: importingOntos) {
                                                expandedElements = getViewer().getExpandedElements();
                                                dataPropFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(dataPropFolder);
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

    public DataPropertyHierarchyProvider() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
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
        assert parentElement instanceof IOntologyElement;
        assert parentElement instanceof IEntityElement;

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();
        try {
            _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
        } catch (NeOnCoreException e1) {
        }

        registerAxiomListener(projectId, ontologyId);

        try {
            String parentId = ((AbstractOwlEntityTreeElement) parentElement).getId();

            String[] subPropertyUris = new GetSubDataProperties(projectId, ontologyId, parentId).getResults();
            List<DataPropertyTreeElement> dataPropNodes = new ArrayList<DataPropertyTreeElement>();
            int i = 0;
            for (String dataPropUri: subPropertyUris) {
                OWLDataProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLDataProperty(OWLUtilities.toIRI(dataPropUri));
                dataPropNodes.add(new DataPropertyTreeElement(prop, ontologyId, projectId, this));
                i++;
            }
            Collections.sort(dataPropNodes, new AlphabeticalOWLEntityTreeElementComparator<DataPropertyTreeElement>());
            return dataPropNodes.toArray(new DataPropertyTreeElement[dataPropNodes.size()]);

        } catch (CommandException e) {
            OWLPlugin.logError(e);
            return new DataPropertyTreeElement[0];
        } catch (NeOnCoreException e) {
            OWLPlugin.logError(e);
            return new DataPropertyTreeElement[0];
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public int getChildCount(ITreeElement parentElement) {
        // long x = System.currentTimeMillis();
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();
        try {
            _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
        } catch (NeOnCoreException e1) {
        }

        registerAxiomListener(projectId, ontologyId);

        int dataPropCount = 0;

        //        String id = ""; //$NON-NLS-1$
        try {
            if (parentElement instanceof DataPropertyFolderTreeElement) {
                dataPropCount = new GetRootDataProperties(projectId, ontologyId).getResultCount();
                //                id = "DataProperty folder"; //$NON-NLS-1$
            } else if (parentElement instanceof DataPropertyTreeElement) {
                String parentId = ((AbstractOwlEntityTreeElement) parentElement).getId();
                dataPropCount = new GetSubDataProperties(projectId, ontologyId, parentId).getResultCount();
                // id = parentId;
            }
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }

        // long y = System.currentTimeMillis();
        //        _log.debug("###PERFORMANCE### - Time DataPropertyHierarchyProvider.getChildCount with " + id + ": " + (y - x)); //$NON-NLS-1$ //$NON-NLS-2$
        return dataPropCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        //        _log.debug("###PERFORMANCE### - entering DataPropertyHierarchyProvider.getElements()..."); //$NON-NLS-1$
        // long x = System.currentTimeMillis();
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();
        try {
            _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
        } catch (NeOnCoreException e1) {
        }

        registerAxiomListener(projectId, ontologyId);

        try {
            String[] subPropertyUris = new GetRootDataProperties(projectId, ontologyId).getResults();
            List<DataPropertyTreeElement> dataPropNodes = new ArrayList<DataPropertyTreeElement>();
            for (String dataPropUri: subPropertyUris) {
                OWLDataProperty dataProp = OWLModelFactory.getOWLDataFactory(projectId).getOWLDataProperty(OWLUtilities.toIRI(dataPropUri));
                dataPropNodes.add(new DataPropertyTreeElement(dataProp, ontologyId, projectId, this));
            }
            Collections.sort(dataPropNodes, new AlphabeticalOWLEntityTreeElementComparator<DataPropertyTreeElement>());
            // long y = System.currentTimeMillis();
            //            _log.debug("###PERFORMANCE### - leaving DataPropertyHierarchyProvider.getElements() - Time: " + (y - x)); //$NON-NLS-1$
            return dataPropNodes.toArray(new DataPropertyTreeElement[dataPropNodes.size()]);

        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new DataPropertyTreeElement[0];
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new DataPropertyTreeElement[0];
        }

    }

    public DataPropertyFolderTreeElement getFolder(String ontologyUri, String projectName) {
        DataPropertyFolderProvider prov = (DataPropertyFolderProvider) getExtensionHandler().getProvider(DataPropertyFolderProvider.class);
        DataPropertyFolderTreeElement el = new DataPropertyFolderTreeElement(projectName, ontologyUri, prov);
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
            return getDataPropertyImage();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getText(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public String getText(ITreeElement element) {
        return element.toString();
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
            if (element instanceof DataPropertyTreeElement) {
                DataPropertyTreeElement prop = (DataPropertyTreeElement) element;
                Set<OWLDataProperty> superProps = OWLModelFactory.getOWLModel(prop.getOntologyUri(), prop.getProjectName()).getSuperDataProperties(prop.getId());
                paths.add(new TreeElementPath(new ITreeElement[] {element}));
                boolean finished = false;
                while (!finished) {
                    finished = true;
                    finished = doGetPathElements(paths, prop, superProps, finished);
                }

            }
        } catch (NeOnCoreException e) {
            return new TreeElementPath[0];
        } catch (CloneNotSupportedException e) {
            return new TreeElementPath[0];
        }
        return (TreeElementPath[]) paths.toArray(new TreeElementPath[0]);
    }

    private boolean doGetPathElements(List<TreeElementPath> paths, DataPropertyTreeElement dataProp, Set<OWLDataProperty> superProps, boolean finished) throws CloneNotSupportedException, NeOnCoreException {
        for (int i = 0; i < paths.size(); i++) {
            TreeElementPath currentPath = (TreeElementPath) paths.get(i);
            String topOfPath = ((DataPropertyTreeElement) currentPath.get(0)).getId();
            for (OWLDataProperty superProp: superProps) {
                Set<OWLDataProperty> localSuperProps = OWLModelFactory.getOWLModel(dataProp.getOntologyUri(), dataProp.getProjectName()).getSuperDataProperties(superProp.getIRI().toString());
                if (topOfPath.equals(dataProp.getId())) {
                    TreeElementPath clonedPath = (TreeElementPath) currentPath.clone();
                    DataPropertyTreeElement parent = new DataPropertyTreeElement(superProp, dataProp.getOntologyUri(), dataProp.getProjectName(), this);
                    List<ITreeElement> currentElementList = new ArrayList<ITreeElement>();
                    ITreeElement[] currentElements = currentPath.toArray();
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
                        finished = doGetPathElements(paths, parent, localSuperProps, finished);
                    }
                }
            }
        }
        return finished;
    }

    @SuppressWarnings("unchecked")
    private void registerAxiomListener(String projectId, String ontologyId) {
        Class[] clazzes = new Class[] {OWLSubDataPropertyOfAxiom.class, OWLEquivalentDataPropertiesAxiom.class, OWLDeclarationAxiom.class};
        try {
            _owlModel.addAxiomListener(getAxiomListener(), clazzes);
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private Image getFolderImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.FOLDER);
    }

    private Image getDataPropertyImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
    }
}
