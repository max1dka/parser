import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;

public class LogEntry {
    private final String ipAddress;
    private final OffsetDateTime timestamp;
    private final String method;
    private final String path;
    private final String protocol;
    private final int statusCode;
    private final long bytesSent;
    private final String referer;
    private final String userAgent;

    public LogEntry(String ipAddress, OffsetDateTime timestamp, String method, String path,
                    String protocol, int statusCode, long bytesSent, String referer, String userAgent) {
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.bytesSent = bytesSent;
        this.referer = referer;
        this.userAgent = userAgent;
    }

    // --- Геттеры для всех полей ---
    public String getIpAddress() {
        return ipAddress;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public String getReferer() {
        return referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    // --- Вспомогательные методы ---
    public String getHostnameFromReferer() {
        if (referer != null && !referer.isEmpty()) {
            try {
                URL url = new URL(referer);
                return url.getHost();
            } catch (MalformedURLException e) {
                // Игнорируем некорректные URL рефереров
                return null;
            }
        }
        return null;
    }

    public Integer getHourOfDay() {
        return timestamp != null ? timestamp.getHour() : null;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ip='" + ipAddress + '\'' +
                ", time=" + timestamp +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", status=" + statusCode +
                ", bytes=" + bytesSent +
                '}';
    }
}
