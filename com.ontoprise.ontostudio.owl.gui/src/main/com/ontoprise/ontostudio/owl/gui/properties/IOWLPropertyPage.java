/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 11.03.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.properties;

import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author werner
 *
 */
public interface IOWLPropertyPage {

    void layoutSections();
    
    ScrolledForm getForm();
    
    void refresh();
}
