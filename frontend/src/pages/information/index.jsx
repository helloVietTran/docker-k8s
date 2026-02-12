import { useEffect } from 'react';

import Navbar from '@/components/NavBar/NavBar'; 
import Head from '@/components/Head/Head';
import Footer from '@/components/Footer/Footer';
import Information from '@/components/Information/Infomation';

function InfomationPage() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      <Head />
      <Navbar />
      <Information />
      <Footer />
    </>
  );
}

export default InfomationPage;
