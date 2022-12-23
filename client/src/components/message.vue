<template>
  <div
    class="message"
    :class="{
      viewer: yours,
      speaking: sameUser,
      keepOn: sameTime,
    }"
  >
    <div
      v-if="isMessage(message)"
      class="message-model"
      :class="{ viewer: yours, speaking: sameUser }"
    >
      <div
        class="message-head"
        v-if="!yours && sameUser"
        :class="{ viewer: yours, diffAbove: diffOtherUser }"
      >
        <p class="sender">{{ message.sender }}</p>
      </div>
      <div
        class="content-body"
        v-if="message.content"
        :class="{ viewer: yours }"
      >
        <span class="content" :class="{ viewer: yours }">{{
          message.content
        }}</span>
        <div class="date-body" v-if="sameTime">
          <span class="date">{{ formatDate(new Date(message.time)) }}</span>
        </div>
      </div>
      <div v-else class="content-body-image" :class="{ viewer: yours }">
        <div class="image-box" :class="{ viewer: yours }">
          <img v-bind:src="message.imgPath" />
        </div>
        <div class="date-body" v-if="sameTime">
          <span class="date">{{ formatDate(new Date(message.time)) }}</span>
        </div>
      </div>
    </div>
    <div v-else class="notification">
      <div></div>
      <div class="notification-box">
        <p>{{ message.content }}</p>
      </div>
      <div></div>
    </div>
  </div>
</template>

<script>
export default {
  props: [
    "message",
    "yours",
    "sameUser",
    "sameTime",
    "dayDivide",
    "diffOtherUser",
  ],
  updated() {},
  methods: {
    formatDate(date) {
      const padZero = (num) => {
        if (num < 10) {
          return "0" + String(num);
        } else return String(num);
      };
      let hour = Number(date.getHours());
      const mm = hour > 11 ? "오후" : "오전";
      if (hour > 12) hour -= 12;
      else if (hour == 0) hour = 12;
      let minute = Number(date.getMinutes());
      return `${mm} ${padZero(hour)}:${padZero(minute)}`;
    },
    isMessage(msg) {
      return msg.chatType == "message";
    },
  },
};
</script>

<style scoped>
div.message {
  display: flex;
  align-items: flex-end;
  margin-bottom: 5px;
}
div.message.viewer {
  margin: 0px;
}
div.message-head {
  height: 20px;
  display: flex;
  align-items: center;
}
div.message.viewer,
div.message-head.viewer {
  justify-content: flex-end;
}
div.message-head.diffAbove {
  margin-top: 20px;
}
div.message-head p.sender {
  margin-right: 0.65rem;
  font-size: 1rem;
  font-weight: 600;
}

div.content-body,
div.content-body-image {
  display: flex;
  margin-bottom: 5px;
}
div.content-body.viewer,
div.content-body-image.viewer {
  flex-direction: row-reverse;
  margin-bottom: 10px;
}
div.date-body {
  display: flex;
  align-items: flex-end;
}
span.content {
  background-color: dimgray;
  padding: 5px 10px;
  border-radius: 10px;
  float: left;
}
span.content.viewer {
  float: right;
  background-color: #7e71bb;
}
span.date {
  color: rgb(172, 170, 170);
  font-size: 0.65rem;
  margin: 0px 5px;
}
div.image-box {
  height: 200px;
  width: 200px;
  background-color: black;
  border-radius: 5px;
}
img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
div.notification {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 1.5rem;
  width: 100%;
}
div.divider div {
  height: 1px;
  width: 100%;
  background: #7e71bb;
}
div.notification-box {
  background-color: dimgrey;
  padding: 3px;
  border-radius: 5px;
}
div.notification p {
  margin: 0 0.3rem;
  font-size: 0.85rem;
  justify-content: center;
  text-align: center;
  white-space: nowrap;
  color: white;
}
</style>
