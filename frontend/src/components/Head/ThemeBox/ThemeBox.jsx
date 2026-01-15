import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLightbulb } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';

import { toggleTheme } from '@/redux/themeSlice';

const ThemeBox = () => {
  const dispatch = useDispatch();
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  const handleToggleTheme = () => {
    dispatch(toggleTheme());
  };

  return (
    <div className="flex items-center justify-center">
      <Link onClick={handleToggleTheme}>
        <FontAwesomeIcon
          icon={faLightbulb}
          className={`text-2xl transition-colors duration-200 ${
            darkTheme ? 'text-white' : 'text-orange-bright'
          }`}
        />
      </Link>
    </div>
  );
};

export default ThemeBox;
