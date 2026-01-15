import { useEffect } from 'react';
import Navbar from '@/components/NavBar/NavBar'; 
import Head from '@/components/Head/Head';
import Footer from '@/components/Footer/Footer';
import UserDetail from '@/components/UserDetail/UserDetail';

function UserPage() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  return (
    <>
      <Head />

      <Navbar />
      <UserDetail />
      <Footer />
    </>
  );
}

export default UserPage;
