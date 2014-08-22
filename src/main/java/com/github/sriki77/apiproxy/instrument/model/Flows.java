package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;

@XStreamAlias("Flows")
public class Flows implements NodeHolder, LocationProvider {

    @XStreamImplicit(itemFieldName = "Flow")
    protected List<Flow> flows ;

    @Override
    public void holdNode(Node node) {
        NodeHolder.holdNodes(flows, node);
    }


    @Override
    public void setParent(LocationProvider parent) {
        LocationProvider.setParent(flows, parent);
    }

    public List<Flow> getFlows() {
        return flows == null ? Collections.emptyList() : Collections.unmodifiableList(flows);
    }
}
