import { useEffect } from 'react';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Login from '@/components/Login/Login';
import Footer from '@/components/Footer/Footer';

function LoginPage() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  return (
    <>
      <Head />
      <Navbar />
      <Login />
      <Footer />
    </>
  );
}

export default LoginPage;
