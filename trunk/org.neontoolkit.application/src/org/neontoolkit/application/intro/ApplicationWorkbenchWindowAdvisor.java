package org.neontoolkit.application.intro;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

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
