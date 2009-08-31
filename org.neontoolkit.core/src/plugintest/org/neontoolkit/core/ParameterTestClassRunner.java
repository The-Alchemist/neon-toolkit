/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 19.06.2009
 * Created by: diwe
 ******************************************************************************/
package org.neontoolkit.core;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.notification.RunNotifier;

@SuppressWarnings("restriction")
/**
 * @author diwe
 *
 */
// TestClassMethodsRunner removed in junit 4.5 AND 4.4
// see: http://junit.cvs.sourceforge.net/viewvc/junit/junit/src/org/junit/internal/runners/?hideattic=0
class ParameterTestClassRunner extends org.junit.internal.runners.TestClassMethodsRunner {
    private final Object[] fParameters;

    private final String _testSuffix;

    private final Constructor<?> fConstructor;

    ParameterTestClassRunner(Class<?> klass, Object[] parameters, String testSuffix) {
        super(klass);
        fParameters= parameters;
        _testSuffix = testSuffix;
        fConstructor= getOnlyConstructor();
    }

    @Override
    // Compile Error in JUnit 4.4
    protected Object createTest() throws Exception {
        return fConstructor.newInstance(fParameters);
    }
    
    @Override
    // Compile Error in JUnit 4.4
    protected String getName() {
        return String.format("[%s]", _testSuffix); //$NON-NLS-1$
    }
    
    @Override
    // Compile Error in JUnit 4.4
    protected String testName(final Method method) {
        return String.format("%s[%s]", method.getName(), _testSuffix); //$NON-NLS-1$
    }

    private Constructor<?> getOnlyConstructor() {
        // Compile Error in JUnit 4.4
        Constructor<?>[] constructors= getTestClass().getConstructors();
        assertEquals(1, constructors.length);
        return constructors[0];
    }

    @Override
    protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {
        return new ExtendedAfterTestMethodRunner(test, method, notifier, methodDescription(method));
    }
}
