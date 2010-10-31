package org.neontoolkit.application.intro;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
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
