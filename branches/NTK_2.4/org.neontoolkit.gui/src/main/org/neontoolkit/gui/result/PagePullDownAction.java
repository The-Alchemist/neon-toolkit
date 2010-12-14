/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.result;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;

/*
 * Created on 15.10.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class PagePullDownAction extends Action implements IMenuCreator {
	private Menu _menu;
	private List<MenuAction> _actions = new ArrayList<MenuAction>();

	public PagePullDownAction() {
		setText("Text");  //$NON-NLS-1$
		setToolTipText("Tooltip");  //$NON-NLS-1$
		setImageDescriptor(NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.RESULTS));
		setDisabledImageDescriptor(NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.RESULTS_DISABLED));
		setMenuCreator(this);
		checkEnablement();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	public void dispose() {
		if (_menu != null) {
			_menu.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		if (_menu != null) {
			_menu.dispose();
		}
		_menu = new Menu(parent);
		for (MenuAction action:_actions) {
			addActionToMenu(action);
		}
		return _menu;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	public Menu getMenu(Menu parent) {
		return null;
	}

	/**
	 * Adds the given action to the pull down list of this menu.
	 * @param action
	 */
	public void addAction(MenuAction action) {
		_actions.add(0, action);
		addActionToMenu(action);
	}
	
	/**
	 * Returns all actions contained in the pull down list of this menu.
	 * @return
	 */
	public MenuAction[] getItems() {
		return _actions.toArray(new MenuAction[0]);
	}
	
	/**
	 * Removes the given action from the pull down list of this menu.
	 * If the action is not contained in the list, nothing is removed.
	 * @param action
	 */
	public void removeAction(MenuAction action) {
		_actions.remove(action);
		checkEnablement();
	}
	
	/**
	 * Removes all menu items in the pull down list.
	 */
	public void removeAllActions() {
		_actions.clear();
		checkEnablement();
	}

	/**
	 * internal method to add an action to the list.
	 * @param action
	 */
	private void addActionToMenu(MenuAction action) {
		ActionContributionItem item= new ActionContributionItem(action);
		item.fill(_menu, -1);
		checkEnablement();
	}

	/**
	 * disables the action, if no elements are contained in the pull down
	 * list, otherwise the action is enabled.
	 */
	private void checkEnablement() {
		setEnabled(_actions.size() > 0);
	}
}
