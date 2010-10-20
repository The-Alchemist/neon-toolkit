/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.objectProperty;

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
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DescriptionText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.GetPropertyAttribute;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.SetPropertyAttribute;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectPropertyDomain;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectPropertyRange;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetObjectPropertyDomains;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetObjectPropertyRanges;

/**
 * 
 * @author Nico Stieler
 */
public class ObjectPropertyPropertyPage2 extends AbstractOWLMainIDPropertyPage {

    private static final int DOMAIN = 1;
    private static final int RANGE = 2;

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 3;

    /*
     * JFace Forms variables
     */
    private Section _domainSection;
    private Section _rangeSection;
    private Section _characteristicsSection;

    private Composite _domainFormComposite;
    private Composite _rangeFormComposite;

    /**
     * TODO disjoint object properties are possible in OWL 1.1 October 2007: KAON2 does not support them for the moment
     */

    private Button _functionalCheckBox;
    private Button _inverseFunctionalCheckBox;
    private Button _reflexiveCheckBox;
    private Button _irreflexiveCheckBox;
    private Button _symmetricCheckBox;
    private Button _asymmetricCheckBox;
    private Button _transitiveCheckBox;

    private Group _importedGroup;
    private Button _importedFunctionalCheckBox;
    private Button _importedInverseFunctionalCheckBox;
    private Button _importedReflexiveCheckBox;
    private Button _importedIrreflexiveCheckBox;
    private Button _importedSymmetricCheckBox;
    private Button _importedAsymmetricCheckBox;
    private Button _importedTransitiveCheckBox;

    public ObjectPropertyPropertyPage2() {
        super();
    }

    @Override
    protected String getTitle() {
        return Messages.ObjectPropertyPropertyPage_ObjectProperties;
    }

    @Override
    protected void createMainArea(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_OBJECT_PROPERTIES_VIEW);
        super.createMainArea(composite);
        Composite body = prepareForm(composite);

        createDomainArea(body);
        createRangeArea(body);
        createCheckBoxArea(body);

        _form.reflow(true);
    }

    /**
     * Create domain area
     */
    private void createDomainArea(Composite composite) {
        _domainSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _domainSection.setText(Messages.ObjectPropertyPropertyPage_Domain);
        _domainSection.setDescription(Messages.ObjectPropertyPropertyPage_IntersectionOfDomain);
        _domainSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
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
            String[][] domains = new GetObjectPropertyDomains(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(domains);
            for (String[] domain: sortedSet) {
                String axiomText = domain[0];
                String ontologyUri = domain[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);
                OWLObjectPropertyDomainAxiom objectPropertyDomain = (OWLObjectPropertyDomainAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);

                createRow(new LocatedAxiom(objectPropertyDomain, isLocal), ontologyUri, false, DOMAIN);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        }

        Label createNewLabel = new Label(_domainFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_3);
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
        _rangeSection.setText(Messages.ObjectPropertyPropertyPage_Range);
        _rangeSection.setDescription(Messages.ObjectPropertyPropertyPage_IntersectionOfRange);
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
            String[][] ranges = new GetObjectPropertyRanges(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(ranges);
            for (String[] range: sortedSet) {
                String axiomText = range[0];
                String ontologyUri = range[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);
                OWLObjectPropertyRangeAxiom objectPropertyRange = (OWLObjectPropertyRangeAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);

                createRow(new LocatedAxiom(objectPropertyRange, isLocal), ontologyUri, true, RANGE);
            }
        } catch (NeOnCoreException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        }
        Label createNewLabel = new Label(_rangeFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_4);
        Composite activeComposite = createEmptyRow(true, RANGE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create check box area
     */
    private void createCheckBoxArea(Composite composite) {
        _characteristicsSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _characteristicsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _characteristicsSection.setText(Messages.ObjectPropertyPropertyPage_Functional);
        _characteristicsSection.setDescription(Messages.ObjectPropertyPropertyPage_FunctionalDescription);

        Composite comp = _toolkit.createComposite(_characteristicsSection, SWT.NONE);
        comp.setLayout(new GridLayout(2, true));
        comp.setLayoutData(new ColumnLayoutData());

        _toolkit.adapt(comp);
        _characteristicsSection.setClient(comp);

        Group localGroup = new Group(comp, SWT.NONE);
        localGroup.setText(Messages.ObjectPropertyPropertyPage2_0);
        RowLayout layout = new RowLayout(SWT.VERTICAL);
        localGroup.setLayout(layout);

        createCheckboxFunctional(localGroup);
        createCheckboxInverseFunctional(localGroup);
        createCheckboxReflexive(localGroup);
        createCheckboxIrreflexive(localGroup);
        createCheckboxSymmetric(localGroup);
        createCheckboxAsymmetric(localGroup);
        createCheckboxTransitive(localGroup);

        _importedGroup = new Group(comp, SWT.NONE);
        // imported information
        _importedGroup.setText(Messages.ObjectPropertyPropertyPage2_23);
        layout = new RowLayout(SWT.VERTICAL);
        _importedGroup.setLayout(layout);

        _importedFunctionalCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedFunctionalCheckBox.setText(Messages.ObjectPropertyPropertyPage_CheckBoxes_Functional);
        _importedFunctionalCheckBox.setEnabled(false);

        _importedInverseFunctionalCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedInverseFunctionalCheckBox.setText(Messages.ObjectPropertyPropertyPage_CheckBoxes_InverseFunctional);
        _importedInverseFunctionalCheckBox.setEnabled(false);

        _importedReflexiveCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedReflexiveCheckBox.setText(Messages.ObjectPropertyPropertyPage_CheckBoxes_Reflexive);
        _importedReflexiveCheckBox.setEnabled(false);

        _importedIrreflexiveCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedIrreflexiveCheckBox.setText(Messages.ObjectPropertyPropertyPage_CheckBoxes_Irreflexive);
        _importedIrreflexiveCheckBox.setEnabled(false);

        _importedSymmetricCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedSymmetricCheckBox.setText(Messages.ObjectPropertyPropertyPage_CheckBoxes_Symmetric);
        _importedSymmetricCheckBox.setEnabled(false);

        _importedAsymmetricCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedAsymmetricCheckBox.setText(Messages.ObjectPropertyPropertyPage_CheckBoxes_Asymmetric);
        _importedAsymmetricCheckBox.setEnabled(false);

        _importedTransitiveCheckBox = new Button(_importedGroup, SWT.CHECK);
        _importedTransitiveCheckBox.setText(Messages.ObjectPropertyPropertyPage_CheckBoxes_Transitive);
        _importedTransitiveCheckBox.setEnabled(false);
    }

    /**
     * @param localGroup
     */
    private void createCheckboxAsymmetric(Group localGroup) {
        _asymmetricCheckBox =  createCheckbox(
                localGroup, 
                Messages.ObjectPropertyPropertyPage_CheckBoxes_Asymmetric,
                OWLCommandUtils.ASYMMETRIC);
    }
    /**
     * @param localGroup
     */
    private void createCheckboxSymmetric(Group localGroup) {
        _symmetricCheckBox =  createCheckbox(
                localGroup, 
                Messages.ObjectPropertyPropertyPage_CheckBoxes_Symmetric,
                OWLCommandUtils.SYMMETRIC);
    }

    /**
     * @param localGroup
     */
    private void createCheckboxTransitive(Group localGroup) {
        _transitiveCheckBox =  createCheckbox(
                localGroup, 
                Messages.ObjectPropertyPropertyPage_CheckBoxes_Transitive,
                OWLCommandUtils.TRANSITIVE);
    }

    /**
     * @param localGroup
     */
    private void createCheckboxInverseFunctional(Group localGroup) {
        _inverseFunctionalCheckBox = createCheckbox(
                localGroup, 
                Messages.ObjectPropertyPropertyPage_CheckBoxes_InverseFunctional,
                OWLCommandUtils.INVERSE_FUNCTIONAL);
    }

    /**
     * @param localGroup
     */
    private void createCheckboxFunctional(Group localGroup) {
        _functionalCheckBox = createCheckbox(
                localGroup, 
                Messages.ObjectPropertyPropertyPage_CheckBoxes_Functional,
                OWLCommandUtils.FUNCTIONAL);
    }
    
    private Button createCheckbox(Group localGroup, String label, final String characteristic) {
        Button y = new Button(localGroup, SWT.CHECK);
        y.setText(label);

        y.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button button = (Button) e.getSource();
                boolean selected = button.getSelection();
                try {
                    new SetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.OBJECT_PROP, characteristic, selected).run();
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_13, button.getShell());
                } catch (CommandException e1) {
                    handleException(e1, Messages.ObjectPropertyPropertyPage2_13, button.getShell());
                }
                initCheckboxSection();
                layoutSections();
                _form.reflow(true);
            }
        });
        
        return y;
    }

    /**
     * @param localGroup
     */
    private void createCheckboxReflexive(Group localGroup) {
        _reflexiveCheckBox = createCheckbox(
                localGroup, 
                Messages.ObjectPropertyPropertyPage_CheckBoxes_Reflexive,
                OWLCommandUtils.REFLEXIVE);
    }

    /**
     * @param localGroup
     */
    private void createCheckboxIrreflexive(Group localGroup) {
        _irreflexiveCheckBox = createCheckbox(
                localGroup, 
                Messages.ObjectPropertyPropertyPage_CheckBoxes_Irreflexive,
                OWLCommandUtils.IRREFLEXIVE);
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
            OWLObjectProperty objProp = OWLModelFactory.getOWLDataFactory(_project).getOWLObjectProperty(OWLUtilities.toIRI(_id));
            if (objProp != null) {
                _functionalCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.FUNCTIONAL, false).getAttributeValue());
                _inverseFunctionalCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.INVERSE_FUNCTIONAL, false).getAttributeValue());
                _reflexiveCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.REFLEXIVE, false).getAttributeValue());
                _irreflexiveCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.IRREFLEXIVE, false).getAttributeValue());
                _symmetricCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.SYMMETRIC, false).getAttributeValue());
                _asymmetricCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.ASYMMETRIC, false).getAttributeValue());
                _transitiveCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.TRANSITIVE, false).getAttributeValue());

                if (_showImported) {
                    _importedGroup.setVisible(true);
                    _importedFunctionalCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.FUNCTIONAL, true).getAttributeValue());
                    _importedInverseFunctionalCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.INVERSE_FUNCTIONAL, true).getAttributeValue());
                    _importedReflexiveCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.REFLEXIVE, true).getAttributeValue());
                    _importedIrreflexiveCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.IRREFLEXIVE, true).getAttributeValue());
                    _importedSymmetricCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.SYMMETRIC, true).getAttributeValue());
                    _importedAsymmetricCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.ASYMMETRIC, true).getAttributeValue());
                    _importedTransitiveCheckBox.setSelection(new GetPropertyAttribute(_project, _ontologyUri, _id, OWLCommandUtils.TRANSITIVE, true).getAttributeValue());
                } else {
                    _importedGroup.setVisible(false);
                }
            }
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        } catch (NeOnCoreException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _domainSection.getShell());
        }
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_domainSection);
        sections.add(_rangeSection);
        sections.add(_characteristicsSection);
        return sections;
    }

    private void createRow(LocatedAxiom locatedAxiom, String ontologyUri, boolean enabled, final int mode) throws NeOnCoreException {
        OWLAxiom axiom = (OWLAxiom) locatedAxiom.getAxiom();
        boolean imported = !locatedAxiom.isLocal();
        Composite parent;
        if (mode == DOMAIN) {
            parent = _domainFormComposite;
        } else {
            parent = _rangeFormComposite;
        }

        OWLModel sourceOwlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(ontologyUri, _project);
        }else{
            sourceOwlModel =_owlModel;
        }
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri, sourceOwlModel.getProjectId(),_id);

        final DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, sourceOwlModel, _toolkit);
        final StyledText text = descriptionText.getStyledText();
        OWLClassExpression desc = mode == DOMAIN ? ((OWLObjectPropertyDomainAxiom) axiom).getDomain() : ((OWLObjectPropertyRangeAxiom) axiom).getRange();
        addComplexText(descriptionText);

        final String[] array = getArrayFromDescription(desc);
        text.setText(OWLGUIUtilities.getEntityLabel(array));
        text.setData(desc);
        text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, descriptionText);
        OWLGUIUtilities.enable(text, false);
        row.addWidget(text);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, new LocatedAxiom(axiom, true)) {

            @Override
            public void savePressed() {
                // save modified entries
                String newValue = text.getText();
                try {
                    OWLClassExpression newDescription = _manager.parseDescription(newValue, _localOwlModel);//NICO are you sure?
                    remove();
                    if (mode == DOMAIN) {
                        new CreateObjectPropertyDomain(_project, _sourceOwlModel.getOntologyURI(), _id, OWLUtilities.toString(newDescription)).run();//NICO are you sure?
                        initDomainSection(true);
                    } else {
                        new CreateObjectPropertyRange(_project, _sourceOwlModel.getOntologyURI(), _id, OWLUtilities.toString(newDescription)).run();//NICO are you sure?
                        initRangeSection(true);
                    }
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_13, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException e) {
                    handleException(e, Messages.ObjectPropertyPropertyPage2_13, text.getShell());
                    text.setFocus();
                    return;
                }
                layoutSections();
                _form.reflow(true);
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

    private Composite createEmptyRow(boolean enabled, final int domainOrRange) {
        Composite parent;
        if (domainOrRange == DOMAIN) {
            parent = _domainFormComposite;
        } else {
            parent = _rangeFormComposite;
        }
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);

        DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, _owlModel, _toolkit);
        final StyledText text = descriptionText.getStyledText();
        row.addWidget(text);
        addComplexText(descriptionText);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                String input = text.getText();
                try {
                    String value = OWLUtilities.toString(OWLPlugin.getDefault().getSyntaxManager().parseDescription(input, _localOwlModel));//NICO are you sure?
                    if (domainOrRange == DOMAIN) {
                        new CreateObjectPropertyDomain(_project, _sourceOwlModel.getOntologyURI(), _id, value).run();//NICO are you sure?
                        initDomainSection(true);
                    } else {
                        new CreateObjectPropertyRange(_project, _sourceOwlModel.getOntologyURI(), _id, value).run();//NICO are you sure?
                        initRangeSection(true);
                    }
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_13, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException e) {
                    handleException(e, Messages.ObjectPropertyPropertyPage2_13, text.getShell());
                    text.setFocus();
                    return;
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
        initCheckboxSection();

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
    }

    @Override
    public void dispose() {
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
                    if (axiom1 instanceof OWLObjectPropertyDomainAxiom) {
                        OWLObjectPropertyDomainAxiom domain = (OWLObjectPropertyDomainAxiom) axiom1;
                        OWLClassExpression desc1 = domain.getDomain();
                        uri1 = OWLGUIUtilities.getUriForSorting(desc1, _owlModel);

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                        if (axiom2 instanceof OWLObjectPropertyDomainAxiom) {
                            OWLObjectPropertyDomainAxiom domain2 = (OWLObjectPropertyDomainAxiom) axiom2;
                            OWLClassExpression desc2 = domain2.getDomain();
                            uri2 = OWLGUIUtilities.getUriForSorting(desc2, _owlModel);
                        }
                    } else if (axiom1 instanceof OWLObjectPropertyRangeAxiom) {
                        OWLObjectPropertyRangeAxiom range = (OWLObjectPropertyRangeAxiom) axiom1;
                        OWLClassExpression desc1 = range.getRange();
                        uri1 = OWLGUIUtilities.getUriForSorting(desc1, _owlModel);

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                        if (axiom2 instanceof OWLObjectPropertyRangeAxiom) {
                            OWLObjectPropertyRangeAxiom range2 = (OWLObjectPropertyRangeAxiom) axiom2;
                            OWLClassExpression desc2 = range2.getRange();
                            uri2 = OWLGUIUtilities.getUriForSorting(desc2, _owlModel);
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
