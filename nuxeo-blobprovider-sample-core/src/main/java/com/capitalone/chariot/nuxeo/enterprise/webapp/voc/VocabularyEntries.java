/**
 * 
 */

package com.capitalone.chariot.nuxeo.enterprise.webapp.voc;

import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;
import org.osgi.framework.Bundle;


/**
 * @author rkarremolla
 */
public class VocabularyEntries extends DefaultComponent {

    protected Bundle bundle;
    
    public Bundle getBundle() {
        return bundle;
    }
    
    /**
     * Component activated notification. 
     * Called when the component is activated. All component dependencies are resolved at that moment.
     * Use this method to initialize the component. 
     * <p>
     * The default implementation of this method is storing the Bundle owning that component in a class field.
     * You can use the bundle object to lookup for bundle resources:
     * <code>URL url = bundle.getEntry("META-INF/some.resource");</code>, load classes or to interact with OSGi framework.
     * <p> 
     * Note that you must always use the Bundle to lookup for resources in the bundle. Do not use the classloader for this.
     * @param context the component context. Use it to get the current bundle context
     */
    @Override
    public void activate(ComponentContext context) {
        this.bundle = context.getRuntimeContext().getBundle(); 
    }

    /**
     * Component deactivated notification.
     * Called before a component is unregistered.
     * Use this method to do cleanup if any and free any resources held by the component.
     *  
     * @param context the component context. Use it to get the current bundle context
     */
    @Override
    public void deactivate(ComponentContext context) {
        this.bundle = null;
    }

    /**
     * Application started notification.
     * Called after the application started. 
     * You can do here any initialization that requires a working application 
     * (all resolved bundles and components are active at that moment) 
     * 
     * @param context the component context. Use it to get the current bundle context
     */
    @Override
    public void applicationStarted(ComponentContext context) {
        // do nothing by default. You can remove this method if not used.
    }

}
