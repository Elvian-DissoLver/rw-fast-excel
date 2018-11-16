package com.fastExcel;

import org.apache.poi.ss.usermodel.DateUtil;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public class rFEWorker implements fastExcel {

    private Set<String> columnNames;

    private static final String FILE = "/simple.xlsx";

    private static final String FILE2 = "/data.xlsx";

    public rFEWorker(Set<String> columnNames) {
        this.columnNames = columnNames;
    }

    private static InputStream openResource(String name) {
        InputStream result = rFESimple.class.getResourceAsStream(name);

        if (result == null) {
            throw new IllegalStateException("Cannot read resource " + name);
        }
        return result;
    }



    @Override
    public Iterator<Map<String, String>> getIterator() {

//        InputStream is = openResource(FILE);
        InputStream is = openResource(FILE2);

        ReadableWorkbook wb = null;
        try {
            wb = new ReadableWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Optional<Sheet> sheet =  wb.findSheet("Sheet1");

        Stream<Row> rows = null;
        Sheet sheet1 = null;
        Row header = null;
        Iterator<Row> rowIt = null;
        List<String> columns = null;

        System.out.print("hello");
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
                    data.add(cell.getValue().toString());
                    break;
                case NUMBER:
//                    if (DateUtil.isCellDateFormatted(cell.getValue().toString())) {
//                        data.add(dateFormat.format(cell.getDateCellValue()));
//                    } else {
//                        data.add(String.valueOf(cell.getNumericCellValue()));
//                    }
//                    data.add(cell.RawValue());
                    break;
                case BOOLEAN:

                    break;
                case FORMULA:

                    break;
                default: data.add("");
            }
        }
        return data;
    }

    @Override
    public void close() throws IOException {

    }
}