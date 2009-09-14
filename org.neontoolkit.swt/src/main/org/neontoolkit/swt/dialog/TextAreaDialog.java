/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt.dialog;

import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/* 
 * Created on 28.10.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class TextAreaDialog extends Dialog {

	private Shell _shell;
	private Text _text;
	private Image _image;
	private String _result;
	
	public TextAreaDialog(Shell parent, int style) {
		super(parent, style);
	}

	public String open(String title, String initialText) {
		_result = initialText;
		Shell parent = getParent();
		_shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.MAX);
		_shell.setText(title); 
		if (_image != null) {
			_shell.setImage(_image);
		}
		createDialog(_shell);
		createButtons(_shell);
		_text.setText(initialText);           

		int width = 600;
		int height = 300;
		width = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.5);
		height = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.33);
		_shell.setSize(width, height);
		int y = parent.getBounds().height / 2;
		int x = parent.getBounds().width / 2;
		y -= _shell.getBounds().height / 2;
		x -= _shell.getBounds().width / 2;
		_shell.setLocation(x, y);
		_shell.open();
		Display display = parent.getDisplay();
		while (!_shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return _result;
	}

	protected void createDialog(Composite composite) {
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;
        
        _text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalSpan = 2;
        gridData.horizontalSpan = 2;
        gridData.heightHint = 160;
        gridData.widthHint = 320;
        _text.setLayoutData(gridData);

        new Label(composite, SWT.NONE).setLayoutData(new GridData());
	}
	
	protected void createButtons(Composite composite) {
        Composite composite2 = new Composite(composite, 0);
        composite2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        GridLayout layout = new GridLayout(2, true);
		composite2.setLayout(layout);
		composite.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});
        
        Button buttonOk = new Button(composite2, SWT.PUSH);
        buttonOk.setText("&OK"); //$NON-NLS-1$
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        buttonOk.setLayoutData(gridData);
        buttonOk.addSelectionListener(new SelectionAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                _result = _text.getText();
                _shell.dispose();
            }
         });
        
        Button buttonCancel = new Button(composite2, SWT.PUSH);
        buttonCancel.setText("&Cancel"); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        buttonCancel.setLayoutData(gridData);
        buttonCancel.addSelectionListener(new SelectionAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
            	_result = null;
                _shell.dispose();
            }
        });
	}
	
	public void setImage(Image image) {
		_image = image;
	}
}
