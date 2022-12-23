import { createApp } from "vue";
import App from "./App.vue";
import piniaPluginPersistedState from "pinia-plugin-persistedstate";

// Import all of Bootstrap's JS
import "./assets/styles.scss";
import * as bootstrap from "bootstrap";

import { createPinia } from "pinia";
import router from "./routers";

const pinia = createPinia();
pinia.use(piniaPluginPersistedState);
const app = createApp(App);

app.use(pinia);
app.use(router);
app.mount("#app");
