package com.fastExcel;


import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import io.minio.MinioClient;
import io.minio.errors.*;;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.xmlpull.v1.XmlPullParserException;

import static com.fastExcel.DataSourceConfig.sourceTypes.MY_FILES;
import static com.fastExcel.MinioProperties.*;

public class rFEWorker implements fastExcel {

    @Value("${minio.endpoint}") String minioEndpoint = "http://127.0.0.1:9000";
    @Value("${minio.accessKey}") String minioAccessKey = "FVIG25MUBNDXQVN190H2";
    @Value("${minio.secretKey}") String minioSecretKey = "PSfjY4VTZaJCWMWnf6QnRLu4P0QKJQYEotMPgK9I";
    @Value("${minio.bucket.name}") String minioBucketName = "input";

    private static final Logger log = LoggerFactory.getLogger(rFEWorker.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    private static final String FILE_SIMPLE = "/simple.xlsx";

    private static final String FILE2 = "/data.xlsx";

    private static final String FILE_ERROR = "/ErrorTypes.xlsx";
    private Object LocalDateTime;

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

    public rFEWorker(Map<String, String> minioConfig) {
        this.minioConfig = minioConfig;
    }

    public rFEWorker(String filePath, String fileUrl, String sheetName,
                     Set<String> columnNames, String type, String fileSource) {
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.sheetName = sheetName;
        this.columnNames = columnNames;
        this.type = type;
        this.fileSource = fileSource;
    }

    private static InputStream openResource(String name) {
        InputStream result = rFESimple.class.getResourceAsStream(name);

        if (result == null) {
            throw new IllegalStateException("Cannot read resource " + name);
        }
        return result;
    }


    @Override
    public List<String> getHeader() {
        return null;
    }

    @Override
    public SampleFile getSample() {
        return null;
    }

    @Override
    public Iterator<Map<String, String>> getIterator() {

//        InputStream is = openResource(FILE_SIMPLE);
//        InputStream is = openResource(FILE2);
//        InputStream is = openResource(FILE_ERROR);

        InputStream is = null;

        try {
            is = getInputStream(filePath, fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ReadableWorkbook wb = null;
        try {
            wb = new ReadableWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Optional<Sheet> sheet =  wb.findSheet("Sheet1");
        Optional<Sheet> sheet =  wb.findSheet(this.sheetName);
//        Optional<Sheet> sheet =  wb.findSheet("RawErrors");

        Stream<Row> rows = null;
        Sheet sheet1;
        Row header;
        Iterator<Row> rowIt = null;
        List<String> columns = null;

//        sheet1 = sheet.get();

        if (sheet.isPresent()) {

            sheet1 = sheet.get();
            try {
                rows =  sheet1.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            rowIt = rows.iterator();
            header = rowIt.next();
            columns = mapRow(header);

        }

        Iterator<Row> finalRowIt = rowIt;


        ReadableWorkbook finalWorkbook = wb;

        List<String> finalColumns = columns;

        return new Iterator<Map<String, String>>() {
            @Override
            public boolean hasNext() {
                if (finalRowIt.hasNext()) {
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
              Row row = finalRowIt.next();
                List<String> data = mapRow(row);
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < finalColumns.size(); i++) {
                    if (i < data.size()) {
                        for (String columnName : columnNames) {
                            if (finalColumns.get(i).equals(columnName)) {
                                rowData.put(finalColumns.get(i), data.get(i));
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
        for (int count = 0; count < row.getCellCount(); count++) {
            Cell cell = row.getCell(count);
//            org.apache.poi.
            if (cell == null) {
                data.add("");
                continue;
            }

            switch (cell.getType()) {
                case STRING:
                    data.add(cell.asString());
                    break;
                case NUMBER:
                    if(cell.asDate().toString().substring(0,4).equals("1900")) {
                        data.add(String.valueOf(cell.getRawValue()));
                    }
                    else{
                        data.add(String.valueOf(cell.asDate()));
                    }
//                    data.add(String.valueOf(cell.asNumber()));
                    break;
                case BOOLEAN:
                    data.add(String.valueOf(cell.asBoolean()));
                    break;
                case FORMULA:
                    data.add(String.valueOf(cell.getValue()));
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
        InputStream is = null;

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