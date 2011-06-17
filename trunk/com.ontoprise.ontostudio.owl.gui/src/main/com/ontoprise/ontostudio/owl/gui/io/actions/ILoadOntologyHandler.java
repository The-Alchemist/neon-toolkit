/**
 *
 */
package com.ontoprise.ontostudio.owl.gui.io.actions;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Nico Stieler
 * Created on: 01.06.2011
 */
public interface ILoadOntologyHandler {
    public void fixedProject(IStructuredSelection selection, String ontologyURI);
}
