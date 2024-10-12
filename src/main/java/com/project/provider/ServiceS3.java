package com.project.provider;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceS3 {


    private final ConnectionProviderS3 connectionProviderS3;
    private final S3Client s3Client;

    public ServiceS3(ConnectionProviderS3 connectionProviderS3) {
        this.connectionProviderS3 = connectionProviderS3;
        this.s3Client = connectionProviderS3.getS3Client();
    }

    // Método para listar todos os buckets e pegar o primeiro
    public void listBuckets() {
        List<String> bucketNames = new ArrayList<>();
        List<Bucket> buckets = s3Client.listBuckets().buckets();
        for (Bucket bucket : buckets) {
            bucketNames.add(bucket.name());
            System.out.println("Bucket: " + bucket.name());
        }

        downloadObjects(bucketNames.get(0));
    }

    // Método para listar objetos de um bucket
    public void listObjects(String bucketName) {
        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        List<S3Object> objects = s3Client.listObjects(listObjects).contents();
        for (S3Object object : objects) {
            System.out.println("Objeto: " + object.key());
        }

    }

    // Método para fazer download de todos os objetos de um bucket
    public void downloadObjects(String bucketName) {
        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        List<S3Object> objects = s3Client.listObjects(listObjects).contents();
        for (S3Object object : objects) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(object.key())
                    .build();

            try (InputStream objectContent = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream())) {
                Files.copy(objectContent, new File(object.key()).toPath());
                System.out.println("Arquivo baixado: " + object.key());
            } catch (Exception e) {
                System.err.println("Erro ao baixar o arquivo: " + object.key());
                e.printStackTrace();
            }
        }
    }

    // Método para criar um bucket
    public void createBucket(String bucketName) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.createBucket(createBucketRequest);
        System.out.println("Bucket criado: " + bucketName);
    }

    // Método para fazer upload de um arquivo para o S3
    public void uploadFile(String bucketName, String filePath) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(UUID.randomUUID().toString()) // Usa um UUID como chave
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(new File(filePath)));

        System.out.println("Arquivo enviado com sucesso!");
    }

    // Método para excluir um objeto de um bucket
    public void deleteObject(String bucketName, String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        System.out.println("Objeto deletado: " + objectKey);
    }

}
