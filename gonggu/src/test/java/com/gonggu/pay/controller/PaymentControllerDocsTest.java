package com.gonggu.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonggu.pay.domain.Account;
import com.gonggu.pay.domain.Payment;
import com.gonggu.pay.domain.Transaction;
import com.gonggu.pay.domain.User;
import com.gonggu.pay.repository.AccountRepository;
import com.gonggu.pay.repository.PaymentRepository;
import com.gonggu.pay.repository.TransactionRepository;
import com.gonggu.pay.repository.UserRepository;
import com.gonggu.pay.request.PaymentCharge;
import com.gonggu.pay.request.RemitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.gonngu.com",uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class PaymentControllerDocsTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private User testUser;


    @BeforeEach
    void clean(){
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        paymentRepository.deleteAll();
        userRepository.deleteAll();
        testUser = User.builder()
                .name("테스트유저")
                .roles(Collections.singletonList("ROLE_USER")).build();
        userRepository.save(testUser);
    }
    @Test
    @DisplayName("페이 정보")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getPayment() throws Exception{

        Payment payment = Payment.builder()
                .balance(100000L)
                .user(testUser).build();
        paymentRepository.save(payment);

        this.mockMvc.perform(get("/payment")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment/info"
                        , responseFields(
                                fieldWithPath("walletId").description("지갑 ID"),
                                fieldWithPath("balance").description("지갑 잔액")
                        )
                ));
    }
    @Test
    @DisplayName("페이 충전")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void chargePayment() throws Exception{
        Payment payment = Payment.builder()
                .balance(100000L)
                .user(testUser).build();
        paymentRepository.save(payment);

        Account account = Account.builder()
                .id(12345L)
                .balance(100000L)
                .user(testUser).build();
        accountRepository.save(account);

        PaymentCharge paymentCharge = PaymentCharge.builder()
                .account("계좌1")
                .requestCoin(50000L).build();

        this.mockMvc.perform(post("/payment/charge")
                        .content(objectMapper.writeValueAsString(paymentCharge))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment/charge"
                        , requestFields(
                                fieldWithPath("account").description("계좌 이름"),
                                fieldWithPath("requestCoin").description("충전 금액")
                        )
                ));
    }
//
//    @Test
//    @DisplayName("페이 반환")
//    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    void dischargePayment() throws Exception{
//
//        Payment payment = Payment.builder()
//                .balance(100000L)
//                .user(testUser).build();
//        paymentRepository.save(payment);
//
//        Account account = Account.builder()
//                .id(12345L)
//                .balance(100000L)
//                .user(testUser).build();
//        accountRepository.save(account);
//
//        PaymentCharge paymentCharge = PaymentCharge.builder()
//                .account("계좌1")
//                .requestCoin(50000L).build();
//
//        this.mockMvc.perform(post("/payment/discharge")
//                        .content(objectMapper.writeValueAsString(paymentCharge))
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("payment/discharge"
//                        , requestFields(
//                                fieldWithPath("account").description("계좌 이름"),
//                                fieldWithPath("requestCoin").description("반환 금액")
//                        )
//                ));
//    }
//    @Test
//    @DisplayName("페이 송금")
//    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    void remitPayment() throws Exception{
//        User user = User.builder()
//                .name("유저").build();
//        userRepository.save(user);
//        Payment paymentTest = Payment.builder()
//                .balance(100000L)
//                .user(testUser).build();
//        paymentRepository.save(paymentTest);
//        Payment payment = Payment.builder()
//                .balance(100000L)
//                .user(user).build();
//        paymentRepository.save(payment);
//
//        RemitRequest request = RemitRequest.builder()
//                .to(user.getId())
//                .amount(30000L).build();
//
//        mockMvc.perform(post("/payment/remit")
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("payment/remit"
//                        , requestFields(
//                                fieldWithPath("to").description("송금 상대 유저 ID"),
//                                fieldWithPath("amount").description("송금 금액")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("거래내역 가져오기")
//    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    void getTransaction() throws Exception{
//        User user = User.builder()
//                .name("유저").build();
//        userRepository.save(user);
//        Payment paymentTest = Payment.builder()
//                .balance(100000L)
//                .user(testUser).build();
//        paymentRepository.save(paymentTest);
//        Payment payment = Payment.builder()
//                .balance(100000L)
//                .user(user).build();
//        paymentRepository.save(payment);
//
//        LocalDateTime now = LocalDateTime.now();
//        List<Transaction> transactions = IntStream.range(0,10)
//                .mapToObj(i->Transaction.builder()
//                        .from(testUser)
//                        .to(user)
//                        .date(now)
//                        .amount(1000L + i * 1000)
//                        .build()).collect(Collectors.toList());
//        transactionRepository.saveAll(transactions);
//
//        this.mockMvc.perform(get("/payment/transaction?filter=1&order=1&start={s}&end={e}",
//                        now.minusDays(3),now)
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("payment/transaction"
//                        , pathParameters(
//                                parameterWithName("filter").description("송금 상대 유저 ID").optional()
//                                        .attributes(key("constraint").value("입력 x: 모두, 1:보낸 내역, 2: 받은 내역")),
//                                parameterWithName("order").description("순서").optional()
//                                        .attributes(key("constraint").value("입력 x: 최신순, 1: 금액 순")),
//                                parameterWithName("start").description("검색 범위 시작 날짜").optional()
//                                        .attributes(key("constraint").value("LocalDateTime 형식, 입력하지 않으면 필터 없음")),
//                                parameterWithName("start").description("검색 범위 종료 날짜").optional()
//                                        .attributes(key("constraint").value("LocalDateTime 형식, 입력하지 않으면 현시간까지"))
//                        )
//                        , responseFields(
//                                fieldWithPath("[].id").description("거래내역 ID"),
//                                fieldWithPath("[].fromName").description("송금자 이름"),
//                                fieldWithPath("[].toName").description("수신자 이름"),
//                                fieldWithPath("[].amount").description("거래 금액"),
//                                fieldWithPath("[].time").description("거래 시간")
//                        )
//                ));
//    }
}
