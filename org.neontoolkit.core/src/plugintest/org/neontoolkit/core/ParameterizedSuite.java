/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Created on 27.08.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
@SuppressWarnings("restriction")
// TestClassRunner removed in junit 4.5
// see: http://junit.cvs.sourceforge.net/viewvc/junit/junit/src/org/junit/internal/runners/?hideattic=0
public class ParameterizedSuite extends org.junit.internal.runners.TestClassRunner {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface UpdateParameters {
    }

    /**
	 */
	public ParameterizedSuite(Class<?> klass) throws IllegalAccessException, InvocationTargetException, Exception {
		this(klass, getParametersList(klass), getAnnotatedClasses(klass));
	}

	/**
	 * Internal use only.
	 * @throws Exception 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public ParameterizedSuite(Class<?> klass, Collection<?> parameters) throws Exception {
		this(klass, parameters != null ? parameters : getParametersList(klass), getAnnotatedClasses(klass));
	}

	// This won't work correctly in the face of concurrency. For that we need to
	// add parameters to getRunner(), which would be much more complicated.
	private static Set<Class<?>> parents = new HashSet<Class<?>>();
	
	protected ParameterizedSuite(Class<?> klass, Collection<?> parameters, Class<?>[] annotatedClasses) throws Exception {
	      // Compile Error in JUnit 4.4
        super(klass, new ParameterizedClassesRequest(klass.getName(), update(klass, parameters), annotatedClasses).getRunner());
		parents.remove(klass);
	}

	private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws org.junit.internal.runners.InitializationError {
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
			throw new org.junit.internal.runners.InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName())); //$NON-NLS-1$
		return suiteClasses.toArray(new Class<?>[0]);
	}
	
	private static Collection<?> update(Class<?> klass, Collection<?> parameters) throws Exception {
        Method updateMethod = getUpdateMethod(klass);
        if (updateMethod == null) {
            return parameters;
        }
        else {
            return (Collection<?>)updateMethod.invoke(null,(Object)parameters);
        }
	}

	private static Method getUpdateMethod(Class<?> klass) throws Exception {
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
	
	@Override
    // Compile Error in JUnit 4.4
	protected void validate(org.junit.internal.runners.MethodValidator methodValidator) {
		methodValidator.validateStaticMethods();
		methodValidator.validateInstanceMethods();
	}

	private static Collection<?> getParametersList(Class<?> klass) throws IllegalAccessException, InvocationTargetException, Exception {
		Method parametersMethod = getParametersMethod(klass);
		if (parametersMethod == null) {
			return null;
		}
		else {
			return (Collection<?>)parametersMethod.invoke(null);
		}
	}
	
	private static Method getParametersMethod(Class<?> klass) throws Exception {
		for (Method each : klass.getMethods()) {
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
}
