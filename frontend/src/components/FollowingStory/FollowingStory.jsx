import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { useQuery } from '@tanstack/react-query';

import TopStory from '@/components/TopStory/TopStory';
import StoryCard from '@/components/StoryCard/StoryCard';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import PrimaryHeading from '@/components/Heading/PrimaryHeading/PrimaryHeading';
import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import { getMyFollowedStories } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';
import getReadingHistoriesFromLocal from '@/utils/getReadingHistoryFromLocal';

function FollowingStory() {
  const { isAuthenticated } = useSelector((state) => state.auth);
  const { darkTheme } = useSelector((state) => state.theme);

  // lịch sử đọc truyện theo thiết bị
  const [localReadingHistories, setLocalReadingHistories] = useState([]);

  useEffect(() => {
    setLocalReadingHistories(getReadingHistoriesFromLocal());
  }, []);

  const { data: followedData } = useQuery({
    queryKey: [queryKey.MY_FOLLOWED_STORIES],
    queryFn: getMyFollowedStories,
    staleTime: 5 * 60 * 1000,
  });

  console.log(followedData);
  return (
    <DefaultLayout>
      <Container shouldApplyPadding isBackgroundVisible>
        <BreadCumb />
        <PrimaryHeading
          size={2}
          icon={faAngleRight}
          title="Truyện đang theo dõi"
        />
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-2 mt-2">
          <div className="lg:col-span-8 w-full">
            <div className={darkTheme ? 'dark' : ''}>
              {!isAuthenticated ? (
                <>
                  <div className="dark:text-gray-200 text-sm">
                    <p>
                      Vui lòng{' '}
                      <Link
                        className="text-blue-detail-heading dark:text-dark-red-hover hover:underline"
                        to="/login"
                      >
                        Đăng nhập
                      </Link>{' '}
                      để truy cập truyện đã theo dõi ở bất cứ đâu.
                      <br />
                      Để theo dõi truyện, nhấn vào <u>Theo dõi</u> như hình bên
                      dưới: <br />
                    </p>
                  </div>
                  <img
                    src="/images/back-ground/huong-dan-theo-doi-truyen.jpg"
                    width="660px"
                    alt="Hướng dẫn theo dõi"
                  />
                </>
              ) : (
                <>
                  <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-x-2 gap-y-4">
                    {followedData?.length > 0 ? (
                      followedData.map((item) => (
                        <div key={item.id}>
                          <StoryCard
                            data={item.story}
                            readingHistoryData={localReadingHistories}
                            followId={item.id}
                          />
                        </div>
                      ))
                    ) : (
                      <p className="col-span-full text-red-400">
                        Bạn chưa theo dõi bộ truyện nào!
                      </p>
                    )}
                  </div>
                </>
              )}
            </div>
          </div>

          <div className="lg:col-span-4 w-full">
            <TopStory />
          </div>
        </div>
      </Container>
    </DefaultLayout>
  );
}

export default FollowingStory;
