public class Main {
    public static void main(String[] args) {
        String logFilePath = "access (1).log"; // Имя вашего лог-файла
        System.out.println("Обработка лог-файла: " + logFilePath);
        LogProcessor.processLogFile(logFilePath);
    }
}
