package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class FlowSteps implements NodeHolder, LocationProvider {

    @XStreamImplicit(itemFieldName = "Step")
    protected List<Step> steps;

    @XStreamOmitField
    protected LocationProvider parent;

    @Override
    public String toString() {
        return "FlowSteps{" +
                "steps=" + steps +
                '}';
    }

    public List<Step> getSteps() {
        return steps == null ? Collections.emptyList() : Collections.unmodifiableList(steps);
    }

    public Step cloneStep(Step step) {
        final int i = steps.indexOf(step);
        if (i == -1) {
            throw new RuntimeException("Step not found: " + step);
        }
        Step copy = step.duplicate();
        steps.add(i, copy);
        final List<Node> nodes = updateNode(i);
        return new DOMStep(copy, getNameNode(nodes), getCondNode(nodes));
    }

    protected List<Node> updateNode(int index) {
        final Node domNode = getDOMNode();
        NodeList childNodes = domNode.getChildNodes();
        final Node origNode = getNthStep(childNodes, index);
        final Node cloneNode = origNode.cloneNode(true);
        final List<Node> nodes = usefulNodes(cloneNode);
        if (nodes.size() < 1 || nodes.size() > 2) {
            throw new RuntimeException("Step nodes with only Name and Condition elements are supported");
        }
        domNode.insertBefore(cloneNode, origNode);
        return nodes;
    }

    private List<Node> usefulNodes(Node cloneNode) {
        List<Node> usefulNodes = new ArrayList<>();
        final NodeList childNodes = cloneNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            if (item.getNodeName().equalsIgnoreCase("Name")
                    || item.getNodeName().equalsIgnoreCase("Condition")) {
                usefulNodes.add(item);
            }
        }
        return usefulNodes;
    }

    private Optional<Node> getNameNode(List<Node> nodes) {
        return nodes.stream().filter(n -> n.getNodeName().equalsIgnoreCase("Name")).findFirst();
    }

    private Optional<Node> getCondNode(List<Node> nodes) {
        return nodes.stream().filter(n -> n.getNodeName().equalsIgnoreCase("Condition")).findFirst();
    }


    private Node getNthStep(NodeList childNodes, int index) {
        for (int i = 0, j = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            if (item.getNodeName().equalsIgnoreCase("Step")) {
                if (index == j) {
                    return item;
                }
                ++j;
            }
        }
        throw new RuntimeException(index + "th step node not found");
    }


    @Override
    public void setParent(LocationProvider parent) {
        this.parent = parent;
        LocationProvider.setParent(steps, this);
    }

    protected abstract Node getDOMNode();


}
