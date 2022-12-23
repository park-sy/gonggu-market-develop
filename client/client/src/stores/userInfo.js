import { defineStore } from "pinia";

// export const useUserInfoStore = defineStore("userInfo", {
//   state: () => ({ userName: "", userNick: "", loggedIn: false }),
//   actions: {
//     setInfo(name, nick, isLoggedIn) {
//       this.userName = name;
//       this.userNick = nick;
//       this.loggedIn = isLoggedIn;
//     },
//   },
//   getters: {
//     getInfo(state) {
//       return [state.userName, state.userNick, state.loggedIn];
//     },
//   },
//   persist: true,
// });

export const useUserInfoStore = defineStore("userInfo", {
  state: () => ({ userNick: "", JWT: "", loggedIn: false }),
  actions: {
    setInfo(nick, JWT, isLoggedIn) {
      this.userNick = nick;
      this.JWT = JWT;
      this.loggedIn = isLoggedIn;
    },
  },
  getters: {
    getInfo(state) {
      return [state.userNick, state.JWT, state.loggedIn];
    },
  },
  persist: true,
});
