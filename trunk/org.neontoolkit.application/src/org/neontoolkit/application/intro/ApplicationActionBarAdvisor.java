package org.neontoolkit.application.intro;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private IWorkbenchAction introAction;
    
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		introAction = ActionFactory.INTRO.create(window);
		register(introAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		
//		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
//		MenuManager searchMenu = new MenuManager("&Search", "search");
//		MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);

		// Help
		MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
		helpMenu.add(introAction);
		
//		menuBar.add(fileMenu);
//		menuBar.add(searchMenu);
//		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

	}

}
