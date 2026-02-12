import React, { useState, useCallback } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { useSelector } from 'react-redux';

import UserPointTabSwitcher from '@/components/UserPointTabSwitcher/UserPointTabSwitcher';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import { attendance, getPoint } from '@/api/pointApi';
import isToday from '@/utils/isToday';
import calculateTime from '@/utils/calculateTime';
import { queryKey } from '@/config/queryKey';

function UserPoint() {
  const { darkTheme } = useSelector((state) => state.theme);
  const queryClient = useQueryClient();

  const [activeItem, setActiveItem] = useState('option 1');

  const { data: pointData } = useQuery({
    queryKey: [queryKey.POINT],
    queryFn: getPoint,
  });

  const attendanceMutation = useMutation({
    mutationFn: attendance,
    onSuccess: () => {
      queryClient.invalidateQueries([queryKey.POINT]);

      toast.success('Điểm danh thành công!', { style: { fontSize: '14px' } });
    },
    onError: () => {
      toast.error('Vui lòng thử lại sau!', { style: { fontSize: '14px' } });
    },
  });

  const handleClick = useCallback((item) => {
    setActiveItem(item);
  }, []);

  const handleAttendance = async () => {
    if (isToday(pointData?.lastAttendanceDate)) return;
    attendanceMutation.mutate();
  };

  return (
    <div className={darkTheme ? 'dark dark:text-gray-200' : ''}>
      <SecondaryHeading size={1.6} title="Linh thạch" bottom={20} />
      <h2 className="text-lg font-bold mb-3">
        Linh thạch hiện có
        <span className="ml-2 text-green-600 text-xl">{pointData?.total}</span>
      </h2>
      <p className="text-sm mb-6">
        Linh thạch thể hiện mức độ Tài Phú của bạn tại Viettruyen, dùng để{' '}
        <span className="text-blue-500 underline dark:text-orange-400">
          mua vật phẩm
        </span>
        . Kiếm Linh thạch bằng cách
        <span className="font-semibold"> Điểm danh</span>
      </p>

      <UserPointTabSwitcher
        options={['Lịch sử', 'Điểm danh']}
        activeItem={activeItem}
        onClick={handleClick}
      />

      {activeItem === 'option 1' && (
        <table className="table-base mt-4">
          <thead>
            <tr className="table-header">
              <th className="px-4 py-2">Thời gian</th>
              <th className="px-4 py-2">Loại</th>
              <th className="px-4 py-2">Linh thạch</th>
            </tr>
          </thead>
          <tbody>
            {pointData?.pointHistories
              ?.slice() // shallow copy
              .reverse()
              .map((item) => (
                <tr key={item.id}>
                  <td className="table-cell">
                    {calculateTime(item.createdAt)}
                  </td>
                  <td className="table-cell">
                    {item.type === 'ATTENDANCE' ? 'Điểm danh' : 'Mua vật phẩm'}
                  </td>
                  <td
                    className={`table-cell ${
                      item.type === 'ATTENDANCE'
                        ? 'text-green-600'
                        : 'text-red-500'
                    }`}
                  >
                    {item?.description}
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      )}

      {activeItem === 'option 2' && (
        <>
          <h3 className="mt-5 mb-2 font-semibold text-lg">Lưu ý</h3>
          <ul className="list-disc list-inside text-sm mb-5">
            <li>Mỗi ngày chỉ được điểm danh 1 lần</li>
            <li>Mỗi lần điểm danh sẽ nhận được 100 Linh thạch</li>
            <li>Điểm danh liên tục 7 ngày sẽ tặng thêm 300 Linh thạch</li>
          </ul>
          <PrimaryButton
            title="Điểm danh"
            color="green"
            onClick={handleAttendance}
            disabled={isToday(pointData?.lastAttendanceDate)}
          />
        </>
      )}
    </div>
  );
}

export default UserPoint;
