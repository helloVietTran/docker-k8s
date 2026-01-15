import { useState, useEffect } from 'react';

import ListFrame from '@/components/List/ListFrame/ListFrame';
import ListHeading from '@/components/Heading/ListHeading/ListHeading';
import ListItem from '@/components/List/ListItem/ListItem';

import getReadingHistoriesFromLocal from '@/utils/getReadingHistoryFromLocal';
import deleteReadingHistoryFromLocal from '@/utils/deleteReadingHistoryFromLocal';

function HistoryList() {
  const [localReadingHistories, setLocalReadingHistories] = useState([]);

  useEffect(() => {
    setLocalReadingHistories(getReadingHistoriesFromLocal());
  }, []);

  const handleDelete = (id) => {
    setLocalReadingHistories(deleteReadingHistoryFromLocal(id));
  };

  return (
    <ListFrame>
      <ListHeading title="Lịch sử đọc truyện" path="/history" />

      {localReadingHistories.length > 0 ? (
        localReadingHistories.map((story) => {
          return (
            <ListItem
              data={story}
              hasDeleteBtn
              handleDeleteReadingHistory={handleDelete}
              key={story.id}
            />
          );
        })
      ) : (
        <p>Bạn không có lịch sử đọc truyện trên thiết bị này</p>
      )}
    </ListFrame>
  );
}

export default HistoryList;
