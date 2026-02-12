import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAngleDown, faBars } from '@fortawesome/free-solid-svg-icons';
import { useQuery } from '@tanstack/react-query';

import BreadCumb from '@/components/BreadCumb/BreadCumb';
import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import InfoSideBar from './InfomationSidebar/InfomationSidebar';
import UserMainContent from './UserMainContent';
import { getMyInfo } from '@/api/userApi';
import { queryKey } from '@/config/queryKey';

function Information() {
  const [isHided, setIsHided] = useState(false);

  const handleClick = () => {
    setIsHided(!isHided);
  };

  const { data: user } = useQuery({
    queryKey: [queryKey.MY_INFO],
    queryFn: getMyInfo,
    staleTime: 5 * 60 * 1000,
    retry: 2,
    retryDelay: () => 2000,
  });

  return (
    <DefaultLayout>
      <Container shouldApplyPadding isBackgroundVisible>
        <BreadCumb />

        <div className="mt-5 grid grid-cols-1 md:grid-cols-12 gap-4">
          {/* Sidebar */}
          <div className="md:col-span-3">
            <div className="text-sm">
              <section className="bg-[#222] p-3 flex items-center flex-nowrap justify-start">
                <img
                  alt="user"
                  src={
                    user?.imgSrc
                      ? user.imgSrc
                      : '/images/anonymous/anonymous.png'
                  }
                  className="w-[60px] h-[60px] object-cover rounded-full mr-2"
                />
                <div className="p-2">
                  <span className="block text-white opacity-80 truncate">
                    Tài khoản của
                  </span>
                  <span className="block text-white opacity-80 uppercase font-bold truncate overflow-hidden max-w-[100px]">
                    {user?.name}
                  </span>
                </div>
                <FontAwesomeIcon
                  icon={isHided ? faBars : faAngleDown}
                  onClick={handleClick}
                  className="text-white opacity-80 ml-auto"
                />
              </section>
            </div>
            <InfoSideBar isHided={isHided} />
          </div>

          {/* Main Content */}
          <div className="md:col-span-9">
            <UserMainContent />
          </div>
        </div>
      </Container>
    </DefaultLayout>
  );
}

export default Information;
