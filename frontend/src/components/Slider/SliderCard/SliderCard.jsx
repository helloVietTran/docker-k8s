import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClockRotateLeft } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';
import classNames from 'classnames/bind';

import styles from './SliderCard.module.scss';

const cx = classNames.bind(styles);

function SliderCard({ data }) {
  return (
    <div className={cx('story-card')}>
      <Link to={`/story/${data.slug}/${data.id}`}>
        <img
          src={data.imgSrc}
          alt="slider"
          className={cx('story-card__image')}
        />
        <span className={cx('story-card__overlay')}></span>

        <div className={cx('story-card__caption')}>
          <h3 className={cx('story-card__caption-name')}>{data.name}</h3>
          <div className={cx('story-card__caption-desc')}>
            <span className={cx('story-card__caption-desc-chap')}>
              Chapter {data.newestChapter}
            </span>

            <span className={cx('story-card__caption-desc-update')}>
              <FontAwesomeIcon
                icon={faClockRotateLeft}
                className={cx('clock-icon')}
              />
              {data.updatedAt}
            </span>
          </div>
        </div>
      </Link>
    </div>
  );
}

SliderCard.propTypes = {
  data: PropTypes.object.isRequired,
};

export default SliderCard;
