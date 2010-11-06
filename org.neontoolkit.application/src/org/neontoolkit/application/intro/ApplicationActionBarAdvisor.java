package org.neontoolkit.application.intro;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // for the Search Menu
    private IWorkbenchAction searchAction;
    
	// for the Help Menu
    private IWorkbenchAction introAction;
    private IWorkbenchAction helpAction;
    private IWorkbenchAction searchHelpAction;
    private IWorkbenchAction aboutAction;

	// for the Windows Menu
    private IWorkbenchAction openPerspectiveAction;
    private IWorkbenchAction customizePerspectiveAction;
    private IWorkbenchAction savePerspectiveAction;
    private IWorkbenchAction closePerspectiveAction;
    private IWorkbenchAction closeAllPerspectivesAction;
//    private IWorkbenchAction closeAllPerspectivesAction;
//    private IWorkbenchAction closeAllPerspectivesAction;
    private MenuManager viewMenu; 
    private IWorkbenchAction resetPerspectiveAction;
    private IWorkbenchAction preferencesAction;
    
    // for the File Menu
    private IWorkbenchAction newAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction switchWorkspaceAction;
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

		customizePerspectiveAction = ActionFactory.EDIT_ACTION_SETS.create(window);
		register(customizePerspectiveAction);
		savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create(window);
		register(savePerspectiveAction);
		closeAllPerspectivesAction = ActionFactory.CLOSE_ALL_PERSPECTIVES.create(window);
		register(closeAllPerspectivesAction);
		closePerspectiveAction = ActionFactory.CLOSE_PERSPECTIVE.create(window);
		register(closePerspectiveAction);
		savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create(window);
		register(savePerspectiveAction);
		customizePerspectiveAction = ActionFactory.EDIT_ACTION_SETS.create(window);
		register(customizePerspectiveAction);
        viewMenu = new MenuManager("&Show View");
        IContributionItem viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        viewMenu.add(viewList);

		resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
		register(resetPerspectiveAction);
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);
		
		// for the File Menu
		newAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);
		newAction.setText("New");
		newAction.setImageDescriptor(null);
		
		
		register(newAction);
		saveAction = ActionFactory.SAVE.create(window);
		register(saveAction);
		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		register(saveAllAction);
//		switchWorkspaceAction = IDEActionFactory.OPEN_WORKSPACE.create(window);
//		register(switchWorkspaceAction);
//		restartAction = ActionFactory.REVERT.create(window);
//		register(restartAction);
		importAction = ActionFactory.IMPORT.create(window);
		register(importAction);
		exportAction = ActionFactory.EXPORT.create(window);
		register(exportAction);
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
		
		
		// for the Search Menu
		searchAction = ActionFactory.EDIT_ACTION_SETS.create(window);
		register(searchAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		// File Menu
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
		fileMenu.add(newAction);
		fileMenu.add(saveAction);
//		fileMenu.add(saveAllAction);
//		fileMenu.add(switchWorkspaceAction);
//		fileMenu.add(restartAction);
		fileMenu.add(new Separator());
		fileMenu.add(importAction);
		fileMenu.add(exportAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		
		// Window Menu
		MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
		windowMenu.add(viewMenu);
		windowMenu.add(new Separator());
		windowMenu.add(openPerspectiveAction);
		windowMenu.add(customizePerspectiveAction);
		windowMenu.add(closeAllPerspectivesAction);
		windowMenu.add(closePerspectiveAction);
		windowMenu.add(savePerspectiveAction);
		windowMenu.add(new Separator());
		windowMenu.add(preferencesAction);
		
		// Help
		MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
		helpMenu.add(introAction);
		helpMenu.add(helpAction);
		helpMenu.add(searchHelpAction);
		helpMenu.add(new Separator());
		helpMenu.add(aboutAction);
		
		MenuManager searchMenu = new MenuManager("&Search", "org.eclipse.search.menu");
		
		menuBar.add(fileMenu);
		menuBar.insertAfter(IWorkbenchActionConstants.M_FILE, searchMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		

	}

}
