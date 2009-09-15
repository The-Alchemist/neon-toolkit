/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.runners.model.InitializationError;
import org.junit.runners.Suite;
import org.junit.runners.model.RunnerBuilder;

/**
 * @author Dirk Wenke
 *
 */
public class ParameterizedSuite extends Suite {
    private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
        List<Class<?>> suiteClasses = new ArrayList<Class<?>>();
        Class<?> klassOrSuperklass = klass;
        while (klassOrSuperklass != null) { 
            SuiteClasses annotation= klassOrSuperklass.getAnnotation(SuiteClasses.class);
            if (annotation != null) {
                suiteClasses.addAll(Arrays.asList(annotation.value()));
            }
            klassOrSuperklass = klassOrSuperklass.getSuperclass();
        }
        if (suiteClasses.size() == 0)
            throw new InitializationError(String.format("Class '%s' or one of its superclasses must have a SuiteClasses annotation", klass.getName())); //$NON-NLS-1$
        return suiteClasses.toArray(new Class<?>[0]);
    }

    /**
     * @param klass
     * @param builder
     * @throws Exception 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    public ParameterizedSuite(Class<?> klass, RunnerBuilder builder) throws IllegalAccessException, InvocationTargetException, Exception {
        this(klass, null, ParameterizedConfiguration.getParametersList(klass));
    }
    
    public ParameterizedSuite(Class<?> klass, Collection<Properties> properties) throws IllegalAccessException, InvocationTargetException, Exception {
        this(klass, properties, ParameterizedConfiguration.getParametersList(klass));
    }
    
    private ParameterizedSuite(Class<?>klass , Collection<Properties> passedProperties, Collection<Properties> localProperties) throws Exception {
        super(new ParameterizedRunnerBuilder(
                ParameterizedConfiguration.updateConfiguration(klass, localProperties != null ? localProperties : passedProperties)),
                klass,
                getAnnotatedClasses(klass));
    }
}
