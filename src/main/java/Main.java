import com.project.config.Config;
import com.project.services.DatasetToDatabase;
import com.project.services.SlackMessageSender;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config.getAllEnviroments();

        DatasetToDatabase datasetToDatabase = new DatasetToDatabase();

        datasetToDatabase.extractAndInsert();

        SlackMessageSender.sendMessageToSlack();
    }
}
