import Slider from 'react-slick';
import { useQuery } from '@tanstack/react-query';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAngleRight, faAngleLeft } from '@fortawesome/free-solid-svg-icons';

import SliderCard from './SliderCard/SliderCard';
import PrimaryHeading from '@/components/Heading/PrimaryHeading/PrimaryHeading';
import StoryCardSkeleton from '@/components/StoryCardSkeleton/StoryCardSkeleton';
import { getFeaturedStories } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';

function NextArrow({ onClick }) {
  return (
    <div
      onClick={onClick}
      className="absolute right-3 top-1/2 transform -translate-y-1/2 w-[55px] h-[55px] bg-[#d0d0ce] text-[#868484] opacity-50 flex items-center justify-center rounded shadow-md hover:opacity-65 cursor-pointer z-10"
    >
      <FontAwesomeIcon icon={faAngleRight} className="text-2xl" />
    </div>
  );
}

function PrevArrow({ onClick }) {
  return (
    <div
      onClick={onClick}
      className="absolute left-3 top-1/2 transform -translate-y-1/2 w-[55px] h-[55px] bg-[#d0d0ce] text-[#868484] opacity-50 flex items-center justify-center rounded shadow-md hover:opacity-65 cursor-pointer z-10"
    >
      <FontAwesomeIcon icon={faAngleLeft} className="text-2xl" />
    </div>
  );
}

function SliderHome() {
  const { data, isLoading } = useQuery({
    queryKey: [queryKey.FEATURED_STORIES],
    queryFn: getFeaturedStories,
    staleTime: 5 * 60 * 1000,
  });

  const settings = {
    dots: false,
    infinite: true,
    speed: 500,
    slidesToShow: 5,
    slidesToScroll: 1,
    swipeToSlide: true,
    autoplay: true,
    nextArrow: <NextArrow />,
    prevArrow: <PrevArrow />,
    responsive: [
      {
        breakpoint: 992,
        settings: {
          slidesToShow: 4,
        },
      },
      {
        breakpoint: 768,
        settings: {
          slidesToShow: 3,
        },
      },
      {
        breakpoint: 575,
        settings: {
          slidesToShow: 2,
        },
      },
    ],
  };

  return (
    <>
      <PrimaryHeading
        className="text-lg !pb-2"
        title="Truyện đề cử"
        icon={faAngleRight}
      />
      <div className="relative">
        <Slider {...settings}>
          {isLoading
            ? Array.from({ length: 5 }).map((_, index) => (
                <div key={index} className='px-2'>
                  <StoryCardSkeleton />
                </div>
              ))
            : Array.isArray(data?.result) &&
              data.result.map((item) => (
                <div key={item.id}>
                  <SliderCard data={item} />
                </div>
              ))}
        </Slider>
      </div>
    </>
  );
}

export default SliderHome;
