# k6 성능 테스트 가이드

## 파일 구조
```
k6/
├── lib/auth.js              # 공통 로그인 헬퍼
├── seed-data.sql            # 테스트용 시드 데이터 (3,650+ rows)
├── run-all.js               # 통합 테스트 (Before/After 공용)
├── compare-results.sh       # Before/After 결과 비교
├── results/                 # 결과 저장 디렉터리
│   ├── before.json
│   └── after.json
└── scenarios/               # 개별 시나리오 (독립 실행용)
    ├── 01_dashboard.js
    ├── 02_calendar.js
    ├── 03_task_list.js
    ├── 04_feedback_history.js
    └── 05_notifications.js
```

## 전체 실행 순서

### Step 1. 시드 데이터 주입
```bash
docker exec -i blaybus-mysql mysql -uroot -ppassword blaybus < k6/seed-data.sql
```
예상 결과:
- daily_planners: 730건 (2 멘티 × 365일)
- tasks:        5,110건 (730 × 7 과제)
- notifications: 1,500건 (3 유저 × 500건)

### Step 2. Before 측정 (인덱스 추가 전)
```bash
k6 run k6/run-all.js --out json=k6/results/before.json
```

### Step 3. 인덱스 추가
아래 SQL을 MySQL에 실행한다.
```sql
-- tasks 테이블 (대시보드 쿼리 최적화)
ALTER TABLE tasks
  ADD INDEX idx_task_mentee_status_checked_date  (mentee_id, status, is_mentor_checked, task_date),
  ADD INDEX idx_task_mentee_subject_mandatory_date (mentee_id, subject, is_mandatory, task_date),
  ADD INDEX idx_task_mentee_mandatory_status       (mentee_id, is_mandatory, status),
  ADD INDEX idx_task_mentee_date                   (mentee_id, task_date),
  ADD INDEX idx_task_daily_planner                 (daily_planner_id);

-- notifications 테이블 (알림 조회 최적화)
ALTER TABLE notifications
  ADD INDEX idx_notification_user_read    (user_id, is_read),
  ADD INDEX idx_notification_user_created (user_id);

-- task_feedbacks 테이블 (피드백 히스토리 최적화)
ALTER TABLE task_feedbacks
  ADD INDEX idx_feedback_task    (task_id),
  ADD INDEX idx_feedback_image   (image_id);
```

실행 방법:
```bash
docker exec -i blaybus-mysql mysql -uroot -ppassword blaybus << 'EOF'
ALTER TABLE tasks
  ADD INDEX idx_task_mentee_status_checked_date   (mentee_id, status, is_mentor_checked, task_date),
  ADD INDEX idx_task_mentee_subject_mandatory_date (mentee_id, subject, is_mandatory, task_date),
  ADD INDEX idx_task_mentee_mandatory_status       (mentee_id, is_mandatory, status),
  ADD INDEX idx_task_mentee_date                   (mentee_id, task_date),
  ADD INDEX idx_task_daily_planner                 (daily_planner_id);

ALTER TABLE notifications
  ADD INDEX idx_notification_user_read    (user_id, is_read),
  ADD INDEX idx_notification_user_created (user_id);

ALTER TABLE task_feedbacks
  ADD INDEX idx_feedback_task  (task_id),
  ADD INDEX idx_feedback_image (image_id);
EOF
```

### Step 4. After 측정 (인덱스 추가 후)
```bash
k6 run k6/run-all.js --out json=k6/results/after.json
```

### Step 5. 결과 비교
```bash
bash k6/compare-results.sh
```

---

## 개별 시나리오 실행
```bash
# 대시보드만
k6 run k6/scenarios/01_dashboard.js

# 캘린더만
k6 run k6/scenarios/02_calendar.js

# 과제 목록만
k6 run k6/scenarios/03_task_list.js

# 피드백 히스토리만
k6 run k6/scenarios/04_feedback_history.js

# 알림만
k6 run k6/scenarios/05_notifications.js
```

## 테스트 구성
| 시나리오 | VUs | 측정 시간 | 핵심 인덱스 |
|---------|-----|---------|-----------|
| 대시보드 | 30 | 30s | `(mentee_id, status, is_mentor_checked, task_date)` |
| 캘린더 | 20 | 30s | `(daily_planner_id)`, `(mentee_id, task_date)` |
| 과제 목록 | 25 | 30s | `(mentee_id, task_date)` |
| 피드백 히스토리 | 15 | 30s | `(task_id)`, `(image_id)` |
| 알림 목록 | 25 | 30s | `(user_id, is_read)` |
