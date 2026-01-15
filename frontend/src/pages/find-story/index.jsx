import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Sort from '@/components/Sort/Sort';
import Category from '@/components/Category/Category';
import Footer from '@/components/Footer/Footer';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import { options } from '@/config/filter';
import { useQuery } from '@tanstack/react-query';
import { findStory } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';

function FindStory() {
  const [genreCode, setGenreCode] = useState('');

  const navigate = useNavigate();
  const location = useLocation();

  //tách lấy query
  const queryParams = new URLSearchParams(location.search);
  const status = queryParams.get('status');
  const sort = queryParams.get('sort');
  const keyword = queryParams.get('keyword');

  useEffect(() => {
    const checkValidUrl = options.filter((option) =>
      option.path.includes(location.pathname)
    );

    if (checkValidUrl.length === 0) navigate('/404');
  }, [location.pathname, navigate]);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const { data, isLoading } = useQuery({
    queryKey: [queryKey.FIND_STORY, genreCode, status, sort, keyword],
    queryFn: () => findStory(genreCode, status, sort, keyword),
  });

  return (
    <>
      <Head />
      <Navbar />

      <DefaultLayout>
        <Container isBackgroundVisible shouldApplyPadding>
          <BreadCumb />
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-4">
            <div className="lg:col-span-8">
              <Sort data={data} isLoading={isLoading} />
            </div>
            <div className="lg:col-span-4">
              <Category setGenreCode={setGenreCode} />
            </div>
          </div>
        </Container>
      </DefaultLayout>
      <Footer />
    </>
  );
}

export default FindStory;
