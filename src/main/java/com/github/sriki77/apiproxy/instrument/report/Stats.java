package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.model.Step;

import java.util.List;

public class Stats {
    protected String name;

    protected long coverage;

    protected int totalPolicies;

    protected int executedPolicies;

    protected void calcCoverage() {
        if (totalPolicies == 0) {
            return;
        }
        coverage = Math.round(executedPolicies * 100.0 / totalPolicies);

    }

    protected void updateStats(List<Step> steps) {
        totalPolicies = steps.size();
        steps.forEach(s -> {
            if (s.isExecuted()) ++executedPolicies;
        });
    }
}
