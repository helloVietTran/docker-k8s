import { useEffect } from 'react';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Register from '@/components/Register/Register';
import Footer from '@/components/Footer/Footer';

function RegisterPage() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      <Head />
      <Navbar />
      <Register />
      <Footer />
    </>
  );
}

export default RegisterPage;
