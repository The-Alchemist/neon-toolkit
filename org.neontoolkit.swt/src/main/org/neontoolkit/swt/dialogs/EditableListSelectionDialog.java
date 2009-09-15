/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.neontoolkit.swt.Messages;

/*
 * Created on 20.01.2004
 */
/**
 * @author Dirk Wenke
 * 
 * This class creates a dialog with a textfield and a list. The user may select
 * an item from the list or type in the textfield. If the user types in the
 * textfield, only the set of matching elements will be displayed in the list.
 */
public class EditableListSelectionDialog extends SelectionDialog {

    /**
     * Sorting options
     */
    public static final int NONE = 0;
    public static final int ALPHABETIC = 1;

    private Text _ontoTextfield;
    private List _list;
    private ListViewer _viewer;

    private Object _selectedItem;
    private Object[] _selectedItems;

    private ListSelectionDialogModel _model;

    private String _initialText = ""; //$NON-NLS-1$
    private Object[] _itemReminder;
    private Image _image;

    public EditableListSelectionDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    public void setModel(ListSelectionDialogModel newModel) {
        _model = newModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
	protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.EditableListSelectionDialog_1); 

        _ontoTextfield = new Text(composite, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        _ontoTextfield.setLayoutData(gd);

        _ontoTextfield.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                updateList();
            }
        });
        _ontoTextfield.setText(_initialText);

        label = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        label.setLayoutData(gd);

        label = new Label(composite, SWT.NONE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        label.setText(Messages.EditableListSelectionDialog_2); 
        label.setLayoutData(gd);

        _list = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        gd.heightHint = 300;
        gd.widthHint = 300;
        _list.setLayoutData(gd);
        _list.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
                if (_list.getSelectionIndex() >= 0) {
                    _selectedItem = _itemReminder[_list.getSelectionIndex()];
                    if (_model.isItem(_selectedItem)) {
                        okPressed();
                    } else {
                        _ontoTextfield.setText(_selectedItem.toString());
                        updateList();
                        _selectedItem = null;
                    }
                }
            }

            public void mouseDown(MouseEvent e) {
            }

            public void mouseUp(MouseEvent e) {
            }
        });
        _viewer = new ListViewer(_list);
        _viewer.setContentProvider(new IStructuredContentProvider() {

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            public Object[] getElements(Object inputElement) {
                return _model.getListItems(_ontoTextfield.getText());
            }
        });

        _viewer.setLabelProvider(new LabelProvider());
        _viewer.addSelectionChangedListener(new ISelectionChangedListener() {
        	/* (non-Javadoc)
        	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
        	 */
        	public void selectionChanged(SelectionChangedEvent event) {
        		getButton(IDialogConstants.OK_ID).setEnabled(true);
        	}
        });
        updateList();

        _list.getVerticalBar().addSelectionListener(new SelectionAdapter() {

            @Override
			public void widgetSelected(SelectionEvent e) {
                ScrollBar bar = (ScrollBar) e.getSource();
                if (_model.needsUpdate(bar.getSelection(), bar.getThumb())) {
                    _list.update();
                }
            }
        });

        return composite;
    }

    protected void updateList() {
        _itemReminder = _model.getListItems(_ontoTextfield.getText());
        _viewer.setInput(_itemReminder);
        if (_itemReminder.length == 0) {
    		getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    protected Object[] filter(Object[] elements, String startString) {
        java.util.List<Object> itemList = new ArrayList<Object>();
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].toString().startsWith(startString)) {
                itemList.add(elements[i]);
            }
        }
        return itemList.toArray();
    }

    public Object getSelectedItem() {
        return _selectedItem;
    }
    
    public Object[] getSelectedItems() {
        return _selectedItems;
    }
    
    public void saveSelection() {
        int[] selectionIndizes = _list.getSelectionIndices();
        Object[] selections = new Object[selectionIndizes.length];
        for (int i = 0; i < selectionIndizes.length; i++) {
            selections[i] = _list.getItem(selectionIndizes[i]);
        }
        _selectedItems = selections;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
	protected void okPressed() {
        if (_selectedItem == null) {
            if (_itemReminder != null && _itemReminder.length >= 0) {
                _selectedItem = _itemReminder[_list.getSelectionIndex()];
            }
        }
        saveSelection();
        super.okPressed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#open()
     */
    public int open(String init) {
        _initialText = init;
        return open();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    @Override
	protected void cancelPressed() {
        _itemReminder = null;
        _ontoTextfield.setText(""); //$NON-NLS-1$
        super.cancelPressed();
    }

    public ListSelectionDialogModel getModel() {
        return _model;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.SelectionDialog#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (_image != null) {
            shell.setImage(_image);
        }
    }

    public void setImage(Image image) {
        _image = image;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createButtonBar(Composite parent) {
    	Control control = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);

		return control;
    }
}
