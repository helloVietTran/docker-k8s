import React from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

const TextRank = ({ index }) => {
  const { darkTheme } = useSelector((state) => state.theme);

  const getColorClass = () => {
    switch (index) {
      case 0:
        return 'text-[#3498db]';
      case 1:
        return 'text-[#27ae60]';
      case 2:
        return 'text-[#d35400]';
      default:
        return darkTheme ? 'text-[#a3a3a3]' : 'text-[#3b3b3b]';
    }
  };

  return (
    <div>
      <span
        className={`text-2xl leading-[45px] w-10 text-center float-left border-t border-border-bottom-variant-1 ${getColorClass()}`}
      >
        {index + 1 < 10 ? `0${index + 1}` : index + 1}
      </span>
    </div>
  );
};

TextRank.propTypes = {
  index: PropTypes.number.isRequired,
};

export default TextRank;
