/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.navigator.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.Messages;

/*
 * Created on 27.11.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractRenameEntityDialog extends Dialog implements KeyListener {

    protected Label _statusImage;
    protected Label _statusLabel;

    private String _project;
    private String _module;
    private String _identifier;
    private String _title;

    /**
     * @param parentShell
     */
    public AbstractRenameEntityDialog(Shell parentShell, String project, String module, String identifier) {
        super(parentShell);
        _project = project;
        _module = module;
        _identifier = identifier;
    }

    /**
     * @param parentShell
     */
    public AbstractRenameEntityDialog(Shell parentShell, String title, String project, String module, String identifier) {
        this(parentShell, project, module, identifier);
        _title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        if (_title != null) {
            getShell().setText(_title);
        }
        createContent(composite);
        _statusImage = new Label(composite, SWT.NONE);
        _statusImage.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_OBJS_ERROR_TSK).createImage());
        _statusImage.setVisible(false);
        _statusImage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

        _statusLabel = new Label(composite, SWT.NONE);
        _statusLabel.setText(""); //$NON-NLS-1$
        _statusLabel.setVisible(false);
        _statusLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        
        initializeUnits();

        return composite;
    }
    
    protected abstract void createContent(Composite parent);

    public String getIdentifier() {
        return _identifier;
    }

    protected abstract void initializeUnits();

    protected void updateStatus() {
        try {
            _identifier = constructIdentifier();
            _statusLabel.setVisible(false);
            _statusImage.setVisible(false);
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } catch (NeOnCoreException e) {
            // InvalidIdentifier
            _statusLabel.setText(Messages.AbstractRenameEntityDialog_3);
            _statusLabel.setVisible(true);
            _statusImage.setVisible(true);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    protected abstract String constructIdentifier() throws NeOnCoreException;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
        // Nothing to do.
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
        updateStatus();
    }

    protected String getOntologyId() {
        return _module;
    }

    protected String getProjectId() {
        return _project;
    }
}
