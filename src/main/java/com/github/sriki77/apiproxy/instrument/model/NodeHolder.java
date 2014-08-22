package com.github.sriki77.apiproxy.instrument.model;

import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.List;

@FunctionalInterface
public interface NodeHolder {
    void holdNode(Node node);

    static void holdNodes(List<? extends NodeHolder> nodeHolders, Node node) {
        if (nodeHolders != null) {
            nodeHolders.forEach(d -> holdNode(d, node));
        }
    }

    static void holdNode(NodeHolder holder, Node node) {
        if (holder != null) {
            holder.holdNode(node);
        }
    }

    static Node findMyselfUsingXpath(Node node, String path) {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return (Node) xPath.evaluate(path, node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
