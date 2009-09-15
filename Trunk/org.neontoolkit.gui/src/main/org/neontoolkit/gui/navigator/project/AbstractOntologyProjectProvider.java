/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.project;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.IOntologyProjectListener;
import org.neontoolkit.core.project.IProjectFailureListener;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;

/* 
 * Created on: 07.04.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, TreeDataProvider, extendableTreeProvider, Navigator
 */
/**
 * Provider for ontologies in the OntologyNavigator.
 */
public abstract class AbstractOntologyProjectProvider extends DefaultTreeDataProvider {
    protected IOntologyProjectListener _ontologyProjectListener = new OntologyProjectListener();
	
	public class OntologyProjectListener implements IOntologyProjectListener, IProjectFailureListener {
	    
	    @Override
	    public void ontologyAdded(String projectName, String ontologyUri) {
	        //handled within the Ontology/ModuleProvider
	    }
	    @Override
    	public void ontologyModified(String projectName, String ontologyUri, boolean modified) {
            //handled within the Ontology/ModuleProvider    	
	    }
	    @Override
    	public void ontologyRemoved(String projectName, String ontologyUri) {
            //handled within the Ontology/ModuleProvider
	    }
        @Override
        public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri) {
            //handled within the Ontology/ModuleProvider            
        }

	    @Override
	    public void projectAdded(String projectName) {
	    	try {
                String ontologyLanguage = OntologyProjectManager.getDefault().getOntologyProject(projectName).getOntologyLanguage();
                ITreeElement projectElement = newTreeElement(projectName, ontologyLanguage);
                if(projectElement != null) {
                    addChild(null, projectElement);
                }
	    	} catch (NeOnCoreException nce) {
	    		//nothing to do
	    	}
	    }
	    @Override
    	public void projectRemoved(String projectName) {
            //project is removed
            removeChild(null, newTreeElement(projectName));
    	}	    

	    @Override
    	public void projectRenamed(String oldProjectName, String newProjectName) {
            //project is renamed
	    	try {
                String ontologyLanguage = OntologyProjectManager.getDefault().getOntologyProject(newProjectName).getOntologyLanguage();
                ITreeElement projectElement = newTreeElement(oldProjectName, ontologyLanguage);
                if(projectElement != null) {            
                    removeChild(null, projectElement);
                }
                projectElement = newTreeElement(newProjectName, ontologyLanguage);
                if(projectElement != null) {
                    addChild(null, projectElement);
                }
            } catch (NeOnCoreException nce) {
                //nothing to do
            }
    	}
	    
	    @Override
	    public void projectFailureOccured(String project, Exception exception) {
	        if (exception == null) {
	            try {
	            	OntologyProjectManager.getDefault().getOntologyProject(project).restoreProject();
	            } catch (NeOnCoreException nce) {
	                //nothing to do
	            }
	        }
	        refresh(newTreeElement(project));
	    }
        @Override
        public void ontologyStructureModified(String projectName, String ontologyUri, boolean modified) {
            // Nothing to do
        }
	}
	
    private IResourceChangeListener _markerListener = new IResourceChangeListener() {
        public void resourceChanged(IResourceChangeEvent event) {
            IResourceDelta delta = event.getDelta();
            if (delta == null) {
                return;
            }
            IResourceDelta[] res = delta.getAffectedChildren();
            if (res != null && res.length > 0) {
                for (int i = 0; i < res.length; i++) {
                    IResource resource = res[i].getResource();
                    if (resource instanceof IProject) {
                        if (res[i].getKind() == IResourceDelta.CHANGED) {
                        	IMarkerDelta[] markerDelta = res[i].getMarkerDeltas();
                        	if (markerDelta.length > 0) {
                        		if(res[i].getMarkerDeltas()[0].getMarker().getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO) == IMarker.SEVERITY_ERROR) {
	                        		//project markers have changed => simply refresh
                        			IOntologyProject ontologyProject = null;
									try {
										ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(resource.getName());
									} catch (NeOnCoreException e) {
										NeOnCorePlugin.getDefault().logError("", e); //$NON-NLS-1$
									}
                        			if (ontologyProject == null) { // || ontologyProject.getException() !=null) {
                        				refreshProject((IProject) resource, false);
                        			}
                        			else {
                        				updateProject((IProject) resource);
                        			}
                        		}
                        		else {
	                    			updateProject((IProject) resource);
                        		}
                        	}
                        }
                        else if (res[i].getKind() == IResourceDelta.REMOVED) {
                            if (i<res.length-1) {
                                if (res[i+1].getKind() == IResourceDelta.ADDED && res[i+1].getResource() instanceof IProject) {
                                    //Might be a rename operation
                                    continue;
                                }
                            }
                            else if (i > 0) {
                                if (res[i-1].getKind() == IResourceDelta.ADDED && res[i-1].getResource() instanceof IProject) {
                                    //Might be a rename operation
                                    continue;
                                }
                                
                            }
                            String projectName = ((IProject)resource).getName();
                            removeChild(null, newTreeElement(projectName));
                        }
                    }
                }
            }
        }
    };
    
	/**
	 * 
	 */
	public AbstractOntologyProjectProvider() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(_markerListener);
		NeOnCorePlugin.getDefault().addOntologyProjectListener(_ontologyProjectListener);
	}
	
	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(_markerListener);
	    NeOnCorePlugin.getDefault().removeOntologyProjectListener(_ontologyProjectListener);
	}
	
	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildren(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
	 */
	public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex,
			int amount) {
		return new ITreeElement[0];
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
	 */
	public int getChildCount(ITreeElement parentElement) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
	 */
	public ITreeElement[] getElements(ITreeElement parentElement, int topIndex,
			int amount) {
			return new ITreeElement[0];
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getImage(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
	 */
	@Override
	public Image getImage(ITreeElement element) {
		return getLabelProvider().getImage(NeOnCorePlugin.getDefault().getProject(element.toString()));
	}
	
	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getText(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
	 */
	@Override
	public String getText(ITreeElement element) {
		return getLabelProvider().getText(NeOnCorePlugin.getDefault().getProject(element.toString()));
	}

    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDragSupported()
     */
    public boolean isDragSupported() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDropSupported()
     */
    public boolean isDropSupported() {
        return false;
    }

    protected void refreshProject(final String projectName, final boolean expand) {
        if (_handler != null && getViewer() != null) {
            getViewer().getControl().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    ITreeElement element = newTreeElement(projectName);
                    getViewer().refresh(element);
                    if (expand) {
                        getViewer().expandToLevel(element, 1);
                    }
                }
            });
        }
    }

    
    private void refreshProject(final IProject project, final boolean expand) {
        refreshProject(project.getName(), expand);
    }
    
    private void updateProject(final IProject project) {
        if (_handler != null && getViewer() != null) {
        	getViewer().getControl().getDisplay().asyncExec(new Runnable() {
    			public void run() {
    //                String ontologyLanguage = DatamodelPlugin.getDefault().getConnectionProperties(project.getName()).get(IConfig.ONTOLOGY_LANGUAGE).toString();
    		    	getViewer().update(newTreeElement(project.getName()), null);
    			}
        	});
        }
    }

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getPathElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
	 */
	public abstract TreeElementPath[] getPathElements(ITreeElement element);
	
	protected abstract ITreeElement newTreeElement(String projectName);

	protected abstract ITreeElement newTreeElement(String projectName, String ontologyLanguage);

	protected abstract DecoratingLabelProvider getLabelProvider();
}
