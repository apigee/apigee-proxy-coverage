package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.w3c.dom.Node;

@XStreamAlias("FaultRule")
public class FaultRule extends FlowSteps {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamOmitField
    private Node node;

    @Override
    public void holdNode(Node node) {
        this.node = NodeHolder.findMyselfUsingXpath(node, String.format("//FaultRule[@name='%s']", name));
    }


    @Override
    protected Node getDOMNode() {
        return node;
    }

    @Override
    public String location() {
        return LocationProvider.append(parent, "FaultRule: " + name);
    }

    public String getName() {
        return name;
    }
}
