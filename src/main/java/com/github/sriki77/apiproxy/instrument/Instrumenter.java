package com.github.sriki77.apiproxy.instrument;


import com.github.sriki77.apiproxy.instrument.io.ProxyFileHandler;
import com.github.sriki77.apiproxy.instrument.io.ProxyZipFileHandler;
import com.github.sriki77.apiproxy.instrument.model.Endpoint;
import com.github.sriki77.apiproxy.instrument.model.FlowSteps;
import com.github.sriki77.apiproxy.instrument.model.Step;
import com.github.sriki77.apiproxy.instrument.report.KVMapInstrumentReportGenerator;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Instrumenter {

    public static final String OPT_HELP = "help";
    public static final String OPT_ZIP = "z";
    public static final String OPT_DIR = "d";
    public static final String OPT_KV = "kv";
    public static final String OPT_REPORT_DIR = "o";
    public static final File DEFAULT_REPORT_DIRECTORY = new File("report");
    private ProxyFileHandler proxyFileHandler;
    private File kvInstrumentFile;
    private boolean generateReport;
    private File reportDirectory;

    public static void main(String... args) throws Exception {
        final Instrumenter instrumenter = new Instrumenter();
        processCommandLineArgs(instrumenter, args);
        instrumenter.doWork();
    }

    private void doWork() throws IOException {
        if (generateReport) {
            writeReport();
        } else {
            instrumetProxy();
        }
    }

    private void instrumetProxy() throws IOException {
        try (ProxyFileHandler proxyFileHandler = this.proxyFileHandler) {
            final List<Endpoint> endpoints = proxyFileHandler.getEndpoints();
            final ProxyInstrumeter proxyInstrumenter = getProxyInstrumenter(endpoints);
            final List<Endpoint> instrumentedEndPoints = proxyInstrumenter.instrument();
            instrumentedEndPoints.forEach(proxyFileHandler::updateEndpoint);
        }
    }

    private void writeReport() throws IOException {
        final List<Endpoint> endpoints = proxyFileHandler.getEndpoints();
        try (final KVMapInstrumentReportGenerator reportGenerator = new KVMapInstrumentReportGenerator(proxyFileHandler.proxyName(),kvInstrumentFile, reportDirectory)) {
            endpoints.forEach(reportGenerator::generateReport);
        }

        System.err.println("Generated report in directory: " + reportDirectory.getAbsolutePath());
    }

    private int policiesCount(List<Endpoint> endpoints) {
        Set<String> policies = new HashSet<>();
        endpoints.forEach(e -> {
            e.getFaultRules().getFaultRules().forEach(f -> policies.addAll(policies(f)));
            policies.addAll(policies(e.getPreflow().getRequestFlow()));
            policies.addAll(policies(e.getPreflow().getResponseFlow()));
            policies.addAll(policies(e.getPostflow().getRequestFlow()));
            policies.addAll(policies(e.getPostflow().getResponseFlow()));
            e.getFlows().getFlows().forEach(f -> {
                policies.addAll(policies(f.getRequestFlow()));
                policies.addAll(policies(f.getResponseFlow()));
            });
        });
        return policies.size();
    }

    private Set<String> policies(FlowSteps f) {
        return f.getSteps().stream().map(Step::getName).collect(Collectors.toSet());
    }


    private static void processCommandLineArgs(Instrumenter instrumenter, String[] args) throws Exception {
        CommandLineParser parser = new BasicParser();
        Options options = buildOptions();
        processOptions(instrumenter, getCommandLine(args, parser, options), options);

    }

    private static CommandLine getCommandLine(String[] args, CommandLineParser parser, Options options) throws ParseException {
        try {
            return parser.parse(options, args);
        } catch (AlreadySelectedException ase) {
            final OptionGroup optionGroup = ase.getOptionGroup();
            System.out.print("Error: Only one of ");
            String separator = "";
            for (Object option : optionGroup.getOptions()) {
                Option opt = (Option) option;
                System.out.print(separator + "-" + opt.getOpt());
                separator = ",";
            }
            System.out.println(" is allowed");
            printHelpAndExit(options);
        }
        return null;
    }

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(new Option(OPT_HELP, "Print this message"));
        final Option zipFileOption = OptionBuilder.withArgName("file")
                .hasArg().withDescription("Proxy Zip File Name")
                .create(OPT_ZIP);
        final Option dirOption = OptionBuilder.withArgName("directory")
                .hasArg().withDescription("Proxy Directory Name")
                .create(OPT_DIR);
        final OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(zipFileOption);
        optionGroup.addOption(dirOption);
        final Option kv = OptionBuilder.withArgName("kvm.json").hasArg()
                .withDescription("Instrument Key Value Map JSON file")
                .create(OPT_KV);
        final Option optReportDir = OptionBuilder.withArgName("report directory").hasArg()
                .withDescription("Directory for report files")
                .create(OPT_REPORT_DIR);
        options.addOptionGroup(optionGroup);
        options.addOption(kv);
        options.addOption(optReportDir);
        return options;
    }

    private static void processOptions(Instrumenter instrumenter, CommandLine cli, Options options) throws Exception {
        processReportDirectory(instrumenter, cli);
        if (cli.hasOption(OPT_KV)) {
            processKVFile(instrumenter, cli);
        }
        if (cli.hasOption(OPT_ZIP)) {
            processZipFile(instrumenter, cli);
            return;
        }
        if (cli.hasOption(OPT_DIR)) {
            processDir(instrumenter, cli);
            return;
        }
        System.err.println("Proxy zip or directory must be specified");
        printHelpAndExit(options);
    }

    private static void processReportDirectory(Instrumenter instrumenter, CommandLine cli) {
        File reportDirectory = DEFAULT_REPORT_DIRECTORY;
        if (cli.hasOption(OPT_REPORT_DIR)) {
            final String value = cli.getOptionValue(OPT_REPORT_DIR);
            reportDirectory = new File(value);
        }
        FileUtils.deleteQuietly(reportDirectory);
        reportDirectory.mkdirs();
        instrumenter.setReportDirectory(reportDirectory);
    }

    private static void processDir(Instrumenter instrumenter, CommandLine cli) throws Exception {
        final String value = cli.getOptionValue(OPT_DIR);
        File file = new File(value);
        if (!file.exists()) {
            System.err.println("Specified proxy directory not found: " + file);
            System.exit(-1);
        }
        if (!file.isDirectory()) {
            System.err.println("Specified value is not directory: " + file);
            System.exit(-1);
        }
        if (!file.canRead() || !file.canWrite()) {
            System.err.println("Specified proxy directory should be readable and writeable: " + file);
            System.exit(-1);
        }
        instrumenter.setProxyDirectory(file);
    }

    private void setProxyDirectory(File file) throws Exception {
        proxyFileHandler = new ProxyZipFileHandler(file);
    }

    private static void processKVFile(Instrumenter instrumenter, CommandLine cli) throws Exception {
        final String value = cli.getOptionValue(OPT_KV);
        File file = new File(value);
        if (!file.exists()) {
            System.err.println("Specified Key Value Map file not found: " + file);
            System.exit(-1);
        }
        if (file.isDirectory()) {
            System.err.println("Specified file is not a plain JSON file: " + file);
            System.exit(-1);
        }
        if (!file.canRead()) {
            System.err.println("Specified Key Value map file should be readable: " + file);
            System.exit(-1);
        }
        instrumenter.setKVFile(file);
    }

    private void setKVFile(File kvfile) {
        this.kvInstrumentFile = kvfile;
        this.generateReport = true;
    }

    private static void processZipFile(Instrumenter instrumenter, CommandLine cli) throws Exception {
        final String value = cli.getOptionValue(OPT_ZIP);
        File file = new File(value);
        if (!file.exists()) {
            System.err.println("Specified proxy file not found: " + file);
            System.exit(-1);
        }
        if (file.isDirectory()) {
            System.err.println("Specified file is not a zip file: " + file);
            System.exit(-1);
        }
        if (!file.canRead() || !file.canWrite()) {
            System.err.println("Specified proxy file should be readable and writeable: " + file);
            System.exit(-1);
        }
        instrumenter.setProxyFile(file);
    }

    private void setProxyFile(File file) throws Exception {
        proxyFileHandler = new ProxyZipFileHandler(file);
    }

    private static void printHelpAndExit(Options options) {
        System.out.println();
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java " + Instrumenter.class.getName(), options);
        System.exit(-1);
    }

    public ProxyInstrumeter getProxyInstrumenter(List<Endpoint> endpoints) {
        return new KVMapBasedProxyInstrumenter(endpoints);
    }

    public void setReportDirectory(File reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

}

