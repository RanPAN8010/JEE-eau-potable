/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        // Remplace la police sans-serif par défaut par Inter
        sans: ['Inter', 'sans-serif'],
        // Remplace la police serif par défaut par Playfair Display
        serif: ['"Playfair Display"', 'serif'],
      },
    },
  },
  plugins: [],
}