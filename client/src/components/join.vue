<template>
  <div id="outer" class="container">
    <div class="row justify-content-center">
      <div class="col-md-12 col-lg-10">
        <div class="wrap d-md-flex">
          <div class="img" style="background-image: url('https://gongu-image.s3.ap-northeast-2.amazonaws.com/soganguniv.jpg')"></div>
          <div class="login-wrap p-4 p-md-5">
            <div class="d-flex">
              <div class="w-100">
                <h3 class="mb-4">Registration</h3>
              </div>
            </div>
            <div class="form-group mb-3">
              <label class="label" for="id">ID</label>
              <div class="input-group" id="idGroup">
                <input type="text" id="id" class="form-control" placeholder="ID" v-model="userInfo.id" required />
                <button class="btn btn-outline-secondary" type="button" id="idConfirm" @click="idConfirm">중복검사</button>
              </div>
              <small>{{ errorMsg.id }}</small>
            </div>
            <div class="form-group mb-3">
              <label class="label" for="email">Email</label>
              <input type="email" id="email" class="form-control" placeholder="Email" v-model="userInfo.email" required />
              <small>{{ errorMsg.email }}</small>
            </div>
            <div class="form-group mb-3">
              <label class="label" for="password">Password</label>
              <input type="password" id="password" class="form-control" placeholder="Password" v-model="userInfo.password" required />
              <small>{{ errorMsg.password }}</small>
            </div>
            <div class="form-group mb-3">
              <label class="label" for="password2">Password confirm</label>
              <input type="password" id="password2" class="form-control" placeholder="Password again" v-model="userInfo.password2" required />
              <small>{{ errorMsg.password2 }}</small>
            </div>
            <div class="form-group">
              <button type="submit" class="form-control btn btn-primary rounded submit px-3" @click="joinSubmit">Be our member</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const id = document.getElementById("id");
const email = document.getElementById("email");
const password = document.getElementById("password");
const password2 = document.getElementById("password2");

import useAxios from "../modules/axios";
import { useUserInfoStore } from "/@stores/userInfo";
import { reactive, ref } from "vue";
import router from "../routers";

const { axiosGet, axiosPost } = useAxios();
var allIsWell = true;
var idValidated = ref(false); //id의 중복 체크가 완료되었는지

const _userInfo = {
  id: "",
  email: "",
  password: "",
  password2: "",
};

const userInfo = reactive(_userInfo);

const errorMsg = reactive({
  id: "",
  email: "",
  password: "",
  password2: "",
});

const onJoinSuccess = (respData) => {
  alert("회원가입 성공!");
  router.push("/");
};

const onJoinFail = (respData = null) => {
  console.log("❌ FAILED");
};

//validation
//Input error 메시지를 출력한다
function showError(input, message) {
  allIsWell = false;
  errorMsg[input] = message;
  document.getElementById(input).className = "form-control error";
}

//Input success 메시지를 출력한다
function showSuccess(input) {
  errorMsg[input] = "";
  document.getElementById(input).className = "form-control success";
}

//Email이 유효한 형태로 작성되었는지 검사한다
function checkEmail(input) {
  const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  if (re.test(userInfo[input].trim())) {
    showSuccess(input);
  } else {
    showError(input, "잘못된 이메일입니다");
  }
}

//입력값의 길이를 검사한다
function checkLength(input, min, max) {
  if (userInfo[input].length < min) {
    showError(input, `${min}글자 이상 입력해주세요`);
  } else if (userInfo[input].length > max) {
    showError(input, `${max}글자 이하로 입력해주세요`);
  } else {
    showSuccess(input);
  }
}

//비밀번호의 일치 여부를 검사한다
function checkPasswordsMatch(input1, input2) {
  if (userInfo[input1] !== userInfo[input2]) {
    showError(input2, "비밀번호를 다시 확인해주세요");
  }
}

//필수 요소가 전부 작성되었는지 검사한다
function checkRequired(inputArr) {
  for (const [key, value] of Object.entries(userInfo)) {
    if (value === "") {
      console.log(`${key} is missing`);
      showError(key, "필수 입력 항목입니다");
    } else {
      showSuccess(key);
    }
  }
}

//버튼에 대한 이벤트 핸들러를 등록한다.
const joinSubmit = function () {
  allIsWell = true;

  checkRequired();
  checkLength("id", 3, 15);
  checkLength("password", 6, 25);
  checkEmail("email");
  checkPasswordsMatch("password", "password2");

  if (!idValidated.value) {
    showError("id", "ID 중복 체크를 해주세요");
  } else if (allIsWell) {
    console.log("👍 User Information valid!");
    console.log(_userInfo);
    axiosPost("https://api.09market.site/join", null, _userInfo, onJoinSuccess);
  }
};

function onIdConfirm(resp) {
  if (resp.ok === "true") {
    showSuccess("id");
    idValidated.value = true;
    document.getElementById("id").setAttribute("disabled", "");
    const idGroup = document.getElementById("idGroup");
    idGroup.removeChild(idGroup.lastChild);
  } else {
    showError("id", "이미 사용중인 아이디입니다."), 1500;
    idValidated.value = false;
  }
  console.log(idValidated.value);
}

//id 중복검사
function idConfirm() {
  if (userInfo.id.length < 3) {
    alert("ID는 세 글자 이상이여야 합니다");
    return;
  }
  axiosPost("https://api.09market.site/isduplicate", null, { id: userInfo.id }, onIdConfirm);
}
</script>

<style scoped>
@import "../assets/login.css";

#outer {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.form-control.success {
  border-color: #2ecc71;
}

.form-control.error {
  border-color: #e74c3c;
}

small {
  color: #e74c3c;
}

.form-control.error small {
  visibility: visible;
}
</style>
