package com.justreached.Others;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SYED_ZAKRIYA on 2/13/2017.
 */

public class MyJSONParser
{
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    // constructor
    public MyJSONParser() {
    }
    public String getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            //DefaultHttpClient httpClient = new DefaultHttpClient();
            URL urlObj = new URL(url);

            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
            is = urlConnection.getInputStream();
            int status = urlConnection.getResponseCode();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            json = sb.toString();
            is.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;

    }

}
