package com.project.services;

import com.project.provider.ConnectionProviderS3;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ServiceS3 {

    private final ConnectionProviderS3 connectionProviderS3;
    private final S3Client s3Client;
    public static String csvName;

    public ServiceS3(ConnectionProviderS3 connectionProviderS3) {
        this.connectionProviderS3 = connectionProviderS3;
        this.s3Client = connectionProviderS3.getS3Client();
    }

    public String getFirstBucket() {
        List<Bucket> buckets = s3Client.listBuckets().buckets();
        return buckets.get(0).name();
    }

    public String getFirstXlsxKey(String bucketName) {
        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        List<S3Object> objects = s3Client.listObjects(listObjects).contents();

        return objects.stream()
                .filter(x -> x.key().contains("police"))
                .findFirst()
                .map(x -> x.key())
                .orElse(null);
    }

    public InputStream getObjectInputStream(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    public String createLogKey() {
        LocalDateTime dataHoraAtual = LocalDateTime.now();

        return "log" + dataHoraAtual + ".log";
    }

    public void createLog(String bucket, String key, ByteArrayOutputStream byteArrayOutputStream) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("text/plain")
                .build();
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(byteArrayOutputStream.toByteArray()));
        } catch (S3Exception e) {
            System.err.println("Erro ao enviar o arquivo para o S3: " + e.getMessage());
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o ByteArrayOutputStream: " + e.getMessage());
            }
        }
    }
}
