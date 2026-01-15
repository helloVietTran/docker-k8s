import React, { useState, useEffect, useMemo } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import { useQuery } from '@tanstack/react-query';
import { debounce } from 'lodash';

import SearchResult from '../SearchResult/SearchResult';
import { searchStory } from '@/api/storyApi';
import { queryKey } from '@/config/queryKey';

const SearchBox = () => {
  const [searchParam, setSearchParam] = useState('');
  const [debouncedParam, setDebouncedParam] = useState('');
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  const debouncedSearch = useMemo(
    () =>
      debounce((value) => {
        setDebouncedParam(value);
      }, 300),
    []
  );

  useEffect(() => {
    return () => {
      debouncedSearch.cancel();
    };
  }, [debouncedSearch]);

  const handleInputChange = (e) => {
    const value = e.target.value;
    setSearchParam(value);
    debouncedSearch(value);
  };

  const handleBlurInput = () => {
    setIsSearchOpen(false);
  };

  const handleFocusInput = () => {
    setIsSearchOpen(true);
  };

  const { data: searchData, isLoading } = useQuery({
    enabled: debouncedParam.trim().length > 3,
    queryKey: [
      queryKey.searchResultsWithKeyword(debouncedParam),
      debouncedParam,
    ],
    queryFn: () => searchStory(debouncedParam),
  });

  return (
    <div className="search-box relative w-[455px]  md:w-[320px] lg:w-[455px] bg-white rounded-sm shadow-lg">
      <input
        placeholder="Tìm truyện..."
        value={searchParam}
        onChange={handleInputChange}
        onBlur={handleBlurInput}
        onFocus={handleFocusInput}
        className="h-[32px] w-full py-1.5 px-3 text-sm text-gray-700 font-medium border-0 rounded-tl-[4px] rounded-bl-[4px] focus:outline-none"
      />
      <button
        type="button"
        className="absolute right-0 h-[32px] w-[32px] hover:bg-[#e6e6e6] border-none rounded-r-sm"
      >
        <FontAwesomeIcon icon={faSearch} />
      </button>

      {isLoading && (
        <img
          className="absolute w-[30px] top-1/2 transform -translate-y-1/2 right-[30px] z-50"
          src="/images/loading/loading.gif"
          alt="loading"
        />
      )}

      {searchData?.length > 0 && isSearchOpen ? (
        <SearchResult searchData={searchData} />
      ) : (
        <></>
      )}
    </div>
  );
};

export default SearchBox;
