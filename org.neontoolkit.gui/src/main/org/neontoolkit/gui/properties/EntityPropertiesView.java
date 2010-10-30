/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreePath;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.internal.properties.PropertyPageInfo;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

/* 
 * Created on: 31.01.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, EntityProperties, View
 */
/**
 * View that displays the property pages defined via the entityProperties extension
 * point.
 */
public class EntityPropertiesView extends ViewPart implements ISelectionListener {

    public static final String ID = "org.neontoolkit.gui.views.propertiesview"; //$NON-NLS-1$
    
    private static Map<String,IPropertyPage> PROPERTY_PAGES_FOR_TESTING = new HashMap<String,IPropertyPage>(); 

    private PropertyPageInfo[] _propertyActivators;

    private CTabFolder _container;
	private CTabItem _noSelectionPage;

	private IStructuredSelection _selection;

	private IPropertyPage _currentSelectedTab;
	private IMainPropertyPage _oldMainPage = null;
	
	private IPropertyChangeListener _listener;
	private IPartListener _partListener;
	private IWorkbenchPart _activePart;
    
    private static boolean wasDragging = false; // used to prevent changing EntityPropertiesView
    
    private boolean _isPinned = false;
	
    private IAction _pinAction;
		
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
    @Override
	public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        FillLayout layout = new FillLayout();
        composite.setLayout(layout);
        
		_container = new CTabFolder(composite, SWT.BOTTOM | SWT.FLAT);
        _container.setTabHeight(0);
		
		_noSelectionPage = createItem(new EmptyPropertyPage().createContents(_container));
		_noSelectionPage.setText("empty"); //$NON-NLS-1$
		
		IViewSite viewSite = getViewSite();
		if (viewSite != null) { // this will be null in plugin tests
		    IWorkbenchWindow window = viewSite.getWorkbenchWindow(); 
		    window.getSelectionService().addPostSelectionListener(this);
		}
		_propertyActivators = NeOnUIPlugin.getDefault().getPropertyPageInfos();
        
        for (int i = 0; i < _propertyActivators.length; i++) {
        	PropertyPageInfo currentActivator = _propertyActivators[i]; 
            try {
				createTab(currentActivator, _container, null);
			} catch (CoreException e) {
				new NeonToolkitExceptionHandler().handleException(
						Messages.EntityPropertiesView_1+currentActivator.getId(), 
						e, 
						composite.getShell());
			}
        }
        
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        _listener = new IPropertyChangeListener() {

            //Listenes to the events that change the namespace and display
            // language settings
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    CTabItem sel = _container.getSelection();
                    if (sel != null && sel.getData() != null) {
                    	if (_currentSelectedTab != null) {
                    	    if (_oldMainPage != null) {
                    	        _oldMainPage.update();
                    	    }
                    		_currentSelectedTab.update();
                    	}
                    }
                }
            }
        };

        store.addPropertyChangeListener(_listener);
        
        _partListener = new IPartListener() {
            @Override
            public void partActivated(IWorkbenchPart part) {
                _activePart = part;
            }

            @Override
            public void partBroughtToTop(IWorkbenchPart part) {}

            @Override
            public void partClosed(IWorkbenchPart part) {}

            @Override
            public void partDeactivated(IWorkbenchPart part) {
                if (_activePart == part) {
                    _activePart = null;
                }
            }

            @Override
            public void partOpened(IWorkbenchPart part) {
            }
        };
        
        getSite().getPage().addPartListener(_partListener);
        
        createActions();
        contributeToActionBar();
	}
	
	private void createActions() {
		_pinAction = new PinAction(this);
		_pinAction.setToolTipText(Messages.EntityPropertiesView_0); 
	}
	
	private void contributeToActionBar() {
        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(_pinAction);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	    _container.setFocus();
	}
	
	public Runnable getSelectionRunnable(final IWorkbenchPart part, final IStructuredSelection sel) {
		return new Runnable() {
			public void run() {
				if (_currentSelectedTab != null) {
					_currentSelectedTab.deSelectTab();
					_currentSelectedTab = null;
				}
				if (_oldMainPage != null && !_oldMainPage.isDisposed()) {
					_oldMainPage.resetSelection();
					_oldMainPage = null;
				}
				for (int i = 0; i < _propertyActivators.length; i++) {
				    if (_propertyActivators[i].matches(_selection.getFirstElement())) {
				        try {
					        IPropertyPage page = _propertyActivators[i].getPropertyPage();
					        
					        CTabItem tabItem = getTab(_propertyActivators[i]);
					        if (tabItem != null) {
	                            // bring entity properties view to the top, if open
	                            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(EntityPropertiesView.ID);
	                            if (view != null) {
	                                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(view);
	                            }
	                            
	                            Control[] children = ((Composite)tabItem.getControl()).getChildren();
					        	CTabFolder subFolder = null;
					        	for (int j=0; j<children.length && subFolder == null; j++) {
					        		if (children[j] instanceof CTabFolder) {
					        			subFolder = (CTabFolder)children[j];
					        		}
					        	}
					        	if (subFolder != null) {
					        		if (subFolder.getSelectionIndex() == -1) {
						        		subFolder.setSelection(0);
						        	}
							        _oldMainPage = (IMainPropertyPage)page;

							        _currentSelectedTab = ((PropertyPageInfo)subFolder.getSelection().getData()).getPropertyPage();
                                    _container.setSelection(tabItem);
							        _oldMainPage.setSelection(part, sel);
		                            _currentSelectedTab.selectTab();
		                            
							        if(!page.isDisposed()) {
							            setTitleImage(((IMainPropertyPage)page).getImage());
							        }
					        	}
						        return;
					        }
				        } catch (Exception ce) {
				            new NeonToolkitExceptionHandler().handleException(Messages.EntityPropertiesView_2, ce, getViewSite().getShell());
				        }
				    }
				}
				if(sel.isEmpty())
				    showEmptyPage();
			}
		};
	}
	
	private CTabItem getTab(PropertyPageInfo info) {
		for (CTabItem tab: _container.getItems()) {
			if (tab.getData() == info) {
				return tab;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {

        if(selection.isEmpty()){
            @SuppressWarnings("unused")
            String projectName = null , ontologyName = null;
            if(selection instanceof TreeSelection){
                TreeSelection treeSelection = (TreeSelection) _selection;
                for(TreePath path : treeSelection.getPaths()){

                    Object x = path.getSegment(path.getSegmentCount()-1);
                    if(x instanceof IProjectElement){
                        projectName = ((IProjectElement) x).getProjectName();
                    }
                    if(x instanceof IOntologyElement){
                        ontologyName = ((IOntologyElement) x).getOntologyUri();
                    }
                }
            }
            //NICO if _selection is deleted, you can select the one of its parents: super entity, ontology, project
            showEmptyPage();
        }
	    if (_activePart == null) {
	        _activePart = getSite().getPage().getActivePart();
	    }
        if (isPinned() || wasDragging() || selection.equals(_selection)) {
			return;
		}
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        if (_selection != null && !_selection.isEmpty() && !selection.isEmpty()) {
            if (_selection.getFirstElement().equals(((IStructuredSelection)selection).getFirstElement())) {
            	_selection = (IStructuredSelection)selection;
                return;
            }
        }
        
        //TODO diwe: Just temporary to fix bug #12148
        //If feature #12321 is implemented, this fix has to be removed
        if (isSubSelection((IStructuredSelection)selection)) {
            return;
        }

		if (selection instanceof IStructuredSelection) {
		    _selection = (IStructuredSelection)selection;
			if (!_selection.isEmpty()) {
				BusyIndicator.showWhile(_container.getDisplay(), getSelectionRunnable(part, _selection));
				return;
			}
		}
	}
	
	private boolean isSubSelection(IStructuredSelection selection) {
	    if (_selection == null || selection == null) {
	        return false;
	    }
	    if (selection.isEmpty() && !_selection.isEmpty()) {
	        return false;
	    }
	    List<?> oldSelection = _selection.toList();
	    for (Iterator<?> it=selection.iterator(); it.hasNext();) {
	        if (!oldSelection.contains(it.next())) {
	            return false;
	        }
	    }
	    return true;
	}
	
	private void showEmptyPage() {//NICO Has be redone: Bug 19(WORD)
//	    System.out.println("showEmptyPage"); //$NON-NLS-1$
//		if (!_noSelectionPage.getControl().isDisposed()) {
//            if (_oldMainPage != null && !_oldMainPage.isDisposed()) {
//                _oldMainPage.resetSelection();
//                _oldMainPage = null;
//            }
//            setTitleImage(NeOnUIPlugin.getDefault().getImageRegistry().get(SharedImages.ONTOLOGY));
//			_container.setSelection(_noSelectionPage);
//		}

	}

	private CTabItem createItem(Control control) {
    	CTabItem item = new CTabItem(_container, SWT.NONE);
    	item.setControl(control);
    	return item;
    }
	
    private CTabItem createTab(PropertyPageInfo info, CTabFolder folder, IMainPropertyPage mainPage) throws CoreException {
        IPropertyPage page = info.getPropertyPage();
        PROPERTY_PAGES_FOR_TESTING.put(info.getId(), page);
        
        Composite composite = new Composite(folder, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        
    	CTabItem item = new CTabItem(folder, SWT.NONE);
    	item.setControl(composite);
    	item.setData(info);
    	item.setText(info.getName());
    	
        if (mainPage == null) {
        	((IMainPropertyPage)page).setPart(this);
        	((IMainPropertyPage)page).createGlobalContents(composite);
    		CTabFolder subFolder = new CTabFolder(composite, SWT.BOTTOM | SWT.FLAT);
            subFolder.addSelectionListener(new SelectionAdapter() {
            	@Override
                public void widgetSelected(SelectionEvent e) {
                	PropertyPageInfo data = (PropertyPageInfo)e.item.getData();
                	try {
                		setCurrentSelectedTab(data.getPropertyPage());
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
                }
            
            });

    		subFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
    		createTab(info, subFolder, (IMainPropertyPage)page);
        	if (info.getSubContributors().length > 0) {
        		//Subcontributors exist, add a CTabFolder
        		for (PropertyPageInfo subContributor: info.getSubContributors()) {
        			createTab(subContributor, subFolder, (IMainPropertyPage)page);
        		}
        	}
        }
        else {
    		layout.marginWidth = 0;
    		layout.marginHeight = 0;
    		layout.marginRight = 0;
    		layout.marginTop = 0;
    		layout.marginBottom = 5;
        	composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        	if (page != mainPage) {
        		page.setMainPage(mainPage);
        	}
            page.createContents(composite);
        }
        return item;
    }
	
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
	public void dispose() {
        super.dispose();
        if (_oldMainPage != null) {
            _oldMainPage.resetSelection();
            _oldMainPage.dispose();
            _oldMainPage = null;
        }
        // may be reasonable?
//        for (int i = 0; i < _propertyActivators.length; i++) {
//            try {
//                _propertyActivators[i].getPropertyPage().dispose();
//            } catch (CoreException e) {
//                e.printStackTrace();
//            }
//        }
        if (_listener != null) {
            NeOnUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(_listener);
            _listener = null;
        }
        IWorkbenchWindow window = getViewSite().getWorkbenchWindow(); 
		window.getSelectionService().removePostSelectionListener(this);
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage != null) {
		    activePage.removePartListener(_partListener);
		}
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    @SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (_container != null && _container.getSelection() != null && _container.getSelection().getData() instanceof IAdaptable) {
			Object ad = ((IAdaptable) _container.getSelection().getData()).getAdapter(adapter);
			if (ad != null) {
				return ad;
			}
		}
		return super.getAdapter(adapter);
	}

	public IStructuredSelection getSelection() {
		return _selection;
	}
    
    private boolean wasDragging() {
        boolean tmp = wasDragging;
        wasDragging = false;
        return tmp;
    }

    
    public static void setDragging(boolean wasDragging) {
        EntityPropertiesView.wasDragging = wasDragging;
    }
    
    public IPropertyPage get_oldPage() {
        return this._oldMainPage;
    }
    
    public void setPinned(boolean pin) {
    	_isPinned = pin;
    }
    
    public boolean isPinned() {
    	return _isPinned;
    }
    
    /**
     * Adds a selectionlistener to the entity properties view. Listeners get notified, if 
     * the selected page changes.
     * @param listener
     */
    public void addSelectionListener(SelectionListener listener) {
    	_container.addSelectionListener(listener);
    }

    /**
     * Removes the selection listener from the entity properties view.
     * @param listener
     */
    public void removeSelectionListener(SelectionListener listener) {
    	_container.removeSelectionListener(listener);
    }
    
    public Map<String,IPropertyPage> getPropertyPagesForTesting() {
        return PROPERTY_PAGES_FOR_TESTING;
    }
    public void setCurrentSelectedTab(IPropertyPage tab){
        if(_currentSelectedTab != null) {
            _currentSelectedTab.deSelectTab();
        }
        _currentSelectedTab = tab;
        _currentSelectedTab.selectTab();
        selectIt(tab);
    }
    /**
     * 
     */
    private void selectIt (IPropertyPage tab) {
        for (int i = 0; i < _propertyActivators.length; i++) {
            if (_propertyActivators[i].matches(_selection.getFirstElement())) {
                try {
                    IPropertyPage page = _propertyActivators[i].getPropertyPage();
                    
                    CTabItem tabItem = getTab(_propertyActivators[i]);
                    if (tabItem != null) {
                        // bring entity properties view to the top, if open
                        IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(EntityPropertiesView.ID);
                        if (view != null) {
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(view);
                        }
                        
                        Control[] children = ((Composite)tabItem.getControl()).getChildren();
                        CTabFolder subFolder = null;
                        for (int j=0; j<children.length && subFolder == null; j++) {
                            if (children[j] instanceof CTabFolder) {
                                subFolder = (CTabFolder)children[j];
                            }
                        }
                        if (subFolder != null) {
                            if (subFolder.getSelectionIndex() == -1) {
                                subFolder.setSelection(0);
                            }
                            int index = 0;
                            _oldMainPage = (IMainPropertyPage)page;
                            CTabItem[] items = subFolder.getItems();
                            for(int k = 0; k < items.length; k++){
                                Object data = items[k].getData();
                                if(data instanceof PropertyPageInfo){
                                    PropertyPageInfo info = (PropertyPageInfo)data;
                                    if(info.getPropertyPage().equals(tab)){
                                        index = k;
                                        break;
                                    }
                                        
                                }
                            }
                            subFolder.setSelection(index);
                            _currentSelectedTab = ((PropertyPageInfo)subFolder.getSelection().getData()).getPropertyPage();
                            _container.setSelection(tabItem);
                        }
                        return;
                    }
                } catch (Exception ce) {
                    new NeonToolkitExceptionHandler().handleException(Messages.EntityPropertiesView_2, ce, getViewSite().getShell());
                }
            }
        }
//        if(sel.isEmpty())
//            showEmptyPage();
    }

    public IPropertyPage getCurrentSelectedTab(){
        return _currentSelectedTab;
    }
    /**
     * @return the _propertyActivators
     */
    public PropertyPageInfo[] getPropertyActivators() {
        return _propertyActivators;
    }
}
