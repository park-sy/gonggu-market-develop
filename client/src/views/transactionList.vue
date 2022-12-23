<template>
  <table class="table table-bordered d-none d-lg-table">
    <thead>
      <tr>
        <th>거래시각</th>
        <th>구분</th>
        <th>보낸분/받는분</th>
        <th>금액</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(transaction, i) in transactionList" :key="i">
        <td>{{ formatTime(transaction.time) }}</td>
        <td>
          <el-tag :type="'info'" class="mx-1" effect="dark" v-if="userStore.userNick === transaction.toName"> 입금 </el-tag>
          <el-tag :type="'info'" class="mx-1" effect="plain" v-else> 송금 </el-tag>
        </td>
        <td>{{ userStore.userNick === transaction.toName ? transaction.fromName : transaction.toName }}</td>
        <td>{{ new Intl.NumberFormat("ko").format(transaction.amount) }}원</td>
      </tr>
    </tbody>
  </table>
  <div class="h-100 py-5 px-2 mt-1 mb-5 text-bg-light rounded-3 d-lg-none text-center">상세 거래 내역은 PC에서 확인할 수 있습니다.</div>
</template>
<script setup>
import useAxios from "@/modules/axios";
import { useUserInfoStore } from "/@stores/userInfo";
import * as moment_ from "moment";
import { useRouter } from "vue-router";
import { parseToYMDHmS } from "@/modules/dayParser";

const moment = moment_;
const userStore = useUserInfoStore();
const { axiosDelete } = useAxios();
const router = useRouter();
const props = defineProps({
  transactionList: Array,
});

function formatTime(value) {
  return parseToYMDHmS(value);
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
th,
td {
  vertical-align: middle !important;
  text-align: center;
}
</style>
