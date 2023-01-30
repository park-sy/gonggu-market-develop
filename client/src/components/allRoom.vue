<template>
  <!-- Not Used -->
  <h1>ALL ROOMS!!</h1>
  <div>
    <ul v-for="room in roomList">
      <li>
        <router-link :to="{ name: 'room', params: { roomId: room.deal_id } }">{{
          room.title
        }}</router-link>
        <button type="button" @click="onEnterRoom(room.deal_id)">
          Be a Member
        </button>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "allRoom",
  data() {
    return {
      roomList: [],
    };
  },
  beforeMount() {
    const { axiosGet, axiosPost } = useAxios();

    const getRoomSuccess = (respData) => {
      console.log("✅ Get Rooms - Success");
      console.log(respData);
      this.roomList = respData.rooms;
    };
    const getRoomFail = (respData) => {
      console.log("❌ Get Rooms - Fail");
    };
    axiosGet(`/room`, null, null, getRoomSuccess, getRoomFail);
  },
};
</script>

<script setup>
import useAxios from "../modules/axios";
import { useUserInfoStore } from "../stores/userInfo";
const enterRoomSuccess = (respData) => {
  console.log("Success");
};
const enterRoomFail = (respData) => {
  alert("Already in Room...");
};
const onEnterRoom = (roomId) => {
  console.log(roomId);
  const { axiosGet, axiosPost } = useAxios();
  const store = useUserInfoStore();
  const data = { roomId: roomId, nickname: store.userNick };
  axiosPost("/rooms/enter", data, enterRoomSuccess, enterRoomFail);
};
</script>
