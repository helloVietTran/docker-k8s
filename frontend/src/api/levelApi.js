import axiosInstance from '@/config/axiosConfig';

const levelPrefix = '/level';

export const increaseExperence = async (chapterId) => {
  const { data } = await axiosInstance.patch(
    `${levelPrefix}/increase/${chapterId}`
  );
  return data;
};
