import React from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';
import classNames from 'classnames';

const Container = ({
  children,
  isBackgroundVisible,
  backgroundColor,
  shouldApplyPadding = false,
  className,
}) => {
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  const containerClass = classNames(
    'w-full md:max-w-[750px] lg:max-w-[970px] xl:max-w-[1030px] rounded-t-md',
    {
      'p-4': shouldApplyPadding,
      'bg-dark-container-background': isBackgroundVisible && darkTheme,
      'bg-container-background': isBackgroundVisible && !darkTheme,
    },
    className
  );

  return (
    <div
      className={containerClass}
      style={{ backgroundColor, margin: '0 auto' }}
    >
      {children}
    </div>
  );
};

Container.propTypes = {
  children: PropTypes.node.isRequired,
  isBackgroundVisible: PropTypes.bool,
  shouldApplyPadding: PropTypes.bool,
  backgroundColor: PropTypes.string,
  className: PropTypes.string,
};

export default Container;
