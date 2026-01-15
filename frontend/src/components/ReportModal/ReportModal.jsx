import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';

import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import { reportChapterError } from '@/api/reportApi';

const ReportModal = ({ onClick, storyName, atChapter }) => {
  const reportMutate = useMutation({
    mutationFn: reportChapterError,
    onSuccess: () => {
      toast.success('Report thành công', {
        style: { fontSize: '14px' },
        duration: 3000,
        position: 'top-center',
      });
    },
    onError: () => {
      toast.error('Vui lòng điền đầy đủ thông tin', {
        style: { fontSize: '14px' },
        duration: 3000,
        position: 'top-center',
      });
    },
    onSettled: () => {
      onClick(false);
    },
  });

  const [report, setReport] = useState({
    type: '',
    description: '',
    storyName,
    atChapter,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setReport({ ...report, [name]: value });
  };

  const handleSubmit = () => {
    reportMutate.mutate(report);
  };

  return (
    <>
      {/* Modal backdrop */}
      <div className="fixed inset-0 bg-black/50 z-50"></div>

      {/* Modal content */}
      <div className="fixed top-1/2 left-1/2 z-50 w-[430px] p-6 bg-white rounded shadow-xl transform -translate-x-1/2 -translate-y-1/2">
        <h2 className="text-xl text-center mb-5 font-normal">Báo lỗi</h2>

        <select
          name="type"
          onChange={handleChange}
          className="form-control w-full h-12 mb-4 px-3 py-2 border border-gray-300 rounded text-sm focus:outline-none"
        >
          <option defaultValue="">--Chọn loại lỗi--</option>
          <option value="Ảnh lỗi, không thấy ảnh">
            Ảnh lỗi, không thấy ảnh
          </option>
          <option value="Chapter bị trùng">Chapter bị trùng</option>
          <option value="Chapter chưa dịch">Chapter chưa dịch</option>
          <option value="Up sai truyện">Up sai truyện</option>
          <option value="Lỗi khác">Lỗi khác</option>
        </select>

        <p className="text-red-600 text-sm leading-6 mb-4">
          Mô tả chính xác lỗi sẽ được thưởng 100 linh thạch
        </p>

        <input
          placeholder="Mô tả..."
          name="description"
          value={report.description}
          onChange={handleChange}
          className="form-control w-full h-12 px-3 py-2 border border-gray-300 rounded text-sm focus:outline-none mb-4"
        />

        <div className="flex justify-end gap-2">
          <PrimaryButton title="Gửi" color="green" onClick={handleSubmit} />
          <PrimaryButton
            title="Hủy"
            color="blue"
            onClick={() => onClick(false)}
          />
        </div>
      </div>
    </>
  );
};

ReportModal.propTypes = {
  onClick: PropTypes.func.isRequired,
  storyName: PropTypes.string.isRequired,
  atChapter: PropTypes.number.isRequired,
};

export default ReportModal;
