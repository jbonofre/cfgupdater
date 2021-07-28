package net.nanthrax.example.cfgupdater.bundleb;

import org.osgi.annotation.bundle.Header;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.util.Dictionary;

@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext bundleContext) {
        // it's possible to use ConfigurationAdmin or change the cfg file directly
        ServiceReference<ConfigurationAdmin> reference = bundleContext.getServiceReference(ConfigurationAdmin.class);
        if (reference != null) {
            ConfigurationAdmin configurationAdmin = bundleContext.getService(reference);
            try {
                // retrieve the configuration
                Configuration configuration = configurationAdmin.getConfiguration("net.nanthrax.example.cfgupdater");
                Dictionary<String, Object> properties = configuration.getProcessedProperties(null);
                properties.put(bundleContext.getBundle().getSymbolicName() + ".uninstall", bundleContext.getBundle().getLocation());
                configuration.update(properties);
            } catch (Exception e) {
                System.err.println("Can not update configuration net.nanthrax.example.cfgupdater");
                e.printStackTrace(System.err);
            }
        }
        bundleContext.ungetService(reference);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        // no op
    }

}
