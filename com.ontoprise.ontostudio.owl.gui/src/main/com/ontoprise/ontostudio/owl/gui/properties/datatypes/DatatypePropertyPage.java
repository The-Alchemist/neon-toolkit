/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.datatypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
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
import org.neontoolkit.gui.properties.IImagePropertyPage;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.DataRangeRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DatatypeText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.datatypes.CreateEquivalentDatatype;
import com.ontoprise.ontostudio.owl.model.commands.datatypes.GetEquivalentDatatypeHits;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;


public class DatatypePropertyPage extends AbstractOWLMainIDPropertyPage implements IImagePropertyPage{

    private static final int EQUIV = 3;
    
    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 6;

    /*
     * JFace Forms variables
     */
    private Section _equivalentDataRangesSection;

    private Composite _equivalentFormComposite;


    public DatatypePropertyPage() {
        super();
    }

    @Override
    protected void createMainArea(Composite composite) {
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_DATATYPE_VIEW);
        super.createMainArea(composite);
        Composite body = prepareForm(composite);

        createEquivalentDatatypeArea(body);

        _form.reflow(true);
    }
//    @Override
//    public Composite createContents(Composite composite) {
////        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_DATATYPE_VIEW);
//        Composite body = prepareForm(composite);
//
//        createEquivalentDatatypeArea(body);
//
////        layoutSections();
//        _form.reflow(true);
//        return body;
//    }
    /**
     * Create equivalent data type area
     */
    private void createEquivalentDatatypeArea(Composite composite) {
        _equivalentDataRangesSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _equivalentDataRangesSection.setText(Messages.DatatypeTaxonomyPropertyPage_EquivalentDatatypes_0);
        _equivalentDataRangesSection.setLayoutData(new ColumnLayoutData());
        _equivalentDataRangesSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _equivalentFormComposite = _toolkit.createComposite(_equivalentDataRangesSection, SWT.NONE);
        _equivalentFormComposite.setLayout(new GridLayout());
        _equivalentFormComposite.setLayoutData(new ColumnLayoutData());
        _toolkit.adapt(_equivalentFormComposite);
        _equivalentDataRangesSection.setClient(_equivalentFormComposite);
    }

    private void initEquivalentSection(boolean setFocus) {
        clearComposite(_equivalentFormComposite);
        try {
            String[][] results = new GetEquivalentDatatypeHits(_project, _ontologyUri, _id).getResults();
            TreeSet<String[]> sortedSet = getSortedSet(results, EQUIV);
            for (String[] result: sortedSet) {
                String axiomText = result[0];
                String ontologyUri = result[1];
                boolean isLocal = ontologyUri.equals(_ontologyUri);

                OWLDatatypeDefinitionAxiom axiom = (OWLDatatypeDefinitionAxiom) OWLUtilities.axiom(axiomText);
                List<LocatedAxiom> list = new LinkedList<LocatedAxiom>();
                list.add(new LocatedAxiom(axiom, isLocal));
                createRow(list, getEquivalentDataRange(axiom), ontologyUri, EQUIV);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivalentDataRangesSection.getShell());
        } catch (CommandException e1) {
            handleException(e1, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivalentDataRangesSection.getShell());
        }

        Label createNewLabel = new Label(_equivalentFormComposite, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.DatatypeTaxonomyPropertyPage_1);
        Composite activeComposite = createEmptyRow(true, EQUIV);
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        initEquivalentSection(false);

        // closeToolBar();
        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void refreshComponents() {
        super.refreshComponents();

        initEquivalentSection(false);

        layoutSections();
        _form.reflow(true);
    }
    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_equivalentDataRangesSection);
        return sections;
    }
    private void createRow( List<LocatedAxiom> axioms, OWLDataRange dataRange, String sourceOnto, final int mode) throws NeOnCoreException {
        boolean imported = !sourceOnto.equals(_ontologyUri);
        Composite parent = _equivalentFormComposite;
        if (mode == EQUIV) {
            parent = _equivalentFormComposite;
        } 

        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project);
        }
       
        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, sourceOnto, sourceOwlModel.getProjectId(),_id);

        DatatypeText datatypeText = new DatatypeText(row.getParent(), _owlModel, sourceOwlModel);
        final StyledText text = datatypeText.getStyledText();
        text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, datatypeText);
        final String[] array = getArrayFromDescription(dataRange);
        text.setText(OWLGUIUtilities.getEntityLabel(array));
        text.setData(dataRange);
        OWLGUIUtilities.enable(text, false);
        row.addWidget(text);

        DataRangeRowHandler rowHandler = new DataRangeRowHandler(this, _owlModel, sourceOwlModel, dataRange, axioms) {

            @Override
            public void savePressed() {
                // save modified entries
                String value = text.getText();
                try {
                    if (mode == EQUIV) {
                        new CreateEquivalentDatatype(_project, _sourceOwlModel.getOntologyURI(), _id, value).run();//NICO
                        initEquivalentSection(true);
                    }

                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivalentDataRangesSection.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException k2e) {
                    handleException(k2e, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivalentDataRangesSection.getShell());
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
                OWLDataRange thisDataType = OWLUtilities.dataRange(_id);
                
                OWLAxiomUtils.triggerRemovePressed(owlAxioms, getEntity().asOWLDatatype(), _namespaces, OWLUtilities.toString(thisDataType), _sourceOwlModel, WizardConstants.ADD_DEPENDENT_MODE);
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
        Composite parent = _equivalentFormComposite ;
        if (mode == EQUIV) {
            parent = _equivalentFormComposite;
        }
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);
        final StyledText text = new DatatypeText(row.getParent(), _owlModel, _owlModel).getStyledText();
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
                    OWLDataRange dataProp = _manager.parseDataRange(value, _localOwlModel);
                    String thisEntity;

                    switch (mode) {
                        case EQUIV:
                            thisEntity = OWLUtilities.toString(OWLUtilities.dataRange(_id));
                            if (!OWLUtilities.toString(dataProp).equals(thisEntity)) {
                                new CreateEquivalentDatatype(_project, _sourceOwlModel.getOntologyURI(), thisEntity, OWLUtilities.toString(dataProp)).run();
                                initEquivalentSection(true);
                            } else {
                                MessageDialog.openWarning(_equivalentFormComposite.getShell(), Messages.DatatypeTaxonomyPropertyPage_EquivalentDatatypes_47, Messages.DatatypeTaxonomyPropertyPage_EquivalentDatatypes_1 + " " + Messages.DatatypeTaxonomyPropertyPage_EquivalentDatatypes_0 + Messages.DatatypeTaxonomyPropertyPage_EquivalentDatatypes_2); //$NON-NLS-1$
                            }
                            break;
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivalentDataRangesSection.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException ce) {
                    handleException(ce, Messages.ClazzPropertyPage2_ErrorRetrievingData, _equivalentDataRangesSection.getShell());
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
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
    }


    @Override
    protected String getTitle() {
        return Messages.DatatypeTaxonomyPropertyPage_Datatype;
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    @Override
    public void update() {
        super.update();

        initEquivalentSection(false);

        layoutSections();
        _form.reflow(true);
    }
    private TreeSet<String[]> getSortedSet(String[][] entityArray, final int mode) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
//                    String thisId = DatatypePropertyPage.this._id;
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0]);
                    OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0]);
                    String uri1 = ""; //$NON-NLS-1$
                    String uri2 = ""; //$NON-NLS-1$
                    if (mode == EQUIV) {
                        OWLDatatypeDefinitionAxiom thisDatatype = (OWLDatatypeDefinitionAxiom) axiom1;
                        OWLDataRange dataRange1 = getEquivalentDataRange(thisDatatype);
                        uri1 = OWLGUIUtilities.getEntityLabel(dataRange1, ontologyUri1, _project);

                        OWLDatatypeDefinitionAxiom equivalentDatatype = (OWLDatatypeDefinitionAxiom) axiom2;
                        OWLDataRange dataRange2 = getEquivalentDataRange(equivalentDatatype);
                        uri2 = OWLGUIUtilities.getEntityLabel(dataRange2, ontologyUri1, _project);
                    }
                        
                    int localResult = uri1.compareToIgnoreCase(uri2);
                    // make sure multiple hits with the same uri are added multiple times
                    if (localResult == 0) {
                        int x = ontologyUri1.compareToIgnoreCase(ontologyUri2);
                        if (x == 0) {
                            // make sure multiple hits with the same Uri are added multiple times
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
        for (String[] hit: entityArray) {
            unsortedSet.add(hit);
        }
        sortedSet.addAll(unsortedSet);
        return sortedSet;
    }
    
    private OWLDataRange getEquivalentDataRange(OWLDatatypeDefinitionAxiom axiom) throws NeOnCoreException{
        if(OWLUtilities.toString(axiom.getDataRange()).equals(OWLUtilities.toString(OWLUtilities.dataRange(_id))))
            return axiom.getDatatype();
        else
            return axiom.getDataRange();
    }
}
