import { envConstant } from '@/config/envConstant';

const getReadingHistoriesFromLocal = () => {
  return (
    JSON.parse(localStorage.getItem(envConstant.readingHistoryOnLocal)) || []
  );
};

export default getReadingHistoriesFromLocal;
