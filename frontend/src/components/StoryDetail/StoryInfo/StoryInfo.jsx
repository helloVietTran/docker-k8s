import React, { useEffect, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import {
  faUser,
  faRss,
  faTags,
  faEye,
  faPlus,
} from '@fortawesome/free-solid-svg-icons';
import toast from 'react-hot-toast';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

import StoryInfoRow from '../StoryInfoRow/StoryInfoRow';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import { getFollowedStoryByStoryId, ratingStory } from '@/api/storyApi';
import { followStory, unfollowStory } from '@/api/userApi';
import { queryKey } from '@/config/queryKey';

const StoryInfo = ({ story, isAuthenticated }) => {
  const { darkTheme } = useSelector((state) => state.theme);
  const queryClient = useQueryClient();

  const [isFollowed, setIsFollowed] = useState(false);
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const { storyID } = useParams();

  // query
  const { data: myFollowedStory, isSuccess } = useQuery({
    enabled: !!storyID,
    queryKey: ['my-followed-story', storyID],
    queryFn: () => getFollowedStoryByStoryId(storyID),
    retryDelay: () => 10000,
  });

  // check
  useEffect(() => {
    if (isSuccess) setIsFollowed(true);
    else setIsFollowed(false);
  }, [isSuccess]);

  const toastStyles = {
    fontSize: '14px',
  };

  // mutation
  const followMutation = useMutation({
    mutationFn: followStory,
    onSuccess: () => {
      queryClient.invalidateQueries(['my-followed-story', storyID]);

      queryClient.invalidateQueries({
        queryKey: [queryKey.MY_FOLLOWED_STORIES],
      });

      setIsFollowed(true);
    },
    onError: (error) => {
      toast.error('Có lỗi xảy ra. Vui lòng quay lại sau!', {
        style: toastStyles,
        duration: 3000,
        position: 'top-center',
      });
    },
  });

  const unfollowMutation = useMutation({
    mutationFn: unfollowStory,
    onSuccess: () => {
      queryClient.invalidateQueries(['my-followed-story', storyID]);

      queryClient.invalidateQueries({
        queryKey: [queryKey.MY_FOLLOWED_STORIES],
      });

      setIsFollowed(false);
    },
    onError: () => {
      toast.error('Có lỗi xảy ra. Vui lòng quay lại sau!', {
        style: toastStyles,
        duration: 3000,
        position: 'top-center',
      });
    },
  });

  // chức năng đánh giá
  const ratingMutation = useMutation({
    mutationFn: ratingStory,
    onSuccess: () => {
      toast.success('Rating thành công!', {
        style: toastStyles,
      });

      queryClient.invalidateQueries({
        queryKey: [queryKey.storyDetail(storyID), storyID],
      });
    },
    onError: (error) => {
      toast.error('Có lỗi xảy ra. Vui lòng quay lại sau!', {
        style: toastStyles,
      });
    },
  });

  const totalStars = 5;

  const handleMouseEnter = (index) => {
    setHoverRating(index);
  };

  const handleMouseLeave = () => {
    setHoverRating(0);
  };

  const handleClick = (index) => {
    setRating(index);
    ratingMutation.mutate({ storyId: storyID, point: index });
  };

  // handle follow, unfollow
  const handleFollowStory = async (id) => {
    if (!isAuthenticated) {
      toast.error('Vui lòng đăng nhập để theo dõi truyện', {
        style: toastStyles,
      });
      return;
    }

    followMutation.mutate(storyID);
  };

  const handleUnFollowStory = async () => {
    if (!myFollowedStory) return;
    unfollowMutation.mutate({
      followId: myFollowedStory.id,
      storyId: storyID,
    });
  };

  const genreContent = (
    <>
      {story.genres &&
        story.genres.map((item, index) => {
          return (
            <span key={item}>
              <Link>{item}</Link>
              {index !== story.genres.length - 1 && <span> - </span>}
            </span>
          );
        })}
    </>
  );

  return (
    <div className={`${darkTheme ? 'dark' : ''}`}>
      <div className="grid grid-cols-1 sm:grid-cols-12 gap-4">
        <div className="sm:col-span-4">
          <img
            src={story.imgSrc}
            alt="story"
            className="w-full object-cover rounded-sm"
          />
        </div>

        <div className="sm:col-span-8">
          <StoryInfoRow
            label="Tác giả"
            content={!story.authorName ? 'Đang cập nhật' : story.authorName}
            icon={faUser}
          />

          {story.otherName && (
            <StoryInfoRow
              label="Tên khác"
              icon={faPlus}
              content={story.otherName}
            />
          )}

          <StoryInfoRow
            label="Tình trạng"
            content={story.status}
            icon={faRss}
          />

          <StoryInfoRow
            label="Thể loại"
            icon={faTags}
            content={genreContent}
            key={'xyz'}
          />

          <StoryInfoRow
            label="Lượt xem"
            icon={faEye}
            content={story.viewCount.toLocaleString()}
          />

          <div className="text-sm !mb-1">
            <span>
              Xếp hạng:
              <span>{' ' + story.rate}</span>/<span>5</span>-
              <span>{' ' + story.ratingCount + ' '}</span>
              Lượt đánh giá
            </span>
          </div>

          <div className="flex gap-1 !mb-2">
            {Array.from({ length: totalStars }, (_, index) => {
              const starIndex = index + 1;
              return (
                <img
                  className="!size-6 cursor-pointer"
                  key={starIndex}
                  src={
                    starIndex <= (hoverRating || rating)
                      ? '/images/rating/star-on.png'
                      : '/images/rating/star-off.png'
                  }
                  alt="star"
                  onMouseEnter={() => handleMouseEnter(starIndex)}
                  onMouseLeave={handleMouseLeave}
                  onClick={() => handleClick(starIndex)}
                />
              );
            })}
          </div>

          <>
            {isFollowed ? (
              <PrimaryButton
                color="red"
                title="Bỏ theo dõi"
                onClick={handleUnFollowStory}
              />
            ) : (
              <PrimaryButton
                color="green"
                onClick={handleFollowStory}
                title="Theo dõi"
              />
            )}

            <strong className="ml-1">
              {story.follower.toLocaleString() || 0}
            </strong>
            <span> Lượt theo dõi</span>
          </>

          <div className="flex !gap-2 !mt-2">
            <Link to={`/story/${story.slug}/${story.id}/chap-1`}>
              <PrimaryButton color="green" title="Đọc từ đầu" />
            </Link>
            <Link
              to={`/story/${story.slug}/${story.id}/chap-${story.newestChapter}`}
            >
              <PrimaryButton color="yellow" title="Đọc mới nhất" />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
StoryInfo.propTypes = {
  isAuthenticated: PropTypes.bool.isRequired,
  story: PropTypes.object.isRequired,
};
export default StoryInfo;
