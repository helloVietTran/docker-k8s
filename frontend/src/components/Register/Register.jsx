import { useForm } from 'react-hook-form';
import { useMutation } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { zodResolver } from '@hookform/resolvers/zod';
import toast from 'react-hot-toast';
import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import SubmitButton from '@/components/Button/SubmitButton/SubmitButton';
import Input from '@/components/Input/Input';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import { login } from '@/api/authApi';
import { login as loginAction } from '@/redux/authSlice';
import { createUser } from '@/api/userApi';
import { registerSchema } from '@/schemas/registerSchema';

function Register() {
  const { darkTheme } = useSelector((state) => state.theme);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const loginMutation = useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      // set token
      const { result } = data;
      localStorage.setItem(envConstant.tokenName, result.accessToken);
      localStorage.setItem(envConstant.refreshTokenName, result.refreshToken);
      // update app state
      dispatch(loginAction());
      navigate('/');
    },
  });

  const createUserMutation = useMutation({
    mutationFn: createUser,
    onSuccess: (variables) => {
      toast.success('Đăng ký thành công!', {
        style: {
          fontSize: '14px',
        },
      });

      // call api login after register success
      loginMutation.mutate({
        email: variables.email,
        password: variables.password,
      });
    },
    onError: () => {
      toast.error('Tài khoản đã tồn tại!', {
        style: {
          fontSize: '14px',
        },
      });
    },
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(registerSchema),
  });

  const handleSubmitData = async (data) => {
    createUserMutation.mutate(data);
  };

  return (
    <DefaultLayout>
      <Container shouldApplyPadding isBackgroundVisible>
        <div className={darkTheme ? 'dark' : ''}>
          <BreadCumb />
          <div className="grid grid-cols-12">
            <div className="col-span-12 md:col-span-6 md:col-start-4">
              <div className="min-h-[500px]">
                <form
                  onSubmit={handleSubmit(handleSubmitData)}
                  className="submit-form"
                >
                  <SecondaryHeading title="Đăng kí" bottom={20} size={1.8} />

                  <Input
                    label="Tên tài khoản"
                    id="name"
                    type="text"
                    placeholder="Nhập tài khoản"
                    register={register}
                    errors={errors}
                  />

                  <Input
                    label="Email"
                    id="email"
                    type="text"
                    placeholder="VD abc@gmail.com"
                    register={register}
                    errors={errors}
                  />

                  <Input
                    label="Mật khẩu"
                    id="password"
                    type="password"
                    placeholder="Nhập mật khẩu"
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

                  <div className="flex justify-end mt-2 space-x-2">
                    <span className="text-sm dark:text-gray-200">
                      Có tài khoản ?
                    </span>
                    <Link
                      to="/login"
                      className="text-sm text-light-blue-heading hover:text-main-purple hover:underline"
                    >
                      Đăng nhập ngay
                    </Link>
                  </div>

                  <SubmitButton type="normal" title="Đăng kí" />
                </form>
              </div>
            </div>
          </div>
        </div>
      </Container>
    </DefaultLayout>
  );
}

export default Register;
