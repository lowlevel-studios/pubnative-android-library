package net.pubnative.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by davidmartin on 08/12/15.
 */
public class PubnativeTestUtils {

    public static String getResponseJSON(String jsonFile){

        return getStringFromJSONFile("response", jsonFile);
    }

    private static String getStringFromJSONFile(String folder, String jsonFile) {

        InputStream configStream = PubnativeTestUtils.class.getResourceAsStream("/" + folder + "/" + jsonFile);
        return readStringFromInputStream(configStream);
    }

    private static String readStringFromInputStream(InputStream inputStream) {

        BufferedReader bufferReader  = null;
        StringBuilder  stringBuilder = new StringBuilder();

        try {

            String line;
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = bufferReader.readLine()) != null) {

                stringBuilder.append(line);
            }

        } catch (IOException e) {

            System.out.println("PubnativeStringUtils.readTextFile - ERROR: " + e);

        } finally {

            if (bufferReader != null) {

                try {

                    bufferReader.close();

                } catch (IOException e) {

                    System.out.println("PubnativeStringUtils.readTextFile - ERROR: " + e);
                }
            }
        }

        return stringBuilder.toString();
    }
}
