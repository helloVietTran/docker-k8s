import { z } from 'zod';

export const changePasswordSchema = z
  .object({
    oldPassword: z
      .string()
      .min(6, 'Mật khẩu cũ phải dài 6-16 kí tự')
      .max(16, 'Mật khẩu dài 6-16 kí tự')
      .regex(
        /^[a-zA-Z0-9]+$/,
        'Mật khẩu cũ chỉ được chứa chữ cái và số, không có ký tự đặc biệt'
      ),
    password: z
      .string()
      .min(6, 'Mật khẩu phải dài 6-16 kí tự')
      .max(16, 'Mật khẩu dài 6-16 kí tự')
      .regex(
        /^[a-zA-Z0-9]+$/,
        'Mật khẩu chỉ được chứa chữ cái và số, không có ký tự đặc biệt'
      ),

    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    path: ['confirmPassword'],
    message: 'Nhập lại mật khẩu không chính xác',
  });
