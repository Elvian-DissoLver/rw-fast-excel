package com.fastExcel;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.dhatim.fastexcel.reader.ReadableWorkbook;

import org.dhatim.fastexcel.reader.*;

import static org.assertj.core.api.Assertions.assertThat;

public class rFESimple {
    private static final String FILE = "/simple.xlsx";

    private static final Object[][] VALUES = {
            {1, "Lorem", date(2018, 1, 1)},
            {2, "ipsum", date(2018, 1, 2)},
            {3, "dolor", date(2018, 1, 3)},
            {4, "sit", date(2018, 1, 4)},
            {5, "amet", date(2018, 1, 5)},
            {6, "consectetur", date(2018, 1, 6)},
            {7, "adipiscing", date(2018, 1, 7)},
            {8, "elit", date(2018, 1, 8)},
            {9, "Ut", date(2018, 1, 9)},
            {10, "nec", date(2018, 1, 10)},
    };

    private static LocalDateTime date(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    public void testSimple1Cell()  {

        ArrayList<Cell> value = new ArrayList<>();
        InputStream is = openResource(FILE);

        ReadableWorkbook wb = null;
        try {
            wb = new ReadableWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = wb.getFirstSheet();

        Stream<Row> rows = null;
        try {
            rows = sheet.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rows.forEach(r -> {
            Cell cell = r.getCell(0);
            value.add(cell);
            BigDecimal num = r.getCellAsNumber(0).orElse(null);
            String str = r.getCellAsString(1).orElse(null);
            LocalDateTime date = r.getCellAsDate(2).orElse(null);

            Object[] values = VALUES[r.getRowNum() - 1];
            assertThat(num).isEqualTo(BigDecimal.valueOf((Integer) values[0]));
            assertThat(str).isEqualTo((String) values[1]);
            assertThat(date).isEqualTo((LocalDateTime) values[2]);
        });

        System.out.print(value.toString());

    }

    public void testSimpleMoreCell()  {

        ArrayList<String> value = new ArrayList<>();
        ArrayList<String> value2 = new ArrayList<>();

        InputStream is = openResource(FILE);

        ReadableWorkbook wb = null;
        try {
            wb = new ReadableWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = wb.getFirstSheet();

        Stream<Row> rows = null;
        try {
            rows = sheet.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AtomicBoolean firstLine = new AtomicBoolean(true);
        rows.forEach(r -> {
            if(firstLine.get()){
                firstLine.set(false);
            }
            else {
                BigDecimal num = r.getCellAsNumber(0).orElse(null);
                value.add(String.valueOf(num));
                String str = r.getCellAsString(1).orElse(null);
                value.add(str);
                LocalDateTime date = r.getCellAsDate(2).orElse(null);
                value.add(String.valueOf(date));
            }
        });

        System.out.print(value.toString());

    }

    private static InputStream openResource(String name) {
        InputStream result = rFESimple.class.getResourceAsStream(name);

        if (result == null) {
            throw new IllegalStateException("Cannot read resource " + name);
        }
        return result;
    }
}
