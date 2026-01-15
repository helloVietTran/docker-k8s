import React, { useState } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { useSelector } from 'react-redux';

const Input = ({
  type,
  placeholder,
  config,
  errors,
  label,
  id,
  register,
  onChange,
}) => {
  const { darkTheme } = useSelector((state) => state.theme);
  const [isHidePassword, setIsHidePassWord] = useState(true);
  return (
    <div
      className={`input-container ${darkTheme ? 'dark' : ''}`}
      style={{ position: type === 'password' ? 'relative' : 'unset' }}
    >
      <label htmlFor={id}>{label}</label>
      <input
        id={id}
        placeholder={placeholder}
        type={isHidePassword && type === 'password' ? 'password' : 'text'}
        {...register(id, config)}
        className={errors[id] ? 'invalid' : ''}
        onChange={onChange}
      />
      {errors[id] && <span className={'error'}>{errors[id].message}</span>}

      {type === 'password' && (
        <FontAwesomeIcon
          icon={isHidePassword ? faEye : faEyeSlash}
          onClick={() => setIsHidePassWord(!isHidePassword)}
          className={errors[id] ? 'input-icon' : 'input-icon translateY'}
        />
      )}
    </div>
  );
};

Input.propTypes = {
  type: PropTypes.string,
  placeholder: PropTypes.string,
  config: PropTypes.object,
  label: PropTypes.string.isRequired,
  id: PropTypes.string.isRequired,
  register: PropTypes.func.isRequired,
  errors: PropTypes.object.isRequired,
};

export default Input;
