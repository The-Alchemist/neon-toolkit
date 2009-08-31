/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;

/* 
 * Created on: 06.02.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Namespaces, Action
 */
/**
 * An action in the workbench toolbar to enable the user to hide/show the
 * namespaces.
 */
public class ShowNamespaceAction extends SelectionAdapter implements
		IWorkbenchWindowPulldownDelegate {
	private Menu _menu;
	private MenuItem _localItem;
	private MenuItem _uriItem;
	private MenuItem _qNameItem;

	public void run(IAction action) {
		if (_menu != null) {
			_menu.setVisible(true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
		if (_menu != null) {
			_menu.dispose(); // jops 2008.12.05: fix npe
		}
	}

	public void init(IWorkbenchWindow window) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt
	 * .widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		if (_menu == null) {
			_menu = new Menu(parent);
			_localItem = new MenuItem(_menu, SWT.CHECK);
			_localItem.setText(Messages.ShowNamespaceAction_0);
			_localItem.addSelectionListener(this);
			_uriItem = new MenuItem(_menu, SWT.CHECK);
			_uriItem.setText(Messages.ShowNamespaceAction_1);
			_uriItem.addSelectionListener(this);
			_qNameItem = new MenuItem(_menu, SWT.CHECK);
			_qNameItem.setText(Messages.ShowNamespaceAction_2);
			_qNameItem.addSelectionListener(this);
			
			hookSubMenu(parent, _menu);
			switch (NeOnUIPlugin.getDefault().getIdDisplayStyle()) {
			case NeOnUIPlugin.DISPLAY_LOCAL:
				_localItem.setSelection(true);
				break;
			case NeOnUIPlugin.DISPLAY_QNAME:
				_qNameItem.setSelection(true);
				break;
			default:
				_uriItem.setSelection(true);
			}
		}
		return _menu;
	}
	
	protected void hookSubMenu(Control parent, Menu parentMenu) {
	    // empty implementation. subclasses can hook a submenu here. 
	}

    protected void deselect() {
        _localItem.setSelection(false);
        _uriItem.setSelection(false);
        _qNameItem.setSelection(false);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt
	 * .events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {
		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(),
				new Runnable() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@SuppressWarnings("deprecation")
					public void run() {
						IPreferenceStore store = NeOnUIPlugin.getDefault()
								.getPreferenceStore();
						deselect();
						((MenuItem) e.getSource()).setSelection(true);
						if (e.getSource() == _localItem) {
							store.setValue(NeOnUIPlugin.ID_DISPLAY_PREFERENCE,
									NeOnUIPlugin.DISPLAY_LOCAL);
							// for legacy reasons
							if (store
									.getBoolean(NeOnUIPlugin.NAMESPACE_PREFERENCE)) {
								store.setValue(
										NeOnUIPlugin.NAMESPACE_PREFERENCE,
										false);
							}
						} else if (e.getSource() == _qNameItem) {
							store.setValue(NeOnUIPlugin.ID_DISPLAY_PREFERENCE,
									NeOnUIPlugin.DISPLAY_QNAME);
							// for legacy reasons
							if (store
									.getBoolean(NeOnUIPlugin.NAMESPACE_PREFERENCE)) {
								store.setValue(
										NeOnUIPlugin.NAMESPACE_PREFERENCE,
										false);
							}
						} else if (e.getSource() == _uriItem) {
							store.setValue(NeOnUIPlugin.ID_DISPLAY_PREFERENCE,
									NeOnUIPlugin.DISPLAY_URI);
							// for legacy reasons
							if (!store
									.getBoolean(NeOnUIPlugin.NAMESPACE_PREFERENCE)) {
								store
										.setValue(
												NeOnUIPlugin.NAMESPACE_PREFERENCE,
												true);
							}
						}
					}

				});
	}
}
