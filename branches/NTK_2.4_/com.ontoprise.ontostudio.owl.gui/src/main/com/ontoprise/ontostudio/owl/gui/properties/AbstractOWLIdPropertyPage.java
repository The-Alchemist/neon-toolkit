/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.properties.AbstractIDPropertyPage;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalItemHitsComparer;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DescriptionText;
import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * @author werner
 *
 */
public abstract class AbstractOWLIdPropertyPage extends AbstractIDPropertyPage implements IOWLPropertyPage {

    /** The _part listener. */
    protected IPartListener _partListener;

    /** The _manager. */
    protected ISyntaxManager _manager;

    /** The _owl model. */
    protected OWLModel _owlModel;

    /** The _namespaces. */
    protected OWLNamespaces _namespaces;
    
    /** The factory. */
    protected OWLDataFactory _factory;

    /** The _simple texts. */
    protected List<Composite> _simpleTexts;

    /** The _complex texts. */
    protected List<DescriptionText> _complexTexts;

    /** The _use toolbar. */
    protected boolean _useToolbar = false;

    /*
     * JFace Forms variables
     */
    /** The _form. */
    protected ScrolledForm _form;

    /** The _toolkit. */
    protected FormToolkit _toolkit;

    /** The _buttons to disable. */
    protected List<Button> _buttonsToDisable;
    
    public AbstractOWLIdPropertyPage() {
        super();
        _complexTexts = new ArrayList<DescriptionText>();
        _simpleTexts = new ArrayList<Composite>();
    }
    
    /**
     * Prepare form.
     * 
     * @param composite the composite
     * 
     * @return the composite
     */
    protected Composite prepareForm(Composite composite) {
        _toolkit = new FormToolkit(composite.getDisplay());
        _form = _toolkit.createScrolledForm(composite);
        _form.setBackground(_form.getParent().getBackground());
        GridData data = new GridData(GridData.FILL_BOTH | SWT.H_SCROLL | SWT.V_SCROLL);
        _form.setLayoutData(data);
        Composite body = _form.getBody();
        ColumnLayout layout = new ColumnLayout();
        layout.maxNumColumns = 1;
        body.setLayout(layout);
        return body;
    }
    
    /**
     * Layout sections.
     */
    @Override
    public void layoutSections() {
        List<Section> sections = getSections();
        if (sections != null) {
            for (Iterator<Section> iterator = sections.iterator(); iterator.hasNext();) {
                Section section = iterator.next();
                section.layout(true);
            }
        }
    }

    /**
     * Gets the array from description.
     * 
     * @param desc the desc
     * 
     * @return the array from description
     */
    protected String[] getArrayFromDescription(OWLObject desc) {
        if (_manager == null) {
            _manager = OWLPlugin.getDefault().getSyntaxManager();
        }

        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = _manager.getVisitor(_owlModel, idDisplayStyle);
        return (String[]) desc.accept(visitor);
    }

    @Override
    public ScrolledForm getForm() {
        return _form;
    }

    /**
     * Close all toolbars.
     */
    protected void closeAllToolbars() {
        if (_useToolbar) {
            for (DescriptionText dt: _complexTexts) {
                StyledText styledText = dt.getStyledText();
                if (!styledText.isDisposed()) {
                    dt.closeToolBar();
                }
            }
        }
    }

    /**
     * Clear composite.
     * 
     * @param composite the composite
     */
    protected void clearComposite(Composite composite) {
        if (composite != null) {
            Control[] ctr = composite.getChildren();
            for (Control control: ctr) {
                control.dispose();
            }
        }
    }

    protected void handleException(Throwable e, String message, Shell shell) {
        new NeonToolkitExceptionHandler().handleException(message, e, shell);
    }

    /**
     * Update owl model.
     */
    protected void updateOwlModel() {
        try {
            _owlModel = OWLModelFactory.getOWLModel(_ontologyUri, _project);
            _namespaces = _owlModel.getNamespaces();
            _factory = _owlModel.getOWLDataFactory();
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(Messages.ClazzPropertyPage2_56 + _ontologyUri + Messages.ClazzPropertyPage2_59, e, _form.getShell());
        }
    }

    /**
     * Adds the complex text.
     * 
     * @param text the text
     */
    protected void addComplexText(final DescriptionText text) {
        addComplexText(text, false);
    }

    /**
     * Adds the simple widget.
     * 
     * @param text the text
     */
    public void addSimpleWidget(final Composite text) {
        text.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                resetAllComplexRows();
                for (Composite simpleText: _simpleTexts) {
                    if (!text.equals(simpleText)) {
                        resetSimpleRow(simpleText);
                    }
                }
                if (text instanceof StyledText) {
                    StyledText st = ((StyledText) text);
                    startEditing(st, false);
                } else if (text instanceof CCombo) {
                    if (!text.isDisposed()) {
                        resetSimpleRow(text);
                    }
                }
            }

        });
        _simpleTexts.add(text);
    }

    /**
     * Reset all complex rows.
     */
    private void resetAllComplexRows() {
        for (DescriptionText dt: _complexTexts) {
            StyledText styledText = dt.getStyledText();
            if (!styledText.isDisposed()) {
                resetComplexRow(dt);
            }
        }
    }

    /**
     * Start editing.
     * 
     * @param styledText the styled text
     * @param resize the resize
     */
    private void startEditing(StyledText styledText, boolean resize) {
        if (!styledText.isDisposed()) {
            Composite parent = styledText.getParent();
            if (styledText.getEditable()) {
                parent.setBackground(OWLGUIUtilities.COLOR_FOR_EDITING);
                styledText.setFocus();
                if (resize) {
                    int height = styledText.getLineHeight() * 5 + 5;
                    GridData heightData = (GridData) styledText.getLayoutData();
                    if (styledText.getSize().y < height) {
                        heightData.heightHint = height;
                        styledText.setLayoutData(heightData);
                        layoutParents(styledText);
                    }
                }
            }
        }
    }

    /**
     * Adds the complex text.
     * 
     * @param text the text
     * @param complexRows the complex rows
     */
    protected void addComplexText(final DescriptionText text, final boolean complexRows) {
        text.getStyledText().addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                for (DescriptionText descText: _complexTexts) {
                    StyledText styledText = descText.getStyledText();
                    if (!styledText.isDisposed()) {
                        if (!text.equals(descText)) {
                            // close toolbar
                            resetComplexRow(descText);
                        } else {
                            // open toolbar and start editing, but first close all other toolbars
                            if (styledText.getEditable()) {
                                startEditing(styledText, true);
                                if (_useToolbar) {
                                    descText.openToolBar(styledText);
                                }
                            }
                        }
                    }
                }
                if (!complexRows) {
                    for (Composite simpleText: _simpleTexts) {
                        resetSimpleRow(simpleText);
                    }
                }
            }

        });
        _complexTexts.add(text);
    }

    /**
     * Reset complex row.
     * 
     * @param descText the desc text
     */
    private void resetComplexRow(DescriptionText descText) {
        descText.closeToolBar();
        resetSimpleRow(descText.getStyledText());
    }

    /**
     * Reset simple row.
     * 
     * @param descText the desc text
     */
    private void resetSimpleRow(Composite descText) {
        if (!descText.isDisposed()) {
            if (descText instanceof StyledText) {
                if (((StyledText) descText).getEditable()) {
                    Composite parent = descText.getParent();
                    parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                }
            } else if (descText instanceof CCombo) {
                if (((CCombo) descText).getEditable()) {
                    Composite parent = descText.getParent();
                    parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                }
            }

            GridData heightData = (GridData) descText.getLayoutData();
            heightData.heightHint = -1;
            descText.setLayoutData(heightData);
            layoutParents(descText);
        }
    }

    /**
     * Layout parents.
     * 
     * @param text the text
     */
    private void layoutParents(Composite text) {
        Composite parent = text.getParent();
        parent.layout(true);
        parent = parent.getParent();
        parent.layout(true);
    }

    /**
     * Creates the edit button.
     * 
     * @param parent the parent
     * @param enabled the enabled
     * 
     * @return the button
     */
    protected Button createEditButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createEditButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    /**
     * Creates the add button.
     * 
     * @param parent the parent
     * @param enabled the enabled
     * 
     * @return the button
     */
    protected Button createAddButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createAddButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    /**
     * Creates the remove button.
     * 
     * @param parent the parent
     * @param enabled the enabled
     * 
     * @return the button
     */
    protected Button createRemoveButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createRemoveButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    /**
     * Edits the pressed.
     * 
     * @param editButton the edit button
     * @param removeButton the remove button
     * @param text the text
     */
    protected void editPressed(Button editButton, Button removeButton, StyledText text) {
        editButton.setText(OWLGUIUtilities.BUTTON_LABEL_SAVE);
        removeButton.setText(OWLGUIUtilities.BUTTON_LABEL_CANCEL);
        text.getParent().setBackground(OWLGUIUtilities.COLOR_FOR_EDITING);
        OWLGUIUtilities.enable(text, true);
        text.setFocus();
        disableOtherButtons(new Button[] {editButton, removeButton}, _buttonsToDisable);
    }

    /**
     * Disables the buttons of all other rows.
     * 
     * @param exceptions the exceptions
     * @param buttonsToDisable the buttons to disable
     */
    protected void disableOtherButtons(Button[] exceptions, List<Button> buttonsToDisable) {
        for (Button b1: buttonsToDisable) {
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

    /**
     * Enable widgets.
     * 
     * @param widgets the widgets
     */
    protected void enableWidgets(final List<Composite> widgets) {
        for (Iterator<Composite> iterator = widgets.iterator(); iterator.hasNext();) {
            Composite composite = (Composite) iterator.next();
            OWLGUIUtilities.enable(composite, true);
        }
    }

    /**
     * Creates the save button.
     * 
     * @param parent the parent
     * @param enabled the enabled
     * 
     * @return the button
     */
    protected Button createSaveButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createSaveButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    /**
     * Gets the sections.
     * 
     * @return the sections
     */
    protected abstract List<Section> getSections();

    public abstract Composite createContents(Composite parent);

    @Override
    public void deSelectTab() {
        closeAllToolbars();
    }

    @Override
    public void dispose() {
        // empty implementation. may be overwritten by subclasses.
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void refresh() {
        _buttonsToDisable = new ArrayList<Button>();
        _useToolbar = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.USE_TOOLBAR);
        _manager = OWLPlugin.getDefault().getSyntaxManager();
        IWorkbenchPage page = OWLPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            _partListener = new LocalPartListener();
            page.removePartListener(_partListener);
            page.addPartListener(_partListener);
        }
        updateOwlModel();
        closeAllToolbars();
    }

    @Override
    public void update() {
        updateOwlModel();
    }

    /**
     * Assumes that a Set<ItemHits<? extends Predicate, ? extends Predicate>> is passed, converts it to a List and sorts it alphabetically.
     * 
     * @param itemHits the item hits
     * 
     * @return the list< item hits>
     */
    @SuppressWarnings("unchecked")
    protected List<ItemHits> sort(Object itemHits) {
        Set<ItemHits> x = (Set<ItemHits>) itemHits;
        ArrayList itemHitsList = new ArrayList<ItemHits<? extends OWLObject,? extends OWLObject>>();
        for (ItemHits<? extends OWLObject,? extends OWLObject> hit: x) {
            itemHitsList.add(hit);
        }
        AlphabeticalItemHitsComparer<ItemHits<? extends OWLObject,? extends OWLObject>> comparer = new AlphabeticalItemHitsComparer<ItemHits<? extends OWLObject,? extends OWLObject>>(_owlModel);
        Collections.sort(itemHitsList, comparer);
        return (List<ItemHits>) itemHitsList;
    }

    @Override
    protected void switchPerspective() {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID, NeOnUIPlugin.getDefault(), NeOnUIPlugin.ASK_FOR_PRESPECTIVE_SWITCH);
    }

    /**
     * The listener interface for receiving localPart events. The class that is interested in processing a localPart event implements this interface, and the
     * object created with that class is registered with a component using the component's <code>addLocalPartListener<code> method. When
     * the localPart event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see LocalPartEvent
     */
    class LocalPartListener implements IPartListener, IWorkbenchListener {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partActivated(IWorkbenchPart part) {
            closeAllToolbars();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
         */
        public void partBroughtToTop(IWorkbenchPart part) {
            closeAllToolbars();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
         */
        public void partClosed(IWorkbenchPart part) {
            closeAllToolbars();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partDeactivated(IWorkbenchPart part) {
            if (part instanceof AbstractOWLMainIDPropertyPage) {
                closeAllToolbars();
            }
        }

        /**
         * Part closed.
         * 
         * @param partRef the part ref
         */
        public void partClosed(IWorkbenchPartReference partRef) {
            if (partRef instanceof AbstractOWLMainIDPropertyPage) {
                closeAllToolbars();
            }
        }

        /**
         * Part deactivated.
         * 
         * @param partRef the part ref
         */
        public void partDeactivated(IWorkbenchPartReference partRef) {
            closeAllToolbars();
        }

        /**
         * Part hidden.
         * 
         * @param partRef the part ref
         */
        public void partHidden(IWorkbenchPartReference partRef) {
            closeAllToolbars();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IWorkbenchListener#postShutdown(org.eclipse.ui.IWorkbench)
         */
        public void postShutdown(IWorkbench workbench) {
            closeAllToolbars();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IWorkbenchListener#preShutdown(org.eclipse.ui.IWorkbench, boolean)
         */
        public boolean preShutdown(IWorkbench workbench, boolean forced) {
            closeAllToolbars();
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
         */
        public void partOpened(IWorkbenchPart part) {
            closeAllToolbars();
        }

    }
}
