/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.swt.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.swt.Messages;


/* 
 * Created on 12.01.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
/**
 * This class represents a selection dialog with a tree control.
 * ContentProviders and LabelProviders can be set to customize the dialog.
 * 
 * @author Dirk Wenke
 */
public class TreeSelectionDialog extends Dialog {
	private String _title;
	protected TreeViewer _viewer;
	protected ITreeContentProvider _contentProvider;
	protected ILabelProvider _labelProvider;
	protected IStructuredSelection _selection;
	private int _treeStyle;
	
	public TreeSelectionDialog(Shell parentShell, String shellTitle, int treeStyle) {
		super(parentShell);
		_title = shellTitle;
		_treeStyle = treeStyle;
	}

	@Override
	protected int getShellStyle() {
		return  SWT.SHELL_TRIM;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(_title);

		Composite composite = (Composite)super.createDialogArea(parent);
		Composite innerComposite = new Composite(composite, SWT.NONE);
		innerComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
		GridLayout gl= new GridLayout();
		gl.numColumns= 1;
		innerComposite.setLayout(gl);
		
		Label label = new Label(innerComposite, SWT.NONE);
		label.setText(Messages.TreeSelectionDialog_0); 
		label.setLayoutData(new GridData());
		
		_viewer = new TreeViewer(innerComposite, _treeStyle);
		_viewer.setUseHashlookup(true);
		if (_contentProvider != null) {
			_viewer.setContentProvider(_contentProvider);
		}
		if (_labelProvider != null) {
			_viewer.setLabelProvider(_labelProvider);
		}
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.minimumHeight = 150;
		gd.minimumWidth = 200;
		_viewer.getTree().setLayoutData(gd);
		_viewer.setInput(""); //$NON-NLS-1$
		_viewer.setExpandedElements(_contentProvider.getElements(_viewer.getTree()));

		return composite;
	}
	
	public void setContentProvider(ITreeContentProvider contentProvider) {
		_contentProvider = contentProvider;
	}
	
	public void setLabelProvider(ILabelProvider labelProvider) {
		_labelProvider = labelProvider;
	}
	
	public Object getSelection() {
		return _selection;
	}
	
	@Override
	protected void okPressed() {
		_selection = (IStructuredSelection)_viewer.getSelection();
		super.okPressed();
	}
}
