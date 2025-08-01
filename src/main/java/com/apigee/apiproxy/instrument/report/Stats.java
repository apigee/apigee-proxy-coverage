package com.apigee.apiproxy.instrument.report;

import java.util.List;

import com.apigee.apiproxy.instrument.model.Step;

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
