import React from 'react';

function StoryCardSkeleton() {
  return (
    <div className="mt-3">
      <div className="relative">
        <div className="w-full h-[200px] bg-gray-300 animate-pulse rounded border border-gray-300 shadow-sm"></div>

        <div className="absolute inset-x-0 bottom-0 flex justify-around h-[25px] px-1 bg-black opacity-65 leading-[25px]">
          {[...Array(3)].map((_, i) => (
            <div
              key={i}
              className="flex gap-1 items-center px-1 text-white text-sm"
            >
              <div className="w-4 h-4 bg-gray-400 rounded-full animate-pulse"></div>
              <div className="w-6 h-3 bg-gray-400 rounded animate-pulse"></div>
            </div>
          ))}
        </div>
      </div>

      <div className="mt-2 w-3/4 h-4 bg-gray-300 rounded animate-pulse"></div>

      <div className="text-xs flex items-center justify-end mt-1">
        <span className="w-28 h-4 bg-gray-300 rounded animate-pulse"></span>
      </div>

      <ul className="mt-2 space-y-1">
        {[...Array(3)].map((_, idx) => (
          <li key={idx} className="flex justify-between items-center">
            <div className="w-1/2 h-3 bg-gray-300 rounded animate-pulse"></div>
            <div className="w-1/4 h-3 bg-gray-300 rounded animate-pulse"></div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default StoryCardSkeleton;
