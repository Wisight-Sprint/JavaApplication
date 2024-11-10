package com.project.services;

import com.project.provider.ConnectionProviderS3;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

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

    public String getFirstObject(String bucketName) {
        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        List<S3Object> objects = s3Client.listObjects(listObjects).contents();

        return objects.get(0).key();
    }

    public InputStream getObjectInputStream(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
}
