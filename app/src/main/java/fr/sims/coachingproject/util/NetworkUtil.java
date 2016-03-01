package fr.sims.coachingproject.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by dfour on 15/02/2016.
 */
public class NetworkUtil {

    public static NetworkResponse get(String url, String token){
        return request(url, "GET", token, null);
    }

    public static NetworkResponse post(String url, String token, String body){
        return request(url, "POST", token, body);
    }

    public static NetworkResponse put(String url, String token, String body){
        return request(url, "PUT", token, body);
    }

    public static NetworkResponse patch(String url, String token, String body){
        return request(url, "PATCH", token, body);
    }

    private static NetworkResponse request(String urlString, String method, String token, String body) {
        StringBuilder res = new StringBuilder();

        HttpsURLConnection urlConnection;
        BufferedReader br;

        int responseCode = NetworkResponse.UNKNOWN_ERROR;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            if(token != null) {
                urlConnection.setRequestProperty("Authorization", "Token "+token);
            }

            if(method.equals("PUT") || method.equals("POST") || method.equals("PATCH")) {
                urlConnection.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                os.write(body.getBytes());
                os.flush();
                os.close();
            }

            responseCode = urlConnection.getResponseCode();
            InputStream is;
            if(responseCode >= 400) {
                is = urlConnection.getErrorStream();
            } else {
                is = urlConnection.getInputStream();
            }

            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
            br.close();

            urlConnection.disconnect();
        } catch (UnknownHostException e1) {
            responseCode = NetworkResponse.UNKNOWN_HOST_ERROR;
        } catch (ProtocolException e2) {
            responseCode = NetworkResponse.PROTOCOL_ERROR;
        } catch (MalformedURLException e3) {
            responseCode = NetworkResponse.MALFORMED_URL_ERROR;
        } catch (IOException e4) {
            responseCode = NetworkResponse.STREAM_EXCEPTION_ERROR;
        }
        return new NetworkResponse(res.toString(), responseCode);
    }

    public static class NetworkResponse {

        public final static int UNKNOWN_ERROR = -1;
        public final static int UNKNOWN_HOST_ERROR = -2;
        public final static int PROTOCOL_ERROR = -3;
        public final static int MALFORMED_URL_ERROR = -4;
        public final static int STREAM_EXCEPTION_ERROR = -5;

        private String mBody;
        private int mReturnCode;

        public NetworkResponse(String mBody, int mReturnCode) {
            this.mBody = mBody;
            this.mReturnCode = mReturnCode;
        }

        public String getBody() {
            return mBody;
        }

        public int getReturnCode() {
            return mReturnCode;
        }
    }

}
