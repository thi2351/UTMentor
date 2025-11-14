package com.example.utmentor.services;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName;

    public GoogleCloudStorageService(
            @Value("${gcs.bucket-name}") String bucketName,
            @Value("${gcs.project-id}") String projectId,
            @Value("${gcs.credentials.fileName}") String credentialsFilePath) {

        this.bucketName = bucketName;

        InputStream serviceAccountStream = getClass().getClassLoader().getResourceAsStream(credentialsFilePath);
        if (serviceAccountStream == null) {
            throw new RuntimeException("Không tìm thấy file " + credentialsFilePath + " trong resources");
        }

        try (serviceAccountStream) {
            this.storage = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(ServiceAccountCredentials.fromStream(serviceAccountStream))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Google Cloud Storage client", e);
        }
    }


    public String uploadFile(MultipartFile file, String destination) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        storage.create(
                com.google.cloud.storage.BlobInfo.newBuilder(bucketName, destination + "/" + fileName).build(),
                file.getBytes()
        );
        return String.format("https://storage.googleapis.com/%s/%s/%s", bucketName, destination, fileName);

    }
}
