import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import toast from 'react-hot-toast';
import { useMutation, useQuery } from '@tanstack/react-query';

import TopStory from '@/components/TopStory/TopStory';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import TabSwitcher from '@/components/TabSwitcher/TabSwitcher';
import PrimaryHeading from '@/components/Heading/PrimaryHeading/PrimaryHeading';
import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import HistoryCard from './HistoryCard/HistoryCard';

import getReadingHistoriesFromLocal from '@/utils/getReadingHistoryFromLocal';
import deleteReadingHistoryFromLocal from '@/utils/deleteReadingHistoryFromLocal';
import {
  deleteReadingHistory,
  getMyReadingHistory,
} from '@/api/readingHistoryApi';

function ReadHistory() {
  const { isAuthenticated } = useSelector((state) => state.auth);
  const { darkTheme } = useSelector((state) => state.theme);
  const [activeLabel, setActiveLabel] = useState('tab-1');

  // lịch sử đọc truyện theo thiết bị
  const [localReadingHistories, setLocalReadingHistories] = useState([]);

  useEffect(() => {
    setLocalReadingHistories(getReadingHistoriesFromLocal());
  }, []);

  const handleDeleteReadingHistory = (id) => {
    setLocalReadingHistories(deleteReadingHistoryFromLocal(id));
  };

  const { data, refetch } = useQuery({
    enabled: isAuthenticated,
    queryKey: ['myReadingHistories'],
    queryFn: getMyReadingHistory,
  });

  const style = {
    fontSize: '14px',
  };
  const deleteReadingHistoryInServerMutation = useMutation({
    mutationFn: deleteReadingHistory,
    onSuccess: () => {
      toast.success('Xóa lịch sử thành công', {
        style,
      });
      refetch();
    },
    onError: () => {
      toast.error('Vui lòng thử lại sau!', {
        style,
      });
    },
  });

  const handleDeleteReadingHistoryInServer = async (id) => {
    // id của bản ghi reading history
    deleteReadingHistoryInServerMutation.mutate(id);
  };

  return (
    <DefaultLayout>
      <Container shouldApplyPadding isBackgroundVisible>
        <BreadCumb />
        <div
          className={`grid grid-cols-1 lg:grid-cols-12 gap-6 ${darkTheme ? 'dark' : ''}`}
        >
          {/* LEFT COLUMN - 8/12 */}
          <div className="col-span-12 lg:col-span-8">
            <PrimaryHeading
              title="Lịch sử đọc truyện"
              icon={faAngleRight}
              size={2}
            />
            <p className="text-sm mt-1 dark:text-gray-200">
              Lịch sử đọc truyện "Theo tài khoản" chỉ được lưu khi đọc hết
              chapter
            </p>

            <div className="mt-4">
              <TabSwitcher
                firstLabel="Từ thiết bị"
                secondaryLabel="Theo tài khoản"
                onClick={setActiveLabel}
                activeTab={activeLabel}
              />
            </div>

            <div className="text-sm mt-4">
              {activeLabel === 'tab-1' && localReadingHistories.length > 0 && (
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
                  {localReadingHistories.map((item) => (
                    <HistoryCard
                      key={item.id}
                      data={item}
                      handleDeleteReadingHistory={handleDeleteReadingHistory}
                    />
                  ))}
                </div>
              )}

              {activeLabel === 'tab-2' && (
                <>
                  {isAuthenticated ? (
                    data && data.length > 0 ? (
                      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
                        {data.map((item) => (
                          <HistoryCard
                            key={item.id}
                            data={item}
                            handleDeleteReadingHistory={
                              handleDeleteReadingHistoryInServer
                            }
                          />
                        ))}
                      </div>
                    ) : (
                      <p>Bạn chưa đọc truyện nào</p>
                    )
                  ) : (
                    <div className="col-span-full">
                      <p className="pb-2 dark:text-gray-200">
                        Vui lòng
                        <Link
                          to="/login"
                          className="text-blue-600 dark:text-dark-red-hover hover:underline"
                        >
                          {' Đăng nhập '}
                        </Link>
                        để trải nghiệm tính năng này, truyện được hiển thị như
                        ảnh dưới:
                      </p>
                      <img
                        className="w-full object-cover mb-4"
                        src="images/back-ground/lich-su-doc-truyen.jpg"
                        alt="huong-dan"
                      />
                    </div>
                  )}
                </>
              )}
            </div>
          </div>

          {/* RIGHT COLUMN - 4/12 */}
          <div className="col-span-12 lg:col-span-4">
            <TopStory />
          </div>
        </div>
      </Container>
    </DefaultLayout>
  );
}

export default ReadHistory;
