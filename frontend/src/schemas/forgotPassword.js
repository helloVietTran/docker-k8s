import { z } from 'zod';

export const forgotPasswordSchema = z.object({
  email: z.string().min(1, 'Chưa nhập email').email('Email không hợp lệ'),
});
