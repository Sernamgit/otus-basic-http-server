package ru.otus.java.basic.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LogManager.getLogger(HttpRequest.class);

    private String rawRequest;
    private String uri;
    private HttpMethod method;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private String body;

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.headers = new HashMap<>();
        this.parse();
    }

    private void parse() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        this.uri = rawRequest.substring(startIndex + 1, endIndex);
        this.method = HttpMethod.valueOf(rawRequest.substring(0, startIndex));
        this.parameters = new HashMap<>();

        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            this.uri = elements[0];
            String[] keysValues = elements[1].split("&");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                this.parameters.put(keyValue[0], keyValue[1]);
            }
        }

        String[] lines = rawRequest.split("\r\n");
        boolean isBody = false;
        for (String line : lines) {
            if (line.isEmpty()) {
                isBody = true;
            } else if (isBody) {
                this.body = line;
            } else if (line.contains(": ")) {
                String[] header = line.split(": ", 2);
                headers.put(header[0], header[1]);
            }
        }

        if (method == HttpMethod.POST) {
            this.body = rawRequest.substring(
                    rawRequest.indexOf("\r\n\r\n") + 4
            );
        }
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void printInfo(boolean showRawRequest) {
        logger.debug("URI: {}", uri);
        logger.debug("Method: {}", method);
        logger.debug("Body: {}", body);
        if (showRawRequest) {
            logger.debug("Raw Request: {}", rawRequest);
        }
    }
}
