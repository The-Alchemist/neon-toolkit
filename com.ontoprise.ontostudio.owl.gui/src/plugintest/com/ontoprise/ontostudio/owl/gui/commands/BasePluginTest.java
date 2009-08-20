/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 17.04.2009
 * Created by: josp
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.neontoolkit.core.AfterEarly;
import org.neontoolkit.core.AfterLate;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;

/**
 * Avoid copy and paste code and put equal code here!
 */
// _THIS_IS_NOT_A_TEST_
public abstract class BasePluginTest {
    protected static final String PROJECT_ID = "TestProject"; //$NON-NLS-1$
    
    @AfterEarly
    public void readAndDispatchEarly() {
        for (String projectId: OntologyProjectManager.getDefault().getOntologyProjects()) {
            try {
                OntologyProjectManager.getDefault().getOntologyProject(projectId).readAndDispatchWhileWaitingForEvents();
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @AfterLate
    public void readAndDispatchLate() {
        for (String projectId: OntologyProjectManager.getDefault().getOntologyProjects()) {
            try {
                OntologyProjectManager.getDefault().getOntologyProject(projectId).readAndDispatchWhileWaitingForEvents();
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * Internal method that returns true if elements are equal or false otherwise
     * 
     * @param expected
     * @param actual
     * @return
     */
    protected static boolean isEqual(Object expected, Object actual) {
        boolean equal = (expected != null) ? expected.equals(actual) : (actual == null);
        if (!equal && expected != null) {
            // not equal => check if elements are arrays and compare the contents in this case
            if (expected.getClass().isArray() && actual.getClass().isArray()) {
                equal = true;
                Object[] expectedArray = (Object[]) expected;
                Object[] actualArray = (Object[]) actual;
                if (expectedArray.length == actualArray.length) {
                    for (int i = 0; i < expectedArray.length && equal; i++) {
                        equal = isEqual(expectedArray[i], actualArray[i]);
                    }
                }
                else {
                    equal = false;
                }
            }
        }
        return equal;
    }
    
    public static void assertUnsortedArrayEquals(Object[] expected, Object[] actual) {
        // josp 2009-04-08: Added null asserts
        Assert.assertNotNull("expected array is null", expected); //$NON-NLS-1$
        Assert.assertNotNull("actual array is null", actual);//$NON-NLS-1$
        
        boolean equal = true;
        if (expected.length != actual.length) {
            equal = false;
        }
        else if (expected.getClass().getComponentType() != actual.getClass().getComponentType()) {
            equal = false;
        }
        
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expected));
        List<Object> actualList = new ArrayList<Object>(Arrays.asList(actual));
        if (equal) {
            for (Iterator<?> i=expectedList.iterator(); i.hasNext();) {
                Object next = i.next();
                for (Object actualEntry: actualList) {
                    if (isEqual(next, actualEntry)) {
                        actualList.remove(actualEntry);
                        i.remove();
                        break;
                    }
                }
            }
            if (expectedList.size() != 0 || actualList.size() != 0) {
                equal = false;
            }
        }
        if (!equal) {
            Assert.assertArrayEquals(expectedList.toArray(), actualList.toArray());
        }
    }

    protected boolean contains(String[] haystack, String needle) {
    	return Arrays.asList(haystack).contains(needle);
    }
}
