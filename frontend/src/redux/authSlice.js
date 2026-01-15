import { createSlice } from '@reduxjs/toolkit';
import { envConstant } from '@/config/envConstant';

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    isAuthenticated: false,
  },
  reducers: {
    login: (state) => {
      state.isAuthenticated = true;
    },
    logout: (state) => {
      // remove token
      localStorage.removeItem(envConstant.tokenName);
      localStorage.removeItem(envConstant.refreshTokenName);

      state.isAuthenticated = false;
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
