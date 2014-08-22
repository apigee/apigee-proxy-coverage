package com.github.sriki77.apiproxy.instrument.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProxyZipFileHandler extends ProxyDirectoryHandler {
    private File zipFile;

    public ProxyZipFileHandler(File zipFile) throws Exception {
        super(expandZipFile(zipFile));
        this.zipFile = zipFile;
    }

    static File expandZipFile(File zipFile) throws IOException {
        final Path tempDirectory = Files.createTempDirectory(zipFile.getName());
        ZipUtil.unpack(zipFile, tempDirectory.toFile());
        return tempDirectory.toFile();
    }

    public File buildInstrumentedZipFile() {
        final File parentDir = zipFile.getParentFile();
        final File targetZipFile = targetZipFile(parentDir);
        FileUtils.deleteQuietly(targetZipFile);
        ZipUtil.pack(proxyDir, targetZipFile);
        return targetZipFile;
    }

    private File targetZipFile(File parentDir) {
        final String origName = zipFile.getName();
        final String newName = FilenameUtils.getBaseName(origName) + "_instr." +
                FilenameUtils.getExtension(origName);
        return new File(parentDir, newName);
    }

    @Override
    public void close() throws IOException {
        super.close();
        File instrZipFile = buildInstrumentedZipFile();
        System.err.println("Instrument File generated: " + instrZipFile);
    }
}
