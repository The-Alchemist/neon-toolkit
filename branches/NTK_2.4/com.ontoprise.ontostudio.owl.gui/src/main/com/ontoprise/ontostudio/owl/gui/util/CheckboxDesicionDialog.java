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
public class CheckboxDesicionDialog extends Dialog {

    private String _messageText;
    private String _titleText;
    private String _preferencesRemember;
    private String _preferencesDecision;
    private boolean _check;
    private boolean _lockCheckbox;
    
    public static final int YES = 5;
    public static final int NO = 6;
    

    /**
     * @param lockCheckbox 
     * @param parentShell
     * @throws CheckPreferencesFirstException 
     */
    public CheckboxDesicionDialog(Shell parent, String titleText, String messageText, String preferencesRemember, String preferencesDecision, boolean lockCheckbox){
        super(parent);
        _messageText = messageText;
        _titleText = titleText;
        _preferencesRemember = preferencesRemember;
        _preferencesDecision = preferencesDecision;
        
        _check = !lockCheckbox && getPreferencesRemember();
        _lockCheckbox = lockCheckbox;
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
        checkbox.setEnabled(!_lockCheckbox);
        checkbox.setText("remember my decision"); //$NON-NLS-1$
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
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        //Yes-Button
        //No-Button
        //Cancel-Button
        if(_preferencesDecision == null || getPreferencesDecision()){
            createButton(parent, CheckboxDesicionDialog.YES, "Yes", true); //$NON-NLS-1$
            createButton(parent, CheckboxDesicionDialog.NO, "No", false); //$NON-NLS-1$
            createButton(parent, CheckboxDesicionDialog.CANCEL, "Cancel", false); //$NON-NLS-1$
        }else{
            createButton(parent, CheckboxDesicionDialog.YES, "Yes", false); //$NON-NLS-1$
            createButton(parent, CheckboxDesicionDialog.NO, "No", true); //$NON-NLS-1$
            createButton(parent, CheckboxDesicionDialog.CANCEL, "Cancel", false); //$NON-NLS-1$
        }
    }
    /**
     * @return
     */
    private boolean getPreferencesDecision() {
        if(_preferencesDecision != null)
            return OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(_preferencesDecision);
        return false;
    }
    private void setPreferencesDecision(boolean yes) {
        if(_preferencesDecision != null)
            OWLModelPlugin.getDefault().getPreferenceStore().setValue(_preferencesDecision, yes);
    }
    private boolean getPreferencesRemember() {
        if(_preferencesRemember != null)
            return OWLModelPlugin.getDefault().getPreferenceStore().getBoolean(_preferencesRemember);
        return false;
    }
    private void setPreferencesRemember(boolean yes) {
        if(_preferencesRemember != null)
            OWLModelPlugin.getDefault().getPreferenceStore().setValue(_preferencesRemember, yes);
    }
    @Override
    protected void okPressed() {
    }
    @Override
    protected void cancelPressed() {
        super.cancelPressed();
    }
    protected void yesPressed() {
        setReturnCode(YES);
        close();
    }
    protected void noPressed() {
        setReturnCode(NO);
        close();
    }
    @Override
    public boolean close() {
        if(_preferencesRemember != null && _preferencesDecision != null && _check && !_lockCheckbox){
            switch (getReturnCode()) {
                case YES:  
                    setPreferencesDecision(true);
                    setPreferencesRemember(true);
                    break;
                case NO:
                    setPreferencesDecision(false);
                    setPreferencesRemember(true);
                    break;
                case OK:
                case CANCEL:
                default:
                    break;
            }
        }
        return super.close();
    }
    @Override
    protected void buttonPressed(int buttonId) {
        switch (buttonId) {
            case OK:
                okPressed();
                break;
            case CANCEL:
                cancelPressed();
                break;
            case YES:
                yesPressed();
                break;
            case NO:
                noPressed();
                break;
            default:
                break;
        }
    }


    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

}
