/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.control;

import java.util.Comparator;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;

public class AlphabeticalOWLEntityTreeElementComparator<A extends AbstractOwlEntityTreeElement> implements Comparator<AbstractOwlEntityTreeElement> {

    public int compare(AbstractOwlEntityTreeElement o1, AbstractOwlEntityTreeElement o2) {
        return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
    }

}
