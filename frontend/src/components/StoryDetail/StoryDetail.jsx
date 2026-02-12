import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useParams, useSearchParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faList,
  faAngleRight,
  faAngleLeft,
} from '@fortawesome/free-solid-svg-icons';
import { faFileText } from '@fortawesome/free-regular-svg-icons';
import toast from 'react-hot-toast';

import TopStory from '@/components/TopStory/TopStory';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import CommentList from '@/components/CommentList/CommentList';
import StoryInfo from './StoryInfo/StoryInfo';
import ChapterList from './ChapterList/ChapterList';
import createQueryFn from '@/utils/createQueryFn';
import { getStoryById } from '@/api/storyApi';
import { getChaptersBystoryId } from '@/api/chapterApi';
import { getCommentsByStoryId } from '@/api/commentApi';
import { queryKey } from '@/config/queryKey';

function StoryDetail() {
  const { darkTheme } = useSelector((state) => state.theme);
  const { isAuthenticated } = useSelector((state) => state.auth);

  const [seeMore, setSeeMore] = useState(false); // see more description
  const [viewMore, setViewMore] = useState(false); // view more chapter
  const { storyID } = useParams();

  const [searchParams] = useSearchParams();

  const page = searchParams.get('page') || 1;

  const storyQuery = useQuery({
    enabled: !!storyID,
    queryKey: [queryKey.storyDetail(storyID), storyID],
    queryFn: () => getStoryById(storyID),
    onError: (error) => {
      console.log(error);
    },
    staleTime: 0,
  });
  // getChapter
  const chaptersQuery = useQuery({
    enabled: viewMore === false,
    queryKey: ['chapter', storyID],
    queryFn: createQueryFn(getChaptersBystoryId),
    onError: (error) => {
      toast.error('Có lỗi xảy ra. Vui lòng quay lại sau!', {
        style: {
          fontSize: '14px',
        },
        duration: 3000,
        position: 'top-center',
      });
    },
    cacheTime: 0,
  });

  // get comment of story
  const { data: comments } = useQuery({
    queryKey: [queryKey.commentsOfStory(storyID), storyID, page],
    queryFn: createQueryFn(getCommentsByStoryId),
  });

  if (!storyQuery.data) {
    return (
      <div className="text-center">
        <img src="/images/loading/loading.gif" alt="loading" />
      </div>
    );
  }

  return (
    <div className={darkTheme ? 'dark dark:text-gray-200' : ''}>
      {storyQuery.data && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-2">
          {/* LEFT CONTENT */}
          <div className="lg:col-span-8 col-span-12">
            <BreadCumb comicName={storyQuery.data.name} />

            <h1 className="!text-2xl text-center uppercase font-medium">
              {storyQuery.data.name}
            </h1>
            <time className="time-text !pb-2">
              [ Cập nhật lúc: {storyQuery.data.updatedAt}]
            </time>

            <StoryInfo
              story={storyQuery.data}
              isAuthenticated={isAuthenticated}
            />

            <div>
              <h3 className="blue-detail-heading">
                <FontAwesomeIcon icon={faFileText} className="!mr-2" />
                Nội dung
              </h3>

              <p
                className={`relative text-sm overflow-hidden text-ellipsis ${
                  seeMore ? 'line-clamp-none' : 'line-clamp-3'
                }`}
              >
                {storyQuery.data.description}
                <span className={!seeMore ? 'mask-gradient' : ''}></span>
              </p>
              <span
                className="flex items-center gap-1 cursor-pointer text-sm  hover:underline"
                onClick={() => setSeeMore(!seeMore)}
              >
                {!seeMore && <FontAwesomeIcon icon={faAngleLeft} size="xs" />}
                {!seeMore ? 'Xem thêm' : 'Thu gọn'}
                {seeMore && <FontAwesomeIcon icon={faAngleRight} size="xs" />}
              </span>
            </div>

            <div>
              <h3 className="blue-detail-heading">
                <FontAwesomeIcon icon={faList} className="!mr-2" />
                Danh sách chương
              </h3>
            </div>

            <ChapterList
              data={chaptersQuery.data}
              setViewMore={setViewMore}
              viewMore={viewMore}
            />

            {comments && <CommentList data={comments.data} />}
          </div>

          <div className="lg:col-span-4 col-span-12">
            <TopStory />
          </div>
        </div>
      )}
    </div>
  );
}

export default StoryDetail;
