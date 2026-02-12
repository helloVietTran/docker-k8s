import axiosInstance from '@/config/axiosConfig';

const shopPrefix = '/shop';

export const getAvatarFrame = async () => {
  const { data } = await axiosInstance.get(`${shopPrefix}/avatar-frame`);

  return data.result;
};
