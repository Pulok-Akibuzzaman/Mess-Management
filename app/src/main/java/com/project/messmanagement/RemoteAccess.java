package com.project.messmanagement;


import java.io.*;
import java.net.*;

@SuppressWarnings("ALL")
public class RemoteAccess {

    private String TAG = "RemoteAccess";
    private static RemoteAccess instance = new RemoteAccess();

    // --- SUPABASE CONFIGURATION ---
    private static final String SUPABASE_URL = "https://ksynzkapkcuypivhyfmb.supabase.co/rest/v1/";
    private static final String SUPABASE_KEY = "sb_publishable_9Fnej7Ghnn1AoFhJQ5aClQ_0CRlimmV";

    private RemoteAccess() {
    }

    public static RemoteAccess getInstance() {
        return instance;
    }

    /**
     * Helper for Supabase requests specifically.
     * @param table The table name (e.g., "members")
     * @param method GET, POST, PATCH, etc.
     * @param jsonBody JSON string for POST/PATCH (optional)
     * @return Response string
     */
    public String makeSupabaseRequest(String table, String method, String jsonBody) {
        String url = SUPABASE_URL + table;
        HttpURLConnection http = null;
        try {
            URL urlc = new URL(url);
            http = (HttpURLConnection) urlc.openConnection();
            http.setRequestMethod(method);
            http.setConnectTimeout(10000);
            http.setReadTimeout(10000);

            // Supabase Headers
            http.setRequestProperty("apikey", SUPABASE_KEY);
            http.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Prefer", "return=representation"); // Optional: returns the object after insert/update

            if (jsonBody != null && (method.equals("POST") || method.equals("PATCH") || method.equals("PUT"))) {
                http.setDoOutput(true);
                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonBody);
                writer.flush();
                writer.close();
                os.close();
            }

            http.connect();
            InputStream is;
            int responseCode = http.getResponseCode();
            System.out.println("@SupabaseRequest-ResponseCode: " + responseCode); // Debug log

            if (responseCode >= 200 && responseCode < 300) {
                is = http.getInputStream();
            } else {
                is = http.getErrorStream();
            }

            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (http != null) http.disconnect();
        }
        return null;
    }

    public String makeHttpRequest(String url, String method, String[] keys, String[] values) {
        HttpURLConnection http = null;
        InputStream is = null;
        try {
            StringBuilder postData = new StringBuilder();
            if (keys != null && values != null) {
                for (int i = 0; i < keys.length; i++) {
                    if (i > 0) postData.append("&");
                    postData.append(URLEncoder.encode(keys[i], "UTF-8"));
                    postData.append("=");
                    postData.append(URLEncoder.encode(values[i], "UTF-8"));
                }
            }

            if (method.equals("GET") && postData.length() > 0) {
                url += "?" + postData.toString();
            }

            System.out.println("@RemoteAccess-" + ": " + url);
            URL urlc = new URL(url);
            http = (HttpURLConnection) urlc.openConnection();
            http.setRequestMethod(method);
            http.setConnectTimeout(10000);
            http.setReadTimeout(10000);

            if (method.equals("POST")) {
                http.setDoOutput(true);
                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                os.close();
            }

            http.connect();
            int responseCode = http.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = http.getInputStream();
            } else {
                is = http.getErrorStream();
            }

            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
        return null;
    }

    /**
     * Asynchronously syncs data to Supabase.
     */
    public void syncToSupabase(final String table, final String jsonPayload) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = makeSupabaseRequest(table, "POST", jsonPayload);
                System.out.println("@SupabaseSync-POST-" + table + ": " + response);
            }
        }).start();
    }

    /**
     * Generic sync for Update/Delete.
     */
    public void syncActionToSupabase(final String table, final String method, final String jsonPayload, final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = table;
                if (query != null && !query.isEmpty()) {
                    path += "?" + query;
                }
                String response = makeSupabaseRequest(path, method, jsonPayload);
                System.out.println("@SupabaseSync-" + method + "-" + table + ": " + response);
            }
        }).start();
    }

    /**
     * Asynchronously syncs data to Supabase using UPSERT logic.
     */
    public void upsertToSupabase(final String table, final String jsonPayload) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = SUPABASE_URL + table;
                    URL urlc = new URL(url);
                    HttpURLConnection http = (HttpURLConnection) urlc.openConnection();
                    http.setRequestMethod("POST");
                    http.setConnectTimeout(10000);
                    http.setReadTimeout(10000);

                    // Headers for UPSERT
                    http.setRequestProperty("apikey", SUPABASE_KEY);
                    http.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                    http.setRequestProperty("Content-Type", "application/json");
                    http.setRequestProperty("Prefer", "resolution=merge-duplicates"); // This is the magic line for Upsert

                    http.setDoOutput(true);
                    OutputStream os = http.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonPayload);
                    writer.flush();
                    writer.close();
                    os.close();

                    http.connect();
                    int responseCode = http.getResponseCode();
                    System.out.println("@SupabaseUpsert-" + table + ": " + responseCode);
                    http.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Helper to fetch data from Supabase.
     */
    public String syncFromSupabase(String table, String queryParams) {
        String path = table;
        if (queryParams != null && !queryParams.isEmpty()) {
            path += "?" + queryParams;
        }
        return makeSupabaseRequest(path, "GET", null);
    }
}
