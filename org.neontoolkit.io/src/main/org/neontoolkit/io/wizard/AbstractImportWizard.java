/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProjectFilter;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.exception.OntologyImportException;
import org.neontoolkit.io.filter.FileFilter;

/* 
 * Created on 11.03.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, Import, Ontology
 */

/**
 * Abstract Import Wizard for different types and formats
 */
public abstract class AbstractImportWizard extends Wizard implements IImportWizard {

    protected String _errorMessage = null;
    static Logger _log = Logger.getLogger(AbstractImportWizard.class);

    protected AbstractImportSelectionPage _pageSelection = null;

    protected MTreeView _fView;

    protected Object _selection;

    protected String _preselectedType;
    protected FileFilter _fileFilter;
    protected String _preselectedProject;
    protected IOntologyProjectFilter _filter;

    protected String[] _onto;
    protected URL _url;
    protected String _projectName;
    protected String[][] _messages;

    private boolean _isImportDialog = true;

    class DialogRunnable implements Runnable {

        private String _message;
        private boolean _ask;
        private boolean _value;
        private String _title;

        public DialogRunnable(String title, String message, boolean ask) {
            _title = title;
            _message = message;
            _ask = ask;
        }

        public boolean getValue() {
            return _value;
        }

        public void run() {
//            Shell parent =  getContainer().getShell();
            Shell parent =  Display.getCurrent().getActiveShell();
            if(_ask) {
            	_value = MessageDialog.openConfirm(parent, _title, _message);
            } else {
            	_value = false;
            	MessageDialog.openInformation(parent, _title, _message);
            }
        }
    }

    
    public AbstractImportWizard() {
        super();
        setNeedsProgressMonitor(true);
    }
    
    public void setIsImportDialog(boolean isImportDialog) {
    	_isImportDialog = isImportDialog;
    }
    
    public boolean isImportDialog() {
    	return _isImportDialog;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(IWorkbench,
     *      IStructuredSelection)
     */
    public void init(IWorkbench arg0, IStructuredSelection arg1) {
        IViewReference[] refs = arg0.getActiveWorkbenchWindow().getActivePage().getViewReferences();
        for (int i = 0; i < refs.length; i++) {
            IViewPart view = refs[i].getView(false);
            if (view instanceof MTreeView) {
                _fView = (MTreeView) view;
                arg1 = (IStructuredSelection) _fView.getTreeViewer().getSelection();
                if (arg1 instanceof StructuredSelection) {
                    _selection = ((StructuredSelection) arg1).getFirstElement();
                }
            }
        }
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    @Override
    public void addPages() {
        addImportSelectionPage();
        if (_selection != null) {
            if (_selection instanceof IProject) {
                _pageSelection.setSelectedProject(((IProject) _selection).getName());
            } else if (_selection instanceof IProjectElement) {
                _pageSelection.setSelectedProject(((IProjectElement) _selection).getProjectName());
            }
        }
        if (_preselectedProject != null) {
            _pageSelection.setSelectedProject(_preselectedProject);
        }
        if(_filter != null) {
        	_pageSelection.setOntologyProjectFilter(_filter);
        }
    }

    public void addImportSelectionPage() {
        _pageSelection = getImportSelectionPage();
        addPage(_pageSelection);
    }

    public abstract AbstractImportSelectionPage getImportSelectionPage();

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyImportWizard#getFileFilter()
     */
    public FileFilter getFileFilter() {
        return _fileFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyImportWizard#getFileFilter()
     */
    public void setFileFilter(FileFilter fileFilter) {
        this._fileFilter = fileFilter;
        if (_pageSelection != null) {
            _pageSelection.setFileFilter(fileFilter);
        }
    }
    
    public void setSupportedProjectOntologyLanguage(IOntologyProjectFilter filter) {
        _filter = filter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        return super.canFinish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        final String projectName = _pageSelection.getSelectedProject();
        final URL[] urls = _pageSelection.getSelectedURLS();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask(Messages.getString("AbstractImportWizard.6"), 100 * urls.length); //$NON-NLS-1$
                    for (int i = 0; i < urls.length; i++) {
                        URL url = urls[i];
                        if (url != null) {
                            try {
                                doFinish(projectName, new URL[]{url}, monitor);
                            } catch (OntologyImportException e) {
                            	NeonToolkitExceptionHandler handler = new NeonToolkitExceptionHandler();
                                handler.handleException(e, e.getCause(), getShell());
                            }
                        }
                    }
                    if(monitor != null) {
                        monitor.done();
                    }
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                	if(monitor != null) {
                		monitor.done();
                	}
                }
            }
        };
        try {
            IWizardContainer container = getContainer();
            container.run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            NeonToolkitExceptionHandler handler = new NeonToolkitExceptionHandler();
            handler.handleException((String) null, realException, getShell());
            return false;
        }
        return true;
    }

    protected void throwCoreException(String message, Throwable t) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, "ImportWizard", IStatus.OK, message, t); //$NON-NLS-1$
        throw new CoreException(status);
    }

    public abstract void doFinish(String projectName, URL[] physicalURLs, IProgressMonitor monitor) throws OntologyImportException;
//    {
//    	try {
//    		_messages = new String[0][0];
//	        this._projectName = projectName;
//	        
//	        List<URI> physicalURIsToImport = new ArrayList<URI>();
//	        List<String> ontologyUrisToImport = new ArrayList<String>();
//	        List<String> ontologyUrisToMerge = new ArrayList<String>();
//	        
//	        for (URL physicalURL : physicalURLs) {
//	        	URI physicalURI = physicalURL.toURI();
//	        	String ontologyUri = getOntologyUri(projectName, physicalURI);
//	        	//checkForMerge
//	    		try{
//	   				ImportExportUtils.checkOntology(projectName, ontologyUri);
//		    	} catch (ModuleAlreadyExistsException maee) {
//		    		if(askForMergeModules(maee.getMessage(), Messages.getString("AbstractImportWizard.9"))) {
//		    			ontologyUrisToMerge.add(ontologyUri);
//		    		} else {
//		    			continue;
//		    		}
//		    	} catch (ModuleNotOpenedException mnoe) {
//	        		//Message that ontology has to be added to the project first!
//		    		String message = Messages.getString("AbstractImportWizard.23") 
//		    			+ " '"	+ ontologyUri + "' " +Messages.getString("AbstractImportWizard.24"); //$NON-NLS-1$ //$NON-NLS-2$
//		    		if(askForMergeModules(message, Messages.getString("AbstractImportWizard.25"))) {
//		    	        ProgressListener pl = null;
//		    	        if(monitor != null) {
//		    	        	pl = new ImportProgressListener(projectName, monitor);
//		    	        	pl.phaseStarted("Open ontology " + ontologyUri);
//		    	        }
//		    	        OntologyProject ontologyProject = (OntologyProject) NeOnCorePlugin.getDefault().getOntologyProject(projectName);
//		    			ontologyProject.getOntologyManager().openOntology(ontologyUri, null, pl);
//		    			ImportExportUtils.addOntologiesToProject(projectName, new String[]{ontologyUri});
//		    			ontologyUrisToMerge.add(ontologyUri);
//		    		} else {
//		    			continue;
//		    		}
//		    	}	    	
//				physicalURIsToImport.add(physicalURI);
//				ontologyUrisToImport.add(ontologyUri);
//			}
//        
//    		_onto = null;
//        	try {
//	    		if(physicalURIsToImport.size() > 0) {
//	                _onto = doImport(projectName, physicalURIsToImport.toArray(new URI[0]), monitor);
//	    			
//	    		}
//	        } catch (OntologyImportException ex) {
//            	if(ex.getCause() instanceof InterruptedException) {
//            		//delete canceled module when persistent storage and the module didn't exist before
//            		IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
//            		if(ontologyProject.isPersistent()) {
//                		for (String ontologyUriToRemove : ontologyUrisToImport) {
//                    		if(!ontologyUrisToMerge.contains(ontologyUriToRemove)) {   						
//    	            			if(monitor != null) {
//    	            				monitor.setTaskName(Messages.getString("AbstractImportWizard.22")); //$NON-NLS-1$
//    	            			}
//    	            			//don't need to remove ontology?!
//    	            			new RemoveOntology(projectName, ontologyUriToRemove, true).run();
//                    		}
//                		}
//            		}
//            		String removedOntoUris = ""; //$NON-NLS-1$
//            		String removedPhysUris = ""; //$NON-NLS-1$
//            		for (int i = 0; i < ontologyUrisToImport.size(); i++) {
//						String ontoUri = ontologyUrisToImport.get(i);
//						if(!ontologyUrisToMerge.contains(ontoUri)) {
//							String physUri = physicalURIsToImport.get(i).toString();
//							removedOntoUris += ontoUri;
//							removedPhysUris += physUri;
//						}							
//					}
//            		//Message that canceled
//        	        String title = Messages.getString("AbstractImportWizard.10"); //$NON-NLS-1$
//        	        String message = ""; //$NON-NLS-1$
//        	        if(removedOntoUris.length() > 0) {
//        	        	message = 
//        	        	Messages.getString("AbstractImportWizard.14") //$NON-NLS-1$
//        	        	+ " '" + removedOntoUris + "' " +  //$NON-NLS-1$ //$NON-NLS-2$
//        	        	Messages.getString("AbstractImportWizard.15") //$NON-NLS-1$
//        	        	+ " '" + removedPhysUris + "' " +  //$NON-NLS-1$ //$NON-NLS-2$
//        	        	Messages.getString("AbstractImportWizard.16"); //$NON-NLS-1$
//        	        }
//        			ShowInfo(title, message);
//        			//TODO info for merged ontologies
////            		} else if (isMerged){
////                		//Message that incomplete import
////            	        String title = Messages.getString("AbstractImportWizard.10"); //$NON-NLS-1$
////            	        String message = 
////            	        	Messages.getString("AbstractImportWizard.11") //$NON-NLS-1$
////            	        	+ " " + moduleToImport + " " +  //$NON-NLS-1$ //$NON-NLS-2$
////            	        	Messages.getString("AbstractImportWizard.12"); //$NON-NLS-1$
////            			ShowInfo(title, message);
////            		}
//            	} else {
//            		throw ex;
//            	}
//	        } finally {
//	        	for (String ontologyUri : ontologyUrisToMerge) {
//					if(new ExistsOntology(projectName, ontologyUri).exists()) {
//						ImportExportUtils.setOntologyDirty(projectName, ontologyUri, true);
//					}
//		        }
//	            final RefactoringStatus rs = IOInternalUtils.getErrorStatus(getMessages());
//	            if(rs.hasEntries()) {
//	            	getShell().getDisplay().syncExec(new Runnable() {
//	            		public void run() {
//	    		    		TransformationStatusDialog rsd = new TransformationStatusDialog(rs, "Ontology Import Info", getShell());
//	    		    		rsd.open();
//	            		}
//	            	});
//	            }
//
//    		}
//        } catch (Throwable e) {
//        	if(e instanceof OntologyImportException) {
//        		throw (OntologyImportException) e;
//        	}
//            throw new OntologyImportException(e);
//        }
//    }
//
//    
    
    public abstract String getOntologyUri(URL location, String projectName) throws NeOnCoreException;

    public String getOntologyUri(String projectName, URI physicalURI) throws NeOnCoreException {
    	try {
			return getOntologyUri(physicalURI.toURL(), projectName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
    }

    public boolean askForMergeModules(String message1, String message2) {    	
        String title = Messages.getString("AbstractImportWizard.8"); //$NON-NLS-1$
    	message1 += "\n" + message2; //$NON-NLS-1$
    	message1 += "\n" + Messages.getString("AbstractImportWizard.13"); //$NON-NLS-1$ //$NON-NLS-2$
        DialogRunnable mmdr = new DialogRunnable(title, message1, true); 
        if(Display.getCurrent() != null) {
        	Display.getCurrent().getActiveShell().getDisplay().syncExec(mmdr);
        } else {
          getShell().getDisplay().syncExec(mmdr);
        }
        return mmdr.getValue();
    }

    public void ShowInfo(String title, String message) {    	
        DialogRunnable mmdr = new DialogRunnable(title, message, false); 
        if(Display.getCurrent() != null) {
        	Display.getCurrent().getActiveShell().getDisplay().syncExec(mmdr);
        } else {
        	getShell().getDisplay().syncExec(mmdr);
        }
    }

    public void setSelectedProject(String projectName) {
        this._preselectedProject = projectName;
    }

    public abstract String getPageDescription();

    public abstract String getPageTitle();
    
    public String[][] getMessages() {
    	return _messages;
    }

    public abstract String[] doImport(String project, URI[] physicalURIs, IProgressMonitor monitor) throws OntologyImportException;

}
