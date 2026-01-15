import { useEffect } from 'react';

import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Footer from '@/components/Footer/Footer';
import ComicFilter from '@/components/AdvancedFilters/AdvancedFilters';
import BreadCumb from '@/components/BreadCumb/BreadCumb';

function FindAdvanced() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  return (
    <>
      <Head />

      <Navbar />
      <DefaultLayout>
        <Container shouldApplyPadding isBackgroundVisible>
          <BreadCumb />
          <ComicFilter />
        </Container>
      </DefaultLayout>
      <Footer />
    </>
  );
}

export default FindAdvanced;
