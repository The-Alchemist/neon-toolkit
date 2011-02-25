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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.EntityRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.PropertyText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateEquivalentDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetEquivalentDataPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetSubDataPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.GetSuperDataPropertyHits;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

/**
 * 
 * @author Nico Stieler
 */
public class DataPropertyTaxonomyPropertyPage extends AbstractOWLIdPropertyPage {

    private static final int SUPER = 1;
    private static final int SUB = 2;
    private static final int EQUIV = 3;

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 6;

    /*
     * JFace Forms variables
     */
    private Section _equivPropertySection;
    private Section _superPropertySection;
    private Section _subPropertySection;

    private Composite _equivFormComposite;
    private Composite _superFormComposite;
    private Composite _subFormComposite;

    /**
     * TODO disjoint object properties are possible in OWL 1.1 October 2007: KAON2 does not support them for the moment
     */

    public DataPropertyTaxonomyPropertyPage() {
        super();
    }

    @Override
    public Composite createContents(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_DATA_PROPERTIES_VIEW);
        Composite body = prepareForm(composite);

        createSuperPropertyArea(body);
        createSubPropertyArea(body);
        createEquivPropertyArea(body);

        layoutSections();
        _form.reflow(true);
        return body;
    }

    /**
     * Create super properties area
     */
    private void createSuperPropertyArea(Composite composite) {
        _superPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _superPropertySection.setText(Messages.DataPropertyPropertyPage_SuperProperties);
        _superPropertySection.setLayoutData(new ColumnLayoutData());

        _superFormComposite = _toolkit.createComposite(_superPropertySection, SWT.NONE);
        _superFormComposite.setLayout(new GridLayout());
        _toolkit.adapt(_superFormComposite);
        _toolkit.adapt(_superPropertySection);
        _superPropertySection.setClient(_superFormComposite);
    }

    private void initSuperSection(boolean setFocus) {
        clearComposite(_superFormComposite);

        try {
            String[][] results = new GetSuperDataPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, SUPER);
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLSubDataPropertyOfAxiom axiom = (OWLSubDataPropertyOfAxiom) OWLUtilities.axiom(axiomText, _owlModel.getOntology());
                createRow(new LocatedAxiom(axiom, isLocal), axiom.getSuperProperty(), ontologyUri, false, SUPER);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
        }

        Label createNewLabel = new Label(_superFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.DataPropertyPropertyPage2_3);
        Composite activeComposite = createEmptyRow(true, SUPER);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create sub properties area
     */
    private void createSubPropertyArea(Composite composite) {
        _subPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _subPropertySection.setText(Messages.DataPropertyPropertyPage_SubProperties);
        _subPropertySection.setLayoutData(new ColumnLayoutData());

        _subFormComposite = _toolkit.createComposite(_subPropertySection, SWT.NONE);
        _subFormComposite.setLayout(new GridLayout());
        _toolkit.adapt(_subFormComposite);
        _toolkit.adapt(_subPropertySection);
        _subPropertySection.setClient(_subFormComposite);
    }

    private void initSubSection(boolean setFocus) {
        clearComposite(_subFormComposite);
        try {
            String[][] results = new GetSubDataPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, SUB);
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLSubDataPropertyOfAxiom axiom = (OWLSubDataPropertyOfAxiom) OWLUtilities.axiom(axiomText, _owlModel.getOntology());
                createRow(new LocatedAxiom(axiom, isLocal), axiom.getSubProperty(), ontologyUri, false, SUB);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
        }

        Label createNewLabel = new Label(_subFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.DataPropertyPropertyPage2_3);
        Composite activeComposite = createEmptyRow(true, SUB);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create equivalent properties area
     */
    private void createEquivPropertyArea(Composite composite) {
        _equivPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _equivPropertySection.setText(Messages.DataPropertyPropertyPage_EquivalentProperties);
        _equivPropertySection.setLayoutData(new ColumnLayoutData());

        _equivFormComposite = _toolkit.createComposite(_equivPropertySection, SWT.NONE);
        _equivFormComposite.setLayout(new GridLayout());
        _toolkit.adapt(_equivFormComposite);
        _toolkit.adapt(_equivPropertySection);
        _equivPropertySection.setClient(_equivFormComposite);
    }

    private void initEquivSection(boolean setFocus) {
        clearComposite(_equivFormComposite);

        try {
            String[][] results = new GetEquivalentDataPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, EQUIV);

            OWLOntology ontology = _owlModel.getOntology();
            String thisProperty = OWLUtilities.toString(OWLUtilities.dataProperty(IRIUtils.ensureValidIRISyntax(_id), ontology), ontology);
            List<String> sourceOntoList = new ArrayList<String>();
            List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                sourceOntoList.add(ontologyUri);
                OWLEquivalentDataPropertiesAxiom equivObjProps = (OWLEquivalentDataPropertiesAxiom) OWLUtilities.axiom(axiomText, ontology);
                axiomList.add(new LocatedAxiom(equivObjProps, isLocal));
                Set<OWLDataPropertyExpression> DataProperties = equivObjProps.getProperties();
                for (OWLDataPropertyExpression prop: DataProperties) {
                    if (!OWLUtilities.toString(prop, ontology).equals(thisProperty)) {
                        createRow(axiomList, (OWLDataProperty) prop, false, ontologyUri, EQUIV);
                    }
                }

            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
        }

        Label createNewLabel = new Label(_equivFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.DataPropertyPropertyPage2_2);
        Composite activeComposite = createEmptyRow(true, EQUIV);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        initSuperSection(false);
        initSubSection(false);
        initEquivSection(false);

        // closeToolBar();
        layoutSections();
        _form.reflow(true);
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_superPropertySection);
        sections.add(_subPropertySection);
        sections.add(_equivPropertySection);
        return sections;
    }

    private void createRow(LocatedAxiom locatedAxiom, final OWLDataPropertyExpression objPropExpr, String ontologyUri, boolean enabled, final int mode) throws NeOnCoreException {
        boolean imported = !locatedAxiom.isLocal();
        Composite parent;
        if (mode == SUPER) {
            parent = _superFormComposite;
        } else if (mode == SUB) {
            parent = _subFormComposite;
        } else {
            parent = _equivFormComposite;
        }

        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(ontologyUri, _project);
        }
       
        final FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri,sourceOwlModel.getProjectId(),_id);


        PropertyText propertyText = new PropertyText(row.getParent(), _owlModel, sourceOwlModel, PropertyText.DATA_PROPERTY);
        final StyledText textWidget = propertyText.getStyledText();
        textWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, propertyText);
        OWLGUIUtilities.enable(textWidget, false);
        final String[] array = getArrayFromDescription(objPropExpr);
        String id = OWLGUIUtilities.getEntityLabel(array);
        textWidget.setText(id);
        textWidget.setData(objPropExpr);
        row.addWidget(textWidget);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, locatedAxiom) {

            @Override
            public void savePressed() {
                // save modified entries
                String value = textWidget.getText();
                try {
                    OWLOntology ontology = _localOwlModel.getOntology();
                    OWLDataProperty dataProp = _manager.parseDataProperty(value, _localOwlModel);
                    remove();
                    if (mode == SUPER) {
                        new CreateDataProperty(_project, _sourceOwlModel.getOntologyURI(), _id, dataProp.getIRI().toString()).run();
                        initSubSection(false);
                        initSuperSection(true);
                    } else if (mode == SUB) {
                        new CreateDataProperty(_project, _sourceOwlModel.getOntologyURI(), dataProp.getIRI().toString(), _id).run();
                        initSuperSection(false);
                        initSubSection(true);
                    } else {
                        new CreateEquivalentDataProperty(_project, _sourceOwlModel.getOntologyURI(), OWLUtilities.toString(dataProp, ontology), value);
                        initEquivSection(true);
                    }
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
                    textWidget.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
                    textWidget.setFocus();
                    return;
                }
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
                    textWidget.setText(array[1]);
                } else {
                    textWidget.setText(array[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    private void createRow(List<LocatedAxiom> axioms, OWLDataProperty property, boolean enabled, String sourceOnto, final int mode) throws NeOnCoreException {
        boolean imported = !sourceOnto.equals(_ontologyUri);
        Composite parent;
        if (mode == SUPER) {
            parent = _superFormComposite;
        } else if (mode == SUB) {
            parent = _subFormComposite;
        } else { // EQUIV
            parent = _equivFormComposite;
        }

        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project); //NICO Onto
        }
       
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, sourceOnto, sourceOwlModel.getProjectId(),_id);

        PropertyText propertyText = new PropertyText(row.getParent(), _owlModel, sourceOwlModel, PropertyText.DATA_PROPERTY);
        final StyledText text = propertyText.getStyledText();
        text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, propertyText);
        final String[] array = getArrayFromDescription(property);
        text.setText(OWLGUIUtilities.getEntityLabel(array));
        text.setData(property);
        OWLGUIUtilities.enable(text, false);
        row.addWidget(text);

        EntityRowHandler rowHandler = new EntityRowHandler(this, _owlModel, sourceOwlModel, property, axioms) {

            @Override
            public void savePressed() {
                // save modified entries
                String value = text.getText();
                try {
                    OWLOntology ontology = _localOwlModel.getOntology();
                    OWLDataProperty dataProp = _manager.parseDataProperty(value, _localOwlModel);
                    if (mode == SUPER) {
                        new CreateDataProperty(_project, _sourceOwlModel.getOntologyURI(), _id, dataProp.getIRI().toString()).run();
                        initSuperSection(true);
                    } else if (mode == SUB) {
                        new CreateDataProperty(_project, _sourceOwlModel.getOntologyURI(), dataProp.getIRI().toString(), _id).run();
                        initSubSection(true);
                    } else {
                        new CreateEquivalentDataProperty(_project, _sourceOwlModel.getOntologyURI(), OWLUtilities.toString(dataProp, ontology), value);
                        initEquivSection(true);
                    }

                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
                    text.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(text, false);
                refresh();
            }

            @Override
            public void removePressed() throws NeOnCoreException {
                List<LocatedAxiom> locatedAxioms = getAxioms();
                List<OWLAxiom> owlAxioms = new ArrayList<OWLAxiom>();
                for (LocatedAxiom a: locatedAxioms) {
                    if (a.isLocal()) {
                        owlAxioms.add(a.getAxiom());
                    }else{
                        owlAxioms.add(a.getAxiom());//NICO think about that
                    }
                }
                OWLOntology ontology = _localOwlModel.getOntology();
                OWLObjectProperty thisObjectProperty = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id), ontology);
                
                OWLAxiomUtils.triggerRemovePressed(owlAxioms, getEntity(), _namespaces, OWLUtilities.toString(thisObjectProperty, ontology), _sourceOwlModel, WizardConstants.ADD_DEPENDENT_MODE);
                refresh();
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

                if (array.length > 2) {
                    text.setText(array[2]);
                } else {
                    text.setText(array[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    private Composite createEmptyRow(boolean enabled, final int mode) {
        Composite parent;
        if (mode == SUPER) {
            parent = _superFormComposite;
        } else if (mode == SUB) {
            parent = _subFormComposite;
        } else {
            parent = _equivFormComposite;
        }
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);
        final StyledText text = new PropertyText(row.getParent(), _owlModel, _owlModel, PropertyText.DATA_PROPERTY).getStyledText();
        row.addWidget(text);
        addSimpleWidget(text);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                try {
                    String value = text.getText();
                    OWLDataProperty dataProp = _manager.parseDataProperty(value, _localOwlModel);
                    OWLOntology ontology = _localOwlModel.getOntology();

                    switch (mode) {
                        case SUPER:
                            new CreateDataProperty(_project, _sourceOwlModel.getOntologyURI(), _id, dataProp.getIRI().toString()).run();
                            initSubSection(false);
                            initSuperSection(true);
                            break;
                        case SUB:
                            new CreateDataProperty(_project, _sourceOwlModel.getOntologyURI(), dataProp.getIRI().toString(), _id).run();
                            initSuperSection(false);
                            initSubSection(true);
                            break;
                        case EQUIV:
                            String thisEntity = OWLUtilities.toString(OWLUtilities.dataProperty(IRIUtils.ensureValidIRISyntax(_id), ontology), ontology);
                            if (!OWLUtilities.toString(dataProp, ontology).equals(thisEntity)) {
                                new CreateEquivalentDataProperty(_project, _sourceOwlModel.getOntologyURI(), thisEntity, OWLUtilities.toString(dataProp, ontology)).run();
                                initEquivSection(true);
                            } else {
                                String modeString = Messages.DataPropertyTaxonomyPropertyPage_0;
                                MessageDialog.openWarning(_subFormComposite.getShell(), Messages.DataPropertyPropertyPage2_47, Messages.DataPropertyTaxonomyPropertyPage_1 + " " + modeString + Messages.DataPropertyTaxonomyPropertyPage_2); //$NON-NLS-1$
                            }
                            break;
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException ce) {
                    handleException(ce, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
                    text.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(text, false);
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
            @Override
            public void modifyText(ModifyEvent e) {
                if (text.getText().trim().length() == 0) {
                    row.getAddButton().setEnabled(false);
                    row.getCancelButton().setEnabled(false);
                } else {
                    row.getAddButton().setEnabled(true);
                    row.getCancelButton().setEnabled(true);
                }
            }
        });
        return text;
    }

    @Override
    public void update() {
        super.update();

        initSuperSection(false);
        initSubSection(false);
        initEquivSection(false);

        layoutSections();
        _form.reflow(true);
    }

    private TreeSet<String[]> getSortedSet(String[][] clazzesArray, final int mode) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    String thisId = DataPropertyTaxonomyPropertyPage.this._id;
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0], _owlModel.getOntology());
                    String uri1 = ""; //$NON-NLS-1$
                    String uri2 = ""; //$NON-NLS-1$
                    if (mode == SUPER) {
                        OWLSubDataPropertyOfAxiom subObjProp = (OWLSubDataPropertyOfAxiom) axiom1;
                        OWLDataPropertyExpression objProp = subObjProp.getSuperProperty();
                        if (objProp instanceof OWLDataProperty) {
                            uri1 = OWLGUIUtilities.getEntityLabel(((OWLDataProperty) objProp), ontologyUri1, _project); 
                            OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _owlModel.getOntology());
                            OWLSubDataPropertyOfAxiom subObjProp2 = (OWLSubDataPropertyOfAxiom) axiom2;
                            OWLDataPropertyExpression objProp2 = subObjProp2.getSuperProperty();
                            if (objProp2 instanceof OWLDataProperty) {
                                uri2 = OWLGUIUtilities.getEntityLabel(((OWLDataProperty) objProp2), ontologyUri2, _project);
                            }
                        }
                    } else if (mode == SUB) {
                        OWLSubDataPropertyOfAxiom subObjProp = (OWLSubDataPropertyOfAxiom) axiom1;
                        OWLDataPropertyExpression dpe = subObjProp.getSubProperty();
                        if (dpe instanceof OWLDataProperty) {
                            OWLDataProperty DataProperty = (OWLDataProperty)dpe;
                            uri1 = OWLGUIUtilities.getEntityLabel(DataProperty, ontologyUri1, _project);
                            OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _owlModel.getOntology());
                            OWLSubDataPropertyOfAxiom subObjProp2 = (OWLSubDataPropertyOfAxiom) axiom2;
                            OWLDataPropertyExpression ope2 = subObjProp2.getSubProperty(); 
                            if (ope2 instanceof OWLDataProperty) {
                                OWLDataProperty DataProperty2 = (OWLDataProperty)ope2;
                                if (!(DataProperty2.getIRI().toString().equals(thisId))) {
                                    uri2 = OWLGUIUtilities.getEntityLabel(DataProperty2, ontologyUri2, _project);
                                }
                            }
                        }
                    } else if (mode == EQUIV) {
                        OWLEquivalentDataPropertiesAxiom eop = (OWLEquivalentDataPropertiesAxiom) axiom1;
                        Set<OWLDataPropertyExpression> equivalentDataProps = eop.getProperties();
                        for (OWLDataPropertyExpression expr: equivalentDataProps) {
                            if (expr instanceof OWLDataProperty) {
                                if (!((OWLDataProperty)expr).getIRI().toString().equals(thisId)) {
                                    uri1 = OWLGUIUtilities.getEntityLabel((OWLDataProperty)expr, ontologyUri1, _project);
                                }
                            }
                        }
                        
                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _owlModel.getOntology());
                        OWLEquivalentDataPropertiesAxiom eop2 = (OWLEquivalentDataPropertiesAxiom) axiom2;
                        Set<OWLDataPropertyExpression> equivalentDataProps2 = eop2.getProperties();
                        for (OWLDataPropertyExpression expr: equivalentDataProps2) {
                            if (expr instanceof OWLDataProperty) {
                                if (!((OWLDataProperty)expr).getIRI().toString().equals(thisId)) {
                                    uri2 = OWLGUIUtilities.getEntityLabel((OWLDataProperty)expr, ontologyUri1, _project);
                                }
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
