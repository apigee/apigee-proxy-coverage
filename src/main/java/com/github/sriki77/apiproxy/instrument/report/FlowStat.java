package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.model.Step;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("FlowStat")
public class FlowStat extends Stats {
    public String flowType;

    protected FlowStat(String flowType,String name, List<Step> steps) {
        this.flowType = flowType;
        this.name = name;
        updateStats(steps);
    }

}
