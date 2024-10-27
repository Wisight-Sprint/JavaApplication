package com.project.provider;

import com.project.config.Config;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class ConnectionProviderS3 {

    private final AwsSessionCredentials credentials;

    public ConnectionProviderS3() {
        this.credentials = AwsSessionCredentials.create(
                Config.get("AWS_ACCESS_KEY_ID"),
                Config.get("AWS_SECRET_ACCESS_KEY"),
                Config.get("AWS_SESSION_TOKEN")
        );
    }

    public S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> credentials)
                .build();
    }
}