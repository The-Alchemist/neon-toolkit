/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.ontology;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLStringLiteral;
import org.semanticweb.owlapi.model.OWLTypedLiteral;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.UnknownDatatypeException;
import com.ontoprise.ontostudio.owl.gui.util.forms.AbstractFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.OntologyAnnotationRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.textfields.DatatypeText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.PropertyText;
import com.ontoprise.ontostudio.owl.gui.util.textfields.StringText;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.annotations.AddOntologyAnnotation;
import com.ontoprise.ontostudio.owl.model.commands.annotations.GetOntologyAnnotations;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;

public class OntologyAnnotationsPropertyPage2 extends AbstractOWLIdPropertyPage {

    /*
     * The number of columns for a row (including buttons)
     */
    private static final int NUM_COLS = 6;

    /*
     * JFace Forms variables
     */
    private Section _annotationsSection;

    private Composite _annotationsComp;

    @Override
    public Composite createContents(Composite parent) {
        Composite body = prepareForm(parent);

        createAnnotationsArea(body);
        _form.reflow(true);
        return body;
    }

    private void createAnnotationsArea(Composite composite) {
        // Annotations
        _annotationsSection = _toolkit.createSection(composite, Section.TITLE_BAR);
        _annotationsSection.setText(Messages.OntologyAnnotationsPropertyPage2_2);
        _annotationsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _annotationsComp = _toolkit.createComposite(_annotationsSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        _annotationsComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _annotationsComp.setLayoutData(data);

        _toolkit.adapt(_annotationsComp);
        _annotationsSection.setClient(_annotationsComp);
    }

    private void initAnnotationsSection(boolean setFocus) {
        clearComposite(_annotationsComp);
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = _manager.getVisitor(_owlModel, idDisplayStyle);
        createAnnotationRowTitles(_annotationsComp);
        try {
            // it's an ontology (i.e. not a real entity)
            String[] annots = new GetOntologyAnnotations(_project, _ontologyUri).getResults();

            for (String annotationText: annots) {
                handleAnnotationValue(visitor, annotationText);
            }
        } catch (NeOnCoreException e1) {
            new NeonToolkitExceptionHandler().handleException(e1);
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        Label createNewLabel = new Label(_annotationsComp, SWT.NONE);
        createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createNewLabel.setText(Messages.OntologyAnnotationsPropertyPage2_0);
        Composite activeComposite = createEmptyRow();
        if (setFocus) {
            activeComposite.setFocus();
        }
    }

    private Composite createEmptyRow() {
        final EmptyFormRow formRow = new EmptyFormRow(_toolkit, _annotationsComp, NUM_COLS);
        // text widgets
        final StyledText propertyText = new PropertyText(formRow.getParent(), _owlModel, PropertyText.ANNOTATION_PROPERTY).getStyledText();
        formRow.addWidget(propertyText);
        addSimpleWidget(propertyText);

        final StyledText valueText = new StringText(formRow.getParent()).getStyledText(); // TODO some annotations can also have URIs as values
        formRow.addWidget(valueText);
        addSimpleWidget(valueText);

        final StyledText typeText = new DatatypeText(formRow.getParent(), _owlModel).getStyledText();
        formRow.addWidget(typeText);
        addSimpleWidget(typeText);

        final CCombo languageCombo = OWLGUIUtilities.createLanguageComboBox(formRow.getParent(), true);
        formRow.addWidget(languageCombo);
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
                    String[] newValues = getNewValues(propertyText, valueText, typeText, languageCombo);

                    new AddOntologyAnnotation(_project, _id, newValues).run();
                } catch (NeOnCoreException k2e) {
                    new NeonToolkitExceptionHandler().handleException(Messages.OntologyAnnotationsPropertyPage2_24, k2e, typeText.getShell());
                    typeText.setFocus();
                    return;
                } catch (CommandException e) {
                    new NeonToolkitExceptionHandler().handleException(Messages.OntologyAnnotationsPropertyPage2_24, e, typeText.getShell());
                    typeText.setFocus();
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

        propertyText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (propertyText.getText().trim().length() == 0) {
                    formRow.getAddButton().setEnabled(false);
                    formRow.getCancelButton().setEnabled(false);
                } else {
                    formRow.getAddButton().setEnabled(true);
                    formRow.getCancelButton().setEnabled(true);
                }
            }
        });
        
        
        propertyText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyText, valueText, typeText);
            }

        });

        valueText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyText, valueText, typeText);
            }

        });
        
        typeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyText, valueText, typeText);
            }

        });
        
        return propertyText;
    }

    private void createAnnotationRowTitles(Composite comp) {
        Composite rowComp = _toolkit.createComposite(comp);
		GridLayout layout = new GridLayout(NUM_COLS, false);
        layout.marginHeight = 0;
        rowComp.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        rowComp.setLayoutData(layoutData);
        Color color = _annotationsSection.getTitleBarForeground();

        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 200;
        Text l1 = new Text(rowComp, SWT.NONE);
        l1.setText(Messages.AnnotationsPropertyPage2_AnnotationProperty);
        l1.setForeground(color);
        l1.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
		data.widthHint = 200;
        Text l2 = new Text(rowComp, SWT.NONE);
        l2.setText(Messages.AnnotationsPropertyPage2_Value);
        l2.setForeground(color);
        l2.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        data.widthHint = 200;
        Text l3 = new Text(rowComp, SWT.NONE);
        l3.setText(Messages.AnnotationsPropertyPage2_Type);
        l3.setForeground(color);
        l3.setLayoutData(data);

		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        data.widthHint = 50;
        data.horizontalAlignment = GridData.CENTER;
        Text l4 = new Text(rowComp, SWT.NONE);
        l4.setText(Messages.AnnotationsPropertyPage2_Language);
        l4.setForeground(color);
        l4.setLayoutData(data);

		Button b1 = createEditButton(rowComp, false);
//		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
//		data.heightHint = 20;
//		data.verticalAlignment = GridData.END;
//		b1.setLayoutData(data);
		b1.setVisible(false);

		Button b2 = createRemoveButton(rowComp, false);
//		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
//		data.heightHint = 20;
//		data.verticalAlignment = GridData.END;
//		b2.setLayoutData(data);
		b2.setVisible(false);

    }

    /**
     * @param visitor
     * @param annotationProperty
     * @param o
     * @throws NeOnCoreException
     */
    private void handleAnnotationValue(OWLObjectVisitorEx visitor, String annotationText) throws NeOnCoreException {
        OWLAnnotationAssertionAxiom dummyEntityAnnotation = (OWLAnnotationAssertionAxiom) OWLUtilities.axiom(annotationText, _namespaces, _factory);
        OWLAnnotationProperty prop = dummyEntityAnnotation.getAnnotation().getProperty();
        OWLObject o = dummyEntityAnnotation.getAnnotation().getValue();
        String language = OWLCommandUtils.EMPTY_LANGUAGE;
        String[] propArray = (String[]) prop.accept(visitor);
        ArrayList<String[]> contents = new ArrayList<String[]>();
        if (o instanceof OWLAnonymousIndividual) {
//            OWLIndividual individual = (OWLIndividual) o;
//            String[] indivArray = (String[]) individual.accept(visitor);
//            
//            // FIXME not sure about this. what' s the datatype here?
//            String dataType = OWLConstants.RDFS_LITERAL; //$NON-NLS-1$
//            contents.add(propArray);
//            contents.add(indivArray);
//            contents.add(new String[] {null});
//            contents.add(new String[] {null});
//            createAnnotationsRow(prop, OWLUtilities.toString(o), language, dataType, contents);
            // TODO: migration
            throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
            
        } else if (o instanceof IRI) {
            OWLIndividual individual = _manager.parseIndividual(((IRI)o).toString(), _owlModel);
            String[] indivArray = (String[]) individual.accept(visitor);
            String[] typeArray = new String[] {OWLAxiomUtils.OWL_INDIVIDUAL_LOCAL, OWLAxiomUtils.OWL_INDIVIDUAL};
            
            contents.add(propArray);
            contents.add(indivArray);
            contents.add(typeArray);
            contents.add(new String[] {null});
            createAnnotationsRow(prop, OWLGUIUtilities.getEntityLabel(indivArray), null, OWLGUIUtilities.getEntityLabel(typeArray), contents);

        } else if (o instanceof OWLLiteral) {
            OWLLiteral constant = (OWLLiteral)o;
            if (!constant.isOWLTypedLiteral()) {
                OWLStringLiteral untypedConstant = (OWLStringLiteral)o;
                String literal = untypedConstant.getLiteral();
                language = untypedConstant.getLang();
                String dataType = OWLConstants.RDFS_LITERAL;

                contents.add(propArray);
                contents.add(new String[] {literal});
                contents.add((String[]) OWLModelFactory.getOWLDataFactory(_project).getOWLDatatype(OWLUtilities.toIRI(dataType)).accept(visitor));
                contents.add(new String[] {language});
                createAnnotationsRow(prop, literal, language, dataType, contents);

            } else {
                OWLTypedLiteral typedConstant = (OWLTypedLiteral)o;
                OWLDatatype datatype = typedConstant.getDatatype();
                String literal = typedConstant.getLiteral();

                contents.add(propArray);
                contents.add(new String[] {literal});
                contents.add((String[]) datatype.accept(visitor));
                contents.add(new String[] {null});
                createAnnotationsRow(prop, literal, language, datatype.getIRI().toString(), contents);
            }
        } else {
            throw new IllegalArgumentException(Messages.OntologyAnnotationsPropertyPage2_1 + o);
        }
    }

    private void createAnnotationsRow(OWLAnnotationProperty prop, String annotationValueText, String language, String datatype, ArrayList<String[]> descriptions) throws NeOnCoreException {
        final FormRow formRow = new FormRow(_toolkit, _annotationsComp, NUM_COLS, false, "",_owlModel.getProjectId(),_id); //$NON-NLS-1$

        // text widgets
        final StyledText propertyText = new PropertyText(formRow.getParent(), _owlModel, PropertyText.ANNOTATION_PROPERTY).getStyledText();
        formRow.addWidget(propertyText);

        final StyledText valueText = new StringText(formRow.getParent()).getStyledText();
        formRow.addWidget(valueText);

        final StyledText typeText = new DatatypeText(formRow.getParent(), _owlModel).getStyledText();
        formRow.addWidget(typeText);

        final CCombo languageCombo = OWLGUIUtilities.createLanguageComboBox(formRow.getParent(), false);
        formRow.addWidget(languageCombo);

        final String[] propertyArray = descriptions.get(0);
        final String[] rangeArray = descriptions.get(2);

        OWLGUIUtilities.initStringOrLiteralSwitch(typeText, languageCombo, _owlModel);

        if (descriptions != null && descriptions.size() > 0) {
            String id = OWLGUIUtilities.getEntityLabel(propertyArray);
            propertyText.setText(id);
            OWLGUIUtilities.enable(propertyText, false);
        }

        if (descriptions != null && descriptions.size() > 0) {
            String[] array = descriptions.get(1);
            String id = array[0];
            valueText.setText(id);
            OWLGUIUtilities.enable(valueText, false);
        }

        if (descriptions != null && descriptions.size() > 0) {
            // type/range is optional
            if (rangeArray.length == 1 && rangeArray[0] == null) {
                OWLGUIUtilities.enable(typeText, false);
            } else {
                String id = OWLGUIUtilities.getEntityLabel(rangeArray);
                typeText.setText(id);
                OWLGUIUtilities.enable(typeText, false);
            }
        }

        if (descriptions != null && descriptions.size() > 0) {
            String[] array = descriptions.get(3);
            String id = array[0];
            int index = 0;
            if (id != null) {
                // no language is provided
                index = languageCombo.indexOf(id);
            }
            if (index < 0) {
                languageCombo.add(id);
                index = languageCombo.indexOf(id);
            }
            languageCombo.select(index);
            OWLGUIUtilities.enable(languageCombo, false);
        }

        OntologyAnnotationRowHandler rowHandler = new OntologyAnnotationRowHandler(this, _owlModel, prop.getIRI().toString(), annotationValueText, language, datatype) {

            @Override
            public void savePressed() {
                try {
                    String[] values = getNewValues(propertyText, valueText, typeText, languageCombo);
                    removeOldAnnotation();

                    new AddOntologyAnnotation(_project, _id, values).run();
                } catch (NeOnCoreException k2e) {
                    new NeonToolkitExceptionHandler().handleException(Messages.OntologyAnnotationsPropertyPage2_14, k2e, languageCombo.getShell());
                    typeText.setFocus();
                    return;
                } catch (CommandException e) {
                    new NeonToolkitExceptionHandler().handleException(Messages.OntologyAnnotationsPropertyPage2_14, e, languageCombo.getShell());
                    typeText.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(propertyText, false);
                refresh();
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
                if (rangeArray.length > 1) {
                    typeText.setText(rangeArray[1]);
                } else {
                    typeText.setText(rangeArray[0]);
                }
            }

        };
        formRow.init(rowHandler);
        
        propertyText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyText, valueText, typeText);
            }

        });

        valueText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyText, valueText, typeText);
            }

        });
        
        typeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verifyInput(formRow, propertyText, valueText, typeText);
            }

        });
    }
    
    private void verifyInput(final AbstractFormRow formRow, final StyledText propertyText, final StyledText valueText, final StyledText typeText) {
        boolean error = false;
        String message = null;
        int type = IMessageProvider.NONE;
        if (propertyText.getText().trim().length() == 0) {
            message = Messages.AnnotationsPropertyPage2_2;
            type = IMessageProvider.ERROR;
            error = true;
        }else if (valueText.getText().trim().length() == 0) {
            message = Messages.AnnotationsPropertyPage2_7;
            type = IMessageProvider.ERROR;
            error = true;
        } else{
            
            String datatype = typeText.getText();
            String expandedRange = _namespaces.expandString(datatype);
            String value = valueText.getText();
            try {
                OWLGUIUtilities.verifyUserInput(value, expandedRange);
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
     * Returns the values recently entered into the text fields. For the properties OWLGUIUtilities.getValidURI() is called, because we may have an ID without
     * namespace.
     * 
     * @param quantifierCombo
     * @param propertyText
     * @param valueText
     * @param typeText
     * @param languageCombo
     * @return
     * @throws NeOnCoreException
     * @throws ControlException
     */
    private String[] getNewValues(final StyledText propertyText, final StyledText valueText, final StyledText typeText, final CCombo languageCombo) throws NeOnCoreException,CommandException {
        String property = OWLGUIUtilities.getValidURI(propertyText.getText(), _ontologyUri, _project);
        String value = valueText.getText();
        String type = typeText.getText();
        String language = languageCombo.getText();

        // range is optional, so may be empty
        if (valueText.getText().trim().length() == 0) {
            value = Messages.OntologyAnnotationsPropertyPage2_25;
        }
        String[] newValues = new String[] {property, value, type, language};
        return newValues;
    }

    @Override
    public void refresh() {
        super.refresh();
        initAnnotationsSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void update() {
        super.update();
        initAnnotationsSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_annotationsSection);
        return sections;
    }

}
