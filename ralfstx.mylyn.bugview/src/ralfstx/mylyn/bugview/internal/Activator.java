package ralfstx.mylyn.bugview.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "ralfstx.mylyn.bugview"; //$NON-NLS-1$
  private static Activator plugin;

  public void start( BundleContext context ) throws Exception {
    super.start( context );
    plugin = this;
  }

  public void stop( BundleContext context ) throws Exception {
    plugin = null;
    super.stop( context );
  }

  public static Activator getDefault() {
    return plugin;
  }

}
