package com.student.framework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FileUtil {
    public static ArrayList<String> readFile(String aFilePath) throws Exception
    {
        ArrayList<String> l_result = new ArrayList<String>();
    try {
        BufferedReader l_BufferedReader = new BufferedReader(new FileReader(aFilePath));
        String l_str = "";
        while ((l_str = l_BufferedReader.readLine()) != null) {
            l_result.add(l_str);
        }
        l_BufferedReader.close();

    } catch (Exception e) {
        throw e;
    }

    return l_result;
}
}
