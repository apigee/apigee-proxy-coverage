package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;

public interface InstrumentReportGenerator {
    void generateReport(Endpoint endpoint);
}
