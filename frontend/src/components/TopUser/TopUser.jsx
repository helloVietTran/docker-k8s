import { useQuery } from '@tanstack/react-query';

import PrimaryHeading from '@/components/Heading/PrimaryHeading/PrimaryHeading';
import ListFrame from '@/components/List/ListFrame/ListFrame';
import TextRank from '@/components/TextRank/TextRank';
import LevelBox from '@/components/Box/LevelBox/LevelBox';

import { getTopUsers } from '@/api/userApi';
import { queryKey } from '@/config/queryKey';
import { useSelector } from 'react-redux';

function TopUser() {
  const { darkTheme } = useSelector((state) => state.theme);

  const { data } = useQuery({
    queryKey: [queryKey.TOP_USERS],
    queryFn: getTopUsers,
    staleTime: 5 * 1000,
  });

  return (
    <ListFrame>
      <PrimaryHeading title="Top thành viên" />
      <div className="mt-2 flex flex-col gap-4">
        {Array.isArray(data) &&
          data.map((user, index) => (
            <div
              key={user.id}
              className={`relative text-sm text-gray-800 ${darkTheme ? 'dark dark:text-gray-200' : ''}`}
            >
              <TextRank index={index} />

              <div className="border-t border-gray-300 py-1">
                <img
                  alt="avatar"
                  src={user.imgSrc || 'images/anonymous/anonymous.png'}
                  className="absolute size-11 object-cover left-12 rounded-xs"
                />

                <div className="pl-27 pr-2">
                  <h3 className="text-sm font-normal truncate mb-1">
                    {user.name}
                  </h3>
                  <LevelBox
                    level={user.level.level}
                    process={user.level.process * 100}
                  />
                  <LevelBox point={user.level.rankName} />
                </div>
              </div>
            </div>
          ))}
      </div>
    </ListFrame>
  );
}

export default TopUser;
