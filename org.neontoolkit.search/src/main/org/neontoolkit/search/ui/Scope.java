package org.neontoolkit.search.ui;

/**
 * @author Nico Stieler
 * Created on: 15.09.2010
 * 
 * The Scope class provides ontologies and projects for the entity search
 * 
 * e.g.:
 * {{"projectName1","ontologyName1"},
 *  {"projectName1","ontologyName2"},
 *  {"projectName2",null}}
 * 
 * In this example the Scope is Ontology1 and Ontology2 of Project1 and the whole Project2
 * 
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
