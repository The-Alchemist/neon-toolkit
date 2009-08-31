/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 20.04.2009
 * Created by: krekeler
 ******************************************************************************/
package org.neontoolkit.core;

/**
 * @author krekeler
 *
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.internal.runners.TestIntrospector;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

@SuppressWarnings("restriction")
public class ExtendedAfterTestMethodRunner extends TestMethodRunner {
    private TestIntrospector _testIntrospector;
    private Object _test;
    
    public ExtendedAfterTestMethodRunner(Object test, Method method, RunNotifier notifier, Description description) {
        super(test, method, notifier, description);
        _test = test;
        _testIntrospector = new TestIntrospector(test.getClass());
    }

    @Override
    protected void executeMethodBody() throws IllegalAccessException, InvocationTargetException {
        try {
            super.executeMethodBody();
        } finally {
            runAftersEarly();
        }
    }
    
    @Override
    public void runProtected() {
        try {
            super.runProtected();
        } finally {
            runAftersLate();
        }
    }
    
    // Try to run all @AfterEarly regardless
    private void runAftersEarly() {
        runAnnotation(AfterEarly.class);
    }
    
    // Try to run all @AfterEarly regardless
    private void runAftersLate() {
        runAnnotation(AfterLate.class);
    }
    
    private void runAnnotation(Class<? extends Annotation> annotation) {
        List<Method> afters= _testIntrospector.getTestMethods(annotation);
        for (Method after : afters)
            try {
                invokeMethod(after);
            } catch (InvocationTargetException e) {
                addFailure(e.getTargetException());
            } catch (Throwable e) {
                addFailure(e); // Untested, but seems impossible
            }
    }
    
    private void invokeMethod(Method method) throws Exception {
        method.invoke(_test);
    }
}

