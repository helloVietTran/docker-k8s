import { useSelector } from 'react-redux';
import { useQuery } from '@tanstack/react-query';
import ListHeading from '@/components/Heading/ListHeading/ListHeading';
import ListFrame from '@/components/List/ListFrame/ListFrame';
import PrimaryListItem from '@/components/List/ListItem/ListItem';

import { getMyFollowedStories } from '@/api/storyApi';
import { getMyInfo } from '@/api/userApi';
import { queryKey } from '@/config/queryKey';

function FollowedComicList() {
  const { isAuthenticated } = useSelector((state) => state.auth);
  const { darkTheme } = useSelector((state) => state.theme);

  const { data: user } = useQuery({
    enabled: isAuthenticated,
    queryKey: [queryKey.MY_INFO],
    queryFn: getMyInfo,
    staleTime: 5 * 60 * 1000,
  });

  const { data: followedStories } = useQuery({
    enabled: !!user?.id,
    queryKey: user ? [queryKey.MY_FOLLOWED_STORIES, user.id] : [],
    queryFn: getMyFollowedStories,
  });

  return (
    <ListFrame>
      <ListHeading title="Truyện đang theo dõi" path="/following" />

      {!isAuthenticated ? (
        <p className={`${darkTheme ? 'text-gray-200' : ''} text-sm`}>
          Vui lòng đăng nhập để sử dụng chức năng theo dõi truyện
        </p>
      ) : followedStories && followedStories.length > 0 ? (
        followedStories.map((item) => (
          <PrimaryListItem data={item.story} key={item.id} />
        ))
      ) : (
        <p className={`${darkTheme ? 'text-gray-200' : ''} text-sm`}>
          Bạn chưa theo dõi truyện nào
        </p>
      )}
    </ListFrame>
  );
}

export default FollowedComicList;
