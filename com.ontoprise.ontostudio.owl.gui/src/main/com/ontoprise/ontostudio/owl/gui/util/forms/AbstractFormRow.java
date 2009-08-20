/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

/**
 * Holds text widgets of a form row, and the axioms behind them.
 * 
 * @author werner
 * 
 */
public abstract class AbstractFormRow {

    /*
     * Widgets
     */
    private Composite _rowComp;
    private List<Composite> _widgets;
    private int _cols;

    protected Button _cancelButton;
    protected List<Button> _buttonsToDisable;

    /*
     * Data
     */
    private OWLAxiom _axiom;

    /*
     * RowHandler
     */
    protected AbstractRowHandler _handler;

    public AbstractFormRow(FormToolkit toolkit, Composite parent, int cols) {
        initComposite(toolkit, parent, cols);
        _widgets = new ArrayList<Composite>();
        _buttonsToDisable = new ArrayList<Button>();
    }

    private void initComposite(FormToolkit toolkit, Composite parent, int cols) {
        _cols = cols;
        _rowComp = toolkit.createComposite(parent);

        GridLayout layout = new GridLayout(cols + 1, false);
        layout.marginHeight = 0;
        layout.marginBottom = 0;
        layout.marginLeft = 2;
        layout.marginRight = 2;
        layout.marginTop = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 2;
        layout.makeColumnsEqualWidth = false;

        _rowComp.setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        _rowComp.setLayoutData(layoutData);
    }

    public Composite getParent() {
        return _rowComp;
    }

    public Button getCancelButton() {
        return _cancelButton;
    }

    public List<Composite> getWidgets() {
        return _widgets;
    }

    public OWLAxiom getAxiom() {
        return _axiom;
    }

    public void addWidget(Composite widget) {
        _widgets.add(widget);
    }

    public void removeWidget(Composite widget) {
        _widgets.remove(widget);
    }

    public int getColCount() {
        return _cols;
    }

    protected Button createEditButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createEditButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }
    
    public void disposeButton(Button b) {
        if (_buttonsToDisable.contains(b)) {
            _buttonsToDisable.remove(b);
        }
        b.dispose();
    }

    protected Button createAddButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createAddButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    protected Button createRemoveButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createRemoveButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    protected Button createSaveButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createSaveButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    protected Button createCancelButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createCancelButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    /**
     * This is called when an Edit button is pressed to resize all text widgets in current row.
     * 
     * @param widgets
     */
    protected void maximizeAllWidgets(List<Composite> widgets) {
        for (Composite widget: widgets) {
            if (widget instanceof StyledText) {
                maximizeWidget(((StyledText) widget), false);
            }
        }
        _handler.layoutSections();
        _handler.reflow();
    }

    /**
     * Enlarges the passed text widget, so that the whole text is visible.
     * 
     * @param text
     */
    protected void maximizeWidget(final StyledText text) {
        maximizeWidget(text, true);
    }

    protected void maximizeWidget(final StyledText text, boolean doLayout) {
        GridData data = (GridData) text.getLayoutData();
        String textValue = text.getText();
        Point size = text.getSize();
        int averageCharWidth = new GC(text).getFontMetrics().getAverageCharWidth();
        int completeWidth = averageCharWidth * textValue.length();
        float neededLines = (float) completeWidth / size.x;
        if (size.x != 0 && completeWidth > size.x) {
            int neededHeight = (int) Math.ceil(neededLines) * text.getLineHeight();

            data.heightHint = neededHeight;
            text.setLayoutData(data);
            if (doLayout) {
                _handler.layoutSections();
                _handler.reflow();
            }
        }
    }

    protected void enableWidgets(final List<Composite> widgets) {
        for (Iterator<Composite> iterator = widgets.iterator(); iterator.hasNext();) {
            Composite composite = (Composite) iterator.next();
            enable(composite, true);
        }
    }

    /**
     * Enables/Disables the text widget depending on parameter <code>enabled</code>
     * 
     * @param text
     * @param enabled
     */
    protected void enable(Composite text, boolean enabled) {
        OWLGUIUtilities.enable(text, enabled);
    }

    protected void editPressed(Button editButton, Button removeButton) {
        editButton.setText(OWLGUIUtilities.BUTTON_LABEL_SAVE);
        removeButton.setText(OWLGUIUtilities.BUTTON_LABEL_CANCEL);
        editButton.getParent().setBackground(new Color(null, 250, 250, 210));
        disableOtherButtons(new Button[] {editButton, removeButton}, _buttonsToDisable);
    }

    /**
     * Disables the buttons of all other rows
     * 
     * @param exceptions
     */
    protected void disableOtherButtons(Button[] exceptions, List<Button> buttonsToDisable) {
        for (Button b1: buttonsToDisable) {
            if (b1.isDisposed()) {
                return;
            }
            boolean match = false;
            for (int i = 0; i < exceptions.length; i++) {
                if (exceptions[i].equals(b1)) {
                    match = true;
                }
            }
            if (!match) {
                b1.setEnabled(false);
            }
        }
    }

    public abstract void init(AbstractRowHandler handler) throws NeOnCoreException;

    public abstract Button getSubmitButton();
}
