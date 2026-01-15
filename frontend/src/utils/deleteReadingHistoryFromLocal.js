import { envConstant } from '@/config/envConstant';

const deleteReadingHistoryFromLocal = (id) => {
  let readingHistories = JSON.parse(
    localStorage.getItem(envConstant.readingHistoryOnLocal)
  );
  if (!readingHistories) return [];

  readingHistories = readingHistories.filter((item) => item.id !== id);
  localStorage.setItem(
    envConstant.readingHistoryOnLocal,
    JSON.stringify(readingHistories)
  );
  // trả về mảng sau khi xóa
  return readingHistories;
};
export default deleteReadingHistoryFromLocal;
