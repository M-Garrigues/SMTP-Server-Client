package src;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Coded by Mathieu GARRIGUES on 13/03/2019.
 */
public class Message {

    private final Map<String, List<String>> headers;

    private final String body;

    protected Message(Map<String, List<String>> headers, String body) {
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

}