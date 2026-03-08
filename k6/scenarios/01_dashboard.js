/**
 * 시나리오 01: 대시보드 조회
 *
 * 대상 API:
 *   - GET /mentor/2?type=WEEK  (멘토가 멘티 대시보드 조회)
 *   - GET /mentor/2?type=MONTH
 *   - GET /mentee/me?type=WEEK  (멘티 본인 대시보드)
 *
 * 내부 쿼리 (요청당 10회):
 *   1. countByMenteeIdAndStatusAndIsMentorCheckedAndTaskDateBetween
 *   2. countByMenteeIdAndIsMandatoryAndStatusNot
 *   3. countUncheckedQuestionsByMentee       ← 3-table JOIN
 *   4. existsByMenteeIdAndPlanDateAndMentorFeedbackIsNull
 *   5~10. subject별 진행률 (KOREAN/MATH/ENGLISH × 2)
 *
 * 인덱스 효과 예상: ★★★★★
 *   idx_task_mentee_status_checked_date    → 쿼리 1
 *   idx_task_mentee_mandatory_status       → 쿼리 2
 *   idx_task_mentee_subject_mandatory_date → 쿼리 5~10
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, authHeaders } from '../lib/auth.js';

export const options = {
  scenarios: {
    dashboard_mentor_week: {
      executor: 'constant-vus',
      vus: 20,
      duration: '30s',
      tags: { scenario: 'dashboard_mentor_week' },
    },
    dashboard_mentor_month: {
      executor: 'constant-vus',
      vus: 10,
      duration: '30s',
      startTime: '5s',
      tags: { scenario: 'dashboard_mentor_month' },
    },
    dashboard_mentee_week: {
      executor: 'constant-vus',
      vus: 20,
      duration: '30s',
      tags: { scenario: 'dashboard_mentee_week' },
    },
  },
  thresholds: {
    'http_req_duration{scenario:dashboard_mentor_week}':  ['p(95)<1000', 'p(99)<2000'],
    'http_req_duration{scenario:dashboard_mentor_month}': ['p(95)<1000', 'p(99)<2000'],
    'http_req_duration{scenario:dashboard_mentee_week}':  ['p(95)<1000', 'p(99)<2000'],
    'http_req_failed': ['rate<0.01'],
  },
};

export function setup() {
  // 로그인 - mentor1, mentee1 각각 토큰 발급
  const mentorRes  = http.post(`${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentor1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } });
  const menteeRes  = http.post(`${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentee1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } });

  return {
    mentorToken: mentorRes.headers['Authorization'],
    menteeToken: menteeRes.headers['Authorization'],
  };
}

export default function (data) {
  const scenario = __ENV.K6_SCENARIO_ID || 'dashboard_mentor_week';

  if (scenario === 'dashboard_mentor_week') {
    const res = http.get(`${BASE_URL}/mentor/2?type=WEEK`, authHeaders(data.mentorToken));
    check(res, {
      'dashboard mentor WEEK 200': (r) => r.status === 200,
      'dashboard mentor WEEK has data': (r) => r.json('menteeId') !== undefined,
    });

  } else if (scenario === 'dashboard_mentor_month') {
    const res = http.get(`${BASE_URL}/mentor/2?type=MONTH`, authHeaders(data.mentorToken));
    check(res, {
      'dashboard mentor MONTH 200': (r) => r.status === 200,
    });

  } else if (scenario === 'dashboard_mentee_week') {
    const res = http.get(`${BASE_URL}/mentee/me?type=WEEK`, authHeaders(data.menteeToken));
    check(res, {
      'dashboard mentee WEEK 200': (r) => r.status === 200,
    });
  }

  sleep(0.1);
}
