package com.youtube.analytics;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class YoutubeAnalytics {

    // 1. MAPPER CLASS: Cleans text noise and extracts exact columns
    public static class YoutubeMapper extends Mapper<LongWritable, Text, Text, Text> {
        
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            
            // Skip the CSV header row
            if (key.get() == 0 && line.contains("video_id")) {
                return;
            }
            
            // Regex to split by comma while ignoring commas inside quotation marks
            String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            // Ensure the row has all 9 columns
            if (tokens.length >= 9) {
                String videoId = tokens[0].trim();
                // Clean text noise (remove special non-alphanumeric characters from titles)
                String title = tokens[1].replaceAll("[^a-zA-Z0-9 ]", "").trim(); 
                String channel = tokens[2].trim();
                String country = tokens[3].trim();
                String views = tokens[4].trim();
                String likes = tokens[5].trim();
                String comments = tokens[6].trim();
                String publishedAt = tokens[7].trim();
                String fetchDate = tokens[8].trim();
                
                // Data validation: Skip if critical identifiers or metrics are missing
                if (videoId.isEmpty() || views.isEmpty() || likes.isEmpty()) {
                    return;
                }
                
                // Remove outer quotes if present from title/channel names
                if (title.startsWith("\"") && title.endsWith("\"")) title = title.substring(1, title.length() - 1);
                if (channel.startsWith("\"") && channel.endsWith("\"")) channel = channel.substring(1, channel.length() - 1);
                
                // Construct a clean tab-separated string value
                String cleanedValue = title + "\t" + channel + "\t" + country + "\t" + views + "\t" + likes + "\t" + comments + "\t" + publishedAt + "\t" + fetchDate;
                
                // Output key: videoId, value: cleaned records
                context.write(new Text(videoId), new Text(cleanedValue));
            }
        }
    }

    // 2. REDUCER CLASS: Passes through the cleaned dataset rows safely
    public static class YoutubeReducer extends Reducer<Text, Text, Text, Text> {
        
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text val : values) {
                context.write(key, val);
            }
        }
    }

    // 3. DRIVER METHOD: Sets up and runs the MapReduce execution configuration
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: YoutubeAnalytics <input HDFS path> <output HDFS path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "YouTube Data Cleansing Engine");
        
        job.setJarByClass(YoutubeAnalytics.class);
        job.setMapperClass(YoutubeMapper.class);
        job.setReducerClass(YoutubeReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
