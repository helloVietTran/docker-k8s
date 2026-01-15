import React from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useSelector } from 'react-redux';
import classNames from 'classnames';

const PrimaryHeading = ({ title, icon, className }) => {
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  const combinedClass = classNames('flex gap-2 items-center', className, {
    dark: darkTheme,
  });

  return (
    <div className={combinedClass}>
      <h3 className="text-xl font-medium text-light-blue-heading dark:!text-orange-bright">
        {title}
      </h3>
      {icon && (
        <FontAwesomeIcon
          icon={icon}
          className="text-light-blue-heading dark:!text-orange-bright"
        />
      )}
    </div>
  );
};

PrimaryHeading.propTypes = {
  title: PropTypes.string.isRequired,
  icon: PropTypes.object,
  className: PropTypes.string,
};

export default PrimaryHeading;
