import com.project.config.DBConnectionProvider;
import com.project.provider.ConnectionProviderS3;
import com.project.provider.ServiceS3;
import org.springframework.jdbc.core.JdbcTemplate;

public class Main {
    public static void main(String[] args) {
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getDatabaseConnection();

        ConnectionProviderS3 connectionProviderS3 = new ConnectionProviderS3();
        ServiceS3 serviceS3 = new ServiceS3(connectionProviderS3);

        serviceS3.listBuckets();

    }
}
