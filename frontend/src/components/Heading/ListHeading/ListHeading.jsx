import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import PrimaryHeading from '../PrimaryHeading/PrimaryHeading';
import { useSelector } from 'react-redux';

const ListHeading = ({ title, path }) => {
  const { darkTheme } = useSelector((state) => state.theme);
  return (
    <div className={`flex items-center justify-between pb-2.5`}>
      <PrimaryHeading title={title} className="!mb-1" />
      <Link
        className={`text-sm italic hover:underline font-medium hover:text-dark-blue-link-hover 
        ${darkTheme ? 'text-gray-200 hover:text-dark-red-hover' : ''}`}
        to={path}
      >
        Tất cả
      </Link>
    </div>
  );
};

ListHeading.propTypes = {
  title: PropTypes.string.isRequired,
  path: PropTypes.string,
};

export default ListHeading;
