import React from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useSelector } from 'react-redux';

import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import { getMyFollowedStories } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';

function MyFollowedComic() {
  const { darkTheme } = useSelector((state) => state.theme);

  const { data: followedData } = useQuery({
    queryKey: [queryKey.MY_FOLLOWED_STORIES],
    queryFn: getMyFollowedStories,
    staleTime: 5 * 60 * 1000,
  });

  return (
    <div className={darkTheme ? 'dark dark:text-gray-200' : ''}>
      <SecondaryHeading size={1.6} bottom={20} title="Truyện đang theo dõi" />
      <table className="table-base">
        <thead>
          <tr className="table-header">
            <th colSpan={2} className="px-4 py-2">
              Tên truyện
            </th>
            <th className="px-4 py-2">Chap mới nhất</th>
          </tr>
        </thead>
        <tbody>
          {followedData?.map((follow) => (
            <tr key={follow.id}>
              <td className="table-cell">
                <Link to="/" className="image-link">
                  <img
                    src={follow.story.imgSrc}
                    alt={follow.story.imgSrc}
                    className="table-image"
                  />
                </Link>
              </td>
              <td className="table-cell">
                <Link to="" className="table-story-name">
                  {follow.story.name}
                </Link>
              </td>
              <td className="table-cell">
                <div className="chapter-cell">
                  <Link
                    to={`/story/${follow.story.slug}/${follow.story.id}/chap-${follow.story.newestChapter}`}
                    className="text-sm text-blue-500 hover:underline"
                  >
                    Chap {follow.story.newestChapter}
                  </Link>
                  <time className="time-text">{follow.story.updatedAt}</time>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default MyFollowedComic;
