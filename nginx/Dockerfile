FROM nginx
RUN rm /etc/nginx/conf.d/default.conf
COPY conf/nginx.conf /etc/nginx/conf.d/default.conf
COPY ssl /etc/nginx/ssl