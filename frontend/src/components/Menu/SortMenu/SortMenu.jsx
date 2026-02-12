import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faList,
  faEye,
  faRefresh,
  faThumbsUp,
  faCloudDownload,
  faSignal,
  faComment,
} from '@fortawesome/free-solid-svg-icons';

function SortMenu() {
  return (
    <div className="absolute top-full left-0 w-[300px] bg-white border border-gray-300 shadow-lg z-[1000] text-sm">
      <div className="flex flex-col sm:flex-row">
        <ul className="w-full border-r border-gray-200">
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=10&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faEye} className="pr-1" />
              Top all
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=10&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faEye} className="pr-1" />
              Top tháng
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=11&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faEye} className="pr-1" />
              Top tuần
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=12&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faEye} className="pr-1" />
              Top ngày
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=22&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faList} className="pr-1" />
              Số chapter
            </Link>
          </li>
        </ul>

        <ul className="w-full">
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?status=1"
              className="block w-full px-4 py-2 font-semibold text-red-600 transition hover:bg-gray-100"
            >
              <FontAwesomeIcon icon={faSignal} className="pr-1" />
              Truyện full
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=20&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faThumbsUp} className="pr-1" />
              Yêu thích
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=1&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faRefresh} className="pr-1" />
              Mới cập nhật
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story?sort=2&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faCloudDownload} className="pr-1" />
              Truyện mới
            </Link>
          </li>
          <li className="border-b border-gray-200">
            <Link
              to="/find-story/action?sort=21&status=-1"
              className="block w-full px-4 py-2 transition hover:bg-gray-100 hover:text-blue-600"
            >
              <FontAwesomeIcon icon={faComment} className="pr-1" />
              Bình luận
            </Link>
          </li>
        </ul>
      </div>
    </div>
  );
}

export default SortMenu;
