/**
 * 
 */
package org.sadiframework.generator.perl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.jdesktop.swingworker.SwingWorker;
import org.sadiframework.editor.views.SADIPreferencePanel;
import org.sadiframework.preferences.PreferenceManager;
import org.sadiframework.properties.SADIProperties;

/**
 * @author Eddie
 * 
 */
public class ServiceGeneratorPerlWorker extends SwingWorker<String, Object> {

    private PreferenceManager manager = PreferenceManager.newInstance();

    public ServiceGeneratorPerlWorker() {
        // listen for changes to the DO_SERVICE_GENERATION preference
        manager.addPropertyChangeListener(SADIProperties.DO_PERL_SERVICE_GENERATION,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        // cancel our task
                        if (!manager.getBooleanPreference(
                                SADIProperties.DO_PERL_SERVICE_GENERATION, false)) {
                            if (!isCancelled() || !isDone()) {
                                cancel(true);
                            }
                        }
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingworker.SwingWorker#doInBackground()
     */
    // PRE: all applicable fields needed are correct (i.e. name, definition file exists, etc)
    // POST: unless cancelled, this will 
    protected String doInBackground() throws Exception {
        String perl = manager.getPreference(SADIPreferencePanel.PERL_PATH, "");
        String libs = manager.getPreference(SADIPreferencePanel.PERL_5LIB_DIR, "");
        String scriptDir = manager.getPreference(SADIPreferencePanel.PERL_SADI_SCRIPTS_DIR, "");
        boolean isAsync = manager
                .getBooleanPreference(SADIProperties.GENERATOR_SERVICE_ASYNC, true);
        String name = manager.getPreference(SADIProperties.GENERATOR_SERVICE_NAME, "");
        Generator gen = new Generator(perl, libs, scriptDir);
        String str = "";
        try {
            str = gen.generateService(name, isAsync);
        } catch (IOException ioe) {
            manager.saveBooleanPreference(SADIProperties.DO_PERL_SERVICE_GENERATION, false);
        } catch (InterruptedException ie) {
            manager.saveBooleanPreference(SADIProperties.DO_PERL_SERVICE_GENERATION, false);
        }
        return str;
    }

    protected void done() {
        super.done();
        manager.saveBooleanPreference(SADIProperties.DO_PERL_SERVICE_GENERATION, false);
    }

}