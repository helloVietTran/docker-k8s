import { envConstant } from '@/config/envConstant';

// story structure:  id, slug, imgSrc, chaptersRead[], updatedAt, newestChapter, name, viewCount
function addReadingHistoryOnLocal(story, chapter) {
  let localReadingHistories =
    JSON.parse(localStorage.getItem(envConstant.readingHistoryOnLocal)) || [];

  let existingIndex = localReadingHistories.findIndex(
    (item) => item.id === story.id
  );
  if (existingIndex !== -1) {
    story.chaptersRead = localReadingHistories[existingIndex].chaptersRead;

    localReadingHistories[existingIndex] = story;

    if (!localReadingHistories[existingIndex].chaptersRead.includes(chapter)) {
      localReadingHistories[existingIndex].chaptersRead.push(chapter);
    }
  } else {
    story.chaptersRead = [];
    story.chaptersRead.push(chapter);

    localReadingHistories.push(story);
  }
  localStorage.setItem(
    envConstant.readingHistoryOnLocal,
    JSON.stringify(localReadingHistories)
  );
}

export default addReadingHistoryOnLocal;
