import React from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const PrimaryButton = ({
  title,
  color,
  icon,
  iconPosition,
  onClick,
  disabled,
  type = 'button',
  style,
}) => {
  const baseClasses = ['btn', `btn-${color}`];
  if (disabled) baseClasses.push('btn-disabled');

  return (
    <button
      type={type}
      style={style}
      className={baseClasses.join(' ')}
      onClick={disabled ? undefined : onClick}
      disabled={disabled}
    >
      {icon && iconPosition === 'left' && (
        <span className="!mr-1">
          <FontAwesomeIcon icon={icon} />
        </span>
      )}
      {title}
      {icon && iconPosition === 'right' && (
        <span className="!ml-1">
          <FontAwesomeIcon icon={icon} size="sm" />
        </span>
      )}
    </button>
  );
};

PrimaryButton.propTypes = {
  color: PropTypes.oneOf([
    'red',
    'yellow',
    'green',
    'default',
    'blue',
    'cyan',
    'bright-blue',
  ]).isRequired,
  title: PropTypes.string.isRequired,
  icon: PropTypes.object,
  iconPosition: PropTypes.oneOf(['right', 'left']),
  disabled: PropTypes.bool,
  onClick: PropTypes.func,
  type: PropTypes.string,
  style: PropTypes.object,
};

export default PrimaryButton;
