/*****************************************************************************
 * Copyright (c) 2010 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
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
import org.neontoolkit.gui.properties.IImagePropertyPage;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.forms.AxiomRowHandler;
import com.ontoprise.ontostudio.owl.gui.util.forms.EmptyFormRow;
import com.ontoprise.ontostudio.owl.gui.util.forms.FormRow;
import com.ontoprise.ontostudio.owl.gui.util.textfields.ClassText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationPropertyDomain;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationPropertyRange;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetAnnotationPropertyDomains;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetAnnotationPropertyRanges;
/**
 * 
 * @author Nico Stieler
 */
public class AnnotationPropertyPage2 extends AbstractOWLMainIDPropertyPage implements IImagePropertyPage {
   
    private static final int DOMAIN = 1;
    private static final int RANGE = 2;

    private static final int NUM_COLS = 3;

    /*
     * JFace Forms variables
     */
    private Section _domainSection;
    private Section _rangeSection;

    private Composite _domainFormComposite;
    private Composite _rangeFormComposite;


    public AnnotationPropertyPage2() {
       super();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.owl.gui.properties.BasicOWLEntityPropertyPage#createMainArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createMainArea(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_ANNOTATION_PROPERTIES_VIEW);
        super.createMainArea(composite);
        Composite body = prepareForm(composite);
        
        createDomainArea(body);
        createRangeArea(body);
        
        layoutSections();
        _form.reflow(true);
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_domainSection);
        sections.add(_rangeSection);
        return sections;
    }

    @Override
    public void dispose() {
        IWorkbenchPage page = OWLPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (_partListener != null && page != null) {
            page.removePartListener(_partListener);
        }   
    }

    @Override
    public void refreshComponents() {
        super.refreshComponents();

        initDomainSection(false);
        initRangeSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void update() {
        super.updateComponents();

        initDomainSection(false);
        initRangeSection(false);

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
    }

    @Override
    protected String getTitle() {
        return null;
    }

   /**
    * Create domain area
    */
   private void createDomainArea(Composite composite) {
       _domainSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
       _domainSection.setText(Messages.AnnotationPropertyPropertyPage_Domain);
       _domainSection.setDescription(Messages.AnnotationPropertyPropertyPage_IntersectionOfDomain);
       _domainSection.addExpansionListener(new ExpansionAdapter() {
           @Override
           public void expansionStateChanged(ExpansionEvent e) {
               _form.reflow(true);
               // closeToolBar();
           }
       });
   
       _domainFormComposite = _toolkit.createComposite(_domainSection, SWT.NONE);
       _domainFormComposite.setLayout(new GridLayout());
       _domainFormComposite.setLayoutData(new ColumnLayoutData());
   
       _toolkit.adapt(_domainFormComposite);
       _domainSection.setClient(_domainFormComposite);
   }

   private void initDomainSection(boolean setFocus) {
       cleanup();
       clearComposite(_domainFormComposite);
       try {
           String[][] domains = new GetAnnotationPropertyDomains(_project, _ontologyUri, _id).getResults();
           TreeSet<String[]> sortedSet = getSortedSet(domains);
           for (String[] domain: sortedSet) {
               String axiomText = domain[0];
               String ontologyUri = domain[1];
               boolean isLocal = ontologyUri.equals(_ontologyUri);
               OWLAnnotationPropertyDomainAxiom dataPropertyDomain = (OWLAnnotationPropertyDomainAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);
   
               createRow(new LocatedAxiom(dataPropertyDomain, isLocal), ontologyUri, false, DOMAIN);
           }
       } catch (NeOnCoreException e1) {
           handleException(e1, Messages.AnnotationPropertyPropertyPage_ErrorRetrievingData, _domainSection.getShell());
       } catch (CommandException e) {
           handleException(e, Messages.AnnotationPropertyPropertyPage_ErrorRetrievingData, _domainSection.getShell());
       }
   
       Label createNewLabel = new Label(_domainFormComposite, SWT.NONE);
       createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
       createNewLabel.setText(Messages.AnnotationPropertyPropertyPage_0);
       Composite activeComposite = createEmptyRow(true, DOMAIN);
       if (setFocus) {
           activeComposite.setFocus();
       }
   }

   /**
    * Create range area
    */
   private void createRangeArea(Composite composite) {
       _rangeSection = _toolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
       _rangeSection.setText(Messages.AnnotationPropertyPropertyPage_Range);
       _rangeSection.setDescription(Messages.AnnotationPropertyPropertyPage_IntersectionOfRange);
       _rangeSection.addExpansionListener(new ExpansionAdapter() {
           @Override
           public void expansionStateChanged(ExpansionEvent e) {
               _form.reflow(true);
           }
       });
   
       _rangeFormComposite = _toolkit.createComposite(_rangeSection, SWT.NONE);
       _rangeFormComposite.setLayout(new GridLayout());
       ColumnLayoutData data = new ColumnLayoutData();
       _rangeFormComposite.setLayoutData(data);
   
       _toolkit.adapt(_rangeFormComposite);
       _rangeSection.setClient(_rangeFormComposite);
   }

   private void initRangeSection(boolean setFocus) {
       cleanup();
       clearComposite(_rangeFormComposite);
   
       try {
           String[][] ranges = new GetAnnotationPropertyRanges(_project, _ontologyUri, _id).getResults();
           TreeSet<String[]> sortedSet = getSortedSet(ranges);
           for (String[] domain: sortedSet) {
               String axiomText = domain[0];
               String ontologyUri = domain[1];
               boolean isLocal = ontologyUri.equals(_ontologyUri);
               OWLAnnotationPropertyRangeAxiom annotationPropertyRange = (OWLAnnotationPropertyRangeAxiom) OWLUtilities.axiom(axiomText, _namespaces, _factory);
   
               createRow(new LocatedAxiom(annotationPropertyRange, isLocal), ontologyUri, true, RANGE);
           }
       } catch (NeOnCoreException e) {
           handleException(e, Messages.AnnotationPropertyPropertyPage_ErrorRetrievingData, _domainSection.getShell());
       } catch (CommandException e) {
           handleException(e, Messages.AnnotationPropertyPropertyPage_ErrorRetrievingData, _domainSection.getShell());
       }
       Label createNewLabel = new Label(_rangeFormComposite, SWT.NONE);
       createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
       createNewLabel.setText(Messages.AnnotationPropertyPropertyPage_0);
       Composite activeComposite = createEmptyRow(true, RANGE);
       if (setFocus) {
           activeComposite.setFocus();
       }
   }

    private void createRow(LocatedAxiom locatedAxiom, String ontologyUri, boolean enabled, final int mode) throws NeOnCoreException {
        Composite parent;
        if (mode == DOMAIN) {
            parent = _domainFormComposite;
        } else {
            parent = _rangeFormComposite;
        }
        boolean imported = !locatedAxiom.isLocal();        
        OWLModel sourceOwlModel =_owlModel;
        if(imported){
            sourceOwlModel = OWLModelFactory.getOWLModel(ontologyUri, _project);
        }

        FormRow row = new FormRow(_toolkit, parent, NUM_COLS, imported, ontologyUri,sourceOwlModel.getProjectId(),_id);
        OWLAxiom axiom = locatedAxiom.getAxiom();
        OWLObject desc = mode == DOMAIN ? ((OWLAnnotationPropertyDomainAxiom) axiom).getDomain() : ((OWLAnnotationPropertyRangeAxiom) axiom).getRange();
        
//        String name = null;NICO remove me
//        outer:
//        if(locatedAxiom != null && locatedAxiom.getAxiom() != null){
//            if (mode == DOMAIN) {
//                try {
//                    String[] split = _actualDomain_Range[0].split(" "); //$NON-NLS-1$
//                    name = split[split.length - 1].replace("]","").replace("[",""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//                } catch (NullPointerException e) {
//                }
//            } else {
//                try {
//                    String[] split = _actualDomain_Range[0].split(" "); //$NON-NLS-1$
//                    name = split[split.length - 1].replace("]","").replace("[",""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//                } catch (NullPointerException e) {
//                }
//            }
//        }
        
        final StyledText text;
        ClassText classText = new ClassText(row.getParent(), _owlModel, sourceOwlModel);
        text = classText.getStyledText();
        text.setData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID, classText);
        addSimpleWidget(text);
    
        final String[] array = getArrayFromDescription(desc);
        text.setText(OWLGUIUtilities.getEntityLabel(array));
        text.setData(desc);
        OWLGUIUtilities.enable(text, false);
        row.addWidget(text);
    
        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel, sourceOwlModel, new LocatedAxiom(axiom, true)) {
    
            @Override
            public void savePressed() {
                // save modified entries
                String input = text.getText();
                try {
                    remove();
                   
                    if (mode == DOMAIN) {
                        String value = OWLPlugin.getDefault().getSyntaxManager().parseUri(input, _localOwlModel);
                        new CreateAnnotationPropertyDomain(_project, _sourceOwlModel.getOntologyURI(), _id, value).run();
                        initDomainSection(false);
                    } else {
                        String value = OWLPlugin.getDefault().getSyntaxManager().parseUri(input, _localOwlModel);
                        new CreateAnnotationPropertyRange(_project, _sourceOwlModel.getOntologyURI(), _id, value).run();
                        initRangeSection(false);
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.AnnotationPropertyPropertyPage_1, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException ce) {
                    handleException(ce, Messages.AnnotationPropertyPropertyPage_1, text.getShell());
                    text.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(text, false);
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
                    text.setText(array[1]);
                } else {
                    text.setText(array[0]);
                }
            }
        };
        row.init(rowHandler);
    }
    
    private Composite createEmptyRow(final boolean enabled, final int mode) {
        Composite parent;
        if (mode == DOMAIN) {
            parent = _domainFormComposite;
        } else {
            parent = _rangeFormComposite;
        }
        final EmptyFormRow row = new EmptyFormRow(_toolkit, parent, NUM_COLS);
        final StyledText text;
        ClassText classText = new ClassText(row.getParent(), _owlModel,_owlModel);
        text = classText.getStyledText();
        addSimpleWidget(text);
        row.addWidget(text);
    
        AxiomRowHandler rowHandler = new AxiomRowHandler(this, _owlModel,_owlModel, null) {
    
            @Override
            public void savePressed() {
                // nothing to do
            }
    
            @Override
            public void addPressed() {
                // add new entry
                String input = text.getText();
                try {
                   
                    if (mode == DOMAIN) {
                        String value = OWLPlugin.getDefault().getSyntaxManager().parseUri(input, _localOwlModel);
                        new CreateAnnotationPropertyDomain(_project, _sourceOwlModel.getOntologyURI(), _id, value).run();
                    } else {
                        String value = OWLPlugin.getDefault().getSyntaxManager().parseUri(input, _localOwlModel);
                        new CreateAnnotationPropertyRange(_project, _sourceOwlModel.getOntologyURI(), _id, value).run();
                    }
                } catch (NeOnCoreException ce) {
                    handleException(ce, Messages.AnnotationPropertyPropertyPage_1, text.getShell());
                    text.setFocus();
                    return;
                } catch (CommandException ce) {
                    handleException(ce, Messages.AnnotationPropertyPropertyPage_1, text.getShell());
                    text.setFocus();
                    return;
                }
                OWLGUIUtilities.enable(text, false);
    
                if (mode == DOMAIN) {
                    initDomainSection(true);
                } else {
                    initRangeSection(true);
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
            public void modifyText(ModifyEvent e) {
                if (text.getText().trim().length() == 0) {
                    row.getAddButton().setEnabled(false);
                } else {
                    row.getAddButton().setEnabled(true);
                }
            }
        });
        return text;
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
                    IRI uri1 = null;
                    IRI uri2 = null;
                    if (axiom1 instanceof OWLAnnotationPropertyDomainAxiom) {
                        OWLAnnotationPropertyDomainAxiom domain = (OWLAnnotationPropertyDomainAxiom) axiom1;
                        uri1 = domain.getDomain();

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                        if (axiom2 instanceof OWLAnnotationPropertyDomainAxiom) {
                            OWLAnnotationPropertyDomainAxiom domain2 = (OWLAnnotationPropertyDomainAxiom) axiom2;
                            uri2 = domain2.getDomain();
                        }

                    } else if (axiom1 instanceof OWLAnnotationPropertyRangeAxiom) {
                        OWLAnnotationPropertyRangeAxiom range = (OWLAnnotationPropertyRangeAxiom) axiom1;
                        uri1 = range.getRange();

                        OWLAxiom axiom2 = (OWLAxiom) OWLUtilities.axiom(o2[0], _namespaces, _factory);
                        if (axiom2 instanceof OWLAnnotationPropertyRangeAxiom) {
                            OWLAnnotationPropertyRangeAxiom range2 = (OWLAnnotationPropertyRangeAxiom) axiom2;
                            uri2 = range2.getRange();
                        }
                    }

                    int localResult = 0;
                    if(uri1 != null && uri2 != null){
                        localResult = uri1.compareTo(uri2);
                    }

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
