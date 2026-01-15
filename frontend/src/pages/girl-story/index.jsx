import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Main from '@/components/Main/Main';
import Footer from '@/components/Footer/Footer';
import TopStory from '@/components/TopStory/TopStory';
import { getStoriesByGender } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';
import SplashScreen from '@/components/SplashScreen/SplashScreen';

function GirlStory() {
  const [searchParams] = useSearchParams();

  const page = searchParams.get('page') || 1;

  const storiesQuery = useQuery({
    queryKey: [queryKey.GIRL_STORIES, 'MALE', page],
    queryFn: () => getStoriesByGender('MALE', page),
  });
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      <Head />
      {storiesQuery.isLoading && <SplashScreen />}
      <Navbar />

      <Main title="Truyện con gái" data={storiesQuery.data}>
        <TopStory />
      </Main>
      <Footer />
    </>
  );
}

export default GirlStory;
