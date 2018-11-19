package com.fastExcel;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

import static com.fastExcel.DataSourceConfig.sourceTypes.MY_FILES;


public class main {

    @Autowired
    private rFEWorker rFEW;

    public static void main(String[] args) {
//        rFESimple rFES = new rFESimple();
////        rFES.testSimple1Cell();
//        rFES.testSimpleMoreCell();



        Set<String> columnNames = new HashSet<String>(Arrays.asList("id","name","date","room"));
//        Set<String> columnNames = new HashSet<String>(Arrays.asList("nama","nim","umur"));
//        Set<String> columnNames = new HashSet<String>(Arrays.asList("Error","Bang","Que","Non","Val","ErrType","Type","ISERR","ISNA","ErrTypeIsErr"));
//        Set<String> columnNames = new HashSet<String>(Arrays.asList("value","yyyymmdd","B1yyyymmdd","B2yyyymmdd","bbbb yyyymmdd","bb bbbb yy yyyy","B1 bbbb eeee yyyy","B2 bbbb eeee yyyy"));

        rFEWorker rFEW = new rFEWorker(
                "/simple.xlsx",null,"Feuil1",columnNames,"XLSX",
                MY_FILES.toString());

//        rFEWorker rFEW = new rFEWorker(
//                "/input/simple.xlsx",null,"Feuil1",columnNames,"XLSX",
//                null);

//        rFEWorker rFEW = new rFEWorker(
//                "/calendar_stress_test.xlsx",null,"cal",columnNames,"XLSX",
//                MY_FILES.toString());

//        rEWorker rEW = new rEWorker(
//                "/calendar_stress_test.xlsx",null,"cal",columnNames,"XLSX",
//                MY_FILES.toString());

        Iterator<Map<String, String>> iterable = rFEW.getIterator();


        iterable.forEachRemaining(iter ->  {
            iter.keySet().forEach(key -> {
                System.out.println(key);
                System.out.println(iter.get(key));
            });
        } );
    }
}
