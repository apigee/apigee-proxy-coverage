package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public final class Util {
    private Util() {
    }

    public static XStream xStreamInit() {
        final XStream stream = new XStream(new StaxDriver());
        stream.processAnnotations(new Class[]{ProxyEndpoint.class,
                Endpoint.class, FaultRule.class,
                FaultRules.class, Flow.class, FlowSteps.class,
                RequestFlow.class, ResponseFlow.class, Step.class,
                TargetEndpoint.class});
        return stream;
    }
}
