/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package com.ontoprise.ontostudio.owl.gui.io;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.neontoolkit.io.filter.FileFilter;
import org.neontoolkit.io.wizard.AbstractExportSelectionPage;
import org.neontoolkit.io.wizard.AbstractExportWizard;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.project.SaveOntology;

/* 
 * Created on 17.11.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, Export, FileSystem, Ontology
 */

/**
 * Export Wizard to save an (ontology-)file to the FileSystem
 */
public class FileSystemExportWizard extends AbstractExportWizard {

    public FileSystemExportWizard() {
        super();
        super.setSupportedProjectOntologyLanguage(new String[]{OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE});
        setWindowTitle(Messages.FileSystemExportWizard_0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyImportWizard#doExport(java.lang.String,
     *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doExport(String projectName, String ontologyUri, String physicalUri, String fileFormat, IProgressMonitor monitor) throws Exception {
        monitor.beginTask(Messages.FileSystemExportWizard_1 + ontologyUri + Messages.FileSystemExportWizard_2
                + physicalUri + "\".", //$NON-NLS-1$
                1);
            monitor.beginTask(Messages.FileSystemExportWizard_4, IProgressMonitor.UNKNOWN);
//            OntologyFileSystemExport eo = new OntologyFileSystemExport(projectName, ontologyUri, physicalUri,fileFormat);
            SaveOntology so = new SaveOntology(projectName, ontologyUri, physicalUri);
            so.run();
//            ImportExportControl control = new ImportExportControl();
//            control.exportFileSystem(fileFormat, projectName, moduleId, physicalUri);
//            _messages = so.getMessages();
            monitor.worked(1);
        monitor.done();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyExportWizard#getPageDescription()
     */
    @Override
    public String getPageDescription() {
        return Messages.FileSystemExportWizard_6;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyExportWizard#getPageTitle()
     */
    @Override
    public String getPageTitle() {
        return Messages.FileSystemExportWizard_13;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractExportWizard#getExportSelectionPage()
     */
    @Override
    public AbstractExportSelectionPage getExportSelectionPage() {
        return new FileSystemExportSelectionPage(getFileFilters());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.wizard.AbstractExportWizard#isFileExisting(java.net.URL)
     */
    @Override
    public boolean isFileExisting(URL url) {
        File file = new File(url.getFile());
        return file.exists();
    }
    
    @Override
    public boolean isFileExisting(URI uri) {
        File file = new File(uri);
        return file.exists();
    }
    
    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.io.wizard.AbstractExportWizard#getFileExistingMessage()
     */
    @Override
    protected String getFileExistingMessage(String fileName) {
      String message = Messages.FileSystemExportWizard_12;
      String messageFileName = fileName;
      messageFileName = messageFileName.replaceFirst("/", ""); //$NON-NLS-1$ //$NON-NLS-2$
//      messageFileName = messageFileName.replaceAll("/", "/ "); //$NON-NLS-1$ //$NON-NLS-2$
      message += messageFileName;
      message += Messages.FileSystemExportWizard_13;
      return message;
    }
    
    @Override
    public void doFinish(String projectName, String ontologyUri, String physicalUri, FileFilter fileFilter, IProgressMonitor monitor) throws Exception {
    _messages = new String[0][0];

      monitor.beginTask(Messages.FileSystemExportWizard_8 + ontologyUri + Messages.FileSystemExportWizard_9
              + physicalUri + "\".", //$NON-NLS-1$
              1);
      _exportedOntology = ontologyUri;
      _exportedProject = projectName;
      _exportedUri = physicalUri;
      doExport(projectName, ontologyUri, physicalUri, fileFilter.getOntologyFileFormat(), monitor);
      monitor.worked(1);

  }
    
    @Override
    public FileFilter[] getFileFilters() {
        if(_fileFilters == null) {
            _fileFilters = 
                new FileFilter[] { 
                    new OWL2OntologyFileFilter()
                    };
        }
        return _fileFilters;
    }


}
