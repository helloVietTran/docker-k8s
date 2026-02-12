import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faTimes } from '@fortawesome/free-solid-svg-icons';
import { useSelector } from 'react-redux';

const PrimaryListItem = ({
  data,
  hasDeleteBtn,
  hasViewCount,
  handleDeleteReadingHistory,
}) => {
  const { darkTheme } = useSelector((state) => state.theme);

  return (
    <div
      className={`${darkTheme ? 'dark' : ''} relative !py-2.5 overflow-hidden border-t border-gray-300 capitalize"`}
    >
      <Link to={`/story/${data.slug}/${data.id}`}>
        <img
          alt={data.slug}
          className="absolute h-[55px] w-[55px] object-cover rounded-xs"
          src={data.imgSrc}
        />
      </Link>

      <div className="pl-16">
        <Link
          to={`/story/${data.slug}/${data.id}`}
          className="link-colored !text-sm"
        >
          {data.name}
        </Link>
        <div className="flex-space-between-center mt-1">
          <Link
            to={`/story/${data.slug}/${data.id}/chap-${data.newestChapter}`}
          >
            <span
              className={hasDeleteBtn ? 'read-chapter' : 'dark:!text-gray-200'}
            >
              Chap{' '}
              {!hasDeleteBtn
                ? data.newestChapter
                : Math.max(...data.chaptersRead)}
            </span>
          </Link>
          {!hasDeleteBtn && !hasViewCount && (
            <time className="time-text !text-xs dark:!text-gray-200">
              {data.updatedAt}
            </time>
          )}
          {hasDeleteBtn && (
            <Link
              className="link-colored !text-xs italic hover:underline flex centered-icon-text"
              onClick={() => handleDeleteReadingHistory(data.id)}
            >
              <FontAwesomeIcon icon={faTimes} />
              Xóa
            </Link>
          )}
          {hasViewCount && (
            <span className="text-[#666] dark:text-white text-xs italic !pr-2 flex centered-icon-text">
              <FontAwesomeIcon icon={faEye} />
              {data.viewCount}
            </span>
          )}
        </div>
      </div>
    </div>
  );
};
PrimaryListItem.propTypes = {
  data: PropTypes.object.isRequired,
  hasDeleteBtn: PropTypes.bool,
  hasViewCount: PropTypes.bool,
  handleDeleteReadingHistory: PropTypes.func,
};
export default PrimaryListItem;
