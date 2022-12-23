<template>
  <main class="mt-3">
    <div class="container">
      <h2 class="text-center my-4">키워드 수정</h2>
      <div class="mb-3 row">
        <label class="col-md-3 col-form-label">태그</label>
        <div class="col-md-9">
          <el-tag v-for="tag in keywordRef" :key="tag" :type="'success'" class="mr-1" closable :disable-transitions="false" size="large" @close="handleClose(tag)">
            {{ tag }}
          </el-tag>
          <el-input v-if="inputVisible" ref="InputRef" v-model="inputValue" class="col-auto" @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" />
          <el-button v-else class="button-new-tag mr-1" @click="showInput"> + New Tag </el-button>
        </div>
      </div>
      <div class="col-6 d-grid p-1">
        <button type="button" @click="submitChange" class="btn btn-lg btn-danger">저장하기</button>
      </div>
    </div>
  </main>
</template>

<script setup>
import { reactive, nextTick, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { UploadFilled } from "@element-plus/icons-vue";
import useAxios from "@/modules/axios";
import { useUserInfoStore } from "/@stores/userInfo";

const userStore = useUserInfoStore();
const { axiosPost } = useAxios();
const router = useRouter();

const keyword = [];
const inputValue = ref("");
const inputVisible = ref(false);
const InputRef = ref({});
const keywordRef = ref(keyword);

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
  const dealServer = "https://09market.site/user/updatekeyword";
  console.log(keywordRef.value);
  const changeKeyword = { keyword: keyword };
  console.log(changeKeyword);
  console.log(userStore.JWT);
  await axiosPost(dealServer, userStore.JWT, changeKeyword, onSuccess, onFail);
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
