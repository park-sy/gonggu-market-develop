<template>
  <main class="mt-3">
    <div class="container">
      <h2 class="text-center my-4">공구 등록</h2>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">공구 제목<sup class="requiredInput">*</sup></label>
        <div class="col-md-9">
          <input type="text" class="form-control form-control-sm" v-model="deal.title" placeholder="공구 제목을 입력해 주세요" />
          <small id="titleError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">모을 금액<sup class="requiredInput">*</sup></label>
        <div class="col-md-9">
          <div class="input-group input-group-sm">
            <input type="text" class="form-control" @input="checkPrice" @keyup="calculatePrice" />
            <span class="input-group-text">원</span>
          </div>
          <small id="priceError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">모일 인원<sup class="requiredInput">*</sup></label>
        <div class="col-md-9">
          <div class="input-group input-group-sm">
            <input id="numPeople" type="text" class="form-control" @input="checkPeople" v-model="deal.totalCount" @keyup="calculatePrice" />
            <span class="input-group-text">명</span>
          </div>
          <small id="totalCountError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">1인당 공구가격</label>
        <div class="col-md-9">
          <div class="input-group input-group-sm mb-3">
            <input type="text" class="form-control" v-model="deal.unitprice" disabled readonly />
            <span class="input-group-text">원</span>
          </div>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">구매 사이트<sup class="requiredInput">*</sup></label>
        <div class="col-md-9">
          <input type="text" class="form-control form-control-sm" v-model="deal.url" placeholder="구매 예정인 사이트 링크를 입력해주세요" />
          <small id="urlError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">공구 단위<sup class="requiredInput">*</sup></label>
        <div class="col-auto">
          <div class="input-group input-group-sm">
            <span class="input-group-text">1인당</span>
            <input type="text" class="form-control" v-model="deal.unitQuantity" />
            <select class="form-select from-select-sm" v-model="deal.unit">
              <option v-for="(name, i) in units" :key="i" v-text="name" :value="name"></option>
            </select>
          </div>
          <small id="unitQuantityError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">카테고리<sup class="requiredInput">*</sup></label>
        <div class="col-auto">
          <select class="form-select form-select-sm" v-model="deal.categoryId">
            <option v-for="(name, i) in categories" :key="i" v-text="name" :value="i"></option>
          </select>
          <small id="categoryIdError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">태그</label>
        <div class="col-md-9">
          <el-tag v-for="tag in deal.keywords" :key="tag" :type="'success'" class="mr-1" closable :disable-transitions="false" size="large" @close="handleClose(tag)">
            {{ tag }}
          </el-tag>
          <el-input v-if="inputVisible" ref="InputRef" v-model="inputValue" class="col-auto" @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" />
          <el-button v-else class="button-new-tag mr-1" @click="showInput"> + New Tag </el-button>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">공구 마감일<sup class="requiredInput">*</sup></label>
        <div class="col-md-9">
          <el-date-picker v-model="deal.expireTime" type="date" placeholder="Pick a day" :disabled-date="disabledDate" />
          <small id="expireTimeError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">썸네일 이미지<sup class="requiredInput">*</sup></label>
        <div class="col-md-9">
          <el-upload
            v-model:file-list="uploadedFile"
            class="upload-demo"
            drag
            action="https://api.09market.site/image"
            multiple
            name="image_upload"
            list-type="picture"
            :before-upload="beforeImageUpload"
            :limit="5"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">Drop file here or <em>click to upload</em></div>
            <template #tip>
              <div class="el-upload__tip">
                이미지 파일 당 최대 크기 : 5MB | 최대 5개 파일 업로드 가능<br />
                가장 첫 이미지가 대표 이미지가 됩니다.
              </div>
            </template>
          </el-upload>
          <small id="imagesError"></small>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">상품 설명<sup class="requiredInput">*</sup></label>
        <div class="col-md-9">
          <div class="mb-3">
            <textarea class="form-control" id="exampleFormControlTextarea1" rows="3" v-model="deal.content"></textarea>
            <small id="contentError"></small>
          </div>
        </div>
      </div>
      <div class="mb-3 row">
        <div class="col-6 d-grid p-1">
          <router-link to="/raised">
            <button type="button" class="col-12 btn btn-block btn-lg btn-dark">취소하기</button>
          </router-link>
        </div>
        <div class="col-6 d-grid p-1">
          <button type="button" @click="submitDeal" class="btn btn-lg btn-danger">저장하기</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { reactive, nextTick, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { UploadFilled } from "@element-plus/icons-vue";
import useAxios from "@/modules/axios";
import { categories, units } from "@/modules/selectData";
import { useUserInfoStore } from "/@stores/userInfo";

const userStore = useUserInfoStore();
const { axiosPost } = useAxios();
const router = useRouter();

const labelPosition = ref("top");

const newDeal = {
  title: "",
  price: "",
  totalCount: 2,
  unitprice: 0,
  nowCount: 1,
  url: "",
  unitQuantity: "",
  unit: "개",
  categoryId: 1,
  keywords: [],
  expireTime: "",
  images: [],
  content: "",
  createdby: userStore.userNick,
};

function checkPrice(e) {
  e.target.value = e.target.value.replace(/[^0-9]/g, "");
  deal.price = e.target.value;
}
function checkPeople(e) {
  e.target.value = e.target.value.replace(/[^0-9]/g, "");
  deal.totalCount = e.target.value;
}

onMounted(() => {
  const userPrice = document.getElementById("numPeople");
  numPeople.addEventListener("focusout", checkData);

  function checkData(event) {
    if (deal.totalCount < 2) {
      deal.totalCount = 2;
      deal.unitprice = Math.floor(deal.price / deal.totalCount);
    }
  }
});

const inputValue = ref("");
const inputVisible = ref(false);
const InputRef = ref({});
const categoryVisible = ref(false);
const categoryName = ref("카테고리를 선택해주세요");
const uploadedFile = ref([]);
const deal = reactive(newDeal);
let allIsWell = true; //모든 데이터의 정합성이 만족되었는가

const disabledDate = (time) => {
  const today = new Date();

  const year = today.getFullYear();
  const month = today.getMonth();
  const day = today.getDate();

  return time < new Date(year, month, day) || time > new Date(year, month, day + 7);
};

const handleClose = (tag) => {
  deal.keywords.splice(deal.keywords.indexOf(tag), 1);
};

const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    InputRef.value.input.focus();
  });
};

const handleInputConfirm = () => {
  if (inputValue.value) {
    deal.keywords.push(inputValue.value);
  }
  inputVisible.value = false;
  inputValue.value = "";
};

function beforeImageUpload(img) {
  const regex = /^image\/*/;
  if (!regex.test(img.type)) {
    ElMessage({
      message: "이미지 파일만 업로드 할 수 있습니다.",
      grouping: true,
      type: "error",
    });
    return false;
  } else if (img.size / 1024 / 1024 > 5) {
    ElMessage({
      message: "이미지 사이즈가 너무 큽니다.",
      grouping: true,
      type: "error",
    });
    return false;
  }
  return true;
}

const calculatePrice = (event) => {
  deal.unitprice = Math.floor(deal.price / deal.totalCount);
};

function showError(fieldName, message) {
  allIsWell = false;
  document.getElementById(fieldName + "Error").innerText = message;
}

function showSuccess(fieldName) {
  document.getElementById(fieldName + "Error").innerText = "";
}

function validateData() {
  console.log(newDeal);
  const toSkip = ["unitprice", "unit", "keywords", "createdby", "images", "nowCount"];

  Object.keys(newDeal).forEach((fieldName) => {
    if (toSkip.includes(fieldName)) return;
    if (newDeal[fieldName] == "" || newDeal[fieldName] == null) {
      //null : 날짜 입력했다가 지우기
      showError(fieldName, "필수 항목입니다");
    } else {
      showSuccess(fieldName);
    }
  });

  const urlRegex = /^[(http(s)?):\/\/(www\.)?a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)$/;
  if (!urlRegex.test(newDeal.url)) {
    showError("url", "잘못된 url입니다");
  } else {
    showSuccess("url");
  }

  if (newDeal.images.length === 0) {
    showError("images", "필수 항목입니다");
  } else {
    showSuccess("images");
  }
}

function onSuccess(resp) {
  router.push("/");
}

async function submitDeal() {
  const dealServer = "https://api.09market.site/deal";
  newDeal.images = []; //images 포맷 변경
  for (var i = 0; i < uploadedFile.value.length; i++) {
    newDeal.images.push(uploadedFile.value[i].response.fileName);
  }

  allIsWell = true;
  validateData();

  if (!allIsWell) {
    alert("누락된 항목이 있습니다. 전부 적으셨는지 확인 부탁드려요!");
  } else {
    await axiosPost(dealServer, userStore.JWT, newDeal, onSuccess);
  }
}
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700&display=swap");
.text-center {
  font-family: "Noto Sans KR", sans-serif;
}
el-date-picker {
  width: 50vw;
}
small {
  color: red;
}
.requiredInput {
  color: crimson;
  font-size: 1em;
  margin-left: 0.3em;
}
</style>
