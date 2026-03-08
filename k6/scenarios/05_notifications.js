/**
 * 시나리오 05: 알림 목록 조회
 *
 * 대상 API:
 *   - GET /notifications?filter=all
 *   - GET /notifications?filter=unread
 *   - GET /notifications?filter=read
 *
 * 내부 쿼리:
 *   findByUserId(userId, pageable)            → notifications WHERE user_id = ?
 *   findByUserIdAndIsRead(userId, false, ...) → notifications WHERE user_id = ? AND is_read = ?
 *
 * 인덱스 효과 예상: ★★★
 *   idx_notification_user_read → (user_id, is_read) 복합 인덱스
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, authHeaders } from '../lib/auth.js';

export const options = {
  scenarios: {
    notifications_all: {
      executor: 'constant-vus',
      vus: 30,
      duration: '30s',
      tags: { scenario: 'notifications_all' },
    },
    notifications_unread: {
      executor: 'constant-vus',
      vus: 30,
      duration: '30s',
      tags: { scenario: 'notifications_unread' },
    },
    notifications_read: {
      executor: 'constant-vus',
      vus: 20,
      duration: '30s',
      tags: { scenario: 'notifications_read' },
    },
  },
  thresholds: {
    'http_req_duration{scenario:notifications_all}':    ['p(95)<500', 'p(99)<1000'],
    'http_req_duration{scenario:notifications_unread}': ['p(95)<500', 'p(99)<1000'],
    'http_req_duration{scenario:notifications_read}':   ['p(95)<500', 'p(99)<1000'],
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
  const base = `${BASE_URL}/notifications?size=20`;

  // 전체
  const r1 = http.get(`${base}&filter=all&page=0`, authHeaders(data.menteeToken));
  check(r1, { 'notifications all 200': (r) => r.status === 200 });

  // 미읽음
  const r2 = http.get(`${base}&filter=unread&page=0`, authHeaders(data.menteeToken));
  check(r2, { 'notifications unread 200': (r) => r.status === 200 });

  // 읽음
  const r3 = http.get(`${base}&filter=read&page=0`, authHeaders(data.menteeToken));
  check(r3, { 'notifications read 200': (r) => r.status === 200 });

  sleep(0.1);
}
