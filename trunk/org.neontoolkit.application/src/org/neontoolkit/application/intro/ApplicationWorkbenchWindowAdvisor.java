package org.neontoolkit.application.intro;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
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
