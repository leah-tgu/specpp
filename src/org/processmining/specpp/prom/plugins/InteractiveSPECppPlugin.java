package org.processmining.specpp.prom.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.*;
import org.processmining.log.utils.XUtils;

@Plugin(name = "0Interactive SPECpp Plugin", parameterLabels = {"Event Log"}, level = PluginLevel.NightlyBuild, quality = PluginQuality.Poor, icon = "specpp_icon.png", returnLabels = {"Interactive SPECpp"}, returnTypes = {SPECppSession.class}, help = SPECppPlugin.HELP, categories = {PluginCategory.Discovery}, keywords = {"eST", "model discovery"})
public class InteractiveSPECppPlugin {


    @UITopiaVariant(affiliation = "PADS RWTH Aachen University", author = "Leah Tacke genannt Unterberg", email = "leah.tgu@pads.rwth-aachen.de")
    @PluginVariant(variantLabel = "Interactive SPECpp", requiredParameterLabels = {0})
    public SPECppSession run(UIPluginContext context, XLog log) {
        context.getFutureResult(0).setLabel("Interactive SPECpp on " + XUtils.getConceptName(log));
        return new SPECppSession(log);
    }

}
