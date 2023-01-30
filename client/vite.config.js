import { defineConfig } from "vite";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import vue from "@vitejs/plugin-vue";
import path from "path";
import { VitePWA } from "vite-plugin-pwa";

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    VitePWA({
      registerType: "autoUpdate",
      devOptions: {
        enabled: true,
      },
      strategies: "injectManifest",
      srcDir: "src",
      filename: "sw.js",
      start_url: "/",
      includeAssets: ["favicon.ico", "apple-touch-icon.png", "masked-icon.svg"],
      manifest: {
        name: "우리동네 공구마켓",
        short_name: "09market",
        description: "나누는 행복, 나눔의 행복",
        theme_color: "#ffffff",
        icons: [
          {
            src: "icons/maskable-icon-512x512.png",
            sizes: "512x512",
            type: "image/png",
            purpose: "maskable",
          },
          {
            src: "icons/android-chrome-192x192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "icons/android-chrome-512x512.png",
            sizes: "512x512",
            type: "image/png",
          },
        ],
        display: "standalone", //기기의 최대 화면으로 설정
        orientation: "portrait", //세로로 볼 수 있도록 한다
      },
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src/"),
      "/@components": path.resolve(__dirname, "./src/components"),
      "/@app_modules": path.resolve(__dirname, "./src/modules"),
      "/@stores": path.resolve(__dirname, "./src/stores"),
      "~bootstrap": path.resolve(__dirname, "node_modules/bootstrap"),
    },
  },
  build: {
    chunkSizeWarningLimit: 1600,
  },
});
