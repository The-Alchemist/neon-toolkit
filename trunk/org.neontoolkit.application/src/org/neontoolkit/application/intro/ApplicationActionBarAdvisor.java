package org.neontoolkit.application.intro;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// for the Help Menu
    private IWorkbenchAction introAction;
    private IWorkbenchAction helpAction;
    private IWorkbenchAction searchHelpAction;
    private IWorkbenchAction aboutAction;

	// for the Windows Menu
    private IWorkbenchAction openPerspectiveAction;
    private MenuManager viewMenu; 
    private IWorkbenchAction resetPerspectiveAction;
    private IWorkbenchAction preferencesAction;
    
    // for the File Menu
    private IWorkbenchAction newAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAllAction;
//    private IWorkbenchAction switchWorkspaceAction;
//    private IWorkbenchAction restartAction;
    private IWorkbenchAction importAction;
    private IWorkbenchAction exportAction;
    private IWorkbenchAction exitAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		// for the Help Menu
		introAction = ActionFactory.INTRO.create(window);
		register(introAction);
		helpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(helpAction);
		searchHelpAction = ActionFactory.HELP_SEARCH.create(window);
		register(searchHelpAction);
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
		
		// for the Windows Menu
		openPerspectiveAction = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create(window);
		register(openPerspectiveAction);
		
        viewMenu = new MenuManager("&Show View");
        IContributionItem viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        viewMenu.add(viewList);

		resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
		register(resetPerspectiveAction);
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);
		
		// for the File Menu
		newAction = ActionFactory.NEW.create(window);
		register(newAction);
		saveAction = ActionFactory.SAVE.create(window);
		register(saveAction);
		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		register(saveAllAction);
//		switchWorkspaceAction = ActionFactory.NEW.create(window);
//		register(switchWorkspaceAction);
//		restartAction = ActionFactory.REVERT.create(window);
//		register(restartAction);
		importAction = ActionFactory.IMPORT.create(window);
		register(importAction);
		exportAction = ActionFactory.EXPORT.create(window);
		register(exportAction);
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);		
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		// File Menu
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
		fileMenu.add(newAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAllAction);
//		fileMenu.add(switchWorkspaceAction);
//		fileMenu.add(restartAction);
		fileMenu.add(new Separator());
		fileMenu.add(importAction);
		fileMenu.add(exportAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager searchMenu = new MenuManager("&Search", "search");
		
		// Window Menu
		MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
		windowMenu.add(openPerspectiveAction);
		windowMenu.add(viewMenu);
		windowMenu.add(resetPerspectiveAction);
		windowMenu.add(preferencesAction);
		
		// Help
		MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
		helpMenu.add(introAction);
		helpMenu.add(helpAction);
		helpMenu.add(searchHelpAction);
		helpMenu.add(new Separator());
		helpMenu.add(aboutAction);
		
		menuBar.add(fileMenu);
		menuBar.add(searchMenu);
		menuBar.add(windowMenu);
		menuBar.insertAfter(IWorkbenchActionConstants.M_WINDOW, helpMenu);
		
		disableRunMenu(menuBar);
	}

	private void disableRunMenu(IMenuManager menuBar) {
		menuBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		final MenuManager runMenuManager = new MenuManager("&Run", "org.eclipse.ui.run");
		menuBar.add(runMenuManager);
		runMenuManager.setActionDefinitionId("org.eclipse.ui.run");
//		runMenuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
