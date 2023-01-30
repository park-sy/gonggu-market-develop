<template>
  <nav class="navbar sticky-top navbar-expand-lg" v-if="userStore.loggedIn">
    <div class="container-fluid text-white">
      <router-link
        to="/"
        class="navbar-brand text-white"
        @click="navbarCollapsed = true"
      >
        우리동네 공구마켓
      </router-link>
      <button
        :class="{ 'navbar-toggler': true, collapsed: navbarCollapsed }"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
        @click="navbarCollapsed = !navbarCollapsed"
      >
        <span class="navbar-toggler-icon navbar-dark"></span>
      </button>

      <div
        :class="{
          collapse: true,
          'navbar-collapse': true,
          show: !navbarCollapsed,
        }"
        id="navbarNav"
      >
        <ul
          :class="{ 'navbar-nav': true, 'me-auto': menu.me_auto }"
          v-for="menu in menus_category"
          :key="menu.id"
        >
          <li
            class="nav-item"
            v-for="menu_object in menu.value"
            :key="menu_object.key"
          >
            <router-link
              v-if="menu_object.key == 'nick'"
              :class="{
                'nav-link': true,
                active: menu == menu_object.key,
                'text-white': true,
              }"
              @click="onMovePage($event, menu_object)"
              :to="menu_object.URL"
              >{{ userStore.userNick }}
            </router-link>
            <router-link
              v-else
              :class="{
                'nav-link': true,
                active: menu == menu_object.key,
                'text-white': true,
              }"
              @click="onMovePage($event, menu_object)"
              :to="menu_object.URL"
              >{{ menu_object.value }}
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: "navbar",
};
</script>

<script setup>
import useAxios from "@/modules/axios";
import { ref, computed, watch } from "vue";
import { useUserInfoStore } from "/@stores/userInfo";

const userStore = useUserInfoStore();
const { axiosGet, axiosPost } = useAxios();

const menu = ref("home");
const navbarCollapsed = ref(true);

const menus = [
  //{ key: "home", value: "홈", URL: "/", position: "left" },
  {
    key: "raiseDeal",
    value: "공구 등록",
    URL: "/raised",
    position: "left",
  },
  {
    key: "mychat",
    value: "공구 채팅",
    URL: `/room/0`,
    position: "left",
  },
  {
    key: "paymentCheck",
    value: "공구 페이",
    URL: "/payment/check",
    position: "left",
  },
  {
    key: "nick",
    URL: "/profile",
    value: "",
    position: "right",
  },
  {
    key: "logout",
    value: "로그아웃",
    URL: "/login",
    position: "right",
  },
];

const left_menus = computed(() => menus.filter((i) => i.position == "left"));
const right_menus = computed(() => menus.filter((i) => i.position == "right"));

const onMovePage = (evt, menu_object) => {
  navbarCollapsed.value = true;
  if (menu_object.key === "logout") {
    const jwt = userStore.JWT;
    unsubscribe(jwt);
    userStore.setInfo("", "", false);
  }
  console.log(menu_object);
  if (evt) {
    //evt.preventDefault();
  }
  menu.value = menu_object.key;
};

const menus_category = [
  { id: 1, me_auto: true, value: left_menus.value },
  { id: 2, me_auto: false, value: right_menus.value },
];

function unsubscribe(JWT) {
  navigator.serviceWorker.ready
    .then((swreg) => {
      return swreg.pushManager.getSubscription();
    })
    .then((oldsub) => {
      const body = {
        endpoint: oldsub.endpoint,
      };
      oldsub.unsubscribe();
      console.log("from unsubscribe", JWT);
      axiosPost("https://api.09market.site/user/unregister", JWT, body);
    })
    .then((res) => {
      console.log("😁 Push service unsubscribed");
    })
    .catch((err) => {
      console.error(err);
    });
}
</script>

<style>
nav {
  background-color: #0b2239;
  color: white;
}
</style>
