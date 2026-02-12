import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  darkTheme: JSON.parse(localStorage.getItem('darkTheme')) || false,
};

const themeSlice = createSlice({
  name: 'theme',
  initialState,
  reducers: {
    toggleTheme(state) {
      const newDarkTheme = !state.darkTheme;

      localStorage.setItem('darkTheme', JSON.stringify(newDarkTheme));
      state.darkTheme = newDarkTheme;
    },
  },
});

export const { toggleTheme } = themeSlice.actions;

export default themeSlice.reducer;
