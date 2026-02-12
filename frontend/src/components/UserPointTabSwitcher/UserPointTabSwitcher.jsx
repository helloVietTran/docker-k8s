import React from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import { useSelector } from 'react-redux';

const UserPointTabSwitcher = ({ onClick, options, activeItem }) => {
  const { darkTheme } = useSelector((state) => state.theme);
  return (
    <ul
      className={`flex border-b border-gray-300 dark:border-gray-600 my-5 text-sm ${darkTheme ? 'dark' : ''}`}
    >
      {options.map((option, index) => {
        const number = index + 1;
        const isActive = activeItem === `option ${number}`;

        const liClass = classNames(
          'px-4 py-2 cursor-pointer bg-gray-100 dark:bg-gray-700 border border-transparent border-b-0 hover:text-purple-600 dark:hover:text-orange-400',
          {
            'text-gray-700 dark:text-white': !isActive,
            'bg-white dark:bg-gray-800 text-gray-800 dark:text-orange-400 border border-gray-300 dark:border-gray-600 border-b-transparent border-t-2 border-t-purple-700 dark:border-t-red-600 cursor-default':
              isActive,
          }
        );

        return (
          <li
            key={option}
            className={liClass}
            onClick={() => onClick(`option ${number}`)}
          >
            {option}
          </li>
        );
      })}
    </ul>
  );
};

UserPointTabSwitcher.propTypes = {
  onClick: PropTypes.func.isRequired,
  options: PropTypes.array.isRequired,
  activeItem: PropTypes.string.isRequired,
};

export default UserPointTabSwitcher;
