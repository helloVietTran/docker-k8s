import React from 'react';
import { Link } from 'react-router-dom';

function SearchResult({ searchData }) {
  return (
    <div className="absolute top-10 w-full z-50 overflow-y-auto overflow-x-hidden border border-gray-200 bg-white rounded-sm shadow transition-all">
      <ul>
        {searchData.map((searchValue) => (
          <li key={searchValue.id} className="border-b border-gray-200 !p-2">
            <Link
              to={`/story/${searchValue.slug}/${searchValue.id}`}
              className="flex gap-2"
            >
              <img
                src={searchValue.imgSrc}
                alt="search"
                className="w-[50px] h-[50px] object-cover mr-2"
              />
              <div className="flex-1 text-gray-800">
                <h3 className="text-sm font-semibold text-gray-900 truncate">
                  {searchValue.name}
                </h3>
                <span className="block text-gray-500 text-xs">
                  Chapter {searchValue.newestChapter}
                </span>

                {searchValue.author && (
                  <b className="text-sm block text-blue-600 font-semibold capitalize">
                    {searchValue.author}
                  </b>
                )}

                <p className="italic truncate text-xs text-light-blue-heading">
                  {Array.isArray(searchValue.genres)
                    ? searchValue.genres.join(' - ')
                    : ''}
                </p>
              </div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default SearchResult;
