import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useSelector } from 'react-redux';

import ListFrame from '@/components/List/ListFrame/ListFrame';
import ListItem from '@/components/List/ListItem/ListItem';
import TextRank from '@/components/TextRank/TextRank';

import { getTopStories } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';

function TopStory() {
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  const topStoriesQuery = useQuery({
    queryKey: [queryKey.TOP_STORIES],
    queryFn: getTopStories,
    staleTime: 5 * 60 * 1000,
  });

  return (
    <ListFrame>
      <div
        className={`top-story-header ${darkTheme ? 'dark text-gray-200' : ''}`}
      >
        <Link
          to="/"
          className={`top-tab top-tab-active top-tab-dark top-tab-dark-active`}
        >
          Top tháng
        </Link>
        <Link
          to="/find-story?sort=11&status=-1"
          className={`top-tab top-tab-hover top-tab-dark top-tab-dark-hover`}
        >
          Top tuần
        </Link>
        <Link
          to="/find-story?sort=10&status=-1"
          className={`top-tab top-tab-hover top-tab-dark top-tab-dark-hover`}
        >
          Top ngày
        </Link>
      </div>

      {topStoriesQuery.data?.length > 0 &&
        topStoriesQuery.data.map((story, index) => (
          <div className="relative overflow-hidden capitalize" key={story.id}>
            <TextRank index={index} />
            <ListItem hasViewCount data={story} />
          </div>
        ))}
    </ListFrame>
  );
}

export default TopStory;
