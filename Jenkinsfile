pipeline {
  agent any
  environment { REGISTRY = "${env.REGISTRY ?: 'eventflow'}" }
  stages {
    stage('Build and test') { steps { sh 'mvn -B clean test' } }
    stage('Build images') { steps { sh 'docker compose build' } }
    stage('Publish images') {
      when { expression { return env.PUBLISH_IMAGES == 'true' } }
      steps { sh 'docker compose push' }
    }
    stage('Deploy to Kubernetes') {
      when { expression { return env.DEPLOY_TO_K8S == 'true' } }
      steps { sh 'kubectl apply -f k8s/' }
    }
  }
  post { always { junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml' } }
}
