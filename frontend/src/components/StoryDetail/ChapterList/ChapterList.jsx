import React, { useEffect, useMemo, useState } from 'react';
import PropTypes from 'prop-types';
import { Link, useParams } from 'react-router-dom';

import getReadingHistoriesFromLocal from '@/utils/getReadingHistoryFromLocal';
import { useSelector } from 'react-redux';

const ChapterList = ({ data, viewMore, setViewMore }) => {
  const { storyID } = useParams();
  const [localReadingHistories, setLocalReadingHistories] = useState([]);

  const { darkTheme } = useSelector((state) => state.theme);

  useEffect(() => {
    setLocalReadingHistories(getReadingHistoriesFromLocal());
  }, []);

  const hasReadChapter = useMemo(
    () => (storyId, chapter) => {
      if (!localReadingHistories) return false;
      return localReadingHistories.some(
        (item) => item.id === +storyId && item.chaptersRead.includes(chapter)
      );
    },
    [localReadingHistories]
  );

  return (
    <div className={`w-full ${darkTheme ? 'dark' : ''}`}>
      <div className="border border-gray-300 dark:border-gray-700 rounded-md">
        <div className="px-2 py-2 font-semibold  text-sm uppercase border-b border-gray-200 dark:border-gray-600">
          <div className="grid grid-cols-12 text-gray-800 dark:text-gray-200">
            <div className="col-span-5">Số chương</div>
            <div className="col-span-4 text-center">Cập nhật</div>
            <div className="col-span-3 text-center">Xem</div>
          </div>
        </div>

        <ul className="px-2 py-1 divide-y divide-dashed divide-gray-200 dark:divide-gray-600">
          {data &&
            data.map((item) => {
              const isRead = hasReadChapter(storyID, item.chap);

              return (
                <li key={item.id} className="py-2">
                  <div className="grid grid-cols-12 items-center text-sm">
                    <div className="col-span-5 truncate">
                      <Link
                        to={`chap-${item.chap}`}
                        className={`hover:text-dark-blue-link-hover dark:hover:text-dark-red-hover ${
                          isRead
                            ? 'text-gray-400'
                            : 'text-gray-800 dark:text-white'
                        }`}
                      >
                        Chapter {item.chap}
                      </Link>
                    </div>

                    <div
                      className={`col-span-4 text-center italic text-xs ${
                        isRead
                          ? 'text-gray-400'
                          : 'text-gray-500 dark:text-gray-300'
                      }`}
                    >
                      {item.updatedAt}
                    </div>

                    <div
                      className={`col-span-3 text-center italic text-xs ${
                        isRead
                          ? 'text-gray-400'
                          : 'text-gray-500 dark:text-gray-300'
                      }`}
                    >
                      {item.viewCount}
                    </div>
                  </div>
                </li>
              );
            })}

          {/* {data.length > 20 && viewMore && (
            <li>
              <button
                onClick={() => setViewMore(false)}
                className="w-full mt-2 py-2 text-center border border-gray-300 hover:text-purple-600 hover:underline dark:border-gray-600"
              >
                <FontAwesomeIcon icon={faPlus} className="mr-1" />
                Xem thêm
              </button>
            </li>
          )} */}
        </ul>
      </div>
    </div>
  );
};

ChapterList.propTypes = {
  viewMore: PropTypes.bool.isRequired,
  setViewMore: PropTypes.func.isRequired,
  data: PropTypes.any,
};

export default ChapterList;
