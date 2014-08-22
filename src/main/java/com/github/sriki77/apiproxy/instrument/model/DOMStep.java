package com.github.sriki77.apiproxy.instrument.model;

import org.w3c.dom.Node;

import java.util.Optional;

public class DOMStep extends Step {

    private Step inner;
    private Optional<Node> nameNode;
    private Optional<Node> condNode;

    public DOMStep(Step inner, Optional<Node> nameNode, Optional<Node> condNode) {
        super(inner.name,inner.condition,inner.parent);
        this.inner = inner;
        this.nameNode = nameNode;
        this.condNode = condNode;
        this.baseName= inner.baseName;
        setName(inner.name);
        setCondition(inner.condition);
    }


    @Override
    public void setName(String name) {
        if(name==null){
            return;
        }
        validateNodePresence(nameNode);
        super.setName(name);
        inner.setName(name);
        updateNode(nameNode, name);

    }

    private void validateNodePresence(Optional<Node> node) {
        if (!node.isPresent()) {
            throw new RuntimeException("Name/Condition node not present for original step. You cannot add a new one to the clone.");
        }
    }

    private void updateNode(Optional<Node> node, String value) {
        if (node.isPresent()) {
            node.get().setTextContent(value);
        }
    }

    @Override
    public void setCondition(String condition) {
        if(condition==null){
            return;
        }
        validateNodePresence(condNode);
        super.setCondition(condition);
        inner.setCondition(condition);
        updateNode(condNode, condition);
    }

    @Override
    public Step duplicate() {
        throw new RuntimeException("Duplication of DOM Step not supported");
    }
}
