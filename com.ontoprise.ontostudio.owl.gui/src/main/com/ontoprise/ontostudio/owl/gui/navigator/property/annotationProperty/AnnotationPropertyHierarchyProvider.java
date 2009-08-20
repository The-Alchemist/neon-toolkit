/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty;

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
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalOWLEntityTreeElementComparator;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetRootAnnotationProperties;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetSubAnnotationProperties;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * Provider for annotation properties and their hierarchy in the OntologyNavigator.
 */
public class AnnotationPropertyHierarchyProvider extends DefaultTreeDataProvider {

    protected static Logger _log = Logger.getLogger(AnnotationPropertyHierarchyProvider.class);

    private static final String FOLDER_NAME = "Annotation Properties"; //$NON-NLS-1$

    public boolean _listenersEnabled = true;
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
                                    AnnotationPropertyFolderTreeElement annotPropFolder = getFolder(ontologyId, projectId);
                                    getViewer().refresh(annotPropFolder);
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
                                                annotPropFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(annotPropFolder);
                                                if (_log.isDebugEnabled())
                                                    _log.debug("Refreshed imported " + o.getOntologyURI()); //$NON-NLS-1$
                                                getViewer().setExpandedElements(expandedElements);
                                            }
                                            for (OWLModel o: importingOntos) {
                                                expandedElements = getViewer().getExpandedElements();
                                                annotPropFolder = getFolder(o.getOntologyURI(), projectId);
                                                getViewer().refresh(annotPropFolder);
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

    public AnnotationPropertyHierarchyProvider() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#dispose()
     */
    @Override
    public void dispose() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildren(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public int getChildCount(ITreeElement parentElement) {
        // long x = System.currentTimeMillis();
        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();
        registerAxiomListener(projectId, ontologyId);

        int annotationPropCount = 0;

        //        String id = ""; //$NON-NLS-1$
        try {
            if (parentElement instanceof AnnotationPropertyFolderTreeElement) {
                annotationPropCount = new GetRootAnnotationProperties(projectId, ontologyId).getResultCount();
                //                id = "OWLAnnotationProperty folder"; //$NON-NLS-1$
            } else if (parentElement instanceof AnnotationPropertyTreeElement) {
                String parentId = ((AnnotationPropertyTreeElement) parentElement).getId();
                annotationPropCount = new GetSubAnnotationProperties(projectId, ontologyId, parentId).getResultCount();
                // id = parentId;
            }
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        // long y = System.currentTimeMillis();
        //        _log.debug("###PERFORMANCE### - Time AnnotationPropertyHierarchyProvider.getChildCount with " + id + ": " + (y - x)); //$NON-NLS-1$ //$NON-NLS-2$
        return annotationPropCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        //        _log.debug("###PERFORMANCE### - entering AnnotationPropertyHierarchyProvider.getElements()..."); //$NON-NLS-1$
        // long x = System.currentTimeMillis();
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();
        registerAxiomListener(projectId, ontologyId);

        try {
            String[] annotationPropUris;

            if (parentElement instanceof AnnotationPropertyFolderTreeElement) {
                annotationPropUris = new GetRootAnnotationProperties(projectId, ontologyId).getResults();

            } else {
                String parentId = ((AnnotationPropertyTreeElement) parentElement).getId();
                annotationPropUris = new GetSubAnnotationProperties(projectId, ontologyId, parentId).getResults();
            }

            List<AnnotationPropertyTreeElement> annotPropNodes = new ArrayList<AnnotationPropertyTreeElement>();
            for (String annotationProp: annotationPropUris) {
                OWLAnnotationProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLAnnotationProperty(OWLUtilities.toURI(annotationProp));
                annotPropNodes.add(new AnnotationPropertyTreeElement(prop, ontologyId, projectId, this));
            }
            Collections.sort(annotPropNodes, new AlphabeticalOWLEntityTreeElementComparator<AnnotationPropertyTreeElement>());
            // long y = System.currentTimeMillis();
            //            _log.debug("###PERFORMANCE### - leaving AnnotationPropertyHierarchyProvider.getElements() - Time: " + (y - x)); //$NON-NLS-1$
            return annotPropNodes.toArray(new AnnotationPropertyTreeElement[annotPropNodes.size()]);

        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new AnnotationPropertyTreeElement[0];
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return new AnnotationPropertyTreeElement[0];
        }
    }

    public AnnotationPropertyFolderTreeElement getFolder(String moduleId, String projectName) {
        AnnotationPropertyFolderProvider prov = (AnnotationPropertyFolderProvider) getExtensionHandler().getProvider(AnnotationPropertyFolderProvider.class);
        AnnotationPropertyFolderTreeElement el = new AnnotationPropertyFolderTreeElement(projectName, moduleId, prov);
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
            return getAnnotationPropertyImage();
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
            if (element instanceof AnnotationPropertyTreeElement) {
                AnnotationPropertyTreeElement prop = (AnnotationPropertyTreeElement) element;
                Set<OWLAnnotationProperty> superProps = OWLModelFactory.getOWLModel(prop.getOntologyUri(), prop.getProjectName()).getSuperAnnotationProperties(prop.getId());
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

    private boolean doGetPathElements(List<TreeElementPath> paths, AnnotationPropertyTreeElement clazz, Set<OWLAnnotationProperty> superProps, boolean finished) throws CloneNotSupportedException, NeOnCoreException {
        for (int i = 0; i < paths.size(); i++) {
            TreeElementPath currentPath = (TreeElementPath) paths.get(i);
            String topOfPath = ((AnnotationPropertyTreeElement) currentPath.get(0)).getId();
            for (OWLAnnotationProperty superProp: superProps) {
                Set<OWLAnnotationProperty> localSuperProps = OWLModelFactory.getOWLModel(clazz.getOntologyUri(), clazz.getProjectName()).getSuperAnnotationProperties(superProp.getURI().toString());
                if (topOfPath.equals(clazz.getId())) {
                    TreeElementPath clonedPath = (TreeElementPath) currentPath.clone();
                    AnnotationPropertyTreeElement parent = new AnnotationPropertyTreeElement(superProp, clazz.getOntologyUri(), clazz.getProjectName(), this);
                    currentPath.insert(0, parent);
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

    public void enableListeners() {
        _listenersEnabled = true;
    }

    public void disableListeners() {
        _listenersEnabled = false;
    }

    @SuppressWarnings("unchecked")
    private void registerAxiomListener(String projectId, String ontologyId) {
        try {
            Class[] clazzes = new Class[] {OWLAnnotationProperty.class, OWLAnnotation.class, OWLDeclarationAxiom.class};
            OWLModelFactory.getOWLModel(ontologyId, projectId).addAxiomListener(getAxiomListener(), clazzes);
        } catch (NeOnCoreException e1) {
            new NeonToolkitExceptionHandler().handleException(e1);
        }
    }

    private Image getFolderImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.FOLDER);
    }

    private Image getAnnotationPropertyImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
    }
}
