/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.textfields.AxiomText;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;
/**
 * @author Nico Stieler
 */
public class RestrictionRow extends AbstractRestrictionRow {

    private Button _editButton;
    private Button _removeButton;
    private StyledText _axiomText;

    private boolean _imported;
    private boolean _showAxioms;

    private String _project;
    private String _sourceOnto;
    private String _entityName;

    public RestrictionRow(FormToolkit toolkit, Composite parent, int cols, boolean imported, String sourceOnto, String project, String entityName) {
        super(toolkit, parent, cols);
        _imported = imported;
        _entityName = entityName;
        _sourceOnto = sourceOnto;
        _project = project;
        _showAxioms = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(OWLModelPlugin.SHOW_AXIOMS);
    }

    @Override
    public void init(AbstractRowHandler handler) throws NeOnCoreException {
        _handler = handler;

        // optional textfield for axiom text
        if (_showAxioms) {
            _axiomText = new AxiomText(getParent(), _handler._localOwlModel, _handler._sourceOwlModel, getColCount()).getStyledText();
            _axiomText.setEditable(false);
            StringBuffer buffer = new StringBuffer();
            int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
            if (_handler instanceof EntityRowHandler) {
                List<LocatedAxiom> axioms = ((EntityRowHandler) _handler).getAxioms();
                if (axioms.size() > 0) {
                    OWLAxiom axiom = axioms.get(0).getAxiom();
                    buffer.append(OWLGUIUtilities.getEntityLabel((String[]) axiom.accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(_handler._localOwlModel, idDisplayStyle))));
                }
            } else if (_handler instanceof AxiomRowHandler) {
                OWLAxiom axiom = ((AxiomRowHandler) _handler).getAxiom();
                if (axiom != null) {
                    buffer.append(OWLGUIUtilities.getEntityLabel((String[]) axiom.accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(_handler._localOwlModel, idDisplayStyle))));
                }
            } else if (_handler instanceof DescriptionRowHandler) {
                List<LocatedAxiom> axioms = ((DescriptionRowHandler) _handler).getAxioms();
                if (axioms.size() > 0) {
                    LocatedAxiom axiom = axioms.get(0);
                    buffer.append(OWLGUIUtilities.getEntityLabel((String[]) axiom.getAxiom().accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(_handler._localOwlModel, idDisplayStyle))));
                }
            }
            _axiomText.setText(buffer.toString());
        }

        _editButton = createEditButton(getParent(), true);
        _removeButton = createRemoveButton(getParent(), true);

        _editButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (_editButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_EDIT)) {
                    maximizeAllWidgets(getWidgets());
                    _handler.ensureQName();
                    // enable widgets, so the user can change them
                    editPressed(_editButton, _removeButton);
                    enableWidgets(getWidgets());
                } else {
                    if (_editButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_EDIT_STAR)) {
                        if(MessageDialog.openQuestion(null, Messages.EditImportedTitle, Messages.EditImportedText_0 + _sourceOnto  + " " + Messages.EditImportedText_1 )){ //$NON-NLS-1$//NICO OR preferences
                            maximizeAllWidgets(getWidgets());
                            _handler.ensureQName();
                            // enable widgets, so the user can change them
                            editStarPressed(_editButton, _removeButton);
                            enableWidgets(getWidgets());
                        }
                    } else {
                        if (_editButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_SAVE_STAR)) {
                            _handler.savePressed();
                        }else {
//                            if (_editButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_SAVE)) {
                                _handler.savePressed();
//                            }
                        }
                    }
                }
            }
        });

        _removeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (_removeButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_REMOVE)) {
                    try {
                        _handler.removePressed();
                    } catch (NeOnCoreException e1) {
                        // only log error, this will happen if we try to add an axiom that already exists
                        //						new OntoStudioExceptionHandler().handleException(Messages.getString("FormRow.0"), e1, _removeButton.getShell()); //$NON-NLS-1$
                        new NeonToolkitExceptionHandler().handleException(e1);
                    } catch (CommandException e2) {
                        new NeonToolkitExceptionHandler().handleException(e2);
                    }
                } else{
                    if (_removeButton.getText().equals(OWLGUIUtilities.BUTTON_LABEL_REMOVE_STAR)) {
                        if(MessageDialog.openQuestion(null, Messages.RemoveImportedTitle, Messages.RemoveImportedText_0 + _sourceOnto  + " " + Messages.RemoveImportedText_1 )){ //$NON-NLS-1$//NICO OR preferences
                            try {
                                _handler.removePressed();
                            } catch (NeOnCoreException e1) {
                                new NeonToolkitExceptionHandler().handleException(e1);
                            } catch (CommandException e2) {
                                new NeonToolkitExceptionHandler().handleException(e2);
                            }
                        }
                        
                    } else {
                        _handler.cancelPressed();
                    }
                }
            }

        });
        if (_imported) {
            getParent().setBackground(new Color(null, 176, 196, 222));
//            getParent().setToolTipText(Messages.FormRow_1 + _sourceOnto);

            _editButton.setToolTipText(Messages.FormRow_1 + _sourceOnto);
            _removeButton.setToolTipText(Messages.FormRow_1 + _sourceOnto);

            _editButton.setText(OWLGUIUtilities.BUTTON_LABEL_EDIT_STAR);
            _removeButton.setText(OWLGUIUtilities.BUTTON_LABEL_REMOVE_STAR);
//            _editButton.setEnabled(false);
//            _removeButton.setEnabled(false);
//            Control[] children = getParent().getChildren();
//            for (Control control: children) {
//                control.setToolTipText(Messages.FormRow_1 + _sourceOnto);
//            }
        }
    }

    @Override
    public Button getCancelButton() {
        return _removeButton;
    }

    @Override
    public Button getSubmitButton() {
        return _editButton;
    }
    
    public StyledText getAxiomText() {
        return _axiomText;
    }
    
    public void setAxiomText(StyledText text) {
        _axiomText = text;
    }

    @Override
    public void setCancelButton(Button b) {
        _removeButton = b;
        _buttonsToDisable.add(b);
    }

    @Override
    public void setSubmitButton(Button b) {
        _editButton = b;
        _buttonsToDisable.add(b);
    }
    
    @Override
    public void removeWidget(Composite widget) {
        if (_buttonsToDisable.contains(widget)) {
            _buttonsToDisable.remove(widget);
        }
        super.removeWidget(widget);
    }

    /**
     * 
     */
    @Override
    protected void jump() {
        if(MessageDialog.openQuestion(null, Messages.JumpToTitle, Messages.JumpToText_0 + _sourceOnto + Messages.JumpToText_1)){
            try {
                OWLModel model = OWLModelFactory.getOWLModel(_sourceOnto,_project);

                OWLGUIUtilities.jumpToEntity(_entityName,model);
                
           } catch (NeOnCoreException e1) {
              e1.printStackTrace();
          }
        }
        
    }
}
