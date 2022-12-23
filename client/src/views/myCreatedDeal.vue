<template>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th></th>
        <th>공구명</th>
        <th>공구가격</th>
        <th>모집현황</th>
        <th>잔여일</th>
        <th v-if="showControls"></th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(product, i) in productList" :key="i">
        <td style="width: 100px"><img :src="getImageUrl(product)" style="height: 50px; width: auto" /></td>
        <td style="width: 300px">
          <router-link :to="{ name: 'Detail', query: { id: product.id } }">
            {{ product.title }}
          </router-link>
        </td>
        <td>{{ new Intl.NumberFormat("ko").format(product.unitPrice * product.totalCount) }}원</td>
        <td>{{ product.nowCount }} / {{ product.totalCount }}</td>
        <td>{{ product.remainDate > 0 ? `${product.remainDate}일` : product.remainDate === 0 ? "오늘 마감" : "마감" }}</td>
        <td v-if="showControls">
          <div v-if="!product.deleted">
            <router-link :to="{ name: 'Update', query: { deal_id: product.id } }" v-if="!product.expired" class="me-3" style="text-decoration: none; color: inherit">
              <el-button type="success" plain>수정하기</el-button>
            </router-link>
            <el-button type="danger" plain @click="confirmDelete(product.id)">삭제하기</el-button>
          </div>
        </td>
      </tr>
    </tbody>
  </table>
</template>
<script setup>
import useAxios from "@/modules/axios";
import { useUserInfoStore } from "/@stores/userInfo";
import { useRouter } from "vue-router";

const userStore = useUserInfoStore();
const { axiosDelete } = useAxios();
const router = useRouter();
const props = defineProps({
  productList: Array,
  showControls: Boolean,
});
const getImageUrl = (name) => {
  let fileName;
  if (name.deleted) {
    fileName = "deleted.jpg";
  } else {
    fileName = name.image.fileName;
  }
  return `https://gonggubucket.s3.ap-northeast-2.amazonaws.com/${fileName}`;
};
function formatTime(value) {
  var temp = value.split("T");
  return temp[0];
}
function onSuccess(resp) {
  alert("공구가 삭제되었습니다.");
  router.push("/");
}

//공구 삭제에 대한 사용자 프롬프트
const confirmDelete = (productId) => {
  if (confirm("공구를 삭제할까요?")) {
    axiosDelete(`https://api.09market.site/deal/${productId}`, userStore.JWT, onSuccess);
  }
};
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
