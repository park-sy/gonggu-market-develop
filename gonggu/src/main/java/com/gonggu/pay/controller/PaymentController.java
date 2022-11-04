package com.gonggu.pay.controller;

import com.gonggu.pay.domain.User;
import com.gonggu.pay.request.PaymentCharge;
import com.gonggu.pay.request.RemitRequest;
import com.gonggu.pay.request.TransactionRequest;
import com.gonggu.pay.request.UserTemp;
import com.gonggu.pay.response.PaymentInfo;
import com.gonggu.pay.response.TransactionResponse;
import com.gonggu.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    //페이 정보 불러오기
    @GetMapping("/payment")
    public PaymentInfo getInfo(@ModelAttribute UserTemp userTemp){
        //로그인 처리 위함, 나중에 삭제
        User user = paymentService.findUserTemp(userTemp);
        return paymentService.getInfo(user);
    }
    //코인 충전
    @PostMapping("/payment/charge")
    public void chargeCoin(@ModelAttribute UserTemp userTemp, @RequestBody PaymentCharge paymentCharge){
        //로그인 처리 위함, 나중에 삭제
        User user = paymentService.findUserTemp(userTemp);

        paymentService.charge(paymentCharge, user);
    }
    //코인 반환
    @PostMapping("/payment/discharge")
    public void dischargeCoin(@ModelAttribute UserTemp userTemp, @RequestBody PaymentCharge paymentCharge){
        //로그인 처리 위함, 나중에 삭제
        User user = paymentService.findUserTemp(userTemp);
        paymentService.discharge(paymentCharge, user);
    }
    //코인 송금
    @PostMapping("/payment/remit")
    public void remit(@RequestBody RemitRequest request){
        //로그인 처리 위함, 나중에 삭제
//        User user = paymentService.findUserTemp(userTemp);
        paymentService.remit(request);
    }

    @GetMapping("/payment/transaction")
    public List<TransactionResponse> getMyTransaction(@ModelAttribute TransactionRequest transactionRequest,
                                                      @RequestBody User user){
        return paymentService.getMyTransaction(transactionRequest, user);
    }

}
