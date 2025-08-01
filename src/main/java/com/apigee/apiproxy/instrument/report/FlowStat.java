package com.apigee.apiproxy.instrument.report;

import com.apigee.apiproxy.instrument.model.Step;
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
