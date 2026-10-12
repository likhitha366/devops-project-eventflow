pipeline {
  agent any
  parameters {
    string(name: 'REGISTRY', defaultValue: 'eventflow', description: 'Docker registry/repository prefix')
    string(name: 'IMAGE_TAG', defaultValue: '', description: 'Optional image tag; blank uses the Jenkins build number')
    booleanParam(name: 'PUBLISH_IMAGES', defaultValue: false, description: 'Push images to the configured registry')
    booleanParam(name: 'DEPLOY_TO_K8S', defaultValue: false, description: 'Deploy the published images to Kubernetes')
  }
  environment {
    REGISTRY = "${params.REGISTRY?.trim() ?: 'eventflow'}"
    IMAGE_TAG = "${params.IMAGE_TAG?.trim() ?: env.BUILD_NUMBER ?: 'latest'}"
  }
  stages {
    stage('Build and test') {
      steps {
        script {
          if (isUnix()) { sh 'mvn -B clean test' }
          else { bat 'mvn -B clean test' }
        }
      }
    }
    stage('Build images') {
      steps {
        script {
          if (isUnix()) { sh 'docker compose build' }
          else { bat 'docker compose build' }
        }
      }
    }
    stage('Publish images') {
      when { expression { return params.PUBLISH_IMAGES } }
      steps {
        script {
          if (isUnix()) { sh 'docker compose push' }
          else { bat 'docker compose push' }
        }
      }
    }
    stage('Deploy to Kubernetes') {
      when { expression { return params.DEPLOY_TO_K8S } }
      steps {
        script {
          if (!params.PUBLISH_IMAGES) {
            error('Set PUBLISH_IMAGES=true before deploying so the cluster can pull the built images.')
          }
          def manifest = readFile('k8s/eventflow.yaml')
            .replace('__REGISTRY__', env.REGISTRY)
            .replace('__IMAGE_TAG__', env.IMAGE_TAG)
          writeFile(file: 'eventflow-rendered.yaml', text: manifest)
          if (isUnix()) {
            sh 'kubectl apply -f eventflow-rendered.yaml'
          } else {
            bat 'kubectl apply -f eventflow-rendered.yaml'
          }
        }
      }
    }
  }
  post {
    always { junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml' }
  }
}
