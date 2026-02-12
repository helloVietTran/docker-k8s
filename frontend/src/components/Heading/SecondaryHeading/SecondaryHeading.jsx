import React from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

const SecondaryHeading = ({ title, size, top, bottom }) => {
  const { darkTheme } = useSelector((state) => state.theme);

  return (
    <div
      className={`${darkTheme ? 'text-gray-200' : 'text-inherit'}`}
      style={{ marginTop: `${top}px`, marginBottom: `${bottom}px` }}
    >
      <h3
        className="uppercase font-semibold"
        style={{ fontSize: `${size}rem` }}
      >
        {title}
        <span className="block w-[60px] h-1 bg-rose-500 mt-1"></span>
      </h3>
    </div>
  );
};

SecondaryHeading.propTypes = {
  title: PropTypes.string.isRequired,
  size: PropTypes.number.isRequired,
  top: PropTypes.number,
  bottom: PropTypes.number,
};

export default SecondaryHeading;
