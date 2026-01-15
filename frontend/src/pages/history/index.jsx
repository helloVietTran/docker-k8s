import { useEffect } from 'react';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Footer from '@/components/Footer/Footer';
import ReadingHistory from '@/components/ReadingHistory/ReadingHistory';

function History() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      <Head />
      <Navbar />
      <ReadingHistory />
      <Footer />
    </>
  );
}

export default History;
