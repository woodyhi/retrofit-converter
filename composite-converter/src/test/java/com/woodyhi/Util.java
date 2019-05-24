package com.woodyhi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author June.C
 * @date 2019-05-23
 */
public class Util {

    public static String readSources(String path) {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = Util.class.getResourceAsStream(path);//"/user.txt"
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
