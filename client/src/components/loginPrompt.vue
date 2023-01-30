<template>
  <div id="outer" class="container">
    <div class="row justify-content-center">
      <div class="col-md-12 col-lg-10">
        <div class="wrap d-md-flex">
          <div class="img" style="background-image: url('https://gongu-image.s3.ap-northeast-2.amazonaws.com/soganguniv.jpg')"></div>
          <div class="login-wrap p-4 p-md-5">
            <div class="d-flex">
              <div class="w-100">
                <h3 class="mb-4">Sign In</h3>
              </div>
            </div>
            <div class="mb-3">
              <label class="label" for="name">ID</label>
              <input type="text" class="form-control" placeholder="ID" v-model="userInfo.id" required />
            </div>
            <div class="mb-3">
              <label class="label" for="password">Password</label>
              <input type="password" class="form-control" placeholder="Password" v-model="userInfo.password" required />
            </div>
            <div>
              <button type="submit" class="form-control btn btn-primary rounded submit px-3" @click="loginSubmit">Sign In</button>
            </div>
            <div class="mt-4">
              <p class="text-center">Not a member? <span @click="goToJoin">Sign Up</span></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import useAxios from "../modules/axios";
import { useUserInfoStore } from "/@stores/userInfo";
import { reactive } from "vue";
import router from "../routers";

const { axiosGet, axiosPost } = useAxios();

const userInfo = reactive({
  id: "",
  password: "",
});

const goToJoin = () => {
  router.push("join");
};

const onLoginSuccess = (respData) => {
  if (respData.ok === "true") {
    const userStore = useUserInfoStore();

    userStore.setInfo(userInfo.id, respData.accesstoken, true);
    console.log("✅ userStore", userStore.getInfo);
    if (userStore.loggedIn) {
      router.push("/");
    }
  } else {
    alert("로그인 정보를 다시 확인해주세요");
  }
};

const loginSubmit = () => {
  axiosPost("https://api.09market.site/login", null, userInfo, onLoginSuccess);
};
</script>

<style scoped>
@import "../assets/login.css";
#outer {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

span {
  -webkit-transition: 0.3s all ease;
  -o-transition: 0.3s all ease;
  transition: 0.3s all ease;
  color: #e3b04b;
  cursor: pointer;
}

span:hover,
span:focus {
  color: navy !important;
  outline: none !important;
  -webkit-box-shadow: none;
  box-shadow: none;
}
</style>
