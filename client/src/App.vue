<template>
  <navbar />
  <router-view />
  <footer />
</template>

<script>
export default {
  name: "App",
};
</script>

<script setup>
import { useRoute } from "vue-router";
import navbar from "./components/navbar.vue";
import footer from "./components/footer.vue";

const route = useRoute();
</script>

<style>
ul {
  list-style-type: none;
}
#noAccount {
  margin-top: 0.9em;
}
</style>
