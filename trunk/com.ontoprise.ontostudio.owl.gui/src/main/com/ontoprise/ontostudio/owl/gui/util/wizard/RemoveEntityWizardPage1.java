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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
/**
 * 
 * @author Nico Stieler
 */
public class RemoveEntityWizardPage1 extends RemoveAxiomWizardPage1 {

    protected CheckboxTreeViewer _treeViewer;

    private Map<OWLAxiom,List<OWLAxiom>> _children;

    protected RemoveEntityWizardPage1(String pageName, List<OWLAxiom> axioms, OWLNamespaces namespaces, String sourceEntityUri, String targetEntityUri, String deleteMode, List<OWLEntity> entities, String ontologyUri, String projectId) {
        super(pageName, axioms, namespaces, sourceEntityUri, targetEntityUri, deleteMode, entities, ontologyUri, projectId);
        _children = new HashMap<OWLAxiom,List<OWLAxiom>>();
    }

    @Override
    protected void initAxiomComposite(Composite parent) throws NeOnCoreException {
        ScrolledComposite sc = ((ScrolledComposite) parent);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite axiomComposite = new Composite(sc, SWT.NONE);
        sc.setContent(axiomComposite);
        axiomComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        axiomComposite.setLayout(new GridLayout(3, false));

        OWLModel owlModel = ((RemoveAxiomWizard) getWizard()).getOwlModel();
        for (final OWLAxiom axiom: _axiomsToRemove) {
            createAxiomRow(axiomComposite, owlModel, axiom, false);
            _children.put(axiom, new ArrayList<OWLAxiom>());
            recursiveFetchChildren(axiom, axiom);

            List<OWLAxiom> childList = _children.get(axiom);
            for (OWLAxiom child: childList) {
                Composite childComposite = new Composite(axiomComposite, SWT.NONE);
                GridLayout layout = new GridLayout(3, false);
                layout.marginHeight = 0;
                childComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                childComposite.setLayout(layout);
                GridData data = new GridData();
                data.horizontalSpan = 3;
                childComposite.setLayoutData(data);

                createAxiomRow(childComposite, owlModel, child, true);
            }

        }

        sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        setCanFinish(true);
    }

    /**
     * Recursively queries for the children of the passed OWLAxiom, and adds them to a flat list.
     * @throws NeOnCoreException 
     */
    private void recursiveFetchChildren(OWLAxiom axiom, OWLAxiom rootAxiom) throws NeOnCoreException {
        _dependentAxioms.add(axiom);
        List<OWLAxiom> children = getChildrenForAxiom(axiom);
        if (children != null) {
            children.remove(axiom); // remove parent, which is also contained in list
            for (OWLAxiom child: children) {
                List<OWLAxiom> childList = _children.get(rootAxiom);
                boolean tryRecursive = false;
                if (!childListsContain(child)) {
                    childList.add(child);
                    tryRecursive = true;
                }
                if (child instanceof OWLSubClassOfAxiom) {
                    OWLClassExpression subDesc = ((OWLSubClassOfAxiom)child).getSubClass();
                    OWLClassExpression superDesc = ((OWLSubClassOfAxiom)child).getSuperClass();
                    
                    for (OWLEntity entity: _entities) {
                        //if (OWLUtilities.toString(superDesc).contains(entity.getURI().toString()) || OWLUtilities.toString(subDesc).contains(entity.getURI().toString())) {
                        if (tryRecursive 
                                && !OWLUtilities.toString(superDesc).contains(entity.getIRI().toString()) 
                                && !OWLUtilities.toString(subDesc).contains(entity.getIRI().toString()) ) {
                            recursiveFetchChildren(child, rootAxiom);
                        }
                        //}
                    }
                }
                
                _dependentAxioms.add(child);
            }
        }
    }

    public boolean childListsContain(OWLAxiom axiom) {
        // first check parent axioms
        if (_axiomsToRemove.contains(axiom)) {
            return true;
        }
        // now check children
        for (OWLAxiom a: _axiomsToRemove) {
            List<OWLAxiom> children = _children.get(a);
            if (children != null && children.contains(axiom)) {
                return true;
            }
        }
        return false;
    }

    private void createAxiomRow(Composite axiomComposite, OWLModel owlModel, final OWLAxiom axiom, boolean isChildRow) {
        if (isChildRow) {
            Button dummy = new Button(axiomComposite, SWT.CHECK);
            dummy.setVisible(false);
        }
        final Button checkbox = new Button(axiomComposite, SWT.CHECK);
        checkbox.setLayoutData(new GridData());
        checkbox.setSelection(true);
        checkbox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (checkbox.getSelection()) {
                    _dependentAxioms.add(axiom);
                } else {
                    _dependentAxioms.remove(axiom);
                }
            }

        });
        GridData data = new GridData();
        checkbox.setLayoutData(data);
        _checkboxes.add(checkbox);

        Label text = getLabel(axiomComposite);
        data = (GridData) text.getLayoutData();
        text.setLayoutData(data);

        if (!isChildRow) {
            data = (GridData) text.getLayoutData();
            data.horizontalSpan = 2;
            text.setLayoutData(data);
        }
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
        
        String[] result = (String[]) axiom.accept(visitor);
        text.setText(OWLGUIUtilities.getEntityLabel(result));
    }

    protected void setCanFinish(boolean canFinish) {
        ((RemoveEntityWizard) getWizard()).setCanFinish(canFinish);
    }

    protected List<OWLAxiom> getChildrenForAxiom(OWLAxiom parentElement) throws NeOnCoreException {
        OWLModel owlModel = ((RemoveEntityWizard) getWizard()).getOwlModel();
        if (_deleteMode.equals(RemoveEntityWizard.DELETE_ENTITY)) {
            // DELETE_ENTITY
            return null;
        } else {
            // DELETE_SUBTREE
            Set<OWLAxiom> resultList = new LinkedHashSet<OWLAxiom>();
            if (parentElement instanceof OWLSubObjectPropertyOfAxiom) {
//                List<ObjectPropertyExpression> subProperties = ((SubObjectPropertyOf) parentElement).getSubObjectProperties();
//
//                for (ObjectPropertyExpression prop: subProperties) {
//                    AxiomsForEntitiesCollector collector2 = new AxiomsForEntitiesCollector(owlModel, (OWLEntity) prop);
//                    try {
//                        collector2.run(new NullProgressMonitor());
//                    } catch (InvocationTargetException e) {
//                        return null;
//                    } catch (InterruptedException e) {
//                        return null;
//                    }
//                    List<OWLAxiom> localList = collector2.getResultAsList();
//                    localList.remove(parentElement); // remove same axiom
//                    resultList.addAll(localList);
//                }

                Set<OWLAxiom> localList = new LinkedHashSet<OWLAxiom>(owlModel.getReferencingAxioms((OWLEntity) ((OWLSubObjectPropertyOfAxiom) parentElement).getSubProperty()));
                localList.remove(parentElement); // remove same axiom
                resultList.addAll(localList);
            } else if (parentElement instanceof OWLSubDataPropertyOfAxiom) {
                OWLDataPropertyExpression subProperty = ((OWLSubDataPropertyOfAxiom) parentElement).getSubProperty();
                Set<OWLAxiom> localList = new LinkedHashSet<OWLAxiom>(owlModel.getReferencingAxioms((OWLEntity)subProperty));
                localList.remove(parentElement); // remove same axiom
                resultList.addAll(localList);
            }
            return new ArrayList<OWLAxiom>(resultList);
        }
    }

    @Override
    public List<OWLAxiom> getAxiomsToRemove() {
        List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>();
        for (OWLAxiom a: _dependentAxioms) {
            if (!axiomsToRemove.contains(a)) {
                axiomsToRemove.add(a);
            }
        }
        return axiomsToRemove;
    }

    @Override
    public boolean isPageComplete() {
        return true;
    }

    @Override
    public String getURI() {
        return _sourceEntityUri;
    }

    @Override
    public String getTargetUri() {
        return _targetEntityUri;
    }

    @Override
    public String getInfo() {
        return _info;
    }

    @Override
    public void setDeleteMode(String mode) {
        _deleteMode = mode;
    }

}
