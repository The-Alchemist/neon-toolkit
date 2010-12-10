package org.neontoolkit.application.intro;

import java.util.LinkedList;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.internal.provisional.action.ToolBarContributionItem2;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ActionSetContributionItem;
import org.eclipse.ui.internal.PluginActionCoolBarContributionItem;
import org.eclipse.ui.internal.WorkbenchWindow;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
	@Override
    public void postWindowCreate() {
    	super.postWindowCreate();
    	if(this.getWindowConfigurer().getWindow() instanceof WorkbenchWindow){
    		WorkbenchWindow window = (WorkbenchWindow)this.getWindowConfigurer().getWindow();
	    	MenuManager manager = window.getMenuBarManager();
	    	
	    	removeItemsFromMenu(manager, "file", new String[]{"LineDelimitersTo","openLocalFile"});
	    	removeItemsFromMenu(manager, "help", new String[]{"showKeyAssistHandler"});
	    	
	    	removeRunMenu(manager);

	    	removeNavigationIconsFromCoolBar();
    	}   

    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().resetPerspective();
    	
    }


	public void preWindowOpen() {
		
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1200, 800));
//        configurer.setShowCoolBar(true);
		configurer.setShowPerspectiveBar(true);
//        configurer.setShowStatusLine(false);
//        configurer.setTitle(Messages.ApplicationWorkbenchWindowAdvisor_ApplicationTitle);
//        
		IPreferenceStore apiStore = PlatformUI.getPreferenceStore();
		apiStore.setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, "TOP_RIGHT");
		

    	super.preWindowOpen();
    	configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
    }


	/*
	 * PRIVATE HELP METHODS to remove elements from the GUI
	 */
	
	/**
	 * removes the unneeded navigate and navigation Icons from the CoolBar
	 */
	private void removeNavigationIconsFromCoolBar() {
    	CoolBarManager managerCoolBar = ((WorkbenchWindow)this.getWindowConfigurer().getWindow()).getCoolBarManager();
    	for(IContributionItem item : managerCoolBar.getItems()){
    		if(item instanceof ToolBarContributionItem2){
    			ToolBarContributionItem2 tbcItem = (ToolBarContributionItem2)item;
    			if(tbcItem.getId().equals("org.eclipse.ui.edit.text.actionSet.navigation")){
    				item.setVisible(false);
    			}
    			if(tbcItem.getId().equals("org.eclipse.ui.workbench.navigate")){
//    				tbcItem.setVisible(false);
    				for(IContributionItem navigateItem : tbcItem.getToolBarManager().getItems()){
    					if(navigateItem instanceof PluginActionCoolBarContributionItem){
    						PluginActionCoolBarContributionItem ccItem = (PluginActionCoolBarContributionItem)navigateItem;
	    					if(ccItem.getId().contains("gotoNextAnnotation")){
	    						ccItem.setVisible(false);
	    					} else if(ccItem.getId().contains("gotoPreviousAnnotation")){
	    						ccItem.setVisible(false);
//	    					} else if(ccItem.getId().contains("gotoLastEditPosition")){
//	    						ccItem.setVisible(false);
	    					}
    					}
    				}
    			}
    		}
    	}
		
	}

	/**
	 * sets the items of itemNames in menu menuName invisible
	 * @param manager - Manager of the menuBar
	 * @param menuName - ID of the menu
	 * @param itemNames - Identifying substring of the ID of the item
	 */
	private void removeItemsFromMenu(MenuManager manager, String menuName, String[] itemNames) {
    	IContributionItem hFile = manager.find(menuName);
    	if(hFile instanceof MenuManager){
    		MenuManager file = (MenuManager)hFile;
    		for(IContributionItem item : file.getItems()){
    			if(item != null && item.getId() != null){
    				for(String itemName : itemNames){
    					if(item.getId().contains(itemName))
            	    		item.setVisible(false);
    				}
    			}
    		}
    	}
	}

	/**
	 * Method to remove the Run Menu based on:
	 * 	* run menu itself
	 * 	* External Tools Set
	 * @param manager - Manager of the MenuBar
	 */
	private void removeRunMenu(MenuManager manager) {
    	IContributionItem hRun1 = manager.find("org.eclipse.ui.run");
		hRun1.setVisible(false);
		
		LinkedList<IContributionItem> itemToRemove = new LinkedList<IContributionItem>();
		for(IContributionItem item : manager.getItems()){
			if(item instanceof MenuManager && item.getId() != null){
				if(item.getId().contains("ExternalToolsSet") || item.getId().contains("org.eclipse.ui.run")){
					itemToRemove.add(item);
					item.setVisible(false);
				}
			}
			if(item instanceof ActionSetContributionItem && ((ActionSetContributionItem)item).getActionSetId() != null){
				if(((ActionSetContributionItem)item).getActionSetId().contains("ExternalToolsSet") || ((ActionSetContributionItem)item).getActionSetId().contains("org.eclipse.ui.run")){
					((ActionSetContributionItem)item).getInnerItem().setVisible(false);
					itemToRemove.add(item);
					item.setVisible(false);
				}
			}
		}
		for(IContributionItem item : itemToRemove)
			manager.remove(item);
		
		LinkedList<MenuItem> menuItemToRemove = new LinkedList<MenuItem>();
		for(MenuItem menuItem : manager.getMenu().getItems()){
			Object data = menuItem.getData();
			if(data instanceof MenuManager){
				MenuManager item = (MenuManager)data;
	    		if(item.getId() != null){
	    			if(item.getId().contains("ExternalToolsSet") || item.getId().contains("org.eclipse.ui.run")){
	    				menuItemToRemove.add(menuItem);
	    				item.setVisible(false);
	    			}
	    		}
			}
			if(data instanceof ActionSetContributionItem){
				ActionSetContributionItem item = (ActionSetContributionItem)data;
	    		if(item.getActionSetId() != null){
	    			if(((ActionSetContributionItem)item).getActionSetId().contains("ExternalToolsSet") || ((ActionSetContributionItem)item).getActionSetId().contains("org.eclipse.ui.run")){
	    				((ActionSetContributionItem)item).getInnerItem().setVisible(false);
	    				menuItemToRemove.add(menuItem);
	    				item.setVisible(false);
	    			}
	    		}
			}
		}
		for(MenuItem item : menuItemToRemove){
			item.setEnabled(false);
			item.getMenu().setVisible(false);
		}
		
	}
}
