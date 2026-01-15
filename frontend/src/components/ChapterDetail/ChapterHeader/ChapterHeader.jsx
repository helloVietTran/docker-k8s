import React, { useEffect, useRef, useState } from 'react';
import PropTypes from 'prop-types';
import toast from 'react-hot-toast';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChevronLeft,
  faChevronRight,
  faHeart,
  faHouseChimney,
  faList,
  faTriangleExclamation,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';

import ChapterSelect from '../ChapterSelect/ChapterSelect';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import Container from '@/components/Layout/Container/Container';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import ReportModal from '@/components/ReportModal/ReportModal';

import { useSelector } from 'react-redux';
import { followStory, unfollowStory } from '@/api/userApi';
import { getFollowedStoryByStoryId } from '@/api/storyApi';
import createQueryFn from '@/utils/createQueryFn';
import { queryKey } from '@/config/queryKey';

const ChapterHeader = ({
  story,
  chapter,
  currentChapter,
  setCurrentChapter,
  nextBtnRef,
  prevBtnRef,
  handleNextChap,
  handlePrevChap,
}) => {
  // Refs và states
  const navRef = useRef();
  const queryClient = useQueryClient();

  const [isOpenReportModal, setIsOpenReportModal] = useState(false);
  const [isFixed, setIsFixed] = useState(false);
  const [isFollowed, setIsFollowed] = useState(false);

  const { storyName, storyID } = useParams();
  const { isAuthenticated } = useSelector((state) => state.auth);

  // Truy vấn trạng thái theo dõi truyện
  const { data: myFollowedStory, isSuccess } = useQuery({
    enabled: !!storyID,
    queryKey: [queryKey.MY_FOLLOWED_STORIES, storyID],
    queryFn: createQueryFn(getFollowedStoryByStoryId),
  });

  // Cập nhật trạng thái isFollowed khi truy vấn thành công
  useEffect(() => {
    if (isSuccess) setIsFollowed(true);
    else setIsFollowed(false);
  }, [isSuccess]);

  const toastStyles = { fontSize: '14px' };

  // mutation
  const followMutation = useMutation({
    mutationFn: followStory,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [queryKey.MY_FOLLOWED_STORIES],
      });

      setIsFollowed(true);
    },
    onError: (error) => {
      toast.error('Có lỗi xảy ra. Vui lòng quay lại sau!', {
        style: toastStyles,
        duration: 3000,
        position: 'top-center',
      });
    },
  });

  const unfollowMutation = useMutation({
    mutationFn: unfollowStory,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [queryKey.MY_FOLLOWED_STORIES],
      });

      setIsFollowed(false);
    },
    onError: () => {
      toast.error('Có lỗi xảy ra. Vui lòng quay lại sau!', {
        style: toastStyles,
        duration: 3000,
        position: 'top-center',
      });
    },
  });

  // Sự kiện xử lý scroll để gắn nav bar khi cuộn xuống
  useEffect(() => {
    const handleScroll = () => {
      setIsFixed(window.scrollY >= 240);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Xử lý sự kiện nhấn phím ← hoặc → để chuyển chapter
  const handleKeyDown = (event) => {
    if (
      event.key === 'ArrowRight' &&
      !nextBtnRef.current.classList.contains('control-disabled')
    ) {
      setCurrentChapter((prev) => prev + 1);
    } else if (
      event.key === 'ArrowLeft' &&
      !prevBtnRef.current.classList.contains('control-disabled')
    ) {
      setCurrentChapter((prev) => prev - 1);
    }
  };

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, []);

  // Chuyển chapter khi người dùng chọn từ dropdown
  const handleChangeSelectedChap = (event) => {
    setCurrentChapter(parseInt(event.target.value));
  };

  // Gọi mutation theo dõi truyện
  const handleFollowStory = async (id) => {
    if (!isAuthenticated) {
      toast.error('Vui lòng đăng nhập để theo dõi truyện', {
        style: toastStyles,
      });
      return;
    }

    followMutation.mutate(storyID);
  };

  const handleUnFollowStory = async () => {
    if (!myFollowedStory) return;
    unfollowMutation.mutate({
      followId: myFollowedStory.id,
      storyId: storyID,
    });
  };

  return (
    <Container>
      <div className="pt-4 pb-0 px-4 bg-white border-b border-solid border-gray-200 overflow-hidden rounded-t-md">
        <BreadCumb disabledTheme comicName={story.name} />
        <h1 className="text-xl mb-2">
          <div>
            <span className="font-semibold capitalize">{story.name + ' '}</span>
            <span className="text-lg">- Chap {chapter.chap}</span>
            <span className="text-sm italic ml-3 font-medium text-gray-500">
              Cập nhật lúc: {chapter.updatedAt || ''}
            </span>
          </div>
        </h1>
      </div>

      <div className="bg-chapter-background mb-2">
        {/* Báo lỗi */}
        <div className="py-3 text-center">
          <Link onClick={() => setIsOpenReportModal(true)}>
            <PrimaryButton
              color="yellow"
              title="Báo lỗi"
              icon={faTriangleExclamation}
              iconPosition={'left'}
            />
          </Link>
          {isOpenReportModal && (
            <ReportModal
              onClick={setIsOpenReportModal}
              storyName={story.name}
              atChapter={currentChapter}
            />
          )}
        </div>

        <div className="mb-2 text-center bg-[#d9edf7] text-[#31708f] p-4 border border-[#bce8f1] rounded font-normal text-sm">
          <p>
            Kinh nghiệm sẽ được tính khi bạn đọc một chương đủ 10 giây. Hệ thống
            sẽ đồng bộ kinh nghiệm sau mỗi 5 phút
          </p>
          <em>Mỗi chương cần 5 phút cool down</em>
        </div>

        {/* Navigation chapter */}
        <nav
          className={`flex justify-center items-center h-[42px] ${isFixed ? 'nav-fixed' : ''}`}
          ref={navRef}
        >
          <Link to="/" className="chapter-nav-icon">
            <FontAwesomeIcon icon={faHouseChimney} />
          </Link>

          <Link
            to={`/story/${storyName}/${storyID}`}
            className="chapter-nav-icon"
          >
            <FontAwesomeIcon icon={faList} />
          </Link>

          {/* Chapter control */}
          <button
            className={`btn btn-red ${currentChapter === 1 ? 'btn-disabled' : ''}`}
            onClick={handlePrevChap}
            ref={prevBtnRef}
          >
            <FontAwesomeIcon icon={faChevronLeft} />
          </button>

          <ChapterSelect
            currentChapter={currentChapter}
            lastChap={story.newestChapter}
            handleChangeSelectedChap={handleChangeSelectedChap}
          />

          <button
            className={`btn btn-red mr-2 ${
              story.newestChapter === 1 ||
              currentChapter === story.newestChapter
                ? 'btn-disabled'
                : ''
            }`}
            onClick={handleNextChap}
            ref={nextBtnRef}
          >
            <FontAwesomeIcon icon={faChevronRight} />
          </button>

          {/* Theo dõi / Bỏ theo dõi */}
          {isFollowed ? (
            <PrimaryButton
              color="red"
              title="Bỏ theo dõi"
              onClick={handleUnFollowStory}
              icon={faXmark}
              iconPosition={'right'}
            />
          ) : (
            <PrimaryButton
              color="green"
              title="Theo dõi"
              onClick={handleFollowStory}
              icon={faHeart}
              iconPosition={'right'}
            />
          )}
        </nav>
      </div>
    </Container>
  );
};

// Kiểm tra kiểu props
ChapterHeader.propTypes = {
  story: PropTypes.object.isRequired,
  chapter: PropTypes.object.isRequired,
  currentChapter: PropTypes.number.isRequired,
  setCurrentChapter: PropTypes.func.isRequired,
  nextBtnRef: PropTypes.object.isRequired,
  prevBtnRef: PropTypes.object.isRequired,
  handleNextChap: PropTypes.func.isRequired,
  handlePrevChap: PropTypes.func.isRequired,
};

export default ChapterHeader;
