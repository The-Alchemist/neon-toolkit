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
import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

public class RemoveAxiomWizardPage2 extends UserInputWizardPage {

    protected RemoveAxiomWizardPage1 _page1;
    private List<OWLAxiom> _dependentAxioms;
    private List<OWLAxiom> _axiomsToRemove;

    private List<Button> _checkboxes;

    boolean _canFinish = false;

    protected RemoveAxiomWizardPage2(String pageName, RemoveAxiomWizardPage1 page1, OWLNamespaces namespaces) {
        super(pageName);
        setTitle(pageName);
        setDescription(getDescriptionText());
        _page1 = page1;
        _dependentAxioms = new ArrayList<OWLAxiom>();
        _checkboxes = new ArrayList<Button>();
    }

    protected String getDescriptionText() {
        return Messages.RemoveAxiomWizardPage2_0; 
    }

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout());

        GridData data = new GridData();
        data.verticalIndent = 8;
        data.horizontalSpan = 3;

        Label infoLabel = new Label(comp, SWT.NONE);
        infoLabel.setText(_page1.getInfo());
        infoLabel.setToolTipText(_page1.getInfo());
        infoLabel.setLayoutData(data);

        ScrolledComposite sComp = new ScrolledComposite(comp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        data.widthHint = 400;
        data.heightHint = 300;
        sComp.setLayoutData(data);

        initAxiomComposite(sComp);
        Composite buttonComp = new Composite(comp, SWT.NONE);
        buttonComp.setLayout(new GridLayout(2, false));
        data = new GridData();
        data.horizontalAlignment = GridData.BEGINNING;
        buttonComp.setLayoutData(data);

        Button selectAll = new Button(buttonComp, SWT.PUSH);
        selectAll.setText(Messages.RemoveAxiomWizardPage2_1); 
        selectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (Button b: _checkboxes) {
                    b.setSelection(true);
                    b.notifyListeners(SWT.Selection, new Event());
                }
            }

        });

        Button unselectAll = new Button(buttonComp, SWT.PUSH);
        unselectAll.setText(Messages.RemoveAxiomWizardPage2_2); 
        unselectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (Button b: _checkboxes) {
                    b.setSelection(false);
                    b.notifyListeners(SWT.Selection, new Event());
                }
            }

        });

        Label detailedInfoLabel = new Label(comp, SWT.NONE);
        detailedInfoLabel.setText(getDetailedInfoText());
        data = new GridData();
        data.verticalIndent = 10;
        data.verticalAlignment = SWT.END;
        detailedInfoLabel.setLayoutData(data);
        setControl(comp);
        _canFinish = true;
    }

    public boolean canFinish() {
        return _canFinish;
    }

    protected String getDetailedInfoText() {
        return Messages.RemoveAxiomWizardPage2_3 + Messages.RemoveAxiomWizardPage2_4 + Messages.RemoveAxiomWizardPage2_5;   
    }

    private void initAxiomComposite(Composite parent) {
        ScrolledComposite sc = ((ScrolledComposite) parent);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite axiomComposite = new Composite(sc, SWT.NONE);
        sc.setContent(axiomComposite);
        axiomComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        axiomComposite.setLayout(new GridLayout(2, false));

        // first collect information
        List<OWLAxiom> clonedAxiomsToRemove = new ArrayList<OWLAxiom>();
        clonedAxiomsToRemove.addAll(_axiomsToRemove);
        if (_axiomsToRemove != null) {
            for (OWLAxiom axiom: _axiomsToRemove) {
                List<OWLAxiom> list = new ArrayList<OWLAxiom>();
                List<String> uriList = new ArrayList<String>();
                uriList.add(_page1.getTargetUri());
                uriList.add(_page1.getURI());
                List<OWLAxiom> newAxioms = OWLGUIUtilities.getDependentAxioms(axiom, list, _page1.getEntities(), uriList, _page1._projectId);
                if (newAxioms.size() > 0) {

                    boolean addRow = false;
                    Label text = null;
                    for (OWLAxiom a: newAxioms) {
                        if (a != null) {
                            addRow = true;
                        }
                    }
                    if (addRow) {
                        text = getLabel(axiomComposite);
                    }
                    for (OWLAxiom a: newAxioms) {
                        if (a != null) {
                            createSubEntry(axiomComposite, a);
                        }
                    }

                    if (addRow && text != null) {
                        OWLModel owlModel = ((RemoveAxiomWizard) getWizard()).getOwlModel();
                        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
                        text.setText(OWLGUIUtilities.getEntityLabel((String[]) axiom.accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle))));
                    }
                }

            }
        }

        sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        _axiomsToRemove.clear();
        _axiomsToRemove.addAll(clonedAxiomsToRemove);
    }

    protected String getEmptyMessage() {
        return Messages.RemoveAxiomWizardPage2_6; 
    }

    private Label getLabel(Composite axiomComposite) {
        GridData data;
        Label text = new Label(axiomComposite, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = SWT.BEGINNING;
        data.horizontalSpan = 2;
        text.setLayoutData(data);
        text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        return text;
    }

    @Override
    public boolean canFlipToNextPage() {
        return false;
    }

    private void createSubEntry(Composite axiomComposite, final OWLAxiom newAxiom) {
        if (!_dependentAxioms.contains(newAxiom)) {
            _dependentAxioms.add(newAxiom);
        }
        final Button checkbox = new Button(axiomComposite, SWT.CHECK);
        checkbox.setLayoutData(new GridData());
        checkbox.setSelection(true);
        checkbox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (checkbox.getSelection()) {
                    if (!_dependentAxioms.contains(newAxiom)) {
                        _dependentAxioms.add(newAxiom);
                    }
                } else {
                    _dependentAxioms.remove(newAxiom);
                }
            }

        });
        GridData data = new GridData();
        data.horizontalIndent = 15;
        checkbox.setLayoutData(data);
        _checkboxes.add(checkbox);

        Label text = new Label(axiomComposite, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = SWT.BEGINNING;
        text.setLayoutData(data);
        text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        OWLModel owlModel = ((RemoveAxiomWizard) getWizard()).getOwlModel();
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        text.setText(OWLGUIUtilities.getEntityLabel((String[]) newAxiom.accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle))));
    }
    
    public List<OWLAxiom> getDependentAxioms() {
        return _dependentAxioms;
    }

    public void setAxiomsToRemove(List<OWLAxiom> axioms) {
        _axiomsToRemove = axioms;
    }

}
