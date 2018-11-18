package com.fastExcel;

import java.io.Serializable;
import java.util.Objects;

public class DataSourceConfig implements Serializable {
    public enum Types {
        SQL_PSQL(), SQL_MYSQL(), CSV(), XLS(), XLSX()
    }

    public enum sourceTypes {
        REMOTE(), LOCAL(), MY_FILES()
    }

    private String dataSourceType;
    private String id;
    private String hostName;
    private Integer port;
    private String username;
    private String password;
    private String databaseName;
    private String filePath;
    private String fileUrl;
    private String delimiter;
    private String quoteCharacter;
    private String escapeCharacter;
    private String encoding;
    private String fileSource;
//    private Date createdAt;
//    private Date updatedAt;

    public DataSourceConfig() {

    }

    public DataSourceConfig(String dataSourceType, String id, String hostName, Integer port, String username,
                            String password, String databaseName, String filePath, String fileUrl, String delimiter,
                            String quoteCharacter, String escapeCharacter, String encoding, String fileSource) {
        this.dataSourceType = dataSourceType;
        this.id = id;
        this.hostName = hostName;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;
        this.escapeCharacter = escapeCharacter;
        this.encoding = encoding;
        this.fileSource = fileSource;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setQuoteCharacter(String quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    public String getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(String escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFileSource() {
        return fileSource;
    }

    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }

    @Override
    public String toString() {
        return "DataSourceConfig{" +
                "dataSourceType='" + dataSourceType + '\'' +
                ", id='" + id + '\'' +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", delimiter='" + delimiter + '\'' +
                ", quoteCharacter='" + quoteCharacter + '\'' +
                ", escapeCharacter='" + escapeCharacter + '\'' +
                ", encoding='" + encoding + '\'' +
                ", fileSource='" + fileSource + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSourceConfig that = (DataSourceConfig) o;
        return Objects.equals(dataSourceType, that.dataSourceType) &&
                Objects.equals(id, that.id) &&
                Objects.equals(hostName, that.hostName) &&
                Objects.equals(port, that.port) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(databaseName, that.databaseName) &&
                Objects.equals(filePath, that.filePath) &&
                Objects.equals(fileUrl, that.fileUrl) &&
                Objects.equals(delimiter, that.delimiter) &&
                Objects.equals(quoteCharacter, that.quoteCharacter) &&
                Objects.equals(escapeCharacter, that.escapeCharacter) &&
                Objects.equals(encoding, that.encoding) &&
                Objects.equals(fileSource, that.fileSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSourceType, id, hostName, port, username, password, databaseName, filePath, fileUrl, delimiter, quoteCharacter, escapeCharacter, encoding, fileSource);
    }
}
