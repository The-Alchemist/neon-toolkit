/**
 * Copyright (c) 2008 ontoprise GmbH.
 */
package org.neontoolkit.core;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.AllTests;

/*
 * Created on 27.08.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class ParameterizedClassRequest extends Request {
	private static final String ERROR_MSG= "Runner class %s should have a public constructor with signature %s(Class testClass)"; //$NON-NLS-1$
	private final Class<?> _testClass;
	private final Collection<?> _parameters;

	public ParameterizedClassRequest(Class<?> testClass, Collection<?> parameters) {
		_testClass= testClass;
		_parameters = parameters;
	}
	
	@Override
	public Runner getRunner() {
		Class<? extends Runner> runnerClass = getRunnerClass(_testClass);
		if (runnerClass != null) {
			return getRunner(runnerClass);
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public Runner getRunner(Class<? extends Runner> runnerClass) {
		try {
			if (runnerClass == ParameterizedConfiguration.class) {
				return ParameterizedConfiguration.class.getConstructor(Class.class, Collection.class).newInstance(_testClass, _parameters);
			}
			else if (runnerClass == ParameterizedSuite.class) {
				return ParameterizedSuite.class.getConstructor(Class.class, Collection.class).newInstance(_testClass, _parameters);
			} 
			else {
				return runnerClass.getConstructor(Class.class).newInstance(new Object[] { _testClass });
			}
		} catch (NoSuchMethodException e) {
			String simpleName= runnerClass.getSimpleName();
			org.junit.internal.runners.InitializationError error= new org.junit.internal.runners.InitializationError(String.format(
					ERROR_MSG, simpleName, simpleName));
			return Request.errorReport(_testClass, error).getRunner();
		} catch (Exception e) {
			return Request.errorReport(_testClass, e).getRunner();
		}
	}

	@SuppressWarnings("restriction")
	Class<? extends Runner> getRunnerClass(final Class<?> testClass) {
		if (testClass.getAnnotation(Ignore.class) != null)
			return null;
		RunWith annotation= testClass.getAnnotation(RunWith.class);
		if (annotation != null) {
			return annotation.value();
		} else if (hasSuiteMethod()) {
			return AllTests.class;
		} else {
		    // TestClassRunner removed in junit 4.5
		    // see: http://junit.cvs.sourceforge.net/viewvc/junit/junit/src/org/junit/internal/runners/?hideattic=0
			return org.junit.internal.runners.TestClassRunner.class;
		}
	}
	
	public boolean hasSuiteMethod() {
		// TODO: check all attributes
		try {
			_testClass.getMethod("suite"); //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}

	boolean isPre4Test(Class<?> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}
}