import React from 'react';
import { Link } from 'react-router-dom';

const Logo = () => {
  return (
    <div className={'brand'}>
      <Link className="block" to="/">
        <img
          src="/images/logo/logo.png"
          alt="logo"
          className="!mt-3 w-[88px]"
        />
      </Link>
    </div>
  );
};

export default Logo;
