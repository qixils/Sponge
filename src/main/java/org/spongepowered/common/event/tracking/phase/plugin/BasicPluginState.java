package org.spongepowered.common.event.tracking.phase.plugin;

public class BasicPluginState extends PluginPhaseState<BasicPluginContext> {
    @Override
    public BasicPluginContext createPhaseContext() {
        return new BasicPluginContext(this);
    }

    @Override
    public void unwind(BasicPluginContext phaseContext) {

    }
}
