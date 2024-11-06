pipeline {
    agent {
        label 'agent-in-docker'

    }
    // triggers {
    //     // 5분 마다 git 에 새로운 코드가 있으면 빌드를 실행함.
    //     pollSCM '*/5 * * * *'
    // }
    environment {
        
        DB_NAME = credentials('DB_NAME')
        DB_USER = credentials('DB_USER')
        DB_PW = credentials('DB_PW')
        
        TIME_ZONE="Asia/Seoul"

        DOCKER_ID = credentials('DOCKER_ID')
        DOCKER_ACC_KEY = credentials('DOCKER_ACC_KEY')
        
        JENKINS_PROJECT="${env.JOB_NAME}"
        PROJECT_NAME="spring-chat"

    }
    stages {
        stage('Git Clone') {
            steps {
                git branch: 'master',
                url: 'https://github.com/dnova13/spring-chat',
                // 레포지토리가 private 일경우, credentials 에 설정한 git 계정 정보를 설정
                credentialsId: 'git-signin'

            }
            
            post {
                success { 
                    sh '##################### echo "Successfully Cloned Repository"'
                }
                failure {
                    sh '##################### echo "Fail Cloned Repository"'
                }
            }    
        }

        stage('Generate .env file') {
            steps {

                // echo "RDS_TEST_HOST=postgres" >> .env
                sh '''
                echo "DB_HOST=mariadb" >> .env
                echo "DB_PW=${{secrets.DB_PW}}" >> .env
                echo "DB_NAME=${{secrets.DB_NAME}}" >> .env
                echo "DB_USER=${{secrets.DB_USER}}" >> .env
                echo "TIME_ZONE=${{vars.TIME_ZONE}}" >> .env
                '''
            }
        }

        stage('Generate application-prod.properties') {
            steps {

                // echo "RDS_TEST_HOST=postgres" >> .env
                sh '''
                echo "spring.application.name=chat-spring" >> src/main/resources/application-prod.properties

                # maria db 설정
                echo "spring.datasource.url=jdbc:mariadb://mariadb:3306/chat" >> src/main/resources/application-prod.properties
                echo "spring.datasource.username=${{ secrets.DB_USERNAME }}" >> src/main/resources/application-prod.properties
                echo "spring.datasource.password=${{ secrets.DB_PASSWORD }}" >> src/main/resources/application-prod.properties
                echo "spring.datasource.driver-class-name=org.mariadb.jdbc.Driver" >> src/main/resources/application-prod.properties

                # redis 설정
                echo "spring.data.redis.host=redis" >> src/main/resources/application-prod.properties
                echo "spring.data.redis.port=6379" >> src/main/resources/application-prod.properties

                # spring security 설정
                echo "spring.security.user.name=${{ secrets.SECURITY_USERNAME }}" >> src/main/resources/application-prod.properties
                echo "spring.security.user.password=${{ secrets.SECURITY_PASSWORD }}" >> src/main/resources/application-prod.properties
                '''
            }
        }

        stage('spring server test and build') {
            steps {
                sh'docker compose up -d redis mariadb'
                sh'./gradlew build'
            }
            post {
                success {
                    echo '##################### build success'
                }

                failure {
                    echo '##################### build failed'
                }

                always {
                    sh'docker rm -f redis mariadb'
                }
            }
        }

        stage('Build Docker Push') {
            steps {
                sh 'docker login -u $DOCKER_ID -p $DOCKER_ACC_KEY'
                sh '''

                echo "The project name is $PROJECT_NAME"

                # 나머지 compose 이미지 빌드
                docker-compose build redis
                docker-compose build mariadb
                docker-compose build spring-chat

                # docker tag
                docker tag $JENKINS_PROJECT-spring-chat $DOCKER_ID/$PROJECT_NAME-spring-chat:latest
                docker tag $JENKINS_PROJECT-mariadb $DOCKER_ID/$PROJECT_NAME-mariadb:latest
                docker tag $JENKINS_PROJECT-redis $DOCKER_ID/$PROJECT_NAME-redis:latest

                # docker push
                docker push $DOCKER_ID/$PROJECT_NAME-spring-chat
                docker push $DOCKER_ID/$PROJECT_NAME-mariadb
                docker push $DOCKER_ID/$PROJECT_NAME-redis
                '''
            }
            post {
                success {
                    echo '##################### docker push success'
                }
                failure {
                    echo '##################### docker push failed'
                }
            }
        }

        stage('SSH Deploy') {
            steps {        
                // sshagent (credentials: ['ec3-test']) {
                sshagent (credentials: ['local-server']) {
                sh """
                    # ssh -o StrictHostKeyChecking=no ubuntu@3.39.6.193 '
                    ssh -o StrictHostKeyChecking=no test@36.38.247.69 '
                        # 도커 허브 로그인
                        docker login -u $DOCKER_ID -p $DOCKER_ACC_KEY

                        mkdir -p ~/project/$PROJECT_NAME

                        # Docker Compose로 애플리케이션 빌드 및 실행
                        cd ~/project/$PROJECT_NAME

                        sudo docker pull $DOCKER_ID/$PROJECT_NAME-spring-chat:latest
                        sudo docker pull $DOCKER_ID/$PROJECT_NAME-mariadb:latest
                        sudo docker pull $DOCKER_ID/$PROJECT_NAME-redis:latest

                        sudo docker network create chat-network

                        # mariadb 도커 실행
                        sudo docker run -d \
                            --name mariadb \
                            --memory=128m \
                            --restart=unless-stopped \
                            -v $(pwd)/mariadb/maria_data:/var/lib/mysql \
                            -v $(pwd)/mariadb/sqls/:/docker-entrypoint-initdb.d/ \
                            -e MARIADB_ROOT_PASSWORD=$DB_PW \
                            -e MARIADB_DATABASE=$DB_NAME \
                            -e MARIADB_USER=$DB_USER \
                            -e TZ=$TIME_ZONE \
                            -p 5432:5432 \
                            --expose=5432 \
                            --network=chat-network \
                            $DOCKER_ID/$PROJECT_NAME-postgres


                        # redis 도커 실행
                        sudo docker rm -f redis
                        sudo docker run -d \
                        --name redis \
                        --memory=256m \
                        --restart=on-failure \
                        -v ./redis/redis_data/:/data/ \
                        -v ./redis/my-redis.conf:/usr/local/etc/redis/redis.conf \
                        --network=chat-network \
                        $DOCKER_ID/$PROJECT_NAME-redis

                        # spring-chat backend 실행
                        echo "-------------------------- backend build ------------------------------"
                        sudo docker rm -f spring-chat
                        sudo docker run -d \
                          --name spring-chat \
                          --memory=256m \
                          --restart=on-failure \
                          --network=chat-network \
                          $DOCKER_ID/$PROJECT_NAME-chat
                    '
                """
                }
            }
        }
    }
    
    post {
        always {
            script {
                // sh 'docker-compose down -v --rmi all'
                sh 'docker system prune -f'
                // sh 'docker volume prune -f'
            }
        }
    }
}