FROM amazoncorretto:17

ARG aws_access_key
ARG aws_secret_key
ARG aws_region

ENV LANG C.UTF-8
ENV APPLICATION haylugar
ENV ENVIRONMENT dev

ENV AWS_ACCESS_KEY=$aws_access_key
ENV AWS_SECRET_KEY=$aws_secret_key
ENV AWS_REGION=$aws_region


EXPOSE 8080

COPY entrypoint.sh /vol/greenbundle/component/jar/entrypoint.sh
COPY target/$APPLICATION-*.jar /vol/greenbundle/component/jar/$APPLICATION.jar

HEALTHCHECK CMD curl -f http://localhost:8080/healthcheck || exit 1

RUN chmod +x /vol/greenbundle/component/jar/entrypoint.sh

CMD /vol/greenbundle/component/jar/entrypoint.sh