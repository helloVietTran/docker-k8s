import React from 'react';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { useSelector } from 'react-redux';
import Input from '@/components/Input/Input';
import { zodResolver } from '@hookform/resolvers/zod';

import { changePassword } from '@/api/userApi';
import { changePasswordSchema } from '@/schemas/changePasswordSchema';
import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';

function ChangingPassWord() {
  const { darkTheme } = useSelector((state) => state.theme);
  // define mutationFn
  const toastStyles = {
    fontSize: '14px',
  };
  const changePasswordMutation = useMutation({
    mutationFn: changePassword,
    onSuccess: () => {
      toast.success('Thay đổi mật khẩu thành công', {
        style: toastStyles,
      });
    },
    onError: () => {
      toast.error('Vui lòng thử lại sau ít phút', {
        style: toastStyles,
      });
    },
    onMutate: () => {
      toast.loading('Vui lòng thử lại sau ít phút', {
        style: toastStyles,
        id: 'loading',
      });
    },
    onSettled: () => {
      toast.dismiss('loading');
    },
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
    getValues,
  } = useForm({
    resolver: zodResolver(changePasswordSchema),
    mode: 'onSubmit',
  });

  const handleSubmitData = async (data) => {
    changePasswordMutation.mutate(data);
  };

  return (
    <div className={darkTheme ? 'dark' : ''}>
      <SecondaryHeading size={1.6} title="Đổi mật khẩu" bottom={20} />

      <form onSubmit={handleSubmit(handleSubmitData)} className="submit-form">
        <Input
          label="Mật khẩu"
          id="oldPassword"
          type="password"
          placeholder="Nhập mật khẩu"
          register={register}
          errors={errors}
        />
        <Input
          label="Mật khẩu"
          id="password"
          type="password"
          placeholder="Nhập mật khẩu mới"
          register={register}
          errors={errors}
        />

        <Input
          label="Nhập lại mật khẩu"
          id="confirmPassword"
          type="password"
          placeholder="Nhập lại mật khẩu"
          register={register}
          errors={errors}
        />
        <div className="mt-5">
          <PrimaryButton type="submit" title="Đổi mật khẩu" color="blue" />
        </div>
      </form>
    </div>
  );
}

export default ChangingPassWord;
