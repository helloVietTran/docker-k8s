import React from 'react';
import PropTypes from 'prop-types';

const PostHeading = ({ title, size, top, bottom, isRequired }) => {
  return (
    <h2
      className="font-normal border-l-[3px] border-[#ee2c74] pl-[10px] leading-[1.5] min-h-[27px]"
      style={{
        marginBottom: `${bottom}px`,
        marginTop: `${top}px`,
        fontSize: `${size}rem`,
      }}
    >
      {title}
      {isRequired && (
        <span className="text-[1.3rem] italic ml-1">
          (<span className="text-red-500">*</span>) bắt buộc
        </span>
      )}
    </h2>
  );
};

PostHeading.propTypes = {
  title: PropTypes.string.isRequired,
  size: PropTypes.number,
  top: PropTypes.number,
  bottom: PropTypes.number,
  isRequired: PropTypes.bool,
};

export default PostHeading;
