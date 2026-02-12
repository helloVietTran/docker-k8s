import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';

import Head from '@/components/Head/Head';
import Main from '@/components/Main/Main';
import Footer from '@/components/Footer/Footer';
import TopStory from '@/components/TopStory/TopStory';
import { getHotStories } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';
import SplashScreen from '@/components/SplashScreen/SplashScreen';
import Navbar from '@/components/NavBar/NavBar'; 

function Hot() {
  const [searchParams] = useSearchParams();

  const page = searchParams.get('page') || 1;

  const storiesQuery = useQuery({
    queryKey: [queryKey.HOT_STORIES, page],
    queryFn: () => getHotStories(page),
  });

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      {storiesQuery.isLoading && <SplashScreen />}
      <Head />
      <Navbar />
      <Main title="Truyện hot nhất" data={storiesQuery.data}>
        <TopStory />
      </Main>
      <Footer />
    </>
  );
}

export default Hot;
