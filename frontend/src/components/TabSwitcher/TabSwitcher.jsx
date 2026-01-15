import React from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

import './TabSwitcher.css';

const TabSwitcher = ({ firstLabel, secondaryLabel, onClick, activeTab }) => {
  const { darkTheme } = useSelector((state) => state.theme);

  return (
    <ul
      className={`tab-switcher ${darkTheme ? 'dark' : ''} flex justify-center items-center text-base mb-4 border-b border-gray-300 pb-1`}
    >
      <li
        className={`${activeTab === 'tab-1' ? 'active' : ''} px-2`}
        onClick={() => onClick('tab-1')}
      >
        <span className="cursor-pointer">{firstLabel}</span>
      </li>
      <li
        className={`${activeTab === 'tab-2' ? 'active' : ''} px-2`}
        onClick={() => onClick('tab-2')}
      >
        <span className="cursor-pointer">{secondaryLabel}</span>
        <span className="dot">
          <span className="ping"></span>
        </span>
      </li>
    </ul>
  );
};
TabSwitcher.propTypes = {
  firstLabel: PropTypes.string,
  secondaryLabel: PropTypes.string,
  onClick: PropTypes.func,
  activeTab: PropTypes.string,
};

export default TabSwitcher;
