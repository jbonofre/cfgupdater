package net.nanthrax.example.cfgupdater.bundlea;

import org.osgi.annotation.bundle.Header;
import org.osgi.framework.*;
import org.osgi.service.cm.ManagedService;

import java.util.Enumeration;
import java.util.Hashtable;

@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class Activator implements BundleActivator {

    private ServiceRegistration<ManagedService> registration;

    @Override
    public void start(BundleContext bundleContext) {
        ManagedService managedService = properties -> {
            Enumeration<String> keys = properties.keys();
            while (keys.hasMoreElements()) {
                // key contains id with .install or .uninstall (populated by another bundle, like bundleb)
                String key = keys.nextElement();
                if ((properties.get(key) != null)) {
                    // value is the bundle location
                    String value = (String) properties.get(key);
                    Bundle bundle = bundleContext.getBundle(value);
                    if (key.contains("install")) {
                        if (bundle == null) {
                            System.out.println("Installing bundle " + key + " (" + value + ")");
                            try {
                                bundleContext.installBundle(value);
                            } catch (Exception e) {
                                System.err.println("Can not install bundle " + key + " located " + value);
                                e.printStackTrace(System.err);
                            }
                        }
                    } else {
                        if (bundle != null) {
                            System.out.println("Uninstalling bundle " + key + " (" + value + ")");
                            try {
                                bundle.uninstall();
                            } catch (Exception e) {
                                System.err.println("Can not uninstall bundle " + key + " located " + value);
                                e.printStackTrace(System.err);
                            }
                        }
                    }
                }
            }
        };
        Hashtable<String, String> serviceProperties = new Hashtable<>();
        serviceProperties.put(Constants.SERVICE_PID, "net.nanthrax.example.cfgupdater");
        registration = bundleContext.registerService(ManagedService.class, managedService, serviceProperties);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        if (registration != null) {
            registration.unregister();
        }
    }

}
