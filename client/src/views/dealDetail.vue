<template>
  <main class="mt-3">
    <div class="container">
      <div class="row">
        <div class="col-md-5">
          <div id="carouselExampleControls" class="carousel slide carousel-dark" data-bs-ride="carousel">
            <div class="carousel-inner">
              <div :class="`carousel-item ${i == 0 ? 'active' : ''}`" :key="i" v-for="(productImg, i) in productImage">
                <img :src="getImageUrl(productImg)" class="d-block w-100" alt="..." />
              </div>
            </div>
            <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleControls" data-bs-slide="prev">
              <span class="carousel-control-prev-icon" aria-hidden="true"></span>
              <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleControls" data-bs-slide="next">
              <span class="carousel-control-next-icon" aria-hidden="true"></span>
              <span class="visually-hidden">Next</span>
            </button>
          </div>
        </div>
        <div class="col-md-7">
          <div class="card shadow-sm">
            <div class="card-body">
              <h5 class="card-title col-auto">{{ productDetail.title }}</h5>
              <h5 class="card-title pt-3 pb-3 border-top">
                단가 : {{ new Intl.NumberFormat("ko").format(productDetail.unitPrice) }}원 | 구매 단위 : {{ new Intl.NumberFormat("ko").format(productDetail.unitQuantity) }}{{ productDetail.unit }}
              </h5>
              <p class="card-text border-top pt-3">
                <span class="badge bg-dark me-1">{{ productDetail.category }}</span>
                <span class="badge bg-danger me-1">{{ productDetail.nowCount }}/{{ productDetail.totalCount }}명</span>
                <span class="badge bg-warning me-1">{{ leftDays(productDetail.remainDate) }}</span>
              </p>
              <p class="card-text pb-3" style="font-weight: bolder; font-size: 1.2em">
                <img src="../assets/face_FILL1_wght400_GRAD0_opsz48.svg" alt="" />
                <router-link :to="{ name: 'sellerHistory', query: { seller: productDetail.user } }" style="color: black; text-decoration: none">
                  {{ productDetail.user }}
                </router-link>
                <a :href="productDetail.url" target="_blank" style="text-decoration: none"><el-button plain class="ms-3">구매 사이트로 이동</el-button></a>
              </p>
              <p class="card-text pb-3">{{ productDetail.content }}</p>
              <div class="card-text border-top border-bottom py-3">
                <div class="row">
                  <div class="col-auto">
                    <label class="col-form-label">구매수량</label>
                  </div>
                  <div class="col-auto">
                    <el-input-number
                      v-model="total"
                      size="large"
                      @change="calculatePrice"
                      :min="productDetail.unitQuantity"
                      :max="productDetail.totalCount === productDetail.nowCount ? productDetail.unitQuantity : productDetail.unitQuantity * (productDetail.totalCount - productDetail.nowCount)"
                      :step="productDetail.unitQuantity"
                      id="userPrice"
                      :key="totalUpdated"
                    />
                    {{ productDetail.unit }}
                  </div>
                </div>
              </div>
              <div class="row pt-3 pb-3">
                <div class="col-6">
                  <h3>총 상품 금액</h3>
                </div>
                <div class="col-6" style="text-align: right">
                  <h3 id="totalPrice">{{ new Intl.NumberFormat("ko").format(totalPrice) }}원</h3>
                </div>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <div class="col-12 d-grid p-1">
                  <button type="button" class="btn btn-lg btn-secondary" disabled v-if="outdated || deleted">공구가 마감되었습니다</button>
                  <button type="button" class="btn btn-lg btn-secondary" disabled v-else-if="reached">공구가 정원에 도달하였습니다</button>
                  <button type="button" class="btn btn-lg btn-secondary" disabled v-else-if="productDetail.user === userStore.userNick">자신의 공구에 참여할 수 없습니다</button>
                  <button type="button" class="btn btn-lg btn-danger" @click="enrollDeal" v-else>공구 참여</button>
                </div>
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
import { ref, onMounted, onBeforeMount, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { categories, units } from "@/modules/selectData";
const { axiosGet, axiosPost } = useAxios();
import { useUserInfoStore } from "/@stores/userInfo";

const userStore = useUserInfoStore();
const route = useRoute();
const router = useRouter();
let dealId = route.query.id;

let productDetail = ref({}); //현재 조회중인 제품에 대한 정보
let productImage = ref([]); //현재 조회중인 제품의 이미지 명(XXX.PNG)
let total = ref(0); //사용자가 구매하고자 하는 물건의 개수
let totalPrice = ref(0); //사용자의 총 구매 금액
let totalUpdated = ref(0); //사용자의 키보드 숫자 입력에 의해 구매 수량이 변했는지
let reached = ref(true); //공구 정원에 도달했는지
let deleted = ref(false); //삭제된 공구인지
let outdated = ref(false); //공구 참여 기간이 종료되었는지
let isGuest = ref(false); //공구를 개시한 사람이 아닌 사용자가 공구 페이지로 들어갔는지

//남은 날짜 계산
function leftDays(remainDate) {
  if (remainDate === undefined) return;

  if (remainDate >= 1) {
    return remainDate + "일 뒤 마감";
  } else if (remainDate == 0) {
    return "오늘 마감";
  } else {
    outdated.value = true;
    return "마감된 공구";
  }
}

//total 계산
function changeCount(cnt = "") {
  cnt === "+" ? total.value++ : total.value--;
  if (total.value < 1) total.value = 1;
  calculatePrice();
}

//totalPrice 계산
function calculatePrice() {
  if (total.value % productDetail.value.unitQuantity) {
    total.value = productDetail.value.unitQuantity;
    totalUpdated.value++;
  }
  totalPrice.value = (productDetail.value.unitPrice * total.value) / productDetail.value.unitQuantity;
}

//제품 상세 쿼리에 대한 콜백함수
const saveDetail = function (respData) {
  console.log(respData);
  productDetail.value = respData;
  total.value = productDetail.value.unitQuantity;
  totalPrice.value = productDetail.value.unitPrice;
  productDetail.value.category = productDetail.value.category.name;
  reached.value = productDetail.value.totalCount - productDetail.value.nowCount <= 0 ? true : false;
  deleted.value = productDetail.value.deleted;
  productDetail.value.url = parseURL(productDetail.value.url);
  productImage.value = productDetail.value.images.map((x) => {
    return x.fileName;
  });
};

// //제품 이미지 쿼리에 대한 콜백함수
// const saveImage = function (respData) {
//   productImage.value = respData.result.map((x) => x.path);
//   console.log(productImage.value);
// };

//제품 상세 쿼리
async function getProductDetail() {
  axiosGet(`https://api.09market.site/deal/${dealId}`, userStore.JWT, null, saveDetail);
}

//이미지를 실제 경로에서 가져온다.
const getImageUrl = (name) => {
  return `https://gonggubucket.s3.ap-northeast-2.amazonaws.com/${name}`;
};

//공구에 참여한다
function enrollDeal() {
  const payload = {
    quantity: total.value / productDetail.value.unitQuantity,
  };
  if (confirm(`공구에 참여하시겠습니까? 구매 단위를 다시 한번 확인하시기 바랍니다.\n\n구매단위 : ${payload.quantity}단위`)) {
    axiosPost(`https://api.09market.site/deal/${dealId}/enrollment`, userStore.JWT, payload, onDealEnrollSuccess, onDealEnrollFail);
  }
}

function onDealEnrollSuccess(resp) {
  alert("공구 참여에 성공했습니다.");
  router.push("/raised");
}

function onDealEnrollFail(resp) {
  alert("공구 참여에 실패했습니다.");
  console.log(resp);
}

function parseURL(url) {
  if (url.startsWith("http")) return url;
  return "http://" + url;
}

//onCreated
getProductDetail();
</script>
