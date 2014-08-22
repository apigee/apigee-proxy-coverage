package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PostFlow")
public class PostFlow extends Flow {
    @Override
    protected String getReqNodeXPath() {
        return "//PostFlow/Request";
    }

    @Override
    protected String getResNodeXPath() {
        return "//PostFlow/Response";
    }

    @Override
    public String location() {
        return LocationProvider.append(parent, "PostFlow");
    }
}
