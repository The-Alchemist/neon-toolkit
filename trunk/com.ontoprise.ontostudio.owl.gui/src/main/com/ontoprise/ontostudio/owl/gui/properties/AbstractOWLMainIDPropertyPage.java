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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.properties.AbstractMainIDPropertyPage;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalItemHitsComparer;
import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
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
 * The Class BasicOWLEntityPropertyPage.
 */
public abstract class AbstractOWLMainIDPropertyPage extends AbstractMainIDPropertyPage implements IOWLPropertyPage {

    /** The Constant QUALIFIED_ID. */
    protected static final int QUALIFIED_ID = 0;

    /** The Constant LOCAL_NAME. */
    protected static final int LOCAL_NAME = 1;

    /** The _owl model. */
    protected OWLModel _owlModel;

    /** The _namespaces. */
    protected OWLNamespaces _namespaces;

    protected OWLDataFactory _factory;

    /** The _entity. */
    protected OWLObject _owlObject;

    /** stores the tree node from the Navigator for this property view. */
    ITreeElement _treeElement;

    /** The _buttons to disable. */
    protected List<Button> _buttonsToDisable;

    /** The _part listener. */
    protected IPartListener _partListener;

    /** The _listener. */
    protected IPropertyChangeListener _listener;

    /*
     * JFace Forms variables
     */
    /** The _id form. */
    protected Form _idForm;

    /** The _form. */
    protected ScrolledForm _form;

    /** The _toolkit. */
    protected FormToolkit _toolkit;

    /** The _current proposal. */
    protected IContentProposal _currentProposal;

    /** The _edit mode. */
    protected boolean _editMode = false;

    /** The _complex texts. */
    protected List<DescriptionText> _complexTexts;

    /** The _simple texts. */
    protected List<Composite> _simpleTexts;

    /** The _temp value. */
    protected Object _tempValue;

    /** The _manager. */
    protected ISyntaxManager _manager;

    /** The _use toolbar. */
    protected boolean _useToolbar = false;

    /** The _show imported. */
    protected boolean _showImported = false;

    /**
     * Instantiates a new basic owl entity property page.
     */
    public AbstractOWLMainIDPropertyPage() {
        super();
        _complexTexts = new ArrayList<DescriptionText>();
        _simpleTexts = new ArrayList<Composite>();
        _buttonsToDisable = new ArrayList<Button>();
    }

    /**
     * Adds the complex text.
     * 
     * @param text the text
     */
    protected void addComplexText(final DescriptionText text) {
        addComplexText(text, false);
    }
    
//    @Override
//    public Composite createGlobalContents(Composite parent) {
//        parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
//        return super.createGlobalContents(parent);
//    }

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
     * Start editing.
     * 
     * @param styledText the styled text
     * @param resize the resize
     */
    private void startEditing(StyledText styledText, boolean resize) {
        if (!styledText.isDisposed()) {
            Composite parent = styledText.getParent();
            if (styledText.getEditable()) {
                parent.setBackground(new Color(null, 250, 250, 210));
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
     * Close all toolbars.
     */
    protected void cleanup() {
        closeAllToolbars();
        NeOnUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(_listener);
    }

    private void closeAllToolbars() {
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
     * Gets the sections.
     * 
     * @return the sections
     */
    protected abstract List<Section> getSections();

    /**
     * Gets the form.
     * 
     * @return the form
     */
    public ScrolledForm getForm() {
        return _form;
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
     * Cancel.
     * 
     * @param text the text
     * @param editButton the edit button
     * @param removeButton the remove button
     */
    protected void cancel(final StyledText text, final Button editButton, final Button removeButton) {
        OWLObject desc = (OWLObject) text.getData();
        String[] array = getArrayFromDescription(desc);
        String oldValue = OWLGUIUtilities.getEntityLabel(array);
        text.setText(oldValue);
        OWLGUIUtilities.enable(text, false);
        removeButton.setText(Messages.DataPropertyPropertyPage2_12);
        editButton.setText(Messages.DataPropertyPropertyPage2_13);
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

    /**
     * Layout sections.
     */
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
     * Edits the pressed.
     * 
     * @param editButton the edit button
     * @param removeButton the remove button
     * @param text the text
     */
    protected void editPressed(Button editButton, Button removeButton, StyledText text) {
        editButton.setText(OWLGUIUtilities.BUTTON_LABEL_SAVE);
        removeButton.setText(OWLGUIUtilities.BUTTON_LABEL_CANCEL);
        text.getParent().setBackground(new Color(null, 250, 250, 210));
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
     * Maximize widget.
     * 
     * @param text the text
     * @param doLayout the do layout
     */
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
                layoutSections();
                _form.reflow(true);
            }
        }
    }

    /**
     * This is called when an Edit button is pressed to resize all text widgets in current row.
     * 
     * @param widgets the widgets
     */
    protected void maximizeAllWidgets(List<Composite> widgets) {
        for (Composite widget: widgets) {
            if (widget instanceof StyledText) {
                maximizeWidget(((StyledText) widget), false);
            }
        }
        layoutSections();
        _form.reflow(true);
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
     * Creates the cancel button.
     * 
     * @param parent the parent
     * @param enabled the enabled
     * 
     * @return the button
     */
    protected Button createCancelButton(Composite parent, boolean enabled) {
        Button button = OWLGUIUtilities.createCancelButton(parent, enabled);
        if (!_buttonsToDisable.contains(button)) {
            _buttonsToDisable.add(button);
        }
        return button;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.properties.BasicEntityPropertyPage#refreshComponents()
     */
    @Override
    public void refreshComponents() {
        _useToolbar = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.USE_TOOLBAR);
        _showImported = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_IMPORTED);
        _buttonsToDisable = new ArrayList<Button>();
        _editMode = false;
        _manager = OWLPlugin.getDefault().getSyntaxManager();
        cleanup();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.gui.properties.IEntityPropertyPage#getImage()
     */
    public Image getImage() {
        return null;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.properties.BasicEntityPropertyPage#createMainArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createMainArea(Composite composite) {
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        _listener = new IPropertyChangeListener() {

            // Listeners for the events that change the namespace and display
            // language settings
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE) || event.getProperty().equals(OWLPlugin.SYNTAX)) {
                    if (_id != null) {
                        updateComponents();
                    }
                }
            }
        };

        store.addPropertyChangeListener(_listener);
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

    @Override
    protected void initSelection() {
        super.initSelection();

        Object first = getSelection().getFirstElement();
        if (first instanceof AbstractOwlEntityTreeElement) {
            // the owl entities
            AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) first;
            _owlObject = element.getEntity();
            _treeElement = element;
        } else if(first instanceof IIndividualTreeElement){
            IIndividualTreeElement element = (IIndividualTreeElement)first;
            _owlObject = element.getIndividual();
            _treeElement = element;
        }
        updateOwlModel();

        IWorkbenchPage page = OWLPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            _partListener = new LocalPartListener();
            page.removePartListener(_partListener);
            page.addPartListener(_partListener);
        }
        if (_form != null) {
            _form.setMessage(null, IMessageProvider.NONE);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.properties.BasicEntityPropertyPage#updateComponents()
     */
    @Override
    public void updateComponents() {
        _manager = OWLPlugin.getDefault().getSyntaxManager();
        cleanup();
    }

    /**
     * Returns the title of this PropertyPage.
     * 
     * @return the title
     */
    protected abstract String getTitle();

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

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public Composite createContents(Composite parent) {
        _composite = parent;

        ScrolledComposite scomposite = new ScrolledComposite(parent, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
        scomposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scomposite.setExpandHorizontal(true);
        scomposite.setExpandVertical(true);

        Composite composite = new Composite(scomposite, SWT.NONE);
        scomposite.setContent(composite);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);

        createMainArea(composite);

        return scomposite;
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
            cleanup();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
         */
        public void partBroughtToTop(IWorkbenchPart part) {
            cleanup();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
         */
        public void partClosed(IWorkbenchPart part) {
            cleanup();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partDeactivated(IWorkbenchPart part) {
            if (part instanceof AbstractOWLMainIDPropertyPage) {
                cleanup();
            }
        }

        /**
         * Part closed.
         * 
         * @param partRef the part ref
         */
        public void partClosed(IWorkbenchPartReference partRef) {
            if (partRef instanceof AbstractOWLMainIDPropertyPage) {
                cleanup();
            }
        }

        /**
         * Part deactivated.
         * 
         * @param partRef the part ref
         */
        public void partDeactivated(IWorkbenchPartReference partRef) {
            cleanup();
        }

        /**
         * Part hidden.
         * 
         * @param partRef the part ref
         */
        public void partHidden(IWorkbenchPartReference partRef) {
            cleanup();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IWorkbenchListener#postShutdown(org.eclipse.ui.IWorkbench)
         */
        public void postShutdown(IWorkbench workbench) {
            cleanup();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IWorkbenchListener#preShutdown(org.eclipse.ui.IWorkbench, boolean)
         */
        public boolean preShutdown(IWorkbench workbench, boolean forced) {
            cleanup();
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
         */
        public void partOpened(IWorkbenchPart part) {
            cleanup();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.properties.BasicEntityPropertyPage#refreshIdArea()
     */
    @Override
    public void refreshIdArea() {
        try {
            ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
            OWLModel owlModel = OWLModelFactory.getOWLModel(_ontologyUri, _project);
            OWLObject entity = getOWLObject();
            if (entity != null) {
                String[] idArray = (String[]) getOWLObject().accept(manager.getVisitor(owlModel));
                _uriText.setText(idArray[0]); // always use the URI
                _localText.setText(idArray[1]);
                _qNameText.setText(idArray[2]);
            } else {
                _uriText.setText(_id);
            }
        } catch (NeOnCoreException e) {
            _uriText.setText(_id);
            _qNameText.setText(_id);
            _localText.setText(_id);
        }
        _stackLayout.topControl = _uriComposite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#isDisposed()
     */
    @Override
    public boolean isDisposed() {
        return _uriText == null || _uriText.isDisposed();
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
    protected void selectIdTopControl() {
        _stackLayout.topControl = _uriComposite;
        _stackLayout.topControl.getParent().layout();
    }

    /**
     * Gets the owl object.
     * 
     * @return the owl object
     * 
     * @throws NeOnCoreException the KAO n2 exception
     */
    public OWLObject getOWLObject() throws NeOnCoreException {
        return _owlObject;
    }

    public void setEntity(OWLObject owlObject) {
        _owlObject = owlObject;
    }

    protected void handleException(Throwable e, String message, Shell shell) {
        new NeonToolkitExceptionHandler().handleException(message, e, shell);
    }

    @Override
    public void deSelectTab() {
        cleanup();
    }

    @Override
    public void resetSelection() {
        cleanup();
    }

    @Override
    protected void switchPerspective() {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID, NeOnUIPlugin.getDefault(), NeOnUIPlugin.ASK_FOR_PRESPECTIVE_SWITCH);
    }
}
