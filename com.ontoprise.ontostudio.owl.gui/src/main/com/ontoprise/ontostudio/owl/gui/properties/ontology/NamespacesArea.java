/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.util.URIUtils;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.dialogs.DefaultNamespaceListener;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.textfields.ShortStringText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.UriText;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.ExistsPrefix;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetDefaultNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetNamespaceForPrefix;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetPrefixForNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.GetRegisteredPrefixes;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.RemoveNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.SetDefaultNamespace;
import com.ontoprise.ontostudio.owl.model.commands.namespaces.SetNamespacePrefix;
/**
 * 
 * @author Nico Stieler
 */
public class NamespacesArea extends AbstractOWLIdPropertyPage {

    private Section _namespacesSection;
    private Composite _namespacesComp;
    private FormToolkit _toolkit;
    private ScrolledForm _form;

    private Group _defaultNsGroup;
    private List<Button> _defaultNsButtons;

    private Object _tempValue;
    private Set<DefaultNamespaceListener> _defaultNamespaceListeners;
    private boolean _drawSection;
    private String _title;

    private String _defaultNamespace;
    private String _defaultPrefix;

    public NamespacesArea(Composite composite, FormToolkit toolkit, final ScrolledForm form, String ontologyURI, String projectID, String title, boolean drawSection) {
        _defaultNamespaceListeners = new HashSet<DefaultNamespaceListener>();
        _ontologyUri = ontologyURI;
        _project = projectID;
        _toolkit = toolkit;
        _form = form;
        _drawSection = drawSection;
        _title = title;
        createContents(composite);
    }

    public NamespacesArea(Composite composite, FormToolkit toolkit, final ScrolledForm form, String ontologyURI, String projectID, String title) {
        this(composite, toolkit, form, ontologyURI, projectID, title, true);
    }

    @Override
    public Composite createContents(Composite composite) {
        if (_drawSection) {
            _namespacesSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.COMPACT | Section.EXPANDED);
            _namespacesSection.setText(_title);
            _namespacesSection.addExpansionListener(new ExpansionAdapter() {
                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    _form.reflow(true);
                }
            });
            _namespacesComp = _toolkit.createComposite(_namespacesSection, SWT.NONE);
        } else {
            _namespacesComp = _toolkit.createComposite(composite);
        }
        GridLayout gridLayout = new GridLayout();
        _namespacesComp.setLayout(gridLayout);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        _namespacesComp.setLayoutData(data);

        _toolkit.adapt(_namespacesComp);
        if (_drawSection) {
            _namespacesSection.setClient(_namespacesComp);
        }

        _defaultNsGroup = new Group(_namespacesComp, SWT.NONE);
        _defaultNsButtons = new ArrayList<Button>();
        return _namespacesComp;
    }

    public void initNamespacesSection(String passedPrefix) {
        if (_ontologyUri == null || _project == null) {
            return;
        }

        clearComposite(_namespacesComp);
        _buttonsToDisable = new ArrayList<Button>();
        Map<String,String> _namespacePrefixMap = new HashMap<String,String>();
        try {
            boolean defaultAvailable = false;
            _defaultNamespace = new GetDefaultNamespace(_project, _ontologyUri).getResult();
            String[] definedPrefixes = new GetRegisteredPrefixes(_project, _ontologyUri).getResults();

            for (String prefix: definedPrefixes) {
                String namespace = new GetNamespaceForPrefix(_project, _ontologyUri, prefix).getResult();
                if (namespace.equals(_defaultNamespace)) {
                    defaultAvailable = true;
                    _defaultPrefix = prefix;
                }
                String oldPrefix = _namespacePrefixMap.get(namespace);
                if (oldPrefix == null || oldPrefix != null && oldPrefix.equals("")) { //$NON-NLS-1$
                    _namespacePrefixMap.put(namespace, prefix);
                }
            }

            for (String namespace: _namespacePrefixMap.keySet()) {
                ArrayList<String[]> contents = new ArrayList<String[]>();
                String prefix = _namespacePrefixMap.get(namespace);
                contents.add(new String[] {prefix});
                contents.add(new String[] {namespace});
                createNamespacesRow(_namespacesComp, contents, false, ""); //$NON-NLS-1$
            }
            if (!defaultAvailable && _defaultNamespace != null) {
                // The default NS doesn't seem to have a prefix. (happens e.g. when
                // creating a new ontology. Add a row without prefix with the checked default NS.
                ArrayList<String[]> contents = new ArrayList<String[]>();
                contents.add(new String[] {""}); //$NON-NLS-1$
                contents.add(new String[] {_defaultNamespace});
                createNamespacesRow(_namespacesComp, contents, false, ""); //$NON-NLS-1$
                _defaultPrefix = null;
            }
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        Label createNewLabel = new Label(_namespacesComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.NamespacesArea_1);
        createNamespacesRow(_namespacesComp, null, true, passedPrefix);
        try {
            String defaultNamespace = new GetDefaultNamespace(_project, _ontologyUri).getResult();
            for (Button button: _defaultNsButtons) {
                if (!button.isDisposed()) {
                    Object data = button.getData();
                    if (data != null && ((String) button.getData()).equals(defaultNamespace)) {
                        button.setSelection(true);
                        for (Iterator<DefaultNamespaceListener> iterator = _defaultNamespaceListeners.iterator(); iterator.hasNext();) {
                            DefaultNamespaceListener listener = (DefaultNamespaceListener) iterator.next();
                            listener.defaultNamespaceChanged(defaultNamespace);
                        }
                    } else {
                        button.setSelection(false);
                    }
                }
            }

        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
    }

    private void createNamespacesRow(Composite parent, ArrayList<String[]> descriptions, boolean enabled, final String passedPrefix) {
        Composite rowComp = _toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(5, false);
        layout.marginHeight = 0;
        rowComp.setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        rowComp.setLayoutData(layoutData);

        final List<Composite> widgets = new ArrayList<Composite>();

        // default namespace checkbox
        final Button defaultNsButton = OWLGUIUtilities.createCheckBox(rowComp, true);

        defaultNsButton.setToolTipText(Messages.NamespacesArea_2);
        _defaultNsButtons.add(defaultNsButton);

        if (descriptions != null && descriptions.size() > 0) {
            defaultNsButton.setData(descriptions.get(1)[0]);
        }

        // prefix text
        final StyledText prefixText = new ShortStringText(rowComp, _owlModel, _owlModel).getStyledText();//NICO are you sure?

        if (passedPrefix != null) {
            prefixText.setText(passedPrefix);
        }
        prefixText.setToolTipText(Messages.NamespacesArea_4);
        widgets.add(prefixText);

        if (descriptions != null) {
            defaultNsButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    for (Button button: _defaultNsButtons) {
                        if (!button.isDisposed()) {
                            if (button.equals(defaultNsButton)) {
                                // only apply changes if row is not in edit mode, otherwise wait
                                // with applying changes until save is pressed (not immediately when
                                // checkbox is selected/deselected)
                                if (!prefixText.getEditable()) {
                                    String newDefaultNs = (String) button.getData();
                                    try {
                                        ensureLastDefaultNamespaceGetsPrefix();
                                        if (button.getSelection() == false) {
                                            setDefaultNamespace(null);
                                        } else {
                                            setDefaultNamespace(newDefaultNs);
                                        }
                                    } catch (NeOnCoreException e1) {
                                        new NeonToolkitExceptionHandler().handleException(e1);
                                    } catch (CommandException e2) {
                                        new NeonToolkitExceptionHandler().handleException(e2);
                                    }
                                }
                            } else {
                                button.setSelection(false);
                            }
                        }
                    }
                    if (!prefixText.getEditable()) {
                        refresh();
                    }
                }

            });
        }

        // namespace text
        final StyledText namespaceText = new UriText(rowComp, _owlModel,_owlModel).getStyledText();//NICO are you sure?
        namespaceText.setToolTipText(Messages.NamespacesArea_3);
        widgets.add(namespaceText);

        if (descriptions != null && descriptions.size() > 0) {
            String[] array = descriptions.get(1);
            String id = array[0];
            if (id != null && !id.equals("")) { //$NON-NLS-1$
                namespaceText.setText(id);
                namespaceText.setData(array);
            }
            OWLGUIUtilities.enable(namespaceText, false);

            array = descriptions.get(0);
            id = array[0];
            prefixText.setText(id);
            prefixText.setData(array);
            OWLGUIUtilities.enable(prefixText, false);
        }

        // buttons to edit/add a row
        if (descriptions != null) {
            final Button editButton = createEditButton(rowComp, true);
            if (!_buttonsToDisable.contains(editButton)) {
                _buttonsToDisable.add(editButton);
            }

            final Button removeButton = createRemoveButton(rowComp, true);
            if (!_buttonsToDisable.contains(removeButton)) {
                _buttonsToDisable.add(removeButton);
            }

            editButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    _form.setMessage(null, IMessageProvider.NONE);
                    if (editButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_EDIT)) {
                        // enable widgets, so the user can change them
                        String prefix = prefixText.getText();
                        String ns = namespaceText.getText();
                        _tempValue = new String[] {prefix, ns};
                        editPressed(editButton, removeButton, prefixText);
                        enableWidgets(widgets);
                        // also disable other default NS checkboxes
                        setCheckboxesEnabled(defaultNsButton, false);
                    } else {
                        // Save: edit existing entry
                        String[] oldValues = (String[]) _tempValue;
                        try {
                            String prefix = prefixText.getText();
                            String oldPrefix = oldValues[0];
                            String ns = namespaceText.getText();
                            ensureLastDefaultNamespaceGetsPrefix();
                            if (new ExistsPrefix(_project, _ontologyUri, oldPrefix).getExists()) {
                                executeChange(new RemoveNamespace(_project, _ontologyUri, oldPrefix));
                            }
                            executeChange(new SetNamespacePrefix(_project, _ontologyUri, prefix, ns));
                            if (defaultNsButton.getSelection()) {
                                setDefaultNamespace(ns);
                            } else if (_defaultNamespace!=null && _defaultNamespace.equals(ns)) {
                                setDefaultNamespace(null);
                            }
                        } catch (NeOnCoreException ce) {
                            new NeonToolkitExceptionHandler().handleException(Messages.NamespacesArea_18, ce, removeButton.getShell());
                        } catch (CommandException e2) {
                            new NeonToolkitExceptionHandler().handleException(Messages.NamespacesArea_18, e2, removeButton.getShell());
                        }
                        OWLGUIUtilities.enable(prefixText, false);
                        // also disable other default NS checkboxes
                        setCheckboxesEnabled(defaultNsButton, true);
                        refresh();
                    }
                }

            });

            removeButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (removeButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_CANCEL)) {
                        String[] oldValues = (String[]) _tempValue;
                        String oldPrefix = oldValues[0];
                        String oldNs = oldValues[1];
                        prefixText.setText(oldPrefix);
                        namespaceText.setText(oldNs);
                        OWLGUIUtilities.enable(prefixText, false);
                        for (Iterator<Composite> iterator = widgets.iterator(); iterator.hasNext();) {
                            Composite composite = (Composite) iterator.next();
                            OWLGUIUtilities.enable(composite, false);
                        }
                        removeButton.setText(OWLGUIUtilities.BUTTON_LABEL_REMOVE);
                        editButton.setText(OWLGUIUtilities.BUTTON_LABEL_EDIT);
                        _form.setMessage(null, IMessageProvider.NONE);
                        refresh();

                    } else {
                        // remove entry
                        String oldPrefix = prefixText.getText();
                        try {
                            boolean contained = false;
                            String[] registeredPrefixes = new GetRegisteredPrefixes(_project, _ontologyUri).getResults();
                            for (String prefix: registeredPrefixes) {
                                if (prefix.equals(oldPrefix)) {
                                    contained = true;
                                    break;
                                }
                            }
                            if (contained) {
                                String def = new GetDefaultNamespace(_project, _ontologyUri).getResult();
                                if (def != null) {
                                    String prfx = new GetPrefixForNamespace(_project, _ontologyUri, def).getResult();
                                    if (prfx != null && oldPrefix.equals(prfx)) {
                                        // if the removed ns was set as default, also "remove" the default ns.
                                        executeChange(new SetDefaultNamespace(_project, _ontologyUri, null));
                                    }
                                }
                                executeChange(new RemoveNamespace(_project, _ontologyUri, oldPrefix));
                            } else {
                                executeChange(new SetDefaultNamespace(_project, _ontologyUri, null));
                            }
                        } catch (NeOnCoreException ce) {
                            new NeonToolkitExceptionHandler().handleException(Messages.NamespacesArea_19, ce, removeButton.getShell());
                        } catch (CommandException e2) {
                            new NeonToolkitExceptionHandler().handleException(Messages.NamespacesArea_19, e2, removeButton.getShell());
                        }
                        OWLGUIUtilities.enable(prefixText, false);
                        refresh();
                    }
                }
            });
            initVerifyListeners(defaultNsButton, prefixText, namespaceText, editButton);

        } else {
            final Button addButton = OWLGUIUtilities.createAdd2Button(rowComp, true);
            addButton.setEnabled(false);
            if (!_buttonsToDisable.contains(addButton)) {
                _buttonsToDisable.add(addButton);
            }

            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    // add new entry
                    try {
                        String prefix = prefixText.getText();
                        String ns = namespaceText.getText();
                        ensureLastDefaultNamespaceGetsPrefix();
                        executeChange(new SetNamespacePrefix(_project, _ontologyUri, prefix, ns));
                        if (defaultNsButton.getSelection()) {
                            executeChange(new SetDefaultNamespace(_project, _ontologyUri, ns));
                        }
                    } catch (NeOnCoreException ce) {
                        new NeonToolkitExceptionHandler().handleException(Messages.NamespacesArea_20, ce, prefixText.getShell());
                    } catch (CommandException e2) {
                        new NeonToolkitExceptionHandler().handleException(Messages.NamespacesArea_20, e2, prefixText.getShell());
                    }
                    refresh();
                }
            });

            initVerifyListeners(defaultNsButton, prefixText, namespaceText, addButton);
        }
    }

    private void ensureLastDefaultNamespaceGetsPrefix() throws CommandException, NeOnCoreException {
        if (_defaultPrefix == null) {
            // if a default namespace was defined without prefix, set a prefix for that, else it would be lost
            String newPrefix = nextUniqueAlias();
            new SetNamespacePrefix(_project, _ontologyUri, newPrefix, _defaultNamespace).run();
        }
    }

    private void setDefaultNamespace(String ns) throws NeOnCoreException, CommandException {
        executeChange(new SetDefaultNamespace(_project, _ontologyUri, ns));

        for (Iterator<DefaultNamespaceListener> iterator = _defaultNamespaceListeners.iterator(); iterator.hasNext();) {
            DefaultNamespaceListener listener = (DefaultNamespaceListener) iterator.next();
            listener.defaultNamespaceChanged(ns);
        }
    }

    private void executeChange(OWLModuleChangeCommand command) throws CommandException {
        command.run();
        OWLPlugin.getDefault().getPreferenceStore().setValue(OWLPlugin.NAMESPACE_PREFERENCE, System.currentTimeMillis());
    }

    private void setCheckboxesEnabled(final Button defaultNsButton, boolean enabled) {
        for (Button button: _defaultNsButtons) {
            if (!button.isDisposed()) {
                if (!button.equals(defaultNsButton)) {
                    button.setEnabled(enabled);
                }
            }
        }
    }

    private void initVerifyListeners(final Button defaultNsButton, final StyledText prefixText, final StyledText namespaceText, final Button addButton) {
        namespaceText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                verifyEntries(defaultNsButton, prefixText, namespaceText, addButton);
            }
        });

        defaultNsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                verifyEntries(defaultNsButton, prefixText, namespaceText, addButton);
            }
        });

        prefixText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                verifyEntries(defaultNsButton, prefixText, namespaceText, addButton);
            }
        });
    }

    private void verifyEntries(final Button defaultNsButton, final StyledText prefixText, final StyledText namespaceText, final Button addButton) {
        if (defaultNsButton.isDisposed()) {
            return;
        }
        if (defaultNsButton.getSelection()) {
            if (namespaceText.getText().trim().length() == 0) {
                _form.setMessage(Messages.NamespacesArea_22, IMessageProvider.WARNING);
                addButton.setEnabled(false);
            } else {
                verifyUri(namespaceText, addButton);
            }
        } else {
            if (prefixText.getText().trim().length() == 0 && namespaceText.getText().trim().length() != 0) {
                _form.setMessage(Messages.NamespacesArea_16, IMessageProvider.WARNING);
                addButton.setEnabled(false);
            } else {
                if (namespaceText.getText().trim().length() == 0) {
                    _form.setMessage(Messages.NamespacesArea_22, IMessageProvider.WARNING);
                    addButton.setEnabled(false);
                } else {
                    verifyUri(namespaceText, addButton);
                }
            }
        }
    }

    private void verifyUri(final StyledText namespaceText, final Button addButton) {
        String result = URIUtils.validateNamespace(namespaceText.getText(), URIUtils.getUriValidator(Messages.NamespacesArea_21));
        if (result != null) {
            _form.setMessage(result, IMessageProvider.WARNING);
            addButton.setEnabled(false);
        } else {
            _form.setMessage(null, IMessageProvider.NONE);
            addButton.setEnabled(true);
        }
    }

    public void registerDefaultNamespaceListener(DefaultNamespaceListener listener) {
        _defaultNamespaceListeners.add(listener);
    }

    public void removeDefaultNamespaceListener(DefaultNamespaceListener listener) {
        _defaultNamespaceListeners.remove(listener);
    }

    @Override
    public void refresh() {
        super.refresh();
        initNamespacesSection(""); //$NON-NLS-1$
        if (_namespacesSection != null) {
            _namespacesSection.layout(true);
        }
        _form.reflow(true);
    }

    public Section getSection() {
        return _namespacesSection;
    }

    public Composite getComposite() {
        return _namespacesComp;
    }

    public Group getDefaultNsGroup() {
        return _defaultNsGroup;
    }

    public List<Button> getDefaultNsButtons() {
        return _defaultNsButtons;
    }

    public String getSelectedNamespace() {
        String result = null;
        for (Iterator<Button> iterator = _defaultNsButtons.iterator(); iterator.hasNext();) {
            Button button = (Button) iterator.next();
            if (button.getSelection()) {
                result = (String) button.getData();
            }
        }
        return result;
    }

    public void setOntologyUri(String uri) {
        _ontologyUri = uri;
    }

    public void setProjectId(String id) {
        _project = id;
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_namespacesSection);
        return sections;
    }

    private String nextUniqueAlias() throws NeOnCoreException {
        List<String> prefixes = new ArrayList<String>();
        OWLNamespaces namespaces = OWLModelFactory.getOWLModel(_ontologyUri, _project).getNamespaces();
        for (Iterator<String> aliases = namespaces.prefixes(); aliases.hasNext();) {
            prefixes.add(aliases.next());
        }

        String aliasBase = "ns"; //$NON-NLS-1$
        int counter = 1;
        while (prefixes.contains(aliasBase + counter)) {
            counter++;
        }
        return aliasBase + counter;
    }
}
