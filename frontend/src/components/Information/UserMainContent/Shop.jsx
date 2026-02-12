import React, { useState, useCallback } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { useSelector } from 'react-redux';

import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import UserNav from '@/components/UserPointTabSwitcher/UserPointTabSwitcher';
import { buyItem, getPoint } from '@/api/pointApi';
import { getAvatarFrame } from '@/api/shopApi';
import { getMyInfo } from '@/api/userApi';

import { getInventory } from '@/api/inventoryApi';

function Shop() {
  const queryClient = useQueryClient();
  const { darkTheme } = useSelector((state) => state.theme);
  const [activeItem, setActiveItem] = useState('option 1');

  const { data: pointData } = useQuery({
    queryKey: ['point'],
    queryFn: getPoint,
  });
  const { data: avatarFrameData } = useQuery({
    queryKey: ['avatarFrame'],
    queryFn: getAvatarFrame,
  });
  const { data: user } = useQuery({
    queryKey: ['userProfile'],
    queryFn: getMyInfo,
    staleTime: 5 * 60 * 1000,
    retry: 3,
    retryDelay: () => 3000,
  });
  const { data: inventory } = useQuery({
    queryKey: ['inventory'],
    queryFn: getInventory,
    retry: 2,
    staleTime: 60 * 1000,
  });

  const handleClick = useCallback((item) => {
    setActiveItem(item);
  }, []);

  const buyItemMutation = useMutation({
    mutationFn: buyItem,
    onSuccess: () => {
      queryClient.invalidateQueries(['inventory']);
      toast.success('Mua thành công', { style: { fontSize: '14px' } });
    },
    onMutate: () => {
      toast.loading('Đang xử lý', {
        style: { fontSize: '14px' },
        id: 'loading',
      });
    },
    onError: () => {
      toast.error('Vui lòng thử lại sau!', { style: { fontSize: '14px' } });
    },
    onSettled: () => {
      toast.dismiss('loading');
    },
  });

  const handleBuyFrame = async (itemId) => {
    if (inventory) return;
    buyItemMutation.mutate({ itemId, userId: user.id });
  };

  return (
    <div
      className={`text-sm ${darkTheme ? 'dark dark:text-gray-200' : 'text-black'}`}
    >
      <SecondaryHeading size={1.6} title="Cửa hàng" bottom={20} />

      <h2 className="text-xl font-semibold mb-5">
        Linh thạch hiện có
        <span className="ml-1 font-bold text-green-600 dark:text-green-400">
          {pointData && pointData.total}
        </span>
      </h2>

      <p className="italic text-[13px] mb-3">
        Chọn vật phẩm cần mua bởi Linh thạch. Viền sẽ được hiển thị khi bạn
        comment.
      </p>

      <UserNav
        options={['Viền avatar', 'Bang hội']}
        onClick={handleClick}
        activeItem={activeItem}
      />

      {activeItem === 'option 1' && (
        <>
          <p className="mb-5">
            Tất cả vật phẩm đều đồng giá{' '}
            <span className="text-red-500 font-semibold">100</span> Linh thạch
            trong 1 ngày
          </p>

          {/* Tailwind grid layout */}
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            {avatarFrameData?.map((frame) => (
              <div key={frame.id} className="flex flex-col items-center">
                <div className="relative w-full aspect-square mb-2">
                  <img
                    alt="avt-default"
                    src="/images/anonymous/avatar_default.jpg"
                    className="w-full h-full object-cover rounded"
                  />
                  <img
                    alt="frame"
                    src={frame.imgSrc}
                    className="absolute top-0 left-0 w-full h-full object-cover transform scale-[1.12] pointer-events-none"
                  />
                </div>
                <PrimaryButton
                  title="Chọn mua"
                  color="yellow"
                  disabled={!!inventory}
                  onClick={() => handleBuyFrame(frame.id)}
                />
              </div>
            ))}
          </div>
        </>
      )}

      {activeItem === 'option 2' && (
        <p className="mt-4 text-gray-600 dark:text-gray-400">
          Chức năng đang được phát triển
        </p>
      )}
    </div>
  );
}

export default Shop;
