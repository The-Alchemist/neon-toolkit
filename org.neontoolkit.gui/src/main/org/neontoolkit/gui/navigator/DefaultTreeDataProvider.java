/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.ontology.RemoteOntologyCheckWizard;
import org.neontoolkit.gui.navigator.ontology.SaveOntologyRunnableWithProgress;
import org.neontoolkit.gui.properties.ProgressMonitorWithExceptionDialog;

/* 
 * Created on: 31.12.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, TreeViewer, Navigator
 */
/**
 * Abstract default implementation of the ITreeDataProvider interface
 */

public abstract class DefaultTreeDataProvider implements ITreeDataProvider, ISaveableProvider {
    // The id of this provider, defined in the extension point and set by the framework.
    protected String _id;

    protected ITreeExtensionHandler _handler;

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#dispose()
     */
    public void dispose() {
        // default implementation does nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getImage(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public Image getImage(ITreeElement element) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getText(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public String getText(ITreeElement element) {
        return element.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#setId(java.lang.String)
     */
    public void setId(String id) {
        _id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getId()
     */
    public String getId() {
        return _id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#setExtensionHandler(com.ontoprise.ontostudio.gui.TreeExtensionHandler)
     */
    public void setExtensionHandler(ITreeExtensionHandler handler) {
        _handler = handler;
    }

    public ITreeExtensionHandler getExtensionHandler() {
        return _handler;
    }

    /**
     * Returns the viewer that is associated with this provider
     */
    public AbstractComplexTreeViewer getViewer() {
        return _handler.getViewer();
    }

    protected void addChild(final Object parent, final Object child) {
        addChild(parent, child, null);
    }

    protected Widget[] findTreeItems(final Object item) {
        if (_handler != null && getViewer() != null) {
            return getViewer().findTreeItems(item);
        }
        return new Widget[0];
    }

    protected void addChild(final Object parent, final Object child, final IElementComparer comparer) {
        syncExec(new Runnable() {
            public void run() {
                Object parentElement = parent;
                if (parent == null) {
                    MainTreeDataProvider rootProv = MainTreeDataProvider.getDefault();
                    if (rootProv != null) {
                        parentElement = rootProv.getRoot();
                    }
                }
                if (comparer != null) {
                    IElementComparer oldComparer = getViewer().getComparer();
                    getViewer().setComparer(comparer);
                    getViewer().add(parentElement, child);
                    getViewer().setComparer(oldComparer);
                } else {
                    getViewer().add(parentElement, child);
                }
            }
        });
    }

    protected void addChildAsync(final Object parent, final Object child, final IElementComparer comparer) {
        asyncExec(new Runnable() {
            public void run() {
                Object parentElement = parent;
                if (parent == null) {
                    MainTreeDataProvider rootProv = MainTreeDataProvider.getDefault();
                    if (rootProv != null) {
                        parentElement = rootProv.getRoot();
                    }
                }
                if (comparer != null) {
                    IElementComparer oldComparer = getViewer().getComparer();
                    getViewer().setComparer(comparer);
                    getViewer().add(parentElement, child);
                    getViewer().setComparer(oldComparer);
                } else {
                    getViewer().add(parentElement, child);
                }
            }
        });
    }

    protected void removeChild(final Object parent, final Object child) {
        syncExec(new Runnable() {
            public void run() {
                Object parentElement = parent;
                if (parent == null) {
                    MainTreeDataProvider rootProv = MainTreeDataProvider.getDefault();
                    if (rootProv != null) {
                        parentElement = rootProv.getRoot();
                    }
                }
                getViewer().remove(parentElement, new Object[] {child});
            }
        });
    }

    protected void remove(final Object element) {
        asyncExec(new Runnable() {
            public void run() {
                getViewer().remove(element);
            }
        });
    }

    protected void removeChild(final Object parent, final Object child, final IElementComparer comparer) {
        syncExec(new Runnable() {
            public void run() {
                IElementComparer oldComparer = getViewer().getComparer();
                getViewer().setComparer(comparer);
                getViewer().remove(parent, new Object[] {child});
                getViewer().setComparer(oldComparer);
            }
        });
    }

    protected void insertChild(final Object parent, final Object child, final Comparator<Object> comparator) {
        syncExec(new Runnable() {
            public void run() {
                Widget[] items = getViewer().findTreeItems(parent);
                for (Widget item: items) {
                    assert item instanceof TreeItem;
                    TreeItem tItem = (TreeItem) item;

                    // check if item already exists and skip insertion in this case
                    boolean skip = false;
                    for (TreeItem existingChild: tItem.getItems()) {
                        if (child.equals(existingChild.getData())) {
                            skip = true;
                        }
                    }

                    if (!skip) {
                        int index = 0;
                        while (index < tItem.getItemCount() && comparator.compare(child, tItem.getItem(index).getData()) > 0) {
                            index++;
                        }
                        getViewer().insert(parent, child, index);
                    }
                }
            }
        });
    }

    public void replaceItems(final ITreeElement oldElement, final ITreeElement newElement) {
        asyncExec(new Runnable() {
            @Override
            public void run() {
                getViewer().replaceItem(oldElement, newElement);
            }
        });
    }

    protected void refresh() {
        syncExec(new Runnable() {
            public void run() {
                getViewer().refresh();
            }
        });
    }

    protected void refresh(final Object element) {
        syncExec(new Runnable() {
            public void run() {
                getViewer().refresh(element);
            }
        });
    }

    public void doSave(IProgressMonitor progress, ITreeElement element) {
        if (!(element instanceof IProjectElement)) {
            return;
        }
        String projectName = ((IProjectElement) element).getProjectName();
        String selectedOntologyUri = null;
        if (element instanceof IOntologyElement) {
            selectedOntologyUri = ((IOntologyElement) element).getOntologyUri();
        }
        try {
            IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
            
            if (ontologyProject.isPersistent()) {
                return;
            }
            Set<String> ontologyUris = new HashSet<String>();
            if (selectedOntologyUri != null) {
                ontologyUris.add(selectedOntologyUri);
            } else {
                ontologyUris = ontologyProject.getAvailableOntologyURIs();
            }
            for (String ontologyUri: ontologyUris) {
//                URI physicalUri = ontologyProject.getPhysicalUri(ontologyUri);
//                if(!physicalUri.getScheme().equals("file")) { //$NON-NLS-1$
//                    //message that ontology will be saved to workspace
//                    // see Issue 11734
//                    // duplicate code within SaveOntologyDialog, has to be refactored
//                    if(!MessageDialog.openConfirm(getViewer().getControl().getShell(),Messages.DefaultTreeDataProvider_1, 
//                            Messages.DefaultTreeDataProvider_2 + ontologyUri + Messages.DefaultTreeDataProvider_3 + physicalUri + Messages.DefaultTreeDataProvider_4)) {
//                        return;
//                    }
//                    String extension = null;
//                    try {
//                        String formatName = ontologyProject.getOntologyManager().getOntology(ontologyUri).getOntologyFormatting().getFormatName();
//                        extension = "." + OntoBrokerOntologyFileFormat.getFileExtensionForFormat(formatName);                         //$NON-NLS-1$
//                    } catch (Exception e) {
//                        // ignore
//                    }
//                    String fileName =  getNameFromUri(physicalUri.toString());
//                    if(extension == null && fileName.contains(".")) { //$NON-NLS-1$
//                        extension = fileName.substring(fileName.lastIndexOf(".")); //$NON-NLS-1$
//                    }
//                    fileName = ontologyProject.getNewOntologyFilename(fileName, extension);
//                    String physicalFileUri = ontologyProject.getResource().getFile(fileName).getLocationURI().toString();
//                    ontologyProject.setPhysicalUri(ontologyUri, physicalFileUri);
//                }
                RemoteOntologyCheckWizard rocw = new RemoteOntologyCheckWizard(projectName, ontologyUri, getViewer().getControl().getShell());
                if (rocw.shouldSave()){
                    ProgressMonitorWithExceptionDialog progdialog = new ProgressMonitorWithExceptionDialog(getViewer().getControl().getShell());
                    SaveOntologyRunnableWithProgress runnable = new SaveOntologyRunnableWithProgress(projectName, ontologyUri);
                    progdialog.runWithException(true, false, runnable);
                }
            } 
//        } catch (KAON2Exception e) {
//            NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
//            e.printStackTrace();
        } catch (Exception e) {
            new NeonToolkitExceptionHandler().handleException(Messages.DefaultTreeDataProvider_1, e, new Shell());
        }
    }
    
//    private static String getNameFromUri(String physicalUri) {
//        //duplicated code from ImportExportUtils
//        int lastIndex = physicalUri.lastIndexOf('#');
//        physicalUri = physicalUri.substring(lastIndex + 1, physicalUri.length());
//        lastIndex = physicalUri.lastIndexOf('/');
//        return physicalUri.substring(lastIndex + 1, physicalUri.length());
//    }


    public void doSaveAs(ITreeElement element) {
        throw new UnsupportedOperationException();
    }

    public boolean isDirty(ITreeElement element) {
        if (!(element instanceof IProjectElement)) {
            return false;
        }
        String projectName = ((IProjectElement) element).getProjectName();
        String ontologyUri = null;
        if (element instanceof IOntologyElement) {
            ontologyUri = ((IOntologyElement) element).getOntologyUri();
        }
        IOntologyProject ontologyProject = null;
        try {
            ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
            if (ontologyUri != null) {
                return ontologyProject.isOntologyDirty(ontologyUri);
            } else {
                return ontologyProject.getDirtyOntologies().length > 0;
            }
        } catch (NeOnCoreException e) {
            NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
        }
        return false;
    }

    public boolean isSaveAsAllowed(ITreeElement element) {
        return true;
    }

    public void syncExec(Runnable runnable) {
        if (_handler != null && getViewer() != null) {
            getViewer().getControl().getDisplay().syncExec(runnable);
        }
    }

    public void asyncExec(Runnable runnable) {
        if (_handler != null && getViewer() != null) {
            getViewer().getControl().getDisplay().asyncExec(runnable);
        }
    }
}
