import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { useMutation } from '@tanstack/react-query';
import { zodResolver } from '@hookform/resolvers/zod';

import { forgotPasswordSchema } from '@/schemas/forgotPassword';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import Container from '@/components/Layout/Container/Container';
import Input from '@/components/Input/Input';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import { forgotPassword } from '@/api/userApi';

function ForgotPassword() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const style = {
    fontSize: '14px',
  };

  const forgotPasswordMutation = useMutation({
    mutationFn: forgotPassword,
    onSuccess: () => {
      toast.success('Email chứa đường dẫn reset mật khẩu đã được gửi!', {
        style,
      });
    },
    onError: () => {
      toast.error('Đã có lỗi xảy ra. \nVui lòng thử lại sau!', {
        style,
      });
    },
    onMutate: () => {
      toast.loading('Đang xử lý!', {
        style,
        id: 'loading',
      });
    },
    onSettled: () => {
      toast.dismiss('loading');
    },
  });

  const handleSubmitData = async (data) => {
    forgotPasswordMutation.mutate(data);
  };

  return (
    <DefaultLayout>
      <Container isBackgroundVisible shouldApplyPadding>
        <BreadCumb />
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="col-span-1 md:col-span-1 md:col-start-2">
            <form
              onSubmit={handleSubmit(handleSubmitData)}
              className="min-h-[360px]"
            >
              <Input
                label="Email"
                id="email"
                type="text"
                placeholder="VD abc@gmail.com"
                register={register}
                errors={errors}
              />

              <PrimaryButton
                title="Xác nhận"
                type="submit"
                color="blue"
                style={{
                  marginTop: '8px',
                }}
              />
            </form>
          </div>
        </div>
      </Container>
    </DefaultLayout>
  );
}

export default ForgotPassword;
