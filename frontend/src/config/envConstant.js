export const envConstant = {
  baseUrl:
    'https://v-story-be.onrender.com/api',
  tokenName: import.meta.env.VITE_ACCESS_TOKEN_NAME || 'reading_web_jwt',
  refreshTokenName:
    import.meta.env.VITE_REFRESH_ACCESS_TOKEN_NAME ||
    'reading_web_refresh_token',
  readingHistoryOnLocal: 'local_reading_history',
  default_email:
    import.meta.env.VITE_DEFAULT_EMAIL || 'admin123@gmail.com',
  default_password: import.meta.env.VITE_DEFAULT_PASSWORD || 'adminweb123',
};
