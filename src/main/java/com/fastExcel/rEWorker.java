package com.fastExcel;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fastExcel.DataSourceConfig.sourceTypes.MY_FILES;

public class rEWorker implements fastExcel {
    @Value("${minio.endpoint}") String minioEndpoint = "http://127.0.0.1:9000";
    @Value("${minio.accessKey}") String minioAccessKey = "FVIG25MUBNDXQVN190H2";
    @Value("${minio.secretKey}") String minioSecretKey = "PSfjY4VTZaJCWMWnf6QnRLu4P0QKJQYEotMPgK9I";
    @Value("${minio.bucket.name}") String minioBucketName = "input";

    private static final Logger log = LoggerFactory.getLogger(rFEWorker.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");


    private String filePath;
    private String fileUrl;
    private String sheetName;

    // Determine datasource type (XLS or XLSX)
    private String type;

    // Determine the storage of datasource (LOCAL or MY_FILES)
    // LOCAL: datasource stores in HDFS
    // MY_FILES: datasource stores in MINIO
    private String fileSource;

    // Contain column names that will be imported
    private Set<String> columnNames;

    // Contain minio configuration info
    // keys: ENDPOINT, ACCESS_KEY, SECRET_KEY, BUCKET_NAME
    @Autowired
    private Map<String, String> minioConfig;

    public rEWorker(String filePath, String fileUrl, String sheetName,
                     Set<String> columnNames, String type, String fileSource) {
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.sheetName = sheetName;
        this.columnNames = columnNames;
        this.type = type;
        this.fileSource = fileSource;
    }

    @Override
    public Iterator<Map<String, String>> getIterator() {
        log.debug("getIterator sheetName={} type={}", sheetName, type);
        InputStream is = null;
        try {
            is = getInputStream(filePath, fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Workbook workbook = null;
        try {
            if (type.equals(DataSourceConfig.Types.XLS.toString())) {
                log.debug("create HSSFWorkbook");
                workbook = new HSSFWorkbook(is);
            } else {
                log.debug("XSSFWorkbook");
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Sheet sheet = workbook.getSheet(this.sheetName);

        Iterator<Row> iterator = sheet.iterator();

        Row header = iterator.next();
        List<String> columns = mapRow(header);

        Workbook finalWorkbook = workbook;
        return new Iterator<Map<String, String>>() {
            @Override
            public boolean hasNext() {
                if (iterator.hasNext()) {
                    return true;
                } else {
                    try {
                        finalWorkbook.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                }
            }

            @Override
            public Map<String, String> next() {
                Row row = iterator.next();
                List<String> data = mapRow(row);
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < columns.size(); i++) {
                    if (i < data.size()) {
                        for (String columnName : columnNames) {
                            if (columns.get(i).equals(columnName)) {
                                rowData.put(columns.get(i), data.get(i));
                            }
                        }
                    }
                }
                return rowData;
            }
        };
    }

    private List<String> mapRow(Row row) {
        List<String> data = new ArrayList<>();
        for (int count = 0; count < row.getLastCellNum(); count++) {
            Cell cell = row.getCell(count, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

            if (cell == null) {
                data.add("");
                continue;
            }

            switch (cell.getCellTypeEnum()) {
                case STRING:
                    data.add(cell.getRichStringCellValue().getString());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        data.add(dateFormat.format(cell.getDateCellValue()));
                    } else {
                        data.add(String.valueOf(cell.getNumericCellValue()));
                    }
                    break;
                case BOOLEAN:
                    data.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                case FORMULA:
                    switch(cell.getCachedFormulaResultType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            data.add(String.valueOf(cell.getNumericCellValue()));
                            break;
                        case Cell.CELL_TYPE_STRING:
                            data.add(String.valueOf(cell.getRichStringCellValue()));
                            break;
                    }
                    break;
                default: data.add("");
            }
        }
        return data;
    }

    private InputStream getInputStream(String filePath, String fileUrl) throws IOException {
        InputStream is;
        if (fileUrl != null && !fileUrl.isEmpty()) {
            log.debug("fileUrl={}", fileUrl);
            URL url = new URL(fileUrl);
            URLConnection urlc = url.openConnection();
            is = new BOMInputStream(urlc.getInputStream(),
                    ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                    ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE,
                    ByteOrderMark.UTF_32BE);
        } else {
            is = new BOMInputStream(getStorageInputStream(filePath),
                    ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                    ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE,
                    ByteOrderMark.UTF_32BE);
        }
        return is;
    }

    public InputStream getStorageInputStream(String filePath) throws IOException {
        InputStream is;

        if (MY_FILES.toString().equals(fileSource)) {
            MinioClient minioClient;
            try {
                minioClient = new MinioClient(
                        minioEndpoint, minioAccessKey, minioSecretKey);
            } catch (InvalidEndpointException | InvalidPortException e) {
                throw new RuntimeException(e);
            }

            try {
                is = minioClient.getObject(minioBucketName, filePath);
            } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException |
                    InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException |
                    InternalException | InvalidArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Configuration conf = new Configuration(false);
            conf.set("fs.defaultFS", "hdfs://localhost:9000");
//            conf.set("fs.defaultFS", "hdfs://hdfs");
//            conf.set("fs.default.name", conf.get("fs.defaultFS"));
//            conf.set("dfs.nameservices", "hdfs");
//            conf.set("dfs.nameservice.id", "hdfs");
//            conf.set("dfs.ha.namenodes.hdfs", "name-0-node,name-1-node");
//            conf.set("dfs.namenode.rpc-address.hdfs.name-0-node", hdfsNamenodes[0]);
//            conf.set("dfs.namenode.rpc-address.hdfs.name-1-node", hdfsNamenodes[1]);
//            conf.set("dfs.client.failover.proxy.provider.hdfs","org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

            FileSystem fs = FileSystem.get(conf);
            Path path = new Path(filePath);

            is = fs.open(path);
        }

        return is;
    }


    @Override
    public void close() throws IOException {

    }
}
