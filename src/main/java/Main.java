import com.project.config.Config;
import com.project.provider.DBConnectionProvider;
import com.project.services.DatasetToDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

public class Main {
    public static void main(String[] args) {
        Config.getAllEnviroments();

        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getDatabaseConnection();

        DatasetToDatabase datasetToDatabase = new DatasetToDatabase();

        datasetToDatabase.extractAndInsert();
    }
}
