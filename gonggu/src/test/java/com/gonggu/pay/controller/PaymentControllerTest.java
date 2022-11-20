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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

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
                .nickname("테스트유저")
                .email("test@test.com")
                .password("password")
                .roles(Collections.singletonList("ROLE_USER")).build();
        userRepository.save(testUser);
    }
    @Test
    @DisplayName("페이 정보")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getPayment() throws Exception{

        Payment payment = Payment.builder()
                .balance(100000L)
                .user(testUser).build();
        paymentRepository.save(payment);

        mockMvc.perform(get("/payment")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("페이 충전")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

        mockMvc.perform(post("/payment/charge")
                        .content(objectMapper.writeValueAsString(paymentCharge))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/payment")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150000L))
                .andDo(print());
    }
    @Test
    @DisplayName("페이 반환")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void dischargePayment() throws Exception{

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


        mockMvc.perform(post("/payment/discharge")
                        .content(objectMapper.writeValueAsString(paymentCharge))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/payment")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50000L))
                .andDo(print());

    }
    @Test
    @DisplayName("페이 송금")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void remitPayment() throws Exception{
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);
        Payment paymentTest = Payment.builder()
                .balance(100000L)
                .user(testUser).build();
        paymentRepository.save(paymentTest);
        Payment payment = Payment.builder()
                .balance(100000L)
                .user(user).build();
        paymentRepository.save(payment);

        RemitRequest request = RemitRequest.builder()
                .to(user.getNickname())
                .amount(30000L).build();

        mockMvc.perform(post("/payment/remit")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/payment")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(70000L))
                .andDo(print());
    }

    @Test
    @DisplayName("거래내역 가져오기")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getTransaction() throws Exception{
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);
        Payment paymentTest = Payment.builder()
                .balance(100000L)
                .user(testUser).build();
        paymentRepository.save(paymentTest);
        Payment payment = Payment.builder()
                .balance(100000L)
                .user(user).build();
        paymentRepository.save(payment);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = IntStream.range(0,10)
                .mapToObj(i->Transaction.builder()
                        .from(testUser)
                        .to(user)
                        .date(now)
                        .amount(1000L + i * 1000)
                        .build()).collect(Collectors.toList());
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/payment/transaction")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


}