/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.clazz;

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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.DescriptionRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DescriptionText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetDisjointClazzHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetEquivalentClazzHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetEquivalentClazzHitsWithoutRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetEquivalentRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSubDescriptionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSuperDescriptionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSuperDescriptionHitsWithoutRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSuperRestrictionHits;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
/**
 * 
 * @author Nico Stieler
 */
public class ClazzTaxonomyPropertyPage2 extends AbstractOWLIdPropertyPage {

    private static final int SUPER_MODE = 0;
    private static final int SUB_MODE = 1;
    private static final int EQUIV_MODE = 2;
    private static final int DISJOINT_MODE = 4;

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 3;

    /*
     * JFace Forms variables
     */
    private Section _superClazzesSection;
    private Section _subClazzesSection;
    private Section _equivClazzesSection;
    private Section _disjointClazzesSection;

    private Composite _superClazzesComp;
    private Composite _subClazzesComp;
    private Composite _equivClazzesComp;
    private Composite _disjointClazzesComp;

    public ClazzTaxonomyPropertyPage2() {
        super();
    }

    @Override
    public Composite createContents(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_CLASSES_VIEW);
        Composite body = prepareForm(composite);

        createSuperDescriptionsArea(body);
        createSubDescriptionsArea(body);
        createEquivalentDescriptionsArea(body);
        createDisjointClassesArea(body);
        _form.reflow(true);
        return body;
    }

    @Override
    public void refresh() {
        super.refresh();
        initSuperDescriptionsSection(false);
        initSubDescriptionsSection(false);
        initEquivalentDescriptionsSection(false);
        initDisjointDescriptionsSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void update() {
        super.update();
        initSuperDescriptionsSection(false);
        initSubDescriptionsSection(false);
        initEquivalentDescriptionsSection(false);
        initDisjointDescriptionsSection(false);

        layoutSections();
        _form.reflow(true);
    }

    /**
     * Create descriptions area for super classes
     */
    private void createSuperDescriptionsArea(Composite composite) {
        _superClazzesSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);

        _superClazzesSection.setText(Messages.ClazzPropertyPage_SuperClazzes);
        _superClazzesSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _superClazzesComp = _toolkit.createComposite(_superClazzesSection, SWT.NONE);
        _superClazzesComp.setLayout(new GridLayout());
        _superClazzesComp.setLayoutData(new ColumnLayoutData());

        _toolkit.adapt(_superClazzesComp);
        _superClazzesSection.setClient(_superClazzesComp);
    }

    private void initSuperDescriptionsSection(boolean setFocus) {
        closeAllToolbars();
        clearComposite(_superClazzesComp);

        try {
            String[][] superDescriptionHits = getSuperDescriptionHits();
            TreeSet<String[]> sortedSet = getSortedSet(superDescriptionHits);

            for (String[] hit: sortedSet) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                boolean imported = !ontologyUri.equals(_ontologyUri);
                OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom) OWLUtilities.axiom(axiomText, _owlModel.getOntology());
                LocatedAxiom locatedAxiom = new LocatedAxiom(axiom, !imported);
                OWLClassExpression superDescription = axiom.getSuperClass();
                createSuperOrSubRow(_superClazzesComp, superDescription, locatedAxiom, ontologyUri, false, SUPER_MODE);
            }
        } catch (NeOnCoreException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superClazzesComp.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superClazzesComp.getShell());
        }
        Label createNewLabel = new Label(_superClazzesComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ClazzPropertyPage2_2);
        Composite activeComposite = createEmptyRow(_superClazzesComp, SUPER_MODE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    /**
     * Create descriptions area for sub classes
     */
    private void createSubDescriptionsArea(Composite composite) {
        _subClazzesSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _subClazzesSection.setText(Messages.ClazzPropertyPage_SubClazzes);
        _subClazzesSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _subClazzesComp = _toolkit.createComposite(_subClazzesSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 2;
        _subClazzesComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _subClazzesComp.setLayoutData(data);

        _toolkit.adapt(_subClazzesComp);
        _subClazzesSection.setClient(_subClazzesComp);
    }

    private void initSubDescriptionsSection(boolean setFocus) {
        closeAllToolbars();
        clearComposite(_subClazzesComp);

        try {
            String[][] subDescriptionHits = getSubDescriptionHits();
            TreeSet<String[]> sortedSet = getSortedSet(subDescriptionHits);
            
            for (String[] hit: sortedSet) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                boolean imported = !ontologyUri.equals(_ontologyUri);
                OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom) OWLUtilities.axiom(axiomText, _owlModel.getOntology());
                LocatedAxiom locatedAxiom = new LocatedAxiom(axiom, !imported);
                OWLClassExpression subDescription = axiom.getSubClass();
                createSuperOrSubRow(_subClazzesComp, subDescription, locatedAxiom, ontologyUri, false, SUB_MODE);
            }
        } catch (NeOnCoreException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subClazzesComp.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _subClazzesComp.getShell());
        }
        Label createNewLabel = new Label(_subClazzesComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ClazzPropertyPage2_2);
        Composite activeComposite = createEmptyRow(_subClazzesComp, SUB_MODE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    protected String[][] getSuperDescriptionHits() throws CommandException {
    	if(OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_RESTRICTION_IN_CLASS_TAXONOMY_TAB)){
            _superClazzesSection.setText(Messages.ClazzPropertyPage_SuperClazzes);
            return new GetSuperDescriptionHits(_project, _ontologyUri, _id).getResults();
    	}else{
            try{
                int counter = 0;
                 try {
                     GetSuperRestrictionHits getSuperRestrictionHits = new GetSuperRestrictionHits(_project, _ontologyUri, _id);
                    if(getSuperRestrictionHits != null){
                        String[][] value = getSuperRestrictionHits.getResults();
                        if(value != null){
                            counter = value.length;
                        }
                    }
                } catch (CommandException e1) {
                    //nothing to do
                }
                if(counter != 0){
                    String text = Messages.ClazzPropertyPage_SuperClazzes_Without_Restriction_0;
                    text += counter;
                    text += " "; //$NON-NLS-1$
                    if(counter > 1){
                        text += Messages.ClazzPropertyPage_SuperClazzes_Without_Restriction_1;
                    }else{
                        text += Messages.ClazzPropertyPage_SuperClazz_Without_Restriction_1;
                    }
                    _superClazzesSection.setText(text);
                }
            }catch(Exception e){
                //nothing to do
            }
            return new GetSuperDescriptionHitsWithoutRestrictionHits(_project, _ontologyUri, _id).getResults();
    	}
    }

    protected String[][] getSubDescriptionHits() throws CommandException {
        return new GetSubDescriptionHits(_project, _ontologyUri, _id).getResults();
    }

    /**
     * Create descriptions area for equivalent classes
     */
    private void createEquivalentDescriptionsArea(Composite composite) {
        _equivClazzesSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _equivClazzesSection.setText(Messages.ClazzPropertyPage_EquivalentClazzes);
        _equivClazzesSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _equivClazzesComp = _toolkit.createComposite(_equivClazzesSection, SWT.NONE);
        _equivClazzesComp.setLayout(new GridLayout());
        ColumnLayoutData data = new ColumnLayoutData();
        _equivClazzesComp.setLayoutData(data);

        _toolkit.adapt(_equivClazzesComp);
        _equivClazzesSection.setClient(_equivClazzesComp);
    }

    private void initEquivalentDescriptionsSection(boolean setFocus) {
        closeAllToolbars();
        clearComposite(_equivClazzesComp);

        try {
            String[][] equivalentClassHits = getEquivalentClazzHits();
            TreeSet<String[]> sortedSet = getSortedSet(equivalentClassHits);

            for (String[] hit: sortedSet) {
                List<String> sourceOntoList = new ArrayList<String>();
                List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
                String axiomText = hit[0];
                String ontologyUri = hit[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                sourceOntoList.add(ontologyUri);
                OWLEquivalentClassesAxiom equivalentClazzes = 
                    (OWLEquivalentClassesAxiom) OWLUtilities.axiom(axiomText, _owlModel.getOntology());
                axiomList.add(new LocatedAxiom(equivalentClazzes, isLocal));
                Set<OWLClassExpression> descriptions = equivalentClazzes.getClassExpressions();
                for (OWLClassExpression desc: descriptions) {
                    if (!desc.equals(getClazzDescription())) {
                        createDisjointOrEquivalentRow(_equivClazzesComp, desc, axiomList, !isLocal, false, EQUIV_MODE, ontologyUri);
                    }
                }
            }
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superClazzesComp.getShell());
        } catch (NeOnCoreException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superClazzesComp.getShell());
        }

        Label createNewLabel = new Label(_equivClazzesComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.ClazzPropertyPage2_3);
        Composite activeComposite = createEmptyRow(_equivClazzesComp, EQUIV_MODE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    protected String[][] getEquivalentClazzHits() throws CommandException {
        if(OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_RESTRICTION_IN_CLASS_TAXONOMY_TAB)){
            _equivClazzesSection.setText(Messages.ClazzPropertyPage_EquivalentClazzes);
            return new GetEquivalentClazzHits(_project, _ontologyUri, _id).getResults();
        }else{
            try{
                int counter = 0;
                 try {
                    GetEquivalentRestrictionHits getEquivalentRestrictionHits = new GetEquivalentRestrictionHits(_project, _ontologyUri, _id);
                    if(getEquivalentRestrictionHits != null){
                        String[][] value = getEquivalentRestrictionHits.getResults();
                        if(value != null){
                            counter = value.length;
                        }
                    }
                } catch (CommandException e1) {
                    //nothing to do
                }
                if(counter != 0){
                    String text = Messages.ClazzPropertyPage_EquivalentClazzes_Without_Restriction_0;
                    text += counter;
                    text += " "; //$NON-NLS-1$
                    if(counter > 1){
                        text += Messages.ClazzPropertyPage_EquivalentClazzes_Without_Restriction_1;
                    }else{
                        text += Messages.ClazzPropertyPage_EquivalentClazz_Without_Restriction_1;
                    }
                    _equivClazzesSection.setText(text);
                }
            }catch(Exception e){
                //nothing to do
            }
            return new GetEquivalentClazzHitsWithoutRestrictionHits(_project, _ontologyUri, _id).getResults();
        }
    }

    protected String[][] getDisjointDescriptionHits() throws CommandException {
        return new GetDisjointClazzHits(_project, _ontologyUri, _id).getResults();
    }

    /**
     * Create disjoint classes area
     */
    private void createDisjointClassesArea(Composite composite) {
        _disjointClazzesSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _disjointClazzesSection.setText(Messages.ClazzPropertyPage_DisjointClasses);
        _disjointClazzesSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _disjointClazzesComp = _toolkit.createComposite(_disjointClazzesSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        _disjointClazzesComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _disjointClazzesComp.setLayoutData(data);

        _toolkit.adapt(_disjointClazzesComp);
        _disjointClazzesSection.setClient(_disjointClazzesComp);
    }

    private void initDisjointDescriptionsSection(boolean setFocus) {
        closeAllToolbars();
        clearComposite(_disjointClazzesComp);

        try {
            String[][] disjointDescriptionHitsArray = getDisjointDescriptionHits();
            TreeSet<String[]> sortedSet = getSortedSet(disjointDescriptionHitsArray);

            for (String[] hit: sortedSet) {
                List<String> sourceOntoList = new ArrayList<String>();
                List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
                String axiomText = hit[0];
                String ontologyUri = hit[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                sourceOntoList.add(ontologyUri);
                OWLDisjointClassesAxiom disjointClazzes = 
                    (OWLDisjointClassesAxiom) OWLUtilities.axiom(axiomText, _owlModel.getOntology());
                axiomList.add(new LocatedAxiom(disjointClazzes, isLocal));
                Set<OWLClassExpression> descriptions = disjointClazzes.getClassExpressions();

                for (OWLClassExpression desc: descriptions) {
                    if (!desc.equals(getClazzDescription())) {
                        createDisjointOrEquivalentRow(_disjointClazzesComp, desc, axiomList, !isLocal, false, DISJOINT_MODE, ontologyUri);
                    }
                }
            }
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superClazzesComp.getShell());
        } catch (NeOnCoreException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _superClazzesComp.getShell());
        }

        Label createNewLabel = new Label(_disjointClazzesComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.DisjointClazzesPropertyPage2_1);
        Composite activeComposite = createEmptyRow(_disjointClazzesComp, DISJOINT_MODE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private Composite createEmptyRow(Composite parent, final int mode) {
        final EmptyFormRow formRow = new EmptyFormRow(_toolkit, parent, NUM_COLS);

        DescriptionText descriptionText = new DescriptionText(formRow.getParent(), _owlModel, _owlModel, false, _toolkit);
        final StyledText clazzText = descriptionText.getStyledText();
        addComplexText(descriptionText);
        formRow.addWidget(clazzText);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                try {
                    OWLDataFactory factory = _sourceOwlModel.getOWLDataFactory();
                    OWLAxiom newAxiom = null;
                    String value = clazzText.getText();
                    String modeString = ""; //$NON-NLS-1$
                    if (mode == SUPER_MODE || mode == EQUIV_MODE || mode == DISJOINT_MODE) {
                        OWLClassExpression thisClazzDesc = getClazzDescription();
                        OWLClassExpression thatClazzDesc = _manager.parseDescription(value, _localOwlModel);
                        switch (mode) {
                            case SUPER_MODE:
                                newAxiom = factory.getOWLSubClassOfAxiom(thisClazzDesc, thatClazzDesc);
                                break;
                            case EQUIV_MODE:
                                if (!OWLUtilities.toString(thisClazzDesc, _localOwlModel.getOntology()).
                                        equals(OWLUtilities.toString(thatClazzDesc, _localOwlModel.getOntology()))) {
                                    newAxiom = factory.getOWLEquivalentClassesAxiom(thisClazzDesc, thatClazzDesc);
                                } else {
                                    modeString = Messages.ClazzTaxonomyPropertyPage2_0;
                                }
                                break;
                            case DISJOINT_MODE:
                                if (!OWLUtilities.toString(thisClazzDesc, _localOwlModel.getOntology()).
                                        equals(OWLUtilities.toString(thatClazzDesc, _localOwlModel.getOntology()))) {
                                    newAxiom = factory.getOWLDisjointClassesAxiom(thisClazzDesc, thatClazzDesc);
                                } else {
                                    modeString = Messages.ClazzTaxonomyPropertyPage2_1;
                                }
                                break;
                            default:
                                // cannot reach this point.
                                return;
                        }
                    } else { // mode == SUB_MODE
                        OWLClassExpression subClazzDesc = _manager.parseDescription(value, _localOwlModel);
                        OWLClassExpression superClazzDesc = getClazzDescription();
                        newAxiom = factory.getOWLSubClassOfAxiom(subClazzDesc, superClazzDesc);
                    }

                    if (newAxiom != null) {
                        new ApplyChanges(_project, _ontologyUri, new String[] {OWLUtilities.toString(newAxiom, _localOwlModel.getOntology())}, new String[0]).run();
                    } else {
                        MessageDialog.openWarning(_disjointClazzesComp.getShell(), Messages.ClazzTaxonomyPropertyPage2_ApplyChanges, Messages.ClazzTaxonomyPropertyPage2_2 + " " + modeString + Messages.ClazzTaxonomyPropertyPage2_3); //$NON-NLS-1$ 
                    }

                    OWLGUIUtilities.enable(clazzText, false);

                    switch (mode) {
                        case SUB_MODE:
                            initSubDescriptionsSection(true);
                            initSuperDescriptionsSection(false);
                            break;
                        case SUPER_MODE:
                            initSuperDescriptionsSection(true);
                            initSubDescriptionsSection(false);
                            break;
                        case EQUIV_MODE:
                            initEquivalentDescriptionsSection(true);
                            break;
                        case DISJOINT_MODE:
                            initDisjointDescriptionsSection(true);
                            break;
                        default:
                            // cannot reach this point.
                            return;
                    }

                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_52, clazzText.getShell());
                    clazzText.setFocus();
                } catch (CommandException e) {
                    handleException(e, Messages.ClazzPropertyPage2_52, clazzText.getShell());
                    clazzText.setFocus();
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
                OWLClassExpression desc = null;
                try {
                    desc = OWLUtilities.description(clazzText.getText(), _owlModel.getOntology());
                } catch (NeOnCoreException e) {
                }
                if (desc != null) {
                    String[] array = getArrayFromDescription(desc);
                    clazzText.setText(array[2]);
                }
            }

        };
        formRow.init(rowHandler);

        clazzText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (clazzText.getText().trim().length() == 0) {
                    formRow.getCancelButton().setEnabled(false);
                    formRow.getAddButton().setEnabled(false);
                } else {
                    formRow.getCancelButton().setEnabled(true);
                    formRow.getAddButton().setEnabled(true);
                }
            }
        });
        return clazzText;
    }

    /**
     * Is called from within
     * <ul>
     * <li>super classes section</li>
     * <li>sub classes section</li>
     * </ul>
     * 
     * @param parent
     * @param locatedItem
     * @param locatedAxiom
     * @param enabled
     * @param mode
     * @throws NeOnCoreException
     */
    private void createSuperOrSubRow(Composite parent, OWLClassExpression description, final LocatedAxiom locatedAxiom, String ontologyUri, boolean enabled, final int mode) throws NeOnCoreException {
        boolean imported = !locatedAxiom.isLocal();
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri,_owlModel.getProjectId(),_id);

        final String[] array = getArrayFromDescription(description);
        String id = OWLGUIUtilities.getEntityLabel(array);

        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(ontologyUri, _project);
        }
        DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, sourceOwlModel, imported, _toolkit);
        final StyledText clazzText = descriptionText.getStyledText();
        addComplexText(descriptionText);
        clazzText.setText(id);
        clazzText.setData(description);
        clazzText.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, descriptionText);
        OWLGUIUtilities.enable(clazzText, false);
        row.addWidget(clazzText);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, locatedAxiom) {
            @Override
            public void savePressed() {
                // save modified entries
                String value = clazzText.getText();
                try {
                    OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(_project);
                    OWLAxiom axiomToRemove = locatedAxiom.getAxiom();
                    OWLAxiom axiomToAdd;
                    OWLClassExpression id = getClazzDescription();

//                    value = _manager.parseUri(value, _owlModel);
//                    System.out.println(value);
                    if (mode == SUPER_MODE) {
                        OWLClassExpression superClazzDesc = _manager.parseDescription(value, _localOwlModel);
                        axiomToAdd = factory.getOWLSubClassOfAxiom(id, superClazzDesc);
                    } else if (mode == SUB_MODE) {
                        OWLClassExpression subClazzDesc = _manager.parseDescription(value, _localOwlModel);
                        axiomToAdd = factory.getOWLSubClassOfAxiom(subClazzDesc, id);
                    } else {
                        throw new IllegalArgumentException(Messages.ClazzTaxonomyPropertyPage2_4);
                    }

                    if (axiomToRemove != null) {
                        OWLOntology ontology =  _localOwlModel.getOntology();
                        new ApplyChanges(_project, _sourceOwlModel.getOntologyURI(), new String[] {OWLUtilities.toString(axiomToAdd, ontology)}, new String[] {OWLUtilities.toString(axiomToRemove, ontology)}).run();
                    }

                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_45, clazzText.getShell());
                } catch (CommandException e) {
                    handleException(e, Messages.ClazzPropertyPage2_45, clazzText.getShell());
                }
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

                if (array.length > 1) {
                    clazzText.setText(array[1]);
                } else {
                    clazzText.setText(array[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    /**
     * Is called from within
     * <ul>
     * <li>disjoint classes section</li>
     * <li>equivalent classes section</li>
     * </ul>
     * 
     * @param parent
     * @param restriction
     * @param oldAxiom
     * @param enabled
     * @param mode
     * @throws NeOnCoreException
     */
    private void createDisjointOrEquivalentRow(Composite parent, final OWLClassExpression description, final List<LocatedAxiom> axioms, boolean imported, boolean enabled, final int mode, String sourceOnto) throws NeOnCoreException {
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, sourceOnto,_owlModel.getProjectId(),_id);
        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project);
        }
         
        final String[] descriptionArray = getArrayFromDescription(description);
        String id = OWLGUIUtilities.getEntityLabel(descriptionArray);
        DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, sourceOwlModel, imported, _toolkit);
        final StyledText clazzText = descriptionText.getStyledText();
        addComplexText(descriptionText);

        clazzText.setText(id);
        clazzText.setData(description);
        clazzText.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, descriptionText);
        OWLGUIUtilities.enable(clazzText, false);
        row.addWidget(clazzText);

        DescriptionRowHandler rowHandler = new DescriptionRowHandler(this, _owlModel, sourceOwlModel, descriptionArray, axioms) {

            @Override
            public void savePressed() {
                String value = clazzText.getText();
                try {
                    OWLDataFactory factory = _sourceOwlModel.getOWLDataFactory();
                    OWLAxiom axiomToAdd;

                    OWLClassExpression id = getClazzDescription();
                    OWLClassExpression equivOrDisjointClazzDesc = _manager.parseDescription(value, _localOwlModel);
                    switch (mode) {
                        case EQUIV_MODE:
                            axiomToAdd = factory.getOWLEquivalentClassesAxiom(id, equivOrDisjointClazzDesc);
                            break;
                        case DISJOINT_MODE:
                            axiomToAdd = factory.getOWLDisjointClassesAxiom(id, equivOrDisjointClazzDesc);
                            break;
                        default:
                            // cannot reach this point.
                            return;
                    }

                    for (LocatedAxiom axiom: getAxioms()) {
                        if (axiom != null) {
                            OWLAxiom axiomToRemove = axiom.getAxiom();
                            OWLOntology ontology =  _localOwlModel.getOntology();
                            new ApplyChanges(_project, _sourceOwlModel.getOntologyURI(), new String[] {OWLUtilities.toString(axiomToAdd, ontology)}, new String[] {OWLUtilities.toString(axiomToRemove, ontology)}).run();
                        } else {
                            String[] axiomsToRemove = new String[0];
                            if (mode == DISJOINT_MODE) {
                                OWLClassExpression targetClazzDesc = _manager.parseDescription(clazzText.getText(), _localOwlModel);
                                axiomsToRemove = new String[] {OWLUtilities.toString(factory.getOWLDisjointClassesAxiom(id, targetClazzDesc), _localOwlModel.getOntology())};
                            } else if (mode == EQUIV_MODE) {
                                OWLClassExpression eqClazzDesc = (OWLClassExpression) clazzText.getData();
                                axiomsToRemove = new String[] {OWLUtilities.toString(factory.getOWLEquivalentClassesAxiom(id, eqClazzDesc), _localOwlModel.getOntology())};
                            }
                            OWLOntology ontology =  _localOwlModel.getOntology();
                            new ApplyChanges(_project, _sourceOwlModel.getOntologyURI(), new String[] {OWLUtilities.toString(axiomToAdd, ontology)}, axiomsToRemove).run();
                        }
                    }
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_45, clazzText.getShell());
                } catch (CommandException e) {
                    handleException(e, Messages.ClazzPropertyPage2_45, clazzText.getShell());
                }
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
                        owlAxioms.add(a.getAxiom());
                    }
                }
                OWLOntology ontology = _sourceOwlModel.getOntology();
                String thisClass =  OWLUtilities.toString(OWLUtilities.description(IRIUtils.ensureValidIRISyntax(_id), ontology), ontology);
                OWLAxiomUtils.triggerRemovePressed(owlAxioms, OWLUtilities.toString(description, ontology), _namespaces, thisClass, _sourceOwlModel);
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

                if (descriptionArray.length > 1) {
                    clazzText.setText(descriptionArray[1]);
                } else {
                    clazzText.setText(descriptionArray[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_superClazzesSection);
        sections.add(_subClazzesSection);
        sections.add(_equivClazzesSection);
        sections.add(_disjointClazzesSection);
        return sections;
    }

    /**
     * @return the classDescription that is represented by this EPV
     * 
     * @throws NeOnCoreException
     */
    protected OWLClassExpression getClazzDescription() throws NeOnCoreException {
        return OWLUtilities.description(IRIUtils.ensureValidIRISyntax(_id), _owlModel.getOntology());
    }

    private TreeSet<String[]> getSortedSet(String[][] clazzesArray) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0], _owlModel.getOntology());
                    String propertyUri1 = ""; //$NON-NLS-1$
                    String propertyUri2 = ""; //$NON-NLS-1$
                    if (axiom1 instanceof OWLEquivalentClassesAxiom) {
                        OWLEquivalentClassesAxiom clazzes = (OWLEquivalentClassesAxiom) axiom1;
                        Set<OWLClassExpression> descriptions = clazzes.getClassExpressions();
                        for (OWLClassExpression desc: descriptions) {
                            OWLClassExpression thisDescription = ClazzTaxonomyPropertyPage2.this.getClazzDescription();
                            if (thisDescription != null && !thisDescription.equals(desc)) {
                                propertyUri1 = OWLGUIUtilities.getUriForSorting(desc, _owlModel);
                                break;
                            }
                        }

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _owlModel.getOntology());
                        if (axiom2 instanceof OWLEquivalentClassesAxiom) {
                            clazzes = (OWLEquivalentClassesAxiom) axiom2;
                            descriptions = clazzes.getClassExpressions();
                            for (OWLClassExpression desc: descriptions) {
                                OWLClassExpression thisDescription = ClazzTaxonomyPropertyPage2.this.getClazzDescription();
                                if (thisDescription != null && !thisDescription.equals(desc)) {
                                    propertyUri2 = OWLGUIUtilities.getUriForSorting(desc, _owlModel);
                                    break;
                                }
                            }
                        }

                    } else if (axiom1 instanceof OWLSubClassOfAxiom) {
                        OWLClassExpression thisDescription = ClazzTaxonomyPropertyPage2.this.getClazzDescription();
                        OWLClassExpression desc1 = ((OWLSubClassOfAxiom) axiom1).getSuperClass();

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _owlModel.getOntology());
                        OWLClassExpression desc2 = ((OWLSubClassOfAxiom) axiom2).getSuperClass();
                        if (desc1.equals(thisDescription)) {
                            desc1 = ((OWLSubClassOfAxiom) axiom1).getSubClass();
                            desc2 = ((OWLSubClassOfAxiom) axiom2).getSubClass();
                        }
                        propertyUri1 = OWLGUIUtilities.getUriForSorting(desc1, _owlModel);
                        propertyUri2 = OWLGUIUtilities.getUriForSorting(desc2, _owlModel);
                        
                    } else if (axiom1 instanceof OWLDisjointClassesAxiom) {
                        OWLDisjointClassesAxiom clazzes = (OWLDisjointClassesAxiom) axiom1;
                        Set<OWLClassExpression> descs = clazzes.getClassExpressions();
                        for (OWLClassExpression desc: descs) {
                            OWLClassExpression thisDescription = ClazzTaxonomyPropertyPage2.this.getClazzDescription();
                            if (thisDescription != null && !thisDescription.equals(desc)) {
                                propertyUri1 = OWLGUIUtilities.getUriForSorting(desc, _owlModel);
                                break;
                            }
                        }
                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _owlModel.getOntology());
                        if (axiom2 instanceof OWLDisjointClassesAxiom) {
                            clazzes = (OWLDisjointClassesAxiom) axiom2;
                            descs = clazzes.getClassExpressions();
                            for (OWLClassExpression desc: descs) {
                                OWLClassExpression thisDescription = ClazzTaxonomyPropertyPage2.this.getClazzDescription();
                                if (thisDescription != null && !thisDescription.equals(desc)) {
                                    propertyUri2 = OWLGUIUtilities.getUriForSorting(desc, _owlModel);
                                    break;
                                }
                            }
                        }
                    }

                    int localResult = propertyUri1.compareToIgnoreCase(propertyUri2);
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
