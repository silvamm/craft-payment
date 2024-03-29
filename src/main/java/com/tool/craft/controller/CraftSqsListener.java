package com.tool.craft.controller;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.tool.craft.service.storage.StorageService;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CraftSqsListener {

    private final StorageService storageService;
    private final CraftRestController craftRestController;

    @SqsListener(value = "s3-sqs-craft-payment", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receive(S3EventNotification s3EventNotificationRecord) {

        if(recordsEmpty(s3EventNotificationRecord)) return;

        S3EventNotification.S3Entity s3Entity = s3EventNotificationRecord.getRecords().get(0).getS3();

        InputStream inputStream = storageService.get(s3Entity.getObject().getKey());
        try {
            String name = s3Entity.getObject().getKey();
            MultipartFile multipartFile = new MockMultipartFile(name, name, null, inputStream);
            craftRestController.start(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean recordsEmpty(S3EventNotification s3EventNotificationRecord) {
        return s3EventNotificationRecord.getRecords() == null || s3EventNotificationRecord.getRecords().isEmpty();
    }

}
