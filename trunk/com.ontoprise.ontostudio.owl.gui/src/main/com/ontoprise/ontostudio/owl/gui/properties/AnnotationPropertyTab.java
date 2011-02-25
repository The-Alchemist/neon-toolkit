/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties;

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
import org.neontoolkit.gui.properties.IImagePropertyPage;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.individualview.AnonymousIndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.UnknownDatatypeException;
import com.ontoprise.ontostudio.owl.gui.util.forms.AbstractFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.AxiomText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DatatypeAndIndividualText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.PropertyText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.StringText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotations.AddAnonymousIndividualAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.AddEntityAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.EditEntityAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetAnonymousIndividualAnnotationHits;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetEntityAnnotationHits;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;

/**
 * @author Nico Stieler
 * This class is essentially a copy of AnnotationsPropertyPage2. 
 * This is not good but due to the current EPV framework I did not manage to unify both into one class :( 
 */
public class AnnotationPropertyTab extends AbstractOWLIdPropertyPage implements IImagePropertyPage{

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 6;

    /*
     * JFace Forms variables
     */
    private Section _annotationsSection;

    private Composite _annotationsComp;

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.owl.gui.properties.BasicOWLEntityPropertyPage#createMainArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public Composite createContents(Composite composite) {
        Composite body = prepareForm(composite);
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_ANNOTATION_PROPERTIES_VIEW);

        createAnnotationsArea(body);
        _form.reflow(true);
        
        return body;
    }

    /**
     * Creates the annotations area.
     * 
     * @param composite the composite
     */
    private void createAnnotationsArea(Composite composite) {
        _annotationsSection = _toolkit.createSection(composite, Section.TITLE_BAR);
        _annotationsSection.setText(Messages.AnnotationsPropertyPage2_Annotations);
        _annotationsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });

        _annotationsComp = _toolkit.createComposite(_annotationsSection, SWT.NONE);
        _annotationsComp.setLayout(new GridLayout());
        _annotationsComp.setLayoutData(new ColumnLayoutData());

        _toolkit.adapt(_annotationsComp);
        _annotationsSection.setClient(_annotationsComp);
    }

    /**
     * Inits the annotations section.
     * 
     * @param setFocus the set focus
     */
    private void initAnnotationsSection(boolean setFocus) {
        
        clearComposite(_annotationsComp);
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx<?> visitor = _manager.getVisitor(_owlModel, idDisplayStyle);
        try {
            String[][] annotationValueHits;
            
            if(getMainPage().getSelection().getFirstElement() instanceof AnonymousIndividualViewItem){
                AnonymousIndividualViewItem individualViewItem = (AnonymousIndividualViewItem)getMainPage().getSelection().getFirstElement();
                annotationValueHits = new GetAnonymousIndividualAnnotationHits(_project, _ontologyUri, individualViewItem.getIndividual()).getResults();
            
            } else{
                annotationValueHits = new GetEntityAnnotationHits(_project, _ontologyUri, _id).getResults();    
            }
            
            TreeSet<String[]> sortedSet = getSortedSet(annotationValueHits);
            
            createAnnotationRowTitles(_annotationsComp, annotationValueHits.length > 0);
            for (String[] annotationValueHit: sortedSet) {
                OWLAnnotationAssertionAxiom axiom = (OWLAnnotationAssertionAxiom) OWLUtilities.axiom(annotationValueHit[0],_owlModel.getOntology());
                String ontologyUri = annotationValueHit[1];
                boolean imported = !ontologyUri.equals(_ontologyUri);
                handleAnnotationValue(visitor, axiom, imported, ontologyUri);
            }
        } catch (NeOnCoreException e1) {
            handleException(e1, Messages.AnnotationsPropertyPage2_ErrorRetrievingData, _annotationsComp.getShell());
        } catch (CommandException e) {
            handleException(e, Messages.AnnotationsPropertyPage2_ErrorRetrievingData, _annotationsComp.getShell());
        }

        Label createNewLabel = new Label(_annotationsComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.AnnotationsPropertyPage2_1);
        Composite activeComposite = createEmptyRow();
        if (setFocus) {
            activeComposite.setFocus();
        }
        _form.setMessage(null, IMessageProvider.NONE);
        
     }

    /**
     * Creates the annotation row titles.
     * 
     * @param comp the comp
     * @param hasEntries the has entries
     */
    private void createAnnotationRowTitles(Composite comp, boolean hasEntries) {
        boolean showAxiomsCol = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS) && hasEntries;
        Composite rowComp = _toolkit.createComposite(comp);
        GridLayout layout = new GridLayout(NUM_COLS, false);
        layout.marginHeight = 0;
        rowComp.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        rowComp.setLayoutData(layoutData);

        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = PropertyText.WIDTH;
        Text l1 = new Text(rowComp, SWT.NONE);
        l1.setText(Messages.AnnotationsPropertyPage2_AnnotationProperty);
        Color color = _annotationsSection.getTitleBarForeground();

        l1.setForeground(color);
        l1.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = StringText.WIDTH;
        Text l2 = new Text(rowComp, SWT.NONE);
        l2.setText(Messages.AnnotationsPropertyPage2_Value);
        l2.setForeground(color);
        l2.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = DatatypeAndIndividualText.WIDTH;
        Text l3 = new Text(rowComp, SWT.NONE);
        l3.setText(Messages.AnnotationsPropertyPage2_Type);
        l3.setForeground(color);
        l3.setLayoutData(data);

        data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
        data.widthHint = OWLGUIUtilities.LANGUAGE_SELECT_BOX_WIDTH;
        Text l4 = new Text(rowComp, SWT.NONE);
        l4.setText(Messages.AnnotationsPropertyPage2_Language);
        l4.setForeground(color);
        l4.setLayoutData(data);

        if (showAxiomsCol) {
            data = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
            data.widthHint = AxiomText.WIDTH_MORE_COLS;
            Text l5 = new Text(rowComp, SWT.NONE);
            l5.setText(Messages.ClazzPropertyPage_Restrictions_Axiom);
            l5.setForeground(color);
            l5.setLayoutData(data);
        }

        Button b1 = createEditButton(rowComp, false);
        b1.setVisible(false);

        Button b2 = createRemoveButton(rowComp, false);
        b2.setVisible(false);
    }

    /**
     * Handle annotation value.
     * 
     * @param visitor the visitor
     * @param o the o
     * @param annotation the annotation
     * @param imported the imported
     * @param sourceOnto the source onto
     * 
     * @throws NeOnCoreException the KAON2 exception
     */
    private void handleAnnotationValue(OWLObjectVisitorEx<?> visitor, OWLAnnotationAssertionAxiom annotation, boolean imported, String sourceOnto) throws NeOnCoreException {
        OWLAnnotationProperty annotationProperty = annotation.getAnnotation().getProperty();
        String[] propArray = (String[]) annotationProperty.accept(visitor);
        ArrayList<String[]> contents = new ArrayList<String[]>();
        Object o = annotation.getAnnotation().getValue();

        if (o instanceof OWLAnonymousIndividual) {
//            OWLIndividual individual = (OWLIndividual) o;
//            String[] indivArray = (String[]) individual.accept(visitor);
//            contents.add(propArray);
//            contents.add(indivArray);
//            contents.add(new String[] {OWLAxiomUtils.OWL_INDIVIDUAL_LOCAL, OWLAxiomUtils.OWL_INDIVIDUAL});
//            contents.add(new String[] {null});
//            createAnnotationsRow(annotation, contents, imported, sourceOnto);
            // TODO: migration
            throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
            
        } else if (o instanceof IRI) {
          OWLIndividual individual = _manager.parseIndividual(((IRI)o).toString(), _owlModel);
          String[] indivArray = (String[]) individual.accept(visitor);
          contents.add(propArray);
          contents.add(indivArray);
          contents.add(new String[] {OWLAxiomUtils.OWL_INDIVIDUAL_LOCAL, OWLAxiomUtils.OWL_INDIVIDUAL});
          contents.add(new String[] {null});
          createAnnotationsRow(annotation, contents, imported, sourceOnto);

        } else if (o instanceof OWLLiteral) {
            OWLLiteral constant = (OWLLiteral)o;
            OWLDatatype dataType = constant.getDatatype();
            contents.add(propArray);
            contents.add(new String[] {constant.getLiteral()});
            contents.add((String[]) dataType.accept(visitor));
            if(constant.isRDFPlainLiteral()) {
                String language = constant.getLang();
                contents.add(new String[] {language});
            }else{
                contents.add(new String[] {null});
            }
            createAnnotationsRow(annotation, contents, imported, sourceOnto);

        } else {
            // TODO proper error handling
            System.err.print(Messages.AnnotationsPropertyPage2_0 + o);
        }
    }

    /**
     * Creates the annotations row.
     * 
     * @param descriptions a list of length 4 that contains the property, the value, the type and the language. 
     *        Each element is a String Array consisting of uri, label, qname. 
     *        type and language can be empty.
     * @param annotation the annotation axiom
     * @param imported the imported
     * @param sourceOnto the source onto
     * 
     * @throws NeOnCoreException 
     */
    private void createAnnotationsRow(OWLAnnotationAssertionAxiom annotation, ArrayList<String[]> descriptions, boolean imported, String sourceOnto) throws NeOnCoreException {
        boolean isLocal = !imported;
        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(sourceOnto, _project);
        }
       
        final FormRow formRow = new FormRow(_toolkit, _annotationsComp, NUM_COLS, imported, sourceOnto,sourceOwlModel.getProjectId(),_id);
       
        // text widgets
        PropertyText propertyText =  new PropertyText(formRow.getParent(), _owlModel, sourceOwlModel, PropertyText.ANNOTATION_PROPERTY);
        final StyledText propertyTextWidget = propertyText.getStyledText();
        propertyTextWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, propertyText);
        formRow.addWidget(propertyTextWidget);

        StringText valueText = new StringText(formRow.getParent());
        //IndividualText valueText = new IndividualText(formRow.getParent(), _owlModel);
        final StyledText valueTextWidget = valueText.getStyledText(); 
        valueTextWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, valueText);
        formRow.addWidget(valueTextWidget);
        
        DatatypeAndIndividualText typeText = new DatatypeAndIndividualText(formRow.getParent(), _owlModel, sourceOwlModel);
        final StyledText typeTextWidget = typeText.getStyledText();
        typeTextWidget.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, typeText);
        formRow.addWidget(typeTextWidget);
        
        final CCombo languageCombo = OWLGUIUtilities.createLanguageComboBox(formRow.getParent(), false);
        formRow.addWidget(languageCombo);

        OWLGUIUtilities.initStringOrLiteralSwitch(typeTextWidget, languageCombo, sourceOwlModel);

        final String[] propertyArray = descriptions == null ? new String[0] : descriptions.get(0);
        final String[] valueArray = descriptions == null ? new String[0] : descriptions.get(1);
        final String[] typeArray = descriptions == null ? new String[0] : descriptions.get(2);

        // annotationProperty
        if (descriptions != null) {
            String id = OWLGUIUtilities.getEntityLabel(propertyArray);
            propertyTextWidget.setText(id);
            OWLGUIUtilities.enable(propertyTextWidget, false);
        }

        // value
        if (descriptions != null) {
            if (valueArray.length > 1) {
                String id = OWLGUIUtilities.getEntityLabel(valueArray);
                valueTextWidget.setText(id);
            } else {
                valueTextWidget.setText(valueArray[0]);
            }
            OWLGUIUtilities.enable(valueTextWidget, false);
        }

        boolean showLanguage = false;

        // datatype
        if (descriptions != null) {
            // type/range is optional (if not present the first and only entry in array equals null)
            if (typeArray.length == 1 && typeArray[0] == null) {
                typeTextWidget.setText(""); //$NON-NLS-1$
                OWLGUIUtilities.enable(typeTextWidget, false);
            } else {
                String id = OWLGUIUtilities.getEntityLabel(typeArray);
                typeTextWidget.setText(id);
                OWLGUIUtilities.enable(typeTextWidget, false);
                if (annotation.getValue() instanceof OWLLiteral && descriptions.size() >= 4 && descriptions.get(3) != null) {
                    showLanguage = true;
                }
            }
        }

        // language
        if (descriptions != null) {
            int index = 0;
            String[] array = descriptions.get(3);
            if (showLanguage) {
                String id = array[0];
                if (id != null) {
                    index = languageCombo.indexOf(id);
                    if (index < 0) {
                        languageCombo.add(id);
                        index = languageCombo.indexOf(id);
                    }
                }
            }
            languageCombo.select(index);
            OWLGUIUtilities.enable(languageCombo, false);
        }

        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, new LocatedAxiom(annotation, isLocal)) {
            @Override
            public void savePressed() {
                try {
                    String[] values = getNewValues(propertyTextWidget, valueTextWidget, typeTextWidget, languageCombo);
                    new EditEntityAnnotation(_project, _sourceOwlModel.getOntologyURI(), _id, 
                            OWLUtilities.toString(getAxiom(), _localOwlModel.getOntology()), values).run();
                    
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.AnnotationsPropertyPage2_15, propertyTextWidget.getShell());
                    propertyTextWidget.setFocus();
                    return;
                } catch (CommandException e) {
                    handleException(e, Messages.AnnotationsPropertyPage2_15, propertyTextWidget.getShell());
                    propertyTextWidget.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(propertyTextWidget, false);
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
                
                if (typeArray.length > 1) {
                    typeTextWidget.setText(typeArray[1]);
                } else {
                    typeTextWidget.setText(typeArray[0]);
                }
            }
        };
        formRow.init(rowHandler);

        final boolean[] systemChanged = {false};
        final boolean[] userChanged = {true}; //initial: false, edit: true
        propertyTextWidget.addModifyListener(new ModifyPropertyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                autoComplete1AnnotationProperty(propertyTextWidget.getText(), typeTextWidget, systemChanged, userChanged, _owlModel);                
                verifyInput(formRow, propertyTextWidget, valueTextWidget, typeTextWidget);
            }
        });
        valueTextWidget.addModifyListener(new ModifyPropertyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyTextWidget, valueTextWidget, typeTextWidget);
            }
        });
        typeTextWidget.addModifyListener(new ModifyPropertyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                autoComplete2Type(typeTextWidget, systemChanged, userChanged);
                verifyInput(formRow, propertyTextWidget, valueTextWidget, typeTextWidget);
            }
        });
    }

    /**
     * Creates the empty row.
     * 
     * @return the composite
     */
    private Composite createEmptyRow() {
        final EmptyFormRow formRow = new EmptyFormRow(_toolkit, _annotationsComp, NUM_COLS);
        // text widgets
        final StyledText propertyText = new PropertyText(formRow.getParent(), _owlModel, _owlModel, PropertyText.ANNOTATION_PROPERTY).getStyledText();
        formRow.addWidget(propertyText);
        addSimpleWidget(propertyText);

        final StyledText valueText = new StringText(formRow.getParent()).getStyledText(); // TODO some annotations can also have URIs as values
        formRow.addWidget(valueText);
        addSimpleWidget(valueText);

        final StyledText typeText = new DatatypeAndIndividualText(formRow.getParent(), _owlModel, _owlModel).getStyledText();
        formRow.addWidget(typeText);
        addSimpleWidget(typeText);

        final CCombo languageCombo = OWLGUIUtilities.createLanguageComboBox(formRow.getParent(), true);
        formRow.addWidget(languageCombo);
        addSimpleWidget(languageCombo);

        OWLGUIUtilities.initStringOrLiteralSwitch(typeText, languageCombo, _owlModel);
        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, _owlModel, null) {

            @Override
            public void savePressed() {
                // nothing to do
            }

            @Override
            public void addPressed() {
                // add new entry
                try {
                    String[] newValues = getNewValues(propertyText, valueText, typeText, languageCombo);
                    if(getMainPage().getSelection().getFirstElement() instanceof AnonymousIndividualViewItem){
                        AnonymousIndividualViewItem individualViewItem = (AnonymousIndividualViewItem)getMainPage().getSelection().getFirstElement();
                        new AddAnonymousIndividualAnnotation(_project, _sourceOwlModel.getOntologyURI(), individualViewItem.getIndividual(), newValues).run();
                    } else {
                        new AddEntityAnnotation(_project, _sourceOwlModel.getOntologyURI(), _id, newValues).run();
                    }
                } catch (NeOnCoreException k2e) {
                    handleException(k2e, Messages.AnnotationsPropertyPage2_25, languageCombo.getShell());
                    return;
                } catch (CommandException e) {
                    handleException(e, Messages.AnnotationsPropertyPage2_25, languageCombo.getShell());
                    return;
                }
                initAnnotationsSection(true);
                layoutSections();
                _form.reflow(true);
            }

            @Override
            public void ensureQName() {
                // nothing to do
            }

        };
        formRow.init(rowHandler);

        final boolean[] systemChanged = {false};
        final boolean[] userChanged = {false}; //initial: false, edit: true
        
        propertyText.addModifyListener(new ModifyPropertyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                autoComplete1AnnotationProperty(propertyText.getText(), typeText, systemChanged, userChanged, _owlModel);
                verifyInput(formRow, propertyText, valueText, typeText);
            }
        });
        valueText.addModifyListener(new ModifyPropertyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyText, valueText, typeText);
            }
        });
        typeText.addModifyListener(new ModifyPropertyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                autoComplete2Type(typeText, systemChanged, userChanged);
                verifyInput(formRow, propertyText, valueText, typeText);
            }
        });
        return propertyText;
    }

    private void verifyInput(final AbstractFormRow formRow, final StyledText propertyText, final StyledText valueText, final StyledText typeText) {
        boolean error = false;
        String message = null;
        int type = IMessageProvider.NONE;
        if (propertyText.getText().trim().length() == 0) {
            if(valueText.getText().trim().length() == 0 && valueText.getText().trim().length() == 0){//language
                message = null;
                type = IMessageProvider.NONE;
                error = true; //its not an error but the add button should be disabled as well
            }else{
                message = Messages.AnnotationsPropertyPage2_2;
                type = IMessageProvider.ERROR;
                error = true; 
            }
        }else if (valueText.getText().trim().length() == 0) {
            message = Messages.AnnotationsPropertyPage2_7;
            type = IMessageProvider.ERROR;
            error = true;
        } else{

            String datatype = IRIUtils.ensureValidIdentifierSyntax(typeText.getText());
            String expandedRange = _namespaces.expandString(datatype);
            String value = valueText.getText();
            try {
                OWLGUIUtilities.verifyUserInput(value, expandedRange, _owlModel);
            } catch (UnknownDatatypeException e) {
                message = e.getMessage();
                type = IMessageProvider.WARNING;
                error = false;
            } catch (IllegalArgumentException e) {
                message = e.getMessage();
                type = IMessageProvider.ERROR;
                error = true;
            }
        }
        
        _form.setMessage(message, type);
        formRow.getSubmitButton().setEnabled(!error);
    }

    /**
     * Returns the values recently entered into the text fields. For the properties OWLGUIUtilities.getValidURI() 
     * is called, because we may have an ID without namespace.
     * 
     * @param propertyText the property text
     * @param valueText the value text
     * @param typeText the type text
     * @param languageCombo the language combo
     * 
     * @return the new values
     * 
     * @throws NeOnCoreException 
     * @throws ControlException
     */
    private String[] getNewValues(final StyledText propertyText, final StyledText valueText, final StyledText typeText, final CCombo languageCombo) throws NeOnCoreException, CommandException {

        String property = OWLGUIUtilities.getValidURI(propertyText.getText(), _ontologyUri, _project);
        String value = valueText.getText();
        String typeString = typeText.getText();
        String language = languageCombo.getText();

        String type = ""; //$NON-NLS-1$
        if (typeString.length() > 0) {
            type = OWLGUIUtilities.getValidURI(typeString, _ontologyUri, _project);
        }

        // range is optional, so may be empty
        if (valueText.getText().trim().length() == 0) {
            value = ""; //$NON-NLS-1$
        }
        String[] newValues = new String[] {property, value, type, language};
        return newValues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.owl.gui.properties.BasicOWLEntityPropertyPage#getSections()
     */
    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_annotationsSection);
        return sections;
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    @Override
    public void refresh() {
        super.refresh();
        initAnnotationsSection(false);

        layoutSections();
        _form.reflow(true);
        _form.setMessage(null, IMessageProvider.NONE);
    }

    @Override
    public void update() {
        super.update();
        initAnnotationsSection(false);

        layoutSections();
        _form.reflow(true);
    }

    private TreeSet<String[]> getSortedSet(String[][] annotationsArray) {
        Set<String[]> unsortedSet = new HashSet<String[]>();
        TreeSet<String[]> sortedSet = new TreeSet<String[]>(new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                try {
                    String ontologyUri1 = o1[1];
                    String ontologyUri2 = o2[1];
                    OWLOntology ontology = _owlModel.getOntology();
                    OWLAxiom axiom1 = (OWLAxiom) OWLUtilities.axiom(o1[0], ontology);
                    int result = 1;
                    if (axiom1 instanceof OWLAnnotationAssertionAxiom) {
                        OWLAnnotationAssertionAxiom ea = (OWLAnnotationAssertionAxiom)axiom1;
                        OWLAnnotationProperty annotationProperty = ea.getAnnotation().getProperty();
                        String propertyUri1 = OWLGUIUtilities.getEntityLabel(annotationProperty, ontologyUri1, _project);
                        
                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], ontology);
                        if (axiom2 instanceof OWLAnnotationAssertionAxiom) {
                            OWLAnnotationAssertionAxiom ea2 = (OWLAnnotationAssertionAxiom)axiom2;
                            OWLAnnotationProperty annotationProperty2 = ea2.getAnnotation().getProperty();
                            String propertyUri2 = OWLGUIUtilities.getEntityLabel(annotationProperty2, ontologyUri2, _project);
                            result = propertyUri1.compareTo(propertyUri2);
                            if (result == 0) {
                                result = OWLUtilities.toString(ea2.getAnnotation().getValue(), ontology).compareTo(
                                        OWLUtilities.toString(ea.getAnnotation().getValue(), ontology));  
                            }
                        }
                    }
                    if (result == 0) {
                        int x = ontologyUri1.compareTo(ontologyUri2);
                        if (x == 0) {
                            // make sure multiple hits with the same propertyUri are added multiple times
                            return 1;
                        }
                        return x;
                    }
                    return result;
                } catch (NeOnCoreException e) {
                    new NeonToolkitExceptionHandler().handleException(e);
                }
                return 0;
            }
        });
        for (String[] hit: annotationsArray) {
            unsortedSet.add(hit);
        }
        sortedSet.addAll(unsortedSet);
        return sortedSet;
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
    }
}
