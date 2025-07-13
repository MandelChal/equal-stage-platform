#!/bin/bash

BASE_URL="http://localhost:8080"
DB_CONTAINER="equal-stage-platform-db-1"
DB_USER="postgres"
DB_NAME="stage_db"  # עדכני אם שם הבסיס שונה

ENDPOINTS=(
  "/api/fake/lecturers"
  "/api/fake/lectures"
  "/api/fake/reset"
)

echo "⌛ ממתינה שהשרת יעלה..."

until curl -s "$BASE_URL" > /dev/null; do
  printf "."
  sleep 2
done

echo -e "\n🚀 השרת זמין! מריצה POST לכל endpoints..."

for endpoint in "${ENDPOINTS[@]}"
do
  full_url="${BASE_URL}${endpoint}"
  echo "➡️  POST $full_url"
  
  response=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$full_url")

  if [ "$response" == "200" ]; then
    echo "✅ $endpoint OK"
  else
    echo "❌ $endpoint שגיאה: $response"
  fi
done

echo ""
echo "📦 בודקת נתונים בבסיס הנתונים (PostgreSQL)..."

docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" <<EOF
SELECT 'מספר מרצים:', COUNT(*) FROM lecturers;
SELECT 'מספר הרצאות:', COUNT(*) FROM lectures;
EOF
