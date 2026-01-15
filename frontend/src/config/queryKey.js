export const queryKey = {
  STORIES: 'stories',
  HOT_STORIES: 'hot-stories',
  MY_INFO: 'my-info',
  TOP_STORIES: 'top-stories',

  TOP_USERS: 'top-users',
  BOY_STORIES: 'boy-stories',
  GIRL_STORIES: 'girl-stories',

  FEATURED_STORIES: 'featured-stories',
  MY_FOLLOWED_STORIES: 'my-followed-tories',

  NEW_COMMENTS: 'new-comments',
  FIND_ADVANCED: 'find-advanced',

  MY_COMMENTS: 'my-comments',
  POINT: 'point',
  FIND_STORY: 'find-story',
  USER_INFO: 'user-info',

  commentsOfUser: (userId) => `comments-of-${userId}`,
  storyDetail: (storyId) => `story-${storyId}`,

  searchResultsWithKeyword: (keyword) => `search-results-with-${keyword}`,
  followedStoryByStoryId: (storyId) => `followed-story-${storyId}`,

  commentsOfStory: (storyId) => `comments-of-story-${storyId}`,

  commentsOfChapter: (chapterId) => `comments-of-chapter-${chapterId}`,
  resourcesOfChapter: (chapterId) => `comments-of-chapter-${chapterId}`,
};
