import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().min(1, 'Chưa nhập email').email('Email không hợp lệ'),
  password: z
    .string()
    .min(6, 'Mật khẩu phải dài 6-16 kí tự')
    .max(16, 'Mật khẩu dài 6-16 kí tự')
    .regex(
      /^[a-zA-Z0-9]+$/,
      'Mật khẩu chỉ được chứa chữ cái và số, không có ký tự đặc biệt'
    ),
});
