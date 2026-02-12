import { useEffect } from 'react';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Footer from '@/components/Footer/Footer';
import FollowStory from '@/components/FollowingStory/FollowingStory';

function Following() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      <Head />
      <Navbar />
      <FollowStory />
      <Footer />
    </>
  );
}

export default Following;
