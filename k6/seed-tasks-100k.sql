-- ============================================================
-- tasks 테이블 10만 건 시드 (daily_planners 730건 재활용)
-- 730 plans × 140 tasks = 102,200건
--
-- 실행:
--   docker exec -i blaybus-mysql mysql -uroot -ppassword blaybus < k6/seed-tasks-100k.sql
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE task_logs;
TRUNCATE TABLE task_submissions;
TRUNCATE TABLE tasks;
SET FOREIGN_KEY_CHECKS = 1;

-- 시퀀스 1~140 생성 후 daily_planners × 140 CROSS JOIN
INSERT INTO tasks (
  subject, title, status, actual_study_time, task_date,
  is_mentor_checked, is_mandatory, timer_status,
  daily_planner_id, mentee_id, created_at, updated_at
)
SELECT
  ELT(1 + (CRC32(CONCAT(dp.id, n.num)) % 3),
      'KOREAN', 'MATH', 'ENGLISH')                        AS subject,
  CONCAT(
    ELT(1 + (CRC32(CONCAT(dp.id, n.num)) % 3),
        'KOREAN','MATH','ENGLISH'),
    ' 과제 #', n.num, ' (', dp.plan_date, ')'
  )                                                        AS title,
  ELT(1 + (CRC32(CONCAT(dp.id, n.num, 's')) % 3),
      'DONE', 'DONE', 'TODO')                             AS status,
  CASE WHEN (CRC32(CONCAT(dp.id, n.num, 's')) % 3) < 2
       THEN FLOOR(300 + (CRC32(CONCAT(dp.id, n.num, 't')) % 3600))
       ELSE 0 END                                         AS actual_study_time,
  dp.plan_date                                            AS task_date,
  CASE WHEN (CRC32(CONCAT(dp.id, n.num, 's')) % 3) = 0
       THEN TRUE ELSE FALSE END                           AS is_mentor_checked,
  CASE WHEN n.num <= 100 THEN TRUE ELSE FALSE END         AS is_mandatory,
  'STOPPED'                                               AS timer_status,
  dp.id                                                   AS daily_planner_id,
  dp.mentee_id                                            AS mentee_id,
  NOW()                                                   AS created_at,
  NOW()                                                   AS updated_at
FROM daily_planners dp
CROSS JOIN (
  SELECT a.N + b.N * 10 + 1 AS num
  FROM
    (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
     UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
    (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
     UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9
     UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13) b
  WHERE a.N + b.N * 10 + 1 <= 140
) n;

-- 결과 확인
SELECT
  'tasks 총 건수'      AS label, COUNT(*)          AS value FROM tasks
UNION ALL SELECT
  'mentee_id=2 건수',           COUNT(*)          FROM tasks WHERE mentee_id = 2
UNION ALL SELECT
  'mentee_id=3 건수',           COUNT(*)          FROM tasks WHERE mentee_id = 3
UNION ALL SELECT
  '고유 task_date 수',          COUNT(DISTINCT task_date) FROM tasks;
