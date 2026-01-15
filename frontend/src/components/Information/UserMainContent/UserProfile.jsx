import React, { useState, useCallback } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useDropzone } from 'react-dropzone';
import SecondaryHeading from '@/components/Heading/SecondaryHeading/SecondaryHeading';
import PostHeading from '@/components/Heading/PostHeading/PostHeading';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import { getMyInfo, updateUserInfo, uploadAvatar } from '@/api/userApi';
import toast from 'react-hot-toast';
import { queryKey } from '@/config/queryKey';
import { useSelector } from 'react-redux';

function UserProfile() {
  const { darkTheme } = useSelector((state) => state.theme);

  const [infoData, setInfoData] = useState({
    newUserName: '',
    newSurName: '',
    gender: '',
    class: '',
  });

  const [selectedImage, setSelectedImage] = useState(null);
  const queryClient = useQueryClient();

  const { data: user } = useQuery({
    queryKey: [queryKey.MY_INFO],
    queryFn: getMyInfo,
    staleTime: 5 * 60 * 1000,
    retry: 2,
    retryDelay: () => 2000,
  });

  // define mutation
  const updateUserMutate = useMutation({
    mutationFn: updateUserInfo,
    onSuccess: () => {
      toast.success('Cập nhật tài khoản thành công', {
        style: {
          fontSize: '14px',
        },
      });

      queryClient.invalidateQueries([queryKey.MY_INFO]);

      setInfoData({
        newUserName: '',
        newSurName: '',
        gender: '',
        class: '',
      });
    },
    onError: (error) => {
      toast.error('Vui lòng thử lại sau.', {
        style: {
          fontSize: '14px',
        },
      });
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

  const uploadAvatarMutate = useMutation({
    mutationFn: uploadAvatar,
    onSuccess: (data) => {
      queryClient.invalidateQueries(queryKey.MY_INFO);
    },
    onError: (error) => {
      toast.error('Upload avatar thất bại. \n Vui lòng thử lại sau', {
        style: {
          fontSize: '14px',
        },
      });
    },
  });

  // config useDropzone
  // ảnh được mã hóa thành dạng base64 để xem trước ảnh thay vì tải lên server
  const onDrop = useCallback((acceptedFiles) => {
    const file = acceptedFiles[0];
    const reader = new FileReader();

    reader.onload = () => {
      setSelectedImage(reader.result);
    };

    reader.readAsDataURL(file);
  }, []);

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    accept: {
      'image/jpeg': [],
      'image/png': [],
      'image/jpg': [],
      'image/webp': [],
    },
    maxFiles: 1,
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setInfoData({ ...infoData, [name]: value });
  };

  // mã hóa ngược lại ảnh trước khi gửi
  function base64ToFile(base64, filename) {
    const byteString = atob(base64.split(',')[1]);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const uintArray = new Uint8Array(arrayBuffer);

    for (let i = 0; i < byteString.length; i++) {
      uintArray[i] = byteString.charCodeAt(i);
    }

    const blob = new Blob([uintArray], { type: 'image/jpeg' });
    return new File([blob], filename, { type: 'image/jpeg' });
  }

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      infoData.newSurName + infoData.newUserName !== '' ||
      infoData.gender !== ''
    )
      updateUserMutate.mutate({ userId: user.id, infoData });

    if (selectedImage) {
      const formData = new FormData();
      formData.append('file', base64ToFile(selectedImage));
      formData.append('userId', user.id);

      uploadAvatarMutate.mutate(formData);
      setSelectedImage(null);
    }
  };

  return (
    <div className={`${darkTheme ? 'dark' : ''} dark:text-gray-200`}>
      <SecondaryHeading size={1.6} bottom={20} title="Thông tin người dùng" />

      {user && (
        <div className="grid grid-cols-12 gap-6">
          {/* LEFT: Form input */}
          <div className="col-span-12 md:col-span-9">
            <PostHeading title="Cập nhật tài khoản" isRequired bottom={15} />

            <form className="space-y-5">
              <div>
                <label className="block text-sm font-medium">
                  Tên người dùng
                </label>
                <input
                  name="userName"
                  value={user.name}
                  className="form-control mt-2 w-full border px-3 py-2 rounded-md bg-gray-100 text-gray-600 cursor-not-allowed"
                  disabled
                />
              </div>

              <div>
                <label className="block text-sm font-medium">
                  Địa chỉ email
                </label>
                <input
                  name="email"
                  value={user.email}
                  className="form-control mt-2 w-full border px-3 py-2 rounded-md bg-gray-100 text-gray-600 cursor-not-allowed"
                  disabled
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium">
                    Tên <span className="text-red-500">*</span>
                  </label>
                  <input
                    name="newUserName"
                    value={infoData.newUserName}
                    onChange={handleInputChange}
                    placeholder="Tên"
                    className="mt-2 w-full border px-3 py-2 rounded-md"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium">
                    Họ <span className="text-red-500">*</span>
                  </label>
                  <input
                    name="newSurName"
                    value={infoData.newSurName}
                    onChange={handleInputChange}
                    placeholder="Họ"
                    className="mt-2 w-full border px-3 py-2 rounded-md"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium">
                  Giới tính <span className="text-red-500">*</span>
                </label>
                <select
                  name="gender"
                  value={infoData.gender}
                  onChange={handleInputChange}
                  className="mt-2 w-full border px-3 py-2 rounded-md"
                >
                  <option value=""></option>
                  <option value="MALE">Nam</option>
                  <option value="FEMALE">Nữ</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium">
                  Hệ thống tu luyện <span className="text-red-500">*</span>
                </label>
                <select
                  name="class"
                  value={infoData.class}
                  onChange={handleInputChange}
                  className="mt-2 w-full border px-3 py-2 rounded-md"
                >
                  <option value="Ma tu">Ma đạo</option>
                  <option value="Chính đạo">Chính đạo</option>
                </select>
              </div>

              <PrimaryButton
                type="submit"
                onClick={handleSubmit}
                title="Lưu thay đổi"
                color="blue"
              />
            </form>
          </div>

          {/* RIGHT: Avatar uploader */}
          <div className="col-span-12 md:col-span-3">
            <div className="mb-4">
              <span className="block text-sm font-medium mb-1">Avatar</span>
              <img
                src={user?.imgSrc || '/images/anonymous/anonymous.png'}
                alt="avatar"
                className="w-24 h-24 rounded-full object-cover mb-3"
              />

              <div {...getRootProps()} className="mb-2">
                <input {...getInputProps()} />
                <PrimaryButton title="Upload ảnh" color="red" type="button" />
              </div>

              {selectedImage && (
                <img
                  src={selectedImage}
                  alt="Uploaded Preview"
                  className="w-full mt-2 rounded-md border"
                />
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default UserProfile;
