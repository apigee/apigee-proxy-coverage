package com.github.sriki77.apiproxy.instrument.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;

@XStreamAlias("FaultRules")
public class FaultRules implements NodeHolder, LocationProvider {

    @XStreamImplicit(itemFieldName = "FaultRule")
    private List<FaultRule> faultRules;

    @Override
    public void holdNode(Node node) {
        NodeHolder.holdNodes(faultRules, node);
    }

    public List<FaultRule> getFaultRules() {
        return faultRules == null ? Collections.emptyList() : Collections.unmodifiableList(faultRules);
    }


    @Override
    public void setParent(LocationProvider parent) {
        LocationProvider.setParent(faultRules, parent);
    }
}
