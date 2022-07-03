package com.tool.craft.controller;

import com.amazonaws.services.s3.event.S3EventNotification;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CraftSqsListener {

    @SqsListener(value = "s3-sqs-craft-payment", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receive(S3EventNotification s3EventNotificationRecord) {
        S3EventNotification.S3Entity s3Entity = s3EventNotificationRecord.getRecords().get(0).getS3();
    }
}
