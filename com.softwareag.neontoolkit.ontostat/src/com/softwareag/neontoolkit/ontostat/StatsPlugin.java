package com.softwareag.neontoolkit.ontostat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("nls")
public class StatsPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.softwareag.neontoolkit.ontostat";
	public static final String STATPROVIDER_ID = "com.softwareag.neontoolkit.ontostat.statproviders";
	private static StatsPlugin plugin;
	
	public StatsPlugin() {
	}

	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
    public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static StatsPlugin getDefault() {
		return plugin;
	}

	public StatsProvider[] getStatsProviders() {
		Map<String,StatsProvider> res = new HashMap<String,StatsProvider>();
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(STATPROVIDER_ID);
		for (IConfigurationElement confe : config)
			try {
				Object o = confe.createExecutableExtension("providerClass");
				String id = confe.getAttribute("id");
				String order_str = confe.getAttribute("order");
				int order=99;
				try {
					order=Integer.parseInt(order_str);
				} catch (NumberFormatException e) {				
				}
                String title = confe.getAttribute("title");
                String tooltip = confe.getAttribute("tooltip");
				String iconImageURL = confe.getAttribute("icon");
				ImageDescriptor iconImage=null;
				if (iconImageURL!=null && !iconImageURL.isEmpty())
					iconImage = ImageDescriptor.createFromURL(FileLocator.find(this.getBundle(), new Path(iconImageURL), null));
				if (o instanceof StatsProvider) {
					((StatsProvider)o).setDefaults(title, iconImage!=null?iconImage.createImage():null, tooltip);
					res.put((order<10?"0":"")+order+title+id, (StatsProvider)o);
				}
			} catch (Exception e1) {
			}
		if (res.size()==0)
			return new StatsProvider[0];
		StatsProvider[] resarr = new StatsProvider[res.size()];
		String[] keys = res.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		for (int i=0;i<res.size();i++)
			resarr[i]=res.get(keys[i]);
		return resarr;
	}
}
