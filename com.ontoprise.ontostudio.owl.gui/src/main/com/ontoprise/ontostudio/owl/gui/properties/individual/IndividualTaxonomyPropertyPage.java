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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.properties.IMainPropertyPage;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.EntityRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DescriptionText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.IndividualText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateDifferentIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateSameIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.EditDifferentIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.EditEquivalentIndividuals;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetDescriptionHits;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetDifferentIndividualHits;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetEquivalentIndividualHits;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

/**
 * 
 * @author Nico Stieler
 */
public class IndividualTaxonomyPropertyPage extends AbstractOWLIdPropertyPage {

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 3;

    private static final int SAME_AS_MODE = 0;
    private static final int DIFFERENT_FROM_MODE = 1;
    private static final int CLAZZ_MODE = 2;

    /*
     * JFace Forms variables
     */
    private Section _sameAsSection;
    private Section _differentFromSection;
    private Section _clazzesSection;

    private Composite _sameAsComp;
    private Composite _differentFromComp;
    private Composite _clazzesComp;

    private boolean _showAxioms;

    public IndividualTaxonomyPropertyPage() {
        super();
    }

    @Override
    public Composite createContents(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_INDIVIDUAL_VIEW);
        Composite body = prepareForm(composite);

        createSameAsArea(body);
        createDifferentFromArea(body);
        createClazzesArea(body);
        _form.reflow(true);

        return body;
    }

    @Override
    public void refresh() {
        super.refresh();

        initSameAsSection(false);
        initDifferentFromSection(false);
        initClazzesSection(false);

        // closeToolBar();
        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void update() {
        super.update();

        initSameAsSection(false);
        initDifferentFromSection(false);
        initClazzesSection(false);

        layoutSections();
        _form.reflow(true);
    }

    private void initSameAsSection(boolean setFocus) {
        _showAxioms = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS);
        clearComposite(_sameAsComp);
        Map<OWLIndividual,Boolean> localOrImportedMap = new HashMap<OWLIndividual,Boolean>(); // Individual i, Boolean isLocal
        try {
            String[][] individualHitsArray = new GetEquivalentIndividualHits(_project, _ontologyUri, _id).getResults();
            List<String> sourceOntoList = new ArrayList<String>();
            Map<OWLIndividual,List<LocatedAxiom>> individualAxiomMap = new HashMap<OWLIndividual,List<LocatedAxiom>>();
            List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();

            for (String[] hit: individualHitsArray) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                sourceOntoList.add(ontologyUri);
                OWLSameIndividualAxiom axiom = (OWLSameIndividualAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);
                LocatedAxiom locAx = new LocatedAxiom(axiom, isLocal);
                if (!axiomList.contains(locAx)) {
                    axiomList.add(locAx);
                }
                Set<OWLIndividual> individuals = axiom.getIndividuals();
                for (OWLIndividual individual: individuals) {
                    IMainPropertyPage mainPage = getMainPage();
                    OWLObject entity = null;
                    if (mainPage instanceof IndividualPropertyPage2) {
                        entity = ((IndividualPropertyPage2) mainPage).getOWLObject();
                    }
                    if (entity != null && !individual.equals(entity)) {
                        Boolean oldLocalValue = localOrImportedMap.get(individual);
                        if (oldLocalValue == null || oldLocalValue.booleanValue() == false) {
                            localOrImportedMap.put(individual, new Boolean(ontologyUri.equals(_ontologyUri)));
                        }
                        if (!individualAxiomMap.containsKey(individual)) {
                            individualAxiomMap.put(individual, axiomList);
                        }
                    }
                }
                List<OWLIndividual> individualList = new ArrayList<OWLIndividual>(individualAxiomMap.keySet());
                Collections.sort(individualList, new Comparator<OWLIndividual>() {

                    @Override
                    public int compare(OWLIndividual o1, OWLIndividual o2) {
                        try {
                            if (!(o1 instanceof OWLEntity)) {
                                // TODO: migration
                                throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
                            }
                            if (!(o2 instanceof OWLEntity)) {
                                // TODO: migration
                                throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
                            }
                            String uri1 = OWLGUIUtilities.getEntityLabel((OWLEntity)o1, _ontologyUri, _project);
                            String uri2 = OWLGUIUtilities.getEntityLabel((OWLEntity)o2, _ontologyUri, _project);
                            return uri1.compareToIgnoreCase(uri2);
                        } catch (NeOnCoreException e) {
                            return 0;
                        }
                    }
                });
                if (_showAxioms) {
                    for (OWLIndividual i: individualList) {
                        boolean local = localOrImportedMap.get(i).booleanValue();
                        List<LocatedAxiom> axiomListLocal = individualAxiomMap.get(i);
                        for (LocatedAxiom a: axiomListLocal) {
                            List<LocatedAxiom> listToUse = new ArrayList<LocatedAxiom>();
                            listToUse.add(a);
                            createSameAsRow(i, listToUse, !local, sourceOntoList.toArray(new String[sourceOntoList.size()]));
                        }
                    }
                } else {
                    for (OWLIndividual i: individualList) {
                        boolean local = localOrImportedMap.get(i).booleanValue();
                        createSameAsRow(i, axiomList, !local, sourceOntoList.toArray(new String[sourceOntoList.size()]));
                    }
                }
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _clazzesSection.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _clazzesSection.getShell());
        }
        Label createNewLabel = new Label(_sameAsComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.IndividualPropertyPage2_5);
        Composite activeComposite = createEmptyRow(_sameAsComp, SAME_AS_MODE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private void initDifferentFromSection(boolean setFocus) {
        _showAxioms = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS);
        clearComposite(_differentFromComp);
        Map<OWLIndividual,Boolean> localOrImportedMap = new HashMap<OWLIndividual,Boolean>(); // Individual i, Boolean isLocal
        try {
            String[][] individualHitsArray = new GetDifferentIndividualHits(_project, _ontologyUri, _id).getResults();
            List<String> sourceOntoList = new ArrayList<String>();
            Map<OWLIndividual,List<LocatedAxiom>> individualAxiomMap = new HashMap<OWLIndividual,List<LocatedAxiom>>();
            List<LocatedAxiom> axiomList = new ArrayList<LocatedAxiom>();
            for (String[] hit: individualHitsArray) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                if (!sourceOntoList.contains(ontologyUri)) {
                    sourceOntoList.add(ontologyUri);
                }
                OWLDifferentIndividualsAxiom axiom = (OWLDifferentIndividualsAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);
                LocatedAxiom locAx = new LocatedAxiom(axiom, isLocal);
                if (!axiomList.contains(locAx)) {
                    axiomList.add(locAx);
                }
                Set<OWLIndividual> individuals = axiom.getIndividuals();
                for (OWLIndividual individual: individuals) {
                    IMainPropertyPage mainPage = getMainPage();
                    OWLObject entity = null;
                    if (mainPage instanceof IndividualPropertyPage2) {
                        entity = ((IndividualPropertyPage2) mainPage).getOWLObject();
                    }
                    if (entity != null && !individual.equals(entity)) {
                        Boolean oldLocalValue = localOrImportedMap.get(individual);
                        if (oldLocalValue == null || oldLocalValue.booleanValue() == false) {
                            localOrImportedMap.put(individual, new Boolean(ontologyUri.equals(_ontologyUri)));
                        }
                        if (!individualAxiomMap.containsKey(individual)) {
                            individualAxiomMap.put(individual, axiomList);
                        }
                    }
                }
            }
            List<OWLIndividual> individualList = new ArrayList<OWLIndividual>(individualAxiomMap.keySet());
            Collections.sort(individualList, new Comparator<OWLIndividual>() {

                @Override
                public int compare(OWLIndividual o1, OWLIndividual o2) {
                    try {
                        if (!(o1 instanceof OWLEntity)) {
                            // TODO: migration
                            throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
                        }
                        if (!(o2 instanceof OWLEntity)) {
                            // TODO: migration
                            throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
                        }
                        String uri1 = OWLGUIUtilities.getEntityLabel((OWLEntity)o1, _ontologyUri, _project);
                        String uri2 = OWLGUIUtilities.getEntityLabel((OWLEntity)o2, _ontologyUri, _project);
                        return uri1.compareToIgnoreCase(uri2);
                    } catch (NeOnCoreException e) {
                        return 0;
                    }
                }
            });
            if (_showAxioms) {
                for (OWLIndividual i: individualList) {
                    boolean local = localOrImportedMap.get(i).booleanValue();
                    List<LocatedAxiom> axiomListLocal = individualAxiomMap.get(i);
                    for (LocatedAxiom axiom: axiomListLocal) {
                        List<LocatedAxiom> listToUse = new ArrayList<LocatedAxiom>();
                        listToUse.add(axiom);
                        createDifferentFromRow(i, listToUse, !local, sourceOntoList.toArray(new String[sourceOntoList.size()]));
                    }
                }
            } else {
                for (OWLIndividual i: individualList) {
                    boolean local = localOrImportedMap.get(i).booleanValue();
                    createDifferentFromRow(i, individualAxiomMap.get(i), !local, sourceOntoList.toArray(new String[sourceOntoList.size()]));
                }
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _clazzesSection.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _clazzesSection.getShell());
        }
        Label createNewLabel = new Label(_differentFromComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.IndividualPropertyPage2_6);
        Composite activeComposite = createEmptyRow(_differentFromComp, DIFFERENT_FROM_MODE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private Composite createEmptyRow(Composite parent, int mode) {
        int numCols = mode == CLAZZ_MODE ? 3 : NUM_COLS;
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, numCols);

        final StyledText text = mode == CLAZZ_MODE ? getComplexClassText(row, _owlModel) : getIndividualText(row, _owlModel);
        row.addWidget(text);

        AxiomRowHandler rowHandler = null;
        if (mode == DIFFERENT_FROM_MODE) {
            rowHandler = new AxiomRowHandler(this, _owlModel, _owlModel, null) {

                @Override
                public void savePressed() {
                    // nothing to do
                }

                @Override
                public void addPressed() {
                    // add new entry
                    try {
                        String otherIndividual = OWLGUIUtilities.getValidURI(text.getText(), _localOwlModel.getOntologyURI(), _project);//NICO are you sure?
                        if (otherIndividual != null) {
                            if (!otherIndividual.equals(_id)) {
                                new CreateDifferentIndividuals(_project, _sourceOwlModel.getOntologyURI(), new String[] {_id, otherIndividual}).run();//NICO are you sure?
                            } else {
                                MessageDialog.openWarning(_clazzesComp.getShell(), Messages.IndividualPropertyPage2_45, Messages.IndividualTaxonomyPropertyPage_0);
                            }
                        }
                    } catch (NeOnCoreException k2e) {
                        handleException(k2e, Messages.IndividualPropertyPage2_45, text.getShell());
                    } catch (CommandException e) {
                        handleException(e, Messages.IndividualPropertyPage2_45, text.getShell());
                    }
                    initDifferentFromSection(true);
                    layoutSections();
                    _form.reflow(true);
                }

                @Override
                public void ensureQName() {
                    // nothing to do
                }

            };
        } else if (mode == SAME_AS_MODE) {
            rowHandler = new AxiomRowHandler(this, _owlModel, _owlModel, null) {

                @Override
                public void savePressed() {
                    // nothing to do
                }

                @Override
                public void addPressed() {
                    // add new entry
                    try {
                        String sameIndividual = OWLGUIUtilities.getValidURI(text.getText(), _localOwlModel.getOntologyURI(), _project);//NICO are you sure?
                        if (!sameIndividual.equals(_id)) {
                            new CreateSameIndividuals(_project, _sourceOwlModel.getOntologyURI(), new String[] {_id, sameIndividual}).run();//NICO are you sure?
                        } else {
                            MessageDialog.openWarning(_clazzesComp.getShell(), Messages.IndividualPropertyPage2_45, Messages.IndividualTaxonomyPropertyPage_1);
                        }
                    } catch (NeOnCoreException k2e) {
                        handleException(k2e, Messages.IndividualPropertyPage2_45, text.getShell());
                    } catch (CommandException e) {
                        handleException(e, Messages.IndividualPropertyPage2_45, text.getShell());
                    }
                    initSameAsSection(true);
                    layoutSections();
                    _form.reflow(true);
                }

                @Override
                public void ensureQName() {
                    // nothing to do
                }

            };
        } else if (mode == CLAZZ_MODE) {
            rowHandler = new AxiomRowHandler(this, _owlModel, _owlModel, null) {

                @Override
                public void savePressed() {
                    // nothing to do
                }

                @Override
                public void addPressed() {
                    // add new entry
                    try {
                        OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(_project);
                        OWLClassExpression newValue = _manager.parseDescription(text.getText(), _localOwlModel);//NICO are you sure?
                        OWLIndividual individual = factory.getOWLNamedIndividual(OWLUtilities.toIRI(_id));
                        OWLAxiom newAxiom = factory.getOWLClassAssertionAxiom((OWLClassExpression) newValue, individual);
                        _sourceOwlModel.addAxiom(newAxiom);//NICO are you sure?
                    } catch (NeOnCoreException k2e) {
                        handleException(k2e, Messages.IndividualPropertyPage2_45, text.getShell());
                    }
                    initClazzesSection(true);
                    layoutSections();
                    _form.reflow(true);
                }

                @Override
                public void ensureQName() {
                    // nothing to do
                }

            };
        }
        row.init(rowHandler);

        text.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (text.getText() != "") { //$NON-NLS-1$
                    row.getAddButton().setEnabled(true);
                } else {
                    row.getAddButton().setEnabled(false);
                }
            }

        });
        return text;
    }

    private StyledText getIndividualText(final EmptyFormRow row, OWLModel sourceOwlModel) {
        StyledText text = new IndividualText(row.getParent(), _owlModel, sourceOwlModel).getStyledText();
        addSimpleWidget(text);
        return text;
    }

    private StyledText getComplexClassText(EmptyFormRow row, OWLModel sourceOwlModel) {
        DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, sourceOwlModel, _toolkit);
        addComplexText(descriptionText);
        return descriptionText.getStyledText();
    }

    private void initClazzesSection(boolean setFocus) {
        clearComposite(_clazzesComp);
        Map<OWLClassAssertionAxiom,Boolean> localOrImportedMap = new HashMap<OWLClassAssertionAxiom,Boolean>(); // ClassMember i, Boolean isLocal
        try {
            String[][] descriptionHitsArray = new GetDescriptionHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(descriptionHitsArray);
            List<String> sourceOntoList = new ArrayList<String>();

            List<OWLClassAssertionAxiom> clazzMembersList = new ArrayList<OWLClassAssertionAxiom>();
            for (String[] hit: sortedSet) {
                String axiomText = hit[0];
                String ontologyUri = hit[1];

                sourceOntoList.add(ontologyUri);
                boolean isLocal = ontologyUri.equals(_ontologyUri);
                OWLClassAssertionAxiom clazzMember = (OWLClassAssertionAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);
                Boolean oldLocalValue = localOrImportedMap.get(clazzMember);
                if (oldLocalValue == null || oldLocalValue.booleanValue() == false) {
                    localOrImportedMap.put(clazzMember, new Boolean(isLocal));
                }
                if (!clazzMembersList.contains(clazzMember)) {
                    clazzMembersList.add(clazzMember);
                }
            }
            for (OWLClassAssertionAxiom member: clazzMembersList) {
                boolean local = localOrImportedMap.get(member).booleanValue();
                createClazzRow(new LocatedAxiom(member, local), sourceOntoList.toArray(new String[sourceOntoList.size()]), !local);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _clazzesSection.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _clazzesSection.getShell());
        }
        Label createNewLabel = new Label(_clazzesComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.IndividualPropertyPage2_6);
        Composite activeComposite = createEmptyRow(_clazzesComp, CLAZZ_MODE);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private void createClazzRow(final LocatedAxiom axiom, String[] sourceOntologyUris, boolean imported) throws NeOnCoreException {
        String sourceOntologyUri = ""; //$NON-NLS-1$
        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOntologyUri = sourceOntologyUris[0];
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOntologyUri, _project);//NICO Onto
        }
       
        FormRow row = new FormRow(_toolkit, _clazzesComp, 3, imported, sourceOntologyUri,sourceOwlModel.getProjectId(),_id);

        DescriptionText descriptionText = new DescriptionText(row.getParent(), _owlModel, sourceOwlModel, _toolkit);
        final StyledText text = descriptionText.getStyledText();
        text.setToolTipText(Messages.IndividualPropertyPage2_30);
        addComplexText(descriptionText);

        final String[] array = getArrayFromDescription(((OWLClassAssertionAxiom) axiom.getAxiom()).getClassExpression());
        String id = OWLGUIUtilities.getEntityLabel(array);
        text.setText(id);
        text.setData(axiom);
        text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, descriptionText);
        OWLGUIUtilities.enable(text, false);
        row.addWidget(text);

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, axiom) {

            @Override
            public void savePressed() {
                // save modified entries
                try {
                    String value = text.getText();
                    OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(_project);
                    OWLClassExpression clazzDesc = _manager.parseDescription(value, _localOwlModel);//NICO are you sure?
                    OWLIndividual individual = factory.getOWLNamedIndividual(OWLUtilities.toIRI(_id));
                    OWLAxiom oldAxiom = factory.getOWLClassAssertionAxiom(((OWLClassAssertionAxiom) axiom.getAxiom()).getClassExpression(), individual);
                    OWLAxiom newAxiom = factory.getOWLClassAssertionAxiom(clazzDesc, individual);
                    new ApplyChanges(_project, _sourceOwlModel.getOntologyURI(), new String[] {OWLUtilities.toString(newAxiom)}, new String[] {OWLUtilities.toString(oldAxiom)}).run();//NICO are you sure?

                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.IndividualPropertyPage2_45, text.getShell());
                } catch (CommandException e) {
                    handleException(e, Messages.IndividualPropertyPage2_45, text.getShell());
                }
                OWLGUIUtilities.enable(text, false);
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
                    text.setText(array[1]);
                } else {
                    text.setText(array[0]);
                }
            }

        };
        row.init(rowHandler);
    }

    private void createDifferentFromRow(OWLIndividual individual, List<LocatedAxiom> axioms, boolean imported, String[] sourceOntos) throws NeOnCoreException {
        if (!(individual instanceof OWLEntity)) {
            // TODO: migration
            throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
        }
        String sourceOnto = ""; //$NON-NLS-1$
        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOnto = sourceOntos[0];
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project); //NICO sourceOnto
        }
       
        FormRow row = new FormRow(_toolkit, _differentFromComp, NUM_COLS, imported, sourceOnto,sourceOwlModel.getProjectId(),_id);
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = _manager.getVisitor(_owlModel, idDisplayStyle);
        IndividualText individualText = new IndividualText(row.getParent(), _owlModel, sourceOwlModel);
        final StyledText textWidget = individualText.getStyledText();
        textWidget.setToolTipText(Messages.IndividualPropertyPage2_30);
        row.addWidget(textWidget);

        final String[] array = (String[]) individual.accept(visitor);
        String id = OWLGUIUtilities.getEntityLabel(array);
        textWidget.setText(id);
        textWidget.setData(array);
        textWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, individualText);
        OWLGUIUtilities.enable(textWidget, false);

        EntityRowHandler rowHandler = new EntityRowHandler(this, _owlModel, sourceOwlModel, (OWLEntity)individual, axioms) {

            @Override
            public void savePressed() {
                // save modified entries
                try {
                    String value = OWLGUIUtilities.getValidURI(textWidget.getText(), _localOwlModel.getOntologyURI(), _project);//NICO are you sure?
                    List<LocatedAxiom> axioms = getAxioms();
                    for (LocatedAxiom oldAxiom: axioms) {
                        String oldAxiomText = OWLUtilities.toString(oldAxiom.getAxiom());
                        if (oldAxiom.isLocal()) {
                            new EditDifferentIndividuals(_project, _sourceOwlModel.getOntologyURI(), oldAxiomText, getEntity().getIRI().toString(), value).run();//NICO are you sure?
                        }
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.IndividualPropertyPage2_45, textWidget.getShell());
                } catch (CommandException e) {
                    handleException(e, Messages.IndividualPropertyPage2_45, textWidget.getShell());
                }
                OWLGUIUtilities.enable(textWidget, false);
                refresh();
            }

            @Override
            public void removePressed() throws NeOnCoreException {
                List<LocatedAxiom> locatedAxioms = getAxioms();
                List<OWLAxiom> owlAxioms = new ArrayList<OWLAxiom>();
                for (LocatedAxiom a: locatedAxioms) {
                    OWLAxiom axiom = a.getAxiom();
                    if (a.isLocal() && !owlAxioms.contains(axiom)) {
                        owlAxioms.add(axiom);
                    }
                }
                OWLAxiomUtils.triggerRemovePressed(owlAxioms, getEntity(), _namespaces, _id, _sourceOwlModel, WizardConstants.ADD_DEPENDENT_MODE);//NICO are you sure?
                refresh();
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

    private void createSameAsRow(OWLIndividual individual, List<LocatedAxiom> axioms, boolean imported, String[] sourceOntos) throws NeOnCoreException {
        if (!(individual instanceof OWLEntity)) {
            // TODO: migration
            throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
        }
        String sourceOnto = ""; //$NON-NLS-1$
        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOnto = sourceOntos[0];
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project);
        }

        FormRow row = new FormRow(_toolkit, _sameAsComp, NUM_COLS, imported, sourceOnto,sourceOwlModel.getProjectId(),_id);
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = _manager.getVisitor(_owlModel, idDisplayStyle);

        IndividualText text = new IndividualText(row.getParent(), _owlModel, sourceOwlModel);
        final StyledText textWidget = text.getStyledText();
        textWidget.setToolTipText(Messages.IndividualPropertyPage2_30);
        row.addWidget(textWidget);

        final String[] array = (String[]) individual.accept(visitor);
        String id = OWLGUIUtilities.getEntityLabel(array);
        textWidget.setText(id);
        textWidget.setData(array);
        textWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, text);
        OWLGUIUtilities.enable(textWidget, false);

        EntityRowHandler rowHandler = new EntityRowHandler(this, _owlModel, sourceOwlModel, (OWLEntity)individual, axioms) {

            @Override
            public void savePressed() {
                // save modified entries
                try {
                    String value = OWLGUIUtilities.getValidURI(textWidget.getText(), _localOwlModel.getOntologyURI(), _project);//NICO are you sure?
                    for (LocatedAxiom oldAxiom: getAxioms()) {
                        String oldAxiomText = OWLUtilities.toString(oldAxiom.getAxiom());
                        if (oldAxiom.isLocal()) {
                            new EditEquivalentIndividuals(_project, _sourceOwlModel.getOntologyURI(), oldAxiomText, getEntity().getIRI().toString(), value).run();//NICO are you sure?
                        }
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.IndividualPropertyPage2_45, textWidget.getShell());
                } catch (CommandException e) {
                    handleException(e, Messages.IndividualPropertyPage2_45, textWidget.getShell());
                }
                OWLGUIUtilities.enable(textWidget, false);
                refresh();
            }

            @Override
            public void removePressed() throws NeOnCoreException {
                List<LocatedAxiom> locatedAxioms = getAxioms();
                List<OWLAxiom> owlAxioms = new ArrayList<OWLAxiom>();
                for (LocatedAxiom a: locatedAxioms) {
                    if (a.isLocal()) {
                        owlAxioms.add(a.getAxiom());
                    }
                }
                OWLAxiomUtils.triggerRemovePressed(owlAxioms, getEntity(), _namespaces, _id, _sourceOwlModel, WizardConstants.ADD_DEPENDENT_MODE);//NICO are you sure?
                refresh();
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

    /**
     * Create sameAs area
     */
    private void createSameAsArea(Composite composite) {
        // Same As
        _sameAsSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _sameAsSection.setText(Messages.IndividualPropertyPage_Descriptions_SameAs);
        _sameAsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _sameAsComp = _toolkit.createComposite(_sameAsSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        _sameAsComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _sameAsComp.setLayoutData(data);

        _toolkit.adapt(_sameAsComp);
        _sameAsSection.setClient(_sameAsComp);
    }

    /**
     * Create differentFrom area
     */
    private void createDifferentFromArea(Composite composite) {
        // Different From
        _differentFromSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _differentFromSection.setText(Messages.IndividualPropertyPage_Descriptions_DifferentFrom);
        _differentFromSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _differentFromComp = _toolkit.createComposite(_differentFromSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        _differentFromComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _differentFromComp.setLayoutData(data);

        _toolkit.adapt(_differentFromComp);
        _differentFromSection.setClient(_differentFromComp);
    }

    /**
     * Create differentFrom area
     */
    private void createClazzesArea(Composite composite) {
        // Clazzes
        _clazzesSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _clazzesSection.setText(Messages.IndividualPropertyPage_Descriptions_Clazzes);
        _clazzesSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _clazzesComp = _toolkit.createComposite(_clazzesSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        _clazzesComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _clazzesComp.setLayoutData(data);

        _toolkit.adapt(_clazzesComp);
        _clazzesSection.setClient(_clazzesComp);
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_sameAsSection);
        sections.add(_differentFromSection);
        sections.add(_clazzesSection);
        return sections;
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
                    if (axiom1 instanceof OWLClassAssertionAxiom) {
                        OWLClassAssertionAxiom member = (OWLClassAssertionAxiom) axiom1;
                        OWLClassExpression desc = member.getClassExpression();
                        uri1 = OWLGUIUtilities.getUriForSorting(desc, _owlModel);

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                        if (axiom2 instanceof OWLClassAssertionAxiom) {
                            OWLClassAssertionAxiom member2 = (OWLClassAssertionAxiom) axiom2;
                            OWLClassExpression desc2 = member2.getClassExpression();
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
