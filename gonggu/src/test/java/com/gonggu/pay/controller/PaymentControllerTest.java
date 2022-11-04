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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

    @BeforeEach
    void clean(){
        transactionRepository.deleteAll();
        userRepository.deleteAll();
        accountRepository.deleteAll();
        paymentRepository.deleteAll();
    }
    @Test
    @DisplayName("페이 정보")
    void getPayment() throws Exception{
        User user = User.builder()
                .name("유저").build();
        userRepository.save(user);

        Payment payment = Payment.builder()
                .balance(100000L)
                .user(user).build();
        paymentRepository.save(payment);

        mockMvc.perform(get("/payment?id={id}",user.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("제목19"))
                .andDo(print());
    }

    @Test
    @DisplayName("페이 충전")
    void chargePayment() throws Exception{
        User user = User.builder()
                .name("유저").build();
        userRepository.save(user);

        Payment payment = Payment.builder()
                .balance(100000L)
                .user(user).build();
        paymentRepository.save(payment);

        Account account = Account.builder()
                .id(12345L)
                .balance(100000L)
                .user(user).build();
        accountRepository.save(account);

        PaymentCharge paymentCharge = PaymentCharge.builder()
                .account("계좌1")
                .requestCoin(50000L).build();

        mockMvc.perform(post("/payment/charge?id={id}",user.getId())
                        .content(objectMapper.writeValueAsString(paymentCharge))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/payment?id={id}",user.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150000L))
                .andDo(print());
    }
    @Test
    @DisplayName("페이 반환")
    void dischargePayment() throws Exception{
        User user = User.builder()
                .name("유저").build();
        userRepository.save(user);

        Payment payment = Payment.builder()
                .balance(100000L)
                .user(user).build();
        paymentRepository.save(payment);

        Account account = Account.builder()
                .id(12345L)
                .balance(100000L)
                .user(user).build();
        accountRepository.save(account);

        PaymentCharge paymentCharge = PaymentCharge.builder()
                .account("계좌1")
                .requestCoin(50000L).build();

        mockMvc.perform(post("/payment/discharge?id={id}",user.getId())
                        .content(objectMapper.writeValueAsString(paymentCharge))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/payment?id={id}",user.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50000L))
                .andDo(print());

    }
    @Test
    @DisplayName("페이 송금")
    void remitPayment() throws Exception{
        List<User> users = IntStream.range(0,2)
                .mapToObj(i -> User.builder()
                        .name("이름" +i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        List<Payment> payments = IntStream.range(0,2)
                .mapToObj(i -> Payment.builder()
                        .balance(100000L)
                        .user(users.get(i)).build()).collect(Collectors.toList());
        paymentRepository.saveAll(payments);

        RemitRequest request = RemitRequest.builder()
                .from(users.get(0).getId())
                .to(users.get(1).getId())
                .amount(30000L).build();

        mockMvc.perform(post("/payment/remit")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/payment?id={id}",users.get(0).getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(70000L))
                .andDo(print());

        mockMvc.perform(get("/payment?id={id}",users.get(1).getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(130000L))
                .andDo(print());
    }

    @Test
    @DisplayName("거래내역 가져오기")
    void getTransaction() throws Exception{
        List<User> users = IntStream.range(0,2)
                .mapToObj(i -> User.builder()
                        .name("이름" +i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        List<Payment> payments = IntStream.range(0,2)
                .mapToObj(i -> Payment.builder()
                        .balance(100000L)
                        .user(users.get(i)).build()).collect(Collectors.toList());
        paymentRepository.saveAll(payments);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = IntStream.range(0,10)
                        .mapToObj(i->Transaction.builder()
                                .from(users.get(0))
                                .to(users.get(1))
                                .date(now)
                                .amount(1000L + i * 1000)
                                .build()).collect(Collectors.toList());
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/payment/transaction")
                        .content(objectMapper.writeValueAsString(users.get(0)))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("송금 후 거래내역")
    void getTransactionAfterRemit() throws Exception{
        List<User> users = IntStream.range(0,2)
                .mapToObj(i -> User.builder()
                        .name("이름" +i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        List<Payment> payments = IntStream.range(0,2)
                .mapToObj(i -> Payment.builder()
                        .balance(100000L)
                        .user(users.get(i)).build()).collect(Collectors.toList());
        paymentRepository.saveAll(payments);

        RemitRequest request = RemitRequest.builder()
                .from(users.get(0).getId())
                .to(users.get(1).getId())
                .amount(30000L).build();

        mockMvc.perform(post("/payment/remit")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/payment/transaction")
                        .content(objectMapper.writeValueAsString(users.get(0)))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}