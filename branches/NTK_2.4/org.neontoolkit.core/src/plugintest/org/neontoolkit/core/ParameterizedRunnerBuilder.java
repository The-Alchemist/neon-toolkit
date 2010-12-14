/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 30.06.2009
 * Created by: diwe
 ******************************************************************************/
package org.neontoolkit.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.internal.builders.IgnoredBuilder;
import org.junit.internal.builders.JUnit4Builder;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

/**
 * @author diwe
 *
 */
public class ParameterizedRunnerBuilder extends RunnerBuilder {
    private Collection<Properties> _parameters;
    
    public ParameterizedRunnerBuilder(Collection<Properties> parameters) {
        _parameters = parameters;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        List<RunnerBuilder> builders= Arrays.asList(
                new IgnoredBuilder(),
                new ExtendedAnnotatedBuilder(this, _parameters),
                new JUnit4Builder());

        for (RunnerBuilder each : builders) {
            Runner runner= each.safeRunnerForClass(testClass);
            if (runner != null)
                return runner;
        }
        return null;
    }

}
