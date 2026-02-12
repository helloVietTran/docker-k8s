import { useEffect } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

import { options } from '@/config/filter';

function Category({ setGenreCode }) {
  const { darkTheme } = useSelector((state) => state.theme);
  const location = useLocation();

  useEffect(() => {
    if (location.pathname === '/find-story') setGenreCode('');
    else
      options.forEach((genre) => {
        if (location.pathname.includes(genre.path.split('/')[2]))
          setGenreCode(genre.queryCode);
      });
  }, [location, setGenreCode, options]);

  const checkLinkActive = ({ isActive }) =>
    isActive ? '!text-main-purple' : 'hover:!text-main-purple';

  return (
    <>
      <div
        className={`${darkTheme ? 'dark' : ''} text-[#333] !p-3 border border-[#ddd] rounded-sm category`}
      >
        <h2 className="text-base text-light-blue-heading dark:text-orange-bright font-semibold !pb-2">
          Thể loại
        </h2>
        <ul className="category-list dark:text-gray-200">
          {options.map((genre) => (
            <li key={uuidv4()}>
              <NavLink to={genre.path} className={checkLinkActive}>
                {genre.name}
              </NavLink>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}

Category.propTypes = {
  setGenreCode: PropTypes.func.isRequired,
};

export default Category;
