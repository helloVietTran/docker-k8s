import axiosInstance from '@/config/axiosConfig';

const authPrefix = '/auth';

export const login = async (credentialsData) => {
  const { data } = await axiosInstance.post(
    `${authPrefix}/login`,
    credentialsData
  );

  return data;
};

export const logout = async (token) => {
  await axiosInstance.post(`${authPrefix}/logout`, token);
};

export const refresh = async (refreshToken) => {
  const { data } = await axiosInstance.post(
    `${authPrefix}/refresh`,
    refreshToken
  );

  return data.result;
};
