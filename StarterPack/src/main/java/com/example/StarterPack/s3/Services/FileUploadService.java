package com.example.StarterPack.s3.Services;

import java.net.URI;

import org.springframework.stereotype.Service;

import com.example.StarterPack.s3.Config.S3Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileUploadService {
    private final S3Configuration s3Configuration;
    private S3Client s3Client;

    public FileUploadService(S3Configuration s3Configuration) {
        this.s3Configuration = s3Configuration;
        // Tạo credential (giống username + password)
        AwsCredentials credentials = AwsBasicCredentials.create(
                s3Configuration.getAccessKey(),
                s3Configuration.getSecretKey());
        s3Client = S3Client.builder()
        // URL endpoint (có thể là AWS S3 hoặc MinIO)
                .endpointOverride(URI.create(s3Configuration.getBaseUrl()))
        // Region (vd: ap-southeast-1)
                .region(Region.of(s3Configuration.getRegion()))
        // Gắn credentials vào client
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
        // Quan trọng nếu dùng MinIO (URL dạng: /bucket/file thay vì subdomain)
                .forcePathStyle(true)
                .build();
    }

    public String uploadFile(String filePath, byte[] file) {
        System.out.println("-----------------------FileUploadService.uploadFile--------------------");
        /*
            filePath: đường dẫn file trên S3 (vd: images/avatar.png)
            file: nội dung file dạng byte[]
        */
        // Bước 1: Tạo request upload
        PutObjectRequest request = PutObjectRequest.builder()
        // Bucket giống như folder lớn
            .bucket(s3Configuration.getBucketName())
        // Loại lưu trữ (standard, cold...)
            .storageClass(s3Configuration.getStorageClass())
        // Đường dẫn file trong bucket
            .key(filePath)
        // File public (ai cũng truy cập được qua URL)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build();
        // Bước 2: Upload file
        /*
            👉 Gửi file lên S3
            👉 Nếu thành công → file đã nằm trên server 
        */
        s3Client.putObject(request, RequestBody.fromBytes(file));
        try {
            // Bước 3: Lấy URL của file
            GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(s3Configuration.getBucketName()).key(filePath).build();
            return s3Client.utilities().getUrl(getUrlRequest).toURI().toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get URL of uploaded file", e);
        }
    }
}
