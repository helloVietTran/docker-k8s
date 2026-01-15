import { useEffect, useState } from 'react';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';

import DefaultLayout from '@/components/Layout/DefaultLayout/DefaultLayout';
import Container from '@/components/Layout/Container/Container';
import PrimaryHeading from '@/components/Heading/PrimaryHeading/PrimaryHeading';
import Slider from '@/components/Slider/Slider';
import StoryCard from '@/components/StoryCard/StoryCard';
import BreadCumb from '@/components/BreadCumb/BreadCumb';
import Pagination from '@/components/Pagination/Pagination';
import getReadingHistoriesFromLocal from '@/utils/getReadingHistoryFromLocal';
import StoryCardSkeleton from '@/components/StoryCardSkeleton/StoryCardSkeleton';

function Main({ children, title, isBreadcrumbHidden, data }) {
  const [localReadingHistories, setLocalReadingHistories] = useState([]);

  useEffect(() => {
    setLocalReadingHistories(getReadingHistoriesFromLocal());
  }, [setLocalReadingHistories]);

  return (
    <DefaultLayout>
      <Container isBackgroundVisible shouldApplyPadding>
        {!isBreadcrumbHidden ? <BreadCumb /> : null}
        <Slider />
        <div>
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-4">
            {/* Left section: 8/12 columns on large screens */}
            <div className="lg:col-span-8">
              <PrimaryHeading
                title={title}
                icon={faAngleRight}
                className="!mt-4"
              />

              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2">
                {Array.isArray(data?.data) && data.data.length > 0
                  ? data.data.map((item) => (
                      <StoryCard
                        key={item.id}
                        data={item}
                        readingHistoryData={localReadingHistories}
                      />
                    ))
                  : [...Array(4)].map((_, index) => (
                      <StoryCardSkeleton key={index} />
                    ))}
              </div>

              <Pagination data={data} />
            </div>

            {/* Right section: 4/12 columns on large screens */}
            <div className="lg:col-span-4 px-2 mt-4">{children}</div>
          </div>
        </div>
      </Container>
    </DefaultLayout>
  );
}

Main.propTypes = {
  children: PropTypes.node.isRequired,
  title: PropTypes.string,
  isBreadcrumbHidden: PropTypes.bool,
};
export default Main;
