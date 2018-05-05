package com.biffbangpow.faq.config;

/**
 * Configuration for the app.
 * <p/>
 * This object must conform to javabean standard so that it can be loaded by the yaml parser.
 */
public class Configuration {

    private String dbUrl;
    private String dbName;
    private int port;
    private String docRoot;

    public String getDocRoot() {
        return docRoot;
    }

    public void setDocRoot(String docRoot) {
        this.docRoot = docRoot;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
