# Multi-Region YouTube Trending Data Analytics Pipeline

A comprehensive Big Data pipeline designed to ingest, clean, store, and analyze multi-region YouTube trending datasets. This project spans across a 4-phase architecture utilizing the Hadoop ecosystem to process large-scale unstructured data and extract viral video insights.

---

## 🏗️ Project Architecture
1. **Storage (HDFS):** Raw data ingestion and storage management.
2. **Processing (MapReduce):** Custom Java MapReduce programming to clean and parse datasets.
3. **Deployment (Hive Data Warehouse):** Overlaying an optimized External Table Schema onto HDFS.
4. **Analytics (HiveQL):** Running high-performance analytical queries.

---

## 🛠️ Tech Stack & Environment
- **Storage Layer:** Apache Hadoop HDFS
- **Compute Layer:** Apache Hadoop MapReduce (Java)
- **Data Warehouse:** Apache Hive 4.0.0 (Beeline CLI)
- **Runtime Environment:** Java LTS

---

## 🚀 Engineering Challenges & Solutions

### 1. Delimiter & Row Formatting Quirks
* **Challenge:** The MapReduce engine generated output separated by Tabs (`\t`) instead of commas, alongside trailing bytes at the end of rows, causing misaligned columns in standard table builds.
* **Solution:** Engineered a custom Hive DDL mapping using specific row formatting controls to perfectly align with the underlying HDFS storage blocks without corrupting data integrity.

### 2. Hive 4.0.0 Runtime Serialization Error
* **Challenge:** Executing analytical queries on Hive 4.0.0 using a modern Java runtime threw an `InaccessibleObjectException` due to strict JDK module encapsulation restrictions hitting the Kryo serialization library.
* **Solution:** Bypassed the issue by patching the environment globally, forcing open the necessary Java base system modules using targeted runtime flags:
  ```bash
  export HADOOP_CLIENT_OPTS="$HADOOP_CLIENT_OPTS --add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED"

---
## 📊 Sample Analytical Query
```SQL

SELECT title, channel_title, view_count, region 
FROM youtube_trending_data 
ORDER BY view_count DESC 
LIMIT 5;
