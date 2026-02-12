import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClockFour } from '@fortawesome/free-regular-svg-icons';
import { useSelector } from 'react-redux';

import { queryKey } from '@/config/queryKey';
import { getNewComment } from '@/api/commentApi';
import PrimaryHeading from '../Heading/PrimaryHeading/PrimaryHeading';
import ListFrame from '../List/ListFrame/ListFrame';

function NewComment() {
  const { darkTheme } = useSelector((state) => state.theme);

  const { data } = useQuery({
    queryKey: [queryKey.NEW_COMMENTS],
    queryFn: getNewComment,
  });

  return (
    <ListFrame>
      <PrimaryHeading title="Bình luận mới" className="!mb-1" />
      <div className={darkTheme ? 'dark' : ''}>
        {data &&
          data.data.map((comment) => {
            return (
              <div className="!py-4 border-t border-gray-300" key={comment.id}>
                <div className="flex-space-between-center !mb-2">
                  <Link
                    to={`/story/${comment.story.slug}/${comment.story.id}`}
                    className="text-dark-blue-link-hover dark:text-dark-red-hover hover:underline text-sm font-semibold"
                  >
                    {comment.story.name}
                  </Link>

                  <Link
                    to={`/story/${comment.story.slug}/${comment.story.id}/chap-${comment.atChapter}`}
                    className="unread-chapter !text-xs hover:text-dark-blue-link-hover dark:hover:text-dark-red-hover"
                  >
                    {'Chapter ' + comment.atChapter}
                  </Link>
                </div>
                <div>
                  <img
                    alt="avatar"
                    className="size-[40px] object-cover float-left"
                    src={
                      comment.user.imgSrc || 'images/anonymous/anonymous.png'
                    }
                  />
                  <div className="!pl-12">
                    <div className="flex-space-between-center pb-[10px]">
                      <h3 className="text-sm text-blue-detail-heading dark:text-yellow-300">
                        {comment.user.name}
                      </h3>
                      <span className="time-text !text-xs dark:text-gray-200 centered-icon-text">
                        <FontAwesomeIcon icon={faClockFour} />
                        <span>{comment.createdAt}</span>
                      </span>
                    </div>
                    <p className="dark:text-gray-200">{comment.content}</p>
                  </div>
                </div>
              </div>
            );
          })}
      </div>
    </ListFrame>
  );
}

export default NewComment;
