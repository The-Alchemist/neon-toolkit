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

import java.util.Collection;
import java.util.Properties;

import org.junit.internal.builders.AnnotatedBuilder;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

/**
 * @author diwe
 *
 */
public class ExtendedAnnotatedBuilder extends AnnotatedBuilder {
    private Collection<Properties> _parameters;
    private RunnerBuilder _builder;

    /**
     * @param suiteBuilder
     */
    public ExtendedAnnotatedBuilder(RunnerBuilder suiteBuilder, Collection<Properties> parameters) {
        super(suiteBuilder);
        _builder = suiteBuilder;
        _parameters = parameters;
    }
    
    @Override
    public Runner buildRunner(Class<? extends Runner> runnerClass, Class<?> testClass) throws Exception {
        try {
            if (runnerClass == ParameterizedConfiguration.class) {
                return runnerClass.getConstructor(Class.class, Collection.class).newInstance(
                        new Object[] { testClass, _parameters });
            }
            else if (runnerClass == ParameterizedSuite.class) {
                return runnerClass.getConstructor(Class.class, Collection.class).newInstance(
                        new Object[] { testClass, _parameters });
            }
            else return super.buildRunner(runnerClass, testClass);
        } catch (NoSuchMethodException e) {
            return super.buildRunner(runnerClass, testClass);
        }
    }
}
