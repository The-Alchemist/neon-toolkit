/**
 * Copyright (c) 2008 ontoprise GmbH.
 */
package org.neontoolkit.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
public class ParameterizedConfiguration extends org.junit.internal.runners.TestClassRunner {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface DefaultParameters {
    }

    public static final String PARAMETER_TO_STRING = "ParameterToString"; //$NON-NLS-1$
    public static final String PROJECT_FACTORY_ID_KEY = "ProjectFactory"; //$NON-NLS-1$

	public static Collection<Object[]> eachOne(Object... params) {
		List<Object[]> results= new ArrayList<Object[]>();
		for (Object param : params)
			results.add(new Object[] { param });
		return results;
	}
	
	public ParameterizedConfiguration(final Class<?> klass, Collection<?> parameters) throws Exception {
        // Compile Error in JUnit 4.4
		super(klass, new RunAllParameterMethods(klass, parameters));
	}
	
	public ParameterizedConfiguration(final Class<?> klass) throws Exception {
        // Compile Error in JUnit 4.4
		super(klass, new RunAllParameterMethods(klass, null));
	}

	@Override
    // Compile Error in JUnit 4.4
	protected void validate(org.junit.internal.runners.MethodValidator methodValidator) {
		methodValidator.validateStaticMethods();
		methodValidator.validateInstanceMethods();
	}
}
