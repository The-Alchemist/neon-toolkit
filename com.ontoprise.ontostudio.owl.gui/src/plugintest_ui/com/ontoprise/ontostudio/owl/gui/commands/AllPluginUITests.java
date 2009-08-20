package com.ontoprise.ontostudio.owl.gui.commands;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.neontoolkit.core.ParameterizedSuite;

import com.ontoprise.ontostudio.owl.gui.commands.ontologies.FolderSortingUITest;
import com.ontoprise.ontostudio.owl.gui.commands.ontologies.RenameImportedEntitiesUITest;
import com.ontoprise.ontostudio.owl.gui.commands.ui.CreateOWLOntologyUITest;
import com.ontoprise.ontostudio.owl.gui.commands.ui.MoveClazzesRefreshUITest;
import com.ontoprise.ontostudio.owl.gui.commands.ui.RenameProjectUITest;

/*
 * Created on 04.12.2008
 * Created by Werner Hihn
 *
 * Function: 
 * Keywords: 
 */
@RunWith(ParameterizedSuite.class)
@SuiteClasses( {// Build Master J 2009-06-26: Activate tests
/*    */FolderSortingUITest.class,//
        RenameImportedEntitiesUITest.class,//
        CreateOWLOntologyUITest.class,//
        MoveClazzesRefreshUITest.class,//
        RenameProjectUITest.class,//
})
public class AllPluginUITests {

}