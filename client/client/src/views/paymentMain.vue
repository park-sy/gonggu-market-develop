<template>
  <div class="container col-xl-10 col-xxl-8 px-4 py-0 py-md-5">
    <div class="row align-items-center g-lg-5 py-5">
      <div class="col-lg-7 text-center text-lg-start">
        <h1 class="display-4 fw-bold lh-1 mb-3">우리동네 공구페이</h1>
        <div class="h-100 p-5 mt-5 text-bg-dark rounded-3">
          <h2><img src="../assets/savings_FILL0_wght400_GRAD0_opsz48.svg" alt="" /> 현재 잔액</h2>
          <h1>{{ new Intl.NumberFormat("ko").format(balance) }}원</h1>
        </div>
        <div class="row my-3 g-2">
          <div class="col-12 col-md-6">
            <button class="w-100 btn btn-lg btn-secondary" @click="charge">충전하기</button>
          </div>
          <div class="col-12 col-md-6">
            <button class="w-100 btn btn-lg btn-secondary" @click="discharge">환전하기</button>
          </div>
        </div>
      </div>
      <div class="col-md-10 mx-auto col-lg-5">
        <form class="p-4 px-md-5 pt-md-0 border rounded-3 bg-light" @submit.prevent="doTransaction">
          <h3 class="pb-3 pt-md-4 pb-md-3" style="font-weight: bold">송금하기</h3>
          <div class="form-floating mb-3">
            <input type="text" class="form-control" id="floatingReceiver" placeholder="송금받는 자" v-model="receiver" />
            <label for="floatingReceiver">송금받는 자</label>
          </div>
          <div class="form-floating mb-3">
            <input type="text" class="form-control" id="floatingAmount" placeholder="금액" @input="checkPrice" v-model="amount" />
            <label for="floatingAmount">금액</label>
          </div>
          <button class="w-100 btn btn-lg btn-dark" type="submit" v-if="receiver === userStore.userNick" disabled>자신한테 송금할 수 없습니다</button>
          <button class="w-100 btn btn-lg btn-dark" type="submit" v-else-if="amount > balance" disabled>잔액이 부족합니다</button>
          <button class="w-100 btn btn-lg btn-dark" type="submit" v-else>송금하기</button>
          <hr class="my-4" />
          <small class="text-muted">우리동네 공구마켓은 회원간의 송금에 책임을 지지 않으며, 실물 거래 현장에서 송금할 것을 강력히 권장합니다.</small>
        </form>
      </div>
    </div>
    <transaction-list :transactionList="history" />
  </div>
</template>
<script setup>
import { ref } from "vue";
import useAxios from "@/modules/axios";
import { useRouter, useRoute } from "vue-router";
// import { ElMessage, ElMessageBox } from "element-plus";
import { useUserInfoStore } from "/@stores/userInfo";
import transactionList from "./transactionList.vue";

const userStore = useUserInfoStore();
const { axiosGet, axiosPost } = useAxios();
const router = useRouter();
const route = useRoute();

const balance = ref(0);
const receiver = ref("");
const amount = ref("");
const history = ref([]);

function onBalanceSuccess(resp) {
  balance.value = resp.balance;
}
function onFail(resp) {
  alert("데이터를 불러오는데 실패했습니다");
  console.log(resp);
}

function onTransactionSuccess(resp) {
  alert("송금을 성공했습니다");
  router.push("/payment/check");
}

function onTransactionFail(resp) {
  alert(resp);
}

function onHistorySuccess(respData) {
  history.value = respData;
  console.log(history.value);
}

function getBalanceandHistory() {
  axiosGet("https://api.09market.site/payment", userStore.JWT, null, onBalanceSuccess, onFail);
  axiosGet("https://api.09market.site/payment/transaction", userStore.JWT, null, onHistorySuccess, onFail);
}

function checkPrice(e) {
  e.target.value = e.target.value.replace(/[^0-9]/g, "");
}

function doTransaction() {
  if (receiver.value === "" || amount.value === "" || amount.value <= 0) {
    alert("유효하지 않은 송금 정보입니다. 입력값을 다시 확인해주세요");
  } else if (confirm(`송금받는 자 : ${receiver.value}\n금액 : ${new Intl.NumberFormat("ko").format(amount.value)}원\n\n 위 정보가 맞으면 확인 버튼을 누르세요`)) {
    const payload = {
      to: receiver.value,
      amount: amount.value,
    };
    axiosPost("https://api.09market.site/payment/remit", userStore.JWT, payload, onTransactionSuccess, onTransactionFail);
  }
}

//코인 충전
function charge() {
  ElMessageBox.prompt("충전하실 금액을 입력해주세요", "공구 페이 충전", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    inputPattern: /^[0-9]+$/,
    inputErrorMessage: "숫자만 입력 가능합니다",
  })
    .then(({ value }) => {
      ElMessageBox.confirm(`${new Intl.NumberFormat("ko").format(value)}원을 충전합니다.`, "공구 페이 충전", {
        confirmButtonText: "확인",
        cancelButtonText: "취소",
        type: "success",
      })
        .then(() => {
          const payload = {
            requestCoin: value,
          };
          axiosPost("https://api.09market.site/payment/charge", userStore.JWT, payload, onChargeSuccess, onChargeFail);
        })
        .catch(() => {
          ElMessage({
            type: "info",
            message: "충전이 취소됐습니다",
          });
        });
    })
    .catch(() => {
      ElMessage({
        type: "info",
        message: "충전이 취소됐습니다",
      });
    });
}

function onChargeSuccess() {
  ElMessage({
    type: "success",
    message: `충전이 완료되었습니다`,
  });
  setTimeout(() => {
    router.push("/payment/check");
  }, 500);
}

function onChargeFail(resp) {
  ElMessage({
    type: "error",
    message: "오류가 발생했습니다. 다시 시도해주세요.",
  });
}

//코인 환전
function discharge() {
  ElMessageBox.prompt("환전하실 금액을 입력해주세요", "공구 페이 환전", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    inputPattern: /^[0-9]+$/,
    inputErrorMessage: "숫자만 입력 가능합니다",
  })
    .then(({ value }) => {
      ElMessageBox.confirm(`${new Intl.NumberFormat("ko").format(value)}원을 환전합니다.`, "공구 페이 환전", {
        confirmButtonText: "확인",
        cancelButtonText: "취소",
        type: "success",
      })
        .then(() => {
          const payload = {
            requestCoin: value,
          };
          axiosPost("https://api.09market.site/payment/discharge", userStore.JWT, payload, onDischargeSuccess, onDischargeFail);
        })
        .catch(() => {
          ElMessage({
            type: "info",
            message: "환전이 취소됐습니다",
          });
        });
    })
    .catch(() => {
      ElMessage({
        type: "info",
        message: "환전이 취소됐습니다",
      });
    });
}

function onDischargeSuccess() {
  ElMessage({
    type: "success",
    message: `환전이 완료되었습니다`,
  });
  setTimeout(() => {
    router.push("/payment/check");
  }, 500);
}

function onDischargeFail(resp) {
  ElMessage({
    type: "error",
    message: "오류가 발생했습니다. 금액이 정확한지 확인 해주세요.",
  });
}

//onCreate
getBalanceandHistory();
receiver.value = route.query.to === undefined ? "" : route.query.to;
amount.value = route.query.amount === undefined ? "" : route.query.amount;
</script>
<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700&display=swap");
* {
  font-family: "Noto Sans KR", sans-serif;
}
</style>
