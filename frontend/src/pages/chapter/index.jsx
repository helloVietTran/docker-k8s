import { useEffect } from 'react';
import Navbar from '@/components/NavBar/NavBar'; 
import Head from '@/components/Head/Head';
import Footer from '@/components/Footer/Footer';
import ChapterDetail from '@/components/ChapterDetail/ChapterDetail';

function Chapter() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  return (
    <>
      <Head />
      <Navbar isPreventFixed={true} />
      <ChapterDetail />
      <Footer />
    </>
  );
}

export default Chapter;
