import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAngleRight, faTimes } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

function HistoryCard({ data, handleDeleteReadingHistory }) {
  const { darkTheme } = useSelector((state) => state.theme);
  return (
    <div className="mt-3">
      <div className="relative">
        <Link
          to={`/story/${data.slug || data.story.slug}/${data.id || data.story.id}`}
        >
          <img
            className="object-cover w-full h-[200px] border border-gray-300 rounded"
            src={data.imgSrc || data.story.imgSrc}
            alt={data.imgSrc || data.story.imgSrc}
          />
        </Link>
        <div className="absolute bottom-0 left-0 right-0 flex items-center justify-center h-6 text-xs text-white bg-black/65">
          <span
            className="flex items-center gap-1 cursor-pointer hover:text-yellow-400"
            onClick={() => handleDeleteReadingHistory(data.id)}
          >
            <FontAwesomeIcon icon={faTimes} />
            Xóa
          </span>
        </div>
      </div>

      <div className={darkTheme ? 'dark' : ''}>
        <Link
          to={`/story/${data.slug || data.story.slug}/${data.id || data.story.id}`}
          className="link-colored block"
        >
          {data.name}
        </Link>
        <Link
          to={`/story/${data.slug || data.story.slug}/${data.id || data.story.id}/chap-${Math.max(
            ...data.chaptersRead
          )}`}
          className="text-gray-400 theme-hover flex gap-1 items-center"
        >
          Đọc tiếp Chapter {Math.max(...data.chaptersRead)}
          <FontAwesomeIcon icon={faAngleRight} />
        </Link>
      </div>
    </div>
  );
}

HistoryCard.propTypes = {
  data: PropTypes.object.isRequired,
  handleDeleteReadingHistory: PropTypes.func.isRequired,
};

export default HistoryCard;
