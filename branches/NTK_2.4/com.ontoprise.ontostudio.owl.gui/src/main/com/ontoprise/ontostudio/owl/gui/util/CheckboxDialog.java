package com.ontoprise.ontostudio.owl.gui.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;

/**
 * @author Nico Stieler
 * 
 */
public class CheckboxDialog extends Dialog {

    private String _messageText;
    private String _titleText;
    private String _preferencesValue;
    private boolean _check;

    /**
     * @param parentShell
     * @throws CheckPreferencesFirstException 
     */
    public CheckboxDialog(Shell parent, String titleText, String messageText, String preferencesValue){
        super(parent);
        _messageText = messageText;
        _titleText = titleText;
        _preferencesValue = preferencesValue;
        
        _check = OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(_preferencesValue);
    }
    /**
     * @param parentShell
     * @throws CheckPreferencesFirstException 
     */
    public CheckboxDialog(Shell parent, String titleText, String messageText, boolean check){
        super(parent);
        _messageText = messageText;
        _titleText = titleText;
        _preferencesValue = null;
        
        _check = check;
    }


    @Override
    protected Control createDialogArea(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        layout.marginLeft = 10;
        layout.marginTop = 10;
        layout.marginRight = 10;
        layout.marginBottom = 0;
        comp.setLayout(layout);
        getShell().setText(_titleText);

        // info label
        Label infoLabel = new Label(comp, SWT.WRAP);
        infoLabel.setText(_messageText);
        GridData data = new GridData(GridData.FILL, GridData.FILL, false, true);
        data.widthHint = 500;
        infoLabel.setLayoutData(data);

        final Button checkbox = new Button(comp, SWT.CHECK);
        checkbox.setText("don't ask me again");
        checkbox.setSelection(_check);
        SelectionListener listener = new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent event) {
                _check = checkbox.getSelection();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        };
        checkbox.addSelectionListener(listener);

//        Checkb

        new Label(comp, SWT.NONE);
            
        return comp;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void okPressed() {
        if(_check && _preferencesValue != null)
            OWLModelPlugin.getDefault().getPluginPreferences().setValue(_preferencesValue, _check); 
        super.okPressed();
    }
    @Override
    protected void cancelPressed() {
//        if(check)//in case its needed: maybe case specific
//            OWLModelPlugin.getDefault().getPluginPreferences().setValue(_preferencesValue, check); 
        super.cancelPressed();
    }


    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

}
