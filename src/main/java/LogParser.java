import java.util.Locale;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class LogParser {

    // Регулярное выражение для парсинга строки лога
    // 1: IP Address (\S+)
    // 2: Timestamp (\[([^\]]+)\])
    // 3: Method ([A-Z]+)
    // 4: Path (\S+)
    // 5: Protocol (HTTP/\d\.\d)
    // 6: Status Code (\d+)
    // 7: Bytes Sent (\d+|-)?
    // 8: Referer (\"([^\"]*)\")
    // 9: User-Agent (\"([^\"]*)\")
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+)" +                          // 1: IP Address
                    "\\s-\\s-" +                         // Ignored (dashes)
                    "\\s\\[([^\\]]+)\\]" +               // 2: Timestamp (inside brackets)
                    "\\s\"([A-Z]+)\\s(\\S+)\\s(HTTP\\/\\d\\.\\d)\"" + // 3: Method, 4: Path, 5: Protocol
                    "\\s(\\d+)" +                        // 6: Status Code
                    "\\s(\\d+|-)?" +                     // 7: Bytes Sent (can be a number or a dash, optional)
                    "\\s\"([^\"]*)\"" +                  // 8: Referer (quoted string, can be empty or '-')
                    "\\s\"([^\"]*)\"$"                   // 9: User-Agent (quoted string, can be empty or '-')
    );

    // Формат для парсинга даты и времени, включая смещение (+0300)
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public static LogEntry parseLine(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);

        if (!matcher.matches()) {
            return null; // Не удалось распарсить строку
        }

        // Извлекаем группы
        String ipAddress = matcher.group(1);

        OffsetDateTime timestamp = null;
        try {
            timestamp = OffsetDateTime.parse(matcher.group(2), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка парсинга даты: " + matcher.group(2) + " в строке: " + line);
        }

        String method = matcher.group(3);
        String path = matcher.group(4);
        String protocol = matcher.group(5);

        int statusCode = 0;
        try {
            statusCode = Integer.parseInt(matcher.group(6));
        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга кода статуса: " + matcher.group(6) + " в строке: " + line);
        }

        long bytesSent = 0;
        String bytesSentStr = matcher.group(7);
        if (bytesSentStr != null && !bytesSentStr.equals("-")) {
            try {
                bytesSent = Long.parseLong(bytesSentStr);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка парсинга размера данных: " + bytesSentStr + " в строке: " + line);
            }
        }

        String referer = matcher.group(8);
        if (referer != null && referer.equals("-")) {
            referer = null;
        }

        String userAgent = matcher.group(9);
        if (userAgent != null && userAgent.equals("-")) {
            userAgent = null;
        }

        return new LogEntry(ipAddress, timestamp, method, path, protocol, statusCode, bytesSent, referer, userAgent);
    }
}
