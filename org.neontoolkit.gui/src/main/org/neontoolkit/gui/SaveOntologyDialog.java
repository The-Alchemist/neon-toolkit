/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.progress.NotForkedRunnableWithProgress;

/* 
 * Created on 14.06.2004
 * Created by Olga Gromova
 *
 * Keywords: Ontology, Save, Shutdown, Selection
 */

public class SaveOntologyDialog extends SelectionDialog {
	
	private Table table;
	private static final int NOT_SAVE_BUTTON_ID = 3; 
	private Vector<String[]> modifiedOntology;
	private String PROJECT_FAILURE = "Project is not accessible!";  //$NON-NLS-1$
	private boolean _hasUnsavedChanges = false;
	
	protected SaveOntologyDialog(Shell arg0) {
		super(arg0);
	}

	@Override
    protected void createButtonsForButtonBar(Composite composite) {
		super.createButtonsForButtonBar(composite);
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite= (Composite) super.createDialogArea(parent);
		createMessageArea(composite);
		composite.getShell().setText(Messages.SaveOntologyDialog_2); 
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;
		GridData gridData = new GridData();
		
		Label tablesLabel = new Label(composite, SWT.NONE);
      tablesLabel.setText(Messages.SaveOntologyDialog_3); 
      gridData = new GridData();
      gridData.horizontalSpan = 1;
      gridData.verticalAlignment = SWT.TOP;
		tablesLabel.setLayoutData(gridData);
		
		table = new Table(composite, SWT.CHECK|SWT.BORDER);
		table.setHeaderVisible(true);
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText(Messages.SaveOntologyDialog_4); 
		col1.setWidth(200);
		col1.setResizable(true);
		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText(Messages.SaveOntologyDialog_5); 
		col2.setWidth(300);
		col2.setResizable(true);
		gridData = new GridData();
      gridData.horizontalSpan = 1;
      gridData.widthHint = 550;
      gridData.heightHint = 250;
      gridData.verticalAlignment = SWT.TOP;
		table.setLayoutData(gridData);
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.BEGINNING;
		gridData.grabExcessHorizontalSpace = true;
		c.setLayoutData(gridData);
		
		Label fillLabel = new Label(c, SWT.NONE);
		fillLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		createButton(c, 4, "Select All", false).addSelectionListener(new SelectionAdapter() { //$NON-NLS-1$
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem item:table.getItems()) {
					item.setChecked(true);
				}
			}
		});
		createButton(c, 5, "Deselect All", false).addSelectionListener(new SelectionAdapter() { //$NON-NLS-1$
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem item:table.getItems()) {
					item.setChecked(false);
				}
			}
		});
     
		initValues();
		return composite;
	}
	
	private void initValues(){
		//modifiedOntology = getModifiedOntologies();
		table.removeAll();
		for(int i=0; i<modifiedOntology.size(); i++){
			String[] tmp = (String[])modifiedOntology.get(i);
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, tmp[0]);
			item.setText(1, tmp[1]);
            if (PROJECT_FAILURE == tmp[0]) {
                item.setImage(NeOnUIPlugin.getDefault().getImageRegistry().get(SharedImages.REMOVE));
            }
            else {
                item.setChecked(true);
            }
		}
	}
	
	private Vector<String[]> getModifiedOntologies(){
		Vector<String[]> data = new Vector<String[]>();
		String[] projects = OntologyProjectManager.getDefault().getOntologyProjects();
        for(int i=0; i<projects.length; i++){
            try {
        		IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projects[i]);
        		if (!ontologyProject.isPersistent()) {
        			String[] ontos = ontologyProject.getDirtyOntologies();
        			for(int j=0; j<ontos.length; j++){
        				data.add(new String[]{ontos[j], projects[i]});
        				_hasUnsavedChanges = true;
        			}
        		}
            } catch (Exception e) {
                data.add(new String[]{PROJECT_FAILURE, projects[i]});
                NeOnUIPlugin.getDefault().logError(PROJECT_FAILURE, e);
            }
        }
		return data;
	}
		
	@Override
    protected void cancelPressed() {
		super.cancelPressed();
	}
	@Override
    protected void buttonPressed(int buttonId) {
		if (buttonId==NOT_SAVE_BUTTON_ID){
			super.okPressed();
		} else
			super.buttonPressed(buttonId);
	}
	@Override
    protected void okPressed() {
		HashMap<String, HashSet<String>> projectOntos = new HashMap<String, HashSet<String>>();
		TableItem[] items = table.getItems();
		for(int i=0; i<items.length; i++){
			if (items[i].getChecked()){
				String projectName = items[i].getText(1);
				String module = items[i].getText(0) ;
				HashSet<String> modules = new HashSet<String>();
				if(projectOntos.containsKey(projectName)) {
					modules = projectOntos.get(projectName);
				} else {
					projectOntos.put(projectName, modules);
				}
				modules.add(module);
			}
		}
		//save the ontology
		if(projectOntos.size() > 0) {
			if(save(projectOntos) != OK) {
				return;
			}
		}
		setReturnCode(OK);
		close();
	}
		
	@Override
    public int open() {
		modifiedOntology = getModifiedOntologies();
		if (_hasUnsavedChanges)
		    return super.open();
		else
			return Window.OK;
	}
	@Override
    protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setSize(600, 300);
	}
	
    private int save(HashMap<String, HashSet<String>> projectOntos) {//String projectName, String ontologyId) {
        final HashMap<String, HashSet<String>> fprojectOntos = projectOntos;
        Display display = getShell() != null ? getShell().getDisplay() : null;
        IRunnableWithProgress op = new NotForkedRunnableWithProgress(display) {
            @Override
            public void runNotForked(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    			ArrayList<String> ontosNotSaved = new ArrayList<String>();
    			Exception ie = null;
                monitor.beginTask(Messages.SaveOntologyDialog_8, IProgressMonitor.UNKNOWN); 
        		Iterator<String> projectOntosIterator = fprojectOntos.keySet().iterator();
        		while (projectOntosIterator.hasNext()) {
        			String projectName = projectOntosIterator.next();        			
        			HashSet<String> ontologyUris = fprojectOntos.get(projectName);
					try {
						IOntologyProject ontologyProject =  NeOnCorePlugin.getDefault().getOntologyProject(projectName);
	        			for (Iterator<String> iter = ontologyUris.iterator(); iter.hasNext();) {
	        				String ontologyUri = iter.next();                
		    				try {
	    		                ontologyProject.saveOntology(ontologyUri);
	    			        	ontologyProject.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
	    			        	ontologyProject.setOntologyDirty(ontologyUri, false);
							} catch (Exception e) {
								ie = e;
								if (e.getCause() != null) {
									ontosNotSaved.add(ontologyUri + " (" + projectName + "): " + e.getMessage() + " (" + e.getCause() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
								} else {
									ontosNotSaved.add(ontologyUri + " (" + projectName + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
								}
							}
		                }
					} catch (NeOnCoreException e1) {
						ontosNotSaved.add(projectName + ": " + e1.getMessage() + " (" + e1.getCause() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
        		}
    			monitor.done();
    			if (ontosNotSaved.size() > 0) {
    				List<IStatus> notFoundStatus = new ArrayList<IStatus>(); 
    				for (int j=0; j<ontosNotSaved.size(); j++) {
    					notFoundStatus.add(new Status(
    							Status.ERROR,
    							NeOnUIPlugin.getDefault().getBundle().getSymbolicName(), 
    							Status.OK, 
    							ontosNotSaved.get(j), 
    							null));
    				}
    				String msg = Messages.SaveOntologyDialog_7; 
    				IStatus parentStatus = new MultiStatus(
    				        NeOnUIPlugin.getDefault().getBundle().getSymbolicName(), 
    						IStatus.ERROR, 
    						notFoundStatus.toArray(new IStatus[0]), 
    						msg, 
    						new InternalNeOnException(msg.toString(),ie));
    				IWorkbench wb = PlatformUI.getWorkbench();
    				IWorkbenchWindow[] windows = wb.getWorkbenchWindows();
    				new NeonToolkitExceptionHandler().handleException(Messages.SaveOntologyDialog_6,parentStatus, windows[0].getShell());
    				throw new InvocationTargetException(ie);
    			}                	
            }
        };
	    try {
	        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(new Shell());
	        progressDialog.run(false, false, op);         
	        return OK;
	    } catch (InvocationTargetException e) {
            return CANCEL;
        } catch (InterruptedException e) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SaveOntologyDialog_7, e.getMessage()); 
            return CANCEL;
        }
    }
    
}
