/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.neontoolkit.core.natures.OntologyProjectNature;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.progress.NotForkedRunnableWithProgress;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.filter.FileFilter;


/* 
 * Created on 11.03.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, Export, Ontology
 */

/**
 * Abstract Export Wizard for different types and formats
 */
public abstract class AbstractExportWizard extends Wizard implements IExportWizard {

    private Object _selection;
    private MTreeView _fView;
    protected String _selectedProject;
    protected String _selectedOntology;
    protected String _selectedFile;
    
    protected String[] _supportedProjectLanguages;


    protected AbstractExportSelectionPage _pageSelection = null;

    protected FileFilter _fileFilter;
    protected FileFilter[] _fileFilters;

    protected String _exportedProject;
    protected String _exportedOntology;
    protected String _exportedUri;

    protected boolean _showFileFilterSelectionPage = false;

    protected String[][] _messages;
    
    public AbstractExportWizard() {
        setNeedsProgressMonitor(true);
    }

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
                return;
            }
        }
    }

    @Override
    public void addPages() {
        addExportSelectionPage();

        if (_selection != null && _fView != null) {
            if (_selection instanceof IProject) {
                _pageSelection.setSelectedProject(((IProject) _selection).getName());
            } else if (_selection instanceof IProjectElement) {
                IProjectElement treeElem = (IProjectElement) _selection;
                _pageSelection.setSelectedProject(treeElem.getProjectName());
                if (treeElem instanceof IOntologyElement) {
                    _pageSelection.setSelectedOntologyUri(((IOntologyElement) treeElem).getOntologyUri());
                }
            }
        }
        if (this._selectedProject != null) {
            _pageSelection.setSelectedProject(this._selectedProject);
        }
        if (this._selectedOntology != null) {
            _pageSelection.setSelectedOntologyUri(this._selectedOntology);
        }
        if (this._selectedFile != null) {
            _pageSelection.setSelectedFile(_selectedFile);
        }
        if(_supportedProjectLanguages != null) {
            _pageSelection.setSupportedProjectOntologyLanguages(_supportedProjectLanguages);
        }

    }

    public void addExportSelectionPage() {
        _pageSelection = getExportSelectionPage();
        addPage(_pageSelection);
    }

    public abstract AbstractExportSelectionPage getExportSelectionPage();

    @Override
    public boolean performFinish() {
        final String projectName = _pageSelection.getSelectedProject();
//        final URL fileName = _pageSelection.getSelectedURL();
        final URI fileName = _pageSelection.getSelectedURI();
        final String ontologyUri = _pageSelection.getSelectedOntologyUri();
        final FileFilter fileFilter = _pageSelection.getFileFilter();
        
        Display display = getShell() != null ? getShell().getDisplay() : null;
        IRunnableWithProgress op = new NotForkedRunnableWithProgress(display) {
            @Override
            public void runNotForked(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    IWorkspace workspace = ResourcesPlugin.getWorkspace();
                    IWorkspaceRoot root = workspace.getRoot();

                    IProject projectHandle = root.getProject(projectName);
                    OntologyProjectNature nature = (OntologyProjectNature) projectHandle.getNature(OntologyProjectNature.ID);

                    if (!projectHandle.exists()) {
                        throwCoreException(Messages.getString("AbstractExportWizard.0") + projectName + Messages.getString("AbstractExportWizard.1")); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    if (nature == null) {
                        throwCoreException(Messages.getString("AbstractExportWizard.2") + projectName + Messages.getString("AbstractExportWizard.3")); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    if (fileName == null) {
                        throwCoreException(Messages.getString("AbstractExportWizard.4")); //$NON-NLS-1$
                    } else {
                    	doFinish(projectName, ontologyUri, fileName.toString(), fileFilter, monitor);
                    }
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            //check for file existing and ask to overwrite
            if (isFileExisting(fileName)) {
//                try {
//                    URL oldUrl = DatamodelPlugin.getDefault().getLocation(projectName, DatamodelPlugin.createTerm(ontologyId));
//                    if (oldUrl != null) {
//                        if (fileName.equals(oldUrl)) {
//                        }
//                    }
//                } catch (Exception e1) {
//                }
                String message = ""; //$NON-NLS-1$
				message += getFileExistingMessage(fileName.getPath());
                if (!MessageDialog.openConfirm(null, Messages.getString("AbstractExportWizard.11"), message)) { //$NON-NLS-1$
                    return false;
                }
            }
            getContainer().run(false, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            NeonToolkitExceptionHandler oseh = new NeonToolkitExceptionHandler();
            if(realException.getCause() != null) {
                oseh.handleException(realException, realException.getCause(), getShell());
            } else {
                oseh.handleException(Messages.getString("AbstractExportWizard.5"), realException, getShell()); //$NON-NLS-1$
            }
            return false;
        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.getString("AbstractExportWizard.5"), e.getMessage()); //$NON-NLS-1$ 
            return false;
        }
        return true;
    }

    public abstract boolean isFileExisting(URL url);
    public abstract boolean isFileExisting(URI uri);
    
    protected abstract String getFileExistingMessage(String filename);

    protected void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, Messages.getString("AbstractExportWizard.6"), IStatus.OK, message, null); //$NON-NLS-1$
        throw new CoreException(status);
    }

    public void setSelectedProject(String projectName) {
        this._selectedProject = projectName;
    }

    public void setSelectedOntology(String ontologyId) {
        this._selectedOntology = ontologyId;
    }

    public void setSelectedFile(String file) {
        this._selectedFile = file;
    }

    public abstract void doFinish(String projectName, String ontologyUri, String physicalUri, FileFilter fileFilter, IProgressMonitor monitor) throws Exception;
//    {
//		_messages = new String[0][0];
//
//        monitor.beginTask(Messages.getString("AbstractExportWizard.7") + ontologyUri + Messages.getString("AbstractExportWizard.8") //$NON-NLS-1$ //$NON-NLS-2$
//                + physicalUri + "\".", //$NON-NLS-1$
//                1);
//        _exportedOntology = ontologyUri;
//        _exportedProject = projectName;
//        _exportedUri = physicalUri;
//        doExport(projectName, ontologyUri, physicalUri, fileFilter.getOntologyFileFormat(), monitor);
//        final RefactoringStatus rs = IOInternalUtils.getErrorStatus(getMessages());
//        if(rs.hasEntries()) {
//        	getShell().getDisplay().syncExec(new Runnable() {
//        		public void run() {
//		    		TransformationStatusDialog rsd = new TransformationStatusDialog(rs, "Transformation Info", getShell());
//		    		rsd.open();
//        		}
//        	});
//        }
//        monitor.worked(1);
//
//    }
    /**
     * exports an ontology to the given physical uri
     * @param projectName
     * @param ontologyUri
     * @param physicalUri
     * @param fileFormat one of OntobrokerOntologyFileFormat
     * @param monitor
     * @throws Exception
     */
    public abstract void doExport(String projectName, String ontologyUri, String physicalUri, String fileFormat, IProgressMonitor monitor) throws Exception;

    public abstract String getPageDescription();

    public abstract String getPageTitle();

    public String getExportedOntology() {
        return _exportedOntology;
    }

    public String getExportedProject() {
        return _exportedProject;
    }

    public String getExportedUri() {
        return _exportedUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyImportWizard#getFileFilter()
     */
    public FileFilter getFileFilter() {
        return _fileFilter;
    }

    public FileFilter[] getFileFilters() {
    	if(_fileFilters == null) {
    		_fileFilters = 
    			new FileFilter[0];
//    			               { 
//    				new OxmlFileFilter(),
//    				new FLogicFileFilter(),
//    				new RdfFileFilter(), 
//    				new RdfsFileFilter(), 
//    				new NTFileFilter(), 
//    				new N3FileFilter(), 
//    				new TurtleFileFilter(),     				
//    				new OwlFileFilter(),
//    				new OwlXFileFilter(),
//    				new Owl11FileFilter()};
    	}
        return _fileFilters;
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

    public void setFileFilters(FileFilter[] fileFilters) {
        this._fileFilters = fileFilters;
        if (_pageSelection != null) {
            _pageSelection.setFileFilters(fileFilters);
        }
    }
    
    public String[][] getMessages() {
    	return _messages;
    }

    public void setSupportedProjectOntologyLanguage(String[] supportedLanguages) {
        _supportedProjectLanguages = supportedLanguages;
    }


}
