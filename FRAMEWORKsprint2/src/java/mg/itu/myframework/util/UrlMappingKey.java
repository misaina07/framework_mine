package mg.itu.myframework.util;

import mg.itu.myframework.annotation.HttpMethod;

import java.util.Objects;

public class UrlMappingKey {
    private String url;
    private HttpMethod method;

    public UrlMappingKey(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlMappingKey)) return false;
        UrlMappingKey that = (UrlMappingKey) o;
        return url.equals(that.url) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return "<" + url + ", " + method + ">";
    }
}
