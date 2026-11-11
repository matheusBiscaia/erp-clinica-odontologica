#!/bin/bash

BASE_URL="http://localhost:8080"

echo "1. Realizando login..."

RESPOSTA_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "'"$ADMIN_EMAIL"'",
    "senha": "'"$ADMIN_PASSWORD"'"
  }')

TOKEN=$(echo "$RESPOSTA_LOGIN" | python3 -c \
'import sys, json; print(json.load(sys.stdin).get("token", ""))')

if [ -z "$TOKEN" ]; then
  echo "❌ Login falhou."
  echo "$RESPOSTA_LOGIN"
  exit 1
fi

echo "✅ Login realizado."

testar_get() {
  NOME="$1"
  ROTA="$2"

  STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL$ROTA")

  if [ "$STATUS" = "200" ]; then
    echo "✅ $NOME: HTTP $STATUS"
  else
    echo "❌ $NOME: HTTP $STATUS"
  fi
}

echo
echo "2. Testando rotas protegidas..."

testar_get "Pacientes" "/api/pacientes"
testar_get "Dentistas" "/api/dentistas"
testar_get "Agendamentos" "/api/agendamentos"
testar_get "Prontuários" "/api/prontuarios"
testar_get "Pagamentos" "/api/pagamentos"

echo
echo "3. Testando acesso sem token..."

STATUS_SEM_TOKEN=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/api/pacientes")

if [ "$STATUS_SEM_TOKEN" = "403" ]; then
  echo "✅ Segurança funcionando: HTTP 403 sem token."
else
  echo "❌ Era esperado HTTP 403, mas retornou $STATUS_SEM_TOKEN."
fi

echo
echo "Testes finalizados."