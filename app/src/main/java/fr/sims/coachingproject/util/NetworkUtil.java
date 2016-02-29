package fr.sims.coachingproject.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by dfour on 15/02/2016.
 */
public class NetworkUtil {

    public static String get(String url, String token){
        return request(url, "GET", token, null);
    }

    public static String post(String url, String token, String body){
        return request(url, "POST", token, body);
    }

    public static String put(String url, String token, String body){
        return request(url, "PUT", token, body);
    }

    private static String request(String urlString, String method, String token, String body) {
        StringBuilder res = new StringBuilder();

        HttpsURLConnection urlConnection;
        BufferedReader br;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            if(token != null) {
                urlConnection.setRequestProperty("Authorization", "Token "+token);
            }

            if(method.equals("PUT") || method.equals("POST")) {
                urlConnection.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                os.write(body.getBytes());
                os.flush();
                os.close();
            }

            int code = urlConnection.getResponseCode();
            if(code == HttpURLConnection.HTTP_OK) {
                InputStream is = urlConnection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    res.append(line);
                }
                br.close();
            }
            urlConnection.disconnect();
        }catch(UnknownHostException e1){
            //Returns empty string with res.toString()
        }catch (Exception e) {
            e.printStackTrace();
        }
        return res.toString();
    }

}
