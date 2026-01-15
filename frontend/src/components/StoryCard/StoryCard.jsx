import React, { useMemo } from 'react';
import { Link } from 'react-router-dom';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faComment,
  faEye,
  faHeart,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';
import { useMutation, useQueryClient } from '@tanstack/react-query';

import formatNumber from '@/utils/formatNumber';
import { unfollowStory } from '@/api/userApi';
import toast from 'react-hot-toast';
import { queryKey } from '@/config/queryKey';

// data có type StoryReponse
function StoryCard({ data, readingHistoryData, followId = '' }) {
  const { darkTheme } = useSelector((state) => state.theme);
  const queryClient = useQueryClient();

  const hasReadChapter = useMemo(
    () => (storyId, chapter) => {
      if (!readingHistoryData) return false;
      return readingHistoryData.some(
        (item) => item.id === storyId && item.chaptersRead.includes(chapter)
      );
    },
    [readingHistoryData]
  );

  const unfollowMutation = useMutation({
    mutationFn: unfollowStory,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [queryKey.MY_FOLLOWED_STORIES],
      });

      toast.success('Bỏ theo dõi thành công!', {
        style: {
          fontSize: '14px',
        },
        duration: 3000,
        position: 'top-center',
      });
    },
    onError: () => {
      toast.error('Có lỗi xảy ra. Vui lòng quay lại sau!', {
        style: {
          fontSize: '14px',
        },
        duration: 3000,
        position: 'top-center',
      });
    },
  });

  const handleUnfollowStory = () => {
    unfollowMutation.mutate({ storyId: data.id, followId });
  };

  return (
    <div className={`!mt-3 ${darkTheme ? 'dark' : ''}`}>
      <div className="relative">
        <Link to={`/story/${data.slug}/${data.id}`}>
          {data.hot && <span className="icon-hot"></span>}
          <img
            className="object-cover w-full h-[200px] border border-gray-300 rounded shadow-sm"
            src={data.imgSrc}
            alt={data.slug}
          />
        </Link>

        <div className="absolute inset-x-0 bottom-0 flex justify-around h-[25px] px-1 bg-black opacity-65 leading-[25px]">
          <div className="flex gap-1 items-center px-1 text-white text-sm">
            <FontAwesomeIcon icon={faEye} />
            <span>{formatNumber(data.viewCount)}</span>
          </div>

          <div className="flex gap-1 items-center px-1 text-white text-sm">
            <FontAwesomeIcon icon={faComment} />
            <span>{formatNumber(data.commentCount)}</span>
          </div>

          <div className="flex gap-1 items-center px-1 text-white text-sm">
            <FontAwesomeIcon icon={faHeart} />
            <span>{formatNumber(data.follower)}</span>
          </div>
        </div>
      </div>

      <Link
        to={`/story/${data.slug}/${data.id}`}
        className="link-colored capitalize"
      >
        {data.name}
      </Link>

      {followId ? (
        <div className="text-xs flex items-center justify-end mt-1">
          <span
            className="text-red-600 flex items-center gap-1 cursor-pointer hover:underline"
            onClick={handleUnfollowStory}
          >
            <FontAwesomeIcon icon={faXmark} />
            Bỏ theo dõi
          </span>
        </div>
      ) : (
        <></>
      )}
      <ul>
        {data?.chapters ? (
          data.chapters.map((chapter) => {
            return (
              <li
                className={
                  hasReadChapter(data.id, chapter.chap)
                    ? 'read-chapter'
                    : 'unread-chapter'
                }
                key={chapter.id}
              >
                <Link
                  to={`/story/${data.slug}/${data.id}/${chapter.slug}`}
                  className="text-[13px] font-normal theme-hover"
                >
                  Chap {chapter.chap}
                </Link>
                <span className="text-[11px] italic font-normal">
                  {chapter.createdAt}
                </span>
              </li>
            );
          })
        ) : (
          <li
            className={
              hasReadChapter(data.id, chapter.chap)
                ? 'read-chapter'
                : 'unread-chapter'
            }
            key={data.id}
          >
            <span>Chap mới nhất</span>
            <Link
              to={`/story/${data.slug}/${data.id}/chap-${data.newestChapter}`}
            >
              Chapter {data.newestChapter}
            </Link>
          </li>
        )}
      </ul>
    </div>
  );
}

StoryCard.propTypes = {
  data: PropTypes.object.isRequired,
  readingHistoryData: PropTypes.array,
  followId: PropTypes.string,
};
export default StoryCard;

/** Nút đánh dấu đã đọc
 *   <span className="text-green-600 flex items-center gap-1 cursor-pointer hover:underline">
            <FontAwesomeIcon icon={faCheck} />
            Đã đọc
          </span>
 */
