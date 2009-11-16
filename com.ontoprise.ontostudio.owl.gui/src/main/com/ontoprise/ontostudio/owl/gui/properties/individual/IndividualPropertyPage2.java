/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.individual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLStringLiteral;
import org.semanticweb.owlapi.model.OWLTypedLiteral;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.UnknownDatatypeException;
import com.ontoprise.ontostudio.owl.gui.util.forms.AbstractFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DatatypeText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.IndividualText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.PropertyText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.StringText;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateDataPropertyMember;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateObjectPropertyMember;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetDataPropertyMemberHits;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetObjectPropertyMemberHits;


public class IndividualPropertyPage2 extends AbstractOWLMainIDPropertyPage {

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 6;

    /*
     * JFace Forms variables
     */
    private Section _dataPropertyDescriptionsSection;
    private Section _objectPropertyDescriptionsSection;

    private Composite _dataPropertyDescriptionsComp;
    private Composite _objectPropertyDescriptionsComp;

//    private DatatypeVerifier _dataTypeVerifier;

    public IndividualPropertyPage2() {
        super();
    }

    
    @Override
    protected void createMainArea(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_INDIVIDUAL_VIEW);
        super.createMainArea(composite);
        Composite body = prepareForm(composite);

        createObjectPropertyDescriptionsArea(body);
        createDataPropertyDescriptionsArea(body);
        _form.reflow(true);
    }

    @Override
    public void refreshComponents() {
        super.refreshComponents();

        initObjectPropertyDescriptionsSection(false);
        initDataPropertyDescriptionsSection(false);

        // closeToolBar();
        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void updateComponents() {
        super.updateComponents();

        initObjectPropertyDescriptionsSection(false);
        initDataPropertyDescriptionsSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
    }

    /**
     * Create descriptions area
     */
    private void createObjectPropertyDescriptionsArea(Composite composite) {
        // Object Property values
        _objectPropertyDescriptionsSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);

        _objectPropertyDescriptionsSection.setText(Messages.IndividualPropertyPage_Descriptions2);
        _objectPropertyDescriptionsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _objectPropertyDescriptionsComp = _toolkit.createComposite(_objectPropertyDescriptionsSection, SWT.NONE);
        _objectPropertyDescriptionsComp.setLayout(new GridLayout());
        _objectPropertyDescriptionsComp.setLayoutData(new ColumnLayoutData());

        _toolkit.adapt(_objectPropertyDescriptionsComp);
        _objectPropertyDescriptionsSection.setClient(_objectPropertyDescriptionsComp);
    }

    private void initObjectPropertyDescriptionsSection(boolean setFocus) {
        clearComposite(_objectPropertyDescriptionsComp);
        try {
            String[][] results = new GetObjectPropertyMemberHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results);
            createObjectPropertyRowTitles(_objectPropertyDescriptionsComp, _objectPropertyDescriptionsSection, results.length > 0);
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLObjectPropertyAssertionAxiom mem = (OWLObjectPropertyAssertionAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);
                createObjectPropertyRow(_objectPropertyDescriptionsComp, new LocatedAxiom(mem, isLocal), ontologyUri, false);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _dataPropertyDescriptionsSection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _dataPropertyDescriptionsSection.getShell());
        }

        Label createNewLabel = new Label(_objectPropertyDescriptionsComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.IndividualPropertyPage2_4);
        Composite activeComposite = createEmptyObjectPropertyRow(_objectPropertyDescriptionsComp);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private void createObjectPropertyRowTitles(Composite comp, Section section, boolean hasEntries) {
        boolean showAxiomsCol = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS) && hasEntries;
        Composite rowComp = _toolkit.createComposite(comp);
        GridLayout layout = new GridLayout(5, false);
        layout.marginHeight = 0;
        rowComp.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        rowComp.setLayoutData(layoutData);
        Color color = section.getTitleBarForeground();

        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = showAxiomsCol ? 200 : 200;
        Text l1 = new Text(rowComp, SWT.NONE);
        l1.setText(Messages.IndividualPropertyPage_Property);
        l1.setForeground(color);
        l1.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = showAxiomsCol ? 200 : 200;
        Text l2 = new Text(rowComp, SWT.NONE);
        l2.setText(Messages.IndividualPropertyPage_Value);
        l2.setForeground(color);
        l2.setLayoutData(data);

        if (showAxiomsCol) {
            data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
            data.widthHint = 40;
            Text l3 = new Text(rowComp, SWT.NONE);
            l3.setText(Messages.ClazzPropertyPage_Restrictions_Axiom);
            l3.setForeground(color);
            l3.setLayoutData(data);
        }

        Button b1 = createEditButton(rowComp, false);
        // data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        // data.heightHint = 10;
        // data.verticalAlignment = GridData.END;
        // b1.setLayoutData(data);
        b1.setVisible(false);

        Button b2 = createRemoveButton(rowComp, false);
        // data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        // data.heightHint = 10;
        // data.verticalAlignment = GridData.END;
        // b2.setLayoutData(data);
        b2.setVisible(false);
    }

    private void createObjectPropertyRow(Composite parent, LocatedAxiom locatedAxiom, String ontologyUri, boolean enabled) throws NeOnCoreException {
        boolean imported = !locatedAxiom.isLocal();
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = _manager.getVisitor(_owlModel, idDisplayStyle);
        final OWLObjectPropertyAssertionAxiom objectPropertyMember = (OWLObjectPropertyAssertionAxiom) locatedAxiom.getAxiom();

        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri);
        // text widgets
        final StyledText propertyText = new PropertyText(row.getParent(), _owlModel, PropertyText.OBJECT_PROPERTY).getStyledText();
        row.addWidget(propertyText);

        final StyledText valueText = new IndividualText(row.getParent(), _owlModel).getStyledText();
        row.addWidget(valueText);

        final String[] propArray = (String[]) objectPropertyMember.getProperty().accept(visitor);
        String propId = OWLGUIUtilities.getEntityLabel(propArray);
        propertyText.setText(propId);
        propertyText.setData(objectPropertyMember.getProperty());
        OWLGUIUtilities.enable(propertyText, false);
        row.addWidget(propertyText);

        final String[] valueArray = (String[]) objectPropertyMember.getObject().accept(visitor);
        String valueId = OWLGUIUtilities.getEntityLabel(valueArray);
        valueText.setText(valueId);
        valueText.setData(objectPropertyMember.getObject());
        row.addWidget(valueText);
        OWLGUIUtilities.enable(valueText, false);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, locatedAxiom) {

            @Override
            public void savePressed() {
                try {
                    remove();
                    OWLObjectProperty newProp = _manager.parseObjectProperty(propertyText.getText(), _owlModel);
                    OWLIndividual targetIndividual = _manager.parseIndividual(valueText.getText(), _owlModel);

                    new CreateObjectPropertyMember(_project, _ontologyUri, _id, newProp.getURI().toString(), OWLUtilities.toString(targetIndividual)).run();
                } catch (NeOnCoreException e1) {
                    handleException(e1, Messages.IndividualPropertyPage2_18, valueText.getShell());
                    propertyText.setFocus();
                    return;
                } catch (CommandException e1) {
                    handleException(e1, Messages.IndividualPropertyPage2_18, valueText.getShell());
                    propertyText.setFocus();
                    return;
                }
                initObjectPropertyDescriptionsSection(true);

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

                if (propArray.length > 1) {
                    propertyText.setText(propArray[1]);
                } else {
                    propertyText.setText(propArray[0]);
                }
                if (valueArray.length > 1) {
                    valueText.setText(valueArray[1]);
                } else {
                    valueText.setText(valueArray[0]);
                }
            }
        };
        row.init(rowHandler);
    }

    private Composite createEmptyObjectPropertyRow(Composite parent) {
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);
        // text widgets
        final StyledText propertyText = new PropertyText(row.getParent(), _owlModel, PropertyText.OBJECT_PROPERTY).getStyledText();
        row.addWidget(propertyText);
        addSimpleWidget(propertyText);

        final StyledText valueText = new IndividualText(row.getParent(), _owlModel).getStyledText();
        row.addWidget(valueText);
        addSimpleWidget(valueText);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                try {
                    OWLObjectProperty newProp = _manager.parseObjectProperty(propertyText.getText(), _owlModel);
                    OWLIndividual targetIndividual = _manager.parseIndividual(valueText.getText(), _owlModel);

                    new CreateObjectPropertyMember(_project, _ontologyUri, _id, newProp.getURI().toString(), OWLUtilities.toString(targetIndividual)).run();
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.IndividualPropertyPage2_18, valueText.getShell());
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.IndividualPropertyPage2_18, valueText.getShell());
                }
                initObjectPropertyDescriptionsSection(true);
                layoutSections();
                _form.reflow(true);
            }

            @Override
            public void ensureQName() {
                // nothing to do
            }

        };
        row.init(rowHandler);

        propertyText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (propertyText.getText().trim().length() == 0) {
                    row.getCancelButton().setEnabled(false);
                    row.getAddButton().setEnabled(false);
                } else {
                    row.getCancelButton().setEnabled(true);
                    row.getAddButton().setEnabled(true);
                }
            }
        });
        return propertyText;
    }

    /**
     * Create Data Property Descriptions area
     */
    private void createDataPropertyDescriptionsArea(Composite composite) {
        _dataPropertyDescriptionsSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _dataPropertyDescriptionsSection.setText(Messages.IndividualPropertyPage_Descriptions);
        _dataPropertyDescriptionsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _dataPropertyDescriptionsComp = _toolkit.createComposite(_dataPropertyDescriptionsSection, SWT.NONE);
        _dataPropertyDescriptionsComp.setLayout(new GridLayout());
        _dataPropertyDescriptionsComp.setLayoutData(new ColumnLayoutData());

        _toolkit.adapt(_dataPropertyDescriptionsComp);
        _dataPropertyDescriptionsSection.setClient(_dataPropertyDescriptionsComp);

    }

    private void initDataPropertyDescriptionsSection(boolean setFocus) {
        clearComposite(_dataPropertyDescriptionsComp);

        try {
            String[][] results = new GetDataPropertyMemberHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results);
            createDataPropertyRowTitles(_dataPropertyDescriptionsComp, _dataPropertyDescriptionsSection, results.length > 0);
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLDataPropertyAssertionAxiom mem = (OWLDataPropertyAssertionAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);
                createDataPropertyRow(_dataPropertyDescriptionsComp, new LocatedAxiom(mem, isLocal), ontologyUri, false);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _dataPropertyDescriptionsSection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _dataPropertyDescriptionsSection.getShell());
        }

        Label createNewLabel = new Label(_dataPropertyDescriptionsComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.IndividualPropertyPage2_4);
        Composite activeComposite = createEmptyDataPropertyRow(_dataPropertyDescriptionsComp, true);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private void verifyInput(final AbstractFormRow formRow, final StyledText propertyText, final StyledText valueText, final StyledText typeText) {
        boolean error = false;
        String message = null;
        int type = IMessageProvider.NONE;
        if (propertyText.getText().trim().length() == 0) {
            message = Messages.AnnotationsPropertyPage2_2;
            type = IMessageProvider.ERROR;
            error = true;
        }

        if (valueText.getText().trim().length() == 0) {
            message = Messages.AnnotationsPropertyPage2_7;
            type = IMessageProvider.ERROR;
            error = true;
        }

//        String datatype = typeText.getText();
//        if (datatype.equals("")) { //$NON-NLS-1$
//            message = null;
//            type = IMessageProvider.NONE;
//            warning = false;
//        } else {
//            String expandedRange = _namespaces.expandString(datatype);
//            String value = valueText.getText();
//            try {
//                // TODO: migration, use the MAPI parser to check if value is a valid literal for data type expandedRange, see issue 12300.
//                if(value.length() > 0 && _dataTypeVerifier.verify(expandedRange)){
//                    if(new ValueInputVerifier(expandedRange).verify(value)){
//                        message = null;
//                        type = IMessageProvider.NONE;
//                        warning = false;
//                    }else{
//                        message = Messages.AnnotationsPropertyPage2_3 + datatype + Messages.AnnotationsPropertyPage2_4;
//                        type = IMessageProvider.WARNING;
//                        warning = true;
//                    }
//                }
//            } catch (RuntimeException mapiParserException) {
//                message = Messages.AnnotationsPropertyPage2_3 + datatype + Messages.AnnotationsPropertyPage2_4;
//                type = IMessageProvider.WARNING;
//                warning = true;
//            }
//        }

        String datatype = typeText.getText();
        String expandedRange = _namespaces.expandString(datatype);
        String value = valueText.getText();
        try{
            OWLGUIUtilities.verifyUserInput(value, expandedRange);
        }catch (UnknownDatatypeException e){
            message = e.getMessage();
            type = IMessageProvider.WARNING;
            error = false;
        }catch (IllegalArgumentException e){
            message = e.getMessage();
            type = IMessageProvider.ERROR;
            error = true;
        }
        _form.setMessage(message, type);
        formRow.getSubmitButton().setEnabled(!error);
    }

    private void createDataPropertyRowTitles(Composite comp, Section section, boolean hasEntries) {
        boolean showAxiomsCol = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS) && hasEntries;
        Composite rowComp = _toolkit.createComposite(comp);
        GridLayout layout = new GridLayout(6, false);
        layout.marginHeight = 0;
        rowComp.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        rowComp.setLayoutData(layoutData);
        Color color = section.getTitleBarForeground();

        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 200; // showAxiomsCol ? 100 : 200;
        Text l1 = new Text(rowComp, SWT.NONE);
        l1.setText(Messages.IndividualPropertyPage_Property);
        l1.setForeground(color);
        l1.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 200; // showAxiomsCol ? 100 : 190;
        Text l2 = new Text(rowComp, SWT.NONE);
        l2.setText(Messages.IndividualPropertyPage_Value);
        l2.setForeground(color);
        l2.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 200; // showAxiomsCol ? 100 : 160;
        Text l3 = new Text(rowComp, SWT.NONE);
        l3.setText(Messages.IndividualPropertyPage_Type);
        l3.setForeground(color);
        l3.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 50; // showAxiomsCol ? 32 : 32;
        Text l4 = new Text(rowComp, SWT.NONE);
        l4.setText(Messages.IndividualPropertyPage_Language);
        l4.setForeground(color);
        l4.setLayoutData(data);

        if (showAxiomsCol) {
            data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
            data.widthHint = 130;
            Text l5 = new Text(rowComp, SWT.NONE);
            l5.setText(Messages.ClazzPropertyPage_Restrictions_Axiom);
            l5.setForeground(color);
            l5.setLayoutData(data);
        }

        Button b1 = createEditButton(rowComp, false);
        // data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        // data.heightHint = 10;
        // data.verticalAlignment = GridData.END;
        // b1.setLayoutData(data);
        b1.setVisible(false);

        Button b2 = createRemoveButton(rowComp, false);
        // data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        // data.heightHint = 10;
        // data.verticalAlignment = GridData.END;
        // b2.setLayoutData(data);
        b2.setVisible(false);
    }

    /**
     * 
     * @param axiom
     * @return a list of length 4 with string arrays for property, value, type, language. type and language are optional. the string arrays contain uri, label,
     *         qname,.
     */
    private ArrayList<String[]> handleDataPropertyMemberAxiom(OWLDataPropertyAssertionAxiom axiom) {
        OWLLiteral target = axiom.getObject();

        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = _manager.getVisitor(_owlModel, idDisplayStyle);

        String[] propArray = (String[]) axiom.getProperty().accept(visitor);
        ArrayList<String[]> contents = new ArrayList<String[]>();

        if (!target.isTyped()) {
            OWLStringLiteral untypedConstant = (OWLStringLiteral)target;
            String literal = untypedConstant.getLiteral();
            String language = untypedConstant.getLang();
            String dataType = OWLConstants.RDFS_LITERAL;
            contents.add(propArray);
            contents.add(new String[] {literal});
            try {
                contents.add((String[]) OWLModelFactory.getOWLDataFactory(_project).getOWLDatatype(OWLUtilities.toURI(dataType)).accept(visitor));
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
            contents.add(new String[] {language});
            return contents;

        } else {
            OWLTypedLiteral typedConstant = (OWLTypedLiteral)target;
            OWLDatatype dataType = typedConstant.getDatatype();
            contents.add(propArray);
            contents.add(new String[] {typedConstant.getLiteral()});
            contents.add((String[]) dataType.accept(visitor));
            contents.add(new String[] {null});
            return contents;
        }
    }

    /**
     * 
     * @param descriptions a list of length 4 that contains the property, the value, the type and the language. Each element is a String Array consisting of
     *            uri, label, qname. type and language can be empty.
     * 
     * @param enabled
     * @throws NeOnCoreException
     */
    private void createDataPropertyRow(Composite parent, LocatedAxiom locatedAxiom, String ontologyUri, boolean enabled) throws NeOnCoreException {
        boolean imported = !locatedAxiom.isLocal();
        final FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri);
        final OWLDataPropertyAssertionAxiom axiom = (OWLDataPropertyAssertionAxiom) locatedAxiom.getAxiom();
        List<String[]> descriptions = handleDataPropertyMemberAxiom(axiom);

        final StyledText propertyText = new PropertyText(row.getParent(), _owlModel, PropertyText.DATA_PROPERTY).getStyledText();
        row.addWidget(propertyText);

        final StyledText valueText = new StringText(row.getParent()).getStyledText();
        row.addWidget(valueText);

        final StyledText typeText = new DatatypeText(row.getParent(), _owlModel).getStyledText();
        row.addWidget(typeText);

        final CCombo languageCombo = OWLGUIUtilities.createLanguageComboBox(row.getParent(), enabled);
        row.addWidget(languageCombo);

        final String[] propertyArray = descriptions.get(0);
        final String[] valueArray = descriptions.get(1);
        final String[] datatypeArray = descriptions.get(2);

        OWLGUIUtilities.initStringOrLiteralSwitch(typeText, languageCombo, _owlModel);
        // dataProperty
        if (axiom != null) {
            String id = OWLGUIUtilities.getEntityLabel(propertyArray);
            propertyText.setText(id);
            propertyText.setData(propertyArray);
            OWLGUIUtilities.enable(propertyText, false);
        }

        // value
        if (axiom != null) {
            valueText.setText(valueArray[0]);
            valueText.setData(valueArray);
            OWLGUIUtilities.enable(valueText, false);
        }

        boolean showLanguange = false;

        // datatype
        if (axiom != null) {
            String id = OWLGUIUtilities.getEntityLabel(datatypeArray);
            typeText.setText(id);
            typeText.setData(datatypeArray);
            OWLGUIUtilities.enable(typeText, false);
            String datatype = datatypeArray.length > 1 ? datatypeArray[1] : datatypeArray[0];
            try {
                if (((OWLDatatype) _manager.parseDataRange(datatype, _owlModel)).getURI().toString().equals(OWLConstants.RDFS_LITERAL)) { 
                    showLanguange = true;
                }
            } catch (NeOnCoreException e) {
                showLanguange = false;
            }
        }

        // language
        if (showLanguange && axiom != null) {
            String[] array = descriptions.get(3);
            String id = array[0];
            int index = 0;
            if (id != null) {
                index = languageCombo.indexOf(id);
                if (index < 0) {
                    languageCombo.add(id);
                    index = languageCombo.indexOf(id);
                }
            }
            languageCombo.select(index);
            languageCombo.setData(array);
            OWLGUIUtilities.enable(languageCombo, false);
        }

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, locatedAxiom) {

            @Override
            public void savePressed() {
                try {
                    remove();
                    OWLDataPropertyExpression prop = _manager.parseDataProperty(propertyText.getText(), _owlModel);

                    OWLDatatype type;
                    if (typeText.getText().equals("")) { //$NON-NLS-1$
                        type = OWLModelFactory.getOWLDataFactory(_project).getOWLDatatype(OWLUtilities.toURI(OWLConstants.RDFS_LITERAL));
                    } else {
                        type = (OWLDatatype) _manager.parseDataRange(typeText.getText(), _owlModel);
                    }

                    String[] valueArray = new String[] {valueText.getText(), languageCombo.getText()};
                    new CreateDataPropertyMember(_project, _ontologyUri, _id, ((OWLDataProperty)prop).getURI().toString(), type.getURI().toString(), valueArray).run();
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.IndividualPropertyPage2_18, valueText.getShell());
                    propertyText.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.IndividualPropertyPage2_18, valueText.getShell());
                    propertyText.setFocus();
                    return;
                }
                initDataPropertyDescriptionsSection(true);

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

                if (propertyArray.length > 1) {
                    propertyText.setText(propertyArray[1]);
                } else {
                    propertyText.setText(propertyArray[0]);
                }
                if (datatypeArray.length > 1) {
                    typeText.setText(datatypeArray[1]);
                } else {
                    typeText.setText(datatypeArray[0]);
                }
            }

        };
        addListeners(row, propertyText, valueText, typeText);
        row.init(rowHandler);
    }

    private void addListeners(final AbstractFormRow row, final StyledText propertyText, final StyledText valueText, final StyledText typeText) {
        propertyText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                verifyInput(row, propertyText, valueText, typeText);
            }
        });
        valueText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                verifyInput(row, propertyText, valueText, typeText);
            }
        });
        typeText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                verifyInput(row, propertyText, valueText, typeText);
            }
        });
    }

    /**
     * 
     * @param descriptions a list of length 4 that contains the property, the value, the type and the language. Each element is a String Array consisting of
     *            uri, label, qname. type and language can be empty.
     * 
     * @param enabled
     */
    private Composite createEmptyDataPropertyRow(Composite parent, boolean enabled) {
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);
        final StyledText propertyText = new PropertyText(row.getParent(), _owlModel, PropertyText.DATA_PROPERTY).getStyledText();
        row.addWidget(propertyText);
        addSimpleWidget(propertyText);

        final StyledText valueText = new StringText(row.getParent()).getStyledText();
        row.addWidget(valueText);
        addSimpleWidget(valueText);

        final StyledText typeText = new DatatypeText(row.getParent(), _owlModel).getStyledText();
        row.addWidget(typeText);
        addSimpleWidget(typeText);

        final CCombo languageCombo = OWLGUIUtilities.createLanguageComboBox(row.getParent(), enabled);
        row.addWidget(languageCombo);
        addSimpleWidget(languageCombo);

        OWLGUIUtilities.initStringOrLiteralSwitch(typeText, languageCombo, _owlModel);
        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                try {
//                    OWLIndividual individual = _owlModel.get OWLModelFactory.getOWLDataFactory(_project).getOWLNamedIndividual(OWLUtilities.toURI(_id));
//                    InternalParser parser = new InternalParser(_id, OWLNamespaces.EMPTY_INSTANCE, OWLModelFactory.getOWLDataFactory(_project));
//                    OWLIndividual individual = parser.parseOWLIndividual();
                    
                    OWLDataPropertyExpression prop = _manager.parseDataProperty(propertyText.getText(), _owlModel);

                    OWLDatatype type;
                    if (typeText.getText().equals("")) { //$NON-NLS-1$
                        type = OWLModelFactory.getOWLDataFactory(_project).getOWLDatatype(OWLUtilities.toURI(OWLConstants.RDFS_LITERAL));
                    } else {
                        type = (OWLDatatype) _manager.parseDataRange(typeText.getText(), _owlModel);
                    }

                    OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(_project);
                    OWLLiteral c;
                    if (type.getURI().toString().equals(OWLConstants.RDFS_LITERAL)) {
                        if (!languageCombo.getText().equals(OWLCommandUtils.EMPTY_LANGUAGE) && !languageCombo.getText().equals("")) { //$NON-NLS-1$
                            c = factory.getOWLStringLiteral(valueText.getText(), languageCombo.getText());
                        } else {
                            c = factory.getOWLTypedLiteral(valueText.getText(), factory.getOWLDatatype(OWLUtilities.toURI(OWLConstants.XSD_STRING)));
                        }
                    } else {
                        c = factory.getOWLTypedLiteral(valueText.getText(), type);
                    }

                    OWLAxiom newAxiom = factory.getOWLDataPropertyAssertionAxiom(prop, (OWLIndividual)getOWLObject(), c);
                    new ApplyChanges(_project, _ontologyUri, new String[] {OWLUtilities.toString(newAxiom)}, new String[0]).run();
                } catch (NeOnCoreException e1) {
                    handleException(e1, Messages.IndividualPropertyPage2_18, valueText.getShell());
                    return;
                } catch (CommandException e) {
                    handleException(e, Messages.IndividualPropertyPage2_18, valueText.getShell());
                    return;
                }
                initDataPropertyDescriptionsSection(true);
                layoutSections();
                _form.reflow(true);
            }

            @Override
            public void ensureQName() {
                // nothing to do
            }

        };
        row.init(rowHandler);

        addListeners(row, propertyText, valueText, typeText);
        return propertyText;
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_objectPropertyDescriptionsSection);
        sections.add(_dataPropertyDescriptionsSection);
        return sections;
    }

    @Override
    protected String getTitle() {
        return Messages.IndividualPropertyPage_Individuals;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

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
                    if (axiom1 instanceof OWLObjectPropertyAssertionAxiom) {
                        OWLObjectPropertyAssertionAxiom objPropMember = (OWLObjectPropertyAssertionAxiom) axiom1;
                        OWLObjectPropertyExpression objProp = objPropMember.getProperty();
                        if (objProp instanceof OWLObjectProperty) {
                            uri1 = OWLGUIUtilities.getEntityLabel(((OWLObjectProperty) objProp), ontologyUri1, _project);
                            OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                            OWLObjectPropertyAssertionAxiom objPropMember2 = (OWLObjectPropertyAssertionAxiom) axiom2;
                            OWLObjectPropertyExpression objProp2 = objPropMember2.getProperty();
                            if (objProp2 instanceof OWLObjectProperty) {
                                uri2 = OWLGUIUtilities.getEntityLabel(((OWLObjectProperty) objProp2), ontologyUri2, _project);
                            }
                        }
                    } else if (axiom1 instanceof OWLDataPropertyAssertionAxiom) {
                        OWLDataPropertyAssertionAxiom dataPropMember = (OWLDataPropertyAssertionAxiom) axiom1;
                        OWLDataPropertyExpression dataProp = dataPropMember.getProperty();
                        if (dataProp instanceof OWLDataProperty) {
                            uri1 = OWLGUIUtilities.getEntityLabel(((OWLDataProperty) dataProp), ontologyUri1, _project);
                            OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                            OWLDataPropertyAssertionAxiom dataPropMember2 = (OWLDataPropertyAssertionAxiom) axiom2;
                            OWLDataPropertyExpression dataProp2 = dataPropMember2.getProperty();
                            if (dataProp2 instanceof OWLDataProperty) {
                                uri2 = OWLGUIUtilities.getEntityLabel(((OWLDataProperty) dataProp2), ontologyUri2, _project);
                            }
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
