/**
 * 시나리오 02: 플래너 캘린더 조회
 *
 * 대상 API:
 *   - GET /plans/calendar?year=2025&month=12              (전체)
 *   - GET /plans/calendar?year=2025&month=12&subject=KOREAN (과목 필터)
 *   - GET /plans/calendar?year=2025&month=12&incompleteOnly=true (미완료 필터)
 *
 * 내부 쿼리:
 *   1. findByMenteeIdAndPlanDateBetweenWithFilters → daily_planners + tasks JOIN
 *   2. findByDailyPlanIdIn                         → tasks WHERE daily_planner_id IN (...)
 *
 * 인덱스 효과 예상: ★★★★
 *   idx_task_daily_planner    → tasks.daily_planner_id
 *   idx_task_mentee_date      → tasks.mentee_id + task_date
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, authHeaders } from '../lib/auth.js';

export const options = {
  scenarios: {
    calendar_basic: {
      executor: 'constant-vus',
      vus: 20,
      duration: '30s',
      tags: { scenario: 'calendar_basic' },
    },
    calendar_subject_filter: {
      executor: 'constant-vus',
      vus: 15,
      duration: '30s',
      tags: { scenario: 'calendar_subject_filter' },
    },
    calendar_incomplete_filter: {
      executor: 'constant-vus',
      vus: 15,
      duration: '30s',
      tags: { scenario: 'calendar_incomplete_filter' },
    },
  },
  thresholds: {
    'http_req_duration{scenario:calendar_basic}':            ['p(95)<1000', 'p(99)<2000'],
    'http_req_duration{scenario:calendar_subject_filter}':   ['p(95)<1000', 'p(99)<2000'],
    'http_req_duration{scenario:calendar_incomplete_filter}':['p(95)<1000', 'p(99)<2000'],
    'http_req_failed': ['rate<0.01'],
  },
};

export function setup() {
  const menteeRes = http.post(`${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentee1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } });

  return { menteeToken: menteeRes.headers['Authorization'] };
}

export default function (data) {
  const base = `${BASE_URL}/plans/calendar?year=2025&month=12&size=31&sort=planDate`;

  // 기본 캘린더
  const r1 = http.get(base, authHeaders(data.menteeToken));
  check(r1, { 'calendar basic 200': (r) => r.status === 200 });

  // 과목 필터
  const r2 = http.get(`${base}&subject=KOREAN`, authHeaders(data.menteeToken));
  check(r2, { 'calendar KOREAN filter 200': (r) => r.status === 200 });

  // 미완료 필터
  const r3 = http.get(`${base}&incompleteOnly=true`, authHeaders(data.menteeToken));
  check(r3, { 'calendar incompleteOnly 200': (r) => r.status === 200 });

  sleep(0.1);
}
