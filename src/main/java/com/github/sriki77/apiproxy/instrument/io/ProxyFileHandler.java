package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;

import java.io.Closeable;

public interface ProxyFileHandler extends Closeable {

    java.util.List<Endpoint> getEndpoints();

    void updateEndpoint(Endpoint endpoint);

    String proxyName();
}
