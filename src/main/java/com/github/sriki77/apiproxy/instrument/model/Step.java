package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("Step")
public class Step implements LocationProvider {

    @XStreamAlias("Name")
    protected String name;

    @XStreamAlias("Condition")
    protected String condition;

    @XStreamAlias("executed")
    @XStreamAsAttribute
    protected boolean executed = true;

    @XStreamOmitField
    protected String baseName;

    @XStreamOmitField
    protected LocationProvider parent;

    protected Step(String name, String condition, LocationProvider parent) {
        this.name = name;
        this.condition = escapeCondition(condition);
        this.parent = parent;
    }

    private String escapeCondition(String condition) {
        return condition==null? null:condition.replace("<","&lt;").replace(">","&gt;");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }


    public Step duplicate() {
        final Step copy = new Step(name + System.currentTimeMillis(), condition, parent);
        copy.baseName = name;
        return copy;
    }

    @Override
    public void setParent(LocationProvider parent) {
        this.parent = parent;
    }

    @Override
    public String location() {
        String loc = "Policy: " + name;
        if (condition != null) {
            loc += " Condition: " + condition;
        }
        return LocationProvider.append(parent, loc);
    }


    public String policyNameAndCondition() {
        String loc = "Policy: " + baseName;
        if (condition != null) {
            loc += " Condition: " + condition;
        }
        return loc;
    }

    public String initUsingTemplate(String template, String name) {
        return String.format(template, name, LocationProvider.endpointName(this),
                LocationProvider.proxyFileName(this), LocationProvider.flowName(this), policyNameAndCondition());
    }

    public String getName() {
        return name;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public boolean isExecuted() {
        return executed;
    }
}
