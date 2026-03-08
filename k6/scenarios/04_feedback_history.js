/**
 * 시나리오 04: 피드백 히스토리 조회
 *
 * 대상 API:
 *   - GET /feedbacks/history?menteeId=2             (전체)
 *   - GET /feedbacks/history?menteeId=2&subject=MATH (과목 필터)
 *   - GET /feedbacks/history?menteeId=2&year=2025&month=12 (날짜 필터)
 *   - GET /feedbacks/yesterday (어제 피드백)
 *
 * 내부 쿼리:
 *   findFeedbacksWithFilters → task_feedbacks JOIN task JOIN mentee (3-table JOIN)
 *   findYesterdayFeedbacks   → 동일한 JOIN 구조
 *
 * 인덱스 효과 예상: ★★★
 *   task_feedbacks.task_id 인덱스 → JOIN 성능
 *   (반정규화 후: task_feedbacks.mentee_id, task_feedbacks.subject 직접 인덱스)
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, authHeaders } from '../lib/auth.js';

export const options = {
  scenarios: {
    feedback_all: {
      executor: 'constant-vus',
      vus: 15,
      duration: '30s',
      tags: { scenario: 'feedback_all' },
    },
    feedback_subject_filter: {
      executor: 'constant-vus',
      vus: 15,
      duration: '30s',
      tags: { scenario: 'feedback_subject_filter' },
    },
    feedback_date_filter: {
      executor: 'constant-vus',
      vus: 10,
      duration: '30s',
      tags: { scenario: 'feedback_date_filter' },
    },
    feedback_yesterday: {
      executor: 'constant-vus',
      vus: 10,
      duration: '30s',
      tags: { scenario: 'feedback_yesterday' },
    },
  },
  thresholds: {
    'http_req_duration{scenario:feedback_all}':            ['p(95)<1000', 'p(99)<2000'],
    'http_req_duration{scenario:feedback_subject_filter}': ['p(95)<1000', 'p(99)<2000'],
    'http_req_duration{scenario:feedback_date_filter}':    ['p(95)<1000', 'p(99)<2000'],
    'http_req_duration{scenario:feedback_yesterday}':      ['p(95)<500',  'p(99)<1000'],
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
  const base = `${BASE_URL}/feedbacks/history?menteeId=2&page=0&size=10`;

  // 전체 피드백 목록
  const r1 = http.get(base, authHeaders(data.mentorToken));
  check(r1, { 'feedback all 200': (r) => r.status === 200 });

  // 과목 필터
  const r2 = http.get(`${base}&subject=MATH`, authHeaders(data.mentorToken));
  check(r2, { 'feedback subject filter 200': (r) => r.status === 200 });

  // 날짜 필터
  const r3 = http.get(`${base}&year=2025&month=12`, authHeaders(data.mentorToken));
  check(r3, { 'feedback date filter 200': (r) => r.status === 200 });

  // 어제 피드백 (멘티 본인만 조회 가능)
  const r4 = http.get(`${BASE_URL}/feedbacks/yesterday?page=0&size=10`,
    authHeaders(data.menteeToken));
  check(r4, { 'feedback yesterday 200': (r) => r.status === 200 });

  sleep(0.1);
}
