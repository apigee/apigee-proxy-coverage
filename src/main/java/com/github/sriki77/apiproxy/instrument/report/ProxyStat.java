package com.github.sriki77.apiproxy.instrument.report;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("ProxyStat")
public class ProxyStat extends Stats {

    protected List<EndpointStat> stats = new ArrayList<>();

    @Override
    protected void calcCoverage() {
        stats.forEach(e -> e.calcCoverage());
        super.calcCoverage();
    }

    public void add(EndpointStat endpointStat) {
        this.stats.add(endpointStat);
    }
}
