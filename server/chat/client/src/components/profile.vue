<template>
  <main class="mt-3">
    <div class="container" id="outer">
      <div class="info-title">
        <h1>사용자 정보</h1>
      </div>
      <div class="row justify-content-center">
        <div class="basic-info">
          <div class="basic-info-each">
            <label class="basic-info-label">ID</label>
            <input class="basic-info-input" disabled :value="nickname" />
          </div>
          <div class="basic-info-each">
            <label class="basic-info-label">Email</label>
            <input class="basic-info-input" disabled :value="email" />
          </div>
        </div>
        <div class="line-between"></div>
        <div class="keyword-info">
          <div class="info-title">
            <h1>키워드 수정</h1>
          </div>
          <div class="basic-info-each">
            <label class="basic-info-label">Tags</label>
            <div class="basic-info-input">
              <el-tag
                v-for="tag in keywordRef"
                :key="tag"
                :type="'success'"
                class="mr-1"
                closable
                :disable-transitions="false"
                size="large"
                @close="handleClose(tag)"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                class="col-auto"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
              />
              <el-button v-else class="button-new-tag mr-1" @click="showInput">
                + New Tag
              </el-button>
            </div>
          </div>

          <div class="button-box">
            <button
              type="button"
              @click="submitChange()"
              class="btn btn-lg btn-danger"
            >
              수정하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import useAxios from "@/modules/axios";
import { useUserInfoStore } from "/@stores/userInfo";
import { ref, onBeforeMount, reactive, onMounted, nextTick } from "vue";

const userStore = useUserInfoStore();
const { axiosGet, axiosPost } = useAxios();

const nickname = ref("");
const email = ref("");
const keyword = [];
const inputValue = ref("");
const inputVisible = ref(false);
const InputRef = ref({});
const keywordRef = ref(keyword);

const getUserSuccess = (resp) => {
  console.log("✅ Get User Success", resp);
  nickname.value = resp.id;
  email.value = resp.email;
  keywordRef.value = resp.keyword;
};

onBeforeMount(async () => {
  await axiosPost(
    "https://api.09market.site/user/mypage",
    userStore.JWT,
    null,
    getUserSuccess
  );
});
const handleClose = (tag) => {
  keywordRef.value.splice(keywordRef.value.indexOf(tag), 1);
};

const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    InputRef.value.input.focus();
  });
};

const handleInputConfirm = () => {
  if (inputValue.value) {
    keywordRef.value.push(inputValue.value);
  }
  inputVisible.value = false;
  inputValue.value = "";
};

function onSuccess(resp) {
  console.log(resp);
}

function onFail(resp) {
  console.log(resp);
}

async function submitChange() {
  const dealServer = "https://api.09market.site/user/updatekeyword";
  console.log(keywordRef.value);
  const sendKey = [];
  for (const obj of keywordRef.value) sendKey.push(obj);
  const changeKeyword = { keyword: keyword };
  console.log("✅ keyword:", changeKeyword);
  console.log(userStore.JWT);
  await axiosPost(
    dealServer,
    userStore.JWT,
    { keyword: sendKey },
    onSuccess,
    onFail
  );
}
</script>

<style scoped>
#outer {
  margin-top: 50px;
  background-color: rgb(228, 225, 225);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px 0px;
  border-radius: 10px;
  width: 70%;
}
.info-title {
  margin: 30px 0px;
}
.basic-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  width: 100%;
}
.basic-info-each {
  display: flex;
  justify-content: space-evenly;
  width: 90%;
  margin-bottom: 20px;
}
.basic-info-label {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 30%;
}
.basic-info-input {
  width: 60%;
  padding: 5px;
  border-radius: 5px;
  background-color: white;
  border: 1px solid darkgray;
}
.line-between {
  height: 1px;
  width: 80%;
  background-color: darkgrey;
  margin: 30px 0px;
}
.keyword-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  width: 100%;
  font-size: 18px;
}
.button-box {
  width: 80%;
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
