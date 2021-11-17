package server;

import java.util.Objects;

public class PathHTTP {
    public String path;
    public String http;

    public PathHTTP(String path, String http) {
        this.path = path;
        this.http = http;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathHTTP pathHTTP = (PathHTTP) o;
        return this.path.equals(pathHTTP.path) && this.http.equals(pathHTTP.http);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, http);
    }
}
