import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faBell,
  faBook,
  faInfoCircle,
  faList,
  faLock,
  faSignOut,
} from '@fortawesome/free-solid-svg-icons';
import { faCommentDots, faGaugeHigh } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';

function InfomationSideBar({ isHided }) {
  const location = useLocation();

  const getActivedClass = (path) => {
    path = '/secure' + path;
    return location.pathname === path ? 'active' : '';
  };

  if (isHided) {
    return;
  }
  return (
    <ul className="info-sidebar text-sm mt-5">
      <li className={getActivedClass('/dashboard')}>
        <NavLink to="dashboard " activeclassname={'active'}>
          <FontAwesomeIcon icon={faGaugeHigh} />
          <span className={'text'}>Thông tin chung</span>
        </NavLink>
      </li>
      <li className={getActivedClass('/user-profile')}>
        <NavLink to="user-profile" activeclassname={'active'}>
          <FontAwesomeIcon icon={faInfoCircle} />
          Thông tin tài khoản
        </NavLink>
      </li>
      <li className={getActivedClass('/followed-comic')}>
        <NavLink to="followed-comic" activeclassname={'active'}>
          <FontAwesomeIcon icon={faBook} />
          Truyện theo dõi
        </NavLink>
      </li>
      <li className={getActivedClass('/user-point')}>
        <NavLink to="user-point" activeclassname={'active'}>
          <FontAwesomeIcon icon={faList} />
          Linh thạch
        </NavLink>
      </li>
      <li className={getActivedClass('/shop')}>
        <NavLink to="shop" activeclassname={'active'}>
          <FontAwesomeIcon icon={faBell} />
          Cửa hàng
        </NavLink>
      </li>

      <li className={getActivedClass('/my-comment')}>
        <NavLink to="my-comment" activeclassname={'active'}>
          <FontAwesomeIcon icon={faCommentDots} />
          Bình luận
        </NavLink>
      </li>

      <li className={getActivedClass('/changing-password')}>
        <NavLink to="changing-password" activeclassname={'active'}>
          <FontAwesomeIcon icon={faLock} />
          Đổi mật khẩu
        </NavLink>
      </li>
      <li>
        <NavLink to="/">
          <FontAwesomeIcon icon={faSignOut} />
          Thoát
        </NavLink>
      </li>
    </ul>
  );
}

InfomationSideBar.propTypes = {
  isHided: PropTypes.bool.isRequired,
};

export default InfomationSideBar;
