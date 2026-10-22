
FROM nginx:1.27-alpine

COPY html/index.html /usr/share/nginx/html/index.html
COPY css/styles.css /usr/share/nginx/html/styles.css
COPY js/script.js /usr/share/nginx/html/script.js
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
