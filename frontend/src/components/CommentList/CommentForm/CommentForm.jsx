import { useState, useRef } from 'react';
import classNames from 'classnames/bind';
import { useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import PropTypes from 'prop-types';
import { useParams } from 'react-router-dom';

import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { postComment } from '@/api/commentApi';
import { queryKey } from '@/config/queryKey';

function CommentForm({
  hasDistance = false,
  parentCommentId,
  atChapter,
  replyTo,
  onClose,
}) {
  const [content, setContent] = useState('');
  const inputRef = useRef();

  const { storyID } = useParams();
  const queryClient = useQueryClient();
  const { isAuthenticated } = useSelector((state) => state.auth);
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  //define mutation
  const style = {
    fontSize: '14px',
  };

  const postCommentMutation = useMutation({
    mutationFn: postComment,
    onSuccess: () => {
      toast.success('Comment thành công!', {
        style,
      });
      setContent('');
      queryClient.invalidateQueries({
        queryKey: [queryKey.commentsOfStory(storyID)],
      });
      if (onClose && parentCommentId) onClose(parentCommentId);
    },
    onError: () => {
      toast.error('Vui lòng thử lại sau!', {
        style,
      });
    },
  });

  const handleSubmitComment = async () => {
    if (!isAuthenticated) {
      toast.error('Vui lòng đăng nhập để bình luận!', {
        style,
      });
      return;
    }
    if (!content.trim()) {
      toast.error('Nội dung bình luận không được để trống!', {
        style,
      });
      return;
    }

    postCommentMutation.mutate({
      atChapter: atChapter || 1,
      content,
      parentCommentId: parentCommentId || null,
      storyId: storyID,
      replyTo: replyTo || null,
    });
  };

  // clean comment
  const handleClearContent = () => {
    setContent('');
    inputRef.current.focus();
  };

  return (
    <div
      className={classNames(
        'text-sm text-gray-800',
        hasDistance && 'pl-[55px] mt-3',
        darkTheme ? 'dark' : ''
      )}
    >
      <div className="w-full">
        <textarea
          type="text"
          value={content}
          name="content"
          placeholder={replyTo ? `Trả lời ${replyTo}` : 'Viết bình luận...'}
          onChange={(e) => setContent(e.target.value)}
          ref={inputRef}
          className="w-full min-h-[100px] p-3 text-base text-gray-800 dark:text-gray-200 border border-gray-300 rounded-lg resize-y outline-none  focus:border-blue-500 dark:focus:border-yellow-500 focus:shadow-[0_0_8px_rgba(74,144,226,0.4)] placeholder:text-gray-400 placeholder:italic"
        />
      </div>
      <div className="mt-1 flex gap-2">
        <PrimaryButton
          color={darkTheme ? 'yellow' : 'blue'}
          title="Gửi"
          onClick={handleSubmitComment}
        />
        <PrimaryButton
          color="default"
          title="Xóa"
          onClick={handleClearContent}
        />
      </div>
    </div>
  );
}

CommentForm.propTypes = {
  hasDistance: PropTypes.bool,
  replyTo: PropTypes.string,
  parentCommentId: PropTypes.string,
  atChapter: PropTypes.number,
  onClose: PropTypes.func,
};

export default CommentForm;
