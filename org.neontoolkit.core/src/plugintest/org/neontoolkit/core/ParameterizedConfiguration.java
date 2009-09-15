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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * @author diwe
 *
 */
public class ParameterizedConfiguration extends Suite {
    public static final String CONFIGURATION_NAME = "ParameterizedConfiguration.configName"; //$NON-NLS-1$
    public static final String PROJECT_FACTORY_ID_KEY = "ProjectFactory"; //$NON-NLS-1$

    /**
     * Annotation for a method which provides properties objects to be injected 
     * as configuration into the test class constructor by <code>ParameterizedConfiguration</code>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Configurations {
    }

    /**
     * Annotation for a method which consumes the properties passed by a suite and returns 
     * a transformed list of configurations.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface UpdateConfigurations {
    }
    
    /**
     * Annotation for a method which provides a default configuration for a test class. This 
     * default configuration is only used, if no configuration is passed to the test class. 
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface DefaultConfiguration {
    }
    
    private class ParameterizedTestClassRunner extends BlockJUnit4ClassRunner {
        private Properties _config;

        /**
         * @param klass
         * @throws InitializationError
         */
        public ParameterizedTestClassRunner(Class<?> klass, Properties config) throws InitializationError {
            super(klass);
            _config = config;
        }
        
        @Override
        protected Object createTest() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(_config);
        }

        @Override
        protected String getName() {
            return String.format("%s[%s]", super.getName(), getConfigName()); //$NON-NLS-1$
        }

        @Override
        protected String testName(final FrameworkMethod method) {
            return String.format("%s[%s]", method.getName(), //$NON-NLS-1$
                    getConfigName());
        }
        
        private String getConfigName() {
            String configName = _config != null ? String.valueOf(_config.get(CONFIGURATION_NAME)) : null;
            return configName != null ? configName : "Unnamed"; //$NON-NLS-1$
        }

        /**
         * Parameterized test classes must have exactly one constructor with 
         * one Properties argument.
         */
        @Override
        protected void validateZeroArgConstructor(List<Throwable> errors) {
            if (getTestClass().getJavaClass().getConstructors().length == 1) {
                Class<?>[] parameters = getTestClass().getOnlyConstructor().getParameterTypes();
                if (parameters.length == 1 && parameters[0] == Properties.class) {
                    return;
                }
            }
            String errorMsg= "Test class should have exactly one public constructor with a Properties argument.";
            errors.add(new Exception(errorMsg));
        }

        @Override
        protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
            List<FrameworkMethod> allAfters = new ArrayList<FrameworkMethod>();
            allAfters.addAll(getTestClass().getAnnotatedMethods(AfterEarly.class));
            allAfters.addAll(getTestClass().getAnnotatedMethods(After.class));
            allAfters.addAll(getTestClass().getAnnotatedMethods(AfterLate.class));
            return new RunAfters(statement, allAfters, target);
        }
        
        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return childrenInvoker(notifier);
        }
    }
    
    private static final Logger _log = Logger.getLogger(ParameterizedConfiguration.class);
    private List<Runner> _configuredRunners;

    /**
     * @param klass
     * @throws InitializationError
     */
    public ParameterizedConfiguration(Class<?> klass, Collection<Properties> configuration) throws InitializationError, Exception {
        super(klass, Collections.<Runner>emptyList());
        _configuredRunners = new ArrayList<Runner>();
        if (configuration == null) {
            // no configuration passed, search for default configuration
            _log.warn("No configuration passed to ParameterizedConfiguration, look up default configuration!"); //$NON-NLS-1$
            configuration = getParametersList(klass);
        }
        Collection<Properties> updatedConfig = updateConfiguration(klass, configuration);
        if (updatedConfig != null && updatedConfig.size() > 0) {
            for (Properties config:updatedConfig) {
                _configuredRunners.add(new ParameterizedTestClassRunner(getTestClass().getJavaClass(),
                        config));
            }
        }
        else {
            // no configuration found, use default runner
            _log.warn("No configuration passed to ParameterizedConfiguration!"); //$NON-NLS-1$
            _configuredRunners.add(new BlockJUnit4ClassRunner(klass));
        }
    }
    
    /**
     * @throws Exception 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws InitializationError 
     * 
     */
    public ParameterizedConfiguration(Class<?> klass) throws InitializationError, IllegalAccessException, InvocationTargetException, Exception {
        this(klass, getParametersList(klass));
    }
    
    public static Collection<Properties> getParametersList(Class<?> klass) throws IllegalAccessException, InvocationTargetException, Exception {
        Method parametersMethod = getParametersMethod(klass);
        if (parametersMethod == null) {
            Method defaultParametersMethod = getDefaultConfigurationMethod(klass);
            if (defaultParametersMethod != null) {
                _log.warn("No public static parameters method on class "+ klass.getName()); //$NON-NLS-1$
                _log.warn("Using defined default configuration instead!"); //$NON-NLS-1$
                return Arrays.asList((Properties)defaultParametersMethod.invoke(null));
            }
            else {
                return null;
            }
        }
        else {
            return (Collection<Properties>)parametersMethod.invoke(null);
        }
    }
    
    public static Collection<Properties> updateConfiguration(Class<?> klass, Collection<Properties> parameters) throws Exception {
        Method updateMethod = getUpdateMethod(klass);
        if (updateMethod == null) {
            return parameters;
        }
        else {
            return (Collection<Properties>)updateMethod.invoke(null,(Object)parameters);
        }
    }

    private static Method getMethod(Class<?> klass, Class<?> annotationType) {
        for (Method each : klass.getMethods()) {
            if (Modifier.isStatic(each.getModifiers())) {
                Annotation[] annotations= each.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == annotationType)
                        return each;
                }
            }
        }
        return null;
    }

    
    private static Method getParametersMethod(Class<?> klass) throws Exception {
        return getMethod(klass, Configurations.class);
    }
    
    private static Method getDefaultConfigurationMethod(Class<?> klass) throws Exception {
        return getMethod(klass, DefaultConfiguration.class);
    }

    private static Method getUpdateMethod(Class<?> klass) throws Exception {
        Method method = getMethod(klass, UpdateConfigurations.class);
        return (method != null && method.getParameterTypes().length == 1) ? method : null;
    }
    
    @Override
    protected List<Runner> getChildren() {
        return _configuredRunners;
    }
}
