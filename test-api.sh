#!/bin/bash

# Test script for TraceHub APIs
# Make sure all services are running before executing this script

echo "🧪 Testing TraceHub APIs..."
echo "================================"

# Test Ingest Service
echo "📤 Testing Ingest Service..."
echo "POST /api/v1/logs"
curl -X POST http://localhost:8081/api/v1/logs \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": "2025-01-24T10:00:00Z",
    "tenantId": "t1",
    "userId": "u123",
    "action": "LOGIN",
    "status": "SUCCESS",
    "actorIp": "192.168.1.100",
    "message": "User login successful",
    "metadata": {"country": "VN", "ua": "Firefox"}
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test Query Service
echo "🔍 Testing Query Service..."
echo "POST /api/v1/search"
curl -X POST http://localhost:8083/api/v1/search \
  -H "Content-Type: application/json" \
  -d '{
    "tenantId": "t1",
    "from": "2025-01-24T09:00:00Z",
    "to": "2025-01-24T11:00:00Z",
    "action": "LOGIN",
    "size": 100
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test Notifier Service
echo "🚨 Testing Notifier Service..."
echo "GET /api/v1/alerts/rules"
curl -X GET http://localhost:8084/api/v1/alerts/rules \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test Health Endpoints
echo "🏥 Testing Health Endpoints..."
echo "Ingest Service: $(curl -s http://localhost:8081/health)"
echo "Processor Service: $(curl -s http://localhost:8082/health)"
echo "Query Service: $(curl -s http://localhost:8083/health)"
echo "Notifier Service: $(curl -s http://localhost:8084/health)"

echo -e "\n"
echo "✅ API testing completed!"
echo "================================"
echo ""
echo "📊 Check OpenSearch Dashboards: http://localhost:5601"
echo "📈 Check Prometheus metrics: http://localhost:8081/actuator/prometheus"
