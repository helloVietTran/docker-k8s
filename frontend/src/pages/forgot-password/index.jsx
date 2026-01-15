import React, { useEffect } from 'react';

import ForgotPassword from '@/components/ForgotPassword/ForgotPassword';
import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Footer from '@/components/Footer/Footer';

const ForgotPasswordPage = () => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      <Head />

      <Navbar />
      <ForgotPassword />
      <Footer />
    </>
  );
};

export default ForgotPasswordPage;
