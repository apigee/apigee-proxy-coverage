package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TargetEndpoint")
public class TargetEndpoint extends Endpoint {

    @Override
    public String endpointType() {
        return "Target";
    }
}
