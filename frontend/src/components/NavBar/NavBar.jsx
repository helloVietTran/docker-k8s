import { useSelector } from 'react-redux';
import { useState, useEffect } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCaretDown, faHome, faSort } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';

import Container from '@/components/Layout/Container/Container';
import SortMenu from '@/components/Menu/SortMenu/SortMenu';
import GenreMenu from '@/components/Menu/GenreMenu/GenreMenu';

function NavBar({ isPreventFixed }) {
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  const [isFixed, setIsFixed] = useState(false);
  const [isOpenGenreMenu, setIsOpenGenreMenu] = useState(false);
  const [isOpenSortMenu, setIsOpenSortMenu] = useState(false);

  useEffect(() => {
    if (isPreventFixed === undefined) {
      const handleScroll = () => {
        const currentScrollPos = window.scrollY;
        const isFixed = currentScrollPos > 50;
        setIsFixed(isFixed);
      };
      window.addEventListener('scroll', handleScroll);

      return () => {
        window.removeEventListener('scroll', handleScroll);
      };
    }
  }, [isPreventFixed]);

  const checkLinkActive = ({ isActive }) => {
    let classes = 'nav-link';
    if (isActive) classes += ' active';
    if (darkTheme) classes += ' dark';
    if (isActive && darkTheme) classes += ' active dark';
    return classes;
  };

  return (
    <nav
      className={`${darkTheme && 'dark'} bg-navbar-background dark:bg-black/90
                  ${isFixed ? 'fixed top-0 left-0 right-0 z-10' : ''} navbar`}
    >
      <Container>
        <ul className={`nav-list${darkTheme ? ' dark' : ''}`}>
          <li>
            <NavLink to="/" className={checkLinkActive}>
              <FontAwesomeIcon icon={faHome} />
            </NavLink>
          </li>
          <li>
            <NavLink to="/hot" className={checkLinkActive}>
              Hot
            </NavLink>
          </li>
          <li>
            <NavLink to="/following" className={checkLinkActive}>
              Theo dõi
            </NavLink>
          </li>
          <li>
            <NavLink to="/history" className={checkLinkActive}>
              Lịch sử
            </NavLink>
          </li>
          <li
            className="relative"
            onMouseEnter={() => setIsOpenGenreMenu(true)}
            onClick={() => setIsOpenGenreMenu(!isOpenGenreMenu)}
            onMouseLeave={() => setIsOpenGenreMenu(false)}
          >
            <NavLink to="/find-story" className={checkLinkActive}>
              Thể loại
              <FontAwesomeIcon icon={faCaretDown} className="!pl-1" />
            </NavLink>
            {isOpenGenreMenu && <GenreMenu />}
          </li>
          <li
            className="relative"
            onMouseEnter={() => setIsOpenSortMenu(true)}
            onClick={() => setIsOpenSortMenu(!isOpenSortMenu)}
            onMouseLeave={() => setIsOpenSortMenu(false)}
          >
            <Link
              to="/find-story"
              className={`nav-link${darkTheme ? ' dark' : ''}`}
            >
              Xếp hạng
              <FontAwesomeIcon icon={faSort} className="!pl-1" />
            </Link>
            {isOpenSortMenu && <SortMenu />}
          </li>
          <li>
            <NavLink to="/find-advanced" className={checkLinkActive}>
              Tìm truyện
            </NavLink>
          </li>

          <li>
            <NavLink to="/girl-story" className={checkLinkActive}>
              Con gái
            </NavLink>
          </li>

          <li>
            <NavLink to="/boy-story" className={checkLinkActive}>
              Con trai
            </NavLink>
          </li>

          <li className="max-lg:hidden">
            <Link
              className={`nav-link${darkTheme ? ' dark' : ''}`}
              to="https://www.facebook.com/anh.tranviet.3386"
              target="_blank"
            >
              Group
            </Link>
          </li>

          <li className="max-lg:hidden">
            <Link
              className={`nav-link${darkTheme ? ' dark' : ''}`}
              to="https://www.facebook.com/profile.php?id=61556648275676"
              target="_blank"
            >
              Fanpage
            </Link>
          </li>
        </ul>
      </Container>
    </nav>
  );
}

NavBar.propTypes = {
  isPreventFixed: PropTypes.bool,
};

export default NavBar;
