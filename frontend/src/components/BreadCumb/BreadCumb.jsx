import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAnglesRight } from '@fortawesome/free-solid-svg-icons';
import { Link, useLocation, useParams } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

import pathMappings from '@/config/pathMappings';

function BreadCumb({ comicName, disabledTheme = false }) {
  const location = useLocation();
  const { darkTheme } = useSelector((state) => state.theme);

  const { storyName, storyID, chap, userId, chapterID } = useParams();

  const pathname = location.pathname.split('/').filter((x) => x);

  const breadCumbItems = pathname.map((path, index) => {
    let routeTo = `/${pathname.slice(0, index + 1).join('/')}`;
    const isLast = index === pathname.length;

    let displayName = path;
    if (pathMappings[path]) {
      displayName = pathMappings[path].displayName;
      routeTo = pathMappings[path].routeTo || routeTo;
    } else if (path === storyID) {
      displayName = null;
    } else if (path === userId) {
      displayName = null;
    }
    //comicName is props
    else if (path === storyName) {
      displayName = comicName;
      routeTo = `${routeTo}/${storyID}`;
    } else if (path === chap) {
      displayName = chap.replace(/chap-(\d+)/i, 'Chapter $1');
    } else if (path === chapterID) {
      displayName = null;
    }

    return (
      <span key={path}>
        {!isLast ? (
          <>
            {displayName && (
              <>
                <FontAwesomeIcon
                  icon={faAnglesRight}
                  className="px-[6px] text-[#ccc] text-[10px]"
                />
                <Link
                  to={
                    displayName && displayName.includes('Chapter')
                      ? ''
                      : routeTo
                  }
                  className="hover:underline link-colored !text-sm"
                >
                  {displayName}
                </Link>
              </>
            )}
          </>
        ) : (
          <span>{displayName}</span>
        )}
      </span>
    );
  });

  return (
    <div
      className={`${darkTheme && !disabledTheme ? 'dark' : ''} capitalize text-sm pb-2`}
    >
      <Link className="hover:underline link-colored !text-sm" to="/">
        Trang chủ
      </Link>
      {breadCumbItems}
    </div>
  );
}

BreadCumb.propTypes = {
  comicName: PropTypes.string,
  disabledTheme: PropTypes.bool,
};
export default BreadCumb;
