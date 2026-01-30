import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), '')

    return defineConfig({
        plugins: [react()],
        server: {
            host: env.VITE_FRONT_HOST,
            port: 3001,
        },
        preview: {
            host: env.VITE_FRONT_HOST,
            port: 3001,
            strictPort: true,
        },
        build: {
            chunkSizeWarningLimit: 1000
        }
    });
})
