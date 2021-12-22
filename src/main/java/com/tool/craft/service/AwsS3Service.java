package com.tool.craft.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Log4j2
@Service
public class AwsS3Service {

    public String saveInBucket(InputStream inputStream)  {

        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();

        String key = UUID.randomUUID().toString();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket("craft-payment")
                .key(key)
                .build();

        byte[] bytes;
        try {
            bytes = IoUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        s3Client.putObject(objectRequest, RequestBody.fromBytes(bytes));
        return key;
    }
}
