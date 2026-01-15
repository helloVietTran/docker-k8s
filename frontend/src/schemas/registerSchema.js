import { z } from 'zod';

export const registerSchema = z
  .object({
    name: z
      .string()
      .min(6, 'Tài khoản dài 6-16 kí tự')
      .max(16, 'Tài khoản dài 6-16 kí tự'),

    email: z.string().email('Email không hợp lệ'),

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
