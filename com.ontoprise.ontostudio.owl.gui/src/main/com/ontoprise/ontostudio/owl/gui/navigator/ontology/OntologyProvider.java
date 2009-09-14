/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.IOntologyProjectListener;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.neontoolkit.gui.navigator.project.ProjectTreeElement;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlFolderTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLOntologyProjectProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * Provider for owl ontologies in the OntologyNavigator.
 */
public class OntologyProvider extends DefaultTreeDataProvider {
    
    private OntologyProjectListener _listener;

    /**
     * Listener that gets notified if modules change and need to be marked for a future save
     * operation.
     */
    private class OntologyProjectListener implements IOntologyProjectListener {
        public void ontologyModified(final String projectName, String ontology, boolean dirty) {            
            final ITreeElement elem = getOntologyTreeElement(ontology, projectName);
            // PluginTest: java.lang.IllegalStateException: Workbench has not been created yet.
            if (PlatformUI.isWorkbenchRunning() && !PlatformUI.getWorkbench().isClosing()) {
                getViewer().getControl().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        Widget[] treeItems = getViewer().findTreeItems(elem);
                        boolean aOk = true;
                        for (Widget item: treeItems) {
                            if (!(item instanceof TreeItem)) {
                                aOk = false;
                            }
                        }
                        if (treeItems == null || !aOk) {
                            return;
                        }       
                        List<TreeItem> items = new ArrayList<TreeItem>();
                        for (int i=0; i<treeItems.length; i++) {
                            Object treeData = treeItems[i].getData(); 
                            if(treeData instanceof IProjectElement &&
                                ((IProjectElement) treeData).getProjectName().equals(projectName)) {
                                    items.add((TreeItem) treeItems[i]);
                            }
                        }
                        for (TreeItem item : items) {
                            getViewer().update(item.getData(), null);
                        }
                    }
                });
            }
        }

        @Override
        public void ontologyStructureModified(final String projectName, String ontology, boolean modified) {
            final ITreeElement elem = getOntologyTreeElement(ontology, projectName);
            // PluginTest: java.lang.IllegalStateException: Workbench has not been created yet.
            if (PlatformUI.isWorkbenchRunning() && !PlatformUI.getWorkbench().isClosing()) {
                getViewer().getControl().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        Widget[] treeItems = getViewer().findTreeItems(elem);
                        boolean aOk = true;
                        for (Widget item: treeItems) {
                            if (!(item instanceof TreeItem)) {
                                aOk = false;
                            }
                        }
                        if (treeItems == null || !aOk) {
                            return;
                        }       
                        List<TreeItem> items = new ArrayList<TreeItem>();
                        for (int i=0; i<treeItems.length; i++) {
                            Object treeData = treeItems[i].getData(); 
                            if(treeData instanceof IProjectElement &&
                                ((IProjectElement) treeData).getProjectName().equals(projectName)) {
                                    items.add((TreeItem) treeItems[i]);
                            }
                        }
                        for (TreeItem item : items) {
                            getViewer().refresh(item.getData());
                        }
                    }
                });
            }
        }

        @Override
        public void projectAdded(String projectName) {
            
        }
        public void projectRemoved(String projectName) {
        }       
        @Override
        public void projectRenamed(String oldProjectName, String newProjectName) {
        }       
        @Override
        public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri) {
        }
        @Override
        public void ontologyAdded(String projectName, String ontologyUri) {
            try {
                if(!OWLGUIUtilities.isOWLProject(projectName)) {
                    return;
                }
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
            AbstractOntologyTreeElement ontology = getOntologyTreeElement(ontologyUri, projectName);
            if(ontology != null) {
                addChild(getFolder(projectName), ontology);
            }
        }
        @Override
        public void ontologyRemoved(String projectName, String ontologyUri) {
            try {
                if(!OWLGUIUtilities.isOWLProject(projectName)) {
                    return;
                }
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
            AbstractOntologyTreeElement ontology = getOntologyTreeElement(ontologyUri, projectName);
            if(ontology != null) {
                removeChild(getFolder(projectName), ontology);
            }
        }

    }   

    /**
     * Constructor that is called from the eclipse envinronment. Sets the singleton instance to allow static access to the used instance.
     */
    public OntologyProvider() {
        _listener = new OntologyProjectListener();
        NeOnCorePlugin.getDefault().addOntologyProjectListenerByLanguage(_listener, OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
    }
    
    @Override
    public void dispose() {
        NeOnCorePlugin.getDefault().removeOntologyProjectListener(_listener);
    }

    protected AbstractOntologyTreeElement getOntologyTreeElement(String ontology, String projectName) {
        try {
            if(!OWLGUIUtilities.isOWLProject(projectName)) {
                return null;
            }
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
        OntologyTreeElement elem = new OntologyTreeElement(projectName, ontology, OntologyProvider.this);
        return elem;
    }

    @Override
    public Image getImage(ITreeElement element) {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public int getChildCount(ITreeElement parentElement) {
        if (parentElement instanceof OWLProjectTreeElement) {

            String projectId = ((OWLProjectTreeElement) parentElement).getProjectName();

            try {
                IOntologyProject ontoProject = NeOnCorePlugin.getDefault().getOntologyProject(projectId);
                if (ontoProject != null && ontoProject.getProjectFailure() == null) {
                    Set<OWLModel> ontos = OWLModelFactory.getOWLModels(projectId);
                    return ontos == null ? 0 : ontos.size();
                }
            } catch (NeOnCoreException e) {
                OWLPlugin.logError(e);
            }
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    @Override
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        if (parentElement instanceof OWLProjectTreeElement) {

            String projectId = ((OWLProjectTreeElement) parentElement).getProjectName();
            try {
                Set<OWLModel> ontologies = OWLModelFactory.getOWLModels(projectId);
                List<OntologyTreeElement> ontoNodes = new ArrayList<OntologyTreeElement>();
                for (OWLModel onto: ontologies) {
                    OntologyTreeElement temp = new OntologyTreeElement(projectId, onto.getOntologyURI(), this);
                    ontoNodes.add(temp);
                }
                Collections.sort(ontoNodes, new Comparator<OntologyTreeElement>() {

                    public int compare(OntologyTreeElement o1, OntologyTreeElement o2) {
                        return o1.toString().compareToIgnoreCase(o2.toString());
                    }
                });
                return ontoNodes.toArray(new OntologyTreeElement[0]);

            } catch (NeOnCoreException e) {
                OWLPlugin.logError(e);
            }
        }
        return new OntologyTreeElement[0];
    }

    @Override
    public String getText(ITreeElement element) {
        assert element instanceof OntologyTreeElement;
        OntologyTreeElement ontology = (OntologyTreeElement) element;
        if (((DefaultTreeDataProvider) element.getProvider()).isDirty(ontology)) {
            return ">" + element.toString(); //$NON-NLS-1$
        }
        return element.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getPathElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public TreeElementPath[] getPathElements(ITreeElement element) {
        if (element instanceof OntologyTreeElement) {
            TreeElementPath path = new TreeElementPath();
            path.append(element);
            return new TreeElementPath[] {path};
        } else if (element instanceof AbstractOwlEntityTreeElement) {
            TreeElementPath path = new TreeElementPath();
            String projectName = ((AbstractOwlEntityTreeElement) element).getProjectName();
            String ontologyUri = ((AbstractOwlEntityTreeElement) element).getOntologyUri();
            OntologyTreeElement e = new OntologyTreeElement(projectName, ontologyUri, this);
            path.append(e);
            return new TreeElementPath[] {path};
        } else if (element instanceof AbstractOwlFolderTreeElement) {
            TreeElementPath path = new TreeElementPath();
            String projectName = ((AbstractOwlFolderTreeElement) element).getProjectName();
            String ontologyUri = ((AbstractOwlFolderTreeElement) element).getOntologyUri();
            OntologyTreeElement e = new OntologyTreeElement(projectName, ontologyUri, this);
            path.append(e);
            return new TreeElementPath[] {path};
        }
        return new TreeElementPath[0];
    }

    @Override
    public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount) {
        return new ITreeElement[0];
    }

    @Override
    public boolean isDragSupported() {
        return true;
    }

    @Override
    public boolean isDropSupported() {
        return true;
    }
    public ProjectTreeElement getFolder(String projectName) {
        OWLOntologyProjectProvider prov = (OWLOntologyProjectProvider) getExtensionHandler().getProvider(OWLOntologyProjectProvider.class);
        OWLProjectTreeElement el = new OWLProjectTreeElement(projectName, prov);
        return el;
     }

}
