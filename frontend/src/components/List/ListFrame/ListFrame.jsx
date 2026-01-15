import React from 'react';
import PropTypes from 'prop-types';

const ListFrame = ({ children }) => {
  return <div className="text-sm border border-gray-300 p-2.5">{children}</div>;
};

ListFrame.propTypes = {
  children: PropTypes.node.isRequired,
};

export default ListFrame;
