package com.brunodles.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebClient {

    public static final int LOG_NONE = 0;
    public static final int LOG_URL = 1;
    public static final int LOG_BODY = 2;

    public static final String crlf = "\r\n";
    public static final String twoHyphens = "--";
    public static final String boundary = "*****";

    public static final int BUFFER_SIZE = 1024;
    public static final String CONTENT_TYPE = "Content-Type";
    private final String USER_AGENT = "Mozilla/5.0";

    private String method = "GET";
    private String out;
    private Map<String, String> urlParams = new HashMap<String, String>();
    private Map<String, String> postParams = new HashMap<String, String>();
    private Map<String, File> fileParams = new HashMap<String, File>();
    private Map<String, String> requestProperties = new HashMap<String, String>();
    private int timout;
    private final String urlStr;
    private int logLevel = 0;

    public WebClient(String urlStr) {
        this.urlStr = urlStr;
    }

    public WebClient setTimeout(Integer timeout) {
        this.timout = timeout;
        return this;
    }

    public WebClient setOut(String out) {
        this.out = out;
        return this;
    }

    public WebClient setContentType(String type) {
        return addRequestProperty(CONTENT_TYPE, type);
    }

    public WebClient setContentTypeJson() {
        return setContentType("application/json");
    }

    public WebClient setContentTypeMultipartFormData() {
        return setContentType("multipart/form-data;boundary=" + boundary);
    }

    public WebClient addRequestProperty(String key, String value) {
        requestProperties.put(key, value);
        return this;
    }

    public WebClient addUrlParameter(String key, String value) {
        urlParams.put(key, value);
        return this;
    }

    public WebClient addPostParameter(String key, String value) {
        postParams.put(key, value);
        return this;
    }

    public WebClient addFileParameter(String key, File file) {
        fileParams.put(key, file);
        return this;
    }

    public WebClient setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public Response doGet() throws IOException {
        return doMethod("GET");
    }

    public Response doPost() throws IOException {
        return doMethod("POST");
    }

    public Response doPut() throws IOException {
        return doMethod("PUT");
    }

    public Response doMethod(String methodName) throws IOException {
        method = methodName;
        return execute();
    }

    private Response execute() throws IOException {
        String urlParams = buildParams(this.urlParams);
//        urlParams = URLEncoder.encode(urlParams, "UTF-8");
        String requestUrl = urlStr + "?" + urlParams;
//        System.out.println("requestUrl "+requestUrl);
//        requestUrl = URLEncoder.encode(requestUrl, "UTF-8");
        if (logLevel == LOG_URL) System.out.println("requestUrl " + requestUrl);
        URL url = new URL(requestUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setConnectTimeout(timout);
        con.setRequestMethod(method);
        buildProperties(con);
        if (isMultiPart())
            writeMultiPartOut(con, postParams, fileParams);
        else if (out != null)
            writeOut(con, out);
        else if (!postParams.isEmpty())
            writeOut(con, buildParams(postParams));
        int responseCode = con.getResponseCode();
        final String response = readResponse(con);
        return new Response(responseCode, response);
    }

    private boolean isMultiPart() {
        if (!requestProperties.containsKey(CONTENT_TYPE)) return false;
        return requestProperties.get(CONTENT_TYPE).contains("multipart");
    }

    private String buildParams(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder paramsStr = new StringBuilder();
        boolean first = true;
        final Set<Map.Entry<String, String>> params = map.entrySet();
        for (Map.Entry<String, String> entry : params) {
            if (first) {
                first = false;
            } else {
                paramsStr.append("&");
            }
            paramsStr
                    .append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return paramsStr.toString();
    }

    private void buildProperties(HttpURLConnection con) {
        for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
            con.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void writeMultiPartOut(HttpURLConnection con, Map<String, String> params, Map<String, File> fileParams) throws IOException {
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        DataOutputStream wr = new DataOutputStream(os);
        for (Map.Entry<String, String> param : params.entrySet()) {
            wr.writeBytes(twoHyphens + boundary + crlf);
            wr.writeBytes("Content-Disposition: form-data; name=\"" + param.getKey() + "\"" + crlf);
            wr.writeBytes("Content-Type: text/plain" + crlf);
            wr.writeBytes(crlf);
            wr.writeBytes(param.getValue());
            wr.writeBytes(crlf);
        }
        for (Map.Entry<String, File> param : fileParams.entrySet()) {
            File file = param.getValue();
            wr.writeBytes(twoHyphens + boundary + crlf);
            wr.writeBytes("Content-Disposition: form-data; name=\"" + param.getKey() + "\"; " +
                    "filename=\"" + file.getName() + "\"" + crlf);
            wr.writeBytes("Content-Type: text/plain" + crlf);
            wr.writeBytes("Content-Transfer-Encoding: binary" + crlf);
            wr.writeBytes(crlf);

            FileInputStream fileInputStream = new FileInputStream(file);
            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                wr.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, BUFFER_SIZE);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            wr.writeBytes(crlf);
        }
        wr.writeBytes(twoHyphens + boundary + twoHyphens);
        wr.flush();
        wr.close();
    }

    private void writeOut(HttpURLConnection con, String out) throws IOException {
        if (logLevel == LOG_BODY) System.out.println("body " + out);
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        writeOutputStream(os, out);
    }

    public static void writeOutputStream(final OutputStream os, final String out1) throws IOException {
        DataOutputStream wr = new DataOutputStream(os);
        wr.writeBytes(out1);
        wr.flush();
        wr.close();
    }

    private String readResponse(HttpURLConnection con) throws IOException {
        InputStream is = con.getInputStream();
        return readInputStream(is);
    }

    public static String readInputStream(InputStream is) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[BUFFER_SIZE];
        int size = 0;
        while ((size = is.read(buffer)) > 0) {
            builder.append(new String(buffer, 0, size));
        }

        is.close();
        return builder.toString();
    }

    public static class Response {

        public Integer code;
        public String response;

        private Response(Integer code, String response) {
            this.code = code;
            this.response = response;
        }

        @Override
        public String toString() {
            return "Response{" + "code=" + code + ", response=" + response + '}';
        }

    }
}
