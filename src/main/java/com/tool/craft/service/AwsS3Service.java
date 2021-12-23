package com.tool.craft.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Log4j2
@Service
public class AwsS3Service {

    public String saveInBucket(MultipartFile multipartFile) throws IOException {

        final S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();

        String key = UUID.randomUUID() + "." + getExtension(multipartFile.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("craft-payment")
                .key(key)
                .contentLength(multipartFile.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(multipartFile.getBytes()));
        return key;
    }

    public void deleteInBucket(String receipt) {

        final S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();

        s3Client.deleteObject(DeleteObjectRequest.builder().bucket("craft-payment").key(receipt).build());
    }
}
