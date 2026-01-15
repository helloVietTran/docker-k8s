import React from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useSelector } from 'react-redux';

const StoryInfoRow = ({ label, icon, content }) => {
  const darkTheme = useSelector((state) => state.theme.darkTheme);
  return (
    <div
      className={`text-base text-[#777676] ${darkTheme ? 'dark' : ''} dark:text-gray-200 `}
    >
      <div className="grid grid-cols-12 items-start !mb-3">
        <div className="col-span-4 flex items-center gap-1">
          <FontAwesomeIcon icon={icon} />
          <span>{label}</span>
        </div>
        <div className="col-span-8">
          <span>{content}</span>
        </div>
      </div>
    </div>
  );
};

StoryInfoRow.propTypes = {
  content: PropTypes.any,
  label: PropTypes.string,
  icon: PropTypes.object,
};

export default StoryInfoRow;
