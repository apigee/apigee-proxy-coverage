package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Endpoint implements NodeHolder, LocationProvider {

    @XStreamAlias("FaultRules")
    protected FaultRules faultRules;

    @XStreamAlias("PreFlow")
    protected PreFlow preflow;

    @XStreamAlias("Flows")
    protected Flows flows;

    @XStreamAlias("PostFlow")
    protected PostFlow postflow;

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;


    @XStreamOmitField
    private File xmlFile;

    @XStreamOmitField
    private Node node;

    @XStreamOmitField
    private List<PolicyUpdate> updates;

    public File getXmlFile() {
        return xmlFile;
    }

    @Override
    public void holdNode(Node node) {
        this.node = node;
        NodeHolder.holdNode(faultRules, NodeHolder.findMyselfUsingXpath(node, "//FaultRules"));
        NodeHolder.holdNode(preflow, NodeHolder.findMyselfUsingXpath(node, "//PreFlow"));
        NodeHolder.holdNode(postflow, NodeHolder.findMyselfUsingXpath(node, "//PostFlow"));
        NodeHolder.holdNode(flows, NodeHolder.findMyselfUsingXpath(node, "//Flows"));
    }

    public FaultRules getFaultRules() {
        return faultRules == null ? new FaultRules() : faultRules;
    }

    public PreFlow getPreflow() {
        return preflow == null ? new PreFlow() : preflow;
    }

    public Flows getFlows() {
        return flows == null ? new Flows() : flows;
    }

    public PostFlow getPostflow() {
        return postflow == null ? new PostFlow() : postflow;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String location() {
        return endpointType() + ":" + name + ",File:" + xmlFile.getName();
    }


    public abstract String endpointType();

    public void init(File xmlFile, Document node) {
        this.xmlFile = xmlFile;
        holdNode(node);
        setParent(this);
    }

    @Override
    public void setParent(LocationProvider parent) {
        LocationProvider.setParent(faultRules, parent);
        LocationProvider.setParent(preflow, parent);
        LocationProvider.setParent(postflow, parent);
        LocationProvider.setParent(flows, parent);
    }

    public void addUpdate(PolicyUpdate update) {
        if (updates == null) {
            this.updates = new ArrayList<>();
        }
        this.updates.add(update);
    }

    public List<PolicyUpdate> updates() {
        return updates == null ? new ArrayList<>() : updates;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }
}
