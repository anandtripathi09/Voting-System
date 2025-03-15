import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    public static String DB_URL;
    public static String DB_USER;
    public static String DB_PASSWORD;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("config.properties"));
            DB_URL = properties.getProperty("DB_URL");
            DB_USER = properties.getProperty("DB_USER");
            DB_PASSWORD = properties.getProperty("DB_PASSWORD");
        } catch (IOException e) {
            System.err.println("Failed to load database configuration: " + e.getMessage());
        }
    }
}
