/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * Created on: 08.01.2009
 * Created by: werner
 *****************************************************************************/
package com.ontoprise.ontostudio.owl.gui.navigator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.actions.AbstractRenameEntityDialog;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * @author werner
 *
 */
public class RenameOWLEntityDialog extends AbstractRenameEntityDialog {

    private Label _uriLabel;
    private Text _uriText;
    private OWLEntity _entity;
    
    public RenameOWLEntityDialog(Shell parentShell, String title, String project, String module, String identifier, OWLEntity entity) {
        super(parentShell, title, project, module, identifier);
        _entity = entity;
    }

    @Override
    protected String constructIdentifier() throws NeOnCoreException {
        // in case of OWL the user always has to edit the complete URI
        return _uriText.getText();
    }

    @Override
    protected void createContent(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.RenameOWLEntityDialog_0); 
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);
        
        _uriLabel = new Label(composite, SWT.NONE);
        _uriLabel.setText(Messages.RenameOWLEntityDialog_1); 
        _uriLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
        
        _uriText = new Text(composite, SWT.BORDER);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.minimumWidth = 200;
        _uriText.setLayoutData(data);
        _uriText.addKeyListener(this);
        
        Composite statusComposite = new Composite(composite, SWT.NONE);
        gd = new GridData(GridData.FILL, GridData.FILL, true, false);
        gd.horizontalSpan = 2;
        statusComposite.setLayoutData(gd);
        statusComposite.setLayout(new GridLayout(2, false));

        _statusImage = new Label(statusComposite, SWT.NONE);
        _statusImage.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_OBJS_ERROR_TSK).createImage());
        _statusImage.setVisible(false);
        _statusImage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
        
        _statusLabel = new Label(statusComposite, SWT.NONE);
        _statusLabel.setText(""); //$NON-NLS-1$
        _statusLabel.setVisible(false);
        _statusLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
    }

    @Override
    protected void initializeUnits() {
        String identifier = getIdentifier();
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        String id;
        int idDisplayStyle = 0;
        try {
            OWLModel owlModel = OWLModelFactory.getOWLModel(getOntologyId(), getProjectId());
            idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
            id = ((String[]) _entity.accept(manager.getVisitor(owlModel, idDisplayStyle)))[0]; // always use the URI
        } catch (NeOnCoreException e) {
            id = identifier;
        }
        
        String idDisplayText = Messages.RenameOWLEntityDialog_1;
        if (idDisplayStyle == NeOnUIPlugin.DISPLAY_LOCAL) {
            idDisplayText = Messages.RenameOWLEntityDialog_2;
        } else if (idDisplayStyle == NeOnUIPlugin.DISPLAY_QNAME) {
            idDisplayText = Messages.RenameOWLEntityDialog_3;
        } else if (idDisplayStyle == NeOnUIPlugin.DISPLAY_URI) {
            idDisplayText = Messages.RenameOWLEntityDialog_1;
        }
        _uriLabel.setText(idDisplayText);
        _uriText.setText(id);
        _uriText.selectAll();
    }
    
}
