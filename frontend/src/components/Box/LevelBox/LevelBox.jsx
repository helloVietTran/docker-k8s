import React from 'react';
import PropTypes from 'prop-types';

const LevelBox = ({ level, point, process = 0 }) => {
  return (
    <span
      className={`relative inline-block px-1 py-[2px] text-xs rounded border border-red-500 mr-2 ${
        point !== undefined
          ? 'min-w-[36px] text-center bg-[#f9f9f9]'
          : 'bg-gray-300'
      }`}
    >
      {point !== undefined && <span className="text-red-500">{point}</span>}
      {level !== undefined && (
        <span className="text-red-500">{'Cấp ' + level}</span>
      )}
      {process !== undefined && (
        <span
          className="absolute top-0 left-0 right-0 bottom-0 bg-[#d0b32e] opacity-50"
          style={{ width: `${process}%` }}
        ></span>
      )}
    </span>
  );
};

LevelBox.propTypes = {
  level: PropTypes.number,
  point: PropTypes.any,
  process: PropTypes.number,
};

export default LevelBox;
