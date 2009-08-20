/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 06.04.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;
import org.neontoolkit.core.ParameterizedSuite;

import com.ontoprise.ontostudio.owl.gui.commands.ontologies.H2AddOntologyUITest;

/**
 * @author werner
 *
 */
@RunWith(ParameterizedSuite.class)
@SuiteClasses({
    H2AddOntologyUITest.class
})
public class H2OWLSuiteUI {

    @Parameters
    public static Collection<Properties[]> getParameters() {
        List<Properties[]> list = new ArrayList<Properties[]>();

        list.add(new Properties[]{ createConfig(true) });
        list.add(new Properties[]{ createConfig(false) });
        return list;
    }
    
    private static Properties createConfig(boolean cngOn) {
        Properties properties = new Properties();
//        properties.put(IConfig.STORAGE, Storage.H2.toString());
//        properties.put(IConfig.USE_INDEXER, "on");
//        properties.put("H2.URL", "jdbc:h2:mem:test");
//        properties.put("H2.User", "sa");
//        properties.put("H2.Password", "");
//        properties.put(IConfig.EVALUATION_METHOD, "DynamicFiltering");
//        properties.put(IConfig.CONCEPT_NAMES_GROUND, cngOn ? Config.ON : Config.OFF);
//        properties.put(ParameterizedConfiguration.PARAMETER_TO_STRING, "H2Cng"+properties.getProperty(IConfig.CONCEPT_NAMES_GROUND));
        return properties;
    }

}
