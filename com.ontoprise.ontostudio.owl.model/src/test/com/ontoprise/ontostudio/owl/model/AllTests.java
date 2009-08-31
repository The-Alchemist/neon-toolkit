/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ontoprise.ontostudio.owl.model.hierarchy.AllHierarchyTests;
import com.ontoprise.ontostudio.owl.model.util.AllUtilsTests;
import com.ontoprise.ontostudio.owl.model.util.file.AllUtilFileTests;
import com.ontoprise.ontostudio.owl.model.visitors.AllVisitorsTests;

/**
 * @author krekeler
 */
@RunWith(Suite.class)
@SuiteClasses( {
    AllVisitorsTests.class,
    AllUtilsTests.class,
    AllUtilFileTests.class,
    AllHierarchyTests.class,
    })
public class AllTests {

}
