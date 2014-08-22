package com.github.sriki77.apiproxy.instrument.report;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("EndpointStat")
public class EndpointStat extends Stats {
    protected List<FlowStat> stats = new ArrayList<>();
    public String endpointType;


    @Override
    protected void calcCoverage() {
        stats.forEach(f -> f.calcCoverage());
        super.calcCoverage();
    }


    public void add(FlowStat flowStat) {
        this.stats.add(flowStat);
    }
}
