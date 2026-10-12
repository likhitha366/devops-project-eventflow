FROM nginx:1.27-alpine
COPY *.html *.js *.css /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/templates/default.conf.template
ENV PORT=10000 \
    NGINX_ENVSUBST_FILTER='^(PORT|USER_SERVICE_HOSTPORT|EVENT_SERVICE_HOSTPORT|REGISTRATION_SERVICE_HOSTPORT)$' \
    USER_SERVICE_HOSTPORT=user-service:8081 \
    EVENT_SERVICE_HOSTPORT=event-service:8082 \
    REGISTRATION_SERVICE_HOSTPORT=registration-service:8083
EXPOSE 10000
