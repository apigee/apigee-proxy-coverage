package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.w3c.dom.Node;

@XStreamAlias("Request")
public class RequestFlow extends FlowSteps {

    @XStreamOmitField
    private Node node;

    @Override
    public void holdNode(Node node) {
        this.node = node;
    }

    @Override
    protected Node getDOMNode() {
        return node;
    }

    @Override
    public String location() {
        return parent.location()+":RequestFlow";
    }
}
