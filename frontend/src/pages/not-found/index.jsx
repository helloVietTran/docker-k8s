import React from 'react';
import { Link } from 'react-router-dom';

function NotFound() {
  return (
    <div className="text-center flex flex-col items-center justify-center min-h-screen px-4">
      <img
        src="/images/not-found/404.svg"
        alt="not-found"
        className="max-w-[60%] mx-auto"
      />
      <h3 className="font-semibold text-[2.4rem] leading-tight text-gray-800 mt-6">
        Oops! page not found.
      </h3>
      <Link
        to="/"
        className="inline-block !my-4 !px-6 !py-3 border-2 border-gray-600 rounded-full text-gray-600 font-semibold"
      >
        Go home
      </Link>
    </div>
  );
}

export default NotFound;
