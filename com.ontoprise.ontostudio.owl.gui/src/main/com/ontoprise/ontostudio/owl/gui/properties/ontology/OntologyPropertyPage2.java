/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.properties.CollabUserGroup;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.textfields.UriText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.imports.AddOntologyToImports;
import com.ontoprise.ontostudio.owl.model.commands.imports.GetImportedOntologies;
import com.ontoprise.ontostudio.owl.model.commands.imports.RemoveOntologyFromImports;


public class OntologyPropertyPage2 extends AbstractOWLMainIDPropertyPage {

    /*
     * JFace Forms variables
     */
    private Section _importsSection;
    private CCombo _ontologyCombo;

    private Composite _importsComp;

    private NamespacesArea _namespacesArea;

    private CollabUserGroup _userSection;

    /*
     * Controls
     */
    private Text _locText;

    public OntologyPropertyPage2() {
        super();
    }

    /**
     * Loads all areas of the view
     */
    @Override
    protected void createMainArea(Composite composite) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_ONTOLOGY_VIEW);
        super.createMainArea(composite);
        Composite body = prepareForm(composite);

        createImportsArea(body);
        createNamespacesArea(body);
        _userSection = new CollabUserGroup(body, _toolkit, _form);

        _form.reflow(true);
    }

    @Override
    public void refreshIdArea() {
        try {
            OWLModel model = OWLModelFactory.getOWLModel(_ontologyUri, _project);
            if (model != null) {
                _locText.setText(model.getPhysicalURI());
            }
            _uriText.setText(_id);
        } catch (Exception pe) {
            NeonToolkitExceptionHandler oeh = new NeonToolkitExceptionHandler();
            oeh.handleException(pe);
        }
    }

    @Override
    public void refreshComponents() {
        super.refreshComponents();
        initImportsSection();
        _namespacesArea.setOntologyUri(_ontologyUri);
        _namespacesArea.setProjectId(_project);
        _namespacesArea.initNamespacesSection(""); //$NON-NLS-1$

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        initImportsSection();
        _namespacesArea.initNamespacesSection(""); //$NON-NLS-1$
        // initSpeciesSection();

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
    }

    /**
     * Create imports area
     */
    private void createImportsArea(Composite composite) {
        _importsSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _importsSection.setText(Messages.OWLOntologyPropertyPage_Imports);
        _importsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _importsComp = _toolkit.createComposite(_importsSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        _importsComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _importsComp.setLayoutData(data);

        _toolkit.adapt(_importsComp);
        _importsSection.setClient(_importsComp);
    }

    private void initImportsSection() {
        clearComposite(_importsComp);
        Set<String> ontologies = new TreeSet<String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        try {
            String[] results = new GetImportedOntologies(_project, _ontologyUri).getResults();
            ontologies.addAll(Arrays.asList(results));
            for (String ontology: ontologies) {
                createImportsRow(_importsComp, ontology, false);
            }

            Label createNewLabel = new Label(_importsComp, SWT.NONE);
            createNewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            createNewLabel.setText(Messages.OntologyPropertyPage2_3);

            createImportsRow(_importsComp, null, true);
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
    }

    private void createImportsRow(Composite parent, String ontologyUri, boolean enabled) throws NeOnCoreException,CommandException {
        Composite rowComp = _toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 1;
        rowComp.setLayout(layout);

        GridData data = new GridData();
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;
        rowComp.setLayoutData(data);

        final List<Composite> widgets = new ArrayList<Composite>();

        if (ontologyUri != null) {
            final StyledText ontologyText = new UriText(rowComp, _owlModel).getStyledText();

            ontologyText.setToolTipText(Messages.OntologyPropertyPage2_10);
            ontologyText.setText(ontologyUri);
            OWLGUIUtilities.enable(ontologyText, false);

            widgets.add(ontologyText);

            final Button removeButton = createRemoveButton(rowComp, true);

            removeButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String ontologyToRemove = ontologyText.getText();
                    try {
                        new RemoveOntologyFromImports(_project, _ontologyUri, ontologyToRemove).run();
                    } catch (NeOnCoreException e1) {
                        new NeonToolkitExceptionHandler().handleException(Messages.OntologyPropertyPage2_27 + ontologyToRemove + Messages.OntologyPropertyPage2_28, e1, new Shell());
                    } catch (CommandException e1) {
                        new NeonToolkitExceptionHandler().handleException(Messages.OntologyPropertyPage2_27 + ontologyToRemove + Messages.OntologyPropertyPage2_28, e1, new Shell());
                    }
                    refresh();
                }
            });

        } else {
            String[] ontosArray = null;
            ArrayList<String> ontos = new ArrayList<String>();

            // get all ontologies available in this project
            Set<String> allOntos;
            allOntos = NeOnCorePlugin.getDefault().getOntologyProject(_project).getAvailableOntologyURIs();
            for (String onto: allOntos) {
                // ignore the ontology we are editing
                if (!onto.equals(_owlModel.getOntologyURI())) {
                    ontos.add(onto);
                }
            }

            // now remove those that are already being imported
            String[] importedOntos = new GetImportedOntologies(_project, _ontologyUri).getResults();
            for (String importedOntology: importedOntos) {
                ontos.remove(importedOntology);
            }

            String[] resultOntos = null;
            if (ontos.size() == 0) {
                // no ontologies available for import
                ontosArray = new String[] {Messages.OntologyPropertyPage2_29};
                resultOntos = ontosArray;
            } else {
                ontosArray = ontos.toArray(new String[ontos.size()]);
                Arrays.sort(ontosArray);
                int length = ontosArray.length + 1;
                resultOntos = new String[length];
                resultOntos[0] = Messages.OntologyPropertyPage2_31;
                System.arraycopy(ontosArray, 0, resultOntos, 1, ontosArray.length);
            }

            data = new GridData();
            data.widthHint = 250;
            data.verticalAlignment = SWT.TOP;
            data.horizontalAlignment = SWT.FILL;
            data.grabExcessHorizontalSpace = true;

            _ontologyCombo = OWLGUIUtilities.createComboWidget(resultOntos, rowComp, data, SWT.BORDER, true);

            boolean enable = true;
            if (ontosArray == null || ontosArray.length == 0 || (ontosArray.length == 1 && _ontologyCombo.getText().equals(Messages.OntologyPropertyPage2_29)) || _ontologyCombo.getText().equals(Messages.OntologyPropertyPage2_31)) {
                enable = false;
            }
            final Button addButton = OWLGUIUtilities.createAddButton(rowComp, enable);

            _ontologyCombo.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (_ontologyCombo.getText().equals(Messages.OntologyPropertyPage2_31)) {
                        addButton.setEnabled(false);
                    } else {
                        addButton.setEnabled(true);
                    }
                }

            });
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    // add new entry
                    String value = _ontologyCombo.getText();
                    try {
                        new AddOntologyToImports(_project, _ontologyUri, value).run();
                    } catch (NeOnCoreException e1) {
                        new NeonToolkitExceptionHandler().handleException(Messages.OntologyPropertyPage2_30, e1, new Shell());
                    } catch (CommandException e1) {
                        new NeonToolkitExceptionHandler().handleException(Messages.OntologyPropertyPage2_30, e1, new Shell());
                    }
                    refresh();
                }
            });
        }
    }

    /**
     * Create namespaces area
     */
    private void createNamespacesArea(Composite composite) {
        // Namespaces section is outsorced in NamespacesArea, since this section is used multiple times
        _namespacesArea = new NamespacesArea(composite, _toolkit, _form, _ontologyUri, _project, Messages.OWLOntologyPropertyPage_Namespaces);
    }

    @Override
    public void setSelection(IWorkbenchPart part, IStructuredSelection selection) {
        super.setSelection(part, selection);
        switchPerspective();
        Object first = selection.getFirstElement();
        if (first instanceof OntologyTreeElement) {
            OntologyTreeElement element = (OntologyTreeElement) first;
            _id = element.getId();
            _project = element.getProjectName();
            _ontologyUri = element.getOntologyUri();
            updateOwlModel();
            _namespacesArea.setProjectId(_project);
            _namespacesArea.setOntologyUri(_ontologyUri);

            try {
                IOntologyProject ontoProject = OntologyProjectManager.getDefault().getOntologyProject(_project);
                if (ontoProject != null) {
                    String[] users = ontoProject.getUsers(_ontologyUri);
                    if (users != null) {
                        _userSection.setVisible(true);
                        _userSection.setProject(_project);
                        _userSection.setOntology(_ontologyUri);
                        _userSection.update();
                    }
                    else {
                        _userSection.setVisible(false);
                    }
                } else {
                    _userSection.setVisible(false);
                }
            } catch (NeOnCoreException nce) {
                _userSection.setVisible(false);
                OWLPlugin.logError(nce);
            }
        }

        refresh();
    }

    @Override
    protected void selectIdTopControl() {
        // nothing to do here
    }

    @Override
    public Composite createGlobalContents(Composite composite) {
        _toolkit = new FormToolkit(composite.getDisplay());
        _idForm = _toolkit.createForm(composite);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 70;
        _idForm.setLayoutData(data);
        _idForm.setBackground(composite.getBackground());
        _idForm.setText(getTitle());

        Composite idComp = _idForm.getBody();
        idComp.setLayout(new GridLayout(3, false));

        // Name - Text Field
        (new Label(idComp, SWT.HORIZONTAL)).setText(Messages.OWLOntologyPropertyPage_URI);
        _uriText = new Text(idComp, SWT.SINGLE | SWT.BORDER);

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        _uriText.setLayoutData(gridData);
        _uriText.setEnabled(false);

        // Location - TextBox
        new Label(idComp, SWT.NONE).setText(Messages.OWLOntologyPropertyPage_Location);
        _locText = new Text(idComp, SWT.SINGLE | SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        _locText.setLayoutData(gridData);
        _locText.setEnabled(false);
        return idComp;
    }

    @Override
    public void refresh() {
        try {
            OWLModel model = OWLModelFactory.getOWLModel(_ontologyUri, _project);

            if (model != null) {
                _locText.setText(model.getPhysicalURI());
            }
            _uriText.setText(_id);
            initImportsSection();
            layoutSections();
            _form.reflow(true);

        } catch (Exception pe) {
            NeonToolkitExceptionHandler oeh = new NeonToolkitExceptionHandler();
            oeh.handleException(pe);
        }
    }

    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_importsSection);
        sections.add(_namespacesArea.getSection());
        // sections.add(_speciesSection);
        return sections;
    }

    @Override
    protected String getTitle() {
        return Messages.OntologyPropertyPage2_23;
    }

    @Override
    public void dispose() {
    }

}
