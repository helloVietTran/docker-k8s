import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faComment } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';

import CommentForm from './CommentForm/CommentForm';
import CommentItem from './CommentItem/CommentItem';

function CommentList({ data }) {
  return (
    <>
      <ul className="my-2 text-sm flex border-b border-gray-300">
        <li className=" mr-px mb-[-1px] h-full leading-[1.43] border border-transparent px-4 py-2 text-sm border-b-transparent border-t-[2px] border-t-purple-800 bg-white text-gray-700 cursor-default">
          <FontAwesomeIcon icon={faComment} className="mr-1" />
          VietTruyen
        </li>
      </ul>

      <div className="py-2">
        <CommentForm />

        <div className="text-sm">
          {data &&
            data.map((comment) => (
              <div key={comment.id}>
                <CommentItem data={comment} />
                {comment.replies &&
                  comment.replies.map((reply) => (
                    <CommentItem data={reply} isReply key={reply.id} />
                  ))}
              </div>
            ))}
        </div>
      </div>
    </>
  );
}

CommentList.propTypes = {
  data: PropTypes.array,
};
export default CommentList;
