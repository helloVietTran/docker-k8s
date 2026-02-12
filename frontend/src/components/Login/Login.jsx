import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useMutation } from '@tanstack/react-query';
import { zodResolver } from '@hookform/resolvers/zod';
import toast from 'react-hot-toast';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import SubmitButton from '@/components/Button/SubmitButton/SubmitButton';
import Input from '@/components/Input/Input';
import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import { login } from '@/api/authApi';
import { login as loginAction } from '@/redux/authSlice';
import { envConstant } from '@/config/envConstant';
import { loginSchema } from '@/schemas/loginSchema';

function Login() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { darkTheme } = useSelector((state) => state.theme);

  const loginMutation = useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      const { result } = data;
      dispatch(loginAction());
      // set token
      localStorage.setItem(envConstant.tokenName, result.accessToken);
      localStorage.setItem(envConstant.refreshTokenName, result.refreshToken);

      navigate('/');
    },
    onError: () => {
      toast.error(
        'Đăng nhập thất bại! Vui lòng kiểm tra lại tài khoản hoặc mật khẩu.',
        {
          style: {
            fontSize: '14px',
          },
        }
      );
    },
    onMutate: () => {
      toast.loading('Đang xử lý', {
        style: {
          fontSize: '14px',
        },
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
  } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: envConstant.default_email,
      password: envConstant.default_password,
    },
  });

  const handleSubmitData = async (data) => {
    loginMutation.mutate(data);
  };

  return (
    <div className={darkTheme ? 'dark' : ''}>
      <DefaultLayout>
        <Container isBackgroundVisible shouldApplyPadding>
          <BreadCumb />

          <div className="grid grid-cols-12">
            <div className="col-span-12 md:col-span-6 md:col-start-4">
              <div className="min-h-[500px]">
                <SecondaryHeading bottom={20} title="Đăng nhập" size={1.8} />
                <form onSubmit={handleSubmit(handleSubmitData)}>
                  <Input
                    label="Email"
                    id="email"
                    type="text"
                    placeholder="VD abc@gmail.com"
                    register={register}
                    errors={errors}
                  />
                  <Input
                    label="Password"
                    id="password"
                    type="password"
                    placeholder="Nhập mật khẩu"
                    register={register}
                    errors={errors}
                  />

                  <div className="flex justify-end mt-2 space-x-2">
                    <Link
                      to="/forgot-password"
                      className="text-sm text-light-blue-heading hover:text-main-purple hover:underline"
                    >
                      Quên mật khẩu
                    </Link>
                    <Link
                      to="/register"
                      className="text-sm text-light-blue-heading hover:text-main-purple hover:underline"
                    >
                      Đăng kí mới
                    </Link>
                  </div>

                  <SubmitButton type="normal" title="Đăng nhập" />
                </form>
              </div>
            </div>
          </div>
        </Container>
      </DefaultLayout>
    </div>
  );
}

export default Login;
