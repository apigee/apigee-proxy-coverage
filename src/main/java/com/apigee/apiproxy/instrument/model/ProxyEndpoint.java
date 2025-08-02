package com.apigee.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ProxyEndpoint")
public class ProxyEndpoint extends Endpoint {

    @Override
    public String endpointType() {
        return "Proxy";
    }
}
