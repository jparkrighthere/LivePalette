apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ${USER_NAME}-tls-ingress
  namespace: ${NAMESPACE}
spec:
  ingressClassName: public-nginx
  rules:
  - host: ${USER_NAME}-tls.skala25a.project.skala-ai.com
    http:
      paths:
      - backend:
          service:
            name: ${USER_NAME}-${SERVICE_NAME}
            port:
              number: 8080
        path: /api
        pathType: Prefix
      - backend:
          service:
            name: ${USER_NAME}-${SERVICE_NAME}
            port:
              number: 8081
        path: /actuator
        pathType: Prefix
      - backend:
          service:
            name: ${USER_NAME}-${SERVICE_NAME}
            port:
              number: 8080
        path: /swagger
        pathType: Prefix
  tls:
  - hosts:
    - ${USER_NAME}-tls.skala25a.project.skala-ai.com
    secretName: ${USER_NAME}-manual-tls-secret
