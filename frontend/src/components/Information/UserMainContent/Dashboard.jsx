import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { useQuery } from '@tanstack/react-query';

import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import PostHeading from '@/components/Heading/PostHeading/PostHeading';
import ContentBox from '@/components/Box/ContentBox/ContentBox';
import { getMyInfo } from '@/api/userApi';
import { getPoint } from '@/api/pointApi';
import { useSelector } from 'react-redux';
import { queryKey } from '@/config/queryKey';

function Dashboard() {
  const { darkTheme } = useSelector((state) => state.theme);

  const { data: user } = useQuery({
    queryKey: [queryKey.MY_INFO],
    queryFn: getMyInfo,
    staleTime: 5 * 60 * 1000,
    retry: 2,
    retryDelay: () => ư000,
  });

  const { data: point } = useQuery({
    queryKey: ['point'],
    queryFn: getPoint,
  });

  return (
    <div className={`${darkTheme ? 'dark' : ''} dark:text-gray-200`}>
      <SecondaryHeading size={1.6} title="Thông tin chung" bottom={20} />
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-5">
        <div>
          <div className="mb-5">
            <PostHeading title="Thông tin tài khoản" />
          </div>

          {user && (
            <ContentBox
              bodyContent={
                <>
                  <div className="group">
                    <span className="label">Họ và tên</span>
                    <span className="detail">{user.name}</span>
                  </div>
                  <div className="group">
                    <span className="label">-</span>
                    <Link className="detail">Thông tin công khai</Link>
                  </div>
                  <div className="group">
                    <span className="label">Email</span>
                    <span className="detail">{user.email}</span>
                  </div>

                  <Link
                    to="/secure/userProfile"
                    className="mt-11 text-sm text-blue-detail-heading centered-icon-text hover:text-dark-red-hover transition-colors duration-300"
                  >
                    Chỉnh sửa <FontAwesomeIcon icon={faAngleRight} />
                  </Link>
                </>
              }
            />
          )}
        </div>

        <div>
          <div className="mb-5">
            <PostHeading title="Trình độ tu luyện" />
          </div>

          {user && (
            <ContentBox
              bodyContent={
                <>
                  <div className="skill-box">
                    <div className="flex justify-between text-sm mb-1">
                      <span className="text-blue-detail-heading ">
                        Cấp {user.level.level}
                      </span>

                      <span className="text-dark-red-hover">
                        Cấp {user.level.level + 1}
                      </span>
                    </div>

                    <div className="process">
                      <span
                        style={{ width: user.level.process * 100 + '%' }}
                        className="process-bar animate"
                      >
                        {user.level.process * 100}%
                      </span>
                    </div>
                  </div>

                  <div className="group">
                    <span className="label">Trình độ </span>
                    <span className="detail red">{user.level.rankName}</span>
                  </div>
                  <hr />
                  <div className="group">
                    <span className="label">Số linh thạch</span>
                    <Link className="detail">{point ? point.total : 0}</Link>
                  </div>
                </>
              }
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
