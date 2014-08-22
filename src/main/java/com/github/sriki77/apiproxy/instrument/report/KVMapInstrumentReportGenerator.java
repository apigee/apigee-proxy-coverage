package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.io.Util;
import com.github.sriki77.apiproxy.instrument.model.Endpoint;
import com.github.sriki77.apiproxy.instrument.model.FaultRule;
import com.github.sriki77.apiproxy.instrument.model.Flow;
import com.github.sriki77.apiproxy.instrument.model.Step;
import com.jayway.jsonpath.JsonPath;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KVMapInstrumentReportGenerator implements InstrumentReportGenerator, Closeable {

    private static List<Step> overallSteps = new ArrayList<>();
    private static ProxyStat proxyStat = new ProxyStat();

    private String proxyName;
    private final File kvInstrumentFile;
    private final File reportDirectory;
    private List<String> instrumentEntries;
    private File summaryXMLFile;
    ;

    public KVMapInstrumentReportGenerator(String proxyName, File kvInstrumentFile, File reportDirectory) throws IOException {
        this.proxyName = proxyName;
        this.kvInstrumentFile = kvInstrumentFile;
        this.reportDirectory = reportDirectory;
        summaryXMLFile = new File(reportDirectory, "summary.xml");
        instrumentEntries = cleanUpEntries(JsonPath.read(this.kvInstrumentFile, "$.entry[*].value"));
    }

    private List<String> cleanUpEntries(List<String> entries) {
        return entries.stream().map(e -> e.replace(" ", "")).collect(Collectors.toList());
    }

    @Override
    public void generateReport(Endpoint e) {
        final List<Step> allSteps = getAllSteps(e);
        allSteps.forEach(this::updateStep);
        updateStats(e, allSteps);
        writeToDisk(e);
    }

    private void updateStats(Endpoint e, List<Step> allSteps) {
        overallSteps.addAll(allSteps);
        EndpointStat endpointStat = new EndpointStat();
        endpointStat.name = e.getName();
        endpointStat.endpointType = e.endpointType();

        updateStats(allSteps, endpointStat);
        proxyStat.add(endpointStat);
        updateFlowStats(e, endpointStat);
    }


    private void writeToDisk(Endpoint e) {
        final XStream xStream = Util.xStreamInit();
        try {
            final File outFile = new File(reportDirectory, e.endpointType() + "_" + e.getXmlFile().getName());
            final PrettyPrintWriter writer = new PrettyPrintWriter(new FileWriter(outFile));
            xStream.marshal(e, writer);
            writer.close();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void updateStats(List<Step> steps, Stats stats) {
        Set<String> uniquePolicies = new HashSet<>();
        Set<String> executedPolicies = new HashSet<>();
        steps.forEach(s -> {
            uniquePolicies.add(s.getName());
            if (s.isExecuted()) {
                executedPolicies.add(s.getName());
            }
        });
        stats.totalPolicies = uniquePolicies.size();
        stats.executedPolicies = executedPolicies.size();
    }

    private void updateStep(Step step) {
        final String location = step.location();
        step.setExecuted(instrumentEntries.contains(location.replace(" ", "")));
    }

    private List<Step> getAllSteps(Endpoint e) {
        List<Step> steps = new ArrayList<>();
        e.getFaultRules().getFaultRules().forEach(f -> steps.addAll(f.getSteps()));
        steps.addAll(e.getPreflow().getRequestFlow().getSteps());
        steps.addAll(e.getPreflow().getResponseFlow().getSteps());
        steps.addAll(e.getPostflow().getRequestFlow().getSteps());
        steps.addAll(e.getPostflow().getResponseFlow().getSteps());
        e.getFlows().getFlows().forEach(f -> {
            steps.addAll(f.getRequestFlow().getSteps());
            steps.addAll(f.getResponseFlow().getSteps());
        });
        return steps;
    }

    private void updateFlowStats(Endpoint e, EndpointStat stats) {
        updateFaultFlowStats(e, stats);
        updatePreFlowStats(e, stats);
        updatePostFlowStats(e, stats);
        for (Flow flow : e.getFlows().getFlows()) {
            List<Step> steps = new ArrayList<>();
            steps.addAll(flow.getRequestFlow().getSteps());
            steps.addAll(flow.getResponseFlow().getSteps());
            stats.add(new FlowStat("Flow", flow.getName(), steps));
        }
    }

    private void updatePostFlowStats(Endpoint e, EndpointStat stats) {
        List<Step> steps = new ArrayList<>();
        steps.addAll(e.getPostflow().getRequestFlow().getSteps());
        steps.addAll(e.getPostflow().getResponseFlow().getSteps());
        stats.add(new FlowStat("Post Flow", "", steps));
    }

    private void updatePreFlowStats(Endpoint e, EndpointStat stats) {
        List<Step> steps = new ArrayList<>();
        steps.addAll(e.getPreflow().getRequestFlow().getSteps());
        steps.addAll(e.getPreflow().getResponseFlow().getSteps());
        stats.add(new FlowStat("Pre Flow", "", steps));
    }

    private void updateFaultFlowStats(Endpoint e, EndpointStat stats) {
        for (FaultRule faultRule : e.getFaultRules().getFaultRules()) {
            stats.add(new FlowStat("Fault Rule", faultRule.getName(), faultRule.getSteps()));
        }

    }

    @Override
    public void close() throws IOException {
        proxyStat.name = proxyName;
        updateStats(overallSteps, proxyStat);
        proxyStat.calcCoverage();
        final XStream xStream = new XStream();
        xStream.processAnnotations(new Class[]{EndpointStat.class,
                FlowStat.class, ProxyStat.class});
        final PrettyPrintWriter writer = new PrettyPrintWriter(new FileWriter(summaryXMLFile));
        xStream.marshal(proxyStat, writer);
        writer.close();
        generateHTMLReport();
    }

    private void generateHTMLReport() {
        generateSummary();
        generateProxyHtml();
        copyBootStrapCSS();
    }

    private void copyBootStrapCSS() {
        try {
            final FileWriter destFile = new FileWriter(new File(reportDirectory, "bootstrap.min.css"));
            IOUtils.copy(this.getClass().getResourceAsStream("/report/bootstrap.min.css"), destFile);
            destFile.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateProxyHtml() {
        try {
            final Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(this.getClass()
                            .getResourceAsStream("/report/proxy.xsl")));
            final File[] proxyFiles = reportDirectory.listFiles((dir, name) -> name.startsWith("Proxy_") || name.startsWith("Target_"));
            for (File proxyFile : proxyFiles) {
                transformer.transform(new StreamSource(new FileReader(proxyFile)),
                        new StreamResult(new FileWriter(new File(reportDirectory,
                                FilenameUtils.getBaseName(proxyFile.getName()) + ".html"))));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void generateSummary() {
        try {
            final Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(this.getClass()
                            .getResourceAsStream("/report/summary.xsl")));
            transformer.transform(new StreamSource(new FileReader(summaryXMLFile)),
                    new StreamResult(new FileWriter(new File(reportDirectory, "summary.html"))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
