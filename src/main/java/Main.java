import com.project.config.Config;
import com.project.services.DatasetToDatabase;

public class Main {
    public static void main(String[] args) {
        Config.getAllEnviroments();

        DatasetToDatabase datasetToDatabase = new DatasetToDatabase();

        datasetToDatabase.extractAndInsert();
    }
}
