package com.github.sriki77.apiproxy.instrument.io;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum NodeCleaner {

    PROXY_ENDPOINT_NODES("ProxyEndpoint", "FaultRules", "PreFlow", "PostFlow", "Flows"),
    TARGET_ENDPOINT_NODES("TargetEndpoint", "FaultRules", "PreFlow", "PostFlow", "Flows"),
    FLOWS_NODES("Flows", "Flow"),
    FLOW_NODES("Flow", "Request", "Response", "Condition"),
    REQ_NODES("Request", "Step"),
    RES_NODES("Response", "Step"),
    STEP_NODES("Step", "Name", "Condition"),
    FAULT_NODES("FaultRules", "FaultRule"),
    FAULT_RULE_NODES("FaultRule", "Step");

    private final List<String> knownNodes;
    private final String name;

    private NodeCleaner(String name, String... knownNodes) {
        this.name = name;
        this.knownNodes = Arrays.asList(knownNodes);
    }

    public void cleanNode(Node node) {
        final NodeList topChildren = node.getChildNodes();
        List<Node> nodesToBeRemoved = new ArrayList<>();
        for (int i = 0; i < topChildren.getLength(); i++) {
            final Node n = topChildren.item(i);
            if (!knownNodes.contains(n.getNodeName())) {
                nodesToBeRemoved.add(n);
            }
        }
        nodesToBeRemoved.stream().forEach(node::removeChild);
    }

    private static NodeCleaner toCleaner(Node node) {
        final NodeCleaner[] nodeCleaners = values();
        for (int i = 0; i < nodeCleaners.length; i++) {
            NodeCleaner nodeCleaner = nodeCleaners[i];
            if (nodeCleaner.name.equals(node.getNodeName())) {
                return nodeCleaner;
            }
        }
        return null;
    }

    public static void clean(Node n) {
        final NodeCleaner nodeCleaner = toCleaner(n);
        if (nodeCleaner != null) {
            nodeCleaner.cleanNode(n);
        }
    }
}
