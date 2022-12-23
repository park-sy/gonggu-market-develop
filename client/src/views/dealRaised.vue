<template>
  <main class="mt-3 text-center">
    <div class="container">
      <h2 class="text-center mt-4 mb-1">
        {{ title === true ? "내가 개시한 공구" : "내가 참여한 공구" }}<br />
        <el-switch v-model="title" class="mb-2" style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" active-text="내가 개시한 공구" inactive-text="내가 참여한 공구" />
      </h2>
      <div class="float-end mb-1">
        <router-link to="/create">
          <button type="button" class="btn btn-dark mb-2">공구 개시</button>
        </router-link>
      </div>
      <my-created-deal :productList="myCreatedDealList" v-if="title" :showControls="true" />
      <my-enrolled-deal :productList="myJoinedDealList" v-if="!title" />
    </div>
  </main>
</template>

<script setup>
import useAxios from "@/modules/axios";
import myCreatedDeal from "./myCreatedDeal.vue";
import myEnrolledDeal from "./myEnrolledDeal.vue";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserInfoStore } from "/@stores/userInfo";

const userStore = useUserInfoStore();
const { axiosGet, axiosPost, axiosDelete } = useAxios();
const router = useRouter();

const myCreatedDealList = ref([]);
const myJoinedDealList = ref([]);
const title = ref(false); //false : 내가 참여한 공구 / true : 내가 개시한 공구

const saveCreatedResult = function (respData) {
  myCreatedDealList.value = respData;
};

const saveJoinedResult = function (respData) {
  console.log(respData);
  myJoinedDealList.value = respData;
};

function getProductList() {
  axiosGet(`https://api.09market.site/deal/sale/${userStore.userNick}`, userStore.JWT, null, saveCreatedResult);
  axiosGet(`https://api.09market.site/deal/enrollment/${userStore.userNick}`, userStore.JWT, null, saveJoinedResult);
}

const getImageUrl = (name) => {
  return `https://gonggubucket.s3.ap-northeast-2.amazonaws.com/${name}`;
};

//금액을 #,### 형태로 포맷팅한다.
function formatCurrency(value) {
  return currencyFormat(value);
}

function formatTime(value) {
  var temp = value.split("T");
  return temp[0];
}

getProductList(); //script setup에서는 created를 안 써도 된다.
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
  width: 30px;
}
</style>
