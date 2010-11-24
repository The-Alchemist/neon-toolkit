package org.neontoolkit.application.intro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
 * @author Nico Stieler
 * 
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	static String recentWorkspace = Messages.Application_configurationFile_recentWorkspace;
	static String seperator = Messages.Application_configurationFile_recentWorkspace_seperator;
	static String directory = Messages.Application_configurationFile_directory;
	static String connector = Messages.Application_configurationFile_connector;
	static String configFileName = Messages.Application_configurationFile_configFileName;
	

	 String datePattern = Messages.Application_datePattern;
	 String line0;
	 String line1 = Messages.Application_configurationFile_line1;
	 String line2 = Messages.Application_configurationFile_line2;
	 String line3 = Messages.Application_configurationFile_line3;
	 String line4 = Messages.Application_configurationFile_line4;
	 String line5;
	
	Display _display;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
        setupLog4j();
        
    	_display = PlatformUI.createDisplay();

    	handleWorkspaceSettings();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(_display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			_display.dispose();
		}
	}



	/**
	 * handles the workspace settings:
	 * 	(1) reads existing configuration file and extract and set the last-used workspace
	 *  (2) opens dialog to let the user choose the workspace + create configuration file
	 */
	private void handleWorkspaceSettings() {
		File file = new File("configuration");
		String text = Application.directory + Application.connector + Application.configFileName;
		file = new File(text);
		if(!(file.exists() && setWorkspaceBasedOnConfigFile(text))){
			setWorkspaceWithDialog();
		}
	}

	/**
	 * setWorkspaceWithDialog opens a directory dialog to let the user choose his workspace
	 * 
	 * @param display - input display
	 * @throws Exception 
	 */
	private boolean setWorkspaceBasedOnConfigFile(String configFile){
		String line = getLineStartingWith(recentWorkspace, configFile);
		if(line == null){
			return false;
		}
		String urlText = extractURLFromLine(line);
		URL url;
		try {
			url = new URL(urlText);
		} catch (MalformedURLException e) {
			return false;
		}
		
    	return setWorkspace(url);
    	
		
	}
	
	/**
	 * extracts and returns the string of an URL from a given string(line)
	 * 
	 * @param line - the given string to investigate
	 * @return extracted string
	 */
	private String extractURLFromLine(String line) {
		line = line.substring(recentWorkspace.length());
		if(line.contains(seperator)){
			line = line.split(seperator)[0];
		}
		return line;
	}

	/**
	 * returns Line of a given file which starts with a given string
	 * 
	 * @param prefix - prefix of the line of interest
	 * @param configFile - URL of the file to investigate
	 * @return line of interest, if it exists, otherwise it returns null.
	 */
	private String getLineStartingWith(String prefix, String configFile) {
		try {
		    BufferedReader in = new BufferedReader(new FileReader(configFile));
		    String line;
		    while ((line = in.readLine()) != null) {
		    	if(line.startsWith(prefix)){
		    		return line;
		    	}
		    }
		    in.close();
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * set the workspace after user input via an dialog
	 */
	private void setWorkspaceWithDialog() {
    	URL url = promptForWorkspaceLocation();
    	setWorkspace(url);
    	setUpConfigurationFile(url);
	}
	
	/**
	 * write content into the configuration file
	 * 
	 * @param url - workspace URL
	 * @return success of writing.
	 */
	private boolean setUpConfigurationFile(URL url) {
		if(new File(Application.directory).mkdirs())
			try{
				if(new File(Application.directory + Application.connector + Application.configFileName).createNewFile()){
					FileWriter fstream = new FileWriter(Application.directory + Application.connector + Application.configFileName);
					BufferedWriter out = new BufferedWriter(fstream);
					line0 = "#" + currentDateWithDatePattern();
					line5 = recentWorkspace + createCorrectStringFormatForURL(url);
					out.write(line0); out.newLine();
					out.write(line1); out.newLine();
					out.write(line2); out.newLine();
					out.write(line3); out.newLine();
					out.write(line4); out.newLine();
					out.write(line5); out.newLine();
					out.close();
					return true;
				}
			}catch (Exception e){
				return false;
			}
		return false;
	}

	/**
	 * determines and returns a string of the URL in the correct format for the configuration file
	 * 
	 * @param url - URL to transfer to a string
	 * @return string of the URL in the correct format
	 */
	private String createCorrectStringFormatForURL(URL url) {
		 String urlString = url.toString();
		 String prefix = "file:/";
		 String suffix = "/";
		 if(urlString.startsWith(prefix))
			 urlString = urlString.substring(prefix.length());
		 if(urlString.endsWith(suffix))
			 urlString = urlString.substring(0,urlString.length() - suffix.length());

		 urlString = urlString.replace(":", "\\:");
		 urlString = urlString.replace("/", "\\\\");
		return urlString;
	}

	/**
	 * determines and returns a string of the current date in the given pattern
	 * 
	 * @return the String of the date
	 */
	private String currentDateWithDatePattern() {
		SimpleDateFormat simpleDateFormater = new SimpleDateFormat(datePattern, Locale.US);
		return simpleDateFormater.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * Sets the Workspace of the NeOn toolkit
	 * 
	 * @param url to set
	 * @return success of setting
	 */
	private boolean setWorkspace(URL url) {
    	Location instanceLoc = Platform.getInstanceLocation();
    	if (url != null) {
    		try {
    			instanceLoc.set(url, true);
    			return true;
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return false;
	}
	
	/**
	 * Opens a dialog to choose a workspace
	 * 
	 * @return URL of selected Directory
	 */
	private URL promptForWorkspaceLocation() {
       Shell shell = new Shell(_display);
       DirectoryDialog dialog = new DirectoryDialog(shell);
       dialog.setText("Select Workspace Directory");
       dialog.setMessage("Select the workspace directory to use.");
       String dir = dialog.open();
       shell.dispose();
       try {
    	   //if null : default      else: user input
           return dir == null ? new File(Messages.Application_defaultWorkspace).toURL() : new File(dir).toURL();
       } catch (MalformedURLException e) {
           return null;
       }
   }



///**
// * setWorkspaceWithDialogWithOuputAndRestriction opens a directory dialog to let the user choose his workspace, with restrictions
// * 
// * @param display - input display
// */
//private void setWorkspaceWithDialogWithOuputAndRestriction(Display display) {
//
//	String fileName = "C:\\Users\\nis\\out.txt";
//	if( ! (new File(fileName)).exists()){
//
//    	BufferedWriter out = null;
//    	Location instanceLoc = Platform.getInstanceLocation();
//    	try{
//    	    // Create file 
//    	    FileWriter fstream = new FileWriter(fileName);
//    	    out = new BufferedWriter(fstream);
//    	    out.write("TEST");
//    	    out.write("\n");
//    	    if(instanceLoc.isSet()){
//        	    out.write("true");
//    	    }else{
//        	    out.write("false");
//    	    }
//	    }catch (Exception e){
//	    	//nothing to do
//	    }
//    	URL url = promptForInstanceLoc(display);
//    	try{
//    	    out.write(url.toString());
//    	    out.write("\n");
//	    }catch (Exception e){
//	    	//nothing to do
//	    }
//
//    	if (url != null) {
//    		try {
//    			instanceLoc.set(url, true);
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    	    	try{
//    	    	    out.write("Problem");
//    	    	    out.write("\n");
//    		    }catch (Exception e2){
//    		    	//nothing to do
//    		    }
//    		}
//    	}
//    	try{
//    	    out.write("TEST");
//    	    out.write("\n");
//    	    out.close();
//	    }catch (Exception e){
//	    	//nothing to do
//	    }
//	}
//	
//}

//	private void showMessageBox(String text) {
//	    Shell shell = new Shell(_display);
//	    MessageBox dialog = new MessageBox(shell);
//	    dialog.setText("Message");
//	    dialog.setMessage(text);
//	    dialog.open();
//	    shell.dispose();
//	}



	
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
