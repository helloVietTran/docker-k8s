import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Main from '@/components/Main/Main';
import Footer from '@/components/Footer/Footer';
import FollowedComicList from '@/components/FollowedStoryList/FollowedStoryList';
import HistoryList from '@/components/HistoryList/HistoryList';
import TopUser from '@/components/TopUser/TopUser';
import NewComment from '@/components/NewComment/NewComment';
import TopStory from '@/components/TopStory/TopStory';
import SplashScreen from '@/components/SplashScreen/SplashScreen';
import { getStories } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';

function Home() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const [searchParams] = useSearchParams();
  const page = searchParams.get('page') || 1;

  const { data, isLoading } = useQuery({
    queryKey: [queryKey.STORIES, page],
    queryFn: () => getStories(page),
  });

  return (
    <>
      {isLoading && <SplashScreen />}
      <Head />
      <Navbar />
      <Main
        title="Truyện mới cập nhật"
        isBreadcrumbHidden={true}
        data={data?.result}
      >
        <div className="flex flex-col gap-4 !mt-4">
          <FollowedComicList />
          <HistoryList />
          <TopStory />
          <TopUser />
          <NewComment />
        </div>
      </Main>
      <Footer />
    </>
  );
}

export default Home;
