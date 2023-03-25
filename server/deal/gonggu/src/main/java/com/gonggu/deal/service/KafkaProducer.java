package com.gonggu.deal.service;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.User;
import com.gonggu.deal.exception.DealNotFound;
import com.gonggu.deal.kafka.DealMemberToPush;
import com.gonggu.deal.repository.DealMemberRepository;
import com.gonggu.deal.kafka.DealUserToChat;
import com.gonggu.deal.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final DealRepository dealRepository;
    private final DealMemberRepository dealMemberRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Async
    public void sendDealMemberToPush(String topicName, Long dealId){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        List<String> members = dealMemberRepository.findByDeal(deal).stream()
                .map(o->o.getUser().getNickname()).collect(Collectors.toList());
        DealMemberToPush dealMemberToPush = new DealMemberToPush(dealId,deal.getTitle(),members);

        HashMap<String, Object> pros = new HashMap<>();
        pros.put(topicName, dealMemberToPush);
        kafkaTemplate.send(topicName,dealMemberToPush);
    }
    @Async
    public void sendDealAndUserToChat(String topicName, Long dealId, User user){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        DealUserToChat dealUserToChat = new DealUserToChat(dealId,deal.getTitle(), user.getNickname());

        HashMap<String, Object> pros = new HashMap<>();
        pros.put(topicName, dealUserToChat);
        kafkaTemplate.send(topicName,dealUserToChat);
    }
    @Async
    public void sendDealMemberAndTimeToPush(String topicName, Deal deal, int hour){
        List<String> members = dealMemberRepository.findByDeal(deal).stream()
                .map(o->o.getUser().getNickname()).collect(Collectors.toList());
        DealMemberToPush dealMemberToPush = new DealMemberToPush(deal.getId(),deal.getTitle(),members, hour);
        HashMap<String, Object> pros = new HashMap<>();
        pros.put(topicName, dealMemberToPush);
        kafkaTemplate.send(topicName,dealMemberToPush);
    }
//    @KafkaListener(topics = "test", groupId = "testgroup")
//    public void consumeTest(String message) throws IOException {
//        System.out.println(String.format("Consumed message : %s", message));
//    }
}
