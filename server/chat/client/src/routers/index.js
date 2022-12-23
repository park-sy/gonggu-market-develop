import { createRouter, createWebHistory } from "vue-router";
import landingPage from "../components/landingPage.vue";
import loginPrompt from "../components/loginPrompt.vue";
import join from "../components/join.vue";
import allRoom from "../components/allRoom.vue";
import room from "../components/room.vue";
import dealCreate from "../views/dealCreate.vue";
import dealDetail from "../views/dealDetail.vue";
import dealList from "../views/dealList.vue";
import dealRaised from "../views/dealRaised.vue";
import dealUpdate from "../views/dealUpdate.vue";
import changeKeyword from "../views/changeKeyword.vue";
import profile from "../components/profile.vue";
import paymentCheck from "../views/paymentCheck.vue";
import paymentMain from "../views/paymentMain.vue";
import sellerHistory from "../views/sellerHistory.vue";
import { useUserInfoStore } from "/@stores/userInfo";
import axios from "axios";

const routes = [
  {
    path: "/",
    name: "Home",
    component: dealList,
    meta: { authRequired: true },
  },
  {
    path: "/create",
    name: "Create",
    component: dealCreate,
    meta: { authRequired: true },
  },
  {
    path: "/detail",
    name: "Detail",
    component: dealDetail,
    meta: { authRequired: true },
  },
  {
    path: "/raised",
    name: "Raised",
    component: dealRaised,
    meta: { authRequired: true },
  },
  {
    path: "/update",
    name: "Update",
    component: dealUpdate,
    meta: { authRequired: true },
  },
  {
    path: "/login",
    name: "login",
    component: loginPrompt,
    meta: { authRequired: false },
  },
  {
    path: "/join",
    name: "join",
    component: join,
    meta: { authRequired: false },
  },
  {
    path: "/profile",
    name: "profile",
    component: profile,
    meta: { authRequired: true },
  },
  {
    path: "/room/:roomId",
    name: "room",
    component: room,
    meta: { authRequired: true },
  },
  {
    path: "/changeKeyword",
    name: "changeKeyword",
    component: changeKeyword,
    meta: { authRequired: true },
  },
  {
    path: "/payment/check",
    name: "paymentCheck",
    component: paymentCheck,
    meta: { authRequired: true },
  },
  {
    path: "/payment",
    name: "paymentMain",
    component: paymentMain,
    meta: { authRequired: true },
  },
  {
    path: "/history",
    name: "sellerHistory",
    component: sellerHistory,
    meta: { authRequired: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

const checkValidRoomMember = async (currentUser, curRoomId) => {
  //console.log(currentUser, curRoomId);
  const { VITE_BASE_URL } = import.meta.env;
  const result = await axios.get(
    `https://api.09market.site/room/${curRoomId}/nickname/${currentUser}`
  );
  //console.log(result.data, result.status);
  if (result.data.ok === true) {
    console.log("✅ IS MEMBER!!");
    return true;
  } else {
    return false;
  }
};

router.beforeEach(async (to, from, next) => {
  // console.log("to =>", to);
  // console.log("from =>", from);

  const store = useUserInfoStore();
  const isLoggedIn = store.loggedIn;
  const currentUser = store.userNick;

  const curRoomId = Number(to.params.roomId);

  if (isLoggedIn && currentUser) {
    if (to.meta.authRequired === false) {
      console.log("❗️ NOT allowed");
      next({ name: "Home" });
    } else if (curRoomId && curRoomId > 0) {
      if ((await checkValidRoomMember(currentUser, curRoomId)) === true) {
        next();
      } else {
        alert("방의 멤버가 아닙니다.");
        next({ name: "landingPage" });
      }
    } else {
      next();
    }
  } else if (isLoggedIn === false && to.meta.authRequired) {
    console.log("❗️ NOT allowed");
    next("login");
  } else {
    next();
  }
});

export default router;
