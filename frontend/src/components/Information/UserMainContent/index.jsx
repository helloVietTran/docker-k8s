import React from 'react';
import { Outlet } from 'react-router-dom';

function UserMainContent() {
  return (
    <div className="pb-3">
      <Outlet />
    </div>
  );
}

export default UserMainContent;
