# Полезные PromQL запросы для Ingress Nginx

### Статус и доступность

Количество активных Ingress-подов: count(up{job="ingress-nginx-controller-metrics"})

Проверка получения данных по конкретному хосту:
nginx_ingress_controller_requests{host="arch.homework"}

### Задержки (Latency) — Интерпретация результата

Ваш текущий результат: 0.909 (это 909 мс).

#### P95 (95-й перцентиль) времени обработки:
histogram_quantile(0.95, sum(rate(nginx_ingress_controller_request_duration_seconds_bucket[5m])) by (le, ingress, host))
Что это значит: 95% пользователей ждут ответа меньше этого времени. Только 5% "медленных" запросов превышают это значение.

#### P50 (Медиана):
histogram_quantile(0.50, sum(rate(nginx_ingress_controller_request_duration_seconds_bucket[5m])) by (le, ingress))
Что это значит: Время ответа для "среднего" пользователя.

### Ошибки и производительность (RPS)

RPS (Количество запросов в секунду) по хостам:
sum(rate(nginx_ingress_controller_requests[5m])) by (host)

Процент успешных запросов (Success Rate %):
sum(rate(nginx_ingress_controller_requests{status!~"5.."}[5m])) / sum(rate(nginx_ingress_controller_requests[5m])) * 100