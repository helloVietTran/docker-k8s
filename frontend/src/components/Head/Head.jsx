import React, { useState, useCallback, useRef, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faSearch,
  faCaretDown,
  faBars,
  faTimes,
} from '@fortawesome/free-solid-svg-icons';
import { Link, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';

import ThemeBox from './ThemeBox/ThemeBox';
import Logo from './Logo/Logo';
import SearchBox from './SearchBox/SearchBox';
import Container from '@/components/Layout/Container/Container';
import UserMenu from '@/components/Menu/UserMenu/UserMenu';
import { logout } from '@/redux/authSlice';
import { getMyInfo } from '@/api/userApi';
import { queryKey } from '@/config/queryKey';

function Header() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { isAuthenticated } = useSelector((state) => state.auth);
  const [isOpenUserMenu, setIsOpenUserMenu] = useState(false);
  const [searchValue, setSearchValue] = useState('');

  const [isOpenMobileMenu, setIsOpenMobileMenu] = useState(false); // state open mobile navbar
  const userMenuRef = useRef(null);

  const { data: user } = useQuery({
    enabled: isAuthenticated,
    queryKey: [queryKey.MY_INFO],
    queryFn: getMyInfo,
  });

  const handleLogout = useCallback(() => {
    dispatch(logout());
    setIsOpenUserMenu(false);
  }, [dispatch]);

  const handleOpenMobileNavbar = () => setIsOpenMobileMenu(true);
  const handleCloseMobileNavbar = () => setIsOpenMobileMenu(false);
  const handleToggleMobileNavbar = () =>
    setIsOpenMobileMenu((prevState) => !prevState);

  const handleSearch = () => {
    navigate(`/find-story?keyword=${searchValue}`);
    handleCloseMobileNavbar();
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch();
  };

  // Đóng menu user khi click ra ngoài
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setIsOpenUserMenu(false);
      }
    };
    if (isOpenUserMenu) {
      document.addEventListener('mousedown', handleClickOutside);
    } else {
      document.removeEventListener('mousedown', handleClickOutside);
    }

    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isOpenUserMenu]);

  return (
    <header style={{ backgroundImage: 'url(/images/back-ground/bg_head.jpg)' }}>
      <Container>
        <div className="flex items-center justify-between h-[50px] py-2 header-container">
          <Logo />
          <SearchBox />
          <ThemeBox />

          {!isAuthenticated ? (
            <ul className="auth-navigation flex gap-1 text-base text-white">
              <li>
                <Link
                  to="/login"
                  className="!text-white text-sm hover:underline"
                >
                  Đăng nhập
                </Link>
              </li>
              <span>/</span>
              <li>
                <Link
                  to="/register"
                  className="!text-white text-sm hover:underline"
                >
                  Đăng ký
                </Link>
              </li>
            </ul>
          ) : (
            <div
              ref={userMenuRef}
              className="user-menu relative text-white text-sm flex gap-1 items-center cursor-pointer"
              onClick={() => setIsOpenUserMenu((prev) => !prev)}
            >
              <img
                src={user?.imgSrc || '/images/anonymous/anonymous.png'}
                alt="avatar"
                className="h-5 object-cover rounded-xs pr-1"
              />
              <span>Cá nhân</span>
              <FontAwesomeIcon icon={faCaretDown} className="pl-1" />
              {isOpenUserMenu && (
                <UserMenu
                  onClose={() => setIsOpenUserMenu(false)}
                  handleLogout={handleLogout}
                />
              )}
            </div>
          )}

          {/* MOBILE BUTTON */}
          <div
            className="search-mobile-btn absolute right-12 rounded px-[5px] pt-[1px] pb-0 bg-transparent border border-transparent 
            cursor-pointer"
            onClick={handleOpenMobileNavbar}
          >
            <FontAwesomeIcon icon={faSearch} className="text-white text-xl" />
          </div>

          <button
            onClick={handleToggleMobileNavbar}
            className={`max-md:flex hidden invisible max-md:visible  absolute right-2  items-center justify-center
              size-7 rounded cursor-pointer transition-colors duration-100 
              ${isOpenMobileMenu ? 'bg-[#a68f25]' : 'bg-[#d0b32e]'}`}
          >
            <FontAwesomeIcon
              icon={isOpenMobileMenu ? faTimes : faBars}
              className="text-white text-xl"
            />
          </button>
        </div>

        {/* MOBILE MENU */}
        {isOpenMobileMenu && (
          <div className="fixed inset-x-0 top-[50px] bottom-0 bg-[#141414] z-50 shadow-inner text-sm px-4 overflow-y-auto scrollbar-hide max-md:block hidden">
            <div className="flex items-center gap-2 pt-2">
              <input
                type="text"
                placeholder="Tìm truyện ..."
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
                onKeyDown={handleKeyDown}
                className="flex-1 h-10 px-4 rounded-md border border-gray-300  text-gray-200 placeholder-gray-500 
                focus:outline-none focus:ring-1 focus:ring-yellow-400 focus:border-yellow-400"
              />
              <button
                type="button"
                onClick={handleSearch}
                className="h-10 w-10 rounded-md bg-white border border-gray-300 hover:bg-gray-500 hover:text-gray-200 
                transition-colors duration-300 flex items-center justify-center"
              >
                <FontAwesomeIcon icon={faSearch} />
              </button>
            </div>

            <ul className="my-2 space-y-1">
              {[
                { label: 'Trang chủ', to: '/' },
                { label: 'Hot', to: '/hot' },
                { label: 'Lịch sử', to: '/history' },
                { label: 'Theo dõi', to: '/following' },
                { label: 'Thể loại', to: '/find-story' },
                { label: 'Tìm truyện', to: '/find-advanced' },
                { label: 'Con gái', to: '/girl-story' },
                { label: 'Con trai', to: '/boy-story' },
              ].map(({ label, to }) => (
                <li
                  key={to}
                  className="text-white hover:text-yellow-400 transition-colors duration-300"
                >
                  <Link
                    to={to}
                    onClick={handleCloseMobileNavbar}
                    className="uppercase leading-[35px] block"
                  >
                    {label}
                  </Link>
                </li>
              ))}

              <li className="text-white hover:text-yellow-400 transition-colors duration-300">
                <a
                  href="https://www.facebook.com/anh.tranviet.3386"
                  target="_blank"
                  rel="noreferrer"
                  className="uppercase leading-[35px] block"
                >
                  Group
                </a>
              </li>
              <li className="text-white hover:text-yellow-400 transition-colors duration-300">
                <a
                  href="https://www.facebook.com/profile.php?id=61556648275676"
                  target="_blank"
                  rel="noreferrer"
                  className="uppercase leading-[35px] block"
                >
                  FanPage
                </a>
              </li>

              {!isAuthenticated ? (
                <>
                  <li className="text-white hover:text-yellow-400 transition-colors duration-300">
                    <Link
                      to="/login"
                      onClick={handleCloseMobileNavbar}
                      className="uppercase leading-[35px] block"
                    >
                      Đăng nhập
                    </Link>
                  </li>
                  <li className="text-white hover:text-yellow-400 transition-colors duration-300">
                    <Link
                      to="/register"
                      onClick={handleCloseMobileNavbar}
                      className="uppercase leading-[35px] block"
                    >
                      Đăng kí
                    </Link>
                  </li>
                </>
              ) : (
                <li
                  className="flex items-center gap-1 text-white text-sm cursor-pointer leading-[35px] relative"
                  onMouseEnter={() => setIsOpenUserMenu(true)}
                  onMouseLeave={() => setIsOpenUserMenu(false)}
                >
                  <img
                    src={user?.imgSrc || '/images/anonymous/anonymous.png'}
                    alt="avatar"
                    className="h-5 pr-1"
                  />
                  <span>Cá nhân</span>
                  <FontAwesomeIcon icon={faCaretDown} />
                  {isOpenUserMenu && (
                    <UserMenu
                      handleLogout={handleLogout}
                      handleClickUserMenu={handleCloseMobileNavbar}
                      textColor="#333"
                    />
                  )}
                </li>
              )}
            </ul>
          </div>
        )}
      </Container>
    </header>
  );
}

export default Header;
