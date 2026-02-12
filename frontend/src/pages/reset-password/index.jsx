import { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

import Head from '@/components/Head/Head';
import Navbar from '@/components/NavBar/NavBar'; 
import Footer from '@/components/Footer/Footer';
import ResetPassword from '@/components/ResetPassword/ResetPassword';

function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const ticket = searchParams.get('ticket');
  const userId = searchParams.get('uid');

  useEffect(() => {
    if (!ticket) navigate('/');

    window.scrollTo(0, 0);
  }, [ticket, navigate]);

  return (
    <>
      <Head />
      <Navbar />
      <ResetPassword token={ticket} userId={userId} />
      <Footer />
    </>
  );
}

export default ResetPasswordPage;
