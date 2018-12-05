package com.fastExcel;

import java.util.List;
import java.util.Map;

public class SampleFile {
    private Map<String, List<String>> header;
    private Map<String, List<Map<String, String>>> data;

    public SampleFile(Map<String, List<String>> header, Map<String, List<Map<String, String>>> data) {
        this.header = header;
        this.data = data;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public void setHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public Map<String, List<Map<String, String>>> getData() {
        return data;
    }

    public void setData(Map<String, List<Map<String, String>>> data) {
        this.data = data;
    }
}
