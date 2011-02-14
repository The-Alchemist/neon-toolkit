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

import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.lens.LensListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.neontoolkit.jpowergraph.Messages;

/**
 * The legend control panel. With the check box to show or not the Legend
 * @author Vmendez
 * Created on: 08/12/2009
 */
public class LegendControlPanel extends Composite {

    private Button showLegend;
    private LegendLens legendLens;
    
    public LegendControlPanel(Composite theParent, LegendLens theLegendLens) {
        this(theParent, theLegendLens, true);
    }
    
    public LegendControlPanel(Composite theParent, LegendLens theLegendLens, boolean showLegendValue) {
        super(theParent, SWT.NONE);
        legendLens = theLegendLens;
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 0;
        this.setLayout(gridLayout);
        
        showLegend = new Button(this, SWT.CHECK);
        showLegend.setToolTipText(Messages.LegendControlPanel_0);
        showLegend.setText(Messages.LegendControlPanel_0short);
        showLegend.setSelection(showLegendValue);
        showLegend.setEnabled(legendLens != null);
        setSelectedItemFromLens();
        this.addActionListeners();
    }

    private void addActionListeners() {
        if (legendLens != null && showLegend != null){
            showLegend.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    if (legendLens.isShowLegend() != showLegend.getSelection()){
                        legendLens.setShowLegend(showLegend.getSelection());
                    }
                }
            });
        }
        
        if (legendLens != null){
            legendLens.addLensListener(new LensListener() {
                public void lensUpdated(Lens lens) {
                    setSelectedItemFromLens();
                }
            });
        }
    }

    protected void setSelectedItemFromLens() {
        if (legendLens != null && showLegend != null){
            showLegend.setSelection(legendLens.isShowLegend());
        }
    }
}
