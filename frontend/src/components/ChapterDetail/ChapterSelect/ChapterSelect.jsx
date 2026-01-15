import React from 'react';
import PropTypes from 'prop-types';

const ChapterSelect = ({
  currentChapter,
  lastChap,
  handleChangeSelectedChap,
}) => {
  const makeOptions = () => {
    const options = [];
    for (let i = 1; i <= lastChap; i++) {
      options.push(
        <option key={i} value={i}>
          Chapter {i}
        </option>
      );
    }
    return options;
  };

  return (
    <select
      value={currentChapter}
      onChange={handleChangeSelectedChap}
      className="inline-block w-[120px] md:w-[240px] h-[34px] border border-gray-300 bg-white rounded-sm px-2 text-sm mx-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
    >
      {makeOptions()}
    </select>
  );
};

ChapterSelect.propTypes = {
  currentChapter: PropTypes.number.isRequired,
  lastChap: PropTypes.number.isRequired,
  handleChangeSelectedChap: PropTypes.func.isRequired,
};

export default ChapterSelect;
