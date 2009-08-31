/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.navigator.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.handlers.HandlerUtil;
import org.neontoolkit.core.exception.InformationAlreadyExistsException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.EditorEvent;
import org.neontoolkit.gui.navigator.IEditorListener;
import org.neontoolkit.gui.navigator.MTreeView;

/*
 * Created on 19.11.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractNewHandler extends AbstractHandler {
	protected MTreeView _view;

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		_view = HandlerUtil.getActivePart(arg0) instanceof MTreeView ? 
				(MTreeView)HandlerUtil.getActivePart(arg0) : null;
		return runWithArgumentsSet();
	}
	
	/**
	 * Runs the handler. The _view variable has to be set before.
	 * @return
	 */
	protected Object runWithArgumentsSet() {
		if (_view != null) {
			if (_view.isEditing()) {
				_view.finishEditing(false);
			}
			TreeItem[] selection = _view.getTreeViewer().getTree().getSelection();
			if (selection != null && selection.length > 0) {
			    TreeItem parentItem = selection[0];
			    if (!parentItem.getExpanded()) {
			        _view.getTreeViewer().expandToLevel(parentItem.getData(), 1);
			    }
				Object newItem = createNewItem(selection[0].getData());
				selection = _view.getTreeViewer().getTree().getSelection();
				if (newItem != null) {
					final TreeItem newTreeItem = new TreeItem(selection[0], SWT.HORIZONTAL, 0);
					newTreeItem.setData(newItem);
					newTreeItem.setText(newItem.toString());
					newTreeItem.setImage(getImage());
					_view.startEditing(newTreeItem);
					_view.addEditorListener(new IEditorListener() {
                        public void editingCancelled(EditorEvent event) {
                            _view.removeEditorListener(this);
                        }
                        public void editingFinished(EditorEvent event) {
                        	try {
                       			if (finishEditing(event.getItem(), event.getText())) {
                       				_view.removeEditorListener(this);
                       			}
                       			else {
                       			    if (event.isFinishedRegularly()) {
                       			        _view.startEditing(newTreeItem, event.getText());
                       			    }
                       			    else {
                                        _view.removeEditorListener(this);
                                        event.getItem().dispose();
                       			    }
                       			}
                        	} catch (InformationAlreadyExistsException ie) {
                        		MessageDialog.openInformation(_view.getSite().getShell(), "Creation not possible!", ie.getMessage()); //$NON-NLS-1$
                        		if (event.isFinishedRegularly()) {
	               					_view.startEditing(newTreeItem, event.getText());
                        		}
                        		else {
                       				_view.removeEditorListener(this);
                        			event.getItem().dispose();
                        		}
                        	} catch (Throwable e) {
                        		new NeonToolkitExceptionHandler().handleException("Creation failed!", e, _view.getSite().getShell()); //$NON-NLS-1$
                        		if (event.isFinishedRegularly() && !(e instanceof SWTException)) {
	               					_view.startEditing(newTreeItem, event.getText());
                        		}
                        		else {
                       				_view.removeEditorListener(this);
                        			event.getItem().dispose();
                        		}
                        	}
                        }
					});
				}
			}
		}
		return null;
	}

	/**
	 * Creates a new TreeItem to be displayed in the GUI. At this point no actions on the datamodel 
	 * have to be done. The insertion of the new element in the datamodel has to be done in the 
	 * performStore() method.
	 * 
	 * @param parent
	 * @return
	 */
	public abstract Object createNewItem(Object parent);
	
	/**
	 * This method should return the image of new created item
	 * @return
	 */
	public abstract Image getImage();
	
	/**
	 * Called when the editing was successful and the changed information has to be stored.
	 * Returns true if the storage was successful, if false is returned, the item
	 * automatically enters edit mode. If an exception is thrown, the exception is
	 * shown in an error dialog.
	 * @param item
	 * @param newText
	 */
	public abstract boolean finishEditing(TreeItem item, String newText) throws Exception;
}
