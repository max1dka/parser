
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LogProcessor {

    public static void processLogFile(String filepath) {
        Set<String> uniqueIps = new HashSet<>();
        Map<Integer, Integer> statusCodeCounts = new HashMap<>();
        Map<String, Integer> methodCounts = new HashMap<>();
        Map<String, Integer> popularPaths = new HashMap<>();
        Map<String, Integer> popularReferers = new HashMap<>();
        Map<Integer, Integer> requestsByHour = new HashMap<>(); // Integer для часа, Integer для количества
        long totalBytesTransferred = 0;
        int totalRequests = 0;
        int failedParses = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = LogParser.parseLine(line);
                if (entry != null) {
                    totalRequests++;
                    uniqueIps.add(entry.getIpAddress());

                    statusCodeCounts.merge(entry.getStatusCode(), 1, Integer::sum);
                    methodCounts.merge(entry.getMethod(), 1, Integer::sum);
                    popularPaths.merge(entry.getPath(), 1, Integer::sum);

                    if (entry.getReferer() != null) {
                        popularReferers.merge(entry.getReferer(), 1, Integer::sum);
                    }

                    totalBytesTransferred += entry.getBytesSent();

                    if (entry.getTimestamp() != null) {
                        requestsByHour.merge(entry.getHourOfDay(), 1, Integer::sum);
                    }

                } else {
                    failedParses++;
                    // System.err.println("Не удалось распарсить строку: " + line); // Можно включить для дебага
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return;
        }

        System.out.println("--- Общая статистика ---");
        System.out.println("Всего запросов: " + totalRequests);
        System.out.println("Уникальных IP-адресов: " + uniqueIps.size());
        System.out.println("Общий объем переданных данных: " + totalBytesTransferred + " байт");
        if (failedParses > 0) {
            System.out.println("Не удалось распарсить строк: " + failedParses);
        }

        System.out.println("\n--- Статусы HTTP ---");
        printTopEntries(statusCodeCounts, 5);

        System.out.println("\n--- Методы HTTP ---");
        printTopEntries(methodCounts, 5);

        System.out.println("\n--- Самые популярные пути ---");
        printTopEntries(popularPaths, 5);

        System.out.println("\n--- Самые популярные рефереры ---");
        printTopEntries(popularReferers, 5);

        System.out.println("\n--- Запросы по часам дня ---");
        // Сортируем по часу для лучшей читаемости
        requestsByHour.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("Час %02d:00: %d запросов%n", entry.getKey(), entry.getValue()));
    }

    // Вспомогательная функция для печати N самых популярных элементов из Map
    private static <K> void printTopEntries(Map<K, Integer> map, int limit) {
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .forEach(entry -> System.out.printf("'%s': %d запросов%n", entry.getKey(), entry.getValue()));
    }
}
