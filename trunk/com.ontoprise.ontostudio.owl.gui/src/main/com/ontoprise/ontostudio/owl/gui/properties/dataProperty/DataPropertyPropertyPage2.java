/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.dataProperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLObject;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DatatypeText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DescriptionText;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.GetPropertyAttribute;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.SetPropertyAttribute;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataPropertyDomain;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataPropertyRange;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetDataPropertyDomains;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetDataPropertyRanges;


public class DataPropertyPropertyPage2 extends AbstractOWLMainIDPropertyPage {

    private static final int DOMAIN = 1;
    private static final int RANGE = 2;

    private static final int NUM_COLS = 3;

    /*
     * JFace Forms variables
     */
    private Section _domainSection;
    private Section _rangeSection;
    private Section _functionalSection;

    private Composite _domainFormComposite;
    private Composite _rangeFormComposite;

    private Group _importedGroup;
    private Button _functionalCheckBox;
    private Button _importedFunctionalCheckBox;

    public DataPropertyPropertyPage2() {
        super();
    }

    @Override
    protected String getTitle() {
        return Messages.DataPropertyPropertyPage_DataProperties;
    }

    @Override
    protected void createMainArea(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_DATA_PROPERTIES_VIEW);
        super.createMainArea(composite);
        Composite body = prepareForm(composite);

        createDomainArea(body);
        createRangeArea(body);
        createCheckboxArea(body);

        layoutSections();
        _form.reflow(true);
    }

    /**
     * Create domain area
     */
    private void createDomainArea(Composite composite) {
        _domainSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _domainSection.setText(Messages.DataPropertyPropertyPage_Domain);
        _domainSection.setDescription(Messages.DataPropertyPropertyPage_IntersectionOfDomain);
        _domainSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
                // closeToolBar();
            }
        });

        _domainFormComposite = _toolkit.createComposite(_domainSection, SWT.NONE);
        _domainFormComposite.setLayout(new GridLayout());
        _domainFormComposite.setLayoutData(new ColumnLayoutData());

        _toolkit.adapt(_domainFormComposite);
        _domainSection.setClient(_domainFormComposite);
    }

    private void initDomainSection(boolean setFocus) {
        cleanup();
        clearComposite(_domainFormComposite);
        try {
            String[][] domains = new GetDataPropertyDomains(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(domains);
            for (String[] domain: sortedSet) {
                String axiomText = domain[0];
                String ontologyUri = domain[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);
                OWLDataPropertyDomainAxiom objectPropertyDomain = (OWLDataPropertyDomainAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);

                createRow(new LocatedAxiom(objectPropertyDomain, isLocal), ontologyUri, false, DOMAIN);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        }

        Label createNewLabel = new Label(_domainFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.DataPropertyPropertyPage2_0);
        Composite activeComposite = createEmptyRow(true, DOMAIN);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create range area
     */
    private void createRangeArea(Composite composite) {
        _rangeSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _rangeSection.setText(Messages.DataPropertyPropertyPage_Range);
        _rangeSection.setDescription(Messages.DataPropertyPropertyPage_IntersectionOfRange);
        _rangeSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _rangeFormComposite = _toolkit.createComposite(_rangeSection, SWT.NONE);
        _rangeFormComposite.setLayout(new GridLayout());
        ColumnLayoutData data = new ColumnLayoutData();
        _rangeFormComposite.setLayoutData(data);

        _toolkit.adapt(_rangeFormComposite);
        _rangeSection.setClient(_rangeFormComposite);
    }

    private void initRangeSection(boolean setFocus) {
        cleanup();
        clearComposite(_rangeFormComposite);

        try {
            String[][] ranges = new GetDataPropertyRanges(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(ranges);
            for (String[] domain: sortedSet) {
                String axiomText = domain[0];
                String ontologyUri = domain[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);
                OWLDataPropertyRangeAxiom dataPropertyRange = (OWLDataPropertyRangeAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);

                createRow(new LocatedAxiom(dataPropertyRange, isLocal), ontologyUri, true, RANGE);
            }
        } catch (NeOnCoreException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        }
        Label createNewLabel = new Label(_rangeFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.DataPropertyPropertyPage2_1);
        Composite activeComposite = createEmptyRow(true, RANGE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create check box area
     */
    private void createCheckboxArea(Composite composite) {
        _functionalSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _functionalSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _functionalSection.setText(Messages.DataPropertyPropertyPage_Functional);
        _functionalSection.setDescription(Messages.DataPropertyPropertyPage_FunctionalDescription);

        Composite comp = _toolkit.createComposite(_functionalSection, SWT.NONE);
        comp.setLayout(new GridLayout(2, true));
        comp.setLayoutData(new ColumnLayoutData());

        _toolkit.adapt(comp);
        _functionalSection.setClient(comp);

        Group localGroup = new Group(comp, SWT.NONE);
        localGroup.setText(Messages.DataPropertyPropertyPage2_22);
        RowLayout layout = new RowLayout(SWT.VERTICAL);
        localGroup.setLayout(layout);

        _functionalCheckBox = new Button(localGroup, SWT.CHECK);
        _functionalCheckBox.setText(Messages.DataPropertyPropertyPage_CheckBoxes_Functional);

        _functionalCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button button = (Button) e.getSource();
                try {
                    new SetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.DATA_PROP, OWLCommandUtils.FUNCTIONAL, button.getSelection()).run();
                } catch (NeOnCoreException e1) {
                    handleException(e1, Messages.DataPropertyPropertyPage2_14, button.getShell());
                } catch (CommandException e1) {
                    handleException(e1, Messages.DataPropertyPropertyPage2_14, button.getShell());
                }
                initCheckboxSection();
                layoutSections();
                _form.reflow(true);
            }
        });

        _importedGroup = new Group(comp, SWT.NONE);
        layout = new RowLayout(SWT.VERTICAL);
        _importedGroup.setLayout(layout);
        _importedGroup.setText(Messages.DataPropertyPropertyPage2_23);

        _importedFunctionalCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedFunctionalCheckBox.setText(Messages.DataPropertyPropertyPage_CheckBoxes_Functional);
        _importedFunctionalCheckBox.setEnabled(false);
    }

    @Override
    public void refreshComponents() {
        super.refreshComponents();

        initDomainSection(false);
        initRangeSection(false);
        initCheckboxSection();

        // closeToolBar();
        layoutSections();
        _form.reflow(true);
    }

    private void initCheckboxSection() {
        try {
            _functionalCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.FUNCTIONAL, false).getAttributeValue());

            if (_showImported) {
                _importedGroup.setVisible(true);
                _importedFunctionalCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.FUNCTIONAL, true).getAttributeValue());
            } else {
                _importedGroup.setVisible(false);
            }
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        }
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_domainSection);
        sections.add(_rangeSection);
        sections.add(_functionalSection);
        return sections;
    }

    private void createRow(LocatedAxiom locatedAxiom, String ontologyUri, boolean enabled, final int mode) throws NeOnCoreException {
        Composite parent;
        if (mode == DOMAIN) {
            parent = _domainFormComposite;
        } else {
            parent = _rangeFormComposite;
        }
        boolean imported = !locatedAxiom.isLocal();
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri);
        OWLAxiom axiom = locatedAxiom.getAxiom();
        OWLObject desc = mode == DOMAIN ? ((OWLDataPropertyDomainAxiom) axiom).getDomain() : ((OWLDataPropertyRangeAxiom) axiom).getRange();

        final StyledText text;
        if (mode == DOMAIN) {
            DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, _toolkit);
            text = descriptionText.getStyledText();
            text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, descriptionText);
            addComplexText(descriptionText);
        } else {
            DatatypeText datatypeText = new DatatypeText(row.getParent(), _owlModel); 
            text = datatypeText.getStyledText();
            text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, datatypeText);
        }

        final String[] array = getArrayFromDescription(desc);
        text.setText(OWLGUIUtilities.getEntityLabel(array));
        text.setData(desc);
        OWLGUIUtilities.enable(text, false);
        row.addWidget(text);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, new LocatedAxiom(axiom, true)) {

            @Override
            public void savePressed() {
                // save modified entries
                String input = text.getText();
                try {
                    remove();
                   
                    if (mode == DOMAIN) {
                        String value = OWLUtilities.toString(OWLPlugin.getDefault().getSyntaxManager().parseDescription(input, _owlModel)); 
                        new CreateDataPropertyDomain(_project, _ontologyUri, _id, value).run();
                        initDomainSection(false);
                    } else {
                        String value = OWLUtilities.toString(OWLPlugin.getDefault().getSyntaxManager().parseDataRange(input, _owlModel)); 
                        new CreateDataPropertyRange(_project, _ontologyUri, _id, value).run();
                        initRangeSection(false);
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.DataPropertyPropertyPage2_14, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException ce) {
                    handleException(ce, Messages.DataPropertyPropertyPage2_14, text.getShell());
                    text.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(text, false);
                layoutSections();
                _form.reflow(true);
            }

            @Override
            public void addPressed() {
                // nothing to do
            }

            @Override
            public void ensureQName() {
                int mode = OWLGUIUtilities.getEntityLabelMode();
                if (mode == NeOnUIPlugin.DISPLAY_URI || mode == NeOnUIPlugin.DISPLAY_QNAME) {
                    return;
                }

                if (array.length > 1) {
                    text.setText(array[1]);
                } else {
                    text.setText(array[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    private Composite createEmptyRow(final boolean enabled, final int mode) {
        Composite parent;
        if (mode == DOMAIN) {
            parent = _domainFormComposite;
        } else {
            parent = _rangeFormComposite;
        }
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);
        final StyledText text;
        if (mode == DOMAIN) {
            DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, _toolkit);
            text = descriptionText.getStyledText();
            addComplexText(descriptionText);
        } else {
            text = new DatatypeText(row.getParent(), _owlModel).getStyledText();
            addSimpleWidget(text);
        }
        row.addWidget(text);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                String input = text.getText();
                try {
                   
                    if (mode == DOMAIN) {
                        String value = OWLUtilities.toString(OWLPlugin.getDefault().getSyntaxManager().parseDescription(input, _owlModel));
                        new CreateDataPropertyDomain(_project, _ontologyUri, _id, value).run();
                    } else {
                        String value = OWLUtilities.toString(OWLPlugin.getDefault().getSyntaxManager().parseDataRange(input, _owlModel));
                        new CreateDataPropertyRange(_project, _ontologyUri, _id, value).run();
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.DataPropertyPropertyPage2_14, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException ce) {
                    handleException(ce, Messages.DataPropertyPropertyPage2_14, text.getShell());
                    text.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(text, false);

                if (mode == DOMAIN) {
                    initDomainSection(true);
                } else {
                    initRangeSection(true);
                }
                layoutSections();
                _form.reflow(true);
            }

            @Override
            public void ensureQName() {
                // nothing to do
            }

        };
        row.init(rowHandler);

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (text.getText().trim().length() == 0) {
                    row.getAddButton().setEnabled(false);
                } else {
                    row.getAddButton().setEnabled(true);
                }
            }
        });
        return text;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();

        initDomainSection(false);
        initRangeSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
    }

    @Override
    public void dispose() {
        IWorkbenchPage page = OWLPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (_partListener != null && page != null) {
            page.removePartListener(_partListener);
        }
    }

    private TreeSet<String[]> getSortedSet(String[][] clazzesArray) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0], _namespaces, _factory);
                    String uri1 = ""; //$NON-NLS-1$
                    String uri2 = ""; //$NON-NLS-1$
                    if (axiom1 instanceof OWLDataPropertyDomainAxiom) {
                        OWLDataPropertyDomainAxiom domain = (OWLDataPropertyDomainAxiom) axiom1;
                        OWLClassExpression desc1 = domain.getDomain();
                        uri1 = OWLGUIUtilities.getUriForSorting(desc1, _owlModel);

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                        if (axiom2 instanceof OWLDataPropertyDomainAxiom) {
                            OWLDataPropertyDomainAxiom domain2 = (OWLDataPropertyDomainAxiom) axiom2;
                            OWLClassExpression desc2 = domain2.getDomain();
                            uri2 = OWLGUIUtilities.getUriForSorting(desc2, _owlModel);
                        }
                    } else if (axiom1 instanceof OWLDataPropertyRangeAxiom) {
                        OWLDataPropertyRangeAxiom range = (OWLDataPropertyRangeAxiom) axiom1;
                        OWLDataRange dr = range.getRange();
                        uri1 = OWLGUIUtilities.getEntityLabel(dr, ontologyUri1, _project);

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                        if (axiom2 instanceof OWLDataPropertyRangeAxiom) {
                            OWLDataPropertyRangeAxiom range2 = (OWLDataPropertyRangeAxiom) axiom2;
                            OWLDataRange dr2 = range2.getRange();
                            uri2 = OWLGUIUtilities.getEntityLabel(dr2, ontologyUri1, _project);
                        }
                    }

                        
                    int localResult = uri1.compareToIgnoreCase(uri2);
                    // make sure multiple hits with the same propertyUri are added multiple times
                    if (localResult == 0) {
                        int x = ontologyUri1.compareToIgnoreCase(ontologyUri2);
                        if (x == 0) {
                            // make sure multiple hits with the same propertyUri are added multiple times
                            return 1;
                        }
                        return x;
                    }
                    return localResult;
                } catch (NeOnCoreException e) {
                    new NeonToolkitExceptionHandler().handleException(e);
                }
                return 0;
            }
        });
        for (String[] hit: clazzesArray) {
            unsortedSet.add(hit);
        }
        sortedSet.addAll(unsortedSet);
        return sortedSet;
    }
}
