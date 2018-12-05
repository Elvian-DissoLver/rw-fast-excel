package com.fastExcel;


import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

public interface fastExcel<T> extends Closeable {
    List<String> getHeader();

    SampleFile getSample();

    Iterator<T> getIterator();

}
