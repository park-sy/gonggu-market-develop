package com.gonggu.pay.service;

import com.gonggu.pay.domain.User;
import com.gonggu.pay.kafka.PaymentToPush;
import com.gonggu.pay.repository.PaymentRepository;
import com.gonggu.pay.response.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentRepository paymentRepository;
    @Async
    public void sendPushRemitInfo(String topicName, User user, Long amount) {
        LocalDateTime now = LocalDateTime.now();
        PaymentToPush paymentToPush = new PaymentToPush(user.getNickname(),amount,now);
        HashMap<String, Object> pros = new HashMap<>();
        pros.put(topicName, paymentToPush);
        kafkaTemplate.send(topicName, pros);
    }
//    @KafkaListener(topics = "remitCreate", groupId = "gonggu")
//    public void consumeTest(String message) throws IOException {
//        System.out.println(String.format("Consumed message : %s", message));
//    }
}
