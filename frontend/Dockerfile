# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# Bake API endpoints into the static build (browser talks to localhost:8080)
ARG VITE_API_URL=http://localhost:8080/api/v1
ARG VITE_AUTH_URL=http://localhost:8080/api/auth
ENV VITE_API_URL=${VITE_API_URL}
ENV VITE_AUTH_URL=${VITE_AUTH_URL}
RUN npm run build

# ---- Runtime stage ----
FROM nginx:alpine
# Single-page app fallback
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
