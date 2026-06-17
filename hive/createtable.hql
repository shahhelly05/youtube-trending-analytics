CREATE EXTERNAL TABLE IF NOT EXISTS youtube_trending_data (
    video_id STRING,
    title STRING,
    channel_title STRING,
    region STRING,
    view_count BIGINT,
    likes BIGINT,
    dislikes BIGINT,
    published_at STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/youtube_project/cleaned_output';
