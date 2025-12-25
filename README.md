# 2025-10-otus-microservices-Didenko
Репозиторий для курса по микросервисной архитектуре

## HW2

#### Работа с Docker Hub
`docker build -t danilflint/ok_microservice:latest .`
`docker push danilflint/ok_microservice:latest`

#### Запуск приложения
`cd Microservice`  
`docker-compose up -d`

## HW3
#### Полезные команды
`minikube start --nodes 2` - запуск minikube  
`minikube node add` - добавление ноды
`minikube addons list` - список плагинов
`minikube addons enable ingress` - активировать ingress
`snap install kubectl --classic` - установка kubectl  
https://krew.sigs.k8s.io/docs/user-guide/setup/install/ - установка krew (менеджер плагинов)  
`kubectl krew install node-shell` - установка средства подключения к нодам

`kubectl get pod` - посмотреть поды  
`kubectl get node` - посмотреть ноды  
`kubectl get deployment` - посмотреть deployments  
`kubectl get service` - посмотреть сервисы  
`kubectl get ingress` - посмотреть ingresses  
`kubectl apply -f *.yaml` - применение конфигурации  
`kubectl delete pod <имя>` - удаление пода  
`kubectl delete deployment <имя>` - удаление deployment
`kubectl delete service <имя>` - удаление сервиса  
`kubectl delete ingress <имя>` - удаление ingress  
`kubectl get pod -o wide` - расширенная информация о поде  
`kubectl proxy` - позволяет на localhost обращаться к подам
`kubectl describe pod ok-app` - описание состояния пода
`kubectl node-shell <node>`
`kubectl exec -it <имя_пода> -- /bin/{bash | sh}` - подключиться к поду

#### Запуск кластера
1. `cd Microservice`
2. `minikube start --nodes 2`
3. `minikube addons enable ingress`
4. `kubectl apply -f kubernetes/.`
4. `curl http://arch.homework/health` (прописать у себя в /etc/hosts хост arch.homework)



