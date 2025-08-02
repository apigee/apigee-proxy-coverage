package com.apigee.apiproxy.instrument.io;

import java.io.Closeable;

import com.apigee.apiproxy.instrument.model.Endpoint;

public interface ProxyFileHandler extends Closeable {

    java.util.List<Endpoint> getEndpoints();

    void updateEndpoint(Endpoint endpoint);

    String proxyName();
}
