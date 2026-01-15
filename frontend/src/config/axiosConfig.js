import axios from 'axios';
import { envConstant } from './envConstant';

const baseUrl = envConstant.baseUrl;

const axiosInstance = axios.create({
  baseURL: baseUrl,
  timeout: 10000,
});

let isRefreshing = false;
let refreshPromise = null;

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(envConstant.tokenName);

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      if (!isRefreshing) {
        isRefreshing = true;

        try {
          const refreshToken = localStorage.getItem(
            envConstant.refreshTokenName
          );
          if (!refreshToken) {
            throw new Error('Refresh token not found');
          }

          const { data } = await axios.post(`${baseUrl}/auth/refresh`, {
            refreshToken,
          });

          const { accessToken } = data;

          localStorage.setItem(envConstant.tokenName, accessToken);
          localStorage.setItem(envConstant.refreshTokenName, refreshToken);

          isRefreshing = false;
          refreshPromise = null;

          originalRequest._retry = true;
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return axiosInstance(originalRequest);
        } catch (err) {
          console.error('Refresh token failed. Redirecting to login.');
          isRefreshing = false;
          refreshPromise = null;

          localStorage.removeItem(envConstant.tokenName);
          localStorage.removeItem(envConstant.refreshTokenName);

          window.location.href = '/login';
          return Promise.reject(err);
        }
      }

      // Đợi refresh token hoàn tất rồi thử lại request
      if (!refreshPromise) {
        refreshPromise = new Promise((resolve) => {
          const interval = setInterval(() => {
            if (!isRefreshing) {
              clearInterval(interval);
              resolve();
            }
          }, 50);
        });
      }

      await refreshPromise;
      const newToken = localStorage.getItem(envConstant.tokenName);

      originalRequest._retry = true;
      originalRequest.headers.Authorization = `Bearer ${newToken}`;

      return axiosInstance(originalRequest);
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
