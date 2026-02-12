import React from 'react';
import { Link } from 'react-router-dom';
import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';

import { useQuery } from '@tanstack/react-query';
import { getMyComment } from '@/api/commentApi';
import { queryKey } from '@/config/queryKey';
import { useSelector } from 'react-redux';

function MyComment() {
  const { data: commentData } = useQuery({
    queryKey: [queryKey.MY_COMMENTS],
    queryFn: getMyComment,
    staleTime: 5 * 60 * 1000,
  });

  const { darkTheme } = useSelector((state) => state.theme);
  return (
    <div className={darkTheme ? 'dark dark:text-gray-200' : ''}>
      <SecondaryHeading size={1.6} bottom={20} title="Truyện đang theo dõi" />
      <table className="table-base">
        <thead>
          <tr className="table-header">
            <th colSpan={2} className="px-4 py-2">
              Tên truyện
            </th>
            <th className="px-4 py-2">Chapter</th>
            <th className="px-4 py-2">Thời gian</th>
            <th className="px-4 py-2">Nội dung</th>
          </tr>
        </thead>
        <tbody>
          {commentData?.map((comment) => (
            <tr key={comment.id}>
              <td className="table-cell">
                <Link to="">
                  <img
                    src={comment.story.imgSrc}
                    alt="story-logo"
                    className="table-image"
                  />
                </Link>
              </td>
              <td className="table-cell">
                <Link to="" className="table-story-name">
                  {comment.story.name}
                </Link>
              </td>
              <td className="table-cell">
                <div className="flex-space-between-center ">
                  <Link className="text-sm text-blue-500 hover:underline">
                    Chapter {comment.atChapter}
                  </Link>
                </div>
              </td>
              <td className="table-cell">
                <div className="flex-space-between-center ">
                  <time className="time-text">{comment.createdAt}</time>
                </div>
              </td>
              <td className="table-cell">{comment.content}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default MyComment;
