package org.neontoolkit.application.intro;

import java.util.LinkedList;

import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.ui.internal.WorkbenchWindow;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    @SuppressWarnings("restriction")
	@Override
    public void postWindowCreate() {
    	super.postWindowCreate();
    	if(this.getWindowConfigurer().getWindow() instanceof WorkbenchWindow){
	    	MenuManager manager = ((WorkbenchWindow)this.getWindowConfigurer().getWindow()).getMenuBarManager();
	    	IContributionItem hFile = manager.find("file");
	    	if(hFile instanceof MenuManager){
	    		MenuManager file = (MenuManager)hFile;
	    		for(IContributionItem item : file.getItems()){
	    			if(item != null && item.getId() != null){
	    				if(item.getId().contains("LineDelimitersTo") || item.getId().contains("openLocalFile"))
	        	    		item.setVisible(false);
	    			}
	    		}
	    	}
	    	IContributionItem hHelp = manager.find("help");
	    	if(hHelp instanceof MenuManager){
	    		MenuManager help = (MenuManager)hHelp;
	    		for(IContributionItem item : help.getItems()){
	    			if(item != null && item.getId() != null){
	    				if(item.getId().contains("LineDelimitersTo") || item.getId().contains("openLocalFile"))
	        	    		item.setVisible(false);
	    			}
	    		}
	    	}
//	    	IContributionItem hRun1 = manager.find("org.eclipse.ui.run");
//	    	hRun1.setVisible(false);
//	    	IContributionItem[] items = manager.getItems();
//	    	LinkedList<IContributionItem> itemToRemove = new LinkedList<IContributionItem>();
//	    	for(IContributionItem item : manager.getItems()){
//	    		System.out.println(item.getId());
//	    		if(item instanceof MenuManager && item.getId() != null){
//	    			if(item.getId().contains("ExternalToolsSet") || item.getId().contains("org.eclipse.ui.run")){
//	    				itemToRemove.add(item);
//	    				item.setVisible(false);
//	    			}
//	    		}
//	    		if(item instanceof ActionSetContributionItem && ((ActionSetContributionItem)item).getActionSetId() != null){
//	    			if(((ActionSetContributionItem)item).getActionSetId().contains("ExternalToolsSet") || ((ActionSetContributionItem)item).getActionSetId().contains("org.eclipse.ui.run")){
//	    				((ActionSetContributionItem)item).getInnerItem().setVisible(false);
//	    				itemToRemove.add(item);
//	    				item.setVisible(false);
//	    			}
//	    		}
//	    		System.out.println("visible: " + item.isVisible());
//	    		System.out.println("================");
//	    	}
//	    	for(IContributionItem item : itemToRemove)
//	    		manager.remove(item);
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
	    	System.out.println("run2");
    	}    		
    }
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1000, 700));
        configurer.setShowCoolBar(true);
		configurer.setShowPerspectiveBar(true);
        configurer.setShowStatusLine(false);
        configurer.setTitle(Messages.ApplicationWorkbenchWindowAdvisor_ApplicationTitle);
        
		IPreferenceStore apiStore = PlatformUI.getPreferenceStore();
		apiStore.setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, "TOP_RIGHT"); 
    }
}
