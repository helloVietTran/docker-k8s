import { useState, useEffect, useRef } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import {
  faChevronRight,
  faChevronLeft,
} from '@fortawesome/free-solid-svg-icons';
import { useSelector } from 'react-redux';

import BreadCumb from '@/components/BreadCumb/BreadCumb';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import Container from '@/components/Layout/Container/Container';
import ChapterImageList from './ChapterImageList/ChapterImageList';
import ChapterHeader from './ChapterHeader/ChapterHeader';
import CommentList from '@/components/CommentList/CommentList';
import {
  getChapterByStoryIdAndChap,
  getChapterResource,
  increaseView,
} from '@/api/chapterApi';
import { getStoryById } from '@/api/storyApi';
import { getCommentsByChapterId } from '@/api/commentApi';
import addReadingHistoryOnLocal from '@/utils/addReadingHistoryOnLocal';
import { increaseExperence } from '@/api/levelApi';
import { updateReadingHistory } from '@/api/readingHistoryApi';
import { queryKey } from '@/config/queryKey';

function ChapterDetail() {
  const nextBtnRef = useRef();
  const prevBtnRef = useRef();

  const { darkTheme } = useSelector((state) => state.theme);
  const { isAuthenticated } = useSelector((state) => state.auth);

  const navigate = useNavigate();
  const { storyName, storyID, chap } = useParams();
  const [searchParams] = useSearchParams();
  const page = searchParams.get('page') || 1;

  // Lấy số chapter hiện tại từ URL (dạng: chap-1 -> lấy 1)
  const [currentChapter, setCurrentChapter] = useState(+chap.slice(5));

  // query
  const { data: story, isError: isNotFoundStory } = useQuery({
    enabled: !!storyID,
    queryKey: [queryKey.storyDetail(storyID), storyID],
    queryFn: () => getStoryById(storyID),
    staleTime: 5 * 60 * 1000,
  });

  if (isNotFoundStory) {
    navigate('/not-found');
  }

  const { data: chapter, isError: isNotFoundChapter } = useQuery({
    enabled: !!storyID && !!chap.slice(5),
    queryKey: ['chapter', storyID, parseInt(chap.slice(5))],
    queryFn: () => getChapterByStoryIdAndChap(storyID, parseInt(chap.slice(5))),
  });

  if (isNotFoundChapter) {
    navigate('/not-found');
  }

  // ảnh
  const { data: resource } = useQuery({
    enabled: !!chapter?.id,
    queryKey: [queryKey.resourcesOfChapter(chapter?.id), chapter?.id],
    queryFn: () => getChapterResource(chapter?.id),
  });

  const { data: comments } = useQuery({
    enabled: !!chapter?.id,
    queryKey: [queryKey.commentsOfChapter(chapter?.id), chapter?.id, page],
    queryFn: () => getCommentsByChapterId(chapter?.id, page),
  });

  //  MUTATION
  const increaseViewMutation = useMutation({
    mutationFn: increaseView,
    onSuccess: (data) => {
      console.log(data.result);
    },
  });

  const increaseExperenceMutation = useMutation({
    mutationFn: increaseExperence,
    onSuccess: (data) => {
      console.log(data.result);
    },
  });

  const updateReadingHistoryMutation = useMutation({
    mutationFn: updateReadingHistory,
  });

  useEffect(() => {
    const delay = setTimeout(() => {
      // lưu lịch sử đọc truyện trên local storage
      addReadingHistoryOnLocal(
        {
          id: story.id,
          slug: story.slug,
          name: story.name,
          imgSrc: story.imgSrc,
          updatedAt: story.updatedAt,
          newestChapter: story.newestChapter,
          viewCount: story.viewCount,
        },
        +chap.slice(5)
      );

      // tăng view cho chapter hiện tại
      increaseViewMutation.mutate({
        storyId: storyID,
        chapterId: chapter?.id,
      });

      // Ghi lại lịch sử đọc truyện
      updateReadingHistoryMutation.mutate({
        storyId: storyID,
        chapterRead: +chap.slice(5),
      });

      // đã xscs thực thì tăng exp cho người dùng
      if (isAuthenticated) increaseExperenceMutation.mutate(chapter?.id);
    }, 10000);

    return () => clearTimeout(delay); // clear nếu chưa đọc đủ 10s
  }, [storyID, chap, isAuthenticated]);

  //  HANDLE CHUYỂN CHAPTER
  useEffect(() => {
    navigate(`/story/${storyName}/${storyID}/chap-${currentChapter}`);
  }, [currentChapter, navigate, storyID, storyName]);

  const handleNextChap = () => {
    const nextChapter = currentChapter + 1;
    // Kiểm tra nếu button không bị disable thì chuyển chap
    if (!nextBtnRef.current.classList.contains('control-disabled')) {
      setCurrentChapter(nextChapter);
      window.scrollTo(0, 0);
    }
  };

  const handlePrevChap = () => {
    const prevChapter = currentChapter - 1;
    if (!prevBtnRef.current.classList.contains('control-disabled')) {
      setCurrentChapter(prevChapter);
      window.scrollTo(0, 0);
    }
  };

  return (
    <div className={`py-10 min-h-[400px] bg-[#111] ${darkTheme ? '' : ''}`}>
      {story && chapter && resource && (
        <>
          {/* Header bao gồm tên truyện, chap, chức năng theo dõi, báo lỗi,... */}
          <ChapterHeader
            story={story}
            chapter={chapter}
            currentChapter={currentChapter}
            setCurrentChapter={setCurrentChapter}
            nextBtnRef={nextBtnRef}
            prevBtnRef={prevBtnRef}
            handleNextChap={handleNextChap}
            handlePrevChap={handlePrevChap}
          />

          <ChapterImageList data={resource} />

          {/* Navigation ở dưới cùng (next/prev chap) */}
          <Container
            backgroundColor="#f6f7f9"
            className="rounded-none rounded-b-md"
          >
            <div className="flex justify-center p-2 gap-1 overflow-hidden">
              <PrimaryButton
                disabled={story.newestChapter === currentChapter + 1}
                color="red"
                onClick={handlePrevChap}
                icon={faChevronLeft}
                iconPosition="left"
                title="Chap trước"
              />
              <PrimaryButton
                disabled={
                  story.newestChapter === 1 ||
                  currentChapter === story.newestChapter
                }
                color="red"
                onClick={handleNextChap}
                icon={faChevronRight}
                iconPosition="right"
                title="Chap sau"
              />
            </div>

            <div className="p-4">
              <BreadCumb disabledTheme comicName={story?.name} />
              {comments && <CommentList data={comments.data} />}
            </div>
          </Container>
        </>
      )}
    </div>
  );
}

export default ChapterDetail;
