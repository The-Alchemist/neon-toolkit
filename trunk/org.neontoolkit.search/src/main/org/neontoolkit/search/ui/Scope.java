package org.neontoolkit.search.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.neontoolkit.core.project.OntologyProjectManager;

/**
 * @author Nico Stieler
 * Created on: 15.09.2010
 */
public class Scope {
    private String[][] projects_ontologies;
    
    /**
     * 
     */
    public Scope() {
        super();
        projects_ontologies = new String[0][0];
    }

    /**
     * @return the projects_ontologies
     */
    public String[][] getProjects_ontologies() {
        return projects_ontologies;
    }

    /**
     * @param projectsOntologies the projects_ontologies to set
     */
    public void setProjects_ontologies(String[][] projectsOntologies) {
        projects_ontologies = projectsOntologies;
    }
    /**
     * @param projectsOntologies the projects_ontologies to set
     */
    public void setProjects(String[] projects) {
        projects_ontologies = new String[projects.length][];
        int counter = 0;
        for(String project: projects){
            projects_ontologies[counter++] = new String[]{project,null};
        }
    }
    

}
