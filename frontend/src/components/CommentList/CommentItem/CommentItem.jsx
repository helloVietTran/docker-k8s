import React, { useState } from 'react';
import {
  faAngleLeft,
  faAngleUp,
  faComment,
  faMailForward,
  faThumbsDown,
  faThumbsUp,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useParams, useSearchParams } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';

import LevelBox from '@/components/Box/LevelBox/LevelBox';
import CommentForm from '../CommentForm/CommentForm';
import { dislikeComment, likeComment } from '@/api/reactionApi';
import { queryKey } from '@/config/queryKey';

const CommentItem = ({ data: comment, isReply = false }) => {
  const { isAuthenticated } = useSelector((state) => state.auth);
  const [openCommentForms, setOpenCommentForms] = useState({});

  const darkTheme = useSelector((state) => state.theme.darkTheme);
  const queryClient = useQueryClient();

  const { storyID } = useParams();
  const [searchParams] = useSearchParams();

  const page = searchParams.get('page') || 1;

  const handleReplyClick = (commentId) => {
    setOpenCommentForms((prevState) => ({
      ...prevState,
      [commentId]: !prevState[commentId],
    }));
  };

  const handleCloseForm = (commentId) => {
    setOpenCommentForms((prevState) => ({
      ...prevState,
      [commentId]: false,
    }));
  };

  const style = {
    fontSize: '14px',
  };

  // define mutation
  const likeMutation = useMutation({
    mutationFn: likeComment,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [queryKey.commentsOfStory(storyID)],
      });
    },
    onError: () => {
      toast.error('Vui lòng thử lại sau!', {
        style,
      });
    },
  });

  const dislikeMutation = useMutation({
    mutationFn: dislikeComment,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [queryKey.commentsOfStory(storyID)],
      });
    },
    onError: () => {
      toast.error('Vui lòng thử lại sau!', {
        style,
      });
    },
  });

  // reaction
  const handleLikeComment = (commentId) => {
    if (!isAuthenticated) {
      toast.error('Vui lòng đăng nhập!', {
        style,
      });
      return;
    }
    likeMutation.mutate(commentId);
  };

  const handleDisLikeComment = (commentId) => {
    if (!isAuthenticated) {
      toast.error('Vui lòng đăng nhập!', {
        style,
      });
      return;
    }

    dislikeMutation.mutate(commentId);
  };

  return (
    <div
      className={`py-3 ${isReply ? 'pl-14' : ''} ${darkTheme ? 'dark' : ''} `}
      key={comment.id}
    >
      {/* Avatar */}
      <figure className="relative float-left h-10 w-10 overflow-hidden rounded-full">
        <Link to={`/users/${comment.user.id}`}>
          <img
            src={comment.user.imgSrc || '/images/anonymous/anonymous.png'}
            alt="avatar"
            className="h-full w-full object-cover"
          />

          {comment.user?.frame && (
            <img
              className="absolute top-0 left-0 transform scale-110"
              src={comment.user.frame}
              alt="frame"
            />
          )}
        </Link>
      </figure>

      {/* Comment Box */}
      <div className="ml-14 relative">
        <FontAwesomeIcon
          icon={isReply ? faAngleUp : faAngleLeft}
          className={`absolute text-gray-400 text-sm bg-transparent ${
            isReply ? 'top-[-9px] left-[5px]' : 'top-[5px] left-[-6px]'
          }`}
        />

        <div className="border border-gray-300 bg-white dark:bg-gray-800 p-2 text-sm rounded">
          {/* Header */}
          <div className="border-b border-gray-300 pb-1 flex flex-wrap items-center">
            <Link
              className="text-blue-detail-heading dark:text-yellow-400 font-semibold text-[13px] mr-2 hover:underline hover:text-purple-600 "
              to={`/users/${comment.user.id}`}
            >
              {comment.user.name}
            </Link>

            <LevelBox
              process={comment.user.level.process}
              level={comment.user.level.level}
            />

            <span className="link-colored ml-2 italic !text-xs">{`Chapter ${comment.atChapter}`}</span>
          </div>

          {/* Comment Body */}
          <div className="py-2 break-words text-gray-800 dark:text-gray-100">
            {isReply && (
              <span className="text-orange-500 inline-block mr-1 italic">
                <FontAwesomeIcon icon={faMailForward} className="mr-1" />
                {comment.replyTo}
              </span>
            )}
            {comment.content}
          </div>

          {/* Footer */}
          <ul className="flex flex-wrap items-center text-blue-detail-heading dark:text-gray-200 text-[13px] space-x-4 mt-1">
            <li>
              <button
                onClick={() => handleReplyClick(comment.id)}
                className="hover:underline"
              >
                <FontAwesomeIcon icon={faComment} className="mr-1" />
                Trả lời
              </button>
            </li>

            <li>
              <button
                onClick={() => handleLikeComment(comment.id)}
                className="hover:underline"
              >
                <FontAwesomeIcon icon={faThumbsUp} className="mr-1" />
                {comment.likeCount || 0}
              </button>
            </li>

            <li>
              <button
                onClick={() => handleDisLikeComment(comment.id)}
                className="hover:underline"
              >
                <FontAwesomeIcon icon={faThumbsDown} className="mr-1" />
                {comment.dislikeCount || 0}
              </button>
            </li>

            <li>
              <abbr className="text-gray-400 text-xs whitespace-nowrap border-b border-dotted">
                {comment.createdAt}
              </abbr>
            </li>
          </ul>
        </div>
      </div>

      {/* Reply Form */}
      {openCommentForms[comment.id] && (
        <CommentForm
          hasDistance
          onClose={handleCloseForm}
          atChapter={comment.atChapter}
          replyTo={comment.user.name}
          parentCommentId={comment.id}
        />
      )}
    </div>
  );
};

CommentItem.propTypes = {
  data: PropTypes.object.isRequired,
  reply: PropTypes.bool,
};

export default CommentItem;
