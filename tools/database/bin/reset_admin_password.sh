#!/usr/bin/env bash
# Reset the local development admin password (local / development ONLY).
#
# Usage:
#   tools/database/bin/reset_admin_password.sh            # reset to default "admin"
#   tools/database/bin/reset_admin_password.sh <password> # reset to a custom password
#
# The password is stored as a BCrypt hash ({bcrypt}$2a$...), never plaintext.
# The script also clears the Redis (db 11) user cache so the change applies at once.
# Do NOT run this against sit / pro.

set -euo pipefail

NEW_PASSWORD="${1:-admin}"
PG_CONTAINER="${PG_CONTAINER:-pgsql}"
REDIS_CONTAINER="${REDIS_CONTAINER:-redis}"
DB_NAME="${DB_NAME:-one_platform}"
REDIS_PASSWORD="${REDIS_PASSWORD:-redis}"
REDIS_DB="${REDIS_DB:-11}"
APP_USER="${APP_USER:-admin}"

echo "[1/3] target: container=${PG_CONTAINER} db=${DB_NAME} user=${APP_USER}"

CRYPTO_JAR="$(find "${HOME}/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-crypto" \
    -name 'spring-security-crypto-*.jar' ! -name '*-sources.jar' 2>/dev/null | sort | tail -1)"
LOGGING_JAR="$(find "${HOME}/.gradle/caches/modules-2/files-2.1/commons-logging/commons-logging" \
    -name 'commons-logging-*.jar' ! -name '*-sources.jar' 2>/dev/null | sort | tail -1)"

if [[ -z "${CRYPTO_JAR}" || -z "${LOGGING_JAR}" ]]; then
    echo "ERROR: spring-security-crypto / commons-logging jar not found. Run ./gradlew build once first." >&2
    exit 1
fi

TMPDIR_HASH="$(mktemp -d)"
trap 'rm -rf "${TMPDIR_HASH}"' EXIT

cat > "${TMPDIR_HASH}/GenPwd.java" <<'JAVA'
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenPwd {
    public static void main(String[] args) {
        System.out.print("{bcrypt}" + new BCryptPasswordEncoder().encode(args[0]));
    }
}
JAVA

javac -cp "${CRYPTO_JAR}" -d "${TMPDIR_HASH}" "${TMPDIR_HASH}/GenPwd.java"
HASH="$(java -cp "${TMPDIR_HASH}:${CRYPTO_JAR}:${LOGGING_JAR}" GenPwd "${NEW_PASSWORD}")"

if [[ "${HASH}" != \{bcrypt\}\$2* ]]; then
    echo "ERROR: generated hash has an unexpected format." >&2
    exit 1
fi
echo "[2/3] bcrypt hash generated"

docker exec -i "${PG_CONTAINER}" psql -U root -d "${DB_NAME}" -v ON_ERROR_STOP=1 \
    -v hash="${HASH}" -v app_user="${APP_USER}" <<'SQL'
update sys_user
   set password = :'hash',
       updated_at = now()
 where username = :'app_user';
SQL
echo "[3/3] database updated"

docker exec "${REDIS_CONTAINER}" redis-cli -a "${REDIS_PASSWORD}" --no-auth-warning -n "${REDIS_DB}" \
    --scan --pattern 'user:*' 2>/dev/null | while read -r key; do
    [[ -n "${key}" ]] && docker exec "${REDIS_CONTAINER}" redis-cli -a "${REDIS_PASSWORD}" \
        --no-auth-warning -n "${REDIS_DB}" del "${key}" >/dev/null
done
echo "[3/3] redis user cache cleared"

echo "DONE: password for \"${APP_USER}\" has been reset."
