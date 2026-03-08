-- ============================================================
-- k6 성능 테스트용 시드 데이터 (MySQL 8.0 이상)
-- 실행: docker exec -i blaybus-mysql mysql -uroot -ppassword blaybus < k6/seed-data.sql
-- ============================================================

-- ============================================================
-- 1. daily_planners: 각 멘티(2, 3)에 대해 2025-01-01 ~ 2025-12-31 (365일)
-- ============================================================
INSERT INTO daily_planners (plan_date, total_study_time, daily_memo, mentor_feedback, mentee_id, created_at, updated_at)
WITH RECURSIVE dates AS (
  SELECT CAST('2025-01-01' AS DATE) AS d
  UNION ALL
  SELECT DATE_ADD(d, INTERVAL 1 DAY) FROM dates WHERE d < '2025-12-31'
)
SELECT
  d                                 AS plan_date,
  FLOOR(RAND() * 14400)             AS total_study_time,
  CONCAT('공부 메모 ', d)            AS daily_memo,
  CASE WHEN DAYOFWEEK(d) IN (1, 7) THEN NULL
       ELSE CONCAT('멘토 피드백 ', d) END AS mentor_feedback,
  2                                 AS mentee_id,
  NOW()                             AS created_at,
  NOW()                             AS updated_at
FROM dates
UNION ALL
SELECT
  d,
  FLOOR(RAND() * 14400),
  CONCAT('공부 메모 ', d),
  CASE WHEN DAYOFWEEK(d) IN (1, 7) THEN NULL
       ELSE CONCAT('멘토 피드백 ', d) END,
  3,
  NOW(),
  NOW()
FROM dates;

-- ============================================================
-- 2. tasks: 각 daily_planner당 7개 과제 생성
--    subject: KOREAN(3개), MATH(2개), ENGLISH(2개)
--    status: DONE / TODO 랜덤
-- ============================================================
INSERT INTO tasks (subject, title, status, actual_study_time, task_date,
                   is_mentor_checked, is_mandatory, timer_status,
                   daily_planner_id, mentee_id, created_at, updated_at)
SELECT
  ELT(n.num,
    'KOREAN', 'KOREAN', 'KOREAN',
    'MATH', 'MATH',
    'ENGLISH', 'ENGLISH')                       AS subject,
  CONCAT(ELT(n.num, 'KOREAN','KOREAN','KOREAN','MATH','MATH','ENGLISH','ENGLISH'),
         ' 과제 #', n.num, ' - ', dp.plan_date) AS title,
  CASE WHEN (CRC32(CONCAT(dp.id, n.num)) % 3) = 0
       THEN 'TODO' ELSE 'DONE' END              AS status,
  CASE WHEN (CRC32(CONCAT(dp.id, n.num)) % 3) = 0
       THEN 0 ELSE FLOOR(300 + RAND() * 3600) END AS actual_study_time,
  dp.plan_date                                  AS task_date,
  CASE WHEN (CRC32(CONCAT(dp.id, n.num)) % 3) = 0
       THEN FALSE
       WHEN (CRC32(CONCAT(dp.id, n.num)) % 3) = 1 THEN TRUE
       ELSE FALSE END                           AS is_mentor_checked,
  CASE WHEN n.num <= 5 THEN TRUE ELSE FALSE END AS is_mandatory,
  'STOPPED'                                     AS timer_status,
  dp.id                                         AS daily_planner_id,
  dp.mentee_id                                  AS mentee_id,
  NOW()                                         AS created_at,
  NOW()                                         AS updated_at
FROM daily_planners dp
CROSS JOIN (
  SELECT 1 AS num UNION SELECT 2 UNION SELECT 3
  UNION SELECT 4 UNION SELECT 5
  UNION SELECT 6 UNION SELECT 7
) n;

-- ============================================================
-- 3. notifications: 각 유저(1,2,3)에 500건
-- ============================================================
INSERT INTO notifications (type, message, is_read, user_id, target_id, created_at)
WITH RECURSIVE nums AS (
  SELECT 1 AS n
  UNION ALL
  SELECT n + 1 FROM nums WHERE n < 500
)
SELECT
  ELT(1 + (n % 3), 'FEEDBACK', 'COMMENT', 'PLAN_FEEDBACK') AS type,
  CONCAT('알림 메시지 #', n)                                 AS message,
  CASE WHEN n % 4 = 0 THEN FALSE ELSE TRUE END               AS is_read,
  u.user_id                                                  AS user_id,
  n                                                          AS target_id,
  DATE_SUB(NOW(), INTERVAL n HOUR)                           AS created_at
FROM nums
CROSS JOIN (SELECT user_id FROM users) u;

-- ============================================================
-- 확인 쿼리
-- ============================================================
SELECT 'daily_planners' AS tbl, COUNT(*) AS cnt FROM daily_planners
UNION ALL SELECT 'tasks', COUNT(*) FROM tasks
UNION ALL SELECT 'notifications', COUNT(*) FROM notifications;
