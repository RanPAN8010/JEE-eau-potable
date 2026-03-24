import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  base: './',
  plugins: [react()],
  build: {
      // 关键：将输出目录指向 Java 的 webapp
      outDir: '../src/main/webapp', 
      emptyOutDir: true, // 每次构建先清空目标目录
    }
})