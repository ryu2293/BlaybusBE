import http from 'k6/http';
import { check } from 'k6';

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

/**
 * 로그인 후 JWT 토큰을 반환한다.
 * @param {string} username
 * @param {string} password
 * @returns {string} "Bearer <token>"
 */
export function login(username, password) {
  const res = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ username, password }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const ok = check(res, {
    [`login ${username} 200`]: (r) => r.status === 200,
    [`login ${username} has token`]: (r) => r.headers['Authorization'] !== undefined,
  });

  if (!ok) {
    console.error(`로그인 실패: ${username} / status=${res.status}`);
    return null;
  }

  return res.headers['Authorization']; // "Bearer <token>"
}

/**
 * 공통 JSON 요청 헤더
 */
export function authHeaders(token) {
  return {
    headers: {
      Authorization: token,
      'Content-Type': 'application/json',
    },
  };
}
