import React from 'react';
import PropTypes from 'prop-types';

const NotifyBox = ({ content, color, bottom = 0, top = 0 }) => {
  const baseClass = 'content-box';
  const colorClass =
    color === 'red'
      ? 'content-box-red'
      : color === 'green'
        ? 'content-box-green'
        : '';

  return (
    <p
      className={`${baseClass} ${colorClass}`}
      style={{ marginBottom: `${bottom}px`, marginTop: `${top}px` }}
    >
      {content}
    </p>
  );
};

NotifyBox.propTypes = {
  content: PropTypes.string.isRequired,
  color: PropTypes.oneOf(['red', 'green']).isRequired,
  bottom: PropTypes.number,
  top: PropTypes.number,
};

export default NotifyBox;
