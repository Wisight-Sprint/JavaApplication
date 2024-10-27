import com.project.config.Config;
import com.project.provider.DBConnectionProvider;
import com.project.provider.ConnectionProviderS3;
import com.project.services.ServiceS3;
import com.project.services.TransformCsvToXlsx;
import org.springframework.jdbc.core.JdbcTemplate;

public class Main {
    public static void main(String[] args) {
        Config.getAllEnviroments();

        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getDatabaseConnection();

        ConnectionProviderS3 connectionProviderS3 = new ConnectionProviderS3();
        ServiceS3 serviceS3 = new ServiceS3(connectionProviderS3);

        serviceS3.listBuckets();

        TransformCsvToXlsx transformCsvToXlsx = new TransformCsvToXlsx();

        transformCsvToXlsx.convert();
    }
}
