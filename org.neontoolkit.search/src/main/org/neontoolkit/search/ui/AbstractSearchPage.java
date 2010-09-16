/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.SearchPlugin;


/**
 * 
 * @author Nico Stieler
 * modified on: 15.09.2010
 */


public abstract class AbstractSearchPage extends DialogPage implements ISearchPage {
	private static final int HISTORY_SIZE= 12;
	private static final String STORE_CASE_SENSITIVE= "CASE_SENSITIVE"; //$NON-NLS-1$
	private static final String STORE_SEARCH_FLAGS = "SEARCH_FLAGS"; //$NON-NLS-1$
	private static final String STORE_HISTORY= "HISTORY"; //$NON-NLS-1$
	private static final String STORE_HISTORY_SIZE= "HISTORY_SIZE"; //$NON-NLS-1$
	
	private ISearchPageContainer _container;
	private IDialogSettings _dialogSettings;
	
	private SearchPageOption[] _options;
	private int _selectedOptions = 0;
	
	private Combo _pattern;
	private Button _ignoreCase;
	private Button[] _searchTypes;
	
	private boolean _isCaseSensitive;

	private List<SearchPatternData> _previousSearchPatterns = new ArrayList<SearchPatternData>(15);
	
	private boolean _firstTime = true;

	public AbstractSearchPage(SearchPageOption[] options) {
		_options = options;
	}
	
	public boolean performAction() {
		NewSearchUI.runQueryInForeground(null, getSearchQuery());
		return true;
	}

	public void setContainer(ISearchPageContainer container) {
		_container = container;
	}

	public ISearchPageContainer getContainer() {
		return _container;
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		readConfiguration();
		
		Composite result= new Composite(parent, SWT.NONE);
		result.setFont(parent.getFont());
		GridLayout layout= new GridLayout(2, false);
		result.setLayout(layout);
		
		addTextPatternControls(result);
		
		Label separator= new Label(result, SWT.NONE);
		separator.setVisible(false);
		GridData data= new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1);
		data.heightHint= convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);
		
		addTypeSelectionControl(result);

		setControl(result);
		Dialog.applyDialogFont(result);
	}
	
	@Override
    public void performHelp() {
	    String helpContextId = getHelpContextId();
	    if (helpContextId != null) {
	        PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpContextId);
	    }
	    else {
	        super.performHelp();
	    }
    }
	
	public abstract String getHelpContextId();
	
	private void addTextPatternControls(Composite group) {
		// grid layout with 2 columns

		// Info text		
		Label label= new Label(group, SWT.LEAD);
		label.setText("Text");  //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label.setFont(group.getFont());

		// Pattern combo
		_pattern= new Combo(group, SWT.SINGLE | SWT.BORDER);
		_pattern.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected();
				updateOKStatus();
			}
		});
		_pattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateOKStatus();
			}
		});
		_pattern.setFont(group.getFont());
		GridData data= new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint= convertWidthInCharsToPixels(50);
		_pattern.setLayoutData(data);
		
		_ignoreCase= new Button(group, SWT.CHECK);
		_ignoreCase.setText(Messages.AbstractSearchPage_1);  
		_ignoreCase.setSelection(!_isCaseSensitive);
		_ignoreCase.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent e) {
				_isCaseSensitive= !_ignoreCase.getSelection();
			}
		});
		_ignoreCase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		_ignoreCase.setFont(group.getFont());
	}
	
	private void addTypeSelectionControl(Composite c) {
        Group group = new Group(c, SWT.NONE);
        group.setLayout(new GridLayout());

		Composite majorComp = new Composite(group, SWT.NONE);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        majorComp.setLayoutData(data);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        group.setText(Messages.AbstractSearchPage_2); 
        GridLayout layout = new GridLayout(4, true);
        layout.marginHeight = 6;
        layout.marginWidth = 6;
        majorComp.setLayout(layout);

        _searchTypes = new Button[_options.length];
		for (int i=0; i<_options.length; i++) {
			final int index = i; 
			_searchTypes[i] = new Button(majorComp, SWT.CHECK);
			data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			_searchTypes[i].setLayoutData(data);
			_searchTypes[i].setText(_options[i].getName());  
			_searchTypes[i].setSelection(isFlagSet(i));
			_searchTypes[i].addSelectionListener(new SelectionAdapter() {
				@Override
	            public void widgetSelected(SelectionEvent e) {
					setFlag(index,!isFlagSet(index));
					updateOKStatus();
				}
			});
			_searchTypes[i].setFont(group.getFont());
		}

        Composite buttonComp = new Composite(group, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.BEGINNING;
        buttonComp.setLayout(new FillLayout(SWT.HORIZONTAL));
        Button selectAllButton = new Button(buttonComp, SWT.PUSH);
        selectAllButton.setText(Messages.AbstractSearchPage_3);
        selectAllButton.addSelectionListener(new SelectionAdapter() {
        
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (int i=0; i<_searchTypes.length; i++) {
                    _searchTypes[i].setSelection(true);
                    _searchTypes[i].notifyListeners(SWT.Selection, new Event());
                    setFlag(i, true);
                }
            }
        
        });
        Button unSelectAllButton = new Button(buttonComp, SWT.PUSH);
        unSelectAllButton.setText(Messages.AbstractSearchPage_4);
        unSelectAllButton.addSelectionListener(new SelectionAdapter() {
        
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (int i=0; i<_searchTypes.length; i++) {
                    _searchTypes[i].setSelection(false);
                    _searchTypes[i].notifyListeners(SWT.Selection, new Event());
                    setFlag(i, false);
                }
            }
        
        });
	}
	
	private boolean isFlagSet(int index) {
		return (_selectedOptions & _options[index].getOptionBit()) > 0;
	}
	
	private void setFlag(int index, boolean enabled) {
		if (enabled) {
			_selectedOptions |= _options[index].getOptionBit();
		}
		else {
			_selectedOptions = ~(~_selectedOptions | _options[index].getOptionBit());
		}
	}

	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		IDialogSettings s = getDialogSettings();
		_isCaseSensitive= s.getBoolean(STORE_CASE_SENSITIVE);
		
		try {
			_selectedOptions = s.getInt(STORE_SEARCH_FLAGS);
		} catch (NumberFormatException e) {
			// ignore
		}
		try {
			int historySize= s.getInt(STORE_HISTORY_SIZE);
			for (int i= 0; i < historySize; i++) {
				IDialogSettings histSettings= s.getSection(STORE_HISTORY + i);
				if (histSettings != null) {
					SearchPatternData data= SearchPatternData.create(histSettings);
					if (data != null) {
						_previousSearchPatterns.add(data);
					}
				}
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	/**
	 * Returns the stored settings for this dialog.
	 * @return
	 */
	private IDialogSettings getDialogSettings() {
		IDialogSettings settings= SearchPlugin.getDefault().getDialogSettings();
        _dialogSettings= settings.getSection(getDialogSectionName());
		if (_dialogSettings == null) {
			_dialogSettings= settings.addNewSection(getDialogSectionName());
			_dialogSettings.put(STORE_CASE_SENSITIVE, false);
			int flags = 0;
			for (int i=0; i<_options.length; i++) {
				flags |= _options[i].getOptionBit();
			}
			_dialogSettings.put(STORE_SEARCH_FLAGS,flags);
		}
		return _dialogSettings;
	}

	private void handleWidgetSelected() {
		int selectionIndex= _pattern.getSelectionIndex();
		if (selectionIndex < 0 || selectionIndex >= _previousSearchPatterns.size())
			return;
		
		SearchPatternData patternData= (SearchPatternData) _previousSearchPatterns.get(selectionIndex);
		if (!_pattern.getText().equals(patternData.getPattern()))
			return;
		_ignoreCase.setSelection(patternData.isIgnoreCase());
		int searchFlags = patternData.getSearchFlags();
		for (int i=0; i<_options.length; i++) {
			_searchTypes[i].setSelection((searchFlags & _options[i].getOptionBit()) != 0);
		}
		_pattern.setText(patternData.getPattern());
		if (patternData.getWorkingSets() != null)
			getContainer().setSelectedWorkingSets(patternData.getWorkingSets());
		else
			getContainer().setSelectedScope(patternData.getScope());
	}

	private void updateOKStatus() {
		String text = _pattern.getText();
		boolean okEnabled = (text != null) && (text.length() > 0) && (getSearchFlags() != 0);
		getContainer().setPerformActionEnabled(okEnabled);
	}
	
	private String getPattern() {
		return _pattern.getText();
	}
	
	private boolean isIgnoreCase() {
		return _ignoreCase.getSelection();
	}
	
	private int getSearchFlags() {
		return _selectedOptions;
	}

	/**
	 * Return search pattern data and update previous searches.
	 * An existing entry will be updated.
	 * @return the search pattern data
	 */
	protected SearchPatternData getPatternData() {
		SearchPatternData match= findInPrevious(getPattern());
		if (match != null) {
			_previousSearchPatterns.remove(match);
		}
		match= new SearchPatternData(
					getPattern(),
					isIgnoreCase(),
					getSearchFlags(),
					getContainer().getSelectedScope(),
					getContainer().getSelectedProjectNames(),
					getContainer().getSelectedWorkingSets());
		_previousSearchPatterns.add(0, match);
		return match;
	}

	protected abstract ISearchQuery getSearchQuery();
	
	protected abstract String getOntologyLanguage();
	
//	protected String[] getProjectsInScope() {
//		SearchPatternData patternData= getPatternData();
//	
//		// Setup search scope
//		NewSearchUI.activateSearchResultView();
//		
//		int scope = patternData.getScope();
//		String[] projects = new String[0];
//        getScope();
//		switch (scope) {
//		case ISearchPageContainer.WORKSPACE_SCOPE:
//			//search over whole workspace
//			try {
//				projects = OntologyProjectManager.getDefault().getOntologyProjects(getOntologyLanguage());
//			} catch (NeOnCoreException e) {
//				SearchPlugin.logError(Messages.AbstractSearchPage_0, e);
//				projects = new String[0];
//			}
//			break;
//		case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
//			projects = patternData.getProjects();
//			break;
//		case ISearchPageContainer.SELECTION_SCOPE:
//			IStructuredSelection selection = (IStructuredSelection)this.getSelection();
//			List<String> enclProjects = new ArrayList<String>();
//			for (Iterator<?> it=selection.iterator(); it.hasNext();) {
//				Object next = it.next();
//				if (next instanceof IProjectElement) {
//					enclProjects.add(((IProjectElement)next).getProjectName());
//				}
//			}
//			projects = enclProjects.toArray(new String[0]);
//			break;
//		case ISearchPageContainer.WORKING_SET_SCOPE:
//			IWorkingSet[] workingSets = patternData.getWorkingSets();
//			List<String> projectList = new ArrayList<String>();
//			for (IWorkingSet ws: workingSets) {
//				IAdaptable[] adap = ws.getElements();
//				for (IAdaptable adapter: adap) {
//					IProject project = (IProject)adapter.getAdapter(IProject.class);
//					if (project != null) {
//						projectList.add(project.getName());
//					}
//				}
//			}
//			projects = projectList.toArray(new String[0]);
//			break;
//		}
//		return projects;
//	}

    protected Scope getScope() {
        Scope output = new Scope();
        
        SearchPatternData patternData= getPatternData();
    
        // Setup search scope
        NewSearchUI.activateSearchResultView();
        
        int scope = patternData.getScope();
        switch (scope) {
        case ISearchPageContainer.WORKSPACE_SCOPE:
            String[] projects = new String[0];
            //search over whole workspace
            try {
                projects = OntologyProjectManager.getDefault().getOntologyProjects(getOntologyLanguage());//NICO Ontology language ERROR
            } catch (NeOnCoreException e) {
                SearchPlugin.logError(Messages.AbstractSearchPage_0, e);
                projects = new String[0];
            }
            output.setProjects(projects);
            break;
        case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
            String[] projects2 = new String[0];
            projects2 = patternData.getProjects();
            output.setProjects(projects2);
            break;
        case ISearchPageContainer.SELECTION_SCOPE:
            String[][] projects_ontologies;
            IStructuredSelection selection = (IStructuredSelection)this.getSelection();
            List<String> scopeProjects = new ArrayList<String>();
            List<Object> helpOntologies = new ArrayList<Object>();
            for (Iterator<?> it=selection.iterator(); it.hasNext();) {
                Object next = it.next();
                if (next instanceof IOntologyElement) {
                    helpOntologies.add((IOntologyElement)next);
                }else 
                    if (next instanceof IProjectElement) {
                        scopeProjects.add(((IProjectElement)next).getProjectName());
                    }
            }
            
            List<Object> help2Ontologies = new ArrayList<Object>();
            for(Object ontology : helpOntologies)
                if(ontology instanceof IProjectElement)
                    if(!scopeProjects.contains(((IProjectElement)ontology).getProjectName()))
                        help2Ontologies.add(ontology);
            
            projects_ontologies = new String[scopeProjects.size() + help2Ontologies.size()][];
            int counter = 0;
            for(String project : scopeProjects){
                projects_ontologies[counter++] = new String[]{project,null};
            }
            for(Object ontology : help2Ontologies){
                String projectName = ((IProjectElement)ontology).getProjectName();
                String OntologyId = ((IOntologyElement)ontology).getOntologyUri();
                projects_ontologies[counter++] = new String[]{projectName,OntologyId};
            }
            output.setProjects_ontologies(projects_ontologies);
            break;
        case ISearchPageContainer.WORKING_SET_SCOPE:
            String[] projects3 = new String[0];
            IWorkingSet[] workingSets = patternData.getWorkingSets();
            List<String> projectList = new ArrayList<String>();
            for (IWorkingSet ws: workingSets) {
                IAdaptable[] adap = ws.getElements();
                for (IAdaptable adapter: adap) {
                    IProject project = (IProject)adapter.getAdapter(IProject.class);
                    if (project != null) {
                        projectList.add(project.getName());
                    }
                }
            }
            projects3 = projectList.toArray(new String[0]);
            output.setProjects(projects3);
            break;
        }
        return output;
    }
	private SearchPatternData findInPrevious(String pattern) {
		for (Iterator<SearchPatternData> iter= _previousSearchPatterns.iterator(); iter.hasNext();) {
			SearchPatternData element= iter.next();
			if (pattern.equals(element.getPattern())) {
				return element;
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
    public void dispose() {
		writeConfiguration();
		super.dispose();
	}

	/*
	 * Implements method from IDialogPage
	 */
	@Override
    public void setVisible(boolean visible) {
		if (visible && _pattern != null) {
			if (_firstTime) {
				_firstTime= false;
				// Set item and text here to prevent page from resizing
				_pattern.setItems(getPreviousSearchPatterns());
				if (!initializePatternControl()) {
					_pattern.select(0);
					handleWidgetSelected();
				}
			}
			_pattern.setFocus();
		}
		updateOKStatus();
		super.setVisible(visible);
	}

	/**
	 * Stores it current configuration in the dialog store.
	 */
	private void writeConfiguration() {
		IDialogSettings s= getDialogSettings();
		s.put(STORE_CASE_SENSITIVE, _isCaseSensitive);
		s.put(STORE_SEARCH_FLAGS, getSearchFlags());
		
		int historySize= Math.min(_previousSearchPatterns.size(), HISTORY_SIZE);
		s.put(STORE_HISTORY_SIZE, historySize);
		for (int i= 0; i < historySize; i++) {
			IDialogSettings histSettings= s.addNewSection(STORE_HISTORY + i);
			SearchPatternData data= ((SearchPatternData) _previousSearchPatterns.get(i));
			data.store(histSettings);
		}
	}

	private String[] getPreviousSearchPatterns() {
		int size= _previousSearchPatterns.size();
		String [] patterns= new String[size];
		for (int i= 0; i < size; i++)
			patterns[i]= ((SearchPatternData) _previousSearchPatterns.get(i)).getPattern();
		return patterns;
	}

	private ISelection getSelection() {
		return _container.getSelection();
	}

	private boolean initializePatternControl() {
		ISelection selection= getSelection();
		if (selection instanceof ITextSelection && !selection.isEmpty()) {
			String text= ((ITextSelection) selection).getText();
			_pattern.setText(text);
			return true;
		}
		return false;
	}

    protected abstract String getDialogSectionName();
}
