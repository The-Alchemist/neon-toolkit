/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * based on the Class DataPropertyTaxonomyPropertyPage with ontoprise GmbH copyrights 
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.annotationProperty;

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
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
//import org.semanticweb.owlapi.model.OWLAnnotationProperty;
//import org.semanticweb.owlapi.model.OWLEquivalentAnnotationPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

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
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
//import com.ontoprise.ontostudio.owl.model.commands.annotationpropertiesproperties.CreateEquivalentAnnotationProperty;
//import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetEquivalentAnnotationPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetSubAnnotationPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetSuperAnnotationPropertyHits;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

/**
 * 
 * @author Nico Stieler
 */
public class AnnotationPropertyTaxonomyPropertyPage extends AbstractOWLIdPropertyPage {

    private static final int SUPER = 1;
    private static final int SUB = 2;
//    private static final int EQUIV = 3;

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 6;

    /*
     * JFace Forms variables
     */
//    private Section _equivPropertySection;
    private Section _superPropertySection;
    private Section _subPropertySection;

//    private Composite _equivFormComposite;
    private Composite _superFormComposite;
    private Composite _subFormComposite;

    /**
     * TODO disjoint object properties are possible in OWL 1.1 October 2007: KAON2 does not support them for the moment
     */

    public AnnotationPropertyTaxonomyPropertyPage() {
        super();
    }

    @Override
    public Composite createContents(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_ANNOTATION_PROPERTIES_VIEW);
        Composite body = prepareForm(composite);

        createSuperPropertyArea(body);
        createSubPropertyArea(body);
//        createEquivPropertyArea(body);

        layoutSections();
        _form.reflow(true);
        return body;
    }

    /**
     * Create super properties area
     */
    private void createSuperPropertyArea(Composite composite) {
        _superPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _superPropertySection.setText(Messages.AnnotationPropertyPropertyPage_SuperProperties);
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
            String[][] results = new GetSuperAnnotationPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, SUPER);
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLSubAnnotationPropertyOfAxiom axiom = (OWLSubAnnotationPropertyOfAxiom) OWLUtilities.axiom(axiomText);
                createRow(new LocatedAxiom(axiom, isLocal), axiom.getSuperProperty(), ontologyUri, false, SUPER);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        }

        Label createNewLabel = new Label(_superFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.AnnotationPropertyPropertyPage2_3);
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
        _subPropertySection.setText(Messages.AnnotationPropertyPropertyPage_SubProperties);
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
            String[][] results = new GetSubAnnotationPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, SUB);
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLSubAnnotationPropertyOfAxiom axiom = (OWLSubAnnotationPropertyOfAxiom) OWLUtilities.axiom(axiomText);
                createRow(new LocatedAxiom(axiom, isLocal), axiom.getSubProperty(), ontologyUri, false, SUB);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
        }

        Label createNewLabel = new Label(_subFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.AnnotationPropertyPropertyPage2_3);
        Composite activeComposite = createEmptyRow(true, SUB);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

//    /**
//     * Create equivalent properties area
//     */
//    private void createEquivPropertyArea(Composite composite) {
//        _equivPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
//        _equivPropertySection.setText(Messages.AnnotationPropertyPropertyPage_EquivalentProperties);
//        _equivPropertySection.setLayoutData(new ColumnLayoutData());
//
//        _equivFormComposite = _toolkit.createComposite(_equivPropertySection, SWT.NONE);
//        _equivFormComposite.setLayout(new GridLayout());
//        _toolkit.adapt(_equivFormComposite);
//        _toolkit.adapt(_equivPropertySection);
//        _equivPropertySection.setClient(_equivFormComposite);
//    }

//    private void initEquivSection(boolean setFocus) {
//        clearComposite(_equivFormComposite);
//
//        try {
//            String[][] results = new GetEquivalentAnnotationPropertyHits(_project, _ontologyUri, _id).getResults();
//            TreeSet<String[]> sortedSet = getSortedSet(results, EQUIV);
//
//            String thisProperty = OWLUtilities.toString(OWLUtilities.annotationProperty(IRIUtils.ensureValidIRISyntax(_id)));
//            List<String> sourceOntoList = new ArrayList<String>();
//            List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
//            for (String[] result: sortedSet) {
//                String axiomText = result[0];
//                String ontologyUri = result[1];
//                boolean isLocal = ontologyUri.equals(_ontologyUri);
//
//                sourceOntoList.add(ontologyUri);
//                OWLEquivalentAnnotationPropertiesAxiom equivObjProps = (OWLEquivalentAnnotationPropertiesAxiom) OWLUtilities.axiom(axiomText);
//                axiomList.add(new LocatedAxiom(equivObjProps, isLocal));
//                Set<OWLAnnotationProperty> AnnotationProperties = equivObjProps.getProperties();
//                for (OWLAnnotationProperty prop: AnnotationProperties) {
//                    if (!OWLUtilities.toString(prop).equals(thisProperty)) {
//                        createRow(axiomList, (OWLAnnotationProperty) prop, false, ontologyUri, EQUIV);
//                    }
//                }
//
//            }
//        } catch (NeOnCoreException e1) {
//            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
//        } catch (CommandException e1) {
//            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivPropertySection.getShell());
//        }
//
//        Label createNewLabel = new Label(_equivFormComposite, SWT.NONE);
//        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        createNewLabel.setText(Messages.AnnotationPropertyPropertyPage2_2);
//        Composite activeComposite = createEmptyRow(true, EQUIV);
//        if (setFocus) {
//            activeComposite.setFocus();
//        }
//    }

    @Override
    public void refresh() {
        super.refresh();
        initSuperSection(false);
        initSubSection(false);
//        initEquivSection(false);

        // closeToolBar();
        layoutSections();
        _form.reflow(true);
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_superPropertySection);
        sections.add(_subPropertySection);
//        sections.add(_equivPropertySection);
        return sections;
    }

    private void createRow(LocatedAxiom locatedAxiom, final OWLAnnotationProperty objPropExpr, String ontologyUri, boolean enabled, final int mode) throws NeOnCoreException {
        boolean imported = !locatedAxiom.isLocal();
        Composite parent;
        if (mode == SUPER) {
            parent = _superFormComposite;
        } else 
//            if (mode == SUB) {
            parent = _subFormComposite;
//        } else {
//            parent = _equivFormComposite;
//        }

        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(ontologyUri, _project);
        }
       
        final FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri,sourceOwlModel.getProjectId(),_id);


        PropertyText propertyText = new PropertyText(row.getParent(), _owlModel, sourceOwlModel, PropertyText.ANNOTATION_PROPERTY);
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
                    OWLAnnotationProperty annotationProp = _manager.parseAnnotationProperty(value, _localOwlModel);
                    remove();
                    if (mode == SUPER) {
                        new CreateAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), _id, annotationProp.getIRI().toString()).run();
                        initSubSection(false);
                        initSuperSection(true);
                    } else if (mode == SUB) {
                        new CreateAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), annotationProp.getIRI().toString(), _id).run();
                        initSuperSection(false);
                        initSubSection(true);
//                    } else {
//                        new CreateEquivalentAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), OWLUtilities.toString(annotationProp), value);
//                        initEquivSection(true);
                    }
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
                    textWidget.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
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

    private void createRow(List<LocatedAxiom> axioms, OWLAnnotationProperty property, boolean enabled, String sourceOnto, final int mode) throws NeOnCoreException {
        boolean imported = !sourceOnto.equals(_ontologyUri);
        Composite parent;
        if (mode == SUPER) {
            parent = _superFormComposite;
        } else //if (mode == SUB) {
            parent = _subFormComposite;
//        } else { // EQUIV
//            parent = _equivFormComposite;
//        }

        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project);
        }
       
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, sourceOnto, sourceOwlModel.getProjectId(),_id);

        PropertyText propertyText = new PropertyText(row.getParent(), _owlModel, sourceOwlModel, PropertyText.ANNOTATION_PROPERTY);
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
                    OWLAnnotationProperty annotationProp = _manager.parseAnnotationProperty(value, _localOwlModel);
                    if (mode == SUPER) {
                        new CreateAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), _id, annotationProp.getIRI().toString()).run();
                        initSuperSection(true);
                    } else if (mode == SUB) {
                        new CreateAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), annotationProp.getIRI().toString(), _id).run();
                        initSubSection(true);
//                    } else {
//                        new CreateEquivalentAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), OWLUtilities.toString(annotationProp), value);
//                        initEquivSection(true);
                    }

                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
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
                OWLObjectProperty thisObjectProperty = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id));
                
                OWLAxiomUtils.triggerRemovePressed(owlAxioms, getEntity(), _namespaces, OWLUtilities.toString(thisObjectProperty), _sourceOwlModel, WizardConstants.ADD_DEPENDENT_MODE);
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
        } else //if (mode == SUB) {
            parent = _subFormComposite;
//        } else {
//            parent = _equivFormComposite;
//        }
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);
        final StyledText text = new PropertyText(row.getParent(), _owlModel, _owlModel, PropertyText.ANNOTATION_PROPERTY).getStyledText();
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
                    OWLAnnotationProperty annotationProp = _manager.parseAnnotationProperty(value, _localOwlModel);

                    switch (mode) {
                        case SUPER:
                            new CreateAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), _id, annotationProp.getIRI().toString()).run();
                            initSubSection(false);
                            initSuperSection(true);
                            break;
                        case SUB:
                            new CreateAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), annotationProp.getIRI().toString(), _id).run();
                            initSuperSection(false);
                            initSubSection(true);
                            break;
//                        case EQUIV:
//                            String thisEntity = OWLUtilities.toString(OWLUtilities.annotationProperty(IRIUtils.ensureValidIRISyntax(_id)));
//                            if (!OWLUtilities.toString(annotationProp).equals(thisEntity)) {
//                                new CreateEquivalentAnnotationProperty(_project, _sourceOwlModel.getOntologyURI(), thisEntity, OWLUtilities.toString(annotationProp)).run();
//                                initEquivSection(true);
//                            } else {
//                                String modeString = Messages.AnnotationPropertyTaxonomyPropertyPage_0;
//                                MessageDialog.openWarning(_subFormComposite.getShell(), Messages.AnnotationPropertyPropertyPage2_47, Messages.AnnotationPropertyTaxonomyPropertyPage_1 + " " + modeString + Messages.AnnotationPropertyTaxonomyPropertyPage_2); //$NON-NLS-1$
//                            }
//                            break;
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException ce) {
                    handleException(ce, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertySection.getShell());
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
//        initEquivSection(false);

        layoutSections();
        _form.reflow(true);
    }

    private TreeSet<String[]> getSortedSet(String[][] clazzesArray, final int mode) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    String thisId = AnnotationPropertyTaxonomyPropertyPage.this._id;
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0]);
                    String uri1 = ""; //$NON-NLS-1$
                    String uri2 = ""; //$NON-NLS-1$
                    if (mode == SUPER) {
                        OWLSubAnnotationPropertyOfAxiom subObjProp = (OWLSubAnnotationPropertyOfAxiom) axiom1;
                        OWLAnnotationProperty objProp = subObjProp.getSuperProperty();
                        if (objProp instanceof OWLAnnotationProperty) {
                            uri1 = OWLGUIUtilities.getEntityLabel(((OWLAnnotationProperty) objProp), ontologyUri1, _project); 
                            OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                            OWLSubAnnotationPropertyOfAxiom subObjProp2 = (OWLSubAnnotationPropertyOfAxiom) axiom2;
                            OWLAnnotationProperty objProp2 = subObjProp2.getSuperProperty();
                            if (objProp2 instanceof OWLAnnotationProperty) {
                                uri2 = OWLGUIUtilities.getEntityLabel(((OWLAnnotationProperty) objProp2), ontologyUri2, _project);
                            }
                        }
                    } else if (mode == SUB) {
                        OWLSubAnnotationPropertyOfAxiom subObjProp = (OWLSubAnnotationPropertyOfAxiom) axiom1;
                        OWLAnnotationProperty dpe = subObjProp.getSubProperty();
                        if (dpe instanceof OWLAnnotationProperty) {
                            OWLAnnotationProperty AnnotationProperty = (OWLAnnotationProperty)dpe;
                            uri1 = OWLGUIUtilities.getEntityLabel(AnnotationProperty, ontologyUri1, _project);
                            OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                            OWLSubAnnotationPropertyOfAxiom subObjProp2 = (OWLSubAnnotationPropertyOfAxiom) axiom2;
                            OWLAnnotationProperty ope2 = subObjProp2.getSubProperty(); 
                            if (ope2 instanceof OWLAnnotationProperty) {
                                OWLAnnotationProperty AnnotationProperty2 = (OWLAnnotationProperty)ope2;
                                if (!(AnnotationProperty2.getIRI().toString().equals(thisId))) {
                                    uri2 = OWLGUIUtilities.getEntityLabel(AnnotationProperty2, ontologyUri2, _project);
                                }
                            }
                        }
//                    } else if (mode == EQUIV) {
//                        OWLEquivalentAnnotationPropertiesAxiom eop = (OWLEquivalentAnnotationPropertiesAxiom) axiom1;
//                        Set<OWLAnnotationProperty> equivalentAnnotationProps = eop.getProperties();
//                        for (OWLAnnotationProperty expr: equivalentAnnotationProps) {
//                            if (expr instanceof OWLAnnotationProperty) {
//                                if (!((OWLAnnotationProperty)expr).getIRI().toString().equals(thisId)) {
//                                    uri1 = OWLGUIUtilities.getEntityLabel((OWLAnnotationProperty)expr, ontologyUri1, _project);
//                                }
//                            }
//                        }
//                        
//                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
//                        OWLEquivalentAnnotationPropertiesAxiom eop2 = (OWLEquivalentAnnotationPropertiesAxiom) axiom2;
//                        Set<OWLAnnotationProperty> equivalentAnnotationProps2 = eop2.getProperties();
//                        for (OWLAnnotationProperty expr: equivalentAnnotationProps2) {
//                            if (expr instanceof OWLAnnotationProperty) {
//                                if (!((OWLAnnotationProperty)expr).getIRI().toString().equals(thisId)) {
//                                    uri2 = OWLGUIUtilities.getEntityLabel((OWLAnnotationProperty)expr, ontologyUri1, _project);
//                                }
//                            }
//                        }
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
