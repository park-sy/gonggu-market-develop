<template>
  <main class="mt-3 text-center">
    <div class="container">
      <h2 class="text-center my-4">{{ route.query.seller }}의 공구<br /></h2>
      <my-created-deal :productList="myCreatedDealList" :showControls="false" />
    </div>
  </main>
</template>

<script setup>
import { ref } from "vue";
import { useRoute } from "vue-router";
import useAxios from "@/modules/axios";
const { axiosGet } = useAxios();
import { useUserInfoStore } from "/@stores/userInfo";

const userStore = useUserInfoStore();

import myCreatedDeal from "./myCreatedDeal.vue";

const route = useRoute();
const myCreatedDealList = ref([]);

const saveCreatedResult = function (respData) {
  myCreatedDealList.value = respData;
};

function getProductList() {
  axiosGet(`https://api.09market.site/deal/sale/${route.query.seller}`, userStore.JWT, null, saveCreatedResult);
}

getProductList(); //script setup에서는 created를 안 써도 된다.
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700&display=swap");
* {
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
