package com.github.sriki77.apiproxy.instrument;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;

public interface ProxyInstrumeter {
    java.util.List<Endpoint> instrument();
}
