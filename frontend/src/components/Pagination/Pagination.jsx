import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import { useSelector } from 'react-redux';

function Pagination({ data }) {
  const navigate = useNavigate();
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  if (!data) return null;

  const { totalElements, totalPages, currentPage, pageSize } = data;

  if (totalElements <= pageSize) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

  const goToNextPage = () => {
    if (currentPage < totalPages) {
      navigate(`?page=${currentPage + 1}`);
    }
  };

  const goToPrevPage = () => {
    if (currentPage > 1) {
      navigate(`?page=${currentPage - 1}`);
    }
  };

  const textColor = darkTheme ? 'text-orange-500' : 'text-blue-600';
  const hoverBg = 'hover:bg-gray-100';
  const activeBg = darkTheme ? 'bg-orange-500' : 'bg-blue-600';

  return (
    <div className={`text-center ${darkTheme ? 'dark text-gray-200' : ''}`}>
      <ul className="inline-flex flex-wrap gap-1 my-6 justify-center">
        {/* Prev Button */}
        <li>
          <button
            onClick={goToPrevPage}
            disabled={currentPage <= 1}
            className={`w-9 h-9 rounded-md border border-gray-300 text-base transition-colors 
              ${
                currentPage <= 1
                  ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                  : `bg-white ${textColor} ${hoverBg}`
              }`}
          >
            ‹
          </button>
        </li>

        {/* Page Numbers */}
        {pages.map((number) => (
          <li key={uuidv4()}>
            <NavLink
              to={`?page=${number}`}
              className={`w-9 h-9 flex items-center justify-center text-base rounded-md border border-gray-300 transition-colors
                ${
                  currentPage === number
                    ? `${activeBg} text-white pointer-events-none`
                    : `bg-white ${textColor} ${hoverBg}`
                }`}
            >
              {number}
            </NavLink>
          </li>
        ))}

        {/* Next Button */}
        <li>
          <button
            onClick={goToNextPage}
            disabled={currentPage >= totalPages}
            className={`w-9 h-9 rounded-md border border-gray-300 text-base transition-colors 
              ${
                currentPage >= totalPages
                  ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                  : `bg-white ${textColor} ${hoverBg}`
              }`}
          >
            ›
          </button>
        </li>
      </ul>
    </div>
  );
}

export default Pagination;
