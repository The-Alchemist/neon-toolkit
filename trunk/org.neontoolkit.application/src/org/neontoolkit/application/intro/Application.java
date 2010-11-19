package org.neontoolkit.application.intro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
/**
 * @author Michael Erdmann
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
        setupLog4j();
        
    	Display display = PlatformUI.createDisplay();

//		setWorkspaceWithDialog(display);
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}



	/**
	 * setWorkspaceWithDialog opens a directory dialog to let the user choose his workspace
	 * 
	 * @param display - input display
	 */
	private void setWorkspaceWithDialog(Display display) {
    	Location instanceLoc = Platform.getInstanceLocation();
    	URL url = promptForInstanceLoc(display);
    	if (url != null) {
    		try {
    			instanceLoc.set(url, true);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
		
	}

	/**
	 * setWorkspaceWithDialogWithOuputAndRestriction opens a directory dialog to let the user choose his workspace, with restrictions
	 * 
	 * @param display - input display
	 */
	private void setWorkspaceWithDialogWithOuputAndRestriction(Display display) {
	
		String fileName = "C:\\Users\\nis\\out.txt";
		if( ! (new File(fileName)).exists()){
	
	    	BufferedWriter out = null;
	    	Location instanceLoc = Platform.getInstanceLocation();
	    	try{
	    	    // Create file 
	    	    FileWriter fstream = new FileWriter(fileName);
	    	    out = new BufferedWriter(fstream);
	    	    out.write("TEST");
	    	    out.write("\n");
	    	    if(instanceLoc.isSet()){
	        	    out.write("true");
	    	    }else{
	        	    out.write("false");
	    	    }
		    }catch (Exception e){
		    	//nothing to do
		    }
	    	URL url = promptForInstanceLoc(display);
	    	try{
	    	    out.write(url.toString());
	    	    out.write("\n");
		    }catch (Exception e){
		    	//nothing to do
		    }
	
	    	if (url != null) {
	    		try {
	    			instanceLoc.set(url, true);
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    	    	try{
	    	    	    out.write("Problem");
	    	    	    out.write("\n");
	    		    }catch (Exception e2){
	    		    	//nothing to do
	    		    }
	    		}
	    	}
	    	try{
	    	    out.write("TEST");
	    	    out.write("\n");
	    	    out.close();
		    }catch (Exception e){
		    	//nothing to do
		    }
		}
		
	}




/*
 * PRIVATE HELP METHODS to remove elements from the GUI
 */
private URL promptForInstanceLoc(Display display) {
       Shell shell = new Shell(display);
       DirectoryDialog dialog = new DirectoryDialog(shell);
       dialog.setText("Select Workspace Directory");
       dialog.setMessage("Select the workspace directory to use.");
       String dir = dialog.open();
       shell.dispose();
       try {
           return dir == null ? null : new File(dir).toURL();
       } catch (MalformedURLException e) {
           return null;
       }
   }



	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
	
//    public void earlyStartup() {
//        String productName = null;
//        IProduct product = Platform.getProduct();
//        if (product != null) {
//            productName = product.getName();
//        }
//
//        String version = null;
//        Bundle bundle = Platform.getBundle("org.neontoolkit.application"); //$NON-NLS-1$
//        if (bundle != null && bundle.getHeaders() != null) {
//            version = String.valueOf(bundle.getHeaders().get(Constants.BUNDLE_VERSION));
//        }
//
//        String buildNumber = "B???";
//        
//        // e.g. NeOnToolkit 2.3.0-B149
//        String message = String.format("Starting '%s' %s-%s", productName, version, buildNumber); //$NON-NLS-1$
//        Logger.getLogger("NeonToolkit").info(message); //$NON-NLS-1$
//        // See: /org.neontoolkit.plugin/about.mappings
//        System.setProperty("neon.BUILD_ID", buildNumber); //$NON-NLS-1$
//    }

    /**
     * properly finds/configures the log4j-properties file
     */
    private void setupLog4j() {
        URL installUrl = null;
        try {
            URL installUrlOriginal = Platform.getInstallLocation().getURL();
            installUrl = new URL(installUrlOriginal.toString().replaceAll(" ", "%20")); //$NON-NLS-1$//$NON-NLS-2$
            if(new File(new URI(installUrl + "log4j.properties")).exists()) {
            	PropertyConfigurator.configure(new URL(installUrl, "log4j.properties")); //$NON-NLS-1$
            }
        } catch (Throwable e) {
            System.err.println("Problems setting log4j.properties (" + installUrl + "): " + e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
	
}
