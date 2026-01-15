import { useState } from 'react';
import { faAngleDoubleUp, faRefresh } from '@fortawesome/free-solid-svg-icons';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useSelector } from 'react-redux';
import StoryCard from '@/components/StoryCard/StoryCard';

import { options } from '@/config/filter';
import { findAdvanced } from '@/api/storyApi';
import PrimaryButton from '@/components/Button/PrimaryButton/PrimaryButton';
import { queryKey } from '@/config/queryKey';
import StoryCardSkeleton from '@/components/StoryCardSkeleton/StoryCardSkeleton';

function AdvancedFilters() {
  const [isOpen, setIsOpen] = useState(true);

  const [checkedItems, setCheckedItems] = useState({}); // Lưu trữ trạng thái chọn/bỏ chọn của các item trong bộ lọc (tick/cross)
  const [minchapter, setMinchapter] = useState('0'); //Số lượng chapter tối thiểu
  const [sort, setSort] = useState('2'); // Tiêu chí sắp xếp thep thời gian
  const [status, setStatus] = useState('-1'); // theo trạng thái
  const [gender, setGender] = useState('-1'); // theo giới tính

  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { darkTheme } = useSelector((state) => state.theme);

  // lấy tham số từ URL
  const genreParam = searchParams.get('genreCodes') || '';
  const notGenreParam = searchParams.get('notGenreCodes') || '';
  const sortParam = searchParams.get('sort') || '-1';
  const genderParam = searchParams.get('gender') || '-1';
  const statusParam = searchParams.get('status') || '2';
  const minChapterParam = searchParams.get('minChapter') || '0';

  const { data, isLoading } = useQuery({
    queryKey: [
      queryKey.FIND_ADVANCED,
      genreParam,
      notGenreParam,
      statusParam,
      sortParam,
      minChapterParam,
      genderParam,
    ],

    queryFn: () =>
      findAdvanced(
        genreParam,
        notGenreParam,
        statusParam,
        sortParam,
        minChapterParam,
        genderParam
      ),
  });

  // Xử lý khi người dùng chọn/bỏ chọn một item trong bộ lọc
  const handleToggle = (index) => {
    setCheckedItems((prevCheckedItems) => {
      const newCheckedItems = { ...prevCheckedItems };

      if (newCheckedItems[index] === 'tick') newCheckedItems[index] = 'cross';
      else if (newCheckedItems[index] === 'cross')
        delete newCheckedItems[index];
      else newCheckedItems[index] = 'tick';

      return newCheckedItems;
    });
  };

  // áp dụng bộ lọc
  const handleAppend = () => {
    const genres = [];
    const notGenres = [];

    Object.entries(checkedItems).forEach(([index, value]) => {
      if (value === 'tick') genres.push(index);
      else if (value === 'cross') notGenres.push(index);
    });

    const queryParams = new URLSearchParams();

    if (genres.length > 0) queryParams.append('genreCodes', genres.join(','));

    queryParams.append('notGenreCodes', notGenres.join(','));

    //nếu là default query thì không đưa lên query param
    if (minchapter !== '0') queryParams.append('minChapter', minchapter);

    if (status !== '-1') queryParams.append('status', status);

    if (sort !== '-1') queryParams.append('sort', sort);

    if (gender !== '0') queryParams.append('gender', gender);

    navigate(`?${queryParams.toString()}`);

    // đặt lại bộ lọc
    setCheckedItems({});
    setStatus('-1');
    setSort('-1');
    setGender('0');
    setMinchapter('0');
  };

  const handleReset = () => {
    setCheckedItems({});
    setStatus('-1');
    setSort('-1');
    setGender('0');
    setMinchapter('0');
  };

  return (
    <div className={darkTheme ? 'dark' : ''}>
      <div className="mb10 text-center">
        <h1 className="text-2xl font-medium dark:text-orange-bright">
          Tìm truyện nâng cao
        </h1>
      </div>
      <div className="my-2 text-center">
        <PrimaryButton
          title={isOpen ? 'Ẩn khung tìm kiếm' : 'Hiện khung tìm kiếm'}
          color="cyan"
          onClick={() => setIsOpen(!isOpen)}
          icon={isOpen ? faAngleDoubleUp : null}
          iconPosition="right"
        />
      </div>

      {isOpen && (
        <div className="text-sm dark:text-gray-300">
          <p className="mb-2 mt-5 flex items-center">
            <span className="icon-tick"></span>
            Tìm trong những thể loại này
          </p>
          <p className="mb-2 flex items-center">
            <span className="icon-cross"></span>
            Loại trừ những thể loại này
          </p>
          <p className="mb-5 relative flex items-center">
            <span className="icon-checkbox"></span>
            Truyện có thể hoặc không thuộc thể loại này
            <PrimaryButton
              title="Reset"
              color="bright-blue"
              onClick={handleReset}
              icon={faRefresh}
              iconPosition="left"
              style={{ position: 'absolute', right: '0px' }}
            />
          </p>

          <div className="grid grid-cols-12 gap-3 !mb-3">
            <div className="md:col-span-2 col-span-12">
              <h3 className="mt-5">Thể loại</h3>
            </div>

            <div className="md:col-span-10 col-span-12">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                {options.slice(1).map((item, index) => {
                  return (
                    <div
                      key={item.name}
                      className="genre-item cursor-pointer"
                      onClick={() => handleToggle(item.queryCode)}
                    >
                      <span
                        className={
                          checkedItems[item.queryCode] === 'tick'
                            ? 'icon-tick'
                            : checkedItems[item.queryCode] === 'cross'
                              ? 'icon-cross'
                              : 'icon-checkbox'
                        }
                      ></span>
                      {item.name}
                    </div>
                  );
                })}
              </div>
            </div>
          </div>

          <div className="grid grid-cols-12 gap-y-4 gap-x-4 mt-6">
            {/* Row 1 */}
            <div className="md:col-span-2 col-span-12">
              <h3 className="mt-1">Số lượng chapter</h3>
            </div>
            <div className="md:col-span-4 col-span-12">
              <select
                className="custom-select"
                value={minchapter}
                onChange={(e) => setMinchapter(e.target.value)}
              >
                <option value="0">&gt; 0 chapter</option>
                <option value="50">&gt;= 50 chapter</option>
                <option value="100">&gt;= 100 chapter</option>
              </select>
            </div>

            <div className="md:col-span-2 col-span-12">
              <h3 className="mt-1">Tình trạng</h3>
            </div>
            <div className="md:col-span-4 col-span-12">
              <select
                className="custom-select"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="-1">Tất cả</option>
                <option value="2">Đang tiến hành</option>
                <option value="1">Đã hoàn thành</option>
              </select>
            </div>

            {/* Row 2 */}
            <div className="md:col-span-2 col-span-12">
              <h3 className="mt-1">Dành cho</h3>
            </div>
            <div className="md:col-span-4 col-span-12">
              <select
                className="custom-select"
                value={gender}
                onChange={(e) => setGender(e.target.value)}
              >
                <option value="-1">Tất cả</option>
                <option value="1">Con gái</option>
                <option value="2">Con trai</option>
              </select>
            </div>

            <div className="md:col-span-2 col-span-12">
              <h3 className="mt-1">Sắp xếp theo</h3>
            </div>
            <div className="md:col-span-4 col-span-12">
              <select
                className="custom-select"
                value={sort}
                onChange={(e) => setSort(e.target.value)}
              >
                <option value="2">Truyện mới nhất</option>
                <option value="10">Xem nhiều nhất</option>
                <option value="20">Theo dõi nhiều nhất</option>
                <option value="21">Bình luận nhiều nhất</option>
                <option value="22">Chapter mới nhất</option>
              </select>
            </div>
          </div>

          <span className="inline-block !ml-[16.667%] max-[992px]:!ml-0 !mt-2">
            <PrimaryButton
              title="Tìm kiếm"
              color="green"
              onClick={handleAppend}
            />
          </span>
        </div>
      )}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
        {isLoading || !data
          ? Array.from({ length: 4 }).map((_, idx) => (
              <StoryCardSkeleton key={idx} />
            ))
          : data.map((item) => <StoryCard key={item.id} data={item} />)}
      </div>
    </div>
  );
}

export default AdvancedFilters;

/**
 *  <option value="200">&gt;= 200 chapter</option>
                <option value="300">&gt;= 300 chapter</option>
                <option value="400">&gt;= 400 chapter</option>
                <option value="500">&gt;= 500 chapter</option>

        <option value="10">Xem nhiều nhất tuần</option>
                <option value="10">Xem nhiều nhất tháng</option>
 */
