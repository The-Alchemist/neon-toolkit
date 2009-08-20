package com.ontoprise.ontostudio.owl.gui.commands;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;
import org.neontoolkit.core.ParameterizedConfiguration;
import org.neontoolkit.core.ParameterizedSuite;

import com.ontoprise.ontostudio.owl.gui.commands.annotationProperty.CreateRemoveAnnotationPropertyTest;
import com.ontoprise.ontostudio.owl.gui.commands.annotations.AnnotationsTest;
import com.ontoprise.ontostudio.owl.gui.commands.clazz.CreateEditRemoveRestrictionsTest;
import com.ontoprise.ontostudio.owl.gui.commands.clazz.CreateRemoveClazzesTest;
import com.ontoprise.ontostudio.owl.gui.commands.clazz.EquivalentDisjointClazzesTest;
import com.ontoprise.ontostudio.owl.gui.commands.clazz.GetDescriptionHitsTest;
import com.ontoprise.ontostudio.owl.gui.commands.dataProperty.CreateRemoveDataPropertyTest;
import com.ontoprise.ontostudio.owl.gui.commands.dataProperty.DataPropertyDomainAndRangeTest;
import com.ontoprise.ontostudio.owl.gui.commands.dataProperty.DataPropertyTaxonomyTest;
import com.ontoprise.ontostudio.owl.gui.commands.dataProperty.IsDataPropertyTest;
import com.ontoprise.ontostudio.owl.gui.commands.events.SyncEventHandlerTest;
import com.ontoprise.ontostudio.owl.gui.commands.individual.CreateMoveIndividualTest;
import com.ontoprise.ontostudio.owl.gui.commands.individual.DataPropertyMemberHitsTest;
import com.ontoprise.ontostudio.owl.gui.commands.individual.EquivalentDifferentIndividualsTest;
import com.ontoprise.ontostudio.owl.gui.commands.individual.ObjectPropertyMemberHitsTest;
import com.ontoprise.ontostudio.owl.gui.commands.namespaces.CreateRemoveNamespacesTest;
import com.ontoprise.ontostudio.owl.gui.commands.objectProperty.AutoCompletionTest;
import com.ontoprise.ontostudio.owl.gui.commands.objectProperty.CreateRemoveObjectPropertyTest;
import com.ontoprise.ontostudio.owl.gui.commands.objectProperty.InverseObjectPropertyTest;
import com.ontoprise.ontostudio.owl.gui.commands.objectProperty.IsObjectPropertyTest;
import com.ontoprise.ontostudio.owl.gui.commands.objectProperty.ObjectPropertyDomainAndRangeTest;
import com.ontoprise.ontostudio.owl.gui.commands.objectProperty.ObjectPropertyTaxonomyTest;
import com.ontoprise.ontostudio.owl.gui.commands.rename.GetAllImportingOntologiesTest;
import com.ontoprise.ontostudio.owl.gui.commands.rename.RenameAnnotationPropertyTest;
import com.ontoprise.ontostudio.owl.gui.commands.rename.RenameProjectTest;

/*
 * Created on 04.12.2008
 * Created by Werner Hihn
 *
 * Function: 
 * Keywords: 
 */
@RunWith(ParameterizedSuite.class)
@SuiteClasses( { 

    CreateValidUriTest.class,
    PropertyAttributesTest.class,
    CreateRemoveAnnotationPropertyTest.class,
    AnnotationsTest.class,
    CreateEditRemoveRestrictionsTest.class,
    CreateRemoveClazzesTest.class, 
    EquivalentDisjointClazzesTest.class,
    GetDescriptionHitsTest.class,
    CreateRemoveDataPropertyTest.class,
    DataPropertyDomainAndRangeTest.class,
    DataPropertyTaxonomyTest.class,
    IsDataPropertyTest.class,
    IsObjectPropertyTest.class,
    CreateMoveIndividualTest.class,
    DataPropertyMemberHitsTest.class,
    EquivalentDifferentIndividualsTest.class,
    ObjectPropertyMemberHitsTest.class,
    AutoCompletionTest.class,
    CreateRemoveNamespacesTest.class,
    CreateRemoveObjectPropertyTest.class,
    InverseObjectPropertyTest.class,
    ObjectPropertyDomainAndRangeTest.class,
    ObjectPropertyTaxonomyTest.class,
    RenameProjectTest.class,
    GetAllImportingOntologiesTest.class,
    SyncEventHandlerTest.class,
    RenameAnnotationPropertyTest.class,
})

public class AllPluginTests {
    /**
     * @return parameters
     */
    @Parameters
    public static Collection<Properties[]> getParameters() {
        List<Properties[]> list = new ArrayList<Properties[]>();
        Properties properties = new Properties();
        properties.put(ParameterizedConfiguration.PARAMETER_TO_STRING, "ManchesterImplementation"); //$NON-NLS-1$
        list.add(new Properties[] { properties });
        return list;
    }
}