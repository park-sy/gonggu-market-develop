package com.gonggu.deal.service;

import com.gonggu.deal.domain.User;
import com.gonggu.deal.kafka.DealMemberToPush;
import com.gonggu.deal.repository.DealMemberRepository;
import com.gonggu.deal.kafka.DealUserToChat;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final DealMemberRepository dealMemberRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void sendDealMember(String topicName, Long dealId){
        List<DealMemberToPush> member = dealMemberRepository.findByDealId(dealId).stream()
                .map(o->new DealMemberToPush(o.getUser().getNickname())).collect(Collectors.toList());

        HashMap<String, Object> pros = new HashMap<>();
        pros.put("topicName", member);
        kafkaTemplate.send(topicName,member);
    }
    public void sendDealAndUser(String topicName, Long dealId, User user){
        DealUserToChat dealUserToChat = new DealUserToChat(dealId, user.getNickname());
        HashMap<String, Object> pros = new HashMap<>();
        pros.put("topicName", dealUserToChat);
        kafkaTemplate.send(topicName,dealUserToChat);
    }

//    @KafkaListener(topics = "chatJoin", groupId = "testgroup")
//    public void consumeTest(String message) throws IOException {
//        System.out.println(String.format("Consumed message : %s", message));
//    }
}
