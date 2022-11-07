// https://vitejs.dev/config/

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "/@": path.resolve(__dirname, "./src"),
      "/@components": path.resolve(__dirname, "./src/components"),
      "/@app_modules": path.resolve(__dirname, "./src/modules"),
      "/@stores": path.resolve(__dirname, "./src/stores"),
    },
  },
  devServer: {
    proxy: {
      "/": {
        target: "http://localhost:8080",
        changeOrigin: true,
        pathRewrite: {
          "^/api": "",
        },
      },
    },
  },
});

module.rules = {
  test: /\.pug$/,
  loader: "pug-plain-loader",
};
