/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalOWLEntityTreeElementComparator;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;
import com.ontoprise.ontostudio.owl.model.event.OWLChangeEvent;

/**
 * Provider for classes and their hierarchy in the OntologyNavigator.
 */

public class DatatypeProvider extends DefaultTreeDataProvider {

    private static final String FOLDER_NAME = Messages.DatatypeFolderTreeElement_0;

    private OWLModel _owlModel;
    private OWLAxiomListener _axiomListener;

    protected OWLAxiomListener getAxiomListener() {
        if (_axiomListener == null) {
            _axiomListener = new OWLAxiomListener() {

                @Override
                public void modelChanged(final OWLChangeEvent event) {
                    // in OWL mode it is impossible to check all OntologyChangeEvent that could
                    // occur here, and remove/add single tree elements accordingly, so we simply
                    // refresh the whole tree.
                    BusyIndicator.showWhile(null, new Runnable() {
                        @Override
                        public void run() {
                            getViewer().getControl().getDisplay().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    boolean refresh = false;
                                    for (OWLEntity entity: event.getPotentiallyAddedEntities()) {
                                        if (entity instanceof OWLDatatype) {
                                            refresh = true;
                                        }
                                    }
                                    for (OWLEntity entity: event.getRemovedEntities()) {
                                        if (entity instanceof OWLDatatype) {
                                            refresh = true;
                                        }
                                    }
                                    if (refresh) {
                                        MTreeView navigator = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
                                        StructuredSelection selection = (StructuredSelection) navigator.getTreeViewer().getSelection();
                                        Object selectedElement = selection.getFirstElement();
                                        if(selectedElement != null) {
                                            String sourceOntoUri = OWLUtilities.toString(event.getSourceOntology().getOntologyID());
                                            String sourceProject = event.getProjectId();
                                            String projectId = ((IProjectElement) selectedElement).getProjectName();
                                            String ontologyId = ((IOntologyElement) selectedElement).getOntologyUri();
                                            if (ontologyId.equals(sourceOntoUri) && sourceProject.equals(projectId)) {
                                                Object[] expandedElements = getViewer().getExpandedElements();
                                                DatatypeFolderTreeElement objPropFolder = getFolder(sourceOntoUri, projectId);
                                                getViewer().refresh(objPropFolder);
                                                getViewer().setExpandedElements(expandedElements);
                                            }
                                        }
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

    public DatatypeProvider() {
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
    @Override
    public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount) {
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();

        try {
            if (_owlModel == null) {
                _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
            }
        } catch (NeOnCoreException e1) {
        }
        registerAxiomListener(projectId, ontologyId);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void registerAxiomListener(String projectId, String ontologyId) {
        try {
            Class[] clazzes = new Class[] {OWLDatatype.class, OWLDeclarationAxiom.class};
            OWLModelFactory.getOWLModel(ontologyId, projectId).addAxiomListener(getAxiomListener(), clazzes);
        } catch (NeOnCoreException e1) {
            new NeonToolkitExceptionHandler().handleException(e1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public int getChildCount(ITreeElement parentElement) {
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();
        try {
            if (_owlModel == null) {
                _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
            }
        } catch (NeOnCoreException e1) {
        }

        registerAxiomListener(projectId, ontologyId);
        int datatypeCount = 0;
        if (parentElement instanceof DatatypeFolderTreeElement) {
            try {
                datatypeCount = OWLModelFactory.getOWLModel(ontologyId, projectId).getAllDatatypes().size();
            } catch (NeOnCoreException e) {
                OWLPlugin.logError(e);
            }
        }

        return datatypeCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    @Override
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();
        try {
            if (_owlModel == null) {
                _owlModel = OWLModelFactory.getOWLModel(ontologyId, projectId);
            }
        } catch (NeOnCoreException e1) {
        }

        registerAxiomListener(projectId, ontologyId);
        Set<OWLDatatype> datatypes = new HashSet<OWLDatatype>();
        try {
            datatypes = OWLModelFactory.getOWLModel(ontologyId, projectId).getAllDatatypes();
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        DatatypeTreeElement[] treeElements = new DatatypeTreeElement[datatypes.size()];
        int i = 0;
        for (OWLDatatype type: datatypes) {
            treeElements[i] = new DatatypeTreeElement(type, ontologyId, projectId, this);
            i++;
        }
        Arrays.sort(treeElements, new AlphabeticalOWLEntityTreeElementComparator<DatatypeTreeElement>());
        return treeElements;
    }

    public DatatypeFolderTreeElement getFolder(String ontologyUri, String projectName) {
        DatatypeFolderProvider prov = (DatatypeFolderProvider) getExtensionHandler().getProvider(DatatypeFolderProvider.class);
        DatatypeFolderTreeElement el = new DatatypeFolderTreeElement(projectName, ontologyUri, prov);
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
            return getDatatypeImage();
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
    @Override
    public boolean isDragSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDropSupported()
     */
    @Override
    public boolean isDropSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getPathElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public TreeElementPath[] getPathElements(ITreeElement element) {
        List<TreeElementPath> paths = new ArrayList<TreeElementPath>();
        try {
            if (element instanceof DatatypeTreeElement) {
                DatatypeTreeElement prop = (DatatypeTreeElement) element;
                Set<OWLDatatype> superProps = Collections.emptySet();
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

    private boolean doGetPathElements(List<TreeElementPath> paths, DatatypeTreeElement datatypeTreeElement, Set<OWLDatatype> superProps, boolean finished) throws CloneNotSupportedException, NeOnCoreException {
        for (int i = 0; i < paths.size(); i++) {
            TreeElementPath currentPath = (TreeElementPath) paths.get(i);
            String topOfPath = ((DatatypeTreeElement) currentPath.get(0)).getId();
            for (OWLDatatype superProp: superProps) {
                Set<OWLDatatype> localSuperProps = Collections.emptySet();
                if (topOfPath.equals(datatypeTreeElement.getId())) {
                    TreeElementPath clonedPath = (TreeElementPath) currentPath.clone();
                    DatatypeTreeElement parent = new DatatypeTreeElement(superProp, datatypeTreeElement.getOntologyUri(), datatypeTreeElement.getProjectName(), this);
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

    private Image getDatatypeImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
    }

    private Image getFolderImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.FOLDER);
    }
}
