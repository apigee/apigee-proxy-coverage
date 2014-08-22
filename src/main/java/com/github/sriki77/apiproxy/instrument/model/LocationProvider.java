package com.github.sriki77.apiproxy.instrument.model;

import java.util.List;

import static com.github.sriki77.apiproxy.instrument.model.LocationProvider.LOC_PARTS.*;


public interface LocationProvider {

    default String location(){
        return "";
    }

    void setParent(LocationProvider parent);

    enum LOC_PARTS {ENDPOINT, PROXY, FLOW, POLICY}

    static String proxyFileName(LocationProvider provider) {
        final String[] splits = provider.location().split(",");
        return splits[PROXY.ordinal()];
    }

    static String endpointName(LocationProvider provider) {
        final String[] splits = provider.location().split(",");
        return splits[ENDPOINT.ordinal()];

    }

    static String flowName(LocationProvider provider) {
        final String[] splits = provider.location().split(",");
        return splits[FLOW.ordinal()];

    }

    static String policyName(LocationProvider provider) {
        final String[] splits = provider.location().split(",");
        return splits[POLICY.ordinal()];

    }

    static String append(LocationProvider parent, String loc) {
        return parent.location() + "," + loc;
    }

    static void setParent(List<? extends LocationProvider> providers, LocationProvider parent) {
        if (providers != null) {
            providers.forEach(p -> setParent(p, parent));
        }
    }

    static void setParent(LocationProvider provider, LocationProvider parent) {
        if (provider != null) {
            provider.setParent(parent);
        }
    }

}
