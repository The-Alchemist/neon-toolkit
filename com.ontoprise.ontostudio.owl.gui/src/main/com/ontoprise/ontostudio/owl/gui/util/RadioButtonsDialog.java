/**
 * written by the NeOn Technology Foundation Ltd.
 */

package com.ontoprise.ontostudio.owl.gui.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Nico Stieler
 * 
 */
public class RadioButtonsDialog extends Dialog {

    private String _messageText;
    private String _titleText;

    private Button[] buttons;
    private Composite _radioBar;
    private String[] _buttonsTexts;
    private String _selected;
    private String _radioButtonTitle;
    
    

    /**
     * @param parentShell
     * @throws CheckPreferencesFirstException 
     */
    public RadioButtonsDialog(Shell parent, String titleText, String messageText, String radioButtonTitle, String[] buttonsTexts){
        super(parent);
        _messageText = messageText;
        _titleText = titleText;
        _buttonsTexts = buttonsTexts;
        _radioButtonTitle = radioButtonTitle;
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


        SelectionListener listener = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if(event.widget instanceof Button){
                    _selected = ((Button)event.widget).getText();
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        };
        _radioBar = new Composite(parent, SWT.NULL);
        _radioBar.setLayout(new RowLayout(SWT.VERTICAL));

        Label label = new Label(_radioBar, SWT.NULL);
        label.setText(_radioButtonTitle);
        buttons = new Button[_buttonsTexts.length];
        for(int i = 0; i < _buttonsTexts.length; i++){
            buttons[i] = new Button(_radioBar, SWT.RADIO);
            buttons[i].setText(_buttonsTexts[i]);
            buttons[i].addSelectionListener(listener);
        }
        new Label(comp, SWT.NONE);
        return comp;
    }
    public String getSelected(){
        return _selected;
    }
    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

}
