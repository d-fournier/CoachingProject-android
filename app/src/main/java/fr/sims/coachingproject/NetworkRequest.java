package fr.sims.coachingproject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by dfour on 15/02/2016.
 */
public class NetworkRequest<T> implements Response.ErrorListener, Response.Listener<String> {

    Gson mGson = new Gson();
    Class<T> mClazz;
    NetworkRequestListener<T> mListener;

    public NetworkRequest(Class<T> clazz, NetworkRequestListener<T> listener){
        mClazz = clazz;
        mListener = listener;
    }

    public String get(String url){
        return request(url, "GET");
    }

    private String request(String urlString, String method) {
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            String data = "TEST";
            os.write(data.getBytes());
            os.flush();
            os.close();

            int code = urlConnection.getResponseCode();
            System.out.println("CODE : " + code);
            if(code == HttpURLConnection.HTTP_OK) {

                InputStream is = urlConnection.getInputStream();
                
                is.close();
                String token = new String(buffer);
                token = token.substring(token.indexOf(":") + 2, token.lastIndexOf("\""));
                urlConnection.disconnect();
                return token;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(mListener != null) {
            mListener.onError();
        }
    }

    @Override
    public void onResponse(String response) {
        if(mListener != null) {
            mListener.onResponse(mGson.fromJson(response, mClazz));
        }
    }

    public interface NetworkRequestListener<T> {
        void onResponse(T object);
        void onError();
    }
}
