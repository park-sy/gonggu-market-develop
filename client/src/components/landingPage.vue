<!-- Just for test -->
<template>
  <!-- Not Used -->
  <h1>Landing Page - test</h1>
  <div class="container p-5">
    <div class="mb-4 bg-light rounded-3">
      <h1>Create Chatroom</h1>
      <form>
        <div class="mb-2 row">
          <label for="roomName" class="col-sm-4 col-form-label">
            Room Name
          </label>
          <div class="col-sm-8">
            <input
              type="text"
              class="form-control"
              v-model="chatRoom.roomName"
              id="roomName"
              required
            />
          </div>
        </div>
        <button
          type="button"
          class="btn btn-danger w-100"
          @click="createRoomSubmit"
        >
          Create
        </button>
      </form>
    </div>
  </div>

  <img
    src="https://bucket-images-for-gongu.s3.ap-northeast-2.amazonaws.com/1669114775635_test.jpeg"
  />
</template>

<script>
export default {
  name: "CreateRoom",
};
</script>

<script setup>
import { reactive } from "vue";
import useAxios from "../modules/axios";
import { useUserInfoStore } from "../stores/userInfo";

const { axiosGet, axiosPost } = useAxios();

const chatRoom = reactive({
  roomName: "",
});

const onCreateSuccess = (respData) => {
  console.log("✅ Room Create Success");
};
const onCreateFail = (respData) => {
  console.log("❌ Room Create Fail");
};

const createRoomSubmit = () => {
  console.log(chatRoom);
  const store = useUserInfoStore();
  const nick = store.userNick;
  axiosPost(
    `/users/${nick}/createRoom`,
    chatRoom,
    onCreateSuccess,
    onCreateFail
  );
};
</script>
