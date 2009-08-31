package org.neontoolkit.gui.result;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;

public class ResultView extends ViewPart {
	public static final String ID = "org.neontoolkit.gui.views.resultview"; //$NON-NLS-1$
	
	private static final String ORDER_GROUP = "orderGroup"; //$NON-NLS-1$
	
	private Label _headerImage;
	private Label _headerLabel;
	private CTabFolder _pageContainer;
	private PagePullDownAction _pullDownAction;
	private Action _removeAction;
	private Action _removeAllAction;

	public ResultView() {
	}

	@Override
	public void createPartControl(Composite parent) {
	    PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.QUERY_RESULT_VIEW);
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		
		_headerImage = new Label(composite, SWT.NONE);
		_headerImage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		_headerLabel = new Label(composite, SWT.NONE);
		_headerLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		_pageContainer = new CTabFolder(composite, SWT.BOTTOM | SWT.FLAT);
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.horizontalSpan = 2;
		_pageContainer.setLayoutData(gd);
		_pageContainer.setTabHeight(0);
		
		initBars();
	}
	
	public void selectTab(CTabItem tabItem) {
		if (tabItem != null) {
			IResultPage page = (IResultPage)tabItem.getData();
			MenuAction menuAction = getMenuAction(tabItem);
			
			ImageDescriptor desc = page.getImageDescriptor();
			if (desc != null) {
				_headerImage.setImage(desc.createImage());
			}
			_headerLabel.setText(page.getHeader());
			_pageContainer.setSelection(tabItem);
			
			for (int i=0; i<_pullDownAction.getItems().length; i++) {
			    MenuAction action = _pullDownAction.getItems()[i];
			    IResultPage itemPage = (IResultPage)action.getTabItem().getData();
			    if (itemPage != null) {
			        action.setText("["+(i+1)+"] "+itemPage.getMenuText()); //$NON-NLS-1$ //$NON-NLS-2$
			    }
				action.setChecked(false);
			}
			menuAction.setChecked(true);
		}
		else {
			_headerImage.setImage(null);
			_headerLabel.setText(Messages.ResultView_2);
			
		}
		_headerLabel.getParent().layout(new Control[]{_headerImage, _headerLabel});
	}
	
	@Override
	public void setFocus() {
	    _pageContainer.setFocus();
		if (_pageContainer.getSelectionIndex() != -1) {
		    _pageContainer.getSelection().getControl().setFocus();
		}
	}
	
	/**
	 * Shows the given result in the result page with the given id. If no result
	 * page can be found, an <code>IllegalArgumentException</code> is thrown.
	 * The source object contains information about the source of the result.
	 * This might be a query  or sth. else that produced the result.
	 * @param pageId
	 * @param source
	 * @param result
	 */
	public void showResult(String pageId, Object source, Object[] result) throws IllegalArgumentException {
		IResultPage page = NeOnUIPlugin.getDefault().getResultPage(pageId);
		if (page == null) {
			//No result page with the given id found
			throw new IllegalArgumentException(Messages.ResultView_3+pageId);
			
		}
		CTabItem tab = getTab(page);
		if (tab == null) {
			tab = createPage(page);
		}
		else {
		    //tab existed, put menu item on top
	        MenuAction menuAction = getMenuAction(tab);
		    _pullDownAction.removeAction(menuAction);
		    _pullDownAction.addAction(menuAction);
		}
		page.setInput(source, result);
        MenuAction menuAction = getMenuAction(tab);
        menuAction.setText(page.getMenuText());

		selectTab(tab);
		
		checkEnablement();
		
		getSite().getPage().activate(this);
	}
	
	/**
	 * Returns the tab associated with the given result page.
	 * If no such page exists, <code>null</code> is returned.
	 * @param page
	 * @return
	 */
	private CTabItem getTab(IResultPage page) {
		for (CTabItem tab:_pageContainer.getItems()) {
			if (tab.getData() == page) {
				return tab;
			}
		}
		return null;
	}
	
	/**
	 * Returns the menu item in the pull down menu that is 
	 * associated with the given property page. If no such menu
	 * item exists, <code>null</code> is returned.
	 * @param page
	 * @return
	 */
	private MenuAction getMenuAction(CTabItem tabItem) {
		for (MenuAction action: _pullDownAction.getItems()) {
			MenuAction mAction = (MenuAction)action; 
			if (mAction.getTabItem() == tabItem) {
				return mAction;
			}
		}
		return null;
	}
	
	/**
	 * Creates the result page in a new tab.
	 * @param page
	 * @return
	 */
	private CTabItem createPage(IResultPage page) {
        Composite composite = new Composite(_pageContainer, SWT.NONE);
    	composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        
        // Create the tab
    	CTabItem item = new CTabItem(_pageContainer, SWT.NONE);
    	item.setControl(composite);
    	item.setData(page);

    	// Create menu entry in the pull down menu
    	MenuAction action = new MenuAction("", page.getImageDescriptor(), item) { //$NON-NLS-1$
			@Override
			public void run() {
				selectTab(getTabItem());
			}
		};
		_pullDownAction.addAction(action);
    	
    	// Create tab content
    	page.createPage(composite);
    	return item;
	}
	
	/**
	 * initialize the toolbar
	 */
	private void initBars() {
		_pullDownAction = new PagePullDownAction();
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(new Separator(ORDER_GROUP));
		
		_removeAction = new Action(
				Messages.ResultView_5, 
				NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.REMOVE)) {
			@Override
			public void run() {
				CTabItem selection = _pageContainer.getSelection();
				MenuAction selectedAction = getMenuAction(selection);
				_pullDownAction.removeAction(selectedAction);
				((IResultPage)selection.getData()).dispose();
				selection.dispose();
				selectTab(_pageContainer.getSelection());
				checkEnablement();
			}
		};
		_removeAction.setDisabledImageDescriptor(NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.REMOVE_DISABLED));
		_removeAction.setToolTipText(Messages.ResultView_6);
		_removeAllAction = new Action(
				Messages.ResultView_7, 
				NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.REMOVE_ALL)) {
			@Override
			public void run() {
				_pullDownAction.removeAllActions();
				for (CTabItem item:_pageContainer.getItems()) {
					((IResultPage)item.getData()).dispose();
					item.dispose();
				}
				selectTab(null);
				checkEnablement();
			}
		};
		_removeAllAction.setDisabledImageDescriptor(NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.REMOVE_ALL_DISABLED));
		_removeAllAction.setToolTipText(Messages.ResultView_8);
		
		toolbar.appendToGroup(ORDER_GROUP, _removeAction);
		toolbar.appendToGroup(ORDER_GROUP, _removeAllAction);
		toolbar.add(_pullDownAction);
		
		checkEnablement();
	}

	/**
	 * Disables actions, if not applicable.
	 */
	private void checkEnablement() {
		_removeAction.setEnabled(_pageContainer.getItemCount() > 0);
		_removeAllAction.setEnabled(_pageContainer.getItemCount() > 0);
	}
}
