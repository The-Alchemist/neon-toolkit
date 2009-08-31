/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.ontovisualize.painters;

import org.eclipse.swt.widgets.Display;
import org.neontoolkit.jpowergraph.painters.NavigationHistoryPainter;


/*
 * Created by Werner Hihn
 */

public class OntoVisualizerNavigationHistoryPainter extends NavigationHistoryPainter {

    public OntoVisualizerNavigationHistoryPainter(Display theDisplay) {
        super(theDisplay);
    }

//    @Override
//    protected String guiString(Node node) {
//        return ((LabelImageNode)node).getLabel();
//    }

}
