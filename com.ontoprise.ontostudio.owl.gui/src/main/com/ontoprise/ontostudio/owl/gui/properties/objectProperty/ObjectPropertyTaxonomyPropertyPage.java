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
import java.util.concurrent.atomic.AtomicReference;

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
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.syntax.manchester.ManchesterSyntaxVisitor;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.EntityRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.PropertyText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateDisjointObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateEquivalentObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateInverseObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateSubPropertyChainOf;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetDisjointObjectPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetEquivalentObjectPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetInverseObjectPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSubObjectPropertyHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSubPropertyChainOfHits;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.GetSuperObjectPropertyHits;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

/**
 * 
 * @author Nico Stieler
 */
public class ObjectPropertyTaxonomyPropertyPage extends AbstractOWLIdPropertyPage {

    private static final int SUPER = 1;
    private static final int SUB = 2;
    private static final int EQUIV = 3;
    private static final int DISJOINT = 6;
    private static final int INVERSE = 4;
    private static final int CHAIN = 5;

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 3;

    /*
     * JFace Forms variables
     */
    private Section _superPropertySection;
    private Section _subPropertySection;
    private Section _subPropertyChainSection;
    private Section _equivPropertySection;
    private Section _disjointPropertySection;
    private Section _inversePropertySection;

    private Composite _superPropertyComposite;
    private Composite _subPropertyComposite;
    private Composite _subPropertyChainComposite;
    private Composite _equivPropertyComposite;
    private Composite _disjointPropertyComposite;
    private Composite _inversePropertyComp;


    public ObjectPropertyTaxonomyPropertyPage() {
        super();
    }

    @Override
    public Composite createContents(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_OBJECT_PROPERTIES_VIEW);
        Composite body = prepareForm(composite);

        createSuperPropertyArea(body);
        createSubPropertyArea(body);
        createEquivPropertyArea(body);
        createDisjointPropertyArea(body);
        createInversePropertyArea(body);
        createSubPropertyChainArea(body);

        _form.reflow(true);
        return body;
    }

    /**
     * Create equivalent/super properties area
     */
    private void createSuperPropertyArea(Composite composite) {
        _superPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _superPropertySection.setText(Messages.ObjectPropertyPropertyPage_SuperProperties);
        _superPropertySection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        ColumnLayoutData data = new ColumnLayoutData();
        _superPropertySection.setLayoutData(data);
        _superPropertyComposite = _toolkit.createComposite(_superPropertySection, SWT.NONE);
        _superPropertyComposite.setLayout(new GridLayout());
        _toolkit.adapt(_superPropertyComposite);
        _superPropertySection.setClient(_superPropertyComposite);
    }

    private void initSuperSection(boolean setFocus) {
        clearComposite(_superPropertyComposite);

        try {
            String[][] results = new GetSuperObjectPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, SUPER);
            
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLSubObjectPropertyOfAxiom axiom = 
                    (OWLSubObjectPropertyOfAxiom) OWLUtilities.axiom(axiomText);
                createRow(new LocatedAxiom(axiom, isLocal), axiom.getSuperProperty(), null, ontologyUri, false, SUPER);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        }

        Label createNewLabel = new Label(_superPropertyComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_6);
        Composite activeComposite = createEmptyRow(true, SUPER);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create subproperties area
     */
    private void createSubPropertyArea(Composite composite) {
        _subPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _subPropertySection.setText(Messages.ObjectPropertyPropertyPage_SubProperties);
        _subPropertySection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        ColumnLayoutData data = new ColumnLayoutData();
        _subPropertySection.setLayoutData(data);
        _subPropertyComposite = _toolkit.createComposite(_subPropertySection, SWT.NONE);
        _subPropertyComposite.setLayout(new GridLayout());
        _toolkit.adapt(_subPropertyComposite);
        _subPropertySection.setClient(_subPropertyComposite);

    }

    /**
     * Create subpropertychain area
     */
    private void createSubPropertyChainArea(Composite composite) {
        _subPropertyChainSection = _toolkit.createSection(composite, Section.DESCRIPTION |Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _subPropertyChainSection.setText(Messages.ObjectPropertyPropertyPage_SubPropertyChains);
        _subPropertyChainSection.setDescription(Messages.ObjectPropertyPropertyPage_SubPropertyChains_tooltip);
        _subPropertyChainSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        ColumnLayoutData data = new ColumnLayoutData();
        _subPropertyChainSection.setLayoutData(data);
        _subPropertyChainComposite = _toolkit.createComposite(_subPropertyChainSection, SWT.NONE);
        _subPropertyChainComposite.setLayout(new GridLayout());
        _toolkit.adapt(_subPropertyChainComposite);
        _subPropertyChainSection.setClient(_subPropertyChainComposite);
    }

    private void initSubSection(boolean setFocus) {
        clearComposite(_subPropertyComposite);
        try {
            String[][] results = new GetSubObjectPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, SUB);
            
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLSubObjectPropertyOfAxiom axiom = 
                    (OWLSubObjectPropertyOfAxiom) OWLUtilities.axiom(axiomText);

                createRow(new LocatedAxiom(axiom, isLocal), axiom.getSubProperty(), null, ontologyUri, false, SUB);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertyComposite.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertyComposite.getShell());
        }

        Label createNewLabel = new Label(_subPropertyComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_6);
        Composite activeComposite = createEmptyRow(true, SUB);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private void initSubPropertyChainSection(boolean setFocus) {
        clearComposite(_subPropertyChainComposite);
        try {
            String[][] results = new GetSubPropertyChainOfHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, CHAIN);

            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLSubPropertyChainOfAxiom axiom = (OWLSubPropertyChainOfAxiom) OWLUtilities.axiom(axiomText);

                createRow(new LocatedAxiom(axiom, isLocal), null, axiom.getPropertyChain(), ontologyUri, false, CHAIN);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertyChainComposite.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subPropertyChainComposite.getShell());
        }

        Label createNewLabel = new Label(_subPropertyChainComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_6);
        Composite activeComposite = createEmptyRow(true, CHAIN);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create equivalent properties area
     */
    private void createEquivPropertyArea(Composite composite) {
        _equivPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _equivPropertySection.setText(Messages.ObjectPropertyPropertyPage_EquivalentProperties);
        _equivPropertySection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _equivPropertySection.setLayoutData(new ColumnLayoutData());

        _equivPropertyComposite = _toolkit.createComposite(_equivPropertySection, SWT.NONE);
        _equivPropertyComposite.setLayout(new GridLayout());
        _toolkit.adapt(_equivPropertyComposite);
        _equivPropertySection.setClient(_equivPropertyComposite);

    }

    private void initEquivSection(boolean setFocus) {
        clearComposite(_equivPropertyComposite);

        try {
            String[][] results = new GetEquivalentObjectPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, EQUIV);

            List<String> sourceOntoList = new ArrayList<String>();
            List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                sourceOntoList.add(ontologyUri);
                OWLEquivalentObjectPropertiesAxiom equivObjProps = 
                    (OWLEquivalentObjectPropertiesAxiom) OWLUtilities.axiom(axiomText);
                axiomList.add(new LocatedAxiom(equivObjProps, isLocal));
                Set<OWLObjectPropertyExpression> objectProperties = equivObjProps.getProperties();

                OWLObjectProperty thisObjectProperty = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id));
                
                for (OWLObjectPropertyExpression prop: objectProperties) {
                    if (!OWLUtilities.toString(prop).equals(OWLUtilities.toString(thisObjectProperty))) {
                        createRow(axiomList, prop, false, ontologyUri,EQUIV);
                    }
                }

            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        }

        Label createNewLabel = new Label(_equivPropertyComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_5);
        Composite activeComposite = createEmptyRow(true, EQUIV);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create disjoint properties area
     */
    private void createDisjointPropertyArea(Composite composite) {
        _disjointPropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _disjointPropertySection.setText(Messages.ObjectPropertyPropertyPage_DisjointProperties);
        _disjointPropertySection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _disjointPropertySection.setLayoutData(new ColumnLayoutData());

        _disjointPropertyComposite = _toolkit.createComposite(_disjointPropertySection, SWT.NONE);
        _disjointPropertyComposite.setLayout(new GridLayout());
        _toolkit.adapt(_disjointPropertyComposite);
        _disjointPropertySection.setClient(_disjointPropertyComposite);

    }

    private void initDisjointSection(boolean setFocus) {
        clearComposite(_disjointPropertyComposite);

        try {
            String[][] results = new GetDisjointObjectPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, DISJOINT);

            List<String> sourceOntoList = new ArrayList<String>();
            List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                sourceOntoList.add(ontologyUri);
                OWLDisjointObjectPropertiesAxiom disjointObjProps = 
                    (OWLDisjointObjectPropertiesAxiom) OWLUtilities.axiom(axiomText);
                axiomList.add(new LocatedAxiom(disjointObjProps, isLocal));
                Set<OWLObjectPropertyExpression> objectProperties = disjointObjProps.getProperties();

                OWLObjectProperty thisObjectProperty = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id));
                
                for (OWLObjectPropertyExpression prop: objectProperties) {
                    if (!OWLUtilities.toString(prop).equals(OWLUtilities.toString(thisObjectProperty))) {
                        createRow(axiomList, prop, false, ontologyUri, DISJOINT);
                    }
                }

            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superPropertySection.getShell());
        }

        Label createNewLabel = new Label(_disjointPropertyComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_5);
        Composite activeComposite = createEmptyRow(true, DISJOINT);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }
    
    /**
     * Create inverse properties area
     */
    private void createInversePropertyArea(Composite composite) {
        _inversePropertySection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _inversePropertySection.setText(Messages.ObjectPropertyPropertyPage_InverseProperties);
        _inversePropertySection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _inversePropertySection.setLayoutData(new ColumnLayoutData());

        _inversePropertyComp = _toolkit.createComposite(_inversePropertySection, SWT.NONE);
        _inversePropertyComp.setLayout(new GridLayout());
        _toolkit.adapt(_inversePropertyComp);
        _inversePropertySection.setClient(_inversePropertyComp);
    }

    private void initInverseSection(boolean setFocus) {
        clearComposite(_inversePropertyComp);
        try {
            String[][] results = new GetInverseObjectPropertyHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, INVERSE);

            List<OWLObjectPropertyExpression> tempList = new ArrayList<OWLObjectPropertyExpression>();
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLInverseObjectPropertiesAxiom axiom = (OWLInverseObjectPropertiesAxiom) OWLUtilities.axiom(axiomText);
                OWLObjectPropertyExpression objectProperty = axiom.getFirstProperty();
                OWLObjectProperty thisObjectProperty = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id));
                
                if (OWLUtilities.toString(objectProperty).equals(OWLUtilities.toString(thisObjectProperty))) {//Alternative: objectProperty.equals(thisObjectProperty)
                    objectProperty = axiom.getSecondProperty();
                }
                if (!tempList.contains(objectProperty)) {
                    tempList.add(objectProperty);
                    createRow(new LocatedAxiom(axiom, isLocal), objectProperty, null, ontologyUri, false, INVERSE);
                }
            }

        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _inversePropertySection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _inversePropertySection.getShell());
        }

        Label createNewLabel = new Label(_inversePropertyComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ObjectPropertyPropertyPage2_5);
        Composite activeComposite = createEmptyRow(true, INVERSE);
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
        initDisjointSection(false);
        initInverseSection(false);
        initSubPropertyChainSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_superPropertySection);
        sections.add(_subPropertySection);
        sections.add(_equivPropertySection);
        sections.add(_disjointPropertySection);
        sections.add(_inversePropertySection);
        sections.add(_subPropertyChainSection);
        return sections;
    }

    /**
     * this is only used for EQUIV and DISJOINT
     * 
     * @param axioms
     * @param property
     * @param enabled
     * @param sourceOnto
     * @throws NeOnCoreException
     */
    private void createRow(List<LocatedAxiom> axioms, OWLObjectPropertyExpression property, boolean enabled, String sourceOnto, final int mode) throws NeOnCoreException {
        boolean imported = !sourceOnto.equals(_ontologyUri);
        Composite parent = _equivPropertyComposite;
        if( mode == DISJOINT)
            parent = _disjointPropertyComposite;
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, sourceOnto,_owlModel.getProjectId(),_id);

        OWLModel sourceOwlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project);
        }else{
            sourceOwlModel =_owlModel;
        }
        PropertyText propertyText = new PropertyText(row.getParent(), _owlModel, sourceOwlModel, PropertyText.OBJECT_PROPERTY);
        final StyledText propertyTextWidget = propertyText.getStyledText();
        propertyTextWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, propertyText);
        final String[] array = getArrayFromDescription(property);
        propertyTextWidget.setText(OWLGUIUtilities.getEntityLabel(array));
        propertyTextWidget.setData(property);
        OWLGUIUtilities.enable(propertyTextWidget, false);
        row.addWidget(propertyTextWidget);

        EntityRowHandler rowHandler = new EntityRowHandler(this, _owlModel, sourceOwlModel, (OWLEntity) property, axioms) {

            @Override
            public void savePressed() {
                // save modified entries
                String value = propertyTextWidget.getText();
                try {
                    OWLObjectPropertyExpression objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                    remove();
                    switch (mode) {
                        case DISJOINT:
                            new CreateDisjointObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, OWLUtilities.toString(objectProp)).run();
                            initDisjointSection(false);
                        case EQUIV:
                        default:
                            new CreateEquivalentObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, OWLUtilities.toString(objectProp)).run();
                            initEquivSection(false);
                            break;
                    }
                        
                    layoutSections();
                    _form.reflow(true);
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, propertyTextWidget.getShell());
                    propertyTextWidget.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, propertyTextWidget.getShell());
                    propertyTextWidget.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(propertyTextWidget, false);
                refresh();
            }

            @Override
            public void removePressed() throws NeOnCoreException {
                remove();
                refresh();
            }
            public void remove() throws NeOnCoreException {
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
                OWLObjectProperty thisObjectProperty = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id));
                OWLAxiomUtils.triggerRemovePressed(owlAxioms, getEntity(), _namespaces, OWLUtilities.toString(thisObjectProperty), _sourceOwlModel, WizardConstants.ADD_DEPENDENT_MODE);
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
                    propertyTextWidget.setText(array[1]);
                } else {
                    propertyTextWidget.setText(array[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    private void createRow(LocatedAxiom locatedAxiom, final OWLObjectPropertyExpression objPropExpr, List<OWLObjectPropertyExpression> propertyChain, String ontologyUri, boolean enabled, final int mode) throws NeOnCoreException {
        OWLAxiom axiom = locatedAxiom.getAxiom();
        boolean imported = !locatedAxiom.isLocal();
        Composite parent;
        if (mode == SUPER) {
            parent = _superPropertyComposite;
        } else if (mode == SUB) {
            parent = _subPropertyComposite;
        } else if (mode == CHAIN) {
            parent = _subPropertyChainComposite;
        } else if (mode == EQUIV) {
            parent = _equivPropertyComposite;
        } else if (mode == DISJOINT) {
            parent = _disjointPropertyComposite;
        } else {
            parent = _inversePropertyComp;
        }

        OWLModel sourceOwlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(ontologyUri, _project);
        }else{
            sourceOwlModel =_owlModel;
        }
        final FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri,sourceOwlModel.getProjectId(),_id);

        final StyledText text = new PropertyText(row.getParent(), _owlModel, sourceOwlModel, PropertyText.OBJECT_PROPERTY).getStyledText();
        final AtomicReference<String[]> array = new AtomicReference<String[]>();
        switch (mode) {
            case SUPER:
            case SUB:
            case EQUIV:
            case DISJOINT:
            case INVERSE:
                array.set(getArrayFromDescription(objPropExpr));
                text.setText(OWLGUIUtilities.getEntityLabel(array.get()));
                OWLGUIUtilities.enable(text, false);
                row.addWidget(text);
                break;

            case CHAIN:
                array.set(new ManchesterSyntaxVisitor(sourceOwlModel).visitSubObjectProperyChain(propertyChain));
                text.setText(OWLGUIUtilities.getEntityLabel(array.get()));
                OWLGUIUtilities.enable(text, false);
                row.addWidget(text);
                break;
            
            default:
                throw new IllegalStateException();
           }
        
        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, new LocatedAxiom(axiom, true)) {

            @Override
            public void savePressed() {
                // save modified entries
                String value = text.getText();
                try {
                    OWLObjectPropertyExpression objectProp;
                    String uri = ""; //$NON-NLS-1$
                    remove();
                    if (mode == SUPER) {
                        objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                        new CreateObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, 
//                                OWLUtilities.toString(objectProp, _localOwlModel.getOntology())
                                IRIUtils.ensureValidIdentifierSyntax(objectProp.toString())
                                ).run();
                        initSubSection(false);
                        initSuperSection(true);
                        
                    } else if (mode == SUB) {
                        objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                        new CreateObjectProperty(_project, _sourceOwlModel.getOntologyURI(), 
//                                OWLUtilities.toString(objectProp, _localOwlModel.getOntology())
                                IRIUtils.ensureValidIdentifierSyntax(objectProp.toString())
                                , _id).run();
                        initSuperSection(false);
                        initSubSection(true);
                        
                    } else if (mode == CHAIN) {
                        List<OWLObjectPropertyExpression> objectPropChain = _manager.parseObjectPropertyChain(value, _localOwlModel);
                        array.set(new ManchesterSyntaxVisitor(_sourceOwlModel).visitSubObjectProperyChain(objectPropChain));
                        text.setText(OWLGUIUtilities.getEntityLabel(array.get()));
                        OWLGUIUtilities.enable(text, false);
                        row.addWidget(text);
                        new CreateSubPropertyChainOf(_project, _sourceOwlModel.getOntologyURI(), 
                                OWLUtilities.toString(objectPropChain), _id).run(); //NICO chain
                        initSubPropertyChainSection(true);

                    } else if (mode == EQUIV) {
                        objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                        if(objectProp instanceof OWLObjectProperty) {
                            OWLObjectProperty op =(OWLObjectProperty)objectProp;
                            uri = OWLUtilities.toString(op);
                        }
                        if (!uri.equals(_id)) { // also true if uri is still ""
                            new CreateEquivalentObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, 
                                    OWLUtilities.toString(objectProp)).run();
                            initEquivSection(true);
                        } else {
                            String modeString = Messages.ObjectPropertyTaxonomyPropertyPage_0;
                            MessageDialog.openWarning(_equivPropertyComposite.getShell(), Messages.DataPropertyPropertyPage2_47, Messages.ObjectPropertyTaxonomyPropertyPage_1 + " " + modeString + Messages.ObjectPropertyTaxonomyPropertyPage_2); //$NON-NLS-1$
                        }

                    } else if (mode == DISJOINT) {
                        objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                        if(objectProp instanceof OWLObjectProperty) {
                            OWLObjectProperty op =(OWLObjectProperty)objectProp;
                            uri = OWLUtilities.toString(op);
                        }
                        if (!uri.equals(_id)) { // also true if uri is still ""
                            new CreateDisjointObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, 
                                    OWLUtilities.toString(objectProp)).run();
                            initDisjointSection(true);
                        } else {
                            String modeString = Messages.ObjectPropertyTaxonomyPropertyPage_0;
                            MessageDialog.openWarning(_disjointPropertyComposite.getShell(), Messages.DataPropertyPropertyPage2_47, Messages.ObjectPropertyTaxonomyPropertyPage_1 + " " + modeString + Messages.ObjectPropertyTaxonomyPropertyPage_2); //$NON-NLS-1$
                        }
                        
                    } else { // inverse
                        objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                        if(objectProp instanceof OWLObjectProperty) {
                            OWLObjectProperty op =(OWLObjectProperty)objectProp;
                            uri = OWLUtilities.toString(op);
                        }
                        if (!uri.equals(_id)) { // also true if uri is still ""
                            new CreateInverseObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, 
                                    OWLUtilities.toString(objectProp)).run();
                            initInverseSection(true);
                        } else {
                            String modeString = Messages.ObjectPropertyTaxonomyPropertyPage_3;
                            MessageDialog.openWarning(_inversePropertyComp.getShell(), Messages.DataPropertyPropertyPage2_47, Messages.ObjectPropertyTaxonomyPropertyPage_4 + " " + modeString + Messages.ObjectPropertyTaxonomyPropertyPage_5); //$NON-NLS-1$
                        }
                    }

                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, text.getShell());
                    text.setFocus();
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
            public void remove() throws NeOnCoreException {
                if (mode == INVERSE) {
                    try {
                        // have to remove in both directions
//                        OWLNamespaces ns = _sourceOwlModel.getNamespaces();
                        OWLDataFactory factory = _sourceOwlModel.getOWLDataFactory();

                        OWLAxiom changingAxiom = factory.getOWLInverseObjectPropertiesAxiom(objPropExpr, 
                                OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id)));
                        new ApplyChanges(_project, _sourceOwlModel.getOntologyURI(), new String[0], new String[] {
                            OWLUtilities.toString(changingAxiom)}).run();

                        OWLAxiom changingAxiom2 = factory.getOWLInverseObjectPropertiesAxiom(
                                OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(_id)), objPropExpr);
                        new ApplyChanges(_project, _sourceOwlModel.getOntologyURI(), new String[0], 
                                new String[] {OWLUtilities.toString(changingAxiom2)}).run();
                    } catch (CommandException ce) {
                        handleException(ce, Messages.ObjectPropertyPropertyPage2_45, text.getShell());
                    }
                } else {
                    super.remove();
                }
                refresh();
            }

            @Override
            public void ensureQName() {
                int mode = OWLGUIUtilities.getEntityLabelMode();
                if (mode == NeOnUIPlugin.DISPLAY_URI || mode == NeOnUIPlugin.DISPLAY_QNAME) {
                    return;
                }

                if (array.get().length > 1) {
                    text.setText(array.get()[1]);
                } else {
                    text.setText(array.get()[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    private Composite createEmptyRow(boolean enabled, final int mode) {
        Composite parent;
        if (mode == SUPER) {
            parent = _superPropertyComposite;
        } else if (mode == SUB) {
            parent = _subPropertyComposite;
        } else if (mode == CHAIN) {
            parent = _subPropertyChainComposite;
        } else if (mode == EQUIV) {
            parent = _equivPropertyComposite;
        } else if (mode == DISJOINT) {
            parent = _disjointPropertyComposite;
        } else {
            parent = _inversePropertyComp;
        }
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);

        final StyledText text = new PropertyText(row.getParent(), _owlModel, PropertyText.OBJECT_PROPERTY).getStyledText();
        row.addWidget(text);
        addSimpleWidget(text);
        
        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                try {
                    String value = text.getText();
                    OWLObjectPropertyExpression objectProp;
                    String uri = ""; //$NON-NLS-1$
                    switch (mode) {
                        case SUPER:
                            objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                            new CreateObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, 
//                                    OWLUtilities.toString(objectProp)
                                    IRIUtils.ensureValidIdentifierSyntax(objectProp.toString())
                                    ).run();
                            initSubSection(false);
                            initSuperSection(true);
                            break;
                            
                        case SUB:
                            objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                            new CreateObjectProperty(_project, _sourceOwlModel.getOntologyURI(), 
//                                    OWLUtilities.toString(objectProp)
                                    IRIUtils.ensureValidIdentifierSyntax(objectProp.toString())
                                    , _id).run();
                            initSuperSection(false);
                            initSubSection(true);
                            break;
                            
                        case CHAIN:
                            List<OWLObjectPropertyExpression> objectPropChain = _manager.parseObjectPropertyChain(value, _localOwlModel);
                            new CreateSubPropertyChainOf(_project, _sourceOwlModel.getOntologyURI(), OWLUtilities.toString(objectPropChain), _id).run();//NICO chain
                            initSubPropertyChainSection(false);
                            break;
                            
                        case EQUIV:
                            objectProp = _manager.parseObjectProperty(value, _localOwlModel);
//                            if(objectProp instanceof OWLObjectProperty) {
//                                OWLObjectProperty op =(OWLObjectProperty)objectProp;
//                                uri = OWLUtilities.toString(op);
//                            }
                            uri = IRIUtils.ensureValidIdentifierSyntax(objectProp.toString());
                            if (!uri.equals(_id)) { // also true if uri is still ""
                                new CreateEquivalentObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, uri).run();
                                initEquivSection(true);
                            } else {
                                MessageDialog.openWarning(_equivPropertyComposite.getShell(), Messages.ObjectPropertyPropertyPage2_45, Messages.ObjectPropertyTaxonomyPropertyPage_1 + Messages.ObjectPropertyTaxonomyPropertyPage_0 + Messages.ObjectPropertyTaxonomyPropertyPage_2);
                            }
                            break;
                            
                        case DISJOINT:
                            objectProp = _manager.parseObjectProperty(value, _localOwlModel);
                            uri = IRIUtils.ensureValidIdentifierSyntax(objectProp.toString());
                            if (!uri.equals(_id)) { // also true if uri is still ""
                                new CreateDisjointObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, uri).run();
                                initDisjointSection(true);
                            } else {
                                MessageDialog.openWarning(_disjointPropertyComposite.getShell(), Messages.ObjectPropertyPropertyPage2_45, Messages.ObjectPropertyTaxonomyPropertyPage_1 + Messages.ObjectPropertyTaxonomyPropertyPage_6 + Messages.ObjectPropertyTaxonomyPropertyPage_2);
                            }
                            break;
                            
                        case INVERSE:
                            objectProp = _manager.parseObjectProperty(value, _localOwlModel);
//                            if(objectProp instanceof OWLObjectProperty) {
//                                OWLObjectProperty op =(OWLObjectProperty)objectProp;
//                                uri = OWLUtilities.toString(op);
//                            }
                            uri = IRIUtils.ensureValidIdentifierSyntax(objectProp.toString());
                            if (!uri.equals(_id)) { // also true if uri is still ""
                                new CreateInverseObjectProperty(_project, _sourceOwlModel.getOntologyURI(), _id, uri).run();
                                initInverseSection(true);
                            } else {
                                MessageDialog.openWarning(_inversePropertyComp.getShell(), Messages.ObjectPropertyPropertyPage2_45, Messages.ObjectPropertyTaxonomyPropertyPage_4 + Messages.ObjectPropertyTaxonomyPropertyPage_3 + Messages.ObjectPropertyTaxonomyPropertyPage_5);
                            }
                            break;
                    }

                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, text.getShell());
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
        initDisjointSection(false);
        initInverseSection(false);
        initSubPropertyChainSection(false);

        layoutSections();
        _form.reflow(true);
    }

    private TreeSet<String[]> getSortedSet(String[][] clazzesArray, final int mode) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    String thisId = ObjectPropertyTaxonomyPropertyPage.this._id;
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0]);
                    String uri1 = ""; //$NON-NLS-1$
                    String uri2 = ""; //$NON-NLS-1$
                    if (mode == SUPER) {
                        OWLSubObjectPropertyOfAxiom subObjProp = (OWLSubObjectPropertyOfAxiom) axiom1;
                        OWLObjectPropertyExpression objProp = subObjProp.getSuperProperty();
                        if (objProp instanceof OWLObjectProperty) {
                            uri1 = OWLGUIUtilities.getEntityLabel(((OWLObjectProperty) objProp), ontologyUri1, _project); 
                            OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                            OWLSubObjectPropertyOfAxiom subObjProp2 = (OWLSubObjectPropertyOfAxiom) axiom2;
                            OWLObjectPropertyExpression objProp2 = subObjProp2.getSuperProperty();
                            if (objProp2 instanceof OWLObjectProperty) {
                                uri2 = OWLGUIUtilities.getEntityLabel(((OWLObjectProperty) objProp2), ontologyUri2, _project);
                            }
                        }
                        
                    } else if (mode == SUB) {
                        OWLSubObjectPropertyOfAxiom subObjProp = (OWLSubObjectPropertyOfAxiom) axiom1;
                        OWLObjectPropertyExpression ope = subObjProp.getSubProperty();
                        if (ope instanceof OWLObjectProperty) {
                            OWLObjectProperty objectProperty = (OWLObjectProperty)ope;
                            if (!(objectProperty.getIRI().toString().equals(thisId))) {
                                uri1 = OWLGUIUtilities.getEntityLabel(objectProperty, ontologyUri1, _project);

                                OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                                OWLSubObjectPropertyOfAxiom subObjProp2 = (OWLSubObjectPropertyOfAxiom) axiom2;
                                OWLObjectPropertyExpression ope2 = subObjProp2.getSubProperty();
                                if (ope2 instanceof OWLObjectProperty) {
                                    OWLObjectProperty objectProperty2 = (OWLObjectProperty)ope2;
                                    if (!(objectProperty2.getIRI().toString().equals(thisId))) {
                                        uri2 = OWLGUIUtilities.getEntityLabel(objectProperty2, ontologyUri2, _project);
                                    }
                                }
                            }
                        }

                    } else if (mode == CHAIN) {
//                        OWLSubPropertyChainOfAxiom subObjPropChainAxiom1 = (OWLSubPropertyChainOfAxiom) axiom1;
//                        List<OWLObjectPropertyExpression> chain1 = subObjPropChainAxiom1.getPropertyChain();

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
//                        OWLSubPropertyChainOfAxiom subObjPropChainAxiom2 = (OWLSubPropertyChainOfAxiom) axiom1;
//                        List<OWLObjectPropertyExpression> chain2 = subObjPropChainAxiom2.getPropertyChain();
                    
                        uri1 = OWLUtilities.toString(axiom1);
                        uri2 = OWLUtilities.toString(axiom2);

                    } else if (mode == EQUIV) {
                        OWLEquivalentObjectPropertiesAxiom eop = (OWLEquivalentObjectPropertiesAxiom) axiom1;
                        Set<OWLObjectPropertyExpression> equivalentObjectProps = eop.getProperties();
                        for (OWLObjectPropertyExpression expr: equivalentObjectProps) {
                            if (expr instanceof OWLObjectProperty) {
                                if (!((OWLObjectProperty)expr).getIRI().toString().equals(thisId)) {
                                    uri1 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)expr, ontologyUri1, _project);
                                }
                            }
                        }
                        
                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                        OWLEquivalentObjectPropertiesAxiom eop2 = (OWLEquivalentObjectPropertiesAxiom) axiom2;
                        Set<OWLObjectPropertyExpression> equivalentObjectProps2 = eop2.getProperties();
                        for (OWLObjectPropertyExpression expr: equivalentObjectProps2) {
                            if (expr instanceof OWLObjectProperty) {
                                if (!((OWLObjectProperty)expr).getIRI().toString().equals(thisId)) {
                                    uri2 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)expr, ontologyUri1, _project);
                                }
                            }
                        }
                    } else if (mode == DISJOINT) {
                        OWLDisjointObjectPropertiesAxiom eop = (OWLDisjointObjectPropertiesAxiom) axiom1;
                        Set<OWLObjectPropertyExpression> disjointObjectProps = eop.getProperties();
                        for (OWLObjectPropertyExpression expr: disjointObjectProps) {
                            if (expr instanceof OWLObjectProperty) {
                                if (!((OWLObjectProperty)expr).getIRI().toString().equals(thisId)) {
                                    uri1 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)expr, ontologyUri1, _project);
                                }
                            }
                        }
                        
                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                        OWLDisjointObjectPropertiesAxiom eop2 = (OWLDisjointObjectPropertiesAxiom) axiom2;
                        Set<OWLObjectPropertyExpression> disjointObjectProps2 = eop2.getProperties();
                        for (OWLObjectPropertyExpression expr: disjointObjectProps2) {
                            if (expr instanceof OWLObjectProperty) {
                                if (!((OWLObjectProperty)expr).getIRI().toString().equals(thisId)) {
                                    uri2 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)expr, ontologyUri1, _project);
                                }
                            }
                        }
                    } else if (mode == INVERSE) {
                        OWLInverseObjectPropertiesAxiom iop = (OWLInverseObjectPropertiesAxiom) axiom1;
                        OWLObjectPropertyExpression first = iop.getFirstProperty();
                        OWLObjectPropertyExpression second = iop.getSecondProperty();
                        if (((OWLObjectProperty)first).getIRI().toString().equals(thisId)) {
                            uri1 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)second, ontologyUri1, _project);
                        } else {
                            uri1 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)first, ontologyUri1, _project);
                        }
                        
                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                        OWLInverseObjectPropertiesAxiom iop2 = (OWLInverseObjectPropertiesAxiom) axiom2;
                        
                        OWLObjectPropertyExpression first2 = iop2.getFirstProperty();
                        OWLObjectPropertyExpression second2 = iop2.getSecondProperty();
                        if (((OWLObjectProperty)first2).getIRI().toString().equals(thisId)) {
                            uri2 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)second2, ontologyUri1, _project);
                        } else {
                            uri2 = OWLGUIUtilities.getEntityLabel((OWLObjectProperty)first2, ontologyUri1, _project);
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
