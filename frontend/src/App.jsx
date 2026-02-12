import React, { useEffect } from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import AppRoutes from './routes/AppRoutes';
import { useDispatch } from 'react-redux';
import { fetchMyInfo } from './api/userApi';
import { login } from './redux/authSlice';

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await fetchMyInfo();

        dispatch(login());
      } catch (err) {
        // dont need handle this
        console.log(err);
      }
    };

    checkAuth();
  }, []);

  return (
    <Router>
      <Toaster position="top-center" />
      <AppRoutes />
    </Router>
  );
}

export default App;
