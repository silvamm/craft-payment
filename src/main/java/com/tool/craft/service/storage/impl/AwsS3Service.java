package com.tool.craft.service.storage.impl;

import com.tool.craft.service.storage.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Log4j2
@Service
public class AwsS3Service implements StorageService {

    @Override
    public String save(byte[] bytes, Long size, String extension) {
        final S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();

        String key = UUID.randomUUID() + "." + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("craft-payment")
                .key(key)
                .contentLength(size)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        return key;
    }

    @Override
    public void delete(String fileNameWithExtension) {
        final S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();

        s3Client.deleteObject(DeleteObjectRequest.builder().bucket("craft-payment").key(fileNameWithExtension).build());
    }
}
