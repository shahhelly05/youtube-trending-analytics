SELECT 
    region, 
    COUNT(DISTINCT video_id) AS total_trending_videos,
    SUM(view_count) AS total_views, 
    SUM(likes) AS total_likes 
FROM youtube_trending_data 
GROUP BY region 
ORDER BY total_views DESC;
