/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

public class RemoveAxiomWizardPage1 extends UserInputWizardPage {

    protected String _sourceEntityUri;
    protected String _targetEntityUri;
    protected String _ontologyUri;
    protected String _projectId;
    protected String _info;
    protected List<OWLAxiom> _dependentAxioms;
    protected List<OWLAxiom> _axiomsToRemove;
    protected List<Button> _checkboxes;
    protected String _deleteMode;
    protected List<OWLEntity> _entities;

    private OWLNamespaces _namespaces;
    private OWLDataFactory _factory;

    protected RemoveAxiomWizardPage1(String pageName, List<OWLAxiom> axioms, OWLNamespaces namespaces, String sourceEntityUri, String targetEntityUri, String deleteMode, List<OWLEntity> entities, String ontologyUri, String projectId) {
        super(pageName);
        setTitle(pageName);
        setDescription(Messages.RemoveAxiomWizardPage1_0 + Messages.RemoveAxiomWizardPage1_1);
        _ontologyUri = ontologyUri;
        _projectId = projectId;
        _sourceEntityUri = sourceEntityUri;
        _targetEntityUri = targetEntityUri;
        _entities = entities;
        _namespaces = namespaces;
        try {
            _factory = OWLModelFactory.getOWLDataFactory(projectId);
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }

        // initially all axioms are checked
        _axiomsToRemove = axioms;
        _checkboxes = new ArrayList<Button>();
        _dependentAxioms = new ArrayList<OWLAxiom>();
        _deleteMode = deleteMode;
    }

    public void createControl(Composite parent) {
        try {
            _info = getInfoString();
        } catch (NeOnCoreException e1) {
            _info = ""; //$NON-NLS-1$
        }

        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout());
        GridData data = new GridData();
        data.widthHint = 400;
        comp.setLayoutData(data);

        data = new GridData();
        data.verticalIndent = 8;
        data.horizontalSpan = 3;

        Label infoLabel = new Label(comp, SWT.NONE);
        infoLabel.setText(_info);
        infoLabel.setToolTipText(_info);
        infoLabel.setLayoutData(data);

        ScrolledComposite sComp = new ScrolledComposite(comp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        data.widthHint = 400;
        data.heightHint = 300;
        sComp.setLayoutData(data);

        try {
            // display axioms with checkbox
            initAxiomComposite(sComp);
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }

        Composite buttonComp = new Composite(comp, SWT.NONE);
        buttonComp.setLayout(new GridLayout(2, false));
        data = new GridData();
        data.horizontalAlignment = GridData.BEGINNING;
        buttonComp.setLayoutData(data);

        Button selectAll = new Button(buttonComp, SWT.PUSH);
        selectAll.setText(Messages.RemoveAxiomWizardPage1_2); 
        selectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectAll();
            }

        });

        Button unselectAll = new Button(buttonComp, SWT.PUSH);
        unselectAll.setText(Messages.RemoveAxiomWizardPage1_3); 
        unselectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                unSelectAll();
            }

        });
        setControl(comp);
    }

    protected void selectAll() {
        for (Button b: _checkboxes) {
            b.setSelection(true);
            b.notifyListeners(SWT.Selection, new Event());
        }
    }

    protected void unSelectAll() {
        for (Button b: _checkboxes) {
            b.setSelection(false);
            b.notifyListeners(SWT.Selection, new Event());
        }
    }

    private String getInfoString() throws NeOnCoreException {
        StringBuffer info = new StringBuffer();
        if (_sourceEntityUri.equals("") && _targetEntityUri.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
            List<OWLEntity> entities = ((RemoveAxiomWizard) getWizard()).getEntities();
            if (entities.size() > 1) {
                info.append(Messages.RemoveAxiomWizardPage1_4); 
            } else {
                info.append(Messages.RemoveAxiomWizardPage1_5); 
                info.append(_namespaces.abbreviateAsNamespace(entities.get(0).getURI().toString()));
                info.append("]"); //$NON-NLS-1$
            }
        } else {
            info.append(Messages.RemoveAxiomWizardPage1_6);
            OWLClassExpression sourceDesc = OWLUtilities.description(_sourceEntityUri, _namespaces, _factory);
            OWLClassExpression targetDesc = OWLUtilities.description(_targetEntityUri, _namespaces, _factory);
            String sourceUriToDisplay = OWLGUIUtilities.getEntityLabel(sourceDesc, _ontologyUri, _projectId);
            String targetUriToDisplay = OWLGUIUtilities.getEntityLabel(targetDesc, _ontologyUri, _projectId);
            if (sourceUriToDisplay.length() > 80) {
                sourceUriToDisplay = sourceUriToDisplay.substring(0, 80) + ("...");//$NON-NLS-1$
            }
            if (targetUriToDisplay.length() > 80) {
                targetUriToDisplay = targetUriToDisplay.substring(0, 80) + ("...");//$NON-NLS-1$
            }
            info.append(sourceUriToDisplay);
            info.append(" - "); //$NON-NLS-1$
            info.append(targetUriToDisplay);
            info.append("]"); //$NON-NLS-1$
        }
        return info.toString();
    }

    protected void initAxiomComposite(Composite parent) throws NeOnCoreException {
        ScrolledComposite sc = ((ScrolledComposite) parent);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite axiomComposite = new Composite(sc, SWT.NONE);
        sc.setContent(axiomComposite);
        axiomComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        axiomComposite.setLayout(new GridLayout(2, false));

        List<OWLAxiom> clonedAxiomsToRemove = new ArrayList<OWLAxiom>();
        clonedAxiomsToRemove.addAll(_axiomsToRemove);
        if (_axiomsToRemove != null && _axiomsToRemove.size() > 0) {
            for (final OWLAxiom axiom: _axiomsToRemove) {
                final Button checkbox = new Button(axiomComposite, SWT.CHECK);
                checkbox.setLayoutData(new GridData());
                checkbox.setSelection(true);
                checkbox.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (checkbox.getSelection()) {
                            if (!_axiomsToRemove.contains(axiom)) {
                                _axiomsToRemove.add(axiom);
                            }
                        } else {
                            _axiomsToRemove.remove(axiom);
                        }
                    }

                });
                GridData data = new GridData();
                checkbox.setLayoutData(data);
                _checkboxes.add(checkbox);

                Label text = getLabel(axiomComposite);

                OWLModel owlModel = ((RemoveAxiomWizard) getWizard()).getOwlModel();
                int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
                String[] result = (String[]) axiom.accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle));
                StringBuffer buffer = new StringBuffer();
                OWLUtilities.toString(axiom, buffer, owlModel.getNamespaces());
                String id = buffer.toString();
                if (result != null) {
                    id = OWLGUIUtilities.getEntityLabel(result);
                }
                text.setText(id);
            }
        } else {
            setErrorMessage(Messages.RemoveAxiomWizardPage1_7); 
            setPageComplete(false);
        }

        sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        _axiomsToRemove.clear();
        _axiomsToRemove.addAll(clonedAxiomsToRemove);
    }

    public List<OWLAxiom> getAxiomsToRemove() {
        return _axiomsToRemove;
    }

    public String getURI() {
        return _sourceEntityUri;
    }

    public String getTargetUri() {
        return _targetEntityUri;
    }

    public String getInfo() {
        return _info;
    }

    public List<OWLEntity> getEntities() {
        return _entities;
    }

    public void setDeleteMode(String mode) {
        _deleteMode = mode;
    }

    protected Label getLabel(Composite axiomComposite) {
        GridData data = new GridData();
        Label text = new Label(axiomComposite, SWT.WRAP);
        data.horizontalAlignment = SWT.BEGINNING;
        data.grabExcessHorizontalSpace = false;
        text.setLayoutData(data);
        text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        return text;
    }
}
