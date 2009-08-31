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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.runners.Parameterized.Parameters;
import org.neontoolkit.core.ParameterizedConfiguration.DefaultParameters;
import org.neontoolkit.core.ParameterizedSuite.UpdateParameters;

/**
 * @author diwe
 *
 */
// CompositeRunner removed in junit 4.5
// see: http://junit.cvs.sourceforge.net/viewvc/junit/junit/src/main/java/org/junit/internal/runners/?hideattic=0
@SuppressWarnings("restriction")
public class RunAllParameterMethods extends org.junit.internal.runners.CompositeRunner {
    private final Class<?> fKlass;
    private final Collection<?> _passedParameters;

    public RunAllParameterMethods(Class<?> klass, Collection<?> passedParameters) throws Exception {
        super(klass.getName());
        fKlass= klass;
        _passedParameters = passedParameters;
        int i= 0;
        for (final Object each : getParametersList()) {
            if (each instanceof Object[]) {
                Object[] parameters = (Object[])each;
                String suffix = null;
                if (parameters.length > 0 && parameters[0] instanceof Properties) {
                    suffix = ((Properties)parameters[0]).getProperty(ParameterizedConfiguration.PARAMETER_TO_STRING);
                }
                if (suffix == null) {
                    suffix = new Integer(i).toString();
                }
                   // Compile Error in JUnit 4.4
                super.add(new ParameterTestClassRunner(klass, (Object[])each, suffix));
            }
            else {
                Method method = getParametersMethod();
                if (method != null) {
                    throw new Exception(String.format("%s.%s() must return a Collection of arrays.", fKlass.getName(), method.getName())); //$NON-NLS-1$
                }
                else {
                    throw new Exception(String.format("Parameters of %s must be a Collection of arrays.", fKlass.getName())); //$NON-NLS-1$
                }
            }
        }
    }

    private Collection<?> getParametersList() throws IllegalAccessException, InvocationTargetException, Exception {
        if (_passedParameters != null) {
            return update(_passedParameters);
        }
        else {
            Method parametersMethod = getParametersMethod();
            if (parametersMethod != null) {
                return update((Collection<?>) parametersMethod.invoke(null));
            }
            else {
                Method defaultParametersMethod = getDefaultParametersMethod();
                if (defaultParametersMethod != null) {
                    System.err.println("No public static parameters method on class "+ getName()); //$NON-NLS-1$
                    System.err.println("Using defined default configuration instead!"); //$NON-NLS-1$
                    return update((Collection<?>) defaultParametersMethod.invoke(null));
                }
                else {
                    List<Properties[]> list = new ArrayList<Properties[]>();
                    list.add(new Properties[]{new Properties()});
                    System.err.println("No public static parameters and default parameters method on class "+ getName()); //$NON-NLS-1$
                    System.err.println("Running without configuration instead!"); //$NON-NLS-1$
                    return list;
                }
            }
        }
    }
    
    private Method getUpdateMethod(Class<?> klass) throws Exception {
        for (Method each : klass.getMethods()) {
            if (Modifier.isStatic(each.getModifiers())) {
                Annotation[] annotations= each.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == UpdateParameters.class && each.getParameterTypes().length == 1)
                        return each;
                }
            }
        }
        return null;
    }
    
    private Collection<?> update(Collection<?> parameters) throws Exception {
        Method updateMethod = getUpdateMethod(fKlass);
        if (updateMethod == null) {
            return parameters;
        }
        else {
            return (Collection<?>)updateMethod.invoke(null,(Object)parameters);
        }
    }
    
    private Method getParametersMethod() throws Exception {
        for (Method each : fKlass.getMethods()) {
            if (Modifier.isStatic(each.getModifiers())) {
                Annotation[] annotations= each.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == Parameters.class)
                        return each;
                }
            }
        }
        return null;
    }

    private Method getDefaultParametersMethod() throws Exception {
        for (Method each : fKlass.getMethods()) {
            if (Modifier.isStatic(each.getModifiers())) {
                Annotation[] annotations= each.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == DefaultParameters.class)
                        return each;
                }
            }
        }
        return null;
    }
}
