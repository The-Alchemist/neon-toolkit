/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package org.neontoolkit.jpowergraph.viewcontrols;
import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;
import net.sourceforge.jpowergraph.lens.TooltipLens;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.neontoolkit.jpowergraph.Messages;

/**
 * @author Vmendez
 * Created on: 08/12/2009
 */
public class TooltipControlPanel extends Composite {
    private Button showToolTips;
    private TooltipLens tooltipLens;
    
    /**
     * 
     */
    public TooltipControlPanel(Composite theParent, TooltipLens theTooltipLens) {
        this(theParent, theTooltipLens, true);
    }
    
    public TooltipControlPanel(Composite theParent, TooltipLens theTooltipLens, boolean showToolTipsValue) {
        super(theParent, SWT.NONE);
        tooltipLens = theTooltipLens;
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 0;
        this.setLayout(gridLayout);
        
        showToolTips = new Button(this, SWT.CHECK);
        showToolTips.setToolTipText(Messages.TooltipControlPanel_0);
        showToolTips.setSelection(showToolTipsValue);
        showToolTips.setEnabled(tooltipLens != null);
        setSelectedItemFromLens();
        this.addActionListeners();
    }

    private void addActionListeners() {
        if (tooltipLens != null && showToolTips != null){
            showToolTips.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    if (tooltipLens.isShowToolTips() != showToolTips.getSelection()){
                        tooltipLens.setShowToolTips(showToolTips.getSelection());
                    }
                }
            });
        }
        
        if (tooltipLens != null){
            tooltipLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (tooltipLens != null && showToolTips != null){
            showToolTips.setSelection(tooltipLens.isShowToolTips());
        }
    }

   
}
