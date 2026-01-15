import React from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGoogle } from '@fortawesome/free-brands-svg-icons';

const SubmitButton = ({ type, title }) => {
  const baseClasses = 'w-full py-[10px] text-sm font-medium';

  const typeClasses = {
    normal:
      'bg-[#fdd835] text-[#4a4a4a] border border-[#fdd835] my-[15px] cursor-pointer rounded hover:bg-[#fce13a] hover:border-[#fce13a] transition-colors duration-200 ease-in-out',
    google:
      'rounded relative bg-[#dd4b39] text-white border border-[rgba(0,0,0,0.2)] mb-[15px] cursor-pointer hover:bg-[#c23321]',
  };

  return (
    <button
      type="submit"
      className={`${baseClasses} ${typeClasses[type] || ''}`}
    >
      {type === 'google' && (
        <FontAwesomeIcon
          icon={faGoogle}
          className="absolute left-0 pl-1 text-[1.6rem]"
        />
      )}
      {title}
    </button>
  );
};

SubmitButton.propTypes = {
  type: PropTypes.oneOf(['normal', 'google']),
  title: PropTypes.string.isRequired,
};

export default SubmitButton;
