/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/* 
 * Created on: 30.01.2008
 * Created by: Dirk Wenke
 *
 * Keywords: 
 */
/**
 * Instances of this class represent a interface object that
 * provides multiple elements in radio button style.
 * Only one of the elements can be selected 
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.HORITONTAL, SWT.VERTICAL</dd>
 * </dl>
 * <p>
 * Note: The style flag indicates the layout of the component.
 * </p>
 */

public class RadioButtonComposite extends Composite {
    protected Button[] _buttons;

    /**
     * @param parent
     * @param style
     */
    public RadioButtonComposite(Composite parent, int style, String[] options) {
        this(parent, style, options, null);
    }
    
    /**
     * @param parent
     * @param style
     */
    public RadioButtonComposite(Composite parent, int style, String[] options, String[] tooltips) {
        this(parent, style, options, tooltips, null);
    }
    
    public RadioButtonComposite(Composite parent, int style, String[] options, String[] tooltips, Object[] data) {
        super(parent, SWT.NONE);
        GridLayout layout = new GridLayout(getNumberOfColumns(style, options.length), true);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        assert tooltips == null || options.length == tooltips.length;
        assert data == null || options.length == data.length;
        
        _buttons = new Button[options.length];
        for (int i=0; i<options.length; i++) {
            Control control = createButton(this, i, options[i], tooltips != null ? tooltips[i] : null, data != null ? data[i] : null);
            control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        }
    }
    
    protected Control createButton(Composite c, int index, String text, String tooltip, Object data) {
        _buttons[index] = new Button(c, SWT.RADIO);
        _buttons[index].setText(text);
        if (tooltip != null) {
            _buttons[index].setToolTipText(tooltip);
        }
        if (data != null) {
            _buttons[index].setData(data);
        }
        return _buttons[index];
    }
    
    public void setSelection(String option) {
        for (int i=0; i<_buttons.length; i++) {
            if (_buttons[i].getText().equals(option)) {
                setSelection(i);
            }
        }
    }
    
    public void setSelectionData(Object data) {
        for (int i=0; i<_buttons.length; i++) {
            if (data.equals(_buttons[i].getData())) {
                setSelection(i);
            }
        }
    }

    public void setSelection(int index) {
        for (int i=0; i<_buttons.length; i++) {
            _buttons[i].setSelection(index == i);
        }
    }

    public String getSelection() {
        for (Button button: _buttons) {
            if (button.getSelection()) {
                return button.getText();
            }
        }
        return null;
    }
    
    public Object getSelectionData() {
        for (Button button: _buttons) {
            if (button.getSelection()) {
                return button.getData();
            }
        }
        return null;
    }

    private int getNumberOfColumns(int style, int numberOfElements) {
        return style == SWT.HORIZONTAL ? numberOfElements : 1; 
    }
    
    public void addSelectionListener(SelectionListener adapter) {
        for (int i = 0; i < _buttons.length; i++) {
            _buttons[i].addSelectionListener(adapter);
        }
    }
}
