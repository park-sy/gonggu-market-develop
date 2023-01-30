<template></template>
<script setup>
import useAxios from "@/modules/axios";
import { useRouter, useRoute } from "vue-router";
import { useUserInfoStore } from "/@stores/userInfo";

const userStore = useUserInfoStore();
const { axiosGet, axiosPost } = useAxios();
const router = useRouter();
const route = useRoute();

function onPayCreateFail(resp) {
  console.log("Pay create error", resp);
}

function onSuccess(resp) {
  if (route.query.to !== undefined && route.query.amount !== undefined) {
    router.push({ name: "paymentMain", query: { to: route.query.to, amount: route.query.amount } });
  } else {
    router.push("/payment");
  }
}

function onFail(resp) {
  if (confirm("지갑 정보가 없습니다. 새로 생성하시겠습니까?")) {
    axiosPost("https://api.09market.site/payment", userStore.JWT, null, onSuccess, onPayCreateFail);
  } else {
    router.push("/");
  }
}

function checkPayment() {
  console.log(userStore.JWT);
  axiosGet("https://api.09market.site/payment", userStore.JWT, null, onSuccess, onFail);
}

checkPayment();
</script>
