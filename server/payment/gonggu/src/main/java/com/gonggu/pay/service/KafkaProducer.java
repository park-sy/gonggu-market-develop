package com.gonggu.pay.service;

import com.gonggu.pay.domain.User;
import com.gonggu.pay.kafka.PaymentToPush;
import com.gonggu.pay.repository.PaymentRepository;
import com.gonggu.pay.response.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    @Value(value = "${message.topic.name}")
    private String topicName;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentRepository paymentRepository;
    public void sendPushRemitInfo(User user, Long amount) {
        LocalDateTime now = LocalDateTime.now();

        HashMap<String, Object> pros = new HashMap<>();
        pros.put("payments", new PaymentToPush(user.getNickname(),amount,now));
        kafkaTemplate.send(topicName, pros);
    }

}
