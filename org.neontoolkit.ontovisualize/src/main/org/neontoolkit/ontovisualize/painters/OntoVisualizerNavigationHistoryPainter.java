/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
