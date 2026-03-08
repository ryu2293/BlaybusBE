/**
 * 날짜별 과제 목록 조회 성능 벤치마크
 *
 * 대상 API: GET /mentee/tasks/list?date=<date>
 * 내부 쿼리: SELECT * FROM tasks WHERE mentee_id = ? AND task_date = ?
 *
 * Before: mentee_id 단일 FK 인덱스 → 해당 멘티 51,100건 스캔 후 날짜 필터
 * After:  (mentee_id, task_date) 복합 인덱스 → 정확한 날짜 rows만 직접 탐색
 *
 * 실행:
 *   k6 run k6/task-list-bench.js --out json=k6/results/before.json
 *   (인덱스 추가 후)
 *   k6 run k6/task-list-bench.js --out json=k6/results/after.json
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { BASE_URL, authHeaders } from './lib/auth.js';

// ── 커스텀 메트릭 ────────────────────────────────────────────
const taskListDuration = new Trend('task_list_duration', true);
const errorRate        = new Rate('error_rate');

// ── 부하 설정 ────────────────────────────────────────────────
export const options = {
  stages: [
    { duration: '10s', target: 50 },  // ramp-up
    { duration: '40s', target: 50 },  // steady state
    { duration: '5s',  target: 0  },  // ramp-down
  ],
  thresholds: {
    'task_list_duration':    ['p(95)<800', 'p(99)<1500'],
    'http_req_duration':     ['p(95)<800', 'p(99)<1500'],
    'http_req_failed':       ['rate<0.01'],
    'error_rate':            ['rate<0.01'],
  },
};

// ── 시드 데이터 날짜 범위: 2025-01-01 ~ 2025-12-31
// VU별로 다른 날짜를 요청해 쿼리 캐시 효과 제거
const DATES = (function () {
  const list = [];
  const start = new Date('2025-01-01');
  for (let i = 0; i < 365; i++) {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    list.push(d.toISOString().slice(0, 10)); // "YYYY-MM-DD"
  }
  return list;
})();

// ── Setup: 로그인 ─────────────────────────────────────────────
export function setup() {
  const menteeRes = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ username: 'mentee1', password: '1234' }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (menteeRes.status !== 200) {
    throw new Error(`로그인 실패: status=${menteeRes.status}`);
  }

  return { menteeToken: menteeRes.headers['Authorization'] };
}

// ── 메인 테스트 ───────────────────────────────────────────────
export default function (data) {
  // VU 번호 + iteration 조합으로 날짜 분산 → 매 요청마다 다른 날짜
  const date = DATES[(__VU * 7 + __ITER) % DATES.length];

  const res = http.get(
    `${BASE_URL}/mentee/tasks/list?date=${date}`,
    authHeaders(data.menteeToken)
  );

  taskListDuration.add(res.timings.duration);

  const ok = check(res, {
    'status 200':       (r) => r.status === 200,
    'body is array':    (r) => {
      try { return Array.isArray(r.json()); } catch { return false; }
    },
  });

  errorRate.add(!ok);

  sleep(0.05); // 50ms 간격 (초당 ~20 req/VU 상한)
}

// ── 종료 요약 ─────────────────────────────────────────────────
export function handleSummary(data) {
  const d = data.metrics.task_list_duration;
  if (!d) return {};

  const values = d.values;
  console.log('\n========== 과제 목록 조회 성능 결과 ==========');
  console.log(`avg   : ${values.avg.toFixed(1)} ms`);
  console.log(`med   : ${values.med.toFixed(1)} ms`);
  console.log(`p(90) : ${values['p(90)'].toFixed(1)} ms`);
  console.log(`p(95) : ${values['p(95)'].toFixed(1)} ms`);
  console.log(`p(99) : ${(values['p(99)'] ?? values.max).toFixed(1)} ms`);
  console.log(`max   : ${values.max.toFixed(1)} ms`);
  console.log(`req/s : ${data.metrics.http_reqs.values.rate.toFixed(1)}`);
  console.log('===============================================\n');

  return {};
}
