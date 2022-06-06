package gg.tracer.commons;

import gg.tracer.commons.menu.WorkerMenu;
import gg.tracer.commons.plugin.TracerPlugin;

/**
 * @author Bradley Steele
 */
public class TracerCommons extends TracerPlugin {

    @Override
    public void enable() {
        register(WorkerMenu.class);
    }
}
