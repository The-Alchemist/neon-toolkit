/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.core;

import java.util.Collection;

import org.junit.runner.Request;
import org.junit.runner.Runner;

/*
 * Created on 27.08.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class ParameterizedClassesRequest extends Request {
	private final Class<?>[] _classes;
	private final String _testName;
	private final Collection<?> _parameters;
	
	public ParameterizedClassesRequest(String name, Collection<?> parameters, Class<?>... classes) {
		_classes= classes;
		_testName= name;
		_parameters = parameters;
	}

	@SuppressWarnings("restriction")
	@Override 
	public Runner getRunner() {
	    // CompositeRunner removed in junit 4.5
	    // see: http://junit.cvs.sourceforge.net/viewvc/junit/junit/src/main/java/org/junit/internal/runners/?hideattic=0
		org.junit.internal.runners.CompositeRunner runner= new org.junit.internal.runners.CompositeRunner(_testName);
		for (Class<?> clazz : _classes) {
			Runner childRunner= getParameterizedClassRequest(clazz, _parameters).getRunner();
			if (childRunner != null)
				runner.add(childRunner);
		}
		return runner;
	}

	public static Request getParameterizedClassRequest(Class<?> clazz, Collection<?> parameters) {
		return new ParameterizedClassRequest(clazz, parameters);
	}
}
