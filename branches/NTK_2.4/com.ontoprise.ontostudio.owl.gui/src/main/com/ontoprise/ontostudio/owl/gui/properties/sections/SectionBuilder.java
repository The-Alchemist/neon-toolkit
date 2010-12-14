/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.properties.sections;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author janiko
 *
 */
public abstract class SectionBuilder {

    protected Section _section;
    protected FormToolkit _toolkit;
    
    public void createNewSection(Composite parent, final ScrolledForm form){
        _section = _toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        if (form != null) {
            _section.addExpansionListener(new ExpansionAdapter() {
                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    form.reflow(true);
                }
            });
        }
    }
    
    public Section getSection(){
        return _section;
    }
    
    public abstract void addText();
    
    public abstract void addContent();
    
    public abstract void initSection();
}
