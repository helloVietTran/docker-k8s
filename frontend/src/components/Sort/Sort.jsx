import { useState, useEffect } from 'react';
import { useParams, useLocation, useSearchParams } from 'react-router-dom';
import queryString from 'query-string';
import { faEye, faHeart, faComment } from '@fortawesome/free-solid-svg-icons';
import { faList } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

import FilterButton from '@/components/Button/FilterButton/FilterButton';
import GenreSelect from '@/components/GenreSelect/GenreSelect';
import StoryCard from '@/components/StoryCard/StoryCard';
import { options } from '@/config/filter';
import StoryCardSkeleton from '../StoryCardSkeleton/StoryCardSkeleton';

function Sort({ data, isLoading }) {
  const { darkTheme } = useSelector((state) => state.theme);
  const [activeNavLink, setActiveNavLink] = useState('');

  const [searchParams] = useSearchParams({ status: '-1', sort: '1' });
  const { genre } = useParams();
  const location = useLocation();

  const genreFeild = options.find((option) => option.path.includes(genre));

  useEffect(() => {
    const checkLinkActive = () => {
      if (isActive('status', '-1')) setActiveNavLink('item 1');
      else if (isActive('status', '1')) setActiveNavLink('item 2');
      else if (isActive('status', '2')) setActiveNavLink('item 3');
      else if (isActive('sort', '1')) setActiveNavLink('item 4');
      else if (isActive('sort', '2')) setActiveNavLink('item 5');
      else if (isActive('sort', '10')) setActiveNavLink('item 6');
      else if (isActive('sort', '11')) setActiveNavLink('item 7');
      else if (isActive('sort', '20')) setActiveNavLink('item 8');
      else if (isActive('sort', '21')) setActiveNavLink('item 9');
      else if (isActive('sort', '22')) setActiveNavLink('item 10');
    };
    checkLinkActive();
    return () => setActiveNavLink(null);
  }, [searchParams]);

  // hàm thêm query vào url
  const generateQueryString = (queries) => {
    const currentParams = queryString.parse(location.search);
    const combinedParams = { ...currentParams, ...queries };
    return queryString.stringify(combinedParams);
  };

  // hàm check xem có thêm query mặc định hay không
  const shouldUseDefaultQuery = () => {
    const currentParams = queryString.parse(location.search);
    return Object.keys(currentParams).length === 0;
  };

  // xử lí sự so khớp path NavLink bằng query và url
  const isActive = (paramName, paramValue) => {
    return searchParams.get(paramName) === paramValue;
  };
  return (
    <div className={darkTheme ? 'dark' : ''}>
      <h1 className="text-2xl font-medium text-center dark:text-gray-200">
        {genreFeild === undefined ? 'Tìm truyện tranh' : 'Truyện thể loại '}
        <b>{genreFeild === undefined ? null : genreFeild.name}</b>
      </h1>

      <div className="genre-select-wrapper mb-2">
        <GenreSelect options={options} />
      </div>

      <p className="text-sm !p-2 border border-gray-300 rounded-xs dark:!text-gray-200">
        {genreFeild === undefined
          ? 'Tất cả thể loại truyện tranh'
          : genreFeild.description}
      </p>

      <ul className="text-center my-2">
        <FilterButton
          label="Tất cả"
          pathName={location.pathname}
          search={generateQueryString({ status: -1 })}
          activeNavLink={activeNavLink}
          name="item 1"
        />
        <FilterButton
          label="Hoàn thành"
          pathName={location.pathname}
          search={generateQueryString({ status: 1 })}
          activeNavLink={activeNavLink}
          name="item 2"
        />
        <FilterButton
          label="Đang tiến hành"
          pathName={location.pathname}
          search={generateQueryString({ status: 2 })}
          activeNavLink={activeNavLink}
          name="item 3"
        />
      </ul>
      <div>
        <div className="flex flex-col gap-4">
          {/* Row: Label + Filter buttons */}
          <div className="flex flex-col lg:flex-row items-start lg:items-center gap-2">
            <div className="w-full lg:w-1/4">
              <span className="font-medium dark:text-gray-200">
                Sắp xếp theo:
              </span>
            </div>
            <div className="w-full lg:w-3/4">
              <ul className="flex flex-wrap gap-1">
                <FilterButton
                  pathName={location.pathname}
                  label="Ngày cập nhật"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 1, status: -1 })
                      : generateQueryString({ sort: 1 })
                  }
                  orangeActive={isActive('sort', '1')}
                />
                <FilterButton
                  pathName={location.pathname}
                  label="Truyện mới"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 2, status: -1 })
                      : generateQueryString({ sort: 2 })
                  }
                  orangeActive={isActive('sort', '2')}
                />
                <FilterButton
                  pathName={location.pathname}
                  label="Top tháng"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 10, status: -1 })
                      : generateQueryString({ sort: 10 })
                  }
                  orangeActive={isActive('sort', '10')}
                  icon={faEye}
                />
                <FilterButton
                  pathName={location.pathname}
                  label="Top tuần"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 11, status: -1 })
                      : generateQueryString({ sort: 11 })
                  }
                  orangeActive={isActive('sort', '11')}
                  icon={faEye}
                />
                <FilterButton
                  pathName={location.pathname}
                  label="Top ngày"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 12, status: -1 })
                      : generateQueryString({ sort: 12 })
                  }
                  orangeActive={isActive('sort', '12')}
                  icon={faEye}
                />
                <FilterButton
                  pathName={location.pathname}
                  label="Theo dõi"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 20, status: -1 })
                      : generateQueryString({ sort: 20 })
                  }
                  orangeActive={isActive('sort', '20')}
                  icon={faHeart}
                />
                <FilterButton
                  pathName={location.pathname}
                  label="Bình luận"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 21, status: -1 })
                      : generateQueryString({ sort: 21 })
                  }
                  orangeActive={isActive('sort', '21')}
                  icon={faComment}
                />
                <FilterButton
                  pathName={location.pathname}
                  label="Số chapters"
                  search={
                    shouldUseDefaultQuery()
                      ? generateQueryString({ sort: 22, status: -1 })
                      : generateQueryString({ sort: 22 })
                  }
                  orangeActive={isActive('sort', '22')}
                  icon={faList}
                />
              </ul>
            </div>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            {isLoading || !data
              ? Array.from({ length: 4 }).map((_, idx) => (
                  <StoryCardSkeleton key={idx} />
                ))
              : data.map((item) => <StoryCard key={item.id} data={item} />)}
          </div>
        </div>
      </div>
    </div>
  );
}
Sort.propTypes = {
  data: PropTypes.array,
  isLoading: PropTypes.bool,
};

export default Sort;
