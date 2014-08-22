package com.github.sriki77.apiproxy.instrument;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;
import com.github.sriki77.apiproxy.instrument.model.FlowSteps;
import com.github.sriki77.apiproxy.instrument.model.PolicyUpdate;
import com.github.sriki77.apiproxy.instrument.model.Step;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KVMapBasedProxyInstrumenter implements ProxyInstrumeter {

    private final List<Endpoint> endpoints;

    public KVMapBasedProxyInstrumenter(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public List<Endpoint> instrument() {
        endpoints.forEach(this::instrument);
        return endpoints;
    }

    private void instrument(Endpoint e) {
        e.getFaultRules().getFaultRules().forEach(f -> instrument(f, e));
        instrument(e.getPreflow().getRequestFlow(), e);
        instrument(e.getPreflow().getResponseFlow(), e);
        instrument(e.getPostflow().getRequestFlow(), e);
        instrument(e.getPostflow().getResponseFlow(), e);
        e.getFlows().getFlows().forEach(f -> {
            instrument(f.getRequestFlow(), e);
            instrument(f.getResponseFlow(), e);
        });
    }

    private void instrument(FlowSteps f, Endpoint e) {
        new ArrayList<>(f.getSteps()).forEach(s -> instrument(f, s, e));
    }

    private void instrument(FlowSteps f, Step s, Endpoint e) {
        final Step step = f.cloneStep(s);
        try {
            String template = getStepTemplate();
            final String policyData = step.initUsingTemplate(template,step.getName());
            e.addUpdate(new PolicyUpdate(step.getName(), policyData));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private String getStepTemplate() throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream("/kv_instr_template.xml"));

    }
}
