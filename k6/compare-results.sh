#!/usr/bin/env bash
# ============================================================
# Before / After 성능 결과 비교
#
# 사용법: bash k6/compare-results.sh
# 필요: k6/results/before.json, k6/results/after.json
# ============================================================

BEFORE="k6/results/before.json"
AFTER="k6/results/after.json"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

if [[ ! -f "$BEFORE" || ! -f "$AFTER" ]]; then
  echo -e "${RED}오류: $BEFORE 또는 $AFTER 파일이 없습니다.${NC}"
  echo "먼저 k6를 실행하세요:"
  echo "  k6 run k6/run-all.js --out json=k6/results/before.json"
  echo "  (인덱스 추가 후)"
  echo "  k6 run k6/run-all.js --out json=k6/results/after.json"
  exit 1
fi

# JSON에서 특정 메트릭의 p값 추출
# $1: 파일, $2: 메트릭명, $3: p 값 (p(50)|p(95)|p(99)|avg)
extract() {
  local file="$1" metric="$2" stat="$3"
  python3 - <<EOF
import json, sys

data = []
with open("$file") as f:
    for line in f:
        try:
            obj = json.loads(line)
            if obj.get("type") == "Point" and obj.get("metric") == "$metric":
                data.append(obj["data"]["value"])
        except:
            pass

if not data:
    print("N/A")
    sys.exit(0)

data.sort()
n = len(data)
stats = {
    "avg": sum(data)/n,
    "min": data[0],
    "max": data[-1],
    "p(50)": data[int(n*0.50)],
    "p(95)": data[int(n*0.95)],
    "p(99)": data[int(n*0.99)],
}
val = stats.get("$stat", "N/A")
if isinstance(val, float):
    print(f"{val:.1f}")
else:
    print(val)
EOF
}

# 개선율 계산 (before → after)
improvement() {
  local before="$1" after="$2"
  python3 - <<EOF
try:
    b, a = float("$before"), float("$after")
    if b == 0:
        print("N/A")
    else:
        pct = (b - a) / b * 100
        sign = "▼" if pct > 0 else "▲"
        print(f"{sign}{abs(pct):.1f}%")
except:
    print("N/A")
EOF
}

# 색상 출력 (개선되면 초록, 악화되면 빨강)
colored_diff() {
  local before="$1" after="$2"
  local diff
  diff=$(improvement "$before" "$after")
  if [[ "$diff" == ▼* ]]; then
    echo -e "${GREEN}${diff}${NC}"
  elif [[ "$diff" == ▲* ]]; then
    echo -e "${RED}${diff}${NC}"
  else
    echo "$diff"
  fi
}

# ──────────────────────────────────────────────────────────────
echo ""
echo -e "${BOLD}${CYAN}════════════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${CYAN}   설스터디 성능 테스트 결과 비교 (Before → After Index)${NC}"
echo -e "${BOLD}${CYAN}════════════════════════════════════════════════════════════════${NC}"
echo ""

METRICS=(
  "dashboard_duration:대시보드 (10 COUNT queries)"
  "calendar_duration:캘린더 (DailyPlan+Task JOIN)"
  "task_list_duration:과제 목록 (mentee_id+task_date)"
  "feedback_duration:피드백 히스토리 (3-table JOIN)"
  "notification_duration:알림 목록 (user_id+is_read)"
)

# 헤더
printf "%-38s  %8s  %8s  %8s  %8s  %9s\n" \
  "시나리오" "Before p95" "After p95" "Before p99" "After p99" "개선율(p95)"
echo "──────────────────────────────────────────────────────────────────────────────"

for entry in "${METRICS[@]}"; do
  metric="${entry%%:*}"
  label="${entry#*:}"

  b95=$(extract "$BEFORE" "$metric" "p(95)")
  a95=$(extract "$AFTER"  "$metric" "p(95)")
  b99=$(extract "$BEFORE" "$metric" "p(99)")
  a99=$(extract "$AFTER"  "$metric" "p(99)")

  diff_colored=$(colored_diff "$b95" "$a95")

  printf "%-38s  %8s  %8s  %8s  %8s  " \
    "$label" "${b95}ms" "${a95}ms" "${b99}ms" "${a99}ms"
  echo -e "$diff_colored"
done

echo ""
echo -e "${BOLD}평균 응답시간 (avg)${NC}"
echo "──────────────────────────────────────────────────────────────────────────────"

for entry in "${METRICS[@]}"; do
  metric="${entry%%:*}"
  label="${entry#*:}"

  bavg=$(extract "$BEFORE" "$metric" "avg")
  aavg=$(extract "$AFTER"  "$metric" "avg")
  diff_colored=$(colored_diff "$bavg" "$aavg")

  printf "%-38s  %8s  %8s  " "$label" "${bavg}ms" "${aavg}ms"
  echo -e "$diff_colored"
done

echo ""
echo -e "${BOLD}전체 요청 에러율${NC}"
echo "──────────────────────────────────────────────────────────────────────────────"
berr=$(extract "$BEFORE" "error_rate" "avg")
aerr=$(extract "$AFTER"  "error_rate" "avg")
printf "%-38s  %8s  %8s\n" "에러율" "$berr" "$aerr"

echo ""
echo -e "${CYAN}════════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}단위: ms (밀리초) | ▼ = 개선 | ▲ = 성능 저하${NC}"
echo ""
