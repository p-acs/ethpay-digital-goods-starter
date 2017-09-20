package de.pacs.ipfs;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;


public final class Ipfs {

    private static class ReadResultTask {
        private URI uri;
        private byte[] bytes;
        private int statusCode;

        public ReadResultTask(URI uri) {
            this.uri = uri;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public ReadResultTask invoke() throws IOException {
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpClient httpclient = getHttpClient();
            CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
            bytes = null;
            statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity responseEntity = httpResponse.getEntity();
                bytes = IOUtils.toByteArray(responseEntity.getContent());
            }
            httpclient.close();
            return this;
        }
    }


    private Ipfs() {
        //hide
    }

    public static final byte[] cat(String gateway, String multihash) throws IOException {
        URI uri = URI.create(gateway + multihash);
        ReadResultTask readResultTask = new ReadResultTask(uri).invoke();
        byte[] bytes = readResultTask.getBytes();
        if (bytes == null) {
            throw new IOException("response not OK" + readResultTask.getStatusCode());
        } else {
            return bytes;
        }
    }

    private static final CloseableHttpClient getHttpClient() {
        final int timeout = 1000;
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(4 * timeout).setConnectionRequestTimeout(3 * timeout).setSocketTimeout(3 * timeout).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }


}
