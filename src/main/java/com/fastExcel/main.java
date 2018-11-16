package com.fastExcel;

import java.io.IOException;
import java.util.*;


public class main {
    public static void main(String[] args) {
//        rFESimple rFES = new rFESimple();
////        rFES.testSimple1Cell();
//        rFES.testSimpleMoreCell();



//        Set<String> columnNames = new HashSet<String>(Arrays.asList("id","name","date","room"));
        Set<String> columnNames = new HashSet<String>(Arrays.asList("nama","nim","umur"));

        rFEWorker rFEW = new rFEWorker(columnNames);

        Iterator<Map<String, String>> iterable = rFEW.getIterator();


        iterable.forEachRemaining(iter ->  {
            iter.keySet().forEach(key -> {
                System.out.println(key);
                System.out.println(iter.get(key));
            });
        } );
    }
}
