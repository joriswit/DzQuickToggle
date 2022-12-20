package nl.joriswit.dzquicktoggle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Api {

    private final URL baseUrl;

    public Api(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public ArrayList<Switch> GetSwitches() {
        String json;
        URL url;
        try {
            url = new URL(baseUrl, "/json.htm?type=devices&filter=light&used=true");

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpConn.getInputStream();

                StringBuilder textBuilder = new StringBuilder();
                try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                    int c;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char)c);
                    }
                }
                json = textBuilder.toString();
                inputStream.close();
            } else {
                return null;
            }
            httpConn.disconnect();
        } catch (IOException e) {
            return null;
        }

        ArrayList<Switch> items = new ArrayList<Switch>();
        try {
            JSONObject root = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray results = root.getJSONArray("result");
            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                int idx = obj.getInt("idx");
                String name = obj.getString("Name");
                Switch lightSwitch = new Switch(idx, name);
                items.add(lightSwitch);
            }
        } catch (JSONException e) {
            return null;
        }

        return items;
    }

    public boolean Toggle(int idx){
        String json;
        URL url;
        try {
            url = new URL(baseUrl, "/json.htm?type=command&param=switchlight&idx=" + idx + "&switchcmd=Toggle");

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpConn.getInputStream();

                StringBuilder textBuilder = new StringBuilder();
                try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                    int c;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char)c);
                    }
                }
                json = textBuilder.toString();
                inputStream.close();
            } else {
                return false;
            }
            httpConn.disconnect();
        } catch (IOException e) {
            return false;
        }

        try {
            JSONObject root = (JSONObject) new JSONTokener(json).nextValue();
            String status = root.getString("status");
            return status.equals("OK");
        } catch (JSONException e) {
            return false;
        }
    }
}
