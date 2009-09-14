/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty;

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
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalOWLEntityTreeElementComparator;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetRootObjectProperties;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSubObjectProperties;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSuperObjectProperties;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * Provider for concepts and their hierarchy in the OntologyNavigator.
 */
public class ObjectPropertyHierarchyProvider extends DefaultTreeDataProvider {

    protected static Logger _log = Logger.getLogger(ObjectPropertyHierarchyProvider.class);

    private static final String FOLDER_NAME = "Object Properties"; //$NON-NLS-1$

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
                                    ObjectPropertyFolderTreeElement objPropFolder = getFolder(ontologyId, projectId);
                                    getViewer().refresh(objPropFolder);
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
                                                objPropFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(objPropFolder);
                                                if (_log.isDebugEnabled())
                                                    _log.debug("Refreshed imported " + o.getOntologyURI()); //$NON-NLS-1$
                                                getViewer().setExpandedElements(expandedElements);
                                            }
                                            for (OWLModel o: importingOntos) {
                                                expandedElements = getViewer().getExpandedElements();
                                                objPropFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(objPropFolder);
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

    public ObjectPropertyHierarchyProvider() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        try {
            if (_owlModel != null) {
                _owlModel.removeAxiomListener(getAxiomListener());
            }
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
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
            String[] subPropertyIds = new GetSubObjectProperties(projectId, ontologyId, parentId).getResults();
            List<ObjectPropertyTreeElement> objPropNodes = new ArrayList<ObjectPropertyTreeElement>();
            for (String objPropUri: subPropertyIds) {
                OWLObjectProperty objProp = OWLModelFactory.getOWLDataFactory(projectId).getOWLObjectProperty(OWLUtilities.toURI(objPropUri));
                objPropNodes.add(new ObjectPropertyTreeElement(objProp, ontologyId, projectId, this));
            }
            Collections.sort(objPropNodes, new AlphabeticalOWLEntityTreeElementComparator<ObjectPropertyTreeElement>());
            return objPropNodes.toArray(new ObjectPropertyTreeElement[subPropertyIds.length]);

        } catch (CommandException e) {
            OWLPlugin.logError(e);
            return new ObjectPropertyTreeElement[0];
        } catch (NeOnCoreException e) {
            OWLPlugin.logError(e);
            return new ObjectPropertyTreeElement[0];
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

        int objPropCount = 0;

        //        String id = ""; //$NON-NLS-1$
        try {
            if (parentElement instanceof ObjectPropertyFolderTreeElement) {
                objPropCount = new GetRootObjectProperties(projectId, ontologyId).getResultCount();
                //                id = "ObjectProperties folder"; //$NON-NLS-1$
            } else if (parentElement instanceof ObjectPropertyTreeElement) {
                String parentId = ((AbstractOwlEntityTreeElement) parentElement).getId();
                objPropCount = new GetSubObjectProperties(projectId, ontologyId, parentId).getResultCount();
                // id = parentId;
            }

        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        // long y = System.currentTimeMillis();
        //        _log.debug("###PERFORMANCE### - Time ObjectPropertyHierarchyProvider.getChildCount with " + id + ": " + (y - x)); //$NON-NLS-1$ //$NON-NLS-2$
        return objPropCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        //        _log.debug("###PERFORMANCE### - entering ObjectPropertyHierarchyProvider.getElements()..."); //$NON-NLS-1$
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
            String[] rootPropertyUris = new GetRootObjectProperties(projectId, ontologyId).getResults();
            List<ObjectPropertyTreeElement> objPropNodes = new ArrayList<ObjectPropertyTreeElement>();
            for (String objPropUri: rootPropertyUris) {
                OWLObjectProperty objProp = OWLModelFactory.getOWLDataFactory(projectId).getOWLObjectProperty(OWLUtilities.toURI(objPropUri));
                objPropNodes.add(new ObjectPropertyTreeElement(objProp, ontologyId, projectId, this));
            }
            Collections.sort(objPropNodes, new AlphabeticalOWLEntityTreeElementComparator<ObjectPropertyTreeElement>());
            // long y = System.currentTimeMillis();
            //            _log.debug("###PERFORMANCE### - leaving ObjectPropertyHierarchyProvider.getElements() - Time: " + (y - x)); //$NON-NLS-1$
            return objPropNodes.toArray(new ObjectPropertyTreeElement[rootPropertyUris.length]);
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new ObjectPropertyTreeElement[0];
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new ObjectPropertyTreeElement[0];
        }
    }

    public ObjectPropertyFolderTreeElement getFolder(String ontologyUri, String projectName) {
        ObjectPropertyFolderProvider prov = (ObjectPropertyFolderProvider) getExtensionHandler().getProvider(ObjectPropertyFolderProvider.class);
        ObjectPropertyFolderTreeElement el = new ObjectPropertyFolderTreeElement(projectName, ontologyUri, prov);
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
            return getObjectPropertyImage();
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
            if (element instanceof ObjectPropertyTreeElement) {
                ObjectPropertyTreeElement prop = (ObjectPropertyTreeElement) element;
                String[] superObjectProperties = new GetSuperObjectProperties(prop.getProjectName(), prop.getOntologyUri(), prop.getId()).getResults();
                paths.add(new TreeElementPath(new ITreeElement[] {element}));
                boolean finished = false;
                while (!finished) {
                    finished = true;
                    finished = doGetPathElements(paths, prop, superObjectProperties, finished);
                }

            }
        } catch (NeOnCoreException e) {
            return new TreeElementPath[0];
        } catch (CloneNotSupportedException e) {
            return new TreeElementPath[0];
        } catch (CommandException e) {
            return new TreeElementPath[0];
        }
        return (TreeElementPath[]) paths.toArray(new TreeElementPath[0]);
    }

    private boolean doGetPathElements(List<TreeElementPath> paths, ObjectPropertyTreeElement objectPropertyTreeElement, String[] superProps, boolean finished) throws CloneNotSupportedException, NeOnCoreException, CommandException {
        for (int i = 0; i < paths.size(); i++) {
            TreeElementPath currentPath = (TreeElementPath) paths.get(i);
            String topOfPath = ((ObjectPropertyTreeElement) currentPath.get(0)).getId();
            for (String superPropUri: superProps) {
                String[] localSuperProps = new GetSuperObjectProperties(objectPropertyTreeElement.getProjectName(), objectPropertyTreeElement.getOntologyUri(), superPropUri).getResults();
                if (topOfPath.equals(objectPropertyTreeElement.getId())) {
                    TreeElementPath clonedPath = (TreeElementPath) currentPath.clone();
                    OWLObjectProperty superProp = OWLModelFactory.getOWLDataFactory(objectPropertyTreeElement.getProjectName()).getOWLObjectProperty(OWLUtilities.toURI(superPropUri));
                    ObjectPropertyTreeElement parent = new ObjectPropertyTreeElement(superProp, objectPropertyTreeElement.getOntologyUri(), objectPropertyTreeElement.getProjectName(), this);
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
                        finished = doGetPathElements(paths, parent, localSuperProps, finished);
                    }
                }
            }
        }
        return finished;
    }

    @SuppressWarnings("unchecked")
    private void registerAxiomListener(String projectId, String ontologyId) {
        try {
            Class[] clazzes = new Class[] {OWLSubObjectPropertyOfAxiom.class, OWLEquivalentObjectPropertiesAxiom.class, OWLInverseObjectPropertiesAxiom.class, OWLDeclarationAxiom.class};
            OWLModelFactory.getOWLModel(ontologyId, projectId).addAxiomListener(getAxiomListener(), clazzes);
        } catch (NeOnCoreException e1) {
            new NeonToolkitExceptionHandler().handleException(e1);
        }
    }

    private Image getFolderImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.FOLDER);
    }

    private Image getObjectPropertyImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
    }
}
