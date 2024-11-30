package com.project.model;

import com.project.provider.ConnectionProviderS3;
import com.project.provider.DBConnectionProvider;
import com.project.services.ServiceS3;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class Tratativa {

    ConnectionProviderS3 connectionProviderS3 = new ConnectionProviderS3();
    ServiceS3 serviceS3 = new ServiceS3(connectionProviderS3);

    String bucket = serviceS3.getFirstBucket();
    String xlsxKey = serviceS3.getFirstXlsxKey(bucket);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BufferedWriter writerlog = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));

    public abstract void tratativaDados() throws IOException;

    public abstract void writeLog(String message) throws IOException;

}
