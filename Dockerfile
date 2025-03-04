FROM openjdk:25-ea-4-jdk-oraclelinux9
WORKDIR /app
COPY ./ /app
EXPOSE 8080
CMD ["mkdir", "-p", "/app/data"]
COPY src/main/java/com/example/data/*.json /app/data/
ENV USERS_JSON_PATH=/app/data/users.json
ENV PRODUCTS_JSON_PATH=/app/data/products.json
ENV CARTS_JSON_PATH=/app/data/carts.json
ENV ORDERS_JSON_PATH=/app/data/orders.json
CMD ["java","-jar","/app/target/mini1.jar"]
