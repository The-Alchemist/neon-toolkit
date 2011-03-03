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

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.neontoolkit.core.util.IRIUtils;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.properties.complexclazz.ComplexClazzPropertyPage2;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.RestrictionOnPropertyWriter;
import com.ontoprise.ontostudio.owl.gui.util.forms.AbstractFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.AbstractRestrictionRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.AbstractRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.DescriptionRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyRestrictionRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.RestrictionRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.AxiomText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DatatypeText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DescriptionText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.IndividualText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.LiteralText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.NumberText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.PropertyText;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.clazz.AddRestriction;
import com.ontoprise.ontostudio.owl.model.commands.clazz.EditRestriction;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetEquivalentRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSuperRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.IsDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.IsObjectProperty;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
/**
 * 
 * @author Nico Stieler
 */

public class ClazzPropertyPage2 extends AbstractOWLMainIDPropertyPage {

    protected static Logger _log = Logger.getLogger(ClazzPropertyPage2.class);

    /*
     * The number of columns for a row (including buttons)
     */
    public static final int NUM_COLS = 7;

    public static final String[] QUANTOR_TYPES = {OWLCommandUtils.SOME, OWLCommandUtils.ONLY, OWLCommandUtils.HAS_VALUE, OWLCommandUtils.HAS_SELF, OWLCommandUtils.AT_LEAST_MIN, OWLCommandUtils.AT_MOST_MAX, OWLCommandUtils.EXACTLY_CARDINALITY};

    /*
     * JFace Forms variables
     */
    private Section _superRestrictionSection;
    private Section _equivRestrictionsSection;

    private Composite _superRestrictionComp;
    private Composite _equivRestrictionsComp;

    public ClazzPropertyPage2() {
        super();
    }

    @Override
    protected void createMainArea(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_CLASSES_VIEW);
        super.createMainArea(composite);
        Composite body = prepareForm(composite);

        createSuperRestrictionsArea(body);
        createEquivalentRestrictionsArea(body);

        _form.reflow(true);
    }

    /**
     * Create area for equivalent restrictions
     */
    private void createEquivalentRestrictionsArea(Composite composite) {
        _equivRestrictionsSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _equivRestrictionsSection.setText(Messages.ClazzPropertyPage_EquivalentRestrictions);
        _equivRestrictionsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _equivRestrictionsComp = _toolkit.createComposite(_equivRestrictionsSection, SWT.NONE);
        _equivRestrictionsComp.setLayout(new GridLayout());
        ColumnLayoutData data = new ColumnLayoutData();
        _equivRestrictionsComp.setLayoutData(data);

        _toolkit.adapt(_equivRestrictionsComp);
        _equivRestrictionsSection.setClient(_equivRestrictionsComp);
    }

    private void initEquivalentRestrictionSection(boolean setFocus) {
        cleanup();
        clearComposite(_equivRestrictionsComp);

        try {
            String[][] restrictionHitsArray = getEquivalentRestrictionHits();
            TreeSet<String[]> sortedSet = getSortedSet(restrictionHitsArray);
            boolean showAxiomColumn = restrictionHitsArray.length > 0;
            createRowTitles(_equivRestrictionsComp, _equivRestrictionsSection, showAxiomColumn);

            for (String[] hit: sortedSet) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                boolean imported = !ontologyUri.equals(_ontologyUri);
                OWLEquivalentClassesAxiom axiom = 
                    (OWLEquivalentClassesAxiom) OWLUtilities.axiom(axiomText);
                List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
                axiomList.add(new LocatedAxiom(axiom, !imported));
                Set<OWLClassExpression> descriptions = axiom.getClassExpressions();
                for (OWLClassExpression d: descriptions) {
                    if ((d instanceof OWLClass) && ((OWLClass)d).getIRI().toString().equals(_id)) {
                        // ignore, since we show the EPV of this description
                    } else {
                        createRow(_equivRestrictionsComp, d, axiomList, imported, new String[] {ontologyUri}, OWLCommandUtils.EQUIV);
                    }
                }
            }

            Label createNewLabel = new Label(_equivRestrictionsComp, SWT.NONE);
            createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            createNewLabel.setText(Messages.ClazzPropertyPage2_1);
            Composite activeComposite = createEmptyRow(_equivRestrictionsComp, OWLCommandUtils.EQUIV);
            if (setFocus) {
                activeComposite.setFocus();
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivRestrictionsComp.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivRestrictionsComp.getShell());
        }
    }

    private TreeSet<String[]> getSortedSet(String[][] restrictionHitsArray) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0]);
                    String propertyUri1 = ""; //$NON-NLS-1$
                    String propertyUri2 = ""; //$NON-NLS-1$
                    if (axiom1 instanceof OWLEquivalentClassesAxiom) {
                        OWLEquivalentClassesAxiom clazzes = (OWLEquivalentClassesAxiom) axiom1;
                        Set<OWLClassExpression> descriptions = clazzes.getClassExpressions();
                        for (OWLClassExpression desc: descriptions) {
                            OWLClassExpression thisDescription = ClazzPropertyPage2.this.getClazzDescription(); 
                            if (thisDescription != null && !thisDescription.equals(desc)) {
                                propertyUri1 = OWLGUIUtilities.getUriForSorting(desc, _owlModel);
                            }
                        }
                        
                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                        if (axiom2 instanceof OWLEquivalentClassesAxiom) {
                            clazzes = (OWLEquivalentClassesAxiom) axiom2;
                            descriptions = clazzes.getClassExpressions();
                            for (OWLClassExpression desc: descriptions) {
                                OWLClassExpression thisDescription = ClazzPropertyPage2.this.getClazzDescription(); 
                                if (thisDescription != null && !thisDescription.equals(desc)) {
                                    propertyUri2 = OWLGUIUtilities.getUriForSorting(desc, _owlModel);
                                }
                            }
                        }
                        
                    } else if (axiom1 instanceof OWLSubClassOfAxiom) {
                        OWLClassExpression desc1 = ((OWLSubClassOfAxiom)axiom1).getSuperClass();
                        propertyUri1 = OWLGUIUtilities.getUriForSorting(desc1, _owlModel);

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                        OWLClassExpression desc2 = ((OWLSubClassOfAxiom)axiom2).getSuperClass();
                        propertyUri2 = OWLGUIUtilities.getUriForSorting(desc2, _owlModel);
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
        for (String[] hit: restrictionHitsArray) {
            unsortedSet.add(hit);
        }
        sortedSet.addAll(unsortedSet);
        return sortedSet;
    }

    /**
     * Extracted protected method for this, because this has to be overwritten by {@link ComplexClazzPropertyPage2}
     * 
     * @return
     * @throws ControlException
     */
    protected String[][] getSuperRestrictionHits() throws CommandException {
        return new GetSuperRestrictionHits(_project, _ontologyUri, _id).getResults();
    }

    /**
     * Extracted protected method for this, because this has to be overwritten by {@link ComplexClazzPropertyPage2}
     * 
     * @return
     * @throws ControlException
     */
    protected String[][] getEquivalentRestrictionHits() throws CommandException {
        return new GetEquivalentRestrictionHits(_project, _ontologyUri, _id).getResults();
    }

    /**
     * Create area for super restrictions
     */
    private void createSuperRestrictionsArea(Composite composite) {
        // Super Restrictions
        _superRestrictionSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _superRestrictionSection.setText(Messages.ClazzPropertyPage_SuperRestrictions);
        _superRestrictionSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _superRestrictionComp = _toolkit.createComposite(_superRestrictionSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        _superRestrictionComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _superRestrictionComp.setLayoutData(data);

        _toolkit.adapt(_superRestrictionComp);
        _superRestrictionSection.setClient(_superRestrictionComp);
    }

    private void initSuperRestrictionSection(boolean setFocus) {
        cleanup();
        clearComposite(_superRestrictionComp);

        try {
            String[][] restrictionHitsArray = getSuperRestrictionHits();
            TreeSet<String[]> sortedSet = getSortedSet(restrictionHitsArray);
            boolean showAxiomColumn = restrictionHitsArray.length > 0;
            createRowTitles(_superRestrictionComp, _superRestrictionSection, showAxiomColumn);

            for (String[] hit: sortedSet) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                boolean imported = !ontologyUri.equals(_ontologyUri);
                OWLSubClassOfAxiom axiom = 
                    (OWLSubClassOfAxiom) OWLUtilities.axiom(axiomText);
                List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
                axiomList.add(new LocatedAxiom(axiom, !imported));
                OWLClassExpression desc = axiom.getSuperClass();
                
                createRow(_superRestrictionComp, desc, axiomList, imported, new String[] {ontologyUri}, OWLCommandUtils.INCL);
            }

            Label createNewLabel = new Label(_superRestrictionComp, SWT.NONE);
            createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            createNewLabel.setText(Messages.ClazzPropertyPage2_0);
            Composite activeComposite = createEmptyRow(_superRestrictionComp, OWLCommandUtils.INCL);
            if (setFocus) {
                activeComposite.setFocus();
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivRestrictionsComp.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivRestrictionsComp.getShell());
        }
    }

    private void createRowTitles(Composite comp, Section section, boolean showAxiomColumn) {
        Composite rowComp = _toolkit.createComposite(comp);
        GridLayout layout = new GridLayout(NUM_COLS, false);
        layout.marginHeight = 0;
        rowComp.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        rowComp.setLayoutData(layoutData);
        Color color = section.getTitleBarForeground();

        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 200;
        Text l2 = new Text(rowComp, SWT.NONE);
        l2.setText(Messages.ClazzPropertyPage_Restrictions_Property);
        l2.setForeground(color);
        l2.setLayoutData(data);

        data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        data.widthHint = OWLGUIUtilities.QUANTIFIER_COMBO_WIDTH;
        data.horizontalAlignment = GridData.CENTER;
        Text l1 = new Text(rowComp, SWT.NONE);
        l1.setText(Messages.ClazzPropertyPage_Restrictions_Quantor);
        l1.setForeground(color);
        l1.setLayoutData(data);

        data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        data.widthHint = 50;
        data.horizontalAlignment = GridData.CENTER;
        Text l4 = new Text(rowComp, SWT.NONE);
        l4.setText(Messages.ClazzPropertyPage_Restrictions_MinMax);
        l4.setForeground(color);
        l4.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 200;
        Text l3 = new Text(rowComp, SWT.NONE);
        l3.setText(Messages.ClazzPropertyPage_Restrictions_Range);
        l3.setForeground(color);
        l3.setLayoutData(data);

        if (OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS)) {
            data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
            data.widthHint = 130;
            Text l5 = new Text(rowComp, SWT.NONE);
            l5.setText(Messages.ClazzPropertyPage_Restrictions_Axiom);
            l5.setForeground(color);
            l5.setLayoutData(data);
        }

        Button b1 = createEditButton(rowComp, true);
        b1.setVisible(false);

        Button b2 = createRemoveButton(rowComp, true);
        b2.setVisible(false);
    }

    private Composite createEmptyRow(Composite parent, final String clazzType) throws NeOnCoreException {
        final EmptyRestrictionRow formRow = new EmptyRestrictionRow(_toolkit, parent, NUM_COLS);

        String[] quantifiers = QUANTOR_TYPES;

        // property
        final StyledText propertyText = new PropertyText(formRow.getParent(), _owlModel, PropertyText.DATA_PROPERTY | PropertyText.OBJECT_PROPERTY).getStyledText();
        addSimpleWidget(propertyText);
        formRow.addWidget(propertyText);

        // quantifier
        GridData data = new GridData();
        data.widthHint = 111;
        data.verticalAlignment = SWT.TOP;
        final CCombo quantifierCombo = OWLGUIUtilities.createComboWidget(quantifiers, formRow.getParent(), data, SWT.BORDER | SWT.READ_ONLY, true);
        addSimpleWidget(quantifierCombo);
        formRow.addWidget(quantifierCombo);

        // cardinality
        final StyledText cardinalityText = new NumberText(formRow.getParent(), _owlModel).getStyledText();
        addSimpleWidget(cardinalityText);
        cardinalityText.setVisible(false);
        formRow.addWidget(cardinalityText);

        // range
        DescriptionText descriptionText = new DescriptionText(formRow.getParent(), _owlModel, false, _toolkit);
        final StyledText rangeText = descriptionText.getStyledText();
        formRow.addWidget(rangeText);
        formRow.setRangeText(rangeText);
        addComplexText(descriptionText, true);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                try {
                    String[] newValues = getNewValues(quantifierCombo, propertyText, formRow.getRangeText(), cardinalityText);
                    new AddRestriction(_project, _sourceOwlModel.getOntologyURI(), IRIUtils.ensureValidIRISyntax(_id), newValues, clazzType).run();

                    OWLGUIUtilities.enable(quantifierCombo, false);
                    if (clazzType.equals(OWLCommandUtils.EQUIV)) {
                        initEquivalentRestrictionSection(true);
                    } else if (clazzType.equals(OWLCommandUtils.INCL)) {
                        initSuperRestrictionSection(true);
                    }
                    layoutSections();
                    _form.reflow(true);
                    
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_40, _equivRestrictionsComp.getShell());
                } catch (CommandException e) {
                    handleException(e, Messages.ClazzPropertyPage2_40, _equivRestrictionsComp.getShell());
                }
            }

            @Override
            public void ensureQName() {
                // nothing to do
            }

        };
        formRow.init(rowHandler);
        addVerificationListeners(formRow, rowHandler, quantifierCombo, cardinalityText, propertyText, true, false, _ontologyUri);

        return quantifierCombo;
    }

    private void createRow(Composite parent, final OWLClassExpression description, List<LocatedAxiom> axioms, boolean imported, String[] sourceOntos, final String clazzType) throws NeOnCoreException {
        OWLModel sourceOwlModel=_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOntos[0], _project);
        }

        final RestrictionRow row = new RestrictionRow(_toolkit, parent, NUM_COLS, imported, sourceOntos.length > 0 ? sourceOntos[0] : "",sourceOwlModel.getProjectId(),_id); //$NON-NLS-1$
        OWLObjectVisitorEx visitor = _manager.getVisitor(_owlModel, NeOnUIPlugin.getDefault().getIdDisplayStyle());
        final ArrayList<String[]> restrictions = RestrictionOnPropertyWriter.performRestriction(description, _namespaces, visitor, (OWLEntity)_owlObject);

        final String[] propertyArray = restrictions.get(1);
        final String[] rangeArray = restrictions.get(2);

        // property
        PropertyText propertyText = new PropertyText(row.getParent(), _owlModel, sourceOwlModel, PropertyText.DATA_PROPERTY | PropertyText.OBJECT_PROPERTY); 
        final StyledText propertyTextWidget = propertyText.getStyledText();
        propertyTextWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, propertyText);
        OWLGUIUtilities.enable(propertyTextWidget, false);
        row.addWidget(propertyTextWidget);

        propertyTextWidget.setText(OWLGUIUtilities.getEntityLabel(propertyArray));

        // quantifier
        final CCombo quantifierCombo = OWLGUIUtilities.createQuantifierComboBox(row.getParent(), false);
        quantifierCombo.select(quantifierCombo.indexOf(restrictions.get(0)[0]));
        row.addWidget(quantifierCombo);

        // cardinality
        final StyledText cardinalityText = new NumberText(row.getParent(), _owlModel, sourceOwlModel).getStyledText();
        String[] cardArray = restrictions.get(3);
        if (cardArray != null) {
            String card = cardArray[0];
            if (card.trim().length() == 0) {
                cardinalityText.setVisible(false);
            }
            cardinalityText.setText(card);
        } else {
            cardinalityText.setVisible(false);
        }
        OWLGUIUtilities.enable(cardinalityText, false);
        row.addWidget(cardinalityText);

        // range
        final StyledText rangeTextWidget = getRangeText(description, row.getParent(), imported, rangeArray[1]);
        OWLGUIUtilities.enable(rangeTextWidget, false);

        if (rangeArray != null) {//NICO thats a strange hack
            // ignore OWL:Thing
            if (!rangeArray[0].equals(OWLConstants.OWL_THING_URI)) {
                String id = OWLGUIUtilities.getEntityLabel(rangeArray);
                rangeTextWidget.setText(id);
            }
        }
        if(restrictions.get(0)[0].equals(OWLCommandUtils.HAS_SELF)){
            rangeTextWidget.setText(new String());
            rangeTextWidget.setVisible(false);
        }
        row.addWidget(rangeTextWidget);
        row.setRangeText(rangeTextWidget);

        AbstractRowHandler rowHandler = null;

        // need another RowHandler for equivalent classes.
        if (clazzType.equals(OWLCommandUtils.INCL)) {
            rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, axioms.get(0)) {

                @Override
                public void savePressed() {
                    // save modified entries
                    try {
                        String[] values = getNewValues(quantifierCombo, propertyTextWidget, row.getRangeText(), cardinalityText);
                        new EditRestriction(_project, _sourceOwlModel.getOntologyURI(), clazzType, IRIUtils.ensureValidIRISyntax(_id), values, OWLUtilities.toString(description)).run();
                    } catch (NeOnCoreException k2e) {
                        handleException(k2e, Messages.ClazzPropertyPage2_30, _equivRestrictionsComp.getShell());
                        rangeTextWidget.setFocus();
                        return;
                    } catch (CommandException e) {
                        handleException(e, Messages.ClazzPropertyPage2_30, _equivRestrictionsComp.getShell());
                        rangeTextWidget.setFocus();
                        return;
                    }
                    refresh();
                }

                @Override
                public void ensureQName() {
                    int mode = OWLGUIUtilities.getEntityLabelMode();
                    if (mode == NeOnUIPlugin.DISPLAY_URI || mode == NeOnUIPlugin.DISPLAY_QNAME) {
                        return;
                    }

                    if (propertyArray.length > 1) {
                        propertyTextWidget.setText(propertyArray[1]);
                    } else {
                        propertyTextWidget.setText(propertyArray[0]);
                    }
                    if (rangeArray != null) {
                        if (rangeArray.length > 1) {
                            row.getRangeText().setText(rangeArray[1]);
                        } else {
                            row.getRangeText().setText(rangeArray[0]);
                        }
                    }
                }

            };

        } else {
            final String[] descriptionArrayOLD = getArrayFromDescription(description);//NICO maybe this can be used again, but with a new content
            final String[] descriptionArray = new String[]{OWLUtilities.toString(description)};
            rowHandler = new DescriptionRowHandler(this, _owlModel, sourceOwlModel, descriptionArray, axioms) {

                @Override
                public void savePressed() {
                    try {
                        String[] values = getNewValues(quantifierCombo, propertyTextWidget, row.getRangeText(), cardinalityText);
                        new EditRestriction(_project, _ontologyUri, clazzType, IRIUtils.ensureValidIRISyntax(_id), values, OWLUtilities.toString(description)).run();
                    } catch (NeOnCoreException k2e) {
                        handleException(k2e, Messages.ClazzPropertyPage2_30, _equivRestrictionsComp.getShell());
                        rangeTextWidget.setFocus();
                        return;
                    } catch (CommandException e) {
                        handleException(e, Messages.ClazzPropertyPage2_30, _equivRestrictionsComp.getShell());
                        rangeTextWidget.setFocus();
                        return;
                    }
                    refresh();
                }

                @Override
                public void removePressed() {
                    List<LocatedAxiom> locatedAxioms = getAxioms();
                    List<OWLAxiom> owlAxioms = new ArrayList<OWLAxiom>();
                    for (LocatedAxiom a: locatedAxioms) {
                        if (a.isLocal()) {
                            owlAxioms.add(a.getAxiom());
                        }
                    }
                    try {
                        OWLAxiomUtils.triggerRemovePressed(owlAxioms, OWLGUIUtilities.getEntityLabel(getDescriptionArray()), _namespaces, IRIUtils.ensureValidIRISyntax(_id), _sourceOwlModel);
                    } catch (NeOnCoreException e) {
                        throw new RuntimeException(e);
                    }
                    refresh();
                }

                @Override
                public void ensureQName() {
                    int mode = OWLGUIUtilities.getEntityLabelMode();
                    if (mode == NeOnUIPlugin.DISPLAY_URI || mode == NeOnUIPlugin.DISPLAY_QNAME) {
                        return;
                    }

                    if (propertyArray.length > 1) {
                        propertyTextWidget.setText(propertyArray[1]);
                    } else {
                        propertyTextWidget.setText(propertyArray[0]);
                    }
                    if (rangeArray != null) {
                        if (rangeArray.length > 1) {
                            rangeTextWidget.setText(rangeArray[1]);
                        } else {
                            rangeTextWidget.setText(rangeArray[0]);
                        }
                    }
                }

            };
        }
        row.init(rowHandler);

        addVerificationListeners(row, rowHandler, quantifierCombo, cardinalityText, propertyTextWidget, false, imported, sourceOntos[0]);
    }

    private StyledText getRangeText(OWLClassExpression restriction, Composite rowComp, boolean imported, String name) {
        OWLModel sourceOwlModel  =_owlModel;
        if(imported){
            try {
                sourceOwlModel = OWLModelFactory.getOWLModel(_owlModel.getOntologyURI(), _project);//NICO  unsolved: ontoname
//              owlModel = OWLModelFactory.getOWLModel(ontologyUri, _project);
            } catch (NeOnCoreException e) {
                e.printStackTrace();
            }
        }

        if ((restriction instanceof OWLObjectSomeValuesFrom) || 
                (restriction instanceof OWLObjectAllValuesFrom) || 
                (restriction instanceof OWLObjectCardinalityRestriction) || 
                (restriction instanceof OWLObjectHasSelf)) {
            DescriptionText descriptionText = new DescriptionText(rowComp, _owlModel, sourceOwlModel, imported, _toolkit);
            addComplexText(descriptionText, true);
            StyledText text = descriptionText.getStyledText();
            text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, descriptionText);
            return text;
        } else if (restriction instanceof OWLObjectHasValue) {
            IndividualText text = new IndividualText(rowComp, _owlModel, sourceOwlModel);
            StyledText textWidget = text.getStyledText();
            textWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, text);
            return textWidget;
        } else if ((restriction instanceof OWLDataSomeValuesFrom) || 
                (restriction instanceof OWLDataAllValuesFrom) || 
                (restriction instanceof OWLDataCardinalityRestriction)) {
            DatatypeText text = new DatatypeText(rowComp, _owlModel, sourceOwlModel);
            StyledText textWidget = text.getStyledText();
            textWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, text);
            return textWidget;
        } else if (restriction instanceof OWLDataHasValue) {
            LiteralText text = new LiteralText(rowComp, _owlModel, sourceOwlModel);
            StyledText textWidget = text.getStyledText();
            textWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, text);
            return textWidget;
        } else {
            // exception
            return null;
        }
    }

    /**
     * Adds SelectionListeners, that cause warnings to be displayed when required information is missing.
     * 
     * @param rowHandler
     * 
     * @param quantifierCombo
     * @param propertyText
     * @param rangeText
     * @param quantityText
     */
    private void addVerificationListeners(final AbstractRestrictionRow formRow, final AbstractRowHandler rowHandler, final CCombo quantifierCombo, final StyledText cardinalityText, final StyledText propertyText, final boolean emptyRow, final boolean imported, final String sourceOntology) {
        quantifierCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // modify text widgets according to user selection
                String text = quantifierCombo.getText();
                OWLGUIUtilities.enable(cardinalityText, true);
                if (text.equals(OWLCommandUtils.AT_LEAST_MIN) || 
                        text.equals(OWLCommandUtils.AT_MOST_MAX) || 
                        text.equals(OWLCommandUtils.EXACTLY_CARDINALITY)) {
                    cardinalityText.setVisible(true);
                    OWLGUIUtilities.enable(cardinalityText, true);
                    if (cardinalityText.getText().trim().length() == 0 || cardinalityText.getText().equals(Messages.ClazzPropertyPage2_15)) {
                        cardinalityText.setVisible(true);
                    }
                } else {
                    cardinalityText.setVisible(false);
                }
                StyledText rangeText = ((AbstractRestrictionRow) formRow).getRangeText();
                if (text.equals(OWLCommandUtils.HAS_SELF)) {
                    rangeText.setVisible(false);
                }else{
                    rangeText.setVisible(true);
                    OWLGUIUtilities.enable(rangeText, true);
                }
                _form.layout(true);
                initWarnings(formRow, quantifierCombo, cardinalityText, propertyText);
                enableButtons(quantifierCombo, cardinalityText, propertyText, formRow.getRangeText(), formRow.getSubmitButton());
                updateRangeText(formRow, rowHandler, quantifierCombo, cardinalityText, propertyText, emptyRow, "", imported, sourceOntology); //$NON-NLS-1$
            }
        });

        cardinalityText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String quantifier = quantifierCombo.getText();
                if (quantifier.equals(OWLCommandUtils.EXACTLY_CARDINALITY) || quantifier.equals(OWLCommandUtils.AT_LEAST_MIN) || quantifier.equals(OWLCommandUtils.AT_MOST_MAX)) {
                    cardinalityText.setVisible(true);
                }
                if (quantifier.equals(OWLCommandUtils.HAS_SELF)) {
                    ((AbstractRestrictionRow) formRow).getRangeText().setVisible(false);
                }
                initWarnings(formRow, quantifierCombo, cardinalityText, propertyText);
                enableButtons(quantifierCombo, cardinalityText, propertyText, formRow.getRangeText(), formRow.getSubmitButton());
            }
        });

        setListenerForRangeText(formRow, quantifierCombo, cardinalityText, propertyText, formRow.getRangeText());

        propertyText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updateRangeText(formRow, rowHandler, quantifierCombo, cardinalityText, propertyText, emptyRow,"", imported, sourceOntology); //$NON-NLS-1$
            }

        });
    }

    /**
     * @param formRow
     * @param rowHandler
     * @param quantifierCombo
     * @param cardinalityText
     * @param propertyText
     * @param imported 
     */
    private void updateRangeText(final AbstractRestrictionRow formRow, final AbstractRowHandler rowHandler, final CCombo quantifierCombo, final StyledText cardinalityText, final StyledText propertyText, boolean emptyRow, String name, boolean imported, String sourceOntology) {
        if(!OWLCommandUtils.HAS_SELF.equals(quantifierCombo.getText())){
            try {
                OWLModel sourceOwlModel =_owlModel;
                if(imported){
                    sourceOwlModel = OWLModelFactory.getOWLModel(sourceOntology, _project);
                }
    
                String oldRangeValue = ((AbstractRestrictionRow) formRow).getRangeText().getText();
    
                boolean hasValue = false;
                if (OWLCommandUtils.HAS_VALUE.equals(quantifierCombo.getText())) {
                    hasValue = true;
                }
    
                StyledText finalRangeText = null;
                ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
                String input = propertyText.getText();
                if (input.trim().equals("")) { //$NON-NLS-1$
                    return;
                }
                OWLDataProperty prop = manager.parseDataProperty(input, sourceOwlModel);
                boolean isDataProperty = new IsDataProperty(_project, _ontologyUri, prop.getIRI().toString()).isDataProperty();
                if (isDataProperty) {
                    if (hasValue) {
                        finalRangeText = new LiteralText(formRow.getParent(), _owlModel, sourceOwlModel).getStyledText();
                    } else {
                        finalRangeText = new DatatypeText(formRow.getParent(), _owlModel, sourceOwlModel).getStyledText();
                    }
                } else {
                    OWLObjectPropertyExpression objProp = manager.parseObjectProperty(input, sourceOwlModel);
                    boolean isObjectProperty = new IsObjectProperty(_project, _ontologyUri, OWLUtilities.toString(objProp)).isObjectProperty();
                    if (isObjectProperty) {
                        if (hasValue) {
                            finalRangeText = new IndividualText(formRow.getParent(), _owlModel, sourceOwlModel).getStyledText();
                        } else {
                            finalRangeText = new DescriptionText(formRow.getParent(), _owlModel, sourceOwlModel, false, _toolkit).getStyledText();
                        }
                    }
                }
                if (finalRangeText != null) {
                    // have to replace range field for data properties
                    formRow.getRangeText().dispose();
                    formRow.removeWidget(((AbstractRestrictionRow) formRow).getRangeText());
                    
                    if (OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS)) {
                        if (formRow instanceof RestrictionRow) {
                            String axiom = ((RestrictionRow)formRow).getAxiomText().getText();
                            ((RestrictionRow)formRow).getAxiomText().dispose();
                            formRow.removeWidget(((RestrictionRow) formRow).getAxiomText());
                            StyledText newAxiomText = new AxiomText(formRow.getParent(), _owlModel, sourceOwlModel, 4).getStyledText();
                            newAxiomText.setEditable(false);
                            newAxiomText.setText(axiom);
                            ((RestrictionRow) formRow).setAxiomText(newAxiomText);
                            formRow.addWidget(newAxiomText);
                        }
                    }
                    
                    formRow.disposeButton(formRow.getCancelButton());
                    formRow.disposeButton(formRow.getSubmitButton());
                    finalRangeText.setText(oldRangeValue);
                    formRow.setRangeText(finalRangeText);
                    formRow.addWidget(finalRangeText);
                    setListenerForRangeText(formRow, quantifierCombo, cardinalityText, propertyText, finalRangeText);
    
                    final Button addButton = emptyRow ? createAddButton(formRow.getParent(), true) : createSaveButton(formRow.getParent(), true);
                    addButton.addSelectionListener(new SelectionAdapter() {
    
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            if (addButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_SAVE)) {
                                rowHandler.savePressed();
                            } else {
                                rowHandler.addPressed();
                            }
                        }
    
                    });
    
                    final Button cancelButton = createCancelButton(formRow.getParent(), true);
                    cancelButton.addSelectionListener(new SelectionAdapter() {
    
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            if (cancelButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_CANCEL)) {
                                rowHandler.cancelPressed();
                            } else {
                                try {
                                    rowHandler.removePressed();
                                } catch (NeOnCoreException k2e) {
                                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, cancelButton.getShell());
                                    return;
                                } catch (CommandException k2e) {
                                    handleException(k2e, Messages.ObjectPropertyPropertyPage2_45, cancelButton.getShell());
                                    return;
                                }
                            }
                        }
                    });
    
                    formRow.setSubmitButton(addButton);
                    formRow.setCancelButton(cancelButton);
                }
            } catch (CommandException e) {
                // nothing to do
            } catch (NeOnCoreException e) {
                // TODO: migration
                // do nothing
            }
        }
        initWarnings(formRow, quantifierCombo, cardinalityText, propertyText);
        enableButtons(quantifierCombo, cardinalityText, propertyText, formRow.getRangeText(), formRow.getSubmitButton());
        formRow.getParent().layout(true);
        _form.reflow(true);
    }

    private void initWarnings(AbstractFormRow formRow, CCombo quantifierCombo, StyledText cardinalityText, StyledText propertyText) {
        String message = null;
        int type = IMessageProvider.NONE;

        String quantifier = quantifierCombo.getText();
        if (propertyText.getText().trim().length() == 0) {
            if (formRow instanceof AbstractRestrictionRow && ((AbstractRestrictionRow) formRow).getRangeText().getText().trim().length() == 0){
                message = null;
                type = IMessageProvider.NONE;
            }else{
                message = Messages.ClazzPropertyPage2_4;
                type = IMessageProvider.WARNING;
            }
        } else {
            if (cardinalityText.getText().trim().length() == 0 || cardinalityText.getText().equals(Messages.ClazzPropertyPage2_15)) {
                if (quantifier.equals(OWLCommandUtils.AT_LEAST_MIN)) {
                    message = Messages.ClazzPropertyPage2_16;
                    type = IMessageProvider.WARNING;
                } else if (quantifier.equals(OWLCommandUtils.AT_MOST_MAX)) {
                    message = Messages.ClazzPropertyPage2_19;
                    type = IMessageProvider.WARNING;
                } else if (quantifier.equals(OWLCommandUtils.EXACTLY_CARDINALITY)) {
                    message = Messages.ClazzPropertyPage2_21;
                    type = IMessageProvider.WARNING;
                }
            }
            if (formRow instanceof AbstractRestrictionRow) {
                AbstractRestrictionRow row = (AbstractRestrictionRow) formRow;
                if (row.getRangeText().getText().trim().length() == 0 && (
                        quantifier.equals(OWLCommandUtils.SOME) || 
                        quantifier.equals(OWLCommandUtils.HAS_VALUE) || 
                        quantifier.equals(OWLCommandUtils.ONLY))) {
                    if (row.getRangeText().getText().trim().length() == 0) {
                        message = Messages.ClazzPropertyPage2_11;
                        type = IMessageProvider.WARNING;
                    } else {
                        type = IMessageProvider.NONE;
                        message = null;
                    }
                }
            }

            String card = cardinalityText.getText();
            if (card.length() > 0 && (
                    quantifier.equals(OWLCommandUtils.AT_LEAST_MIN) || 
                    quantifier.equals(OWLCommandUtils.AT_MOST_MAX) || 
                    quantifier.equals(OWLCommandUtils.EXACTLY_CARDINALITY))) {
                try {
                    new Integer(card);
                    if (card.startsWith("-")) { //$NON-NLS-1$
                        message = Messages.ClazzPropertyPage2_57;
                        type = IMessageProvider.WARNING;
                    } else {
                        message = null;
                        type = IMessageProvider.NONE;
                    }
                } catch (NumberFormatException nfe) {
                    message = Messages.ClazzPropertyPage2_57;
                    type = IMessageProvider.WARNING;
                }
            }
            if (quantifier.equals(OWLCommandUtils.EXACTLY_CARDINALITY)) {
                if (card.trim().length() == 0) {
                    message = Messages.ClazzPropertyPage2_12;
                    type = IMessageProvider.WARNING;
                }
            } else if (quantifier.equals(OWLCommandUtils.AT_LEAST_MIN)) {
                if (card.trim().length() == 0) {
                    message = Messages.ClazzPropertyPage2_13;
                    type = IMessageProvider.WARNING;
                }
            } else if (quantifier.equals(OWLCommandUtils.AT_MOST_MAX)) {
                if (card.trim().length() == 0) {
                    message = Messages.ClazzPropertyPage2_max;
                    type = IMessageProvider.WARNING;
                }
            }
            if(quantifier.equals(OWLCommandUtils.HAS_SELF)){
                try {
                    if(! new IsObjectProperty(this._project, this._ontologyUri, propertyText.getText().trim()).isObjectProperty()){
                        message = Messages.ClazzPropertyPage2_41;
                        type = IMessageProvider.WARNING;
                    }
                } catch (CommandException e) {
                    e.printStackTrace();
                }
            }
        }
        _form.setMessage(message, type);
    }

    private void setListenerForRangeText(final AbstractFormRow formRow, final CCombo quantifierCombo, final StyledText cardinalityText, final StyledText propertyText, final StyledText rangeText) {
        rangeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                initWarnings(formRow, quantifierCombo, cardinalityText, propertyText);
                enableButtons(quantifierCombo, cardinalityText, propertyText, rangeText, formRow.getSubmitButton());
            }
        });
    }

    private void enableButtons(final CCombo quantifierCombo, final StyledText cardinalityText, final StyledText propertyText, final StyledText rangeText, final Button button) {
        if (_form.getMessage() != null) {
            // if a warning is displayed, submit button should always be disabled
            button.setEnabled(false);
        } else {
            String quantifierString = quantifierCombo.getText();
            String propertyString = propertyText.getText().trim();
            String cardinalityString = cardinalityText.getText().trim();
            String rangeString = rangeText.getText().trim();
            if (quantifierString.equals(Messages.ClazzPropertyPage2_5) || 
                    propertyString.equals(Messages.ClazzPropertyPage2_7) || 
                    propertyString.equals("") //$NON-NLS-1$
                    || cardinalityString.equals("") && //$NON-NLS-1$
                    (quantifierString.equals(OWLCommandUtils.AT_LEAST_MIN) || 
                            quantifierString.equals(OWLCommandUtils.AT_MOST_MAX) || 
                            quantifierString.equals(OWLCommandUtils.EXACTLY_CARDINALITY))
                    || rangeString.equals("") && //$NON-NLS-1$
                    (quantifierString.equals(OWLCommandUtils.SOME) || 
                            quantifierString.equals(OWLCommandUtils.HAS_VALUE) || 
                            quantifierString.equals(OWLCommandUtils.ONLY))) {
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
            }
        }
    }

    /**
     * Returns the values recently entered into the text fields. For the properties OWLGUIUtilities.getValidURI() is called, because we may have an ID without
     * namespace.
     * 
     * @param quantifierCombo
     * @param propertyText
     * @param rangeText
     * @param cardinalityText
     * 
     * @return
     * @throws NeOnCoreException
     * @throws ControlException
     */
    private String[] getNewValues(final CCombo quantifierCombo, final StyledText propertyText, final StyledText rangeText, final StyledText cardText) throws NeOnCoreException, CommandException {

        String quantifier = quantifierCombo.getText();
        String property = OWLGUIUtilities.getValidURI(propertyText.getText(), _ontologyUri, _project);
//        rangeText.d
        String range = rangeText.getText();
        String cardinality = cardText.getText();
        OWLOntology ontology = _owlModel.getOntology();

//        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        OWLDataFactory factory = _owlModel.getOWLDataFactory();
        // range is optional, so may be empty.
        OWLObject rangeDesc = null;
        OWLObject propertyObject = null;
        if (new IsDataProperty(_project, _ontologyUri, property).isDataProperty()) {
            propertyObject = factory.getOWLDataProperty(OWLUtilities.toIRI(_owlModel.getNamespaces().expandString(property)));
            if (range.trim().length() > 0) {
                if (quantifier.equals(OWLCommandUtils.HAS_VALUE)) {
                    // peter as value is rejected. should be stored like "peter"^^ xsd:string instead
                    try {
                        rangeDesc = OWLUtilities.constant(range, _owlModel);
    //                        rangeDesc = manager.parseConstant(range, _owlModel);
                    } catch (NeOnCoreException e) {
                        // bugfix for #9898
                        if (e.getCause() instanceof ParserException) {
                            rangeDesc = OWLUtilities.constant("\"" + range + "\"", _owlModel);  //$NON-NLS-1$//$NON-NLS-2$
    //                            rangeDesc = manager.parseConstant("\"" + range + "\"", _owlModel);  //$NON-NLS-1$//$NON-NLS-2$
                        }
                    }
                } else {
                    rangeDesc = OWLUtilities.dataRange(IRIUtils.ensureValidIRISyntax(_owlModel.getNamespaces().expandString(range)));
    //                    rangeDesc = manager.parseDataRange(range, _owlModel);
                }
            }
        } else {
            propertyObject = factory.getOWLObjectProperty(OWLUtilities.toIRI(_owlModel.getNamespaces().expandString(property)));
            if (range.trim().length() > 0) {
                if (quantifier.equals(OWLCommandUtils.HAS_VALUE)) {
                    rangeDesc = OWLUtilities.individual(IRIUtils.ensureValidIRISyntax(_owlModel.getNamespaces().expandString(range)));
    //                    rangeDesc = manager.parseIndividual(range, _owlModel);
                } else {
                    rangeDesc = OWLUtilities.description(IRIUtils.ensureValidIRISyntax(_owlModel.getNamespaces().expandString(range)));
    //                    rangeDesc = manager.parseDescription(range, _owlModel);
                }
            }
        }
        String rangeStr = rangeDesc != null ? OWLUtilities.toString(rangeDesc) : range;
        String propertyStr = propertyObject != null ? OWLUtilities.toString(propertyObject) : property;
        String[] newValues = new String[] {quantifier, propertyStr, rangeStr, cardinality};
        return newValues;
    }

    @Override
    public void refreshComponents() {
        super.refreshComponents();
        _form.setMessage(null, IMessageProvider.NONE);

        initSuperRestrictionSection(false);
        initEquivalentRestrictionSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        initSuperRestrictionSection(false);
        initEquivalentRestrictionSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
    }

    @Override
    protected String getTitle() {
        return Messages.ClazzPropertyPage2_Clazzes;
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_superRestrictionSection);
        sections.add(_equivRestrictionsSection);
        return sections;
    }

    protected OWLClassExpression getClazzDescription() throws NeOnCoreException {
        return OWLUtilities.description(IRIUtils.ensureValidIRISyntax(_id));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.owl.gui.properties.BasicOWLEntityPropertyPage#getEntity()
     */
    @Override
    public OWLEntity getOWLObject() throws NeOnCoreException {
        OWLClassExpression desc = getClazzDescription();
        if (desc instanceof OWLEntity) {
            return (OWLEntity) desc;
        }

        return null;
    }

    @Override
    public void dispose() {
        // nothing to do
    }

}
