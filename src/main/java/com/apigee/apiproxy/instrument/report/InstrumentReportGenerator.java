package com.apigee.apiproxy.instrument.report;

import com.apigee.apiproxy.instrument.model.Endpoint;

public interface InstrumentReportGenerator {
    void generateReport(Endpoint endpoint);
}
