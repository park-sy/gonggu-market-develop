<template>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th></th>
        <th>공구명</th>
        <th>구매가격</th>
        <th>구매수량</th>
        <th>마감일자</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(product, i) in productList" :key="i">
        <td style="width: 100px"><img :src="getImageUrl(product)" style="height: 50px; width: auto" /></td>
        <td>
          <router-link :to="{ name: 'Detail', query: { id: product.id } }">
            {{ product.title }}
          </router-link>
        </td>
        <td>{{ new Intl.NumberFormat("ko").format(product.unitPrice * product.userCount) }}원</td>
        <td>{{ (product.quantity / product.totalCount) * product.userCount }}{{ product.unit }}</td>
        <td>{{ parseToYMD(product.expiredDate) }}</td>
        <td style="width: 300px" v-if="!product.deleted">
          <el-button type="success" plain @click="confirmTransaction(product)">송금하기</el-button>
          <el-button type="danger" plain @click="confirmDelete(product.id)" v-if="!product.expired">공구취소</el-button>
        </td>
      </tr>
    </tbody>
  </table>
</template>
<script setup>
import useAxios from "@/modules/axios";
import { useUserInfoStore } from "/@stores/userInfo";
import { useRouter } from "vue-router";
import * as moment_ from "moment";
import { parseToYMD } from "@/modules/dayParser";

const moment = moment_;
const userStore = useUserInfoStore();
const { axiosDelete } = useAxios();
const router = useRouter();
const props = defineProps({
  productList: Array,
});

let sendAmount; //송금을 할 때, 얼마를 줄 지 임시로 저장해놓는 변수

const getImageUrl = (name) => {
  let fileName;
  if (name.deleted) {
    fileName = "deleted.jpg";
  } else {
    fileName = name.image.fileName;
  }
  return `https://gonggubucket.s3.ap-northeast-2.amazonaws.com/${fileName}`;
};

function onDeleteSuccess(resp) {
  router.push("/");
}

function onDeleteFail(resp) {
  alert("공구 참여 철회에 실패했습니다.");
}

function confirmTransaction(product) {
  if (confirm("실물 거래 이전에 먼저 송금하는 것은 위험합니다.\n계속 진행하시겠습니까?")) {
    sendAmount = product.unitPrice * product.userCount;
    router.push({ name: "paymentCheck", query: { to: product.hostName, amount: sendAmount } });
  }
}

function confirmDelete(dealId) {
  if (confirm("공구 참여를 취소하시겠습니까?")) {
    axiosDelete(`https://api.09market.site/deal/${dealId}/enrollment`, userStore.JWT, onDeleteSuccess, onDeleteFail);
  }
}
</script>
<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700&display=swap");
.text-center {
  font-family: "Noto Sans KR", sans-serif;
}
.card-img-top {
  height: 15rem;
  object-fit: contain;
  padding: 0.5rem;
}
.card {
  margin-bottom: 20px;
}
td {
  vertical-align: middle !important;
}
</style>
