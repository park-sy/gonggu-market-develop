<template>
  <main class="mt-3">
    <div class="container">
      <div class="row mb-2">
        <div class="col-12">
          <select class="form-select">
            <option
              v-for="(name, i) in categories"
              :key="i"
              v-text="name"
              :value="i"
            ></option>
          </select>
        </div>
      </div>
      <div class="row g-3">
        <div
          class="col-xl-3 col-lg-4 col-md-6"
          :key="i"
          v-for="(deal, i) in dealList"
        >
          <div class="card" style="width: 18rem">
            <img :src="getImageUrl(deal)" class="card-img-top" alt="..." />
            <div class="card-body">
              <h5 class="card-title">{{ deal.title }}</h5>
              <p class="card-text">
                <span class="badge bg-dark me-1">{{ deal.category }}</span>
                <span class="badge bg-danger me-1"
                  >{{ deal.nowCount }}/{{ deal.totalCount }}명</span
                >
                <span class="badge bg-warning me-1">{{
                  leftDays(deal.remainDate)
                }}</span>
              </p>
              <div class="d-flex justify-content-between align-items-center">
                <div class="btn-group" role="group" aria-label="Basic example">
                  <router-link :to="{ name: 'Detail', query: { id: deal.id } }">
                    <button
                      type="button"
                      class="btn btn-sm btn-outline-secondary"
                    >
                      공구 참여
                    </button>
                  </router-link>
                </div>
                <small class="text-dark"
                  >1인당
                  {{
                    new Intl.NumberFormat("ko").format(deal.unitPrice)
                  }}원</small
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import useAxios from "@/modules/axios";
import { onBeforeMount, ref } from "vue";
import { categories, units } from "@/modules/selectData";
import { useUserInfoStore } from "/@stores/userInfo";
import { checkIfSubscribed } from "@/modules/pushRegister";

const userStore = useUserInfoStore();
const { axiosGet, axiosPost } = useAxios();

const dealList = ref([]);
const categorySelected = ref(-1);

const saveResult = function (respData) {
  dealList.value = respData;
  console.log("Get success");
};

onBeforeMount(async () => {
  await axiosGet(
    "https://api.09market.site/deal",
    userStore.JWT,
    null,
    saveResult
  );
});

const getImageUrl = (name) => {
  let fileName;
  if (name.deleted) {
    fileName = "deleted.jpg";
  } else if (name.expired) {
    fileName = "closed.jpg";
  } else {
    fileName = name.image.fileName;
  }
  return `https://gonggubucket.s3.ap-northeast-2.amazonaws.com/${fileName}`;
};

function leftDays(remainDate) {
  if (remainDate >= 1) {
    return remainDate + "일 뒤 마감";
  } else if (remainDate == 0) {
    return "오늘 마감";
  } else {
    return "마감된 공구";
  }
}

if ("serviceWorker" in navigator) {
  console.log("😊 Service Worker in navigator");
}

if (Notification.permission !== "granted") {
  console.log("😢 Push service not yet granted");
  Notification.requestPermission((result) => {
    if (result !== "granted") {
      console.log("🤢 User deined push service");
    } else {
      console.log("👏👏 Push service granted by user");
      checkIfSubscribed(userStore.userNick, userStore.JWT);
    }
  });
} else {
  checkIfSubscribed(userStore.userNick, userStore.JWT);
}
</script>

<style scoped>
.card-img-top {
  height: 15rem;
  object-fit: contain;
  padding: 0.5rem;
}
.card {
  margin-bottom: 20px;
}
</style>
