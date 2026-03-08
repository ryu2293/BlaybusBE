/**
 * 시나리오 03: 날짜별 과제 목록 조회
 *
 * 대상 API:
 *   - GET /mentee/tasks/list?date=2025-12-15  (멘티 본인)
 *   - GET /mentor/tasks/list/2?date=2025-12-15 (멘토가 멘티 조회)
 *
 * 내부 쿼리:
 *   findByMenteeIdAndTaskDate → tasks WHERE mentee_id = ? AND task_date = ?
 *
 * 인덱스 효과 예상: ★★★★
 *   idx_task_mentee_date → (mentee_id, task_date)
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, authHeaders } from '../lib/auth.js';

// 테스트 날짜 목록 (시드 데이터가 있는 날짜 범위 내)
const TEST_DATES = [
  '2025-03-15', '2025-06-10', '2025-09-20',
  '2025-12-01', '2025-12-15', '2025-12-25',
];

export const options = {
  scenarios: {
    task_list_mentee: {
      executor: 'constant-vus',
      vus: 30,
      duration: '30s',
      tags: { scenario: 'task_list_mentee' },
    },
    task_list_mentor: {
      executor: 'constant-vus',
      vus: 20,
      duration: '30s',
      tags: { scenario: 'task_list_mentor' },
    },
  },
  thresholds: {
    'http_req_duration{scenario:task_list_mentee}': ['p(95)<500', 'p(99)<1000'],
    'http_req_duration{scenario:task_list_mentor}': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed': ['rate<0.01'],
  },
};

export function setup() {
  const mentorRes = http.post(`${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentor1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } });
  const menteeRes = http.post(`${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentee1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } });

  return {
    mentorToken: mentorRes.headers['Authorization'],
    menteeToken: menteeRes.headers['Authorization'],
  };
}

export default function (data) {
  // VU별로 다른 날짜를 조회해 캐시 효과 방지
  const date = TEST_DATES[__VU % TEST_DATES.length];

  const r1 = http.get(
    `${BASE_URL}/mentee/tasks/list?date=${date}`,
    authHeaders(data.menteeToken)
  );
  check(r1, { 'task list mentee 200': (r) => r.status === 200 });

  const r2 = http.get(
    `${BASE_URL}/mentor/tasks/list/2?date=${date}`,
    authHeaders(data.mentorToken)
  );
  check(r2, { 'task list mentor 200': (r) => r.status === 200 });

  sleep(0.1);
}
