import React, { useState } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import styles from './Image.module.scss';

const cx = classNames.bind(styles);

const Image = ({ src, alt, loadingGif = '/images/loading/loading.gif' }) => {
  const [isLoading, setIsLoading] = useState(true);
  const handleImageLoad = () => {
    setIsLoading(false);
  };

  return (
    <div className={cx('image-container')}>
      {isLoading && (
        <img src={loadingGif} alt="Loading..." className={cx('loading-gif')} />
      )}
      <img
        src={src}
        alt={alt}
        className={`${cx('responsive-image')} ${isLoading ? cx('hidden') : ''}`}
        onLoad={handleImageLoad}
      />
    </div>
  );
};

Image.propTypes = {
  src: PropTypes.string.isRequired,
  alt: PropTypes.string,
  loadingGif: PropTypes.string,
};

export default Image;
