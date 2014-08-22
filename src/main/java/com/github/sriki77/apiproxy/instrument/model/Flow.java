package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.w3c.dom.Node;

@XStreamAlias("Flow")
public class Flow implements NodeHolder, LocationProvider {

    @XStreamAlias("Request")
    protected RequestFlow requestFlow;

    @XStreamAlias("Response")
    protected ResponseFlow responseFlow;

    @XStreamAlias("Condition")
    protected String condition;

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamOmitField
    protected LocationProvider parent;

    @Override
    public void holdNode(Node node) {
        NodeHolder.holdNode(requestFlow, NodeHolder.findMyselfUsingXpath(node, getReqNodeXPath()));
        NodeHolder.holdNode(responseFlow, NodeHolder.findMyselfUsingXpath(node, getResNodeXPath()));
    }

    protected String getReqNodeXPath() {
        return String.format("//Flow[@name='%s']/Request", name);
    }

    protected String getResNodeXPath() {
        return String.format("//Flow[@name='%s']/Response", name);
    }

    public RequestFlow getRequestFlow() {
        return requestFlow == null ? new RequestFlow() : requestFlow;
    }

    public ResponseFlow getResponseFlow() {
        return responseFlow == null ? new ResponseFlow() : responseFlow;
    }

    @Override
    public void setParent(LocationProvider parent) {
        this.parent = parent;
        LocationProvider.setParent(requestFlow, this);
        LocationProvider.setParent(responseFlow, this);
    }

    @Override
    public String location() {
        return LocationProvider.append(parent, name);
    }

    public String getName() {
        return name;
    }
}
