package com.apigee.apiproxy.instrument;

import com.apigee.apiproxy.instrument.model.Endpoint;

public interface ProxyInstrumeter {
    java.util.List<Endpoint> instrument();
}
