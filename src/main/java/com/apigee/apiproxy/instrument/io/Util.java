package com.apigee.apiproxy.instrument.io;

import com.apigee.apiproxy.instrument.model.Endpoint;
import com.apigee.apiproxy.instrument.model.FaultRule;
import com.apigee.apiproxy.instrument.model.FaultRules;
import com.apigee.apiproxy.instrument.model.Flow;
import com.apigee.apiproxy.instrument.model.FlowSteps;
import com.apigee.apiproxy.instrument.model.ProxyEndpoint;
import com.apigee.apiproxy.instrument.model.RequestFlow;
import com.apigee.apiproxy.instrument.model.ResponseFlow;
import com.apigee.apiproxy.instrument.model.Step;
import com.apigee.apiproxy.instrument.model.TargetEndpoint;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public final class Util {
    private Util() {
    }

    public static XStream xStreamInit() {
        final XStream stream = new XStream(new StaxDriver());
        stream.allowTypesByWildcard(new String[] { 
                "com.apigee.apiproxy.**"
                });
        stream.processAnnotations(new Class[]{ProxyEndpoint.class,
                Endpoint.class, FaultRule.class,
                FaultRules.class, Flow.class, FlowSteps.class,
                RequestFlow.class, ResponseFlow.class, Step.class,
                TargetEndpoint.class});
        return stream;
    }
}
