/**
 * 통합 성능 테스트 스크립트 (Before & After 공용)
 *
 * 실행 방법:
 *   # Before (인덱스 추가 전)
 *   k6 run k6/run-all.js --out json=k6/results/before.json
 *
 *   # After (인덱스 추가 후)
 *   k6 run k6/run-all.js --out json=k6/results/after.json
 *
 *   # 결과 비교
 *   bash k6/compare-results.sh
 *
 * 테스트 구성:
 *   - 총 VUs: 100 (시나리오별 분산)
 *   - 예열: 10초 ramp-up
 *   - 측정: 30초 steady state
 *   - 쿨다운: 5초 ramp-down
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';
import { BASE_URL, authHeaders } from './lib/auth.js';

// ── 커스텀 메트릭 ─────────────────────────────────────────────────────────────
const dashboardDuration   = new Trend('dashboard_duration',   true);
const calendarDuration    = new Trend('calendar_duration',    true);
const taskListDuration    = new Trend('task_list_duration',   true);
const feedbackDuration    = new Trend('feedback_duration',    true);
const notificationDuration= new Trend('notification_duration',true);
const errorRate           = new Rate('error_rate');

// ── 테스트 설정 ───────────────────────────────────────────────────────────────
export const options = {
  scenarios: {
    // ① 대시보드: 가장 쿼리 많음 (요청당 10 COUNT)
    dashboard: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 30 },
        { duration: '30s', target: 30 },
        { duration: '5s',  target: 0  },
      ],
      tags: { scenario: 'dashboard' },
      env: { SCENARIO: 'dashboard' },
    },
    // ② 캘린더: DailyPlan + Task JOIN
    calendar: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 20 },
        { duration: '30s', target: 20 },
        { duration: '5s',  target: 0  },
      ],
      tags: { scenario: 'calendar' },
      env: { SCENARIO: 'calendar' },
    },
    // ③ 과제 목록: tasks WHERE mentee_id + task_date
    task_list: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 25 },
        { duration: '30s', target: 25 },
        { duration: '5s',  target: 0  },
      ],
      tags: { scenario: 'task_list' },
      env: { SCENARIO: 'task_list' },
    },
    // ④ 피드백 히스토리: task_feedbacks JOIN 3-table
    feedback_history: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 15 },
        { duration: '30s', target: 15 },
        { duration: '5s',  target: 0  },
      ],
      tags: { scenario: 'feedback_history' },
      env: { SCENARIO: 'feedback_history' },
    },
    // ⑤ 알림 목록: notifications WHERE user_id + is_read
    notifications: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 25 },
        { duration: '30s', target: 25 },
        { duration: '5s',  target: 0  },
      ],
      tags: { scenario: 'notifications' },
      env: { SCENARIO: 'notifications' },
    },
  },

  thresholds: {
    // 전체 에러율 1% 미만
    'error_rate': ['rate<0.01'],
    // 시나리오별 p95 기준
    'http_req_duration{scenario:dashboard}':        ['p(95)<2000'],
    'http_req_duration{scenario:calendar}':         ['p(95)<1500'],
    'http_req_duration{scenario:task_list}':        ['p(95)<800'],
    'http_req_duration{scenario:feedback_history}': ['p(95)<1500'],
    'http_req_duration{scenario:notifications}':    ['p(95)<800'],
    // 커스텀 메트릭
    'dashboard_duration':    ['p(95)<2000'],
    'calendar_duration':     ['p(95)<1500'],
    'task_list_duration':    ['p(95)<800'],
    'feedback_duration':     ['p(95)<1500'],
    'notification_duration': ['p(95)<800'],
  },
};

// ── 토큰 캐시 (VU별로 setup에서 받은 값 활용) ─────────────────────────────────
export function setup() {
  const mentorRes = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentor1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  const menteeRes = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentee1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (mentorRes.status !== 200 || menteeRes.status !== 200) {
    throw new Error(`로그인 실패! mentor=${mentorRes.status}, mentee=${menteeRes.status}`);
  }

  return {
    mentorToken: mentorRes.headers['Authorization'],
    menteeToken: menteeRes.headers['Authorization'],
  };
}

// ── 날짜 목록 (캐시 버스팅용) ─────────────────────────────────────────────────
const TEST_DATES = [
  '2025-03-01', '2025-06-15', '2025-09-10',
  '2025-11-20', '2025-12-01', '2025-12-15',
];

// ── 메인 테스트 함수 ───────────────────────────────────────────────────────────
export default function (data) {
  const scenario = __ENV.SCENARIO;

  switch (scenario) {
    case 'dashboard':
      runDashboard(data);
      break;
    case 'calendar':
      runCalendar(data);
      break;
    case 'task_list':
      runTaskList(data);
      break;
    case 'feedback_history':
      runFeedbackHistory(data);
      break;
    case 'notifications':
      runNotifications(data);
      break;
    default:
      // fallback: 전체 순서대로 실행
      runDashboard(data);
      runCalendar(data);
      runTaskList(data);
      runNotifications(data);
  }
}

// ── 시나리오별 함수 ───────────────────────────────────────────────────────────

function runDashboard(data) {
  group('dashboard', () => {
    // 멘토 → 멘티 대시보드 (WEEK): 10 COUNT queries
    const r1 = http.get(`${BASE_URL}/mentor/2?type=WEEK`, authHeaders(data.mentorToken));
    dashboardDuration.add(r1.timings.duration, { type: 'mentor_week' });
    const ok1 = check(r1, {
      'dashboard mentor WEEK 200': (r) => r.status === 200,
      'dashboard mentor WEEK body valid': (r) => {
        try { return r.json('menteeId') !== undefined; } catch { return false; }
      },
    });
    errorRate.add(!ok1);

    // 멘토 → 멘티 대시보드 (MONTH): 기간이 더 넓어 더 느림
    const r2 = http.get(`${BASE_URL}/mentor/2?type=MONTH`, authHeaders(data.mentorToken));
    dashboardDuration.add(r2.timings.duration, { type: 'mentor_month' });
    const ok2 = check(r2, { 'dashboard mentor MONTH 200': (r) => r.status === 200 });
    errorRate.add(!ok2);

    // 멘티 본인 대시보드 (WEEK)
    const r3 = http.get(`${BASE_URL}/mentee/me?type=WEEK`, authHeaders(data.menteeToken));
    dashboardDuration.add(r3.timings.duration, { type: 'mentee_week' });
    const ok3 = check(r3, { 'dashboard mentee WEEK 200': (r) => r.status === 200 });
    errorRate.add(!ok3);
  });

  sleep(0.1);
}

function runCalendar(data) {
  group('calendar', () => {
    const base = `${BASE_URL}/plans/calendar?year=2025&month=12&size=31&sort=planDate`;

    // 기본 조회 (월간 전체)
    const r1 = http.get(base, authHeaders(data.menteeToken));
    calendarDuration.add(r1.timings.duration, { filter: 'none' });
    const ok1 = check(r1, { 'calendar basic 200': (r) => r.status === 200 });
    errorRate.add(!ok1);

    // 과목 필터 (JPQL WHERE절 추가)
    const r2 = http.get(`${base}&subject=KOREAN`, authHeaders(data.menteeToken));
    calendarDuration.add(r2.timings.duration, { filter: 'subject' });
    const ok2 = check(r2, { 'calendar KOREAN 200': (r) => r.status === 200 });
    errorRate.add(!ok2);

    // 미완료 필터
    const r3 = http.get(`${base}&incompleteOnly=true`, authHeaders(data.menteeToken));
    calendarDuration.add(r3.timings.duration, { filter: 'incomplete' });
    const ok3 = check(r3, { 'calendar incomplete 200': (r) => r.status === 200 });
    errorRate.add(!ok3);
  });

  sleep(0.1);
}

function runTaskList(data) {
  group('task_list', () => {
    const date = TEST_DATES[__VU % TEST_DATES.length];

    // 멘티 본인 날짜별 과제 조회
    const r1 = http.get(
      `${BASE_URL}/mentee/tasks/list?date=${date}`,
      authHeaders(data.menteeToken)
    );
    taskListDuration.add(r1.timings.duration, { role: 'mentee' });
    const ok1 = check(r1, { 'task list mentee 200': (r) => r.status === 200 });
    errorRate.add(!ok1);

    // 멘토가 멘티 과제 조회
    const r2 = http.get(
      `${BASE_URL}/mentor/tasks/list/2?date=${date}`,
      authHeaders(data.mentorToken)
    );
    taskListDuration.add(r2.timings.duration, { role: 'mentor' });
    const ok2 = check(r2, { 'task list mentor 200': (r) => r.status === 200 });
    errorRate.add(!ok2);
  });

  sleep(0.1);
}

function runFeedbackHistory(data) {
  group('feedback_history', () => {
    const base = `${BASE_URL}/feedbacks/history?menteeId=2&page=0&size=10`;

    // 전체 피드백 히스토리
    const r1 = http.get(base, authHeaders(data.mentorToken));
    feedbackDuration.add(r1.timings.duration, { filter: 'all' });
    const ok1 = check(r1, { 'feedback history 200': (r) => r.status === 200 });
    errorRate.add(!ok1);

    // 과목 필터 (JOIN 후 f.task.subject 필터링)
    const r2 = http.get(`${base}&subject=MATH`, authHeaders(data.mentorToken));
    feedbackDuration.add(r2.timings.duration, { filter: 'subject' });
    const ok2 = check(r2, { 'feedback history MATH 200': (r) => r.status === 200 });
    errorRate.add(!ok2);

    // 년월 필터
    const r3 = http.get(`${base}&year=2025&month=12`, authHeaders(data.mentorToken));
    feedbackDuration.add(r3.timings.duration, { filter: 'date' });
    const ok3 = check(r3, { 'feedback history date filter 200': (r) => r.status === 200 });
    errorRate.add(!ok3);

    // 어제 피드백 (멘티 본인)
    const r4 = http.get(
      `${BASE_URL}/feedbacks/yesterday?page=0&size=10`,
      authHeaders(data.menteeToken)
    );
    feedbackDuration.add(r4.timings.duration, { filter: 'yesterday' });
    const ok4 = check(r4, { 'feedback yesterday 200': (r) => r.status === 200 });
    errorRate.add(!ok4);
  });

  sleep(0.1);
}

function runNotifications(data) {
  group('notifications', () => {
    const base = `${BASE_URL}/notifications?size=20&page=0`;

    // 전체 알림
    const r1 = http.get(`${base}&filter=all`, authHeaders(data.menteeToken));
    notificationDuration.add(r1.timings.duration, { filter: 'all' });
    const ok1 = check(r1, { 'notifications all 200': (r) => r.status === 200 });
    errorRate.add(!ok1);

    // 미읽음 (is_read = false) - 가장 자주 호출되는 패턴
    const r2 = http.get(`${base}&filter=unread`, authHeaders(data.menteeToken));
    notificationDuration.add(r2.timings.duration, { filter: 'unread' });
    const ok2 = check(r2, { 'notifications unread 200': (r) => r.status === 200 });
    errorRate.add(!ok2);

    // 읽음 (is_read = true)
    const r3 = http.get(`${base}&filter=read`, authHeaders(data.menteeToken));
    notificationDuration.add(r3.timings.duration, { filter: 'read' });
    const ok3 = check(r3, { 'notifications read 200': (r) => r.status === 200 });
    errorRate.add(!ok3);
  });

  sleep(0.1);
}
