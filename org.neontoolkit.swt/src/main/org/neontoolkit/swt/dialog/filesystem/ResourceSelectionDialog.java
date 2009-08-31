/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.swt.dialog.filesystem;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.neontoolkit.swt.Messages;
import org.neontoolkit.swt.dialog.TreeSelectionDialog;


/* 
 * Created on 12.01.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public class ResourceSelectionDialog extends TreeSelectionDialog {

	protected TreeEditor _editor;
	protected Text _text;
	/**
	 * The current edited item, or null if not in edit mode
	 */
	protected TreeItem _editingItem;
	protected TreeItem _parentItem;

	public ResourceSelectionDialog(Shell parentShell, String shellTitle, int treeStyle) {
		super(parentShell, shellTitle, treeStyle);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		_editor = new TreeEditor(_viewer.getTree());


		Composite innerComposite = new Composite(composite, SWT.NONE);
		innerComposite.setLayoutData(new GridData());
		innerComposite.setLayout(new GridLayout());
		
		Button button = new Button(innerComposite, SWT.NONE);
		button.setText(Messages.ResourceSelectionDialog_0); 
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewFolder();
			}
		});

		return composite;
	}
	
	private boolean isEditing() {
		return (_editor.getEditor() != null);
	}

	public void startEditing(TreeItem item) {
		startEditing(item, item.getText());
	}
	
	public void startEditing(TreeItem item, String initialText) {
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		_viewer.getTree().showItem(item);
		_editingItem = item;
		_editor.horizontalAlignment = SWT.LEFT;
		_editor.grabHorizontal = true;
		_editor.minimumWidth = 50;
		_text = new Text(_viewer.getTree(), SWT.SINGLE);
		_text.setText(initialText);
		_text.selectAll();
		_editor.setEditor(_text, item);
		_text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					finishEditing();
				} catch (CoreException e1) {
					cancelEditing();
				}
			}
		});
		_text.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.character == SWT.CR) {
                    //Return pressed, accept value and finish editing
                    try {
						finishEditing();
					} catch (CoreException e1) {
						cancelEditing();
					}
                } else if (e.character == SWT.ESC) {
                    cancelEditing();
                }
            }
		});
		_text.setFocus();
		_text.setSelection(0, _text.getText().length());

	}

	public void finishEditing() throws CoreException {
		String text = _text.getText();
		IContainer folder = (IContainer)_parentItem.getData();
		ContainerGenerator generator = new ContainerGenerator(folder.getFullPath().append(text));
		IContainer container = generator.generateContainer(new NullProgressMonitor());
		_editingItem.setData(container);
		_editingItem.setText(_labelProvider.getText(container));
		_editingItem.setImage(_labelProvider.getImage(container));

		_editor.setEditor(null, null);
		_text.dispose();
		
		
		_editingItem = null;
		_parentItem = null;
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
	
	public void cancelEditing() {
		_editor.setEditor(null, null);
		_text.dispose();
		_editingItem.dispose();
		_editingItem = null;
		_parentItem = null;
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	private void createNewFolder() {
		if (_viewer != null) {
			if (isEditing()) {
				try {
					finishEditing();
				} catch (CoreException e) {
					cancelEditing();
				}
			}
			final TreeItem[] selection = _viewer.getTree().getSelection();
			if (selection != null && selection.length > 0) {
			    _parentItem = selection[0];
			    if (!_parentItem.getExpanded()) {
			        _viewer.expandToLevel(_parentItem.getData(), 1);
			    }
				Object newItem = Messages.ResourceSelectionDialog_1; 
				if (newItem != null) {
					final TreeItem newTreeItem = new TreeItem(selection[0], SWT.HORIZONTAL, 0);
					newTreeItem.setData(newItem);
					newTreeItem.setText(newItem.toString());
//					newTreeItem.setImage(getImage());
					startEditing(newTreeItem);
				}
			}
		}
	}
	
	public IResource getResource() {
		IStructuredSelection selection = (IStructuredSelection)getSelection();
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		else {
			return (IResource)selection.getFirstElement();
		}
	}
}
