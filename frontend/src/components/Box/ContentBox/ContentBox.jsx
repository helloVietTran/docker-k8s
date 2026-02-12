import React from 'react';
import PropTypes from 'prop-types';

const ContentBox = ({ bodyContent }) => {
  return (
    <div className="border border-border-bottom-variant-2 rounded-sm px-4 py-6 shadow-xs">
      {bodyContent}
    </div>
  );
};

ContentBox.propTypes = {
  bodyContent: PropTypes.node.isRequired,
};
export default ContentBox;
