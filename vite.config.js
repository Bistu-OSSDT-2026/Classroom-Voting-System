import { defineConfig } from "vite";

export default defineConfig({
  root: ".",
  base: "/team-project/",
  build: {
    outDir: "dist",
    assetsDir: "assets",
  },
  server: {
    port: 3000,
    open: true,
  },
});
